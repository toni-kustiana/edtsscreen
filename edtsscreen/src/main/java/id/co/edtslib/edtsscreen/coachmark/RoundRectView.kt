package id.co.edtslib.edtsscreen.coachmark

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import id.co.edtslib.edtsds.R

class RoundRectView: AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var shape: CoachShape? = null
        set(value) {
            field = value
            invalidate()
        }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val outerBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888)
        val outerCanvas = Canvas(outerBitmap)
        val outerPaint = Paint()
        outerPaint.setColor(ContextCompat.getColor(context, R.color.colorOpacity))

        val innerPaint = Paint()
        innerPaint.setColor(Color.TRANSPARENT)
        innerPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))

        val round = (shape?.radius ?: 0).toFloat()

        outerCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), outerPaint)
        if (shape?.type == CoachShapeType.Circle) {
            outerCanvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), (width/2).toFloat(), innerPaint)
        }
        else {
            outerCanvas.drawRoundRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                round,
                round,
                innerPaint
            )
        }
        canvas?.drawBitmap(outerBitmap, 0f, 0f, Paint())
    }

}