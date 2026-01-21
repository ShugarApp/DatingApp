package com.dating.home.presentation.di

import com.dating.home.presentation.chat.chat_detail.ChatDetailViewModel
import com.dating.home.presentation.chat.chat_list.ChatListViewModel
import com.dating.home.presentation.chat.chat_list_detail.ChatListDetailViewModel
import com.dating.home.presentation.chat.create_chat.CreateChatViewModel
import com.dating.home.presentation.chat.manage_chat.ManageChatViewModel
import com.dating.home.presentation.home.swipe.FeedViewModel
import com.dating.home.presentation.matches.MatchesViewModel
import com.dating.home.presentation.profile.edit_profile.EditProfileViewModel
import com.dating.home.presentation.profile.profile.ProfileViewModel
import com.dating.home.presentation.profile.settings.SettingsViewModel
import com.dating.home.presentation.profile.settings.changepassword.ChangePasswordViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homePresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::ManageChatViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::FeedViewModel)
    viewModelOf(::MatchesViewModel)
    viewModelOf(::ChangePasswordViewModel)
    viewModelOf(::EditProfileViewModel)
}
