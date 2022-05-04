package tk.nikomitk.dooropenerhalfnew

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
    //TODO turn logout preference red, delete otps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val supportActBar:
                androidx.appcompat.widget.Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(supportActBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val logoutButton: Preference = findPreference("logoutButton")!!
            logoutButton.setOnPreferenceClickListener {
                Toast.makeText(this.context, "dings logout lul", Toast.LENGTH_SHORT).show()
                return@setOnPreferenceClickListener true
            }

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