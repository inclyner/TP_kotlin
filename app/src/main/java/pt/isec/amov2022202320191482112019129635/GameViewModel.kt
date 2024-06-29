package pt.isec.amov2022202320191482112019129635

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class GameViewModel: ViewModel() {
    /*private lateinit var db: FirebaseFirestore
    val gameModel = GameModel

    fun startGame() {}*/
    companion object {
        const val SERVER_PORT = 9001

        const val MOVE_FIRST_ROW_HALF_LEFT_RIGHT = 0
        const val MOVE_FIRST_ROW_HALF_RIGHT_LEFT = 1
        const val MOVE_FIRST_ROW_LEFT_RIGHT = 2
        const val MOVE_FIRST_ROW_RIGHT_LEFT = 3

        const val MOVE_SECOND_ROW_HALF_LEFT_RIGHT = 4
        const val MOVE_SECOND_ROW_HALF_RIGHT_LEFT = 5
        const val MOVE_SECOND_ROW_LEFT_RIGHT = 6
        const val MOVE_SECOND_ROW_RIGHT_LEFT = 7

        const val MOVE_THIRD_ROW_HALF_LEFT_RIGHT = 8
        const val MOVE_THIRD_ROW_HALF_RIGHT_LEFT = 9
        const val MOVE_THIRD_ROW_LEFT_RIGHT = 10
        const val MOVE_THIRD_ROW_RIGHT_LEFT = 11

        const val MOVE_FIRST_COLUMN_HALF_TOP_BOTTOM = 12
        const val MOVE_FIRST_COLUMN_HALF_BOTTOM_TOP = 13
        const val MOVE_FIRST_COLUMN_TOP_BOTTOM = 14
        const val MOVE_FIRST_COLUMN_BOTTOM_TOP = 15

        const val MOVE_SECOND_COLUMN_HALF_TOP_BOTTOM = 16
        const val MOVE_SECOND_COLUMN_HALF_BOTTOM_TOP = 17
        const val MOVE_SECOND_COLUMN_TOP_BOTTOM = 18
        const val MOVE_SECOND_COLUMN_BOTTOM_TOP = 19

        const val MOVE_THIRD_COLUMN_HALF_TOP_BOTTOM = 20
        const val MOVE_THIRD_COLUMN_HALF_BOTTOM_TOP = 21
        const val MOVE_THIRD_COLUMN_TOP_BOTTOM = 22
        const val MOVE_THIRD_COLUMN_BOTTOM_TOP = 23

        const val MOVE_NONE=25

        const val ME = 1
        const val OTHER = 2
        const val NONE = 0
    }
    var numUsersConnected=0;

    enum class State {
        STARTING, PLAYING_BOTH, PLAYING_ME, PLAYING_OTHER, ROUND_ENDED, GAME_OVER
    }

    enum class ConnectionState {
        SETTING_PARAMETERS, SERVER_CONNECTING, CLIENT_CONNECTING, CONNECTION_ESTABLISHED,
        CONNECTION_ERROR, CONNECTION_ENDED
    }

    private val _state = MutableLiveData(State.STARTING)
    val state : LiveData<State>
        get() = _state

    private val _connectionState = MutableLiveData(ConnectionState.SETTING_PARAMETERS)
    val connectionState : LiveData<ConnectionState>
        get() = _connectionState

    var myMove = MOVE_NONE
    var otherMove = MOVE_NONE
    var lastVictory = NONE

    private var socket: Socket? = null
    private val socketI: InputStream?
        get() = socket?.getInputStream()
    private val socketO: OutputStream?
        get() = socket?.getOutputStream()

    private var serverSocket: ServerSocket? = null

    private var threadComm: Thread? = null

    fun startGame() {
        myMove = MOVE_NONE
        otherMove = MOVE_NONE
        _state.postValue(State.PLAYING_BOTH)

    }

    fun changeMyMove(move: Int) {
        if (_connectionState.value != ConnectionState.CONNECTION_ESTABLISHED)
            return
        if (myMove != MOVE_NONE || move !in arrayOf(
                MOVE_FIRST_ROW_HALF_LEFT_RIGHT,MOVE_FIRST_ROW_HALF_RIGHT_LEFT
                ,MOVE_FIRST_ROW_LEFT_RIGHT,MOVE_FIRST_ROW_RIGHT_LEFT
                ,MOVE_SECOND_ROW_HALF_LEFT_RIGHT,MOVE_SECOND_ROW_HALF_RIGHT_LEFT
                ,MOVE_SECOND_ROW_LEFT_RIGHT,MOVE_SECOND_ROW_RIGHT_LEFT
                ,MOVE_THIRD_ROW_HALF_LEFT_RIGHT,MOVE_THIRD_ROW_HALF_RIGHT_LEFT
                ,MOVE_THIRD_ROW_LEFT_RIGHT,MOVE_THIRD_ROW_RIGHT_LEFT
                ,MOVE_FIRST_COLUMN_HALF_TOP_BOTTOM,MOVE_FIRST_COLUMN_HALF_BOTTOM_TOP
                ,MOVE_FIRST_COLUMN_TOP_BOTTOM,MOVE_FIRST_COLUMN_BOTTOM_TOP
                ,MOVE_SECOND_COLUMN_HALF_TOP_BOTTOM,MOVE_SECOND_COLUMN_HALF_BOTTOM_TOP
                ,MOVE_SECOND_COLUMN_TOP_BOTTOM,MOVE_SECOND_COLUMN_BOTTOM_TOP
                ,MOVE_THIRD_COLUMN_HALF_TOP_BOTTOM,MOVE_THIRD_COLUMN_HALF_BOTTOM_TOP
                ,MOVE_THIRD_COLUMN_TOP_BOTTOM,MOVE_THIRD_COLUMN_BOTTOM_TOP ))
            return
        myMove = move
        socketO?.run {
            thread {
                try {
                    val printStream = PrintStream(this)
                    printStream.println(move)
                    printStream.flush()
                } catch (_: Exception) {
                    stopGame()
                }
            }
        }
        _state.postValue(State.PLAYING_OTHER)
        checkIfSomeoneWins()
    }

    private fun changeOtherMove(move: Int) {
        if (otherMove != MOVE_NONE || move !in arrayOf(MOVE_FIRST_ROW_HALF_LEFT_RIGHT,MOVE_FIRST_ROW_HALF_RIGHT_LEFT
                ,MOVE_FIRST_ROW_LEFT_RIGHT,MOVE_FIRST_ROW_RIGHT_LEFT
                ,MOVE_SECOND_ROW_HALF_LEFT_RIGHT,MOVE_SECOND_ROW_HALF_RIGHT_LEFT
                ,MOVE_SECOND_ROW_LEFT_RIGHT,MOVE_SECOND_ROW_RIGHT_LEFT
                ,MOVE_THIRD_ROW_HALF_LEFT_RIGHT,MOVE_THIRD_ROW_HALF_RIGHT_LEFT
                ,MOVE_THIRD_ROW_LEFT_RIGHT,MOVE_THIRD_ROW_RIGHT_LEFT
                ,MOVE_FIRST_COLUMN_HALF_TOP_BOTTOM,MOVE_FIRST_COLUMN_HALF_BOTTOM_TOP
                ,MOVE_FIRST_COLUMN_TOP_BOTTOM,MOVE_FIRST_COLUMN_BOTTOM_TOP
                ,MOVE_SECOND_COLUMN_HALF_TOP_BOTTOM,MOVE_SECOND_COLUMN_HALF_BOTTOM_TOP
                ,MOVE_SECOND_COLUMN_TOP_BOTTOM,MOVE_SECOND_COLUMN_BOTTOM_TOP
                ,MOVE_THIRD_COLUMN_HALF_TOP_BOTTOM,MOVE_THIRD_COLUMN_HALF_BOTTOM_TOP
                ,MOVE_THIRD_COLUMN_TOP_BOTTOM,MOVE_THIRD_COLUMN_BOTTOM_TOP))
            return
        otherMove = move
        _state.postValue(State.PLAYING_ME)
        checkIfSomeoneWins()
    }

    private fun checkIfSomeoneWins() {
        /*if (myMove == MOVE_NONE || otherMove == MOVE_NONE)
            return
        val myWin = (myMove == MOVE_ROCK && otherMove == MOVE_SCISSORS) ||
                (myMove == MOVE_PAPER && otherMove == MOVE_ROCK) ||
                (myMove == MOVE_SCISSORS && otherMove == MOVE_PAPER)
        if (myWin) {
            lastVictory = ME
            myWins++
        } else if (myMove != otherMove) {
            lastVictory = OTHER
            otherWins++
        }
        totalGames++
        _state.postValue(State.ROUND_ENDED)*/
    }

    fun stopServer() {
        serverSocket?.close()
        _connectionState.postValue(ConnectionState.CONNECTION_ENDED)
        serverSocket = null
    }

    fun stopGame() {
        try {
            _state.postValue(State.GAME_OVER)
            _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
            socket?.close()
            socket = null
            threadComm?.interrupt()
            threadComm = null
        } catch (_: Exception) { }
    }

    fun startServer(context : Context) {
        if (serverSocket != null || socket != null ||
            _connectionState.value != ConnectionState.SETTING_PARAMETERS)
            return

        _connectionState.postValue(ConnectionState.SERVER_CONNECTING)


        val builder = AlertDialog.Builder(context)

        thread {
            serverSocket = ServerSocket(SERVER_PORT)
            serverSocket?.run {
                try {
                    val socketClient = serverSocket!!.accept()
                    numUsersConnected++
                    /*
                    builder.setMessage("Are you sure you want to start the communication?").setTitle("Start Communication").setPositiveButton("Yes") { _, _ ->
                        startComm(socketClient)
                    }.setNegativeButton("No") { _, _ ->
                    }.create()

                    builder.show()
                     */

                    startComm(socketClient)
                } catch (_: Exception) {
                    _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                } finally {
                    serverSocket?.close()
                    serverSocket = null
                }
            }
        }
    }



    fun startClient(serverIP: String,serverPort: Int = SERVER_PORT) {
        if (socket != null || _connectionState.value != ConnectionState.SETTING_PARAMETERS)
            return

        thread {
            _connectionState.postValue(ConnectionState.CLIENT_CONNECTING)
            try {
                //val newsocket = Socket(serverIP, serverPort)
                val newsocket = Socket()
                newsocket.connect(InetSocketAddress(serverIP,serverPort),5000)
                startComm(newsocket)
            } catch (_: Exception) {
                _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                stopGame()
            }
        }
    }

    private fun startComm(newSocket: Socket) {
        if (threadComm != null)
            return

        socket = newSocket

        threadComm = thread {
            try {
                if (socketI == null)
                    return@thread

                _connectionState.postValue(ConnectionState.CONNECTION_ESTABLISHED)
                val bufI = socketI!!.bufferedReader()

                while (_state.value != State.GAME_OVER) {
                    val message = bufI.readLine()
                    val move = message.toIntOrNull() ?: MOVE_NONE
                    changeOtherMove(move)
                }
            } catch (_: Exception) {
            } finally {
                stopGame()
            }
        }
    }
}