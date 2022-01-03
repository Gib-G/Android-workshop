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
import com.gib.filrouge.Api
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

// Fragment displaying the user's info
// (avatar + buttons to change the avatar,
// and log out).
class UserInfoFragment : Fragment() {

    private val appContext = Api.appContext

    // Used to manage image files and storage.
    private val mediaStore by lazy { MediaStoreRepository(appContext) }
    // The avatar is designated by a URI.
    private lateinit var photoUri: Uri

    private val viewModel = UserViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Grabbing the views comprising the layout.
        val avatar = view?.findViewById<ImageView>(R.id.image_view)
        val takePictureButton = view?.findViewById<Button>(R.id.fragment_user_info_take_picture_button)
        val browseGalleryButton = view?.findViewById<Button>(R.id.fragment_user_info_browse_gallery_button)
        val logoutButton = view?.findViewById<Button>(R.id.fragment_user_info_logout_button)

        // We define the callback functions for click
        // events on the buttons.
        takePictureButton?.setOnClickListener {
            launchCameraWithPermission()
        }
        browseGalleryButton?.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        logoutButton?.setOnClickListener {
            // Erasing the API token from shared preferences.
            PreferenceManager.getDefaultSharedPreferences(appContext).edit {
                putString("auth_token_key", "")
            }
            // Redirecting to the authentication fragment.
            findNavController().navigate(R.id.action_userInfoFragment_to_authenticationFragment)
        }

        // Sets the callback function to use when the avatar
        // changes.
        lifecycleScope.launch {
            viewModel.userInfo.collect { userInfo ->
                avatar?.load(userInfo?.avatar ?: "https://goo.gl/gEgYUd") {
                    error(R.drawable.ic_launcher_background)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    override fun onResume() {
        viewModel.refresh()
        super.onResume()
    }

    // Used to launch an activity in which the user
    // can set whether the app has the permission to use the
    // camera or not.
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted) launchCamera()
            else showExplanation()
        }

    // Used to launch a camera activity to take a picture.
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { accepted ->
            if (accepted) handleImage(photoUri)
            else Snackbar.make(view!!, "Failure", Snackbar.LENGTH_LONG).show()
        }

    // Used to launch a photo gallery activity for the user
    // to select a picture in their gallery.
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if(it != null) {
            handleImage(it)
        }
    }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = checkSelfPermission(appContext, camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        when {
            isAlreadyAccepted -> launchCamera()
            isExplanationNeeded -> showExplanation()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showExplanation() {
        // Making a pop-up dialog box to explain to the user why the camera access is required.
        AlertDialog.Builder(activity)
            .setMessage("Bro, it's OK! We're not the NSA! Unless... 👉👈")
            .setPositiveButton("I have nothing to hide!") { _, _ -> launchAppSettings() }
            .setNegativeButton("Oh boi, oh fvck!") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // The following intent opens the app's settings.
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", appContext.packageName, null)
        )
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
        // Sending the new avatar URI to the API.
        viewModel.updateAvatar(convert(imageUri))
    }

    private fun launchCamera() {
        // Creates a file to hold the avatar image.
        // This file is stored in the device's memory.
        // The file is referred to by a URI stored in the photoUri attribute.
        lifecycleScope.launchWhenStarted {
            photoUri = mediaStore.createMediaUri(
                filename = "picture-${UUID.randomUUID()}.jpg",
                type = FileType.IMAGE,
                location = SharedPrimary
            ).getOrThrow()
        }
        cameraLauncher.launch(photoUri)
    }

}