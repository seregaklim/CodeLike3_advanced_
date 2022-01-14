package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel

class FragmentRegistration: Fragment() {


    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var fragmentBinding: FragmentRegistration? = null
//    fun enterUser(authState: AuthState) {
//        authState.token?.let { authState.copy(it.toLong()) }?.let {
//            authViewModel.getUserId(authState.copy(authState.id),
//                it
//            )
//        }
//    }

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
        val authState = AuthState()

        binding.register.setOnClickListener {

//            if (binding.pass==binding.pass2) {
            AndroidUtils.hideKeyboard(requireView())
            authViewModel.registerUser(
                binding.name.toString(),
                binding.login.toString(),
                binding.pass.toString()
            )

            authViewModel.getUserId(authState)

//            if (authState.id == 0L || authState.token == null)
//
//
//                Snackbar.make(
//                    binding.root,
//                    "${getString(R.string.password_does_not_match)}",
//                    Snackbar.LENGTH_INDEFINITE
//                )
//                    .show()
//            else {
                findNavController().navigateUp()
//            }
        }
        return binding.root
    }
}