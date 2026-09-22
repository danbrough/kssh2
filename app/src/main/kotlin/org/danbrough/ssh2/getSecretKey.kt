package org.danbrough.ssh2

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val KEY_ALIAS = "MySecurePasswordKey"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"

fun getOrCreateSecretKey(): SecretKey {
  val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

  // If the key already exists, return it
  if (keyStore.containsAlias(KEY_ALIAS)) {
    return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
  }

  // Otherwise, generate a new hardware-backed AES key
  val keyGenerator = KeyGenerator.getInstance(
    KeyProperties.KEY_ALGORITHM_AES,
    ANDROID_KEYSTORE
  )

  val spec = KeyGenParameterSpec.Builder(
    KEY_ALIAS,
    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
  )
    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
    .setKeySize(256) // AES-256
    .build()

  keyGenerator.init(spec)
  return keyGenerator.generateKey()
}


private const val TRANSFORMATION = "AES/GCM/NoPadding"

fun encryptPassword(password: String, keyAlias: String): Pair<ByteArray, ByteArray> {
  // 1. Initialize the Android Keystore
  val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

  // 2. Generate the Secret Key if it doesn't exist yet
  if (!keyStore.containsAlias(keyAlias)) {
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)

    val spec = KeyGenParameterSpec.Builder(
      keyAlias,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).run {
      setBlockModes(KeyProperties.BLOCK_MODE_GCM)
      setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
      // Optional: Uncomment below to enforce hardware-level biometric checks
      // setUserAuthenticationRequired(true)
      build()
    }

    keyGenerator.init(spec)
    keyGenerator.generateKey()
  }

  // 3. Retrieve the key and initialize the Cipher for Encryption
  val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
  val cipher = Cipher.getInstance(TRANSFORMATION).apply {
    init(Cipher.ENCRYPT_MODE, secretKey)
  }

  // 4. Encrypt the password string
  val encryptionBytes = cipher.doFinal(password.toByteArray(Charsets.UTF_8))

  // CRITICAL: You must save BOTH the 'encryptionBytes' AND the 'cipher.iv'
  // to a file or SharedPreferences. You cannot decrypt without the IV.
  return Pair(encryptionBytes, cipher.iv)
}

fun decryptPassword(encryptedBytes: ByteArray, iv: ByteArray): String {
  val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
  val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey

  // GCM requires specifying the tag length (typically 128 bits) alongside the IV
  val spec = GCMParameterSpec(128, iv)
  val cipher = Cipher.getInstance(TRANSFORMATION).apply {
    init(Cipher.DECRYPT_MODE, secretKey, spec)
  }

  val decryptedBytes = cipher.doFinal(encryptedBytes)
  return String(decryptedBytes, Charsets.UTF_8)
}
