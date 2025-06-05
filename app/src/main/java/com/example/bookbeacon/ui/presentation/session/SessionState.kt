package com.example.bookbeacon.ui.presentation.session

import com.example.bookbeacon.domain.model.Session
import com.example.bookbeacon.domain.model.Subject

data class SessionState(
    val subjects: List<Subject> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val relatedToSubject: String? = null,
    val subjectId: Int? = null,
    val session: Session? = null
)