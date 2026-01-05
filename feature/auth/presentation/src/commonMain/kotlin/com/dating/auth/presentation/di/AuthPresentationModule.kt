package com.dating.auth.presentation.di

import com.dating.auth.presentation.email_verification.EmailVerificationViewModel
import com.dating.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.dating.auth.presentation.login.LoginViewModel
import com.dating.auth.presentation.register.RegisterViewModel
import com.dating.auth.presentation.register_success.RegisterSuccessViewModel
import com.dating.auth.presentation.reset_password.ResetPasswordViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)
}