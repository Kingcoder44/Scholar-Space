package com.example.bookbeacon.ui.presentation.session

import com.example.bookbeacon.domain.model.Session
import com.example.bookbeacon.domain.model.Subject

sealed class SessionEvent {
        data class OnRelatedSubjectChange(val subject: Subject) : SessionEvent()
        data class SaveSession(val duration: Long) : SessionEvent()
        data class OnDeleteSessionButtonClick(val session: Session) : SessionEvent()
        data object DeleteSession : SessionEvent()
        data object NotifyToUpdateSubject : SessionEvent()
        data class UpdateSubjectIdAndRelatedSubject(
            val subjectId: Int?,
            val relatedToSubject: String?
        ) : SessionEvent()
}