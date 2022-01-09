package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEnterBinding
import ru.netology.nmedia.databinding.FragmentLargePhotoBinding
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class FragmentRegistration: Fragment() {


    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var fragmentBinding: FragmentRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationBinding.inflate(
            inflater,
            container,
            false
        )

        binding.register.setOnClickListener{

//            if (binding.pass==binding.pass2) {
            AndroidUtils.hideKeyboard(requireView())
            viewModel.registerUser(binding.name.toString(),binding.login.toString(), binding.pass.toString())
            findNavController().navigateUp()
//            } else {
//
//                Snackbar.make(
//                    binding.root,
//                    "${getString(R.string.password_does_not_match)}",
//                    Snackbar.LENGTH_INDEFINITE)
//                    .show()
//            }
        }

        return binding.root
    }
}