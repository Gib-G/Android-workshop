package com.gib.filrouge.user

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.gib.filrouge.R
import com.gib.filrouge.network.Api
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class UserInfoFragment : Fragment() {

    private val appContext = Api.appContext

    private val userWebService = Api.userWebService

    private var avatar: ImageView? = null
    private var takePictureButton: Button? = null
    private var uploadImageButton: Button? = null
    private var logoutButton: Button? = null

    val mediaStore by lazy { MediaStoreRepository(appContext) }
    private lateinit var photoUri: Uri

    private var viewModel = UserViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Ne rien mettre avant d'avoir inflate notre layout (fragment_task_list.xml).
        val rootView = inflater.inflate(R.layout.fragment_user_info, container, false);

        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        avatar = view?.findViewById(R.id.image_view);
        takePictureButton = view?.findViewById(R.id.take_picture_button);
        uploadImageButton = view?.findViewById(R.id.upload_image_button);
        logoutButton = view?.findViewById(R.id.logout_button)

        takePictureButton?.setOnClickListener {
            launchCameraWithPermission();
        }
        uploadImageButton?.setOnClickListener {
            galleryLauncher.launch("image/*");
        }
        logoutButton?.setOnClickListener {
            // Erasing the API token from shared preferences.
            PreferenceManager.getDefaultSharedPreferences(appContext).edit {
                putString("auth_token_key", "")
            }
            // Redirecting to the authentication activity.
            //activityLauncher.launch(Intent(this, AuthenticationActivity::class.java))
            findNavController().navigate(R.id.action_userInfoFragment_to_authenticationFragment)
        }

        lifecycleScope.launchWhenStarted {
            photoUri = mediaStore.createMediaUri(
                filename = "picture-${UUID.randomUUID()}.jpg",
                type = FileType.IMAGE,
                location = SharedPrimary
            ).getOrThrow();
        }

        lifecycleScope.launch {
            viewModel.userInfo.collect { userInfo ->
                avatar?.load(userInfo?.avatar ?: "https://goo.gl/gEgYUd") {
                    // In case of error.
                    error(R.drawable.ic_launcher_background);
                    // Some transformation.
                    transformations(CircleCropTransformation());
                }
            }
        }
    }

    override fun onResume() {
        viewModel.refresh();
        super.onResume();
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted) launchCamera();
            else showExplanation();
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { accepted ->
            if (accepted) handleImage(photoUri);
            else Snackbar.make(view!!, "Échec!", Snackbar.LENGTH_LONG).show();
        }


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if(it != null) {
            handleImage(it);
        }
    }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = checkSelfPermission(appContext, camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        when {
            isAlreadyAccepted -> launchCamera(); // lancer l'action souhaitée
            isExplanationNeeded -> showExplanation(); // afficher une explication
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA); // lancer la demande de permission
        }
    }

    private fun showExplanation() {
        // ici on construit une pop-up système (Dialog) pour expliquer la nécessité de la demande de permission
        AlertDialog.Builder(appContext)
            .setMessage("🥺 On a besoin de la caméra, vraiment! 👉👈")
            .setPositiveButton("Bon, ok") { _, _ -> launchAppSettings();/* ouvrir les paramètres de l'app */ }
            .setNegativeButton("Nope") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // Cet intent permet d'ouvrir les paramètres de l'app (pour modifier les permissions déjà refusées par ex)
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", appContext.packageName, null)
        )
        // ici pas besoin de vérifier avant car on vise un écran système:
        startActivity(intent)
    }

    // Converts an image pointed to by a URI into a MultipartBody.Part.
    private fun convert(uri: Uri): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = appContext.contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
        )
    }

    private fun handleImage(imageUri: Uri) {
        // Changing the state flow of UserInfoViewModel,
        // so that collect can be called and the avatar
        // displayed is refreshed.
        viewModel.refresh()
        // Sending the new avatar to the API.
        viewModel.updateAvatar(convert(imageUri))
    }

    private fun launchCamera() {
        cameraLauncher.launch(photoUri);
    }

}