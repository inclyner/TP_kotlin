package pt.isec.amov2022202320191482112019129635

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.amov2022202320191482112019129635.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCredits.setOnClickListener{
            val intent = Intent(this,CreditsActivity::class.java)
            startActivity(intent)
        }
        binding.btnMode1.setOnClickListener{
            val intent = Intent(this,Mode1Activity::class.java)
            startActivity(intent)
        }
        binding.btnServer.setOnClickListener {
            startActivity(GameActivity.getServerModeIntent(this))
        }
        binding.btnClient.setOnClickListener {
            startActivity(GameActivity.getClientModeIntent(this))
        }

        val firestore = Firebase.firestore


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.criar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.mnCriar){
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

}