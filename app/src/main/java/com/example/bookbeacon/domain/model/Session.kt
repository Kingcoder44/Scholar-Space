package com.example.bookbeacon.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
    val sessionSubjectId: Int,
    val relatedToSubject : String,
    val date : Long,
    val duration : Long,
    @PrimaryKey
    val sessionId : Int?=null
)