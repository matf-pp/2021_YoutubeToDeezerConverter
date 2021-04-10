package com.example.projekatppandroid

import kotlinx.serialization.Serializable

@Serializable
data class AllPlaylists(
    val checksum: String,
    val `data`: List<PlaylistInfo>,
    val total: String
)

@Serializable
data class PlaylistInfo(
    val checksum: String,
    val collaborative: Boolean,
    val creation_date: String,
    val creator: Creator,
    val duration: String,
    val fans: String,
    val id: String,
    val is_loved_track: Boolean,
    val link: String,
    val md5_image: String,
    val nb_tracks: String,
    val picture: String,
    val picture_big: String,
    val picture_medium: String,
    val picture_small: String,
    val picture_type: String,
    val picture_xl: String,
    val `public`: Boolean,
    val time_add: String,
    val time_mod: String,
    val title: String,
    val tracklist: String,
    val type: String
)

@Serializable
data class Creator(
    val id: String,
    val name: String,
    val tracklist: String,
    val type: String
)

@Serializable
data class Playlist(
    val checksum: String,
    val data: List<Song>,
    var next: String? = null,
    val total: String
)

// TODO: getteri i setteri (možda ne?)

@Serializable
data class Song(
    val album: Album,
    val artist: Artist,
    val duration: String,
    val explicit_content_cover: String,
    val explicit_content_lyrics: String,
    val explicit_lyrics: Boolean,
    val id: String,
    val link: String,
    val md5_image: String,
    val preview: String,
    val rank: String,
    val readable: Boolean,
    val time_add: String,
    val title: String,
    val title_short: String,
    val title_version: String,
    val type: String
)

@Serializable
data class Album(
    val cover: String,
    val cover_big: String,
    val cover_medium: String,
    val cover_small: String,
    val cover_xl: String,
    val id: String,
    val md5_image: String,
    val title: String,
    val tracklist: String,
    val type: String
)

@Serializable
data class Artist(
    val id: String,
    val link: String,
    val name: String,
    val picture: String,
    val picture_big: String,
    val picture_medium: String,
    val picture_small: String,
    val picture_xl: String,
    val tracklist: String,
    val type: String
)
