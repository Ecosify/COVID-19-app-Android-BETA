/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package testsupport

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import effiia.com.Safe2.sonar.android.app.appComponent
import effiia.com.Safe2.sonar.android.app.di.ApplicationComponent

fun mockContextWithMockedAppComponent(): Context {
    val context = mockk<Context>()
    val appComponent = mockk<ApplicationComponent>(relaxUnitFun = true)
    every { context.appComponent } returns appComponent
    return context
}
