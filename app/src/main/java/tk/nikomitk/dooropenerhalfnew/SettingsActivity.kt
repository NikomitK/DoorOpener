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

class SettingsActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    //TODO turn logout preference red

    companion object {
        lateinit var ipAddress: String
        lateinit var token: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val supportActBar:
                androidx.appcompat.widget.Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(supportActBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        ipAddress = intent.getStringExtra("ipAddress")!!
        token = intent.getStringExtra("token")!!

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
                        type = "keepLogs",
                        token = token,
                        content = newValue.toString(),
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), response.text, Toast.LENGTH_SHORT).show()
                    }
                    if (response.internalMessage.lowercase().contains("invalid token")) {
                        logout()
                    }
                }
                true
            }

            val changePinPreference: EditTextPreference = findPreference("changePin")!!
            changePinPreference.setOnPreferenceChangeListener { _, newValue ->
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        type = "changePin",
                        token = token,
                        content = newValue.toString(),
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), response.text, Toast.LENGTH_SHORT).show()
                    }
                    if (response.internalMessage.lowercase().contains("invalid token")) {
                        logout()
                    }

                }
                true

            }

            val globalLogoutButton: Preference = findPreference("resetLogins")!!
            globalLogoutButton.setOnPreferenceClickListener {
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        type = "globalLogout",
                        token = token,
                        content = "",
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), response.text, Toast.LENGTH_SHORT).show()
                    }
                    if (response.internalMessage.lowercase()
                            .contains("invalid token") || response.internalMessage.lowercase()
                            .contains("success")
                    ) {
                        logout()
                    }
                }
                true
            }

            val resetButton: Preference = findPreference("resetDeviceButton")!!
            resetButton.setOnPreferenceClickListener {
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        type = "reset",
                        token = token,
                        content = "",
                        ipAddress = ipAddress
                    )
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), response.text, Toast.LENGTH_SHORT).show()
                    }
                    if (response.internalMessage.lowercase()
                            .contains("invalid token") || response.internalMessage.lowercase()
                            .contains("success")
                    ) {
                        logout()
                    }
                }
                true
            }

            val logoutButton: Preference = findPreference("logoutButton")!!
            logoutButton.setOnPreferenceClickListener {
                Toast.makeText(this.context, "Logging out :C", Toast.LENGTH_SHORT).show()
                logout()
                return@setOnPreferenceClickListener true
            }

        }

        private fun logout() {
            startActivity(Intent(this.context, LoginActivity::class.java).putExtra("logout", true))
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