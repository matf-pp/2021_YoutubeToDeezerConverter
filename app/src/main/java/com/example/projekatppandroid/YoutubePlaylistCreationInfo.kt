package com.example.projekatppandroid

import kotlinx.serialization.Serializable

@Serializable
data class YoutubePlaylistCreationInfo(
    val etag: String,
    val id: String,
    val kind: String,
    //val snippet: Snippet,
    //val status: Status
)

data class Snippet(
    val channelId: String,
    val channelTitle: String,
    val description: String,
    val localized: Localized,
    val publishedAt: String,
    val thumbnails: Thumbnails,
    val title: String
)

data class Status(
    val privacyStatus: String
)

data class Localized(
    val description: String,
    val title: String
)

data class Thumbnails(
    val default: Default,
    val high: High,
    val medium: Medium
)

data class Default(
    val height: Int,
    val url: String,
    val width: Int
)

data class High(
    val height: Int,
    val url: String,
    val width: Int
)

data class Medium(
    val height: Int,
    val url: String,
    val width: Int
)