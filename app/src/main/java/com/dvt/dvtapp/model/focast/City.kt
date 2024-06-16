package com.dvt.dvtapp.model.focast

import com.dvt.dvtapp.model.current.Coord
import com.google.gson.annotations.SerializedName

data class City(@SerializedName("id"         ) var id         : Int?    = null,
                @SerializedName("name"       ) var name       : String? = null,
                @SerializedName("coord"      ) var coord      : Coord?  = Coord(),
                @SerializedName("country"    ) var country    : String? = null,
                @SerializedName("population" ) var population : Int?    = null,
                @SerializedName("timezone"   ) var timezone   : Int?    = null,
                @SerializedName("sunrise"    ) var sunrise    : Int?    = null,
                @SerializedName("sunset"     ) var sunset     : Int?    = null)
