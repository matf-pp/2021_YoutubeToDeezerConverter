package com.example.projekatppandroid

import com.example.projekatppandroid.Creator
import com.example.projekatppandroid.Song
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistForYoutube
(
    var title : String,
    var description : String,
    var allSongs : MutableList<Song>,
    var creator : Creator,
    var status : Boolean
)
{
    fun makeRandomDescription() {
        description = "The playlist " + title + "was made by the user " + creator.name
    }
}