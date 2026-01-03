package com.example.fitlog.data.repository

import com.example.fitlog.domain.repository.BodyweightRepository
import com.example.fitlog.domain.repository.ExerciseRepository
import com.example.fitlog.domain.repository.RoutineRepository
import com.example.fitlog.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        workoutRepositoryImpl: WorkoutRepositoryImpl
    ): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindRoutineRepository(
        routineRepositoryImpl: RoutineRepositoryImpl
    ): RoutineRepository

    @Binds
    @Singleton
    abstract fun bindBodyweightRepository(
        bodyweightRepositoryImpl: BodyweightRepositoryImpl
    ): BodyweightRepository

}

