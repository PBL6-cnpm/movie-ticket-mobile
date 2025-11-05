package com.pbl6.pbl6_cinestech.ui.seatbooking

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.withMatrix
import com.pbl6.pbl6_cinestech.data.model.response.Seat
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min

class CinemaSeatView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Seat data
    private var rows = listOf<String>()
    private var cols = 0
    private var seats = mutableListOf<Seat>()
    private val selectedSeats = mutableSetOf<String>()

    // Drawing properties
    private var seatSize = 80f
    private val seatSpacing = 20f
    private val screenHeight = 100f
    private val screenMargin = 150f
    private var rowLabelWidth = 100f

    // Scale and Pan with Matrix
    private var scale = 1f
    private val minScale = 1f
    private val maxScale = 5f

    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private val matrixValues = FloatArray(9)
    private val animationMatrix = Matrix()

    // For pan
    private val start = PointF()
    private val mid = PointF()
    private var lastFocusX = 0f
    private var lastFocusY = 0f

    // For touch tracking
    private var lastMoveX = 0f
    private var lastMoveY = 0f
    private var activePointerId = -1
    private var pointerCount = 0

    // For single-finger pan detection
    private var isSingleFingerPanning = false
    private var isTappingMode = false

    private var isAnimating = false

    private val gridBounds = RectF()
    private val viewBounds = RectF()

    private var gridWidth = 0
    private var gridHeight = 0

    private val scaleGestureDetector: ScaleGestureDetector

    // Paints
    private val screenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2C3E50")
        style = Paint.Style.FILL
    }

    private val screenTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val seatPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val seatStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.WHITE
    }

    private val selectedStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#FFD700")
    }

    private val occupiedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#95A5A6")
        style = Paint.Style.FILL
    }

    private val rowLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7F8C8D")
        textSize = 36f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val colLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7F8C8D")
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    interface ZoomChangeListener {
        fun onZoomChanged(zoomLevel: Int)
    }

    private var zoomChangeListener: ZoomChangeListener? = null

    // Listener for seat selection
    interface OnSeatSelectionListener {
        fun onSeatSelected(seat: Seat)
        fun onSeatDeselected(seat: Seat)
    }

    private var seatSelectionListener: OnSeatSelectionListener? = null

    init {
        setBackgroundColor(Color.parseColor("#1A1A2E"))
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    fun setZoomChangeListener(listener: ZoomChangeListener) {
        this.zoomChangeListener = listener
    }

    fun setSeatSelectionListener(listener: OnSeatSelectionListener) {
        this.seatSelectionListener = listener
    }

    fun getZoomLevel(): Int {
        val normalizedScale = (scale - minScale) / (maxScale - minScale)
        return (normalizedScale * 100).toInt().coerceIn(0, 100)
    }

    fun setSeatsData(
        rows: List<String>,
        cols: Int,
        seatsList: List<Seat>,
        occupiedSeats: List<String>
    ) {
        this.rows = rows
        this.cols = cols
        this.seats = seatsList.toMutableList()

        // Mark occupied seats
        seats.forEach { seat ->
            seat.isOccupied = occupiedSeats.contains(seat.name)
        }

        // Calculate grid dimensions
        gridWidth = cols + 2 + (cols / 4).toInt()
        gridHeight = rows.size + 4

        if (width>0 && height>0){
            seatSize = (minOf(width.toFloat() / gridWidth, height.toFloat() / gridHeight) * 0.8).toFloat()
            rowLabelWidth = seatSize * 1.2f
        }
        invalidate()
    }


    fun resetZoomPan() {
        if (isAnimating) return

        val currentMatrixValues = FloatArray(9)
        matrix.getValues(currentMatrixValues)

        val startScale = scale
        val startTranslateX = currentMatrixValues[Matrix.MTRANS_X]
        val startTranslateY = currentMatrixValues[Matrix.MTRANS_Y]

        val targetScale = minScale
        val targetTranslateX = (width - gridWidth) / 2f
        val targetTranslateY = (height - gridHeight) / 2f

        animationMatrix.set(matrix)
        isAnimating = true

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float

            val currentScale = startScale + (targetScale - startScale) * progress
            val currentTranslateX =
                startTranslateX + (targetTranslateX - startTranslateX) * progress
            val currentTranslateY =
                startTranslateY + (targetTranslateY - startTranslateY) * progress

            animationMatrix.reset()
            animationMatrix.postScale(currentScale, currentScale)
            animationMatrix.postTranslate(currentTranslateX, currentTranslateY)

            invalidate()

            if (progress >= 1f) {
                matrix.set(animationMatrix)
                scale = targetScale
                isAnimating = false
                updateGridBounds()
            }
        }

        animator.start()
    }

    private fun updateGridBounds() {
        gridBounds.set(0f, 0f, gridWidth * seatSize, gridHeight * seatSize)
        matrix.mapRect(gridBounds)
        viewBounds.set(0f, 0f, width.toFloat(), height.toFloat())
        Log.d("onSizeChanged", "onSizeChanged gridboud: ${width.toFloat()-(gridWidth*seatSize)} ${height.toFloat()-(gridHeight*seatSize)}")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && gridWidth > 0 && gridHeight > 0) {
            seatSize = (minOf(w.toFloat() / gridWidth, h.toFloat() / gridHeight) * 0.8).toFloat()
            rowLabelWidth = seatSize * 1.2f
        }

    }

    private fun constrainPan() {
        updateGridBounds()

        matrix.getValues(matrixValues)

        var adjustX = 0f
        var adjustY = 0f

        val extraPanSpace = (scale - minScale) * seatSize * 10
        if (gridBounds.width() < viewBounds.width()) {
            adjustX = (viewBounds.width() - gridBounds.width()) / 2 - gridBounds.left
        } else {
            if (gridBounds.left > extraPanSpace) {
                adjustX = -(gridBounds.left - extraPanSpace)
            } else if (gridBounds.right < viewBounds.width() - extraPanSpace) {
                adjustX = (viewBounds.width() - extraPanSpace) - gridBounds.right
            }
        }

        if (gridBounds.height() < viewBounds.height()) {
            adjustY = (viewBounds.height() - gridBounds.height()) / 2 - gridBounds.top
        } else {
            if (gridBounds.top > extraPanSpace) {
                adjustY = -(gridBounds.top - extraPanSpace)
            } else if (gridBounds.bottom < viewBounds.height() - extraPanSpace) {
                adjustY = (viewBounds.height() - extraPanSpace) - gridBounds.bottom
            }
        }
        Log.d("onSizeChanged", "constrainPan: $adjustX $adjustY $extraPanSpace ${gridBounds.height()} ${viewBounds.height()}")
        if (adjustX != 0f || adjustY != 0f) {
            matrix.postTranslate(adjustX, adjustY)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scale *= detector.scaleFactor

            scale = max(minScale, min(scale, maxScale))

            val focusX = detector.focusX
            val focusY = detector.focusY

            matrix.postScale(detector.scaleFactor, detector.scaleFactor, focusX, focusY)

            constrainPan()

            zoomChangeListener?.onZoomChanged(getZoomLevel())

            invalidate()
            return true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        pointerCount = event.pointerCount

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = event.getPointerId(0)

                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                lastFocusX = event.x
                lastFocusY = event.y

                lastMoveX = event.x
                lastMoveY = event.y

                isSingleFingerPanning = false
                isTappingMode = false

                if (!scaleGestureDetector.isInProgress){
                    val touchPoint = getTouchPoint(event.x, event.y)
                    val seatTapped = isTouchingSeat(touchPoint[0], touchPoint[1])
                    if (seatTapped) {
                        isTappingMode = true
                        isSingleFingerPanning = false
                    } else {
                        isSingleFingerPanning = true
                        isTappingMode = false
                    }
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                isSingleFingerPanning = false
                isTappingMode = false
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex < 0) return true

                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)
                val dx = x - lastMoveX
                val dy = y - lastMoveY

                if (abs(dx) > 15f || abs(dy) > 15f) {
                    isSingleFingerPanning = true
                    isTappingMode = false
                }

                // Xử lý pan giống TapPuzzleView
                if (pointerCount >= 2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    if (!scaleGestureDetector.isInProgress) {
                        val dx = x - lastFocusX
                        val dy = y - lastFocusY
                        matrix.postTranslate(dx, dy)
                        constrainPan()
                        invalidate()
                    }
                } else if (isSingleFingerPanning) {
                    matrix.postTranslate(dx, dy)
                    constrainPan()
                    invalidate()
                }

                lastFocusX = x
                lastFocusY = y
                lastMoveX = x
                lastMoveY = y
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (isTappingMode) {
                    val touchPoint = getTouchPoint(event.x, event.y)
                    handleSeatTap(touchPoint[0], touchPoint[1])
                }

                activePointerId = -1
                isSingleFingerPanning = false
                isTappingMode = false
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                activePointerId = -1
                isSingleFingerPanning = false
                isTappingMode = false
                invalidate()
            }
        }

        return true
    }

    private fun isTouchingSeat(x: Float, y: Float): Boolean {
        val startY = screenMargin + screenHeight + seatSpacing * 2

        rows.forEachIndexed { rowIndex, rowLabel ->
            val rowY = startY + rowIndex * (seatSize + seatSpacing)
            val rowSeats = seats.filter { it.name.startsWith(rowLabel) }

            rowSeats.forEach { seat ->
                val colNumber = seat.name.substring(1).toIntOrNull() ?: 0
                val seatX = rowLabelWidth + (colNumber - 1) * (seatSize + seatSpacing)
                val rect = RectF(seatX, rowY, seatX + seatSize, rowY + seatSize)

                if (rect.contains(x, y)) {
                    return true // Đã chạm vào một chiếc ghế
                }
            }
        }
        return false // Không chạm vào ghế nào cả
    }

    private fun getTouchPoint(x: Float, y: Float): FloatArray {
        val invertedMatrix = Matrix()
        matrix.invert(invertedMatrix)
        val touchPoint = floatArrayOf(x, y)
        invertedMatrix.mapPoints(touchPoint)
        return touchPoint
    }

    private fun handleSeatTap(x: Float, y: Float) {
        val startY = screenMargin + screenHeight + seatSpacing * 2

        rows.forEachIndexed { rowIndex, rowLabel ->
            val rowY = startY + rowIndex * (seatSize + seatSpacing)
            val rowSeats = seats.filter { it.name.startsWith(rowLabel) }
                .sortedBy { it.name.substring(1).toIntOrNull() ?: 0 }

            rowSeats.forEach { seat ->
                val colNumber = seat.name.substring(1).toIntOrNull() ?: 0
                val seatX = rowLabelWidth + (colNumber - 1) * (seatSize + seatSpacing)

                val rect = RectF(seatX, rowY, seatX + seatSize, rowY + seatSize)

                if (rect.contains(x, y) && !seat.isOccupied) {
                    seat.isSelected = !seat.isSelected
                    if (seat.isSelected) {
                        selectedSeats.add(seat.name)
                        seatSelectionListener?.onSeatSelected(seat)
                    } else {
                        selectedSeats.remove(seat.name)
                        seatSelectionListener?.onSeatDeselected(seat)
                    }
                    invalidate()
                    return
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (rows.isEmpty() || cols == 0) return

        if (scale == 1f && !isAnimating) {
            val totalWidth = gridWidth * seatSize
            val totalHeight = gridHeight * seatSize
            val drawLeft = (width - totalWidth) / 2f
            val drawTop = (height - totalHeight) / 2f
            matrix.setTranslate(drawLeft, drawTop)
            updateGridBounds()
        }

        val drawMatrix = if (isAnimating) animationMatrix else matrix

        canvas.withMatrix(drawMatrix) {
            drawScreen(canvas)
            drawSeats(canvas)
        }
    }

    private fun drawScreen(canvas: Canvas) {
        val screenWidth = (cols * (seatSize + seatSpacing)) - seatSpacing + rowLabelWidth
        val screenRect = RectF(
            rowLabelWidth,
            0f,
            screenWidth,
            screenHeight
        )

        val gradient = LinearGradient(
            screenRect.left, screenRect.top,
            screenRect.left, screenRect.bottom,
            Color.parseColor("#34495E"),
            Color.parseColor("#2C3E50"),
            Shader.TileMode.CLAMP
        )
        screenPaint.shader = gradient

        canvas.drawRoundRect(screenRect, 20f, 20f, screenPaint)

        canvas.drawText(
            "SCREEN",
            screenRect.centerX(),
            screenRect.centerY() + 15f,
            screenTextPaint
        )

        screenPaint.shader = null
    }

    private fun drawSeats(canvas: Canvas) {
        val startY = screenHeight + seatSpacing * 2 + screenMargin

        rows.forEachIndexed { rowIndex, rowLabel ->
            val y = startY + rowIndex * (seatSize + seatSpacing)

            canvas.drawText(
                rowLabel,
                rowLabelWidth / 2,
                y + seatSize / 2 + 12f,
                rowLabelPaint
            )

            val rowSeats = seats.filter { it.name.startsWith(rowLabel) }
                .sortedBy { it.name.substring(1).toIntOrNull() ?: 0 }

            rowSeats.forEachIndexed { seatIndex, seat ->
                val colNumber = seat.name.substring(1).toIntOrNull() ?: (seatIndex + 1)
                val x = rowLabelWidth + (colNumber - 1) * (seatSize + seatSpacing)

                if (rowIndex == 0) {
                    canvas.drawText(
                        colNumber.toString(),
                        x + seatSize / 2,
                        startY - seatSpacing,
                        colLabelPaint
                    )
                }

                drawSeat(canvas, seat, x, y)
            }
        }
    }

    private fun drawSeat(canvas: Canvas, seat: Seat, x: Float, y: Float) {
        val rect = RectF(x, y, x + seatSize, y + seatSize)

        if (seat.isOccupied) {
            canvas.drawRoundRect(rect, 12f, 12f, occupiedPaint)

            val xPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#7F8C8D")
                strokeWidth = 4f
                style = Paint.Style.STROKE
            }
            val padding = seatSize * 0.25f
            canvas.drawLine(
                x + padding,
                y + padding,
                x + seatSize - padding,
                y + seatSize - padding,
                xPaint
            )
            canvas.drawLine(
                x + seatSize - padding,
                y + padding,
                x + padding,
                y + seatSize - padding,
                xPaint
            )
        } else {
            seatPaint.color = Color.parseColor(seat.type.color)
            canvas.drawRoundRect(rect, 12f, 12f, seatPaint)
            canvas.drawRoundRect(rect, 12f, 12f, seatStrokePaint)

            if (seat.isSelected) {
                canvas.drawRoundRect(rect, 12f, 12f, selectedStrokePaint)

                val checkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#FFD700")
                    strokeWidth = 5f
                    style = Paint.Style.STROKE
                    strokeCap = Paint.Cap.ROUND
                }
                val cx = x + seatSize / 2
                val cy = y + seatSize / 2
                val size = seatSize * 0.3f

                val path = Path().apply {
                    moveTo(cx - size / 2, cy)
                    lineTo(cx - size / 6, cy + size / 2)
                    lineTo(cx + size / 2, cy - size / 3)
                }
                canvas.drawPath(path, checkPaint)
            }

            val legPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#ECF0F1")
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }
            val legHeight = 8f
            canvas.drawLine(x + 15f, y + seatSize, x + 15f, y + seatSize + legHeight, legPaint)
            canvas.drawLine(
                x + seatSize - 15f,
                y + seatSize,
                x + seatSize - 15f,
                y + seatSize + legHeight,
                legPaint
            )
        }
    }

    fun getSelectedSeats(): List<Seat> {
        return seats.filter { it.isSelected }
    }

    fun clearSelection() {
        seats.forEach { it.isSelected = false }
        selectedSeats.clear()
        invalidate()
    }
}