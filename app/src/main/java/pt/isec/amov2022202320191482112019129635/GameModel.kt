package pt.isec.amov2022202320191482112019129635

object GameModel {
    var signals: String = "+-x÷"
    var nExpressions: Int = 0;
    var nSegundos: Int = 60
    var RightAnswerTime: Int = 5
    val respCerta: Int = 2
    val segRespCerta: Int = 1
    var level: Int = 1

    var segundoMaior: Int =0
    var nPontos: Int=0
    var sequenciamaiorvalor = arrayOf(0,0,0)
    var sequenciaSegmaiorvalor = arrayOf(0,0,0)

    fun setUpGame(level: Int){

    }
}
