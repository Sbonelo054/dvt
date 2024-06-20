package com.dvt.dvtapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class FavouriteTable(
    var place: String,
    var minTemp: String,
    var maxTemp: String,
    var description: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}