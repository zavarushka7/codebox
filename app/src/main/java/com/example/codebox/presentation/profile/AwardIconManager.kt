package com.example.codebox.presentation.profile

import com.example.codebox.R

object AwardIconManager{
private val iconMap = mapOf(
//    "award_hater" to R.drawable.award_hater
//    "award_lover" to R.drawable.award_lover
//    "award_count" to R.drawable.award_count
//    "award_graphomaniac" to R.drawable.award_graphomaniac
//    "award_likes_received" to R.drawable.award_likes_received
//    "award_likes_given" to R.drawable.award_likes_given
//    "award_polyglot" to R.drawable.award_polyglot
        "award_unusual"  to R.drawable.award_unusual



    )
    fun getIconRes(iconKey: String): Int? {
        return iconMap[iconKey]
    }
}
