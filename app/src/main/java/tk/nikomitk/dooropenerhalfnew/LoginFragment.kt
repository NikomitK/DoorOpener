package tk.nikomitk.dooropenerhalfnew

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.NetworkUtil.sendMessage
import tk.nikomitk.dooropenerhalfnew.databinding.FragmentFirstBinding
import tk.nikomitk.dooropenerhalfnew.messagetypes.LoginMessage
import tk.nikomitk.dooropenerhalfnew.messagetypes.toJson
import java.io.File

class LoginFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        val storageFile = File(requireActivity().applicationContext.filesDir, "storageFile")

        if (requireActivity().intent.getBooleanExtra(getString(R.string.logout_extra), false)) {
            storageFile.writeText("")
        }

        var storage = Storage()

        if (!storageFile.createNewFile() && storageFile.readText().contains(":")) {
            storage = storageFile.readText().toStorage()
            startNextActivity(
                ipAddress = storage.ipAddress,
                token = storage.token,
            )
        }


        val textAddress: EditText = binding.editTextAddress
        val textPin: EditText = binding.editTextPin
        val checkBoxNewDevice: CheckBox = binding.checkBoxNewDevice
        val checkBoxRememberPassword: CheckBox = binding.checkBoxRememberPassword

        val buttonLogin: Button = binding.buttonLogin

        buttonLogin.setOnClickListener {
            var success = false
            val ipAddress = textAddress.text.toString()
            launch(Dispatchers.IO) {
                val response = sendMessage(
                    ipAddress = ipAddress,
                    message = LoginMessage(
                        type = "login",
                        pin = Integer.parseInt(textPin.text.toString()),
                        isNewDevice = checkBoxNewDevice.isChecked
                    ).toJson()
                )
                if (response.text.lowercase().contains(getString(R.string.success_internal))) {
                    storage.ipAddress = ipAddress
                    if (checkBoxRememberPassword.isChecked) {
                        storage.pin = Integer.parseInt(textPin.text.toString())
                        storage.token = response.internalMessage
                        storageFile.writeText(storage.toJson())
                    }
                    success = true
                }
                requireActivity().runOnUiThread {
                    Toast.makeText(this@LoginFragment.context, response.text, Toast.LENGTH_SHORT)
                        .show()
                    if (success) {
                        startNextActivity(
                            ipAddress = ipAddress,
                            token = response.internalMessage,
                        )
                    }
                }
            }

        }

    }

    private fun startNextActivity(ipAddress: String?, token: String?) {
        val intent = Intent(this.context, OpenActivity::class.java).apply {
            putExtra(getString(R.string.ipaddress_extra), ipAddress)
            putExtra(getString(R.string.token_extra), token)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}