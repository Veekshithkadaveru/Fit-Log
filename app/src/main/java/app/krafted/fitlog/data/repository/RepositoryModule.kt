package app.krafted.fitlog.data.repository

import app.krafted.fitlog.domain.repository.BodyweightRepository
import app.krafted.fitlog.domain.repository.CardioRepository
import app.krafted.fitlog.domain.repository.ExerciseRepository
import app.krafted.fitlog.domain.repository.PersonalRecordRepository
import app.krafted.fitlog.domain.repository.MuscleAnalyticsRepository
import app.krafted.fitlog.domain.repository.ProgressRepository
import app.krafted.fitlog.domain.repository.RepRangeRecordRepository
import app.krafted.fitlog.domain.repository.RoutineRepository
import app.krafted.fitlog.domain.repository.UserPreferencesRepository
import app.krafted.fitlog.domain.repository.WorkoutRepository
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

    @Binds
    @Singleton
    abstract fun bindCardioRepository(
        cardioRepositoryImpl: CardioRepositoryImpl
    ): CardioRepository

    @Binds
    @Singleton
    abstract fun bindPersonalRecordRepository(
        personalRecordRepositoryImpl: PersonalRecordRepositoryImpl
    ): PersonalRecordRepository

    @Binds
    @Singleton
    abstract fun bindRepRangeRecordRepository(
        repRangeRecordRepositoryImpl: RepRangeRecordRepositoryImpl
    ): RepRangeRecordRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgressRepositoryImpl
    ): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindMuscleAnalyticsRepository(
        muscleAnalyticsRepositoryImpl: MuscleAnalyticsRepositoryImpl
    ): MuscleAnalyticsRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}

