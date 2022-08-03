package com.example.qrcode

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.qrcode.Asymmetric.Companion.decryptMessage
import com.example.qrcode.Asymmetric.Companion.encryptMessage
import com.google.zxing.integration.android.IntentIntegrator
import net.glxn.qrgen.android.QRCode
import java.util.*

class MainActivity : AppCompatActivity() {

    var privateKey = BuildConfig.PRIVATE_KEY
    var publicKey = BuildConfig.PUBLIC_KEY

    private val TAG = "MainActivity"
//    val keyPairGenerator = Asymmetric()
//    // Generate private and public key
//    @RequiresApi(Build.VERSION_CODES.O)
//    val privateKey: String = Base64.getEncoder().encodeToString(keyPairGenerator.privateKey.encoded)
//    @RequiresApi(Build.VERSION_CODES.O)
//    val publicKey: String = Base64.getEncoder().encodeToString(keyPairGenerator.publicKey.encoded)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", privateKey)
        Log.d("MainActivity", publicKey)

        setupUI()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI() {
        val qr_code_image = findViewById<ImageView>(R.id.qr_code)
        val qr_code_text = findViewById<EditText>(R.id.qr_text)
        val qr_code_button = findViewById<Button>(R.id.generate_btn)
        val scanerBtn = findViewById<Button>(R.id.scaner_btn)

        qr_code_button.setOnClickListener {
            val text = qr_code_text.text.toString()
            if (text.isNotEmpty()){
                val myBitmap: Bitmap = QRCode.from(testAsymmetricEncrypt(text)).bitmap()
                qr_code_image.setImageBitmap(myBitmap)
            }else{
                Toast.makeText(this, "Oldin Text kiriting", Toast.LENGTH_SHORT).show()
            }
        }

        scanerBtn.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.setOrientationLocked(false)
            scanner.initiateScan()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            val text = testAsymmetricDecrypted(result.contents)
            if (result!=null){
                findViewById<TextView>(R.id.tv_set_text).text = text
                Toast(this).showCustomToast(text, this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testAsymmetricDecrypted(encryptedValue: String): String {
        // Decrypt
        val decryptedText = decryptMessage(encryptedValue, privateKey)
        Log.d(TAG, "Decrypted output: $decryptedText")
        return decryptedText
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testAsymmetricEncrypt(secretText: String): String{
        // Encrypt secret text using public key
        val encryptedValue = encryptMessage(secretText, publicKey)
        Log.d(TAG, "Encrypted Value: $encryptedValue")
        return encryptedValue
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