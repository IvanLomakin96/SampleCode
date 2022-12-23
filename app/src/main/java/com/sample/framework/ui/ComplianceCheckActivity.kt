package com.sample.framework.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.airwatch.core.compliance.task.*
import com.airwatch.login.SDKBaseActivity
import com.sample.airwatchsdk.R
import kotlinx.android.synthetic.main.activity_compliance_check.*

class ComplianceCheckActivity : SDKBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compliance_check)
    }

    fun onCompromisedDetectedTrueBtnClicked(view: View) {
        /*val compromisedProtectionTask = CompromisedProtectionTask().apply { taskResultForTesting = ComplianceTaskResult(if (isBlockAction()) ComplianceTaskResult.ComplianceAction.UI_BLOCK_ACTION else ComplianceTaskResult.ComplianceAction.WIPE_ACTION_DEVICE_LEVEL, "Device compromised true", null, this.taskName) }
        ComplianceManager.executeTask(compromisedProtectionTask)
        Toast.makeText(this, "Device compromised", Toast.LENGTH_SHORT).show()*/
    }

    fun onCompromisedDetectedFalseBtnClicked(view: View) {
        /*val compromisedProtectionTask = CompromisedProtectionTask().apply { taskResultForTesting = ComplianceTaskResult(ComplianceTaskResult.ComplianceAction.COMPLIANT, "Device compromised false", null, this.taskName) }
        ComplianceManager.executeTask(compromisedProtectionTask)
        Toast.makeText(this, "Device not compromised", Toast.LENGTH_SHORT).show()*/
    }

    fun onOfflineCompliantFalseBtnClicked(view: View) {
        /*val offlineAccessTask = OfflineAccessTask.apply { taskResultForTesting = ComplianceTaskResult(if (isBlockAction()) ComplianceTaskResult.ComplianceAction.UI_BLOCK_ACTION else ComplianceTaskResult.ComplianceAction.WIPE_ACTION_DEVICE_LEVEL, "Device exceeded offline timeout", null, this.taskName) }
        ComplianceManager.executeTask(offlineAccessTask)
        Toast.makeText(this, "Offline policy not compliant", Toast.LENGTH_SHORT).show()*/
    }

    fun onOfflineCompliantTrueBtnClicked(view: View) {
        /*val offlineAccessTask = OfflineAccessTask.apply { taskResultForTesting = ComplianceTaskResult(ComplianceTaskResult.ComplianceAction.COMPLIANT, "Device offline compliant", null, this.taskName) }
        ComplianceManager.executeTask(offlineAccessTask)
        Toast.makeText(this, "Offline policy compliant", Toast.LENGTH_SHORT).show()*/
    }

    fun onOSVersionCompliantFalseBtnClicked(view: View) {
        /*val osVersionTask = OSVersionComplianceTask().apply { taskResultForTesting = ComplianceTaskResult(if (isBlockAction()) ComplianceTaskResult.ComplianceAction.UI_BLOCK_ACTION else ComplianceTaskResult.ComplianceAction.WIPE_ACTION_DEVICE_LEVEL, "OS Version not compliant", null, this.taskName) }
        ComplianceManager.executeTask(osVersionTask)
        Toast.makeText(this, "OS version policy not compliant", Toast.LENGTH_SHORT).show()*/
    }

    fun onOSVersionCompliantTrueBtnClicked(view: View) {
        /*val osVersionTask = OSVersionComplianceTask().apply { taskResultForTesting = ComplianceTaskResult(ComplianceTaskResult.ComplianceAction.COMPLIANT, "OS Version compliant", null, this.taskName) }
        ComplianceManager.executeTask(osVersionTask)
        Toast.makeText(this, "OS version compliant", Toast.LENGTH_SHORT).show()*/
    }

    fun onSecurityPatchCompliantFalseBtnClicked(view: View) {
        /*val securityPatchComplianceTask = SecurityPatchComplianceTask().apply { taskResultForTesting = ComplianceTaskResult(if (isBlockAction()) ComplianceTaskResult.ComplianceAction.UI_BLOCK_ACTION else ComplianceTaskResult.ComplianceAction.WIPE_ACTION_DEVICE_LEVEL, "Security patch not compliant", null, this.taskName) }
        ComplianceManager.executeTask(securityPatchComplianceTask)
        Toast.makeText(this, "Security patch not compliant", Toast.LENGTH_SHORT).show()*/
    }

    fun onSecurityPatchCompliantTrueBtnClicked(view: View) {
        /*val securityPatchComplianceTask = SecurityPatchComplianceTask().apply { taskResultForTesting = ComplianceTaskResult(ComplianceTaskResult.ComplianceAction.COMPLIANT, "Security patch compliant", null, this.taskName) }
        ComplianceManager.executeTask(securityPatchComplianceTask)
        Toast.makeText(this, "Security patch compliant", Toast.LENGTH_SHORT).show()*/
    }

    fun onAppVersionCompliantFalseBtnClicked(view: View) {
        /*val appVersionComplianceTask = AppVersionComplianceTask().apply { taskResultForTesting = ComplianceTaskResult(if (isBlockAction()) ComplianceTaskResult.ComplianceAction.UI_BLOCK_ACTION else ComplianceTaskResult.ComplianceAction.WIPE_ACTION_DEVICE_LEVEL, "App version not compliant", null, this.taskName) }
        ComplianceManager.executeTask(appVersionComplianceTask)
        Toast.makeText(this, "App patch version not compliant", Toast.LENGTH_SHORT).show()*/
    }

    fun onAppVersionCompliantTrueBtnClicked(view: View) {
        /*val appVersionComplianceTask = AppVersionComplianceTask().apply { taskResultForTesting = ComplianceTaskResult(ComplianceTaskResult.ComplianceAction.COMPLIANT, "App version compliant", null, this.taskName) }
        ComplianceManager.executeTask(appVersionComplianceTask)
        Toast.makeText(this, "App patch version compliant", Toast.LENGTH_SHORT).show()*/
    }

    private fun isBlockAction(): Boolean {
        return blockBtn.isChecked
    }
}