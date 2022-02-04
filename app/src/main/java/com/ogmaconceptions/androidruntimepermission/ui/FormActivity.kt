package com.ogmaconceptions.androidruntimepermission.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.ogmaconceptions.androidruntimepermission.R
import com.ogmaconceptions.androidruntimepermission.databinding.ActivityFormBinding
import com.ogmaconceptions.androidruntimepermission.utils.BaseActivity
import com.ogmaconceptions.androidruntimepermission.utils.FieldConstants
import com.ogmaconceptions.androidruntimepermission.utils.TextInputLayoutUtils
import com.ogmaconceptions.androidruntimepermission.utils.Validation

class FormActivity : BaseActivity() {
    private lateinit var formBinding: ActivityFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        formBinding = ActivityFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = formBinding.root
        setContentView(view)

        TextInputLayoutUtils.resetTextInputErrorsOnTextChanged(view)

        formBinding.btnSave.setOnClickListener {

            if (checkValidation()) {
                Snackbar.make(
                    formBinding.constraintLayout,
                    this.resources.getString(R.string.validationSucess),
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        }

    }

    private fun checkValidation(): Boolean {
        var flag = true

        val fName = Validation.checkValidation(
            this,
            formBinding.eTxtFirstName.text.toString(),
            formBinding.layoutFirstName,
            R.string.firstNameValidation,
        )
        if (!fName.isValidated) flag = false

        val lName = Validation.checkValidation(
            this,
            formBinding.eTxtLastName.text.toString(),
            formBinding.layoutLastName,
            R.string.lastNameValidation,
        )
        if (!lName.isValidated) flag = false

        val phone = Validation.checkValidation(
            this,
            formBinding.eTxtPhone.text.toString(),
            formBinding.layoutPhone,
            R.string.phoneNumberEmptyValidation,
            FieldConstants.PHONE,
            R.string.phoneNumberValidation
        )
        if (!phone.isValidated) flag = false

        val email = Validation.checkValidation(
            this,
            formBinding.eTxtEmail.text.toString(),
            formBinding.layoutEmail,
            R.string.emailBlankValidation,
            FieldConstants.EMAIL,
            R.string.emailFormatValidation
        )
        if (!email.isValidated) flag = false

        val address = Validation.checkValidation(
            this,
            formBinding.eTxtAddress.text.toString(),
            formBinding.layoutAddress,
            R.string.addressValidation,
        )
        if (!address.isValidated) flag = false

        val password = Validation.checkValidation(
            this,
            formBinding.eTxtPassword.text.toString(),
            formBinding.layoutPassword,
            R.string.passwordBlankValidation,
            FieldConstants.PASSWORD,
            R.string.passwordFormatValidation
        )
        if (!password.isValidated) flag = false

        val confirmPassword = Validation.checkValidation(
            this,
            formBinding.eTxtConfirmPassword.text.toString(),
            formBinding.layoutConfirmPassword,
            R.string.passwordBlankValidation,
            FieldConstants.CONFIRM_PASSWORD,
            R.string.passwordFormatValidation,
            formBinding.eTxtPassword.text.toString(),
            R.string.passwordMatchingValidation
        )
        if (!confirmPassword.isValidated) flag = false

        return flag
    }
}