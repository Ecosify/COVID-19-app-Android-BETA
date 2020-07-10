/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.registration

import org.json.JSONObject
import effiia.com.Safe2.sonar.android.app.functionaltypes.Promise
import effiia.com.Safe2.sonar.android.app.http.HttpClient
import effiia.com.Safe2.sonar.android.app.http.HttpMethod.POST
import effiia.com.Safe2.sonar.android.app.http.HttpRequest
import effiia.com.Safe2.sonar.android.app.http.KeyStorage
import effiia.com.Safe2.sonar.android.app.http.jsonObjectOf
import javax.inject.Inject

class ResidentApi @Inject constructor(
    private val baseUrl: String,
    private val keyStorage: KeyStorage,
    private val httpClient: HttpClient
) {

    fun register(token: String): Promise<Unit> {
        val requestJson = jsonObjectOf("pushToken" to token)
        val request = HttpRequest(
            POST,
            "$baseUrl/api/devices/registrations",
            requestJson
        )

        return httpClient.send(request).mapToUnit()
    }

    fun confirmDevice(deviceConfirmation: DeviceConfirmation): Promise<Registration> {
        val requestJson = jsonObjectOf(
            "activationCode" to deviceConfirmation.activationCode,
            "pushToken" to deviceConfirmation.pushToken,
            "deviceModel" to deviceConfirmation.deviceModel,
            "deviceOSVersion" to deviceConfirmation.deviceOsVersion,
            "postalCode" to deviceConfirmation.postalCode
        )
        val request = HttpRequest(POST, "$baseUrl/api/devices", requestJson)

        return httpClient
            .send(request)
            .map { json: JSONObject ->
                val key = json.getString("secretKey")
                val publicKey = json.getString("publicKey")
                val registrationId = json.getString("id")
                keyStorage.storeServerPublicKey(publicKey)
                keyStorage.storeSecretKey(key)
                Registration(registrationId)
            }
    }
}

data class Registration(val id: String)

data class DeviceConfirmation(
    val activationCode: String,
    val pushToken: String,
    val deviceModel: String,
    val deviceOsVersion: String,
    val postalCode: String
)
