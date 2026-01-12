package com.dating.chat.presentation.di

import com.dating.chat.presentation.chat_detail.ChatDetailViewModel
import com.dating.chat.presentation.chat_list.ChatListViewModel
import com.dating.chat.presentation.chat_list_detail.ChatListDetailViewModel
import com.dating.chat.presentation.create_chat.CreateChatViewModel
import com.dating.chat.presentation.feed.FeedViewModel
import com.dating.chat.presentation.manage_chat.ManageChatViewModel
import com.dating.chat.presentation.matches.MatchesViewModel
import com.dating.chat.presentation.profile.ProfileViewModel
import com.dating.chat.presentation.profile.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
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