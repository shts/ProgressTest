package jp.shts.android.libraries.pausableprogress

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout

class PausableProgressView: FrameLayout, Animator.AnimatorPauseListener, Animator.AnimatorListener {

    interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }

    companion object {
        private const val TAG = "PausableProgressView"
    }

    private var animator: ObjectAnimator? = null

    var duration: Long = 3000L
    set(value) {
        field = value
        animator?.duration = field
    }

    var callback: Callback? = null

    private lateinit var front: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.pausable_progress, this)
        front = findViewById(R.id.front)
        // https://stackoverflow.com/questions/18283196/android-objectanimator-scalex-only-in-one-direction
        front.pivotX = 0f

        ObjectAnimator.ofFloat(front, "scaleX", 0f, 0f).apply { duration = 0 }.start()

        animator = ObjectAnimator.ofFloat(front, "scaleX", 0f, 1f).apply {
            interpolator = LinearInterpolator()
            duration = this@PausableProgressView.duration
            addListener(this@PausableProgressView)
            addPauseListener(this@PausableProgressView)
        }
    }

    fun start(listener: Animator.AnimatorListener? = null) {
        listener?.let { li ->
            animator?.addListener(li)
        }
        animator?.start()
    }

    fun pause() {
        animator?.pause()
    }

    fun resume() {
        animator?.resume()
    }

    fun cancel(listener: Animator.AnimatorListener? = null) {
        listener?.let { li ->
            animator?.addListener(li)
        }
        animator?.cancel()
    }

    fun end() {
        animator?.end()
    }

    fun min(listener: Animator.AnimatorListener? = null) {
        ObjectAnimator.ofFloat(front, "scaleX", 0f, 0f).apply {
            duration = 0L
            listener?.let { addListener(it) }
        }.start()
    }

    fun max(listener: Animator.AnimatorListener? = null) {
        ObjectAnimator.ofFloat(front, "scaleX", 1f, 1f).apply {
            duration = 0L
            listener?.let { addListener(it) }
        }.start()
    }

    override fun onDetachedFromWindow() {
        // https://developer.android.com/reference/android/animation/Animator.html#removeAllListeners()
        animator?.removeAllListeners()
        clearAnimation()
        super.onDetachedFromWindow()
    }

    override fun onAnimationRepeat(p0: Animator?) {
        Log.d(TAG, "onAnimationRepeat")
    }

    override fun onAnimationEnd(p0: Animator?) {
        Log.d(TAG, "onAnimationEnd")
        callback?.onFinishProgress()
    }

    override fun onAnimationCancel(p0: Animator?) {
        Log.d(TAG, "onAnimationCancel")
    }

    override fun onAnimationStart(p0: Animator?) {
        Log.d(TAG, "onAnimationStart")
        callback?.onStartProgress()
    }

    override fun onAnimationPause(p0: Animator?) {
        Log.d(TAG, "onAnimationPause")
    }

    override fun onAnimationResume(p0: Animator?) {
        Log.d(TAG, "onAnimationResume")
    }
}
