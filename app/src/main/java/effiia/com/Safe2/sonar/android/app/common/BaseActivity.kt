/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.common

import android.bluetooth.BluetoothAdapter.STATE_OFF
import android.os.Bundle
import android.view.View
import io.reactivex.disposables.Disposable
import effiia.com.Safe2.sonar.android.app.BuildConfig
import effiia.com.Safe2.sonar.android.app.R
import effiia.com.Safe2.sonar.android.app.appComponent
import effiia.com.Safe2.sonar.android.app.debug.TesterActivity
import effiia.com.Safe2.sonar.android.app.edgecases.ReAllowGrantLocationPermissionActivity
import effiia.com.Safe2.sonar.android.app.edgecases.ReEnableBluetoothActivity
import effiia.com.Safe2.sonar.android.app.edgecases.ReEnableLocationActivity
import effiia.com.Safe2.sonar.android.app.receivers.BluetoothStateBroadcastReceiver
import effiia.com.Safe2.sonar.android.app.receivers.LocationProviderChangedReceiver
import effiia.com.Safe2.sonar.android.app.util.LocationHelper
import effiia.com.Safe2.sonar.android.app.util.NotificationManagerHelper
import effiia.com.Safe2.sonar.android.app.util.isBluetoothDisabled
import javax.inject.Inject

abstract class BaseActivity : ColorInversionAwareActivity() {

    private var locationSubscription: Disposable? = null

    @Inject
    lateinit var locationHelper: LocationHelper

    @Inject
    lateinit var notificationManagerHelper: NotificationManagerHelper

    private lateinit var locationProviderChangedReceiver: LocationProviderChangedReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        locationProviderChangedReceiver = LocationProviderChangedReceiver(locationHelper)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        when (BuildConfig.BUILD_TYPE) {
            "internal", "debug" -> {
                findViewById<View>(R.id.logo)
                    ?.setOnClickListener { TesterActivity.start(this) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        listenBluetoothChange()
        checkLocationPermission()
        listenLocationChange()
    }

    private fun listenBluetoothChange() {
        if (isBluetoothDisabled()) {
            ReEnableBluetoothActivity.start(this)
        }
        bluetoothStateBroadcastReceiver.register(this)
    }

    private fun checkLocationPermission() {
        if (!locationHelper.locationPermissionsGranted()) {
            ReAllowGrantLocationPermissionActivity.start(this)
        }
    }

    private fun listenLocationChange() {
        locationProviderChangedReceiver.register(this)

        locationSubscription =
            locationProviderChangedReceiver.getLocationStatus().subscribe { isLocationEnabled ->
                if (!isLocationEnabled) {
                    ReEnableLocationActivity.start(this)
                }
            }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(bluetoothStateBroadcastReceiver)
        unregisterReceiver(locationProviderChangedReceiver)
        locationSubscription?.dispose()
    }

    private val bluetoothStateBroadcastReceiver =
        BluetoothStateBroadcastReceiver { state ->
            if (state == STATE_OFF) {
                ReEnableBluetoothActivity.start(this)
            }
        }
}
