/*
 * Copyright © 2020 NHSX. All rights reserved.
 *
 */

package uk.nhs.nhsx.sonar.android.client.http.volley

import com.android.volley.Request
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.Test
import uk.nhs.nhsx.sonar.android.client.http.HttpRequest

class VolleyHttpClientShould {

    private val queue = TestQueue()
    private val url = "http://localhost:123"
    private val httpClient = VolleyHttpClient(url, queue)

    @Test
    fun addJsonRequestToQueueWhenPost() {
        val inputRequest = HttpRequest("/api", JSONObject())

        httpClient.post(inputRequest, {}, {})

        assertThat(queue.requests).hasSize(1)

        val request = queue.requests.first()
        assertThat(request.method).isEqualTo(Request.Method.POST)
        assertThat(request.url).isEqualTo("$url/api")
        assertThat(request.body).isEqualTo("{}".toByteArray())
        assertThat(request.bodyContentType).contains("application/json")
        assertThat(request.headers).containsEntry("Accept", "application/json")
    }

    @Test
    fun addJsonRequestToQueueWhenPatch() {
        val inputRequest = HttpRequest("/api", JSONObject(), generateSignatureKey())

        httpClient.patch(inputRequest, {}, {})

        assertThat(queue.requests).hasSize(1)

        val request = queue.requests.first()
        assertThat(request.method).isEqualTo(Request.Method.PATCH)
        assertThat(request.url).isEqualTo("$url/api")
        assertThat(request.body).isEqualTo("{}".toByteArray())
        assertThat(request.bodyContentType).contains("application/json")
        assertThat(request.headers).containsEntry("Accept", "application/json")
    }
}
