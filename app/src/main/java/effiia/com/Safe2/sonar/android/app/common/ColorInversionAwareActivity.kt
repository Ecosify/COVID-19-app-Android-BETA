/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.common

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import effiia.com.Safe2.sonar.android.app.util.isInversionModeEnabled

abstract class ColorInversionAwareActivity : AppCompatActivity {

    constructor() : super()

    constructor(@LayoutRes layoutId: Int) : super(layoutId)

    override fun onResume() {
        super.onResume()
        handleInversion(isInversionModeEnabled())
    }

    open fun handleInversion(inversionModeEnabled: Boolean) {
    }
}
