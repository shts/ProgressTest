package jp.shts.android.libraries.pausableprogress

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout

class StoriesProgressView : LinearLayout, PausableProgressView.Callback {

    interface StoryCallback {
        fun onReturnStories()
        fun onNextStory()
        fun onFinishStories()
    }

    companion object {
        private val PROGRESS_BAR_LAYOUT_PARAM = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        private val SPACE_LAYOUT_PARAM = LayoutParams(5, LayoutParams.WRAP_CONTENT)
    }

    private var progressBars = ArrayList<PausableProgressView>()

    var storiesCount: Int = -1
        set(value) {
            field = value
            bindViews()
        }

    var current: Int = 0
    private set

    var callback: StoryCallback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = HORIZONTAL
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()

        for (i in 0 until storiesCount) {
            val p = createProgressBar().also {
                progressBars.add(it)
            }
            addView(p)
            if (i + 1 < storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar() = PausableProgressView(context).apply {
        layoutParams = PROGRESS_BAR_LAYOUT_PARAM
        callback = this@StoriesProgressView
    }

    private fun createSpace() = View(context).apply {
        layoutParams = SPACE_LAYOUT_PARAM
    }

    fun startStory() {
        progressBars[current].start(object : AnimatorEndListener() {
            override fun onEnd() {
                if (current < storiesCount - 1) {
                    callback?.onNextStory()
                    startStory()
                } else {
                    callback?.onFinishStories()
                }
            }
        })
    }

    fun pauseStory() {
        progressBars[current].pause()
    }

    fun resumeStory() {
        progressBars[current].resume()
    }

    fun cancelStory() { // 多分使わないし必要ない
        progressBars[current].cancel()
        progressBars[current].min()
        current -= 2
        /*
        * 2019-12-27 18:14:21.591 18785-18785/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationStart
        * 2019-12-27 18:14:22.701 18785-18785/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationCancel
        * 2019-12-27 18:14:22.701 18785-18785/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationEnd
        */
    }

    fun endStory() { // 多分使わないし必要ない
        progressBars[current].end()
        /*
        2019-12-27 18:17:30.081 19251-19251/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationStart
        2019-12-27 18:17:31.345 19251-19251/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationEnd
         */
    }

    fun backStory() {
        if (current == 0) {
            startStory()
            return
        }
        progressBars[current].cancel(object : AnimatorEndListener() {
            override fun onEnd() {
                progressBars[current].min(object : AnimatorEndListener() {
                    override fun onEnd() {
                        if (0 <= current - 1) {
                            current--
                            startStory()
                        } else {
                            callback?.onReturnStories()
                        }
                    }
                })
            }
        })
        /*
         2019-12-27 18:15:56.042 19101-19101/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationStart
         2019-12-27 18:15:57.598 19101-19101/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationCancel
         2019-12-27 18:15:57.598 19101-19101/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationEnd
         */
    }

    fun skipStory() {
        // Execute cancel() and max() sequentially
//        progressBars[current].cancel(object : AnimatorEndListener() {
//            override fun onEnd() {
//                progressBars[current].max(object : AnimatorEndListener() {
//                    override fun onEnd() {
//                        if (current < storiesCount - 1) {
//                            current++
//                            startStory()
//                        } else {
//                            callback?.onFinishStories()
//                        }
//                    }
//                })
//            }
//        })
        progressBars[current].cancel()
        progressBars[current].max()
        current++
        if (current < storiesCount) {
            callback?.onNextStory()
            startStory()
        } else {
            callback?.onFinishStories()
        }
        /*
        * 2019-12-27 18:14:21.591 18785-18785/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationStart
        * 2019-12-27 18:14:22.701 18785-18785/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationCancel
        * 2019-12-27 18:14:22.701 18785-18785/jp.shts.android.libraries.pausableprogress D/PausableProgressView: onAnimationEnd
        */
    }

    override fun onStartProgress() {
//        Log.v("StoriesProgressView", "onStartProgress")
    }

    override fun onFinishProgress() {
        Log.v("StoriesProgressView", "onFinishProgress")
//        if (current < storiesCount - 1) {
//            callback?.onNextStory()
//            startStory()
//        } else {
//            callback?.onFinishStories()
//        }
    }

    abstract class AnimatorEndListener : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator?) {
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
        }

        abstract fun onEnd()
    }
}