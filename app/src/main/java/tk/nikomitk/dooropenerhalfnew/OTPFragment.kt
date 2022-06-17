package tk.nikomitk.dooropenerhalfnew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.databinding.FragmentSecondBinding
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.toJson

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class OTPFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.otpTimeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                (p0!!.progress + 1).toString().also { binding.otpTimeTextViewFrag.text = it }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { /* not needed */
            }

            override fun onStopTrackingTouch(p0: SeekBar?) { /* not needed */
            }
        })

        binding.otpUseButtonFrag.setOnClickListener {
            if (
                binding.textOnetimeAddressFrag.text.isNotEmpty() &&
                binding.textOnetimePinFrag.text.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    val response = NetworkUtil.sendMessage(
                        message = Message(
                            type = getString(R.string.otp_open_type),
                            token = binding.textOnetimePinFrag.text.toString(),
                            content = binding.otpTimeTextViewFrag.text.toString()
                        ).toJson(),
                        ipAddress = binding.textOnetimeAddressFrag.text.toString(),
                    )
                    requireActivity().runOnUiThread {
                        response.text.toast(requireContext())
                        if (response.internalMessage == getString(R.string.success_internal))
                            binding.textOnetimePinFrag.setText("")
                    }
                }
            }
        }

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}