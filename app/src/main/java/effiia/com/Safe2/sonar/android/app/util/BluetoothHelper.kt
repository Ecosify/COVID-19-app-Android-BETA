/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.util

import android.bluetooth.BluetoothAdapter

fun isBluetoothEnabled() = BluetoothAdapter.getDefaultAdapter().isEnabled
fun isBluetoothDisabled() = !isBluetoothEnabled()
