package com.josipsh

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.googlecode.tesseract.android.TessBaseAPI
import com.josipsh.databinding.ActivityMainBinding
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tessBaseAPI: TessBaseAPI
    private var tessInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tessBaseAPI = TessBaseAPI {
            binding.textInput.setText("Progress: " + it.percent + " %")
        }

        libraryInit()

        binding.btnStart.setOnClickListener {
            startTranslating()
        }
    }

    private fun libraryInit() {
        extractAssets()

        val dataPath = getTessDataPath()
        val language = getLanguage()
        val engineMode = TessBaseAPI.OEM_LSTM_ONLY

        Log.i(
            "MainActivity", "Initializing Tesseract with: dataPath = [" + dataPath + "], " +
                    "language = [" + language + "], engineMode = [" + engineMode + "]"
        )
        try {
            tessBaseAPI.init(dataPath, language, engineMode)
            Snackbar.make(binding.root, "Tesseract initialized", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok") { }.show()
            tessInit = true
        } catch (e: IllegalArgumentException) {
            Snackbar.make(binding.root, "Cannot initialize Tesseract", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok") { }.show()
            tessInit = false
            Log.e(
                "MainActivity",
                "Cannot initialize Tesseract:",
                e
            )
        }
    }

    private fun startTranslating() {
        val imagePath: File = getImageFile()

        if (!tessInit) {
            Log.e(
                "MainActivity",
                "recognizeImage: Tesseract is not initialized"
            )
            return
        }
//        if (isProcessing()) {
//            Log.e(
//                cz.adaptech.tesseract4android.sample.ui.main.MainViewModel.TAG,
//                "recognizeImage: Processing is in progress"
//            )
//            return
//        }
        // Start process in another thread

        // Start process in another thread
        Thread {
            tessBaseAPI.setImage(imagePath)
            // Or set it as Bitmap, Pix,...
            // tessApi.setImage(imageBitmap);
            val startTime = SystemClock.uptimeMillis()

            // Use getHOCRText(0) method to trigger recognition with progress notifications and
            // ability to cancel ongoing processing.
            tessBaseAPI.getHOCRText(0)

            // Then get just normal UTF8 text as result. Using only this method would also trigger
            // recognition, but would just block until it is completed.
            val text: String = tessBaseAPI.getUTF8Text()
            // binding.textInput.setText(text)
//            result.postValue(text)
//            processing.postValue(false)
//
            Log.i("MainActivity", "Text = $text")

            val duration = SystemClock.uptimeMillis() - startTime
            Snackbar.make(binding.root, "Completed in "+duration / 1000f +"s \n " + text,
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok") { }.show()
        }.start()

    }

}