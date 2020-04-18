package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import ru.skillbranch.devintensive.extensions.pxToDp
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private lateinit var resultBm: Bitmap
    private lateinit var maskBm: Bitmap
    private lateinit var srcBm: Bitmap


    var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)
    private var borderColor: Int = DEFAULT_BORDER_COLOR
    private var initials: String = "??"

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderWidth = ta.getDimension(
                R.styleable.CircleImageView_cv_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )

            borderColor =
                ta.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            ta.recycle()
        }

        scaleType = ScaleType.CENTER_CROP
    }


    @Dimension
    fun getBorderWidth(): Int = context.pxToDp(borderWidth)

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = context.dpToPx(dp)
        invalidate()
    }

    fun getBorderColor(): Int  = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = resources.getColor(colorId, context.theme)
        invalidate()
    }

    fun setInitials(initials: String?) {
        if (initials == null)
            setImageResource(R.drawable.avatar_default)
        else
            setImageBitmap(getTextAvatar(initials))
    }

    private fun getTextAvatar(initials: String): Bitmap? {
        val color = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, color, true)

        val bitmap: Bitmap = Bitmap.createBitmap(
            layoutParams.width,
            layoutParams.height,
            Bitmap.Config.ARGB_8888
        )

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = layoutParams.height / 2f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        val canvas = Canvas(bitmap)
        canvas.drawColor(color.data)

        val textBounds = Rect()
        paint.getTextBounds(initials, 0, initials.length, textBounds)

        canvas.drawText(initials, layoutParams.width / 2f, layoutParams.height / 2f + textBounds.height() / 2f, paint)

        return bitmap
    }


    override fun onDraw(canvas: Canvas) {
        var bitmap = getBitmapFromDrawable() ?: return
        bitmap = getScaledBitmap(bitmap, width)
        bitmap = cropCenterBitmap(bitmap)
        bitmap = makeCircleBitmap(bitmap)

        if (borderWidth > 0)
            bitmap = createRoundBorderBitmap(bitmap, borderWidth.toInt(), borderColor)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    private fun getBitmapFromDrawable(): Bitmap? {
        return if (drawable != null) {
            if (drawable is BitmapDrawable)
                (drawable as BitmapDrawable).bitmap
            else drawable.toBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        } else null
    }

    private fun getScaledBitmap(bitmap: Bitmap, size: Int): Bitmap {
        return if (bitmap.width != size || bitmap.height != size) {
            val minSide = min(bitmap.width, bitmap.height).toFloat()
            val factor = minSide / size
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width / factor).toInt(),
                (bitmap.height / factor).toInt(),
                false
            )
        } else bitmap
    }

    private fun cropCenterBitmap(bitmap: Bitmap): Bitmap {
        val newWidth = (bitmap.width) / 2
        val newHeight = (bitmap.height) / 2

        return if (bitmap.height >= bitmap.width) {
            Bitmap.createBitmap(bitmap, 0, newHeight - newWidth, bitmap.width, bitmap.width)
        } else {
            Bitmap.createBitmap(bitmap, newWidth - newHeight, 0, bitmap.height, bitmap.height)
        }
    }

    private fun makeCircleBitmap(bitmap: Bitmap): Bitmap {
        val smallest = min(bitmap.width, bitmap.height)
        val outputBmp = Bitmap.createBitmap(smallest, smallest, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        canvas.drawCircle(smallest / 2F, smallest / 2F, smallest.toFloat() / 2F, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0F, 0F, paint)

        return outputBmp
    }

    private fun createRoundBorderBitmap(
        bitmap: Bitmap,
        borderWidth: Int,
        borderColor: Int
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.color = borderColor
        paint.strokeWidth = borderWidth.toFloat()

        val smallest = min(bitmap.width, bitmap.height)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(
            smallest / 2F,
            smallest / 2F,
            smallest.toFloat() / 2F - borderWidth / 2,
            paint
        )

        return bitmap
    }

    companion object {
        const val DEFAULT_BORDER_COLOR = Color.WHITE
        const val DEFAULT_BORDER_WIDTH = 2
    }
}