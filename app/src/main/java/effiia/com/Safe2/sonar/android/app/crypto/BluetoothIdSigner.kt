/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.crypto

import effiia.com.Safe2.sonar.android.app.http.SecretKeyStorage
import javax.crypto.Mac
import javax.inject.Inject

class BluetoothIdSigner @Inject constructor(private val secretKeyStorage: SecretKeyStorage) {
    fun computeHmacSignature(
        countryCode: ByteArray,
        cryptogram: ByteArray,
        txPowerLevel: Byte,
        transmissionTimeBytes: ByteArray
    ): ByteArray {
        val message = countryCode + cryptogram + txPowerLevel + transmissionTimeBytes
        val mac: Mac = Mac.getInstance("HMACSHA256")
        val secretKey = secretKeyStorage.provideSecretKey()!!
        mac.init(secretKey)
        mac.update(message)
        return mac.doFinal().sliceArray(0 until 16)
    }

    fun canSign() = secretKeyStorage.provideSecretKey() != null
}
