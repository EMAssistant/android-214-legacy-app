package com.randomvoids.emassistant

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.github.gcacace.signaturepad.views.SignaturePad
import timber.log.Timber
import java.io.File

class HumanSignatureCollectorActivity : AppCompatActivity() {
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_human_signature_capture)
        val signaturePad: SignaturePad = findViewById(R.id.signature_pad)
        val clearButton: Button = findViewById(R.id.clear_button)
        val saveButton: Button = findViewById(R.id.save_button)
        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
                //Event triggered when the pad is signed
                clearButton.isEnabled = true
                saveButton.isEnabled = true
            }

            override fun onClear() {
                clearButton.isEnabled = false
                saveButton.isEnabled = false
                //Event triggered when the pad is cleared
            }
        })

        clearButton.setOnClickListener {
            signaturePad.clear()
        }

        saveButton.setOnClickListener {
            val bitmap = signaturePad.signatureBitmap
            saveBitmap(bitmap)
            finish()
        }
    }


    private fun saveBitmap(bitmap: Bitmap) {
        var newBitmap = bitmap
        val signatureFile = File(this.filesDir, "humanSignature.png")

        val outputStream = this.contentResolver.openOutputStream(signatureFile.toUri())

        //lets crop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            newBitmap = crop(bitmap)
        }

        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream!!.close()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun crop(bitmap: Bitmap): Bitmap {
        val baseline = bitmap.getColor(0,0)
        var stop = false
        var cropTop = 0
        for (y in (0 until bitmap.height)) {
            for (x in (0 until bitmap.width)) {
                if(baseline != bitmap.getColor(x, y)) {
                    stop = true
                    break
                }
            }
            if (stop) {
                if (y > 1)
                    cropTop = y-2
                break
            }
        }

        stop = false
        var cropBot = bitmap.height-1
        for (y in (bitmap.height-1 downTo 0)) {
            for (x in (0 until bitmap.width)) {
                if(baseline != bitmap.getColor(x, y)) {
                    stop = true
                    break
                }
            }
            if (stop) {
                if (y < bitmap.height-2)
                    cropBot = y+2
                break
            }
        }


        stop = false
        var cropLeft = 0
        for (x in (0 until bitmap.width)) {
            for (y in (0 until bitmap.height)) {
                if(baseline != bitmap.getColor(x, y)) {
                    stop = true
                    break
                }
            }
            if (stop) {
                if (x > 1)
                    cropLeft = x-2
                break
            }
        }

        stop = false
        var cropRight = bitmap.width-1
        for (x in (bitmap.width-1 downTo 0)) {
            for (y in (0 until bitmap.height)) {
                if(baseline != bitmap.getColor(x, y)) {
                    stop = true
                    break
                }
            }
            if (stop) {
                if (x < bitmap.width-2)
                    cropRight = x+2
                break
            }
        }
        Timber.d("cropLeft: $cropLeft")
        Timber.d("cropRight: $cropRight")
        Timber.d("cropTop: $cropTop")
        Timber.d("cropBot: $cropBot")
        return Bitmap.createBitmap(bitmap, cropLeft, cropTop, (cropRight-cropLeft), (cropBot-cropTop))
    }
}