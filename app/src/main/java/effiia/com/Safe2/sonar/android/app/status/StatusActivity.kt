/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.status


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.card.MaterialCardView
import effiia.com.Safe2.sonar.android.app.R
import effiia.com.Safe2.sonar.android.app.appComponent
import effiia.com.Safe2.sonar.android.app.ble.BluetoothService
import effiia.com.Safe2.sonar.android.app.common.BaseActivity
import effiia.com.Safe2.sonar.android.app.common.ViewModelFactory
import effiia.com.Safe2.sonar.android.app.diagnose.DiagnoseTemperatureActivity
import effiia.com.Safe2.sonar.android.app.inbox.UserInbox
import effiia.com.Safe2.sonar.android.app.interstitials.CurrentAdviceActivity
import effiia.com.Safe2.sonar.android.app.interstitials.WorkplaceGuidanceActivity
import effiia.com.Safe2.sonar.android.app.notifications.CheckInReminderNotification
import effiia.com.Safe2.sonar.android.app.notifications.ExpiredExposedReminderNotification
import effiia.com.Safe2.sonar.android.app.notifications.ExposedNotification
import effiia.com.Safe2.sonar.android.app.notifications.TestResultNotification
import effiia.com.Safe2.sonar.android.app.referencecode.ReferenceCodeActivity
import effiia.com.Safe2.sonar.android.app.registration.SonarIdProvider
import effiia.com.Safe2.sonar.android.app.status.widgets.*
import effiia.com.Safe2.sonar.android.app.util.*
import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.android.synthetic.main.banner.*
import kotlinx.android.synthetic.main.status_footer_view.*
import javax.inject.Inject

class StatusActivity : BaseActivity() {


    private lateinit var statusLayout: StatusLayout
    private var previousState: UserState? = null

    @Inject
    lateinit var userStateMachine: UserStateMachine

    @Inject
    lateinit var userInbox: UserInbox

    @Inject
    lateinit var checkInReminderNotification: CheckInReminderNotification

    @Inject
    lateinit var expiredExposedReminderNotification: ExpiredExposedReminderNotification

    @Inject
    lateinit var exposedNotification: ExposedNotification

    @Inject
    lateinit var testResultNotification: TestResultNotification

    @Inject
    lateinit var sonarIdProvider: SonarIdProvider

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<StatusViewModel>
    private val viewModel: StatusViewModel by viewModels { viewModelFactory }

    /** Used in default state */
    internal lateinit var recoveryDialog: BottomDialog
    /** Used in SymptomaticState, ExposedSymptomaticState and PositiveState */
    internal lateinit var checkinReminderDialog: BottomDialog
    /** Used in all states */
    internal lateinit var testResultDialog: BottomDialog

