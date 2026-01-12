package com.dating.home.presentation.di

import com.dating.home.presentation.chat_detail.ChatDetailViewModel
import com.dating.home.presentation.chat_list.ChatListViewModel
import com.dating.home.presentation.chat_list_detail.ChatListDetailViewModel
import com.dating.home.presentation.create_chat.CreateChatViewModel
import com.dating.home.presentation.feed.FeedViewModel
import com.dating.home.presentation.manage_chat.ManageChatViewModel
import com.dating.home.presentation.matches.MatchesViewModel
import com.dating.home.presentation.profile.profile_hub.ProfileViewModel
import com.dating.home.presentation.profile.settings.SettingsViewModel
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
}