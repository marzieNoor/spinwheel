package com.marzie.nourmohammadi.lib.ui

import android.animation.Animator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.DecelerateInterpolator
import com.marzie.nourmohammadi.lib.drawableToBitmap
import com.marzie.nourmohammadi.lib.model.LuckyItem


class PielView : View {
    private var mRange = RectF()
    private var mRadius = 0
    private var mArcPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private val mStartAngle = 0f
    private var mCenter = 0
    private var mPadding = 0
    private var mTargetIndex = 0
    private var mRoundOfNumber = 4
    private var isRunning = false
    private var defaultBackgroundColor = -1
    private var drawableCenterImage: Drawable? = null
    private var textColor = -0x1
    private var textFont: Typeface? = null
    private var mLuckyItemList: List<LuckyItem>? = null
    private var mPieRotateListener: PieRotateListener? = null
    private var animation: ViewPropertyAnimator? = null
    private val emptyBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)

    interface PieRotateListener {
        fun rotateDone(index: Int)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    fun setPieRotateListener(listener: PieRotateListener?) {
        mPieRotateListener = listener
    }

    private fun init() {
        mArcPaint = Paint()
        mArcPaint!!.isAntiAlias = true
        mArcPaint!!.isDither = true
        mTextPaint = Paint()
        mTextPaint!!.color = textColor
        textFont?.let {
            mTextPaint!!.typeface = it
        }
        mTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics
        )
        mRange = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (mPadding + mRadius).toFloat(),
            (mPadding + mRadius).toFloat()
        )
        animation = animate()
    }

    fun setData(luckyItemList: List<LuckyItem>?) {
        mLuckyItemList = luckyItemList
        invalidate()
    }

    fun setPieBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setPieCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setPieTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    fun setPieTextFont(fontPath: Typeface) {
        textFont = fontPath
        invalidate()
    }

    private fun drawPieBackgroundWithBitmap(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawBitmap(
            bitmap, null, Rect(
                mPadding / 2,
                mPadding / 2,
                measuredWidth - mPadding / 2,
                measuredHeight - mPadding / 2
            ), null
        )
    }

    /**
     *
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mLuckyItemList == null) {
            return
        }
        drawBackgroundColor(canvas, defaultBackgroundColor)
        init()
        var tmpAngle = mStartAngle
        mLuckyItemList?.let {
            val sweepAngle = (360 / it.size).toFloat()
            for (i in it.indices) {
                mArcPaint!!.color = it[i].color
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
                drawText(canvas, tmpAngle, sweepAngle, it[i].text)
                if (it[i].icon == null) {
                    drawImage(
                        canvas, tmpAngle, emptyBitmap
                    )
                } else {
                    drawImage(
                        canvas, tmpAngle, it[i].icon!!
                    )
                }
                tmpAngle += sweepAngle
            }
        }

        drawCenterImage(canvas, drawableCenterImage)
    }


    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == -1) return
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.color = color
        canvas.drawCircle(
            mCenter.toFloat(), mCenter.toFloat(), mCenter.toFloat(), mBackgroundPaint!!
        )
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = Math.min(measuredWidth, measuredHeight)
        mPadding = if (paddingLeft == 0) 10 else paddingLeft
        mRadius = width - mPadding * 2
        mCenter = width / 2
        setMeasuredDimension(width, width)
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param bitmap
     */
    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = mRadius / mLuckyItemList!!.size
        val angle = ((tmpAngle + 360 / mLuckyItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * Math.sin(angle.toDouble())).toInt()
        val rect = Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2)
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawCenterImage(canvas: Canvas, drawable: Drawable?) {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);
        var bitmap: Bitmap? = drawableToBitmap(drawable!!)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, 90, 90, false)
        canvas.drawBitmap(
            bitmap,
            (measuredWidth / 2 - bitmap.width / 2).toFloat(),
            (measuredHeight / 2 - bitmap.height / 2).toFloat(),
            null
        )
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private fun drawText(canvas: Canvas, tmpAngle: Float, sweepAngle: Float, mStr: String?) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        val textWidth = mTextPaint!!.measureText(mStr)
        val hOffset = (mRadius * Math.PI / mLuckyItemList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mRadius / 2 / 4
        canvas.drawTextOnPath(mStr!!, path, hOffset.toFloat(), vOffset.toFloat(), mTextPaint!!)
    }

    private val angleOfIndexTarget: Float
        /**
         * @return
         */
        private get() {
            val tempIndex = if (mTargetIndex == 0) 1 else mTargetIndex
            return (360 / mLuckyItemList!!.size * tempIndex).toFloat()
        }

    /**
     * @param numberOfRound
     */
    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }

    /**
     * @param index
     */
    fun rotateTo(index: Int) {
        if (isRunning) {
            return
        }
        mTargetIndex = index
        rotation = 0f
        val targetAngle =
            360 * mRoundOfNumber + 270 - angleOfIndexTarget + 360 / mLuckyItemList!!.size / 2
        animate().setInterpolator(DecelerateInterpolator())
            .setDuration(mRoundOfNumber * 1000 + 900L)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isRunning = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    isRunning = false
                    if (mPieRotateListener != null) {
                        mPieRotateListener!!.rotateDone(mTargetIndex)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).rotation(targetAngle).start()

    }

    fun stopTo(index: Int) {
        if (!isRunning) {
            return
        }
        setRound(1)
        mTargetIndex = index
        rotation = 0f
        val targetAngle =
            360 * mRoundOfNumber + 270 - angleOfIndexTarget + 360 / mLuckyItemList!!.size / 2
        animate().setInterpolator(DecelerateInterpolator())
            .setDuration(mRoundOfNumber * 1000 + 900L)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isRunning = true
                }

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {
                    isRunning = false
                    if (mPieRotateListener != null) {
                        mPieRotateListener!!.rotateDone(mTargetIndex)
                    }
                }

                override fun onAnimationRepeat(animation: Animator) {}
            }).rotation(targetAngle).start()

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}
