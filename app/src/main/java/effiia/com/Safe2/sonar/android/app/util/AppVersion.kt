/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.util

import android.content.Context

fun Context.appVersion(): String = packageManager.getPackageInfo(packageName, 0).versionName
