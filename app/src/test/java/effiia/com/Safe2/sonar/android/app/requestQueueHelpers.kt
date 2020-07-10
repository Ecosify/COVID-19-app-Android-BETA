/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app

import com.android.volley.ExecutorDelivery
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.NoCache
import effiia.com.Safe2.sonar.android.app.diagnose.OkHttpStack
import java.util.concurrent.Executors

fun testQueue(): RequestQueue =
    RequestQueue(
        NoCache(),
        BasicNetwork(OkHttpStack()),
        1,
        ExecutorDelivery(Executors.newSingleThreadExecutor())
    ).apply { start() }
