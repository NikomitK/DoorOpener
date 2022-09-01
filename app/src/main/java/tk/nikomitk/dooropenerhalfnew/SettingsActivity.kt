package tk.nikomitk.dooropenerhalfnew

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.marcoscg.licenser.Library
import com.marcoscg.licenser.License
import com.marcoscg.licenser.LicenserDialog
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

            /* I know this is a garbage way to do it, but there are literally no useful libraries
            with good documentation on how to do this and I just want to finish this project
             */
            val licencesButton: Preference = findPreference("thirdPartyLicences")!!
            licencesButton.setOnPreferenceClickListener {
                LicenserDialog(this.requireContext())
                    .setTitle("Licenses")
                    .setCustomNoticeTitle("Notices for files:")
                    .setLibrary(
                        Library(
                            "Core Kotlin Extensions",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "Android AppCompat Library",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "Material Components For Android",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "Android ConstraintLayout",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "Android Navigation Fragment Kotlin Extensions",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "Android Navigation UI Kotlin Extensions",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "AndroidX Preference",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "Gson",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "RecyclerViewMarginDecoration",
                            null,
                            License.APACHE2
                        )
                    )
                    .setLibrary(
                        Library(
                            "Licenser",
                            "https://github.com/marcoscgdev/Licenser",
                            License.MIT
                        )
                    )
                    .setPositiveButton(
                        android.R.string.ok
                    ) { dialogInterface, i ->
                        // TODO: 11/02/2018
                    }
                    .show()
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
                    if (response.internalMessage.lowercase()
                            .contains(getString(R.string.invalid_token_internal)) ||
                        response.internalMessage.lowercase()
                            .contains(getString(R.string.success_internal))
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
                    if (response.internalMessage.lowercase()
                            .contains(getString(R.string.invalid_token_internal))
                    ) {
                        logout()
                    }
                }
                true
            }

            val widgetTimePreference: SeekBarPreference = findPreference("widgetTime")!!
            widgetTimePreference.setOnPreferenceChangeListener { _, newValue ->
                OpenActivity.storage.widgetTime = (newValue as Int)
                OpenActivity.storage.save(OpenActivity.storageFile)
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