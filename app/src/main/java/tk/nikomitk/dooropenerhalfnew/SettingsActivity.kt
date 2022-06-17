package tk.nikomitk.dooropenerhalfnew

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.NetworkUtil.sendMessage
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.toJson

class SettingsActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    //TODO turn logout preference red

    companion object {
        lateinit var ipAddress: String
        lateinit var token: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        ipAddress = intent.getStringExtra(getString(R.string.ipaddress_extra))!!
        token = intent.getStringExtra(getString(R.string.token_extra))!!

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

    }

    class SettingsFragment : PreferenceFragmentCompat(), CoroutineScope by MainScope() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val keepLogsPreference: SwitchPreferenceCompat = findPreference("keepLogs")!!
            keepLogsPreference.setOnPreferenceChangeListener { _, newValue ->
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        message = Message(
                            type = getString(R.string.keep_logs_type),
                            token = token,
                            content = newValue.toString()
                        ).toJson(),
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        response.text.toast(requireContext())
                    }
                    if (response.internalMessage.lowercase()
                            .contains(getString(R.string.invalid_token_internal))
                    ) {
                        logout()
                    }
                }
                true
            }

            val changePinPreference: EditTextPreference = findPreference("changePin")!!
            changePinPreference.setOnPreferenceChangeListener { _, newValue ->
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        message = Message(
                            type = getString(R.string.change_pin_type),
                            token = token,
                            content = newValue.toString()
                        ).toJson(),
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        response.text.toast(requireContext())
                    }
                    if (response.internalMessage.lowercase()
                            .contains(getString(R.string.invalid_token_internal))
                    ) {
                        logout()
                    }
                }
                true
            }

            val globalLogoutButton: Preference = findPreference("resetLogins")!!
            globalLogoutButton.setOnPreferenceClickListener {
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        message = Message(
                            type = getString(R.string.global_logout_type),
                            token = token,
                            content = ""
                        ).toJson(),
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        response.text.toast(requireContext())
                    }
                    if (response.internalMessage.lowercase()
                            .contains(getString(R.string.invalid_token_internal)) || response.internalMessage.lowercase()
                            .contains(getString(R.string.success_internal))
                    ) {
                        logout()
                    }
                }
                true
            }

            val resetButton: Preference = findPreference("resetDevice")!!
            resetButton.setOnPreferenceClickListener {
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        message = Message(
                            type = getString(R.string.reset_type),
                            token = token,
                            content = ""
                        ).toJson(),
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        response.text.toast(requireContext())
                    }
                    if (response.internalMessage.lowercase().contains(getString(R.string.invalid_token_internal)) ||
                        response.internalMessage.lowercase().contains(getString(R.string.success_internal))
                    ) {
                        logout()
                    }
                }
                true
            }

            val tlsSwitch: SwitchPreferenceCompat = findPreference("useTls")!!
            tlsSwitch.setOnPreferenceChangeListener { _, newValue ->
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        message = Message(
                            type = getString(R.string.tls_type),
                            token = token,
                            content = (newValue as Boolean).toString()
                        ).toJson(),
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        response.text.toast(requireContext())
                    }
                    if (response.internalMessage.lowercase().contains(getString(R.string.invalid_token_internal))) {
                        logout()
                    }
                }
                true
            }

            val logoutButton: Preference = findPreference("logoutButton")!!
            logoutButton.setOnPreferenceClickListener {
                getString(R.string.logout_extra).toast(requireContext())
                logout()
                return@setOnPreferenceClickListener true
            }

        }

        private fun logout() {
            startActivity(
                Intent(
                    this.context,
                    LoginActivity::class.java
                ).putExtra(getString(R.string.logout_extra), true)
            )
            OpenActivity.thisActivity.finish()
            requireActivity().finish()
        }
    }

    /*
     * "setDisplayHomeAsUpEnabled didn't work for some reason, this was the easiest solution
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}