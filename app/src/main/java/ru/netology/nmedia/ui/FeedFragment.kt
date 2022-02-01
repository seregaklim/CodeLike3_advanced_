import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FragmentEditPost.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.ActionType
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(
            object : OnInteractionListener {


                override fun onEdit(post: Post) {
                    if (authViewModel.authenticated) {
                    viewModel.edit(post)
                    findNavController().navigate(R.id.action_feedFragment_to_fragmentEditPost, Bundle().apply { textArg = post.content })

                        } else {

                            Snackbar.make(
                                binding.root,
                                "${getString(R.string.registered_users)}",
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .show()
                        }
                    }

                override fun pushPhoto(post: Post) {

                    if (authViewModel.authenticated) {


                            findNavController().navigate(R.id.action_feedFragment_to_fragmentLargePhoto,
                                Bundle().apply {

                                    post.attachment?.let {

                                        putString("url", "${BuildConfig.BASE_URL}/media/${it.url}")
                                        putString("likes", "${post.likes}")
                                        if (post.likedByMe) {
                                            putBoolean("likedByMeTrue", true)
                                        } else {

                                        }
                                    }
                                }
                            )


                        } else {

                            Snackbar.make(
                                binding.root,
                                "${getString(R.string.registered_users)}",
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .show()
                        }
                    }

                override fun onLike(post: Post) {
                    if (authViewModel.authenticated) {
                            if (post.likedByMe) {
                                viewModel.unlikeById(post.id)
                            } else {
                                viewModel.likeById(post.id)
                            }
                        } else {

                            Snackbar.make(
                                binding.root,
                                "${getString(R.string.registered_users)}",
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .show()
                        }
                    }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onShare(post: Post) {
                    authViewModel.data.observe(viewLifecycleOwner) { authState ->
                        if (authState.id != 0L) {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, post.content)
                                type = "text/plain"
                            }

                            val shareIntent =
                                Intent.createChooser(intent, getString(R.string.chooser_share_post))
                            startActivity(shareIntent)
                        } else {

                            Snackbar.make(
                                binding.root,
                                "${getString(R.string.registered_users)}",
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .show()
                        }
                    }
                }}
        )

        binding.list.adapter = adapter

        //обновление странички,Для скрытия или показа значка перезагрузки есть метод isRefreshing
        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        })

        // бработка ошибок cо снэк баром
        viewModel.error.observe(viewLifecycleOwner) { error ->
            Snackbar.make(
                binding.root,
                "${getString(R.string.error_loading)}: ${error.message}",

                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.retry_loading) {
                    when (error.action) {
                        ActionType.GetAll -> viewModel.loadPosts()
                        ActionType.Like -> viewModel.likeById(id.toLong())
                        ActionType.unlikeById -> viewModel.unlikeById(id.toLong())
                        ActionType.Refresh -> viewModel.refreshPosts()
                        ActionType.Save -> viewModel.save()
                        ActionType.RemoveById -> viewModel.removeById(id.toLong())
                        ActionType.CountMessegePost -> viewModel.countMessegePost()
                        ActionType.UnCountMessegePost -> viewModel.unCountNewer()
                    }
                }
                show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner, { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        })

        binding.newer.visibility = View.INVISIBLE
        viewModel.newerCount.observe(viewLifecycleOwner) {

            binding.newer.visibility = if (it == 0) {
                View.INVISIBLE  //невидимая
            } else {
                //  Snackbar.make(binding.root, R.string.add_post, Snackbar.LENGTH_LONG).show()
                View.VISIBLE
            }
            viewModel.countMessegePost()
            binding.newer.text = it.toString()
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }

        binding.fab.setOnClickListener {
            if (authViewModel.authenticated) {
                    findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                } else {

                    Snackbar.make(
                        binding.root,
                        "${getString(R.string.registered_users)}",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .show()
                }
            }

        binding.newer.setOnClickListener {

            viewModel.refreshPosts()
            viewModel.unCountNewer()
            binding.newer.visibility = View.INVISIBLE
        }

        return binding.root
    }
}






