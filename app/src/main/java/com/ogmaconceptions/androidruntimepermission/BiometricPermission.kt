package com.ogmaconceptions.androidruntimepermission

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ogmaconceptions.androidruntimepermission.databinding.ActivityBiometricPermissionBinding
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

import java.util.concurrent.Executor


class BiometricPermission : AppCompatActivity() {
    private lateinit var biometricBinding: ActivityBiometricPermissionBinding
    // create a CancellationSignal variable and assign a value null to it
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private val promptInfo = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric login for my app")
                            .setSubtitle("Log in using your biometric credential")
                            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                            .setConfirmationRequired(true)
                            .build()
    private val PREF_NAME = "biometric-check"
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricBinding = ActivityBiometricPermissionBinding.inflate(layoutInflater)
        setContentView(biometricBinding.root)

        // To build biometric prompt at the time when app is opened

        sharedPref = this.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        val getSharedValue = sharedPref.getInt(PREF_NAME,0)

        checkBiometricSupport()

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {

                    if(getSharedValue == 0) biometricBinding.materialSwitch.isChecked = false
                    Snackbar.make(
                        biometricBinding.constraintLayout,
                        "Authentication error: $errString",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Dismiss") {}
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    biometricBinding.materialSwitch.isChecked = true
                    val editor = sharedPref.edit()
                    editor.putInt(PREF_NAME,1)
                    editor.apply()
                    Snackbar.make(
                        biometricBinding.constraintLayout,
                        "Authentication succeeded!",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Dismiss") {}
                        .show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    if(getSharedValue == 0) biometricBinding.materialSwitch.isChecked = false
                    Snackbar.make(
                        biometricBinding.constraintLayout,
                        "Authentication failed",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Dismiss") {}
                        .show()
                }
            })


        if(getSharedValue == 1){
            biometricBinding.materialSwitch.isChecked = true
            biometricPrompt.authenticate(promptInfo)
        }


        biometricBinding.topAppBar.setNavigationOnClickListener {
            Intent(this,MainActivity::class.java).also {
                startActivity(it)
            }
        }

        biometricBinding.materialSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(getSharedValue == 1){
                biometricBinding.materialSwitch.isChecked = false
                val editor = sharedPref.edit()
                editor.clear()
                editor.apply()
            }else{
                biometricPrompt.authenticate(promptInfo)
            }

        }


    }


    // it checks whether the app the app has fingerprint permission
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isDeviceSecure) {
            Snackbar.make(
                biometricBinding.constraintLayout,
                "Fingerprint authentication has not been enabled in settings",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Dismiss") {}
                .show()
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(
                biometricBinding.constraintLayout,
                "Fingerprint Authentication Permission is not enabled",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Dismiss") {}
                .show()
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else {
            Snackbar.make(
                biometricBinding.constraintLayout,
                "Fingerprint not supported in this device Open using System Lock",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Dismiss") {}
                .show()
            return true
        }
    }
    
}