package com.example.trama.State

import com.example.trama.Data.Model.Review
import com.example.trama.Data.Model.User

data class UserState(
    val userSearchResults: List<User> = emptyList(),
    val isSearchingUsers: Boolean = false,
    val followingFeed: List<Review> = emptyList(),
    val isLoadingFeed: Boolean = false
)