/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.edgecases

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_tablet_not_supported.tabletInformationUrl
import effiia.com.Safe2.sonar.android.app.R
import effiia.com.Safe2.sonar.android.app.util.URL_NHS_TABLET_DEVICE
import effiia.com.Safe2.sonar.android.app.util.openUrl

class TabletNotSupportedActivity : AppCompatActivity(R.layout.activity_tablet_not_supported) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tabletInformationUrl.setOnClickListener {
            openUrl(URL_NHS_TABLET_DEVICE)
        }
    }

    companion object {
        fun start(context: Context) = context.startActivity(getIntent(context))

        private fun getIntent(context: Context) = Intent(context, TabletNotSupportedActivity::class.java)
    }
}
