/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.http

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

fun generateSignatureKey(): SecretKey =
    KeyGenerator.getInstance("HMACSHA256").generateKey()
