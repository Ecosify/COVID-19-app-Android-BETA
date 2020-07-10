/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.status

import android.app.Activity

fun Activity.startStatusActivity() {
    StatusActivity.start(this)
    finish()
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}
