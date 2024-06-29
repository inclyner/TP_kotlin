import android.view.GestureDetector
import android.view.MotionEvent
import pt.isec.amov2022202320191482112019129635.Mode1Activity
import pt.isec.amov2022202320191482112019129635.databinding.Mode1ActivityBinding

class DetectsGestureListener(mode1: Mode1Activity, binding: Mode1ActivityBinding) : GestureDetector.SimpleOnGestureListener() {
    private val SWIPE_VELOCITY_THRESHOLD = 50 // diff between accidental touch and fling
    var sequencia = arrayOf(0,0,0)
    private val mode1 =mode1
    private val binding = binding

    override fun onFling(
        e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
    ): Boolean {
        val gridWidth = binding.grid.measuredWidth
        val gridHeight = binding.grid.measuredHeight;
        val coordinates = IntArray(2)
        binding.grid.getLocationOnScreen(coordinates)
        val initialGridTop=coordinates[1] // Top Y
        val initialGridX = coordinates[0] // Left X
        val intervalo_vertical = gridHeight/5
        val intervalo_horizontal = gridWidth / 5
        val diffX = e2.x.minus(e1.x)
        val diffY = e2.y.minus(e1.y)
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight(e1, e2, intervalo_horizontal, intervalo_vertical, initialGridTop, initialGridX)
                } else {
                    onSwipeLeft(e1, e2, intervalo_horizontal, intervalo_vertical, initialGridTop, initialGridX)
                }
            }
        }
        else { //up or down swipe
            if (Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeTop(e1, e2, intervalo_horizontal, intervalo_vertical, initialGridTop, initialGridX)
                } else {
                    onSwipeBottom(e1, e2, intervalo_horizontal, intervalo_vertical, initialGridTop, initialGridX)
                }
            }
        }
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    private fun onSwipeTop(e1: MotionEvent, e2: MotionEvent, intervalo_horizontal: Int, intervalo_vertical: Int, initialGridTop: Int, initialGridX: Int
    ) {
        if (e1.x > initialGridX && e1.x<intervalo_horizontal && e2.x > initialGridX && e2.x<intervalo_horizontal) {
            if(e1.y>initialGridTop && e1.y<initialGridTop+intervalo_vertical && e2.y>initialGridTop+4*intervalo_vertical && e2.y<initialGridTop+5*intervalo_vertical)
            {
                sequencia= arrayOf(1,4,7)
                mode1.changePoints(sequencia)
            }else if(e1.y>initialGridTop && e1.y<initialGridTop+intervalo_vertical && e2.y>initialGridTop+2*intervalo_vertical && e2.y<initialGridTop+3*intervalo_vertical){
                sequencia= arrayOf(1,4)
                mode1.changePoints(sequencia)
            }
        }
        else if(e1.x>intervalo_horizontal*2 && e1.x<intervalo_horizontal*4 && e2.x>intervalo_horizontal*2 && e2.x<intervalo_horizontal*4){
            if(e1.y>initialGridTop && e1.y<initialGridTop+intervalo_vertical && e2.y>initialGridTop+4*intervalo_vertical && e2.y<initialGridTop+5*intervalo_vertical)
            {
                sequencia= arrayOf(2,5,8)
                mode1.changePoints(sequencia)
            }
            else if(e1.y>initialGridTop && e1.y<initialGridTop+intervalo_vertical && e2.y>initialGridTop+2*intervalo_vertical && e2.y<initialGridTop+3*intervalo_vertical){
                sequencia= arrayOf(2,5)
                mode1.changePoints(sequencia)
            }
        }
        else if(e1.x<intervalo_horizontal*5 && e1.x>intervalo_horizontal*4 && e2.x<intervalo_horizontal*5 && e2.x>intervalo_horizontal*4){
            if(e1.y>initialGridTop && e1.y<initialGridTop+intervalo_vertical && e2.y>initialGridTop+4*intervalo_vertical && e2.y<initialGridTop+5*intervalo_vertical) {
                sequencia= arrayOf(3,6,9)
                mode1.changePoints(sequencia)
            }
            else if(e1.y>initialGridTop && e1.y<initialGridTop+intervalo_vertical && e2.y>initialGridTop+2*intervalo_vertical && e2.y<initialGridTop+3*intervalo_vertical){
                sequencia= arrayOf(3,6,9)
                mode1.changePoints(sequencia)
            }
        }
    }

    private fun onSwipeBottom(e1: MotionEvent, e2: MotionEvent, intervalo_horizontal: Int, intervalo_vertical: Int, initialGridTop: Int, initialGridX: Int
    ) {
        if (e2.x > initialGridX && e2.x<intervalo_horizontal && e1.x > initialGridX && e1.x<intervalo_horizontal) {
            if(e2.y>initialGridTop && e2.y<initialGridTop+intervalo_vertical && e1.y>initialGridTop+4*intervalo_vertical && e1.y<initialGridTop+5*intervalo_vertical)
            {
                sequencia= arrayOf(7,4,1)
                mode1.changePoints(sequencia)
            }
            else if(e2.y>initialGridTop+2*intervalo_vertical && e2.y<initialGridTop+3*intervalo_vertical && e1.y>initialGridTop+4*intervalo_vertical && e1.y<initialGridTop+5*intervalo_vertical){
                sequencia= arrayOf(7,4)
                mode1.changePoints(sequencia)
            }
        }else if(e2.x>intervalo_horizontal*2 && e2.x<intervalo_horizontal*4 && e1.x>intervalo_horizontal*2 && e1.x<intervalo_horizontal*4){
            if(e2.y>initialGridTop && e2.y<initialGridTop+intervalo_vertical && e1.y>initialGridTop+4*intervalo_vertical && e1.y<initialGridTop+5*intervalo_vertical)
            {
                sequencia= arrayOf(8,5,2)
                mode1.changePoints(sequencia)
            }else if(e2.y>initialGridTop+2*intervalo_vertical && e2.y<initialGridTop+3*intervalo_vertical && e1.y>initialGridTop+4*intervalo_vertical && e1.y<initialGridTop+5*intervalo_vertical){
                sequencia= arrayOf(8,5)
                mode1.changePoints(sequencia)
            }
        }else if(e2.x<intervalo_horizontal*5 && e2.x>intervalo_horizontal*4 && e1.x<intervalo_horizontal*5 && e1.x>intervalo_horizontal*4){
            if(e2.y>initialGridTop && e2.y<initialGridTop+intervalo_vertical && e1.y>initialGridTop+4*intervalo_vertical && e1.y<initialGridTop+5*intervalo_vertical)
            {
                sequencia= arrayOf(9,6,3)
                mode1.changePoints(sequencia)
            }else if(e2.y>initialGridTop+2*intervalo_vertical && e2.y<initialGridTop+3*intervalo_vertical && e1.y>initialGridTop+4*intervalo_vertical && e1.y<initialGridTop+5*intervalo_vertical){
                sequencia= arrayOf(9,6)
                mode1.changePoints(sequencia)
            }
        }
    }

    private fun onSwipeLeft(e1: MotionEvent, e2: MotionEvent, intervalo_horizontal: Int, intervalo_vertical: Int, initialGridTop: Int, initialGridX: Int
    ) {
        if(e2.y>initialGridTop && e2.y<initialGridTop+intervalo_vertical && e1.y>initialGridTop && e1.y<initialGridTop+intervalo_vertical)
        {
            if (e2.x > initialGridX && e2.x<intervalo_horizontal && e1.x<intervalo_horizontal*5 && e1.x>intervalo_horizontal*4) {
                sequencia= arrayOf(3,2,1)
                mode1.changePoints(sequencia)
            }
            else if(e2.x>intervalo_horizontal*2 && e2.x<intervalo_horizontal*3 && e1.x<intervalo_horizontal*5 && e1.x>intervalo_horizontal*4){ //Meia Linha
                sequencia= arrayOf(3,2)
                mode1.changePoints(sequencia)
            }
        }
        else if(e2.y>initialGridTop+intervalo_vertical && e2.y<initialGridTop+3*intervalo_vertical && e1.y>initialGridTop+intervalo_vertical && e1.y<initialGridTop+3*intervalo_vertical)
        {
            if (e2.x > initialGridX && e2.x<intervalo_horizontal && e1.x<intervalo_horizontal*5 && e1.x>intervalo_horizontal*4) {
                sequencia= arrayOf(6,5,4)
                mode1.changePoints(sequencia)
            }
            else if(e2.x>intervalo_horizontal*2 && e2.x<intervalo_horizontal*3 && e1.x<intervalo_horizontal*5 && e1.x>intervalo_horizontal*4){ //Meia Linha
                sequencia= arrayOf(6,5)
                mode1.changePoints(sequencia)
            }
        }
        else if(e2.y>initialGridTop+4*intervalo_vertical && e2.y<initialGridTop+5*intervalo_vertical && e1.y>initialGridTop+4*intervalo_vertical && e1.y<initialGridTop+5*intervalo_vertical)
        {
            if (e2.x > initialGridX && e2.x<intervalo_horizontal && e1.x<intervalo_horizontal*5 && e1.x>intervalo_horizontal*4) {
                sequencia= arrayOf(9,8,7)
                mode1.changePoints(sequencia)
            }
            else if(e2.x>intervalo_horizontal*2 && e2.x<intervalo_horizontal*3 && e1.x<intervalo_horizontal*5 && e1.x>intervalo_horizontal*4){ //Meia Linha
                sequencia= arrayOf(9,8)
                mode1.changePoints(sequencia)
            }
        }
    }

    private fun onSwipeRight(e1: MotionEvent, e2: MotionEvent, intervalo_horizontal: Int, intervalo_vertical: Int, initialGridTop: Int, initialGridX: Int
    ) {
        if(e1.y>initialGridTop && e1.y<initialGridTop+intervalo_vertical && e2.y>initialGridTop && e2.y<initialGridTop+intervalo_vertical)
        {
            if (e1.x > initialGridX && e1.x<intervalo_horizontal && e2.x<intervalo_horizontal*5 && e2.x>intervalo_horizontal*4) {
                sequencia = arrayOf(1,2,3)
                mode1.changePoints(sequencia)
            }
            else if(e1.x > initialGridX && e1.x<intervalo_horizontal && e2.x>intervalo_horizontal*2 && e2.x<intervalo_horizontal*4){ //Meia Linha
                sequencia = arrayOf(1,2)
                mode1.changePoints(sequencia)
            }
        }
        else if(e1.y>initialGridTop+intervalo_vertical && e1.y<initialGridTop+3*intervalo_vertical && e2.y>initialGridTop+intervalo_vertical && e2.y<initialGridTop+3*intervalo_vertical)
        {
            if (e1.x > initialGridX && e1.x<intervalo_horizontal && e2.x<intervalo_horizontal*5 && e2.x>intervalo_horizontal*4) {
                sequencia = arrayOf(4,5,6)
                mode1.changePoints(sequencia)
            }
            else if(e1.x > initialGridX && e1.x<intervalo_horizontal && e2.x>intervalo_horizontal*2 && e2.x<intervalo_horizontal*4){ //Meia Linha
                sequencia = arrayOf(4,5)
                mode1.changePoints(sequencia)

            }
        }
        else if(e1.y>initialGridTop+4*intervalo_vertical && e1.y<initialGridTop+5*intervalo_vertical+50 && e2.y>initialGridTop+4*intervalo_vertical && e2.y<initialGridTop+5*intervalo_vertical+50)
        {
            if (e1.x > initialGridX && e1.x<intervalo_horizontal && e2.x<intervalo_horizontal*5 && e2.x>intervalo_horizontal*4) {
                sequencia = arrayOf(7,8,9)
                mode1.changePoints(sequencia)
            }
            else if(e1.x > initialGridX && e1.x<intervalo_horizontal && e2.x>intervalo_horizontal*2 && e2.x<intervalo_horizontal*4){ //Meia Linha
                sequencia = arrayOf(7,8)
                mode1.changePoints(sequencia)
            }
        }
    }
}