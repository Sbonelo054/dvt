package com.dvt.dvtapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class FavouriteTable(
    @ColumnInfo(name = "place") val place: String,
    @ColumnInfo(name = "min_temp") val minTemp: String,
    @ColumnInfo(name = "max_temp") var maxTemp: String,
    var description: String?){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}