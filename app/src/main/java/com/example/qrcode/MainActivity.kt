package com.example.qrcode

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import com.google.zxing.integration.android.IntentIntegrator
import net.glxn.qrgen.android.QRCode

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
    }
    private fun setupUI() {
        val qr_code_image = findViewById<ImageView>(R.id.qr_code)
        val qr_code_text = findViewById<EditText>(R.id.qr_text)
        val qr_code_button = findViewById<Button>(R.id.generate_btn)
        val scanerBtn = findViewById<Button>(R.id.scaner_btn)

        qr_code_button.setOnClickListener {
            val myBitmap: Bitmap = QRCode.from(qr_code_text.text.toString()).bitmap()
            qr_code_image.setImageBitmap(myBitmap)
        }

        scanerBtn.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.setOrientationLocked(false)
            scanner.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result!=null){
                findViewById<TextView>(R.id.tv_set_text).text = result.contents
                Toast(this).showCustomToast(result.contents, this)
            }
        }
    }
}

fun Toast.showCustomToast(massage: String, activity: Activity) {
    val layout = activity.layoutInflater.inflate(R.layout.custom_toast,
        activity.findViewById(R.id.toast_layout_custom))
    val toastMessage = layout.findViewById<TextView>(R.id.toast_message)
    toastMessage.text = massage

    this.apply {
        setGravity(Gravity.BOTTOM, 0, 800)
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }


}