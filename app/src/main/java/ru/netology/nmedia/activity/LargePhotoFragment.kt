package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentLargePhotoBinding
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class LargePhotoFragment: Fragment() {

        companion object {
        var Bundle.textArg: String? by StringArg
    }

        private val viewModel: PostViewModel by viewModels(
            ownerProducer = ::requireParentFragment,
        )

        private var fragmentBinding: FragmentLargePhotoBinding? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setHasOptionsMenu(true)
        }


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val binding = FragmentLargePhotoBinding.inflate(
                inflater,
                container,
                false
            )
            fragmentBinding = binding

            arguments?.textArg
                ?.let(binding.like::setText,)

            arguments?.textArg
                ?.let(binding.share::setText,)





            viewModel.postCreated.observe(viewLifecycleOwner) {
                findNavController().navigateUp()
            }

            viewModel.photo.observe(viewLifecycleOwner) {
                if (it.uri == null) {
                    binding.photoContainer.visibility = View.GONE
                    return@observe
                }

                binding.photoContainer.visibility = View.VISIBLE
                binding.photo.setImageURI(it.uri)
            }

            return binding.root
        }

        override fun onDestroyView() {
            fragmentBinding = null
            super.onDestroyView()
        }

    }
