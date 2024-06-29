package pt.isec.amov2022202320191482112019129635

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import pt.isec.amov2022202320191482112019129635.GameViewModel
import pt.isec.amov2022202320191482112019129635.GameViewModel.Companion.MOVE_NONE
import pt.isec.amov2022202320191482112019129635.GameViewModel.Companion.SERVER_PORT
import pt.isec.amov2022202320191482112019129635.R
import pt.isec.amov2022202320191482112019129635.databinding.Mode1ActivityBinding



class GameActivity : AppCompatActivity() {
    companion object {
        private const val SERVER_MODE = 0
        private const val CLIENT_MODE = 1

        fun getServerModeIntent(context : Context) : Intent {
            return Intent(context,GameActivity::class.java).apply {
                putExtra("mode", SERVER_MODE)
            }
        }

        fun getClientModeIntent(context : Context) : Intent {
            return Intent(context,GameActivity::class.java).apply {
                putExtra("mode", CLIENT_MODE)
            }
        }
    }
    private val model: GameViewModel by viewModels()
    private lateinit var binding: Mode1ActivityBinding
    private lateinit var detector : GestureDetectorCompat
    private lateinit var tvInfo: TextView
    private var dlg: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mode1_activity)

        //detector = GestureDetectorCompat(this, DetectsGestureListener(this,binding))


        model.state.observe(this) {
            updateUI()
        }

        model.connectionState.observe(this) { state ->
            updateUI()
            if (state != GameViewModel.ConnectionState.SETTING_PARAMETERS &&
                state != GameViewModel.ConnectionState.SERVER_CONNECTING &&
                dlg?.isShowing == true) {
                dlg?.dismiss()
                dlg = null
            }

            if (state == GameViewModel.ConnectionState.CONNECTION_ERROR ||
                state == GameViewModel.ConnectionState.CONNECTION_ENDED ) {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //to do: should ask if the user wants to finish
                model.stopGame()
            }
        })

        if (model.connectionState.value != GameViewModel.ConnectionState.CONNECTION_ESTABLISHED) {
            when (intent.getIntExtra("mode", SERVER_MODE)) {
                SERVER_MODE -> startAsServer()
                CLIENT_MODE -> startAsClient()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        dlg?.run {
            if (isShowing)
                dismiss()
        }
    }

    private fun updateUI() {
        if (model.connectionState.value != GameViewModel.ConnectionState.CONNECTION_ESTABLISHED) {
            /*tvInfo.visibility = View.INVISIBLE
            imgRock.visibility = View.INVISIBLE
            imgPaper.visibility = View.INVISIBLE
            imgScissors.visibility = View.INVISIBLE*/
            return
        }
       /* tvInfo.visibility = if (model.myMove == MOVE_NONE) View.VISIBLE else View.INVISIBLE
        imgRock.visibility = if (model.myMove == MOVE_NONE || model.myMove == MOVE_ROCK) View.VISIBLE else View.INVISIBLE
        imgPaper.visibility = if (model.myMove == MOVE_NONE || model.myMove == MOVE_PAPER) View.VISIBLE else View.INVISIBLE
        imgScissors.visibility = if (model.myMove == MOVE_NONE || model.myMove == MOVE_SCISSORS) View.VISIBLE else View.INVISIBLE*/

        if (model.myMove != MOVE_NONE && model.otherMove != MOVE_NONE) // if (model.state == ROUND_ENDED)...
            model.startGame()
            /*imgRock.postDelayed(5000) {

            }*/
    }

    private fun startAsServer() {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = wifiManager.connectionInfo.ipAddress // Deprecated in API Level 31. Suggestion NetworkCallback
        val strIPAddress = String.format("%d.%d.%d.%d",
            ip and 0xff,
            (ip shr 8) and 0xff,
            (ip shr 16) and 0xff,
            (ip shr 24) and 0xff
        )

        val ll = LinearLayout(this).apply {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            this.setPadding(50, 50, 50, 50)
            layoutParams = params
            setBackgroundColor(Color.rgb(240, 224, 208))
            orientation = LinearLayout.HORIZONTAL
            addView(ProgressBar(context).apply {
                isIndeterminate = true
                val paramsPB = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsPB.gravity = Gravity.CENTER_VERTICAL
                layoutParams = paramsPB
                indeterminateTintList = ColorStateList.valueOf(Color.rgb(96, 96, 32))
            })
            addView(TextView(context).apply {
                val paramsTV = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams = paramsTV
                text = String.format(getString(R.string.msg_ip_address),strIPAddress)
                textSize = 20f
                setTextColor(Color.rgb(96, 96, 32))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            })
        }

        dlg = AlertDialog.Builder(this)
            .setTitle("Server Mode")
            .setView(ll)
            .setOnCancelListener {
                model.stopServer()
                finish()
            }
            .create()
        model.startServer(this)

        dlg?.show()
    }

    private fun startAsClient() {
        val edtBox = EditText(this).apply {
            maxLines = 1
            filters = arrayOf(object : InputFilter {
                override fun filter(
                    source: CharSequence?,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
                ): CharSequence? {
                    source?.run {
                        var ret = ""
                        forEach {
                            if (it.isDigit() || it == '.')
                                ret += it
                        }
                        return ret
                    }
                    return null
                }

            })
        }
        val dlg = AlertDialog.Builder(this)
            .setTitle("Client Mode")
            .setMessage("IP:")
            .setPositiveButton("Connect") { _: DialogInterface, _: Int ->
                val strIP = edtBox.text.toString()
                if (strIP.isEmpty() || !Patterns.IP_ADDRESS.matcher(strIP).matches()) {
                    Toast.makeText(this@GameActivity, "Erro", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    model.startClient(strIP)
                }
            }
            .setNeutralButton("Emulator") { _: DialogInterface, _: Int ->
                model.startClient("10.0.2.2", SERVER_PORT-1)
                // Configure port redirect on the Server Emulator:
                // telnet localhost <5554|5556|5558|...>
                // auth <key>
                // redir add tcp:9998:9999
            }
            .setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                finish()
            }
            .setCancelable(false)
            .setView(edtBox)
            .create()

        dlg.show()
    }

}