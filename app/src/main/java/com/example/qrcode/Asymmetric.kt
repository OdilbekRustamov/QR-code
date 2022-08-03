package com.example.qrcode

import android.os.Build
import androidx.annotation.RequiresApi
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import java.security.spec.X509EncodedKeySpec
import java.io.IOException
import java.security.*
import java.util.*

/**
 * RSA(Rivest-Shamir-Adleman) - enables public-key encryption and is widely used to secure sensitive data,
 * particularly when it is being sent over an insecure network such as the HTTP.
 */
class Asymmetric {
    var privateKey: PrivateKey
    var publicKey: PublicKey

    init {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        val pair = keyGen.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
    }
    companion object {
        // Encrypt using publickey
        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(Exception::class)
        fun encryptMessage(plainText: String, publickey: String): String {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publickey))
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.toByteArray()))
        }
        // Decrypt using privatekey
        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(Exception::class)
        fun decryptMessage(encryptedText: String?, privatekey: String):
                String {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privatekey))
            return String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)))
        }

        // convert String publickey to Key object
        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(GeneralSecurityException::class, IOException::class)
        fun loadPublicKey(stored: String): Key {
            val data: ByteArray = Base64.getDecoder().decode(stored.toByteArray())
            val spec = X509EncodedKeySpec(data)
            val fact = KeyFactory.getInstance("RSA")
            return fact.generatePublic(spec)
        }
        // Convert String private key to privateKey object
        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(GeneralSecurityException::class)
        fun loadPrivateKey(key64: String): PrivateKey {
            val clear: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
            val keySpec = PKCS8EncodedKeySpec(clear)
            val fact = KeyFactory.getInstance("RSA")
            val priv = fact.generatePrivate(keySpec)
            Arrays.fill(clear, 0.toByte())
            return priv
        }
    }


}