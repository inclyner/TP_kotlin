package pt.isec.amov2022202320191482112019129635

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.room.Entity
import pt.isec.amov2022202320191482112019129635.databinding.SettingsBinding
import java.io.File

@Entity(

)

class SettingsActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ConfigImageActivity"
        private const val ACTIVITY_REQUEST_CODE_GALLERY = 10
        private const val PERMISSIONS_REQUEST_CODE = 1

        private const val GALLERY = 1
        private const val MODE_KEY = "mode"
        private lateinit var binding: SettingsBinding
        fun getGalleryIntent(context: Context): Intent {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(MODE_KEY, GALLERY)
            return intent
        }

    }

    var mySharedPreferences: MySharedPreferences? = null
    val appDirectory = getFilesDir()
    val imageFile = File.createTempFile("profile_picture", ".jpg",appDirectory)



    private var mode = GALLERY
    private var imagePath: String? = null
    private var readpermissionsGranted = false
        set(value) {
            field = value
            binding.btnAddplayerpic.isEnabled = value
        }
    private var writepermissionsGranted = false // se nao houver permissao para escrever na external storage nao da para guardar o perfil
        set(value) {
            field = value
            binding.Saveplayerprofile.isEnabled = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("myPref",MODE_PRIVATE)
        val editor= sharedPref.edit()
        mySharedPreferences = MySharedPreferences(this)
        binding = SettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mode = intent.getIntExtra(MODE_KEY, GALLERY)
        when (mode) {
            GALLERY ->
                binding.btnAddplayerpic.apply {
                    text = getString(R.string.add_player_picture)
                    setOnClickListener { chooseImage() }
                }
            else -> finish() //podia-se também colocar um toast
        }
        binding.Saveplayerprofile.setOnClickListener {
            saveData()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //load shared preferences

        binding.apply {
            Saveplayerprofile.setOnClickListener {
                val userName = edittextusername.text.toString()
                editor.apply(){putString("userName",userName)}
                editor.commit()
            }
            val userName = sharedPref.getString("userName",null)
            Log.i(TAG,"UserName: $userName")
            //editor.putString(userName,edittextusername.setText())
            edittextusername.setText(userName)
            editor.apply()

        }

        verifyPermissions()
        updatePreview()
    }

    private fun saveData() {
        Log.i(TAG,"SAVEIMAGE")

        /*
        val outputStream=FileOutputStream(imageFile)
        val image: Bitmap? = null// get the image that you want to store
        val file: File? = null// create a file to store the image
        val stream: FileOutputStream = FileOutputStream(file)
        if (image != null) {
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        stream.close()
*/

        //val userN = mySharedPreferences!!.setString("UserName", binding.edittextusername.toString())


        /*
    mySharedPreferences!!.setString("UserName", binding.edittextusername.toString())
    val filename = String.format("%s/%s.%s",
        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "UserPic",
        "png")
    FileOutputStream(filename).use {fos -> binding.playerprofilepic.drawToBitmap().compress(Bitmap.CompressFormat.PNG,100,fos)
    }
    finish()
    */

    }





    fun chooseImage() {

        startActivityForContentResult.launch("image/*")
        //Log.i(TAG, "chooseImage!!!: ")
    }

    var startActivityForContentResult = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        Log.i(TAG, "startActivityForContentResult: ")
        /*uri?.apply {
                val cursor = contentResolver.query(this,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor !=null && cursor.moveToFirst())
                    imagePath = cursor.getString(0)
                updatePreview()
        }*/
        imagePath = uri?.let { createFileFromUri(this, it) }
        updatePreview()
    }


    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        readpermissionsGranted = isGranted
    }
    val requestPermissionLauncherWrite = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        writepermissionsGranted = isGranted
    }

    fun verifyPermissions() {
        Log.i(TAG, "verifyPermissions: ")
        //mode == GALLERY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readpermissionsGranted = ContextCompat.checkSelfPermission(
                this,android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

            if (!readpermissionsGranted)
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            return
        }
        // mode == WRITE_EXTERNAL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            writepermissionsGranted = ContextCompat.checkSelfPermission(
                this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            if (!writepermissionsGranted)
                requestPermissionLauncherWrite.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }
        // GALLERY, versões < API33
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED /*||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED */
        ) {
            readpermissionsGranted = false
            writepermissionsGranted = false
            requestPermissionsLauncher.launch(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else{
            readpermissionsGranted = true
            writepermissionsGranted = true}
    }

    val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantResults ->
        readpermissionsGranted = grantResults.values.any { it }
        writepermissionsGranted= grantResults.values.any{it}
    }

    fun updatePreview() {
        if (imagePath != null)
            setPic(binding.playerprofilepic, imagePath!!)
        mySharedPreferences?.getString("UserName","")
        //else
        // binding.playerprofilepic.background = ResourcesCompat.getDrawable(resources,
        //     R.drawable.no_profile_pic, //android.R.drawable.ic_menu_report_image,
        //   null)
    }
}

