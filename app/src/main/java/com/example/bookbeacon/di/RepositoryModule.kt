package com.example.bookbeacon.di

import com.example.bookbeacon.data.Repos.SessionRepoImpli
import com.example.bookbeacon.data.Repos.SubjectRepoImpli
import com.example.bookbeacon.data.Repos.TaskRepoImpli
import com.example.bookbeacon.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Singleton
    @Binds
    abstract fun subjectRepo(
        impli : SubjectRepoImpli
    ) : SubjectRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepoImpli
    ): TaskRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository(
        impl: SessionRepoImpli
    ): SessionRepository
}