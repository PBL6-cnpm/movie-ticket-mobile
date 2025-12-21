package com.pbl6.pbl6_cinestech.ui.seatbooking

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
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.withMatrix
import com.pbl6.pbl6_cinestech.data.model.response.Seat
import kotlin.math.abs
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

    // Track max seats per row for centering
    private var maxSeatsInRow = 0

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

    private val occupiedStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.parseColor("#7F8C8D")
    }

    private val occupiedPatternPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7F8C8D")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val occupiedIconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.FILL
        textSize = 32f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
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
            seat.isOccupied = occupiedSeats.contains(seat.id)
        }

        // Calculate max seats in any row
        maxSeatsInRow = rows.maxOfOrNull { rowLabel ->
            seats.count { it.name.startsWith(rowLabel) }
        } ?: cols

        // Calculate grid dimensions based on max seats per row
        gridWidth = maxSeatsInRow + 6
        gridHeight = rows.size + 6

        if (width > 0 && height > 0) {
            seatSize = (minOf(width.toFloat() / gridWidth, height.toFloat() / gridHeight) * 0.8f)
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
        viewBounds.set(0f, 0f, width.toFloat(), height * 0.6f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && gridWidth > 0 && gridHeight > 0) {
            seatSize = (minOf(w.toFloat() / gridWidth, h.toFloat() / gridHeight) * 0.8f)
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

                if (!scaleGestureDetector.isInProgress) {
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

    // Calculate centered X position for a seat in a row
    private fun calculateCenteredSeatX(seatCountInRow: Int, seatIndexInRow: Int): Float {
        val maxRowWidth = maxSeatsInRow * (seatSize + seatSpacing) - seatSpacing
        val currentRowWidth = seatCountInRow * (seatSize + seatSpacing) - seatSpacing
        val offsetX = (maxRowWidth - currentRowWidth) / 2f

        return rowLabelWidth + offsetX + seatIndexInRow * (seatSize + seatSpacing)
    }

    private fun isTouchingSeat(x: Float, y: Float): Boolean {
        val startY = screenMargin + screenHeight + seatSpacing * 2

        rows.forEachIndexed { rowIndex, rowLabel ->
            val rowY = startY + rowIndex * (seatSize + seatSpacing)
            val rowSeats = seats.filter { it.name.startsWith(rowLabel) }
                .sortedBy { it.name.substring(1).toIntOrNull() ?: 0 }

            var skipNext = false
            rowSeats.forEachIndexed { seatIndex, seat ->
                if (skipNext) {
                    skipNext = false
                    return@forEachIndexed
                }

                val isCoupleSeat = seat.type.name.contains("Couple", ignoreCase = true)

                val seatX = calculateCenteredSeatX(rowSeats.size, seatIndex)
                val seatWidth = if (isCoupleSeat) (seatSize * 2) + seatSpacing else seatSize
                val rect = RectF(seatX, rowY, seatX + seatWidth, rowY + seatSize)

                if (rect.contains(x, y)) {
                    return true
                }

                if (isCoupleSeat) {
                    skipNext = true
                }
            }
        }
        return false
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

            var skipNext = false
            rowSeats.forEachIndexed { seatIndex, seat ->
                if (skipNext) {
                    skipNext = false
                    return@forEachIndexed
                }

                val isCoupleSeat = seat.type.name.contains("Couple", ignoreCase = true)

                val seatX = calculateCenteredSeatX(rowSeats.size, seatIndex)
                val seatWidth = if (isCoupleSeat) (seatSize * 2) + seatSpacing else seatSize
                val rect = RectF(seatX, rowY, seatX + seatWidth, rowY + seatSize)

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

                if (isCoupleSeat) {
                    skipNext = true
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
        val screenWidth = (maxSeatsInRow * (seatSize + seatSpacing)) - seatSpacing + rowLabelWidth
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

            var skipNext = false
            rowSeats.forEachIndexed { seatIndex, seat ->
                if (skipNext) {
                    skipNext = false
                    return@forEachIndexed
                }

                val isCoupleSeat = seat.type.name.contains("Couple", ignoreCase = true)

                val seatX = calculateCenteredSeatX(rowSeats.size, seatIndex)
                drawSeat(canvas, seat, seatX, y)

                // If this is a couple seat, skip the next position
                if (isCoupleSeat) {
                    skipNext = true
                }
            }
        }
    }

    private fun drawSeat(canvas: Canvas, seat: Seat, x: Float, y: Float) {
        val rect = RectF(x, y, x + seatSize, y + seatSize)

        if (seat.isOccupied) {
            drawOccupiedSeat(canvas, rect, x, y, seat)
        } else {
            // Check if this is a couple seat (type name contains "Couple" or similar identifier)
            val isCoupleSeat = seat.type.name.contains("Couple", ignoreCase = true)

            if (isCoupleSeat) {
                drawCoupleSeat(canvas, seat, x, y)
            } else {
                drawNormalSeat(canvas, seat, x, y)
            }
        }
    }

    private fun drawNormalSeat(canvas: Canvas, seat: Seat, x: Float, y: Float) {
        val rect = RectF(x, y, x + seatSize, y + seatSize)

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

        // Draw seat number
        val seatNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = seatSize * 0.35f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val seatNumber = seat.name.substring(1)
        canvas.drawText(
            seatNumber,
            x + seatSize / 2,
            y + seatSize / 2 + seatNumberPaint.textSize / 3,
            seatNumberPaint
        )

        // Draw legs
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

    private fun drawCoupleSeat(canvas: Canvas, seat: Seat, x: Float, y: Float) {
        // Couple seat occupies 2 seat slots (2 * seatSize + 1 * spacing)
        val seatWidth = (seatSize * 2) + seatSpacing
        val rect = RectF(x, y, x + seatWidth, y + seatSize)

        seatPaint.color = Color.parseColor(seat.type.color)
        canvas.drawRoundRect(rect, 12f, 12f, seatPaint)
        canvas.drawRoundRect(rect, 12f, 12f, seatStrokePaint)

        // Draw divider in the middle
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            strokeWidth = 2f
            style = Paint.Style.STROKE
            alpha = 100
        }
        val dividerX = x + seatWidth / 2
        canvas.drawLine(dividerX, y + seatSize * 0.2f, dividerX, y + seatSize * 0.8f, dividerPaint)

        if (seat.isSelected) {
            canvas.drawRoundRect(rect, 12f, 12f, selectedStrokePaint)

            val checkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFD700")
                strokeWidth = 5f
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
            }
            val cx = x + seatWidth / 2
            val cy = y + seatSize / 2
            val size = seatSize * 0.3f

            val path = Path().apply {
                moveTo(cx - size / 2, cy)
                lineTo(cx - size / 6, cy + size / 2)
                lineTo(cx + size / 2, cy - size / 3)
            }
            canvas.drawPath(path, checkPaint)
        }

        // Draw seat number (centered on the wider seat)
        val seatNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = seatSize * 0.35f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val seatNumber = seat.name.substring(1)
        canvas.drawText(
            seatNumber,
            x + seatWidth / 2,
            y + seatSize / 2 + seatNumberPaint.textSize / 3,
            seatNumberPaint
        )

        // Draw heart icon for couple seat
        val heartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF69B4")
            style = Paint.Style.FILL
            alpha = 150
        }
        val heartSize = seatSize * 0.2f
        val heartX = x + seatWidth / 2
        val heartY = y + seatSize * 0.25f
        drawHeart(canvas, heartX, heartY, heartSize, heartPaint)

        // Draw 4 legs for couple seat
        val legPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#ECF0F1")
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
        val legHeight = 8f
        val legSpacing = seatWidth / 3
        canvas.drawLine(x + legSpacing * 0.5f, y + seatSize, x + legSpacing * 0.5f, y + seatSize + legHeight, legPaint)
        canvas.drawLine(x + legSpacing * 1f, y + seatSize, x + legSpacing * 1f, y + seatSize + legHeight, legPaint)
        canvas.drawLine(x + legSpacing * 2f, y + seatSize, x + legSpacing * 2f, y + seatSize + legHeight, legPaint)
        canvas.drawLine(x + legSpacing * 2.5f, y + seatSize, x + legSpacing * 2.5f, y + seatSize + legHeight, legPaint)
    }

    private fun drawHeart(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        val path = Path().apply {
            moveTo(cx, cy + size * 0.3f)

            // Left curve
            cubicTo(
                cx - size * 0.5f, cy - size * 0.3f,
                cx - size, cy + size * 0.1f,
                cx, cy + size
            )

            // Right curve
            cubicTo(
                cx + size, cy + size * 0.1f,
                cx + size * 0.5f, cy - size * 0.3f,
                cx, cy + size * 0.3f
            )
            close()
        }
        canvas.drawPath(path, paint)
    }

    private fun drawOccupiedSeat(canvas: Canvas, rect: RectF, x: Float, y: Float, seat: Seat) {
        // Check if this is a couple seat
        val isCoupleSeat = seat.type.name.contains("Couple", ignoreCase = true)

        // Couple seat occupies 2 seat slots
        val seatWidth = if (isCoupleSeat) (seatSize * 2) + seatSpacing else seatSize
        val adjustedRect = RectF(x, y, x + seatWidth, y + seatSize)

        // Draw seat with darker gradient
        val gradient = LinearGradient(
            adjustedRect.left, adjustedRect.top,
            adjustedRect.left, adjustedRect.bottom,
            Color.parseColor("#7F8C8D"),
            Color.parseColor("#5D6D7E"),
            Shader.TileMode.CLAMP
        )
        occupiedPaint.shader = gradient
        canvas.drawRoundRect(adjustedRect, 12f, 12f, occupiedPaint)
        occupiedPaint.shader = null

        // Draw darker border
        canvas.drawRoundRect(adjustedRect, 12f, 12f, occupiedStrokePaint)

        // Draw diagonal stripes pattern
        val stripePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#596270")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        val stripeSpacing = seatSize / 4
        for (i in -2..6) {
            val startX = x + (i * stripeSpacing)
            val startY = y
            val endX = startX + seatWidth
            val endY = y + seatSize
            canvas.drawLine(startX, startY, endX, endY, stripePaint)
        }

        // Draw "X" mark in the center
        val cx = x + seatWidth / 2
        val cy = y + seatSize / 2
        val xSize = seatSize * 0.4f

        val xPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#ECF0F1")
            style = Paint.Style.STROKE
            strokeWidth = 6f
            strokeCap = Paint.Cap.ROUND
        }

        // Draw X
        canvas.drawLine(
            cx - xSize / 2, cy - xSize / 2,
            cx + xSize / 2, cy + xSize / 2,
            xPaint
        )
        canvas.drawLine(
            cx + xSize / 2, cy - xSize / 2,
            cx - xSize / 2, cy + xSize / 2,
            xPaint
        )

        // Add legs to occupied seat
        val legPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#5D6D7E")
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
        val legHeight = 8f

        if (isCoupleSeat) {
            // 4 legs for couple seat
            val legSpacing = seatWidth / 3
            canvas.drawLine(x + legSpacing * 0.5f, y + seatSize, x + legSpacing * 0.5f, y + seatSize + legHeight, legPaint)
            canvas.drawLine(x + legSpacing * 1f, y + seatSize, x + legSpacing * 1f, y + seatSize + legHeight, legPaint)
            canvas.drawLine(x + legSpacing * 2f, y + seatSize, x + legSpacing * 2f, y + seatSize + legHeight, legPaint)
            canvas.drawLine(x + legSpacing * 2.5f, y + seatSize, x + legSpacing * 2.5f, y + seatSize + legHeight, legPaint)
        } else {
            // 2 legs for normal seat
            canvas.drawLine(x + 15f, y + seatSize, x + 15f, y + seatSize + legHeight, legPaint)
            canvas.drawLine(x + seatWidth - 15f, y + seatSize, x + seatWidth - 15f, y + seatSize + legHeight, legPaint)
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