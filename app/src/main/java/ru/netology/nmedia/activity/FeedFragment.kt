package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.ActionType
import ru.netology.nmedia.model.ErrorType

import ru.netology.nmedia.viewmodel.PostViewModel
import java.lang.Error

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val bundle = Bundle().apply {
            putString("key", "value")
        }

        bundleOf("key" to "value", Pair("key2", 1))

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            // нужно в зависимости от контента производить соответствующее действие
            override fun onLike(post: Post) {
                if (post.likedByMe) {
                    viewModel.unlikeById(post.id)
                } else {
                    viewModel.likeById(post.id)
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

        })
        adapter.also { it.also { binding.list.adapter = it } }

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner, { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }
        )

        // обновление странички,Для скрытия или показа значка перезагрузки есть метод isRefreshing
        viewModel.data.observe(viewLifecycleOwner, { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
            binding.swiperefresh.isRefreshing = state.refreshing
        })

        // бработка ошибок cо снэк баром
        viewModel.error.observe(viewLifecycleOwner) { error ->
            when (error.action) {

                error.action->  when (error.action) {
                    ActionType.Like -> viewModel.likeById(id.toLong())
                    ActionType.UnlikeById -> viewModel.unlikeById(id.toLong())
                    ActionType.Refresh -> viewModel.refresh()
                    ActionType.GetAll ->  Snackbar.make(
                        binding.root,
                        "${getString(R.string.error_loading)}: ${error.message}",
                        Snackbar.LENGTH_INDEFINITE
                    ).apply {
                        setAction(R.string.retry_loading) {
                            when (error.action) {
                                ActionType.GetAll -> viewModel.loadPosts()
                            }
                        }
                        show()
                    }
                    ActionType.Save->  Snackbar.make(
                        binding.root,
                        "${getString(R.string.error_loading)}: ${error.message}",
                        Snackbar.LENGTH_INDEFINITE
                    ).apply {
                        setAction(R.string.retry_loading) {
                            when (error.action) {

                                ActionType.Save -> viewModel.save()

                            }
                        }
                        show()
                    }
                    ActionType.RemoveById->  Snackbar.make(
                        binding.root,
                        "${getString(R.string.error_loading)}: ${error.message}",
                        Snackbar.LENGTH_INDEFINITE
                    ).apply {
                        setAction(R.string.retry_loading) {
                            when (error.action) {

                                ActionType.RemoveById -> viewModel.removeById(id.toLong())
                            }
                        }
                        show()
                    }
                }
            }
        }

        //обновление постов
        binding.swiperefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}

