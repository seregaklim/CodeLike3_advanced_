package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.databinding.FragmentEnterBinding
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.ActionType
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.token

class FragmentEnter : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    private var fragmentBinding: FragmentEnter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEnterBinding.inflate(
            inflater,
            container,
            false
        )

        authViewModel.data.observe(viewLifecycleOwner) { authState ->
            if (authState.id != 0L) {

                findNavController().navigateUp()
            }
        }
        binding.enter.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())

            authViewModel.loginUser(binding.login.text.toString(), binding.pass.text.toString())

        }

        authViewModel.error.observe(viewLifecycleOwner) { error ->
            Snackbar.make(
                binding.root,
                "${getString(R.string.error_loading)}: ${error.message}",

                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.retry_loading) {
                    when (error.action) {
                        ActionType.LoginUser ->authViewModel.loginUser(binding.login.toString(), binding.pass.toString())

                    }
                }
                show()
            }
        }

        return binding.root
    }

}






















//
//
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import com.bumptech.glide.Glide
//import ru.netology.nmedia.BuildConfig
//import ru.netology.nmedia.R
//import ru.netology.nmedia.databinding.FragmentLargePhotoBinding
//import ru.netology.nmedia.dto.Attachment
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.enumeration.AttachmentType
//import ru.netology.nmedia.util.StringArg
//import ru.netology.nmedia.viewmodel.PostViewModel
//
//class LargePhotoFragment: Fragment() {
//
//    companion object {
//        var Bundle.textArg: String? by StringArg
//    }
//
//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment
//    )
//    private var fragmentBinding: FragmentLargePhotoBinding? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentLargePhotoBinding.inflate(
//            inflater,
//            container,
//            false
//        )
//
//        val post = Post(
//            id = 0,
//            content = "",
//            author = "",
//            authorAvatar = "",
//            authorId = 0,
//            likedByMe = false,
//            likes = 0,
//            published = "",
//            newer =0,
//            attachment = Attachment (
//                url = "http://10.0.2.2:9999/media/d7dff806-4456-4e35-a6a1-9f2278c5d639.png",
//                type = AttachmentType.IMAGE
//            )
//        )
//
//        arguments?.getString("likes")
//            ?.let(binding.like::setText)
//
//
//        binding.apply {
//            post.likedByMe = arguments?.getBoolean("likedByMeTrue") == true
//            like.text =arguments?.getString("likes")
//            like.isChecked =post.likedByMe
//
//            post.attachment?.let {
//
//                Log.d("MyLog", "${BuildConfig.BASE_URL}/media/${it.url}")
//
//                Glide.with(photo)
//                    .load(  arguments?.getString("url"))
//                    .timeout(10_000)
//                    .into(photo)
//
//                like.setOnClickListener {
//
//
//                    if (post.likedByMe) {
//                        viewModel.unlikeById(it.id.toLong())
//                    } else {
//                        viewModel.likeById(it.id.toLong())
//                    }
//
//                }
//
//                binding.share.setOnClickListener {
//
//                    val intent = Intent().apply {
//                        action = Intent.ACTION_SEND
//                        putExtra(Intent.EXTRA_TEXT, post.content)
//                        type = "text/plain"
//                    }
//
//                    val shareIntent =
//                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                    startActivity(shareIntent)
//                }
//
//            }