    var autoScrollEnabled=true;

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {

            when (intent?.action) {
                "BROADCAST_DISPLAY_LOG" -> addBluetoothLog(intent.getStringExtra("log"))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        val autoScrollButton = findViewById(R.id.autoScrollButton) as MaterialCardView
        val autoScrollText = findViewById(R.id.autoScrollText) as TextView
        autoScrollButton.setOnClickListener {
           if(autoScrollEnabled){
               autoScrollText.setText("Auto scroll disabled")
               autoScrollEnabled=false;
           }else{
               autoScrollText.setText("Auto scroll enabled")
               autoScrollEnabled=true;
           }
        }


        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(broadCastReceiver, IntentFilter("BROADCAST_DISPLAY_LOG"))


        if (sonarIdProvider.hasProperSonarId()) {
            BluetoothService.start(this)
        }

        registrationPanel.setState(RegistrationState.Complete)

        refreshState()

        readLatestAdvice.setOnClickListener {
            CurrentAdviceActivity.start(this)
        }

        feelUnwell.setOnClickListener {
            DiagnoseTemperatureActivity.start(this)
        }

        nhsServiceFooter.setOnClickListener {
            openUrl(URL_NHS_LOCAL_SUPPORT)
        }

        toolbar_info.setOnClickListener {
            openUrl(URL_INFO)
        }

        workplace_guidance_card.setOnClickListener {
            WorkplaceGuidanceActivity.start(this)
        }

        reference_link_card.setOnClickListener {
            ReferenceCodeActivity.start(this)
        }

        notificationPanel.setOnClickListener {
            openAppSettings()
        }

        recoveryDialog = createRecoveryDialog()
        checkinReminderDialog = createCheckinReminderDialog()
        testResultDialog = createTestResultDialog(this, userInbox)

        // TODO: maybe move this check into view model?
        val userState = userStateMachine.state()
        if (userState is DefaultState) {
            toggleReferenceCodeCard(this, false)
            toggleNotFeelingCard(this, false)
            addViewModelListener()
            viewModel.onStart()
        }

        notificationPanel.readOutAccessibilityHeading()
    }

    private fun addBluetoothLog(log: String) {
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 8f
        tv_dynamic.setTextColor(Color.parseColor("#ffffff"))
        tv_dynamic.text = log
        scrollLinearLayout.addView(tv_dynamic)

        if(autoScrollEnabled){
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

    }

    /**
     * Note: this is only active for DefaultState
     */
    private fun addViewModelListener() {
        viewModel.viewState().observe(this) { result ->
            when (result) {
                RegistrationState.Complete -> {
                    registrationPanel.setState(result)
                    BluetoothService.start(this)
                    toggleNotFeelingCard(this, true)
                    toggleReferenceCodeCard(this, true)
                }
                RegistrationState.InProgress -> {
                    registrationPanel.setState(result)
                    toggleNotFeelingCard(this, false)
                    toggleReferenceCodeCard(this, false)
                }
            }
        }
    }

    private fun createRecoveryDialog(): BottomDialog {
        val configuration = BottomDialogConfiguration(
            isHideable = false,
            titleResId = R.string.recovery_dialog_title,
            textResId = R.string.recovery_dialog_description,
            secondCtaResId = R.string.close
        )
        return BottomDialog(this, configuration,
            onCancel = {
                userInbox.dismissRecovery()
                finish()
            },
            onSecondCtaClick = {
                userInbox.dismissRecovery()
            })
    }

    private fun createCheckinReminderDialog(): BottomDialog {
        val configuration = BottomDialogConfiguration(
            isHideable = false,
            titleResId = R.string.status_today_feeling,
            textResId = R.string.update_symptoms_prompt,
            firstCtaResId = R.string.update_my_symptoms,
            secondCtaResId = R.string.no_symptoms
        )
        return BottomDialog(
            this, configuration,
            onCancel = {
                finish()
            },
            onFirstCtaClick = {
                DiagnoseTemperatureActivity.start(this)
            },
            onSecondCtaClick = {
                userStateMachine.reset()
                refreshState()
            }
        )
    }

    override fun onResume() {
        super.onResume()

        refreshState()

        statusLayout.onResume(this)

        notificationPanel.isVisible =
            !notificationManagerHelper.areNotificationsEnabled()
    }

    override fun handleInversion(inversionModeEnabled: Boolean) {
        notificationPanel.cardColourInversion(inversionModeEnabled)

        readLatestAdvice.cardColourInversion(inversionModeEnabled)
        bookTest.cardColourInversion(inversionModeEnabled)
        feelUnwell.cardColourInversion(inversionModeEnabled)

        workplace_guidance_card.cardColourInversion(inversionModeEnabled)
        reference_link_card.cardColourInversion(inversionModeEnabled)
    }

    override fun onPause() {
        super.onPause()
        testResultDialog.dismiss()
        checkinReminderDialog.dismiss()
        recoveryDialog.dismiss()
    }

    fun refreshState() {
        val currentState = userStateMachine.state()
        if (previousState != currentState) {
            statusLayout = StatusLayoutFactory.from(currentState)
            statusLayout.refreshStatusLayout(this)
            previousState = currentState
        }
    }

    companion object {
        fun start(context: Context) =
            context.startActivity(getIntent(context))

        fun getIntent(context: Context) =
            Intent(context, StatusActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
    }
}
