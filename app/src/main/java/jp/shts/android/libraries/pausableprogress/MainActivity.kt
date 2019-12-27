package jp.shts.android.libraries.pausableprogress

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PausableProgressView"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storiesProgressView = findViewById<StoriesProgressView>(R.id.progress_view)
        storiesProgressView.storiesCount = 5

        findViewById<Button>(R.id.start).setOnClickListener {
            storiesProgressView.startStory()
        }
        findViewById<Button>(R.id.pause).setOnClickListener {
            storiesProgressView.pauseStory()
        }
        findViewById<Button>(R.id.resume).setOnClickListener {
            storiesProgressView.resumeStory()
        }
        findViewById<Button>(R.id.cancel).setOnClickListener {
            storiesProgressView.cancelStory()
        }
        findViewById<Button>(R.id.end).setOnClickListener {
            storiesProgressView.endStory()
        }
        findViewById<Button>(R.id.back).setOnClickListener {
            storiesProgressView.backStory()
        }
        findViewById<Button>(R.id.skip).setOnClickListener {
            storiesProgressView.skipStory()
        }
    }
}
