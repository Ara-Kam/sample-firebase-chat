package com.example.firebasechat.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.example.firebasechat.R
import com.example.firebasechat.data.entity.User
import com.example.firebasechat.data.util.ResultDataWrapper
import com.example.firebasechat.databinding.FragmentSettingsBinding
import com.example.firebasechat.util.SELECT_PICTURE
import com.example.firebasechat.util.closeKeyboard
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var settingsBinding: FragmentSettingsBinding
    private var dialog: MaterialDialog? = null
    private lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.settingsFragment = this
            it.viewModel = settingsViewModel
        }

        return settingsBinding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsViewModel.getUserLiveData().observe(viewLifecycleOwner, Observer {
            currentUser = it
        })

        settingsViewModel.updateResult.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResultDataWrapper.Status.SUCCESS -> {
                    closeKeyboard(requireActivity())
                    normalState()
                    Toast.makeText(requireContext(), "Data saved!", LENGTH_SHORT).show()

                }

                ResultDataWrapper.Status.ERROR, ResultDataWrapper.Status.CANCELED -> {
                    errorState(it.message)
                }

                ResultDataWrapper.Status.LOADING -> {
                    loadingState()
                }
            }
        })

        settingsBinding.errorLayout.retry.setOnClickListener { normalState() }
    }

    @ExperimentalCoroutinesApi
    fun showEditImageDialog() {
        if (dialog == null)
            dialog = MaterialDialog(requireContext())

        dialog = MaterialDialog(requireContext()).show {
            message(R.string.edit_image)
            customView(
                R.layout.dialog_edit_avatar,
                scrollable = true,
                horizontalPadding = true
            )
            positiveButton(text = getString(R.string.done), click = { dialog ->
                val urlInput: TextInputEditText = dialog.getCustomView()
                    .findViewById(R.id.image_input_url)

                dialog.getActionButton(WhichButton.POSITIVE).setOnClickListener {
                    currentUser.avatar = urlInput.text.toString()
                    settingsViewModel.updateUserField(currentUser, User.Field.AVATAR)
                }

                currentUser.avatar = urlInput.text.toString()
                settingsViewModel.updateUserField(currentUser, User.Field.AVATAR)
            })
            negativeButton(text = getString(R.string.cancel))
            onDismiss { dialog = null }
            lifecycleOwner(this@SettingsFragment)
        }

        val urlInput: TextInputEditText =
            dialog!!.getCustomView().findViewById(R.id.image_input_url)
        urlInput.also {
            it.addTextChangedListener {
                dialog!!.getActionButton(WhichButton.POSITIVE).isEnabled =
                    urlInput.text.toString().isNotEmpty()
            }
            it.setText(currentUser.avatar)
        }

        val fromGalleryButton: Button =
            dialog!!.getCustomView().findViewById(R.id.chose_from_gallery)
        fromGalleryButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_OPEN_DOCUMENT  //ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Select Picture"
                ), SELECT_PICTURE
            )
        }
    }

    @ExperimentalCoroutinesApi
    fun showEditPassDialog() {
        if (dialog == null)
            dialog = MaterialDialog(requireContext())

        dialog = MaterialDialog(requireContext()).show {
            message(R.string.edit_pass)
            customView(
                R.layout.dialog_edit_password,
                scrollable = true,
                horizontalPadding = true
            )
            negativeButton(text = getString(R.string.cancel))
            onDismiss { dialog = null }
            lifecycleOwner(this@SettingsFragment)
        }
        val newPassInput: TextInputEditText = dialog!!.getCustomView()
            .findViewById(R.id.new_pass_input)
        val confirmPassInput: TextInputEditText = dialog!!.getCustomView()
            .findViewById(R.id.confirm_pass_input)
        val savePassButton: Button = dialog!!.getCustomView()
            .findViewById(R.id.save_pass)

        val isSavePassButtonEnabled =
            newPassInput.text.toString().length >= 6 && newPassInput.text.toString() == confirmPassInput.text.toString()
        newPassInput.addTextChangedListener {
            savePassButton.isEnabled = newPassInput.text.toString().length >= 6 && newPassInput.text.toString() == confirmPassInput.text.toString()
        }
        confirmPassInput.addTextChangedListener {
            savePassButton.isEnabled = newPassInput.text.toString().length >= 6 && newPassInput.text.toString() == confirmPassInput.text.toString()
        }
        savePassButton.also {
            it.isEnabled = isSavePassButtonEnabled
            it.setOnClickListener {
                currentUser.password = newPassInput.text.toString()
                settingsViewModel.updateUserField(currentUser, User.Field.PASSWORD)
                dialog!!.dismiss()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun showEditNameDialog() {
        if (dialog == null)
            dialog = MaterialDialog(requireContext())

        dialog = MaterialDialog(requireContext()).show {
            message(R.string.edit_name)
            customView(
                R.layout.dialog_edit_name,
                scrollable = true,
                horizontalPadding = true
            )
            negativeButton(text = getString(R.string.cancel))
            onDismiss { dialog = null }
            lifecycleOwner(this@SettingsFragment)
        }
        val newNameInput: TextInputEditText = dialog!!.getCustomView()
            .findViewById(R.id.new_name_input)
        val saveNameButton: Button = dialog!!.getCustomView()
            .findViewById(R.id.save_name)

        newNameInput.addTextChangedListener {
            saveNameButton.isEnabled = newNameInput.text.toString().isNotEmpty()
        }
        saveNameButton.also {
            it.isEnabled = newNameInput.text.toString().isNotEmpty()
            it.setOnClickListener {
                currentUser.name = newNameInput.text.toString()
                settingsViewModel.updateUserField(currentUser, User.Field.NAME)
                dialog!!.dismiss()
            }
        }

    }

    @ExperimentalCoroutinesApi
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                if (intent == null || intent.data == null) return
                val selectedImageUri: Uri = intent.data!!
                requireContext().contentResolver.takePersistableUriPermission(
                    selectedImageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                currentUser.avatar = selectedImageUri.toString()
                settingsViewModel.updateUserField(currentUser, User.Field.AVATAR_GALLERY)
            }
        }
    }

    private fun normalState() {
        settingsBinding.apply {
            errorLayout.root.visibility = View.GONE
            userDataContainer.visibility = View.VISIBLE
        }
    }

    private fun loadingState() {
        settingsBinding.apply {
            userDataContainer.visibility = View.GONE
        }
    }

    private fun errorState(errorMessage: String?) {
        settingsBinding.apply {
            userDataContainer.visibility = View.GONE
            errorLayout.errorText.text = errorMessage
        }
    }
}