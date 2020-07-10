/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.notifications

import effiia.com.Safe2.sonar.android.app.http.HttpClient
import effiia.com.Safe2.sonar.android.app.http.HttpMethod
import effiia.com.Safe2.sonar.android.app.http.HttpRequest
import javax.inject.Inject

class AcknowledgmentsApi @Inject constructor(private val httpClient: HttpClient) {

    fun send(url: String) {
        httpClient.send(HttpRequest(HttpMethod.PUT, url, null))
    }
}
