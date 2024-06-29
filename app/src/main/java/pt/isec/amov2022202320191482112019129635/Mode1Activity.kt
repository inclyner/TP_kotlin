package pt.isec.amov2022202320191482112019129635

import DetectsGestureListener
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.amov2022202320191482112019129635.databinding.Mode1ActivityBinding
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


class Mode1Activity : AppCompatActivity(){

    companion object{
        var signals: String = "+-x÷"
    }
    var maxSignals: Int = 1
    var maxRange: Int =10
    var nExpressions: Int = 0
    var nSegundos: Long = 60
    var RightAnswerTime: Int = 5
    val respCerta: Int = 2
    val segRespCerta: Int = 1
    var level: Int = 1

    lateinit var secondMaxValue: Board
    lateinit var maxValue : Board
    var nPontos: Int=0

    private lateinit var detector: GestureDetectorCompat
    private lateinit var timerNextLevel : CountDownTimer
    private lateinit var timerGame : CountDownTimer
    private lateinit var viewModel: GameViewModel
    data class Board(val result: Int, val sequencia: Array<Int>)

    val firestore = Firebase.firestore

    private lateinit var binding: Mode1ActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Mode1ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeBoard(level)
        detector = GestureDetectorCompat(this, DetectsGestureListener(this,binding))
        val intent = Intent(this,MainActivity::class.java)
        initGame()
        timerGame = object:CountDownTimer((nSegundos*1000).toLong(), 1000){
            override fun onTick(remaining: Long) {
                binding.timeSeconds.text = remaining.milliseconds.inWholeSeconds.toString()
            }
            override fun onFinish() {
                val dlg = AlertDialog.Builder(this@Mode1Activity)
                    .setTitle("You Lost the Game!")
                    .setMessage("You Gathered $nPontos points!")
                    .setPositiveButton("Try Again"){_:DialogInterface, _:Int->
                        nPontos=0
                        level=1
                        initGame()
                        changeBoard(level)
                    }
                    .setNegativeButton("Leave"){_:DialogInterface, _:Int->
                        startActivity(intent)
                    }.create()

                dlg.show()
            }
        }.start()
        timerNextLevel = object:CountDownTimer(5000, 1000){
            override fun onTick(remaining: Long) {}
            override fun onFinish() {
                changeBoard(level)
                timerGame.onTick(nSegundos)
                timerGame.start()
            }
        }


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(detector.onTouchEvent(event))
            return true
        return super.onTouchEvent(event)
    }

    fun operatorFromChar(charOperator: CharSequence, a:Int, b:Int): Int {
        when (charOperator.toString()) {
            "+" -> return a + b
            "-" -> return a - b
            "÷" -> return a / b
            "x" -> return a * b
            else -> throw Exception("That's not a supported operator")
        }
    }

    private fun initGame(){
        if (level == 1) {
            nExpressions=5
            maxSignals = 1
            RightAnswerTime=5
            nSegundos=60
            maxRange=10
        }
        else if (level == 2) {
            nExpressions=10
            maxSignals =2
            RightAnswerTime=10
            nSegundos = 90
            maxRange = 100
        }
        else if (level == 3) {
            nExpressions=15
            maxSignals =3
            RightAnswerTime=15
            nSegundos = 120
            maxRange = 100
        }
        else if (level == 4) {
            nExpressions=20
            maxSignals =3
            RightAnswerTime=20
            nSegundos = 150
            maxRange = 1000
        }
        else if (level == 5) {
            nExpressions=20
            maxSignals =4
            RightAnswerTime=30
            nSegundos = 180
            maxRange = 1000
        }
        binding.levelNumber.text = level.toString()
    }

    fun changePoints(sequencia: Array<Int>) {
        val s = binding.timeSeconds.text
        if (maxValue.sequencia.contentEquals(sequencia)) {
            nPontos += respCerta
            nSegundos = (s as String).toLong() + (RightAnswerTime).toLong()
            timerGame.onTick(nSegundos*1000)
            nExpressions -= 1
        } else if (secondMaxValue.sequencia.contentEquals(sequencia)) {
            nPontos += segRespCerta
        }
        endGame()
        changeBoard(level)
    }

    fun changeBoard(level: Int) {

        binding.blockNumber1.text = Random.nextInt(1, maxRange).toString()
        binding.blockNumber2.text = Random.nextInt(1, maxRange).toString()
        binding.blockNumber3.text = Random.nextInt(1, maxRange).toString()
        binding.blockNumber4.text = Random.nextInt(1, maxRange).toString()
        binding.blockNumber5.text = Random.nextInt(1, maxRange).toString()
        binding.blockNumber6.text = Random.nextInt(1, maxRange).toString()
        binding.blockNumber7.text = Random.nextInt(1, maxRange).toString()
        binding.blockNumber8.text = Random.nextInt(1, maxRange).toString()
        binding.blockNumber9.text = Random.nextInt(1, maxRange).toString()
        binding.blockOperation1.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation2.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation3.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation4.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation5.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation6.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation7.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation8.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation9.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation10.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation11.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()
        binding.blockOperation12.text = signals.toCharArray().get(Random.nextInt(0,maxSignals)).toString()

        //Calculo 1 linha
        val pesqPdireita = Board(operatorFromChar(binding.blockOperation1.text.toString(),Integer.parseInt(binding.blockNumber1.text.toString()),Integer.parseInt(binding.blockNumber2.text.toString())), arrayOf(1,2))
        val pdireitaPesquerda= Board(operatorFromChar(binding.blockOperation2.text.toString(), Integer.parseInt(binding.blockNumber3.text.toString()), Integer.parseInt(binding.blockNumber2.text.toString())), arrayOf(3,2))
        val plinhaesqPdireita = Board(operatorFromChar(binding.blockOperation2.text.toString(),pesqPdireita.result,Integer.parseInt(binding.blockNumber3.text.toString())), arrayOf(1,2,3))
        val plinhaDirpEsquerda = Board(operatorFromChar(binding.blockOperation1.text.toString(),pdireitaPesquerda.result, Integer.parseInt(binding.blockNumber1.text.toString())), arrayOf(3,2,1))

        //Calculo 2 linha
        val sesqPdireita = Board(operatorFromChar(binding.blockOperation6.text.toString(),Integer.parseInt(binding.blockNumber4.text.toString()),Integer.parseInt(binding.blockNumber5.text.toString())), arrayOf(4,5))
        val sdireitaPesquerda=Board(operatorFromChar(binding.blockOperation7.text.toString(), Integer.parseInt(binding.blockNumber6.text.toString()), Integer.parseInt(binding.blockNumber5.text.toString())), arrayOf(6,5))
        val slinhaesqPdireita = Board(operatorFromChar(binding.blockOperation7.text.toString(),sesqPdireita.result,Integer.parseInt(binding.blockNumber6.text.toString())), arrayOf(4,5,6))
        val slinhaDirpEsquerda = Board(operatorFromChar(binding.blockOperation6.text.toString(),sdireitaPesquerda.result, Integer.parseInt(binding.blockNumber4.text.toString())), arrayOf(6,5,4))

        //Calculo 2 linha
        val tesqPdireita = Board(operatorFromChar(binding.blockOperation11.text.toString(),Integer.parseInt(binding.blockNumber7.text.toString()),Integer.parseInt(binding.blockNumber8.text.toString())), arrayOf(7,8))
        val tdireitaPesquerda=Board(operatorFromChar(binding.blockOperation12.text.toString(), Integer.parseInt(binding.blockNumber9.text.toString()), Integer.parseInt(binding.blockNumber8.text.toString())), arrayOf(9,8))
        val tlinhaesqPdireita = Board(operatorFromChar(binding.blockOperation12.text.toString(),tesqPdireita.result,Integer.parseInt(binding.blockNumber9.text.toString())), arrayOf(7,8,9))
        val tlinhaDirpEsquerda = Board(operatorFromChar(binding.blockOperation11.text.toString(),tdireitaPesquerda.result, Integer.parseInt(binding.blockNumber7.text.toString())), arrayOf(9,8,7))

        //Calculo 1 coluna
        val pTopBottom = Board(operatorFromChar(binding.blockOperation3.text.toString(),Integer.parseInt(binding.blockNumber1.text.toString()), Integer.parseInt(binding.blockNumber4.text.toString())), arrayOf(1,4))
        val pBottomTop = Board(operatorFromChar(binding.blockOperation8.text.toString(),Integer.parseInt(binding.blockNumber7.text.toString()), Integer.parseInt(binding.blockNumber4.text.toString())), arrayOf(7,4))
        val pColTopBottom = Board(operatorFromChar(binding.blockOperation8.text.toString(),pTopBottom.result, Integer.parseInt(binding.blockNumber7.text.toString())), arrayOf(1,4,7))
        val pColBottomTop = Board(operatorFromChar(binding.blockOperation3.text.toString(),pBottomTop.result, Integer.parseInt(binding.blockNumber1.text.toString())), arrayOf(7,4,1))

        //Calculo 2 coluna
        val sTopBottom = Board(operatorFromChar(binding.blockOperation4.text.toString(),Integer.parseInt(binding.blockNumber2.text.toString()), Integer.parseInt(binding.blockNumber5.text.toString())), arrayOf(2,5))
        val sBottomTop = Board(operatorFromChar(binding.blockOperation9.text.toString(),Integer.parseInt(binding.blockNumber8.text.toString()), Integer.parseInt(binding.blockNumber5.text.toString())), arrayOf(8,5))
        val sColTopBottom = Board(operatorFromChar(binding.blockOperation9.text.toString(),sTopBottom.result, Integer.parseInt(binding.blockNumber8.text.toString())), arrayOf(2,5,8))
        val sColBottomTop = Board(operatorFromChar(binding.blockOperation4.text.toString(),sBottomTop.result, Integer.parseInt(binding.blockNumber2.text.toString())), arrayOf(8,5,2))

        //Calculo 3 coluna
        val tTopBottom = Board(operatorFromChar(binding.blockOperation5.text.toString(),Integer.parseInt(binding.blockNumber3.text.toString()), Integer.parseInt(binding.blockNumber6.text.toString())), arrayOf(3,6))
        val tBottomTop = Board(operatorFromChar(binding.blockOperation10.text.toString(),Integer.parseInt(binding.blockNumber9.text.toString()), Integer.parseInt(binding.blockNumber6.text.toString())), arrayOf(9,6))
        val tColTopBottom = Board(operatorFromChar(binding.blockOperation10.text.toString(),tTopBottom.result, Integer.parseInt(binding.blockNumber9.text.toString())), arrayOf(3,6,9))
        val tColBottomTop = Board(operatorFromChar(binding.blockOperation5.text.toString(),tBottomTop.result, Integer.parseInt(binding.blockNumber3.text.toString())), arrayOf(9,6,3))
        val values = arrayListOf<Board>(pesqPdireita, pdireitaPesquerda, plinhaesqPdireita, plinhaDirpEsquerda, sesqPdireita, sdireitaPesquerda, slinhaesqPdireita, slinhaDirpEsquerda,tesqPdireita, tdireitaPesquerda, tlinhaesqPdireita, tlinhaDirpEsquerda,pTopBottom, pBottomTop,pColTopBottom, pColBottomTop,sBottomTop, sBottomTop,sColTopBottom, sColBottomTop,tBottomTop, tBottomTop,tColTopBottom, tColBottomTop)

        var first = Int.MIN_VALUE
        var second = Int.MIN_VALUE
        var indexMax = 0
        var indexSecond = 0

        for(v in values){
            if(v.result>first){
                second=first
                first=v.result
                indexMax = values.indexOf(v)
            }else if(v.result!=first && v.result>second) {
                second = v.result
                indexSecond = values.indexOf(v)
            }
        }
        maxValue = values.get(indexMax)
        secondMaxValue = values.get(indexSecond)
    }

    private fun endGame(){
        if(nExpressions==0){
            level+=1
            initGame()
            timerGame.cancel()
            timerNextLevel.start()
            val dlgWin= AlertDialog.Builder(this@Mode1Activity)
                .setTitle("Congratulations!")
                .setMessage("You Gathered $nPontos points!\nStarting level $level")
            dlgWin.show()
            changeBoard(level)
        }

        if(SetHighScore() && timerGame.equals(0)){
            val dlgWin= AlertDialog.Builder(this@Mode1Activity)
                .setTitle("New High Score!")
                .setMessage("You Gathered $nPontos points!\nAnd lost in level $level\nUploading highscore to cloud!")
            dlgWin.show()
            var mySharedPreferences = MySharedPreferences(this)
            mySharedPreferences?.getString("UserName","")
                ?.let { firestore.collection("highScores").document(it) }
        }
    }

    private fun SetHighScore(): Boolean {
        //if(nPontos>getnposntos from server)
        //devolve true se for highscore
        return false

    }
}