
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg
            ?.let(binding.edit::setText)

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()

            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}