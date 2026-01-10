package com.example.fitlog.data.database

import com.example.fitlog.data.database.entity.ExerciseEntity
import com.example.fitlog.data.database.entity.RoutineEntity
import com.example.fitlog.data.database.entity.RoutineExerciseEntity
import com.example.fitlog.domain.model.ExerciseCategory
import com.example.fitlog.domain.model.Equipment
import com.example.fitlog.domain.model.MuscleGroup

/**
 * Seed data for exercises in the FitLog app
 * Contains 50+ exercises covering all major muscle groups
 */
object SeedData {
    
    val exercises = listOf(
        
        // CHEST EXERCISES (8)
        ExerciseEntity(
            id = 1,
            name = "Barbell Bench Press",
            primaryMuscle = MuscleGroup.CHEST.name,
            secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.TRICEPS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_barbell_bench_press"
        ),
        ExerciseEntity(
            id = 2,
            name = "Dumbbell Bench Press",
            primaryMuscle = MuscleGroup.CHEST.name,
            secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.TRICEPS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_dumbbell_bench_press"
        ),
        ExerciseEntity(
            id = 3,
            name = "Incline Barbell Press",
            primaryMuscle = MuscleGroup.CHEST.name,
            secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.TRICEPS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_incline_barbell_press"
        ),
        ExerciseEntity(
            id = 4,
            name = "Incline Dumbbell Press",
            primaryMuscle = MuscleGroup.CHEST.name,
            secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.TRICEPS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_incline_dumbbell_press"
        ),
        ExerciseEntity(
            id = 5,
            name = "Dumbbell Flyes",
            primaryMuscle = MuscleGroup.CHEST.name,
            secondaryMuscles = MuscleGroup.SHOULDERS.name,
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_dumbbell_flyes"
        ),
        ExerciseEntity(
            id = 6,
            name = "Cable Chest Flyes",
            primaryMuscle = MuscleGroup.CHEST.name,
            secondaryMuscles = MuscleGroup.SHOULDERS.name,
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.CABLE.name,
            thumbnailRes = "ic_cable_chest_flyes"
        ),
        ExerciseEntity(
            id = 7,
            name = "Push-Ups",
            primaryMuscle = MuscleGroup.CHEST.name,
            secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.TRICEPS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_push_ups"
        ),
        ExerciseEntity(
            id = 8,
            name = "Decline Bench Press",
            primaryMuscle = MuscleGroup.CHEST.name,
            secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.TRICEPS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_decline_bench_press"
        ),
        
        // BACK EXERCISES (10)
        ExerciseEntity(
            id = 9,
            name = "Deadlift",
            primaryMuscle = MuscleGroup.BACK.name,
            secondaryMuscles = "${MuscleGroup.GLUTES.name},${MuscleGroup.HAMSTRINGS.name},${MuscleGroup.TRAPS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_deadlift"
        ),
        ExerciseEntity(
            id = 10,
            name = "Pull-Ups",
            primaryMuscle = MuscleGroup.BACK.name,
            secondaryMuscles = "${MuscleGroup.BICEPS.name},${MuscleGroup.REAR_DELTS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_pull_ups"
        ),
        ExerciseEntity(
            id = 11,
            name = "Barbell Rows",
            primaryMuscle = MuscleGroup.BACK.name,
            secondaryMuscles = "${MuscleGroup.BICEPS.name},${MuscleGroup.REAR_DELTS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_barbell_rows"
        ),
        ExerciseEntity(
            id = 12,
            name = "Dumbbell Rows",
            primaryMuscle = MuscleGroup.BACK.name,
            secondaryMuscles = "${MuscleGroup.BICEPS.name},${MuscleGroup.REAR_DELTS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_dumbbell_rows"
        ),
        ExerciseEntity(
            id = 13,
            name = "Lat Pulldowns",
            primaryMuscle = MuscleGroup.BACK.name,
            secondaryMuscles = "${MuscleGroup.BICEPS.name},${MuscleGroup.REAR_DELTS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.CABLE.name,
            thumbnailRes = "ic_lat_pulldowns"
        ),
        ExerciseEntity(
            id = 14,
            name = "Cable Rows",
            primaryMuscle = MuscleGroup.BACK.name,
            secondaryMuscles = "${MuscleGroup.BICEPS.name},${MuscleGroup.REAR_DELTS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.CABLE.name,
            thumbnailRes = "ic_cable_rows"
        ),
        ExerciseEntity(
            id = 15,
            name = "T-Bar Rows",
            primaryMuscle = MuscleGroup.BACK.name,
            secondaryMuscles = "${MuscleGroup.BICEPS.name},${MuscleGroup.REAR_DELTS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.MACHINE.name,
            thumbnailRes = "ic_tbar_rows"
        ),
        ExerciseEntity(
            id = 16,
            name = "Face Pulls",
            primaryMuscle = MuscleGroup.REAR_DELTS.name,
            secondaryMuscles = "${MuscleGroup.BACK.name},${MuscleGroup.TRAPS.name}",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.CABLE.name,
            thumbnailRes = "ic_face_pulls"
        ),
        ExerciseEntity(
            id = 17,
            name = "Shrugs",
            primaryMuscle = MuscleGroup.TRAPS.name,
            secondaryMuscles = MuscleGroup.BACK.name,
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_shrugs"
        ),
        ExerciseEntity(
            id = 18,
            name = "Chin-Ups",
            primaryMuscle = MuscleGroup.BACK.name,
            secondaryMuscles = MuscleGroup.BICEPS.name,
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_chin_ups"
        ),
        
        // SHOULDERS EXERCISES (8)
        ExerciseEntity(
            id = 19,
            name = "Overhead Press",
            primaryMuscle = MuscleGroup.SHOULDERS.name,
            secondaryMuscles = "${MuscleGroup.TRICEPS.name},${MuscleGroup.CORE.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_overhead_press"
        ),
        ExerciseEntity(
            id = 20,
            name = "Dumbbell Shoulder Press",
            primaryMuscle = MuscleGroup.SHOULDERS.name,
            secondaryMuscles = "${MuscleGroup.TRICEPS.name},${MuscleGroup.CORE.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_dumbbell_shoulder_press"
        ),
        ExerciseEntity(
            id = 21,
            name = "Lateral Raises",
            primaryMuscle = MuscleGroup.SHOULDERS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_lateral_raises"
        ),
        ExerciseEntity(
            id = 22,
            name = "Front Raises",
            primaryMuscle = MuscleGroup.SHOULDERS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_front_raises"
        ),
        ExerciseEntity(
            id = 23,
            name = "Rear Delt Flyes",
            primaryMuscle = MuscleGroup.REAR_DELTS.name,
            secondaryMuscles = MuscleGroup.BACK.name,
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_rear_delt_flyes"
        ),
        ExerciseEntity(
            id = 24,
            name = "Arnold Press",
            primaryMuscle = MuscleGroup.SHOULDERS.name,
            secondaryMuscles = MuscleGroup.TRICEPS.name,
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_arnold_press"
        ),
        ExerciseEntity(
            id = 25,
            name = "Pike Push-Ups",
            primaryMuscle = MuscleGroup.SHOULDERS.name,
            secondaryMuscles = MuscleGroup.TRICEPS.name,
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_pike_push_ups"
        ),
        ExerciseEntity(
            id = 26,
            name = "Upright Rows",
            primaryMuscle = MuscleGroup.SHOULDERS.name,
            secondaryMuscles = MuscleGroup.TRAPS.name,
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_upright_rows"
        ),
        
        // LEGS EXERCISES (12)
        ExerciseEntity(
            id = 27,
            name = "Squats",
            primaryMuscle = MuscleGroup.QUADRICEPS.name,
            secondaryMuscles = "${MuscleGroup.GLUTES.name},${MuscleGroup.HAMSTRINGS.name},${MuscleGroup.CORE.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_squats"
        ),
        ExerciseEntity(
            id = 28,
            name = "Romanian Deadlifts",
            primaryMuscle = MuscleGroup.HAMSTRINGS.name,
            secondaryMuscles = "${MuscleGroup.GLUTES.name},${MuscleGroup.BACK.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_romanian_deadlifts"
        ),
        ExerciseEntity(
            id = 29,
            name = "Bulgarian Split Squats",
            primaryMuscle = MuscleGroup.QUADRICEPS.name,
            secondaryMuscles = "${MuscleGroup.GLUTES.name},${MuscleGroup.HAMSTRINGS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_bulgarian_split_squats"
        ),
        ExerciseEntity(
            id = 30,
            name = "Leg Press",
            primaryMuscle = MuscleGroup.QUADRICEPS.name,
            secondaryMuscles = "${MuscleGroup.GLUTES.name},${MuscleGroup.HAMSTRINGS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.MACHINE.name,
            thumbnailRes = "ic_leg_press"
        ),
        ExerciseEntity(
            id = 31,
            name = "Walking Lunges",
            primaryMuscle = MuscleGroup.QUADRICEPS.name,
            secondaryMuscles = "${MuscleGroup.GLUTES.name},${MuscleGroup.HAMSTRINGS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_walking_lunges"
        ),
        ExerciseEntity(
            id = 32,
            name = "Leg Curls",
            primaryMuscle = MuscleGroup.HAMSTRINGS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.MACHINE.name,
            thumbnailRes = "ic_leg_curls"
        ),
        ExerciseEntity(
            id = 33,
            name = "Leg Extensions",
            primaryMuscle = MuscleGroup.QUADRICEPS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.MACHINE.name,
            thumbnailRes = "ic_leg_extensions"
        ),
        ExerciseEntity(
            id = 34,
            name = "Calf Raises",
            primaryMuscle = MuscleGroup.CALVES.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_calf_raises"
        ),
        ExerciseEntity(
            id = 35,
            name = "Hip Thrusts",
            primaryMuscle = MuscleGroup.GLUTES.name,
            secondaryMuscles = "${MuscleGroup.HAMSTRINGS.name},${MuscleGroup.CORE.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_hip_thrusts"
        ),
        ExerciseEntity(
            id = 36,
            name = "Goblet Squats",
            primaryMuscle = MuscleGroup.QUADRICEPS.name,
            secondaryMuscles = "${MuscleGroup.GLUTES.name},${MuscleGroup.CORE.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_goblet_squats"
        ),
        ExerciseEntity(
            id = 37,
            name = "Step-Ups",
            primaryMuscle = MuscleGroup.QUADRICEPS.name,
            secondaryMuscles = "${MuscleGroup.GLUTES.name},${MuscleGroup.CALVES.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_step_ups"
        ),
        ExerciseEntity(
            id = 38,
            name = "Sumo Deadlifts",
            primaryMuscle = MuscleGroup.GLUTES.name,
            secondaryMuscles = "${MuscleGroup.HAMSTRINGS.name},${MuscleGroup.QUADRICEPS.name},${MuscleGroup.BACK.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_sumo_deadlifts"
        ),
        
        // ARMS EXERCISES (10)
        ExerciseEntity(
            id = 39,
            name = "Barbell Curls",
            primaryMuscle = MuscleGroup.BICEPS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_barbell_curls"
        ),
        ExerciseEntity(
            id = 40,
            name = "Dumbbell Curls",
            primaryMuscle = MuscleGroup.BICEPS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_dumbbell_curls"
        ),
        ExerciseEntity(
            id = 41,
            name = "Hammer Curls",
            primaryMuscle = MuscleGroup.BICEPS.name,
            secondaryMuscles = MuscleGroup.FOREARMS.name,
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_hammer_curls"
        ),
        ExerciseEntity(
            id = 42,
            name = "Close-Grip Bench Press",
            primaryMuscle = MuscleGroup.TRICEPS.name,
            secondaryMuscles = "${MuscleGroup.CHEST.name},${MuscleGroup.SHOULDERS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_close_grip_bench"
        ),
        ExerciseEntity(
            id = 43,
            name = "Tricep Dips",
            primaryMuscle = MuscleGroup.TRICEPS.name,
            secondaryMuscles = "${MuscleGroup.CHEST.name},${MuscleGroup.SHOULDERS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_tricep_dips"
        ),
        ExerciseEntity(
            id = 44,
            name = "Overhead Tricep Extension",
            primaryMuscle = MuscleGroup.TRICEPS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.DUMBBELL.name,
            thumbnailRes = "ic_overhead_tricep_extension"
        ),
        ExerciseEntity(
            id = 45,
            name = "Tricep Pushdowns",
            primaryMuscle = MuscleGroup.TRICEPS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.CABLE.name,
            thumbnailRes = "ic_tricep_pushdowns"
        ),
        ExerciseEntity(
            id = 46,
            name = "Preacher Curls",
            primaryMuscle = MuscleGroup.BICEPS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.BARBELL.name,
            thumbnailRes = "ic_preacher_curls"
        ),
        ExerciseEntity(
            id = 47,
            name = "Cable Bicep Curls",
            primaryMuscle = MuscleGroup.BICEPS.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.CABLE.name,
            thumbnailRes = "ic_cable_bicep_curls"
        ),
        ExerciseEntity(
            id = 48,
            name = "Diamond Push-Ups",
            primaryMuscle = MuscleGroup.TRICEPS.name,
            secondaryMuscles = "${MuscleGroup.CHEST.name},${MuscleGroup.SHOULDERS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_diamond_push_ups"
        ),
        
        // CORE EXERCISES (6)
        ExerciseEntity(
            id = 49,
            name = "Planks",
            primaryMuscle = MuscleGroup.CORE.name,
            secondaryMuscles = MuscleGroup.SHOULDERS.name,
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_planks"
        ),
        ExerciseEntity(
            id = 50,
            name = "Russian Twists",
            primaryMuscle = MuscleGroup.CORE.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_russian_twists"
        ),
        ExerciseEntity(
            id = 51,
            name = "Hanging Leg Raises",
            primaryMuscle = MuscleGroup.CORE.name,
            secondaryMuscles = MuscleGroup.FOREARMS.name,
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_hanging_leg_raises"
        ),
        ExerciseEntity(
            id = 52,
            name = "Mountain Climbers",
            primaryMuscle = MuscleGroup.CORE.name,
            secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.QUADRICEPS.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_mountain_climbers"
        ),
        ExerciseEntity(
            id = 53,
            name = "Dead Bug",
            primaryMuscle = MuscleGroup.CORE.name,
            secondaryMuscles = "",
            category = ExerciseCategory.ISOLATION.name,
            equipment = Equipment.BODYWEIGHT.name,
            thumbnailRes = "ic_dead_bug"
        ),
        ExerciseEntity(
            id = 54,
            name = "Ab Wheel Rollouts",
            primaryMuscle = MuscleGroup.CORE.name,
            secondaryMuscles = "${MuscleGroup.SHOULDERS.name},${MuscleGroup.BACK.name}",
            category = ExerciseCategory.COMPOUND.name,
            equipment = Equipment.OTHER.name,
            thumbnailRes = "ic_ab_wheel_rollouts"
        )
    )

    // ROUTINE TEMPLATES - 6 Professional Workout Programs
    val routineTemplates = listOf(
        
        // Sample User Routine (for demonstration)
        RoutineEntity(id = 100, name = "My Morning Workout", isTemplate = false, dayOrder = 0),

        // Template 1: Push/Pull/Legs (6-day)
        RoutineEntity(id = 1, name = "Push/Pull/Legs - Day 1: Push", isTemplate = true, dayOrder = 1),
        RoutineEntity(id = 2, name = "Push/Pull/Legs - Day 2: Pull", isTemplate = true, dayOrder = 2),
        RoutineEntity(id = 3, name = "Push/Pull/Legs - Day 3: Legs", isTemplate = true, dayOrder = 3),
        RoutineEntity(id = 4, name = "Push/Pull/Legs - Day 4: Push", isTemplate = true, dayOrder = 4),
        RoutineEntity(id = 5, name = "Push/Pull/Legs - Day 5: Pull", isTemplate = true, dayOrder = 5),
        RoutineEntity(id = 6, name = "Push/Pull/Legs - Day 6: Legs", isTemplate = true, dayOrder = 6),
        
        // Template 2: Upper/Lower (4-day)
        RoutineEntity(id = 7, name = "Upper/Lower - Day 1: Upper Body", isTemplate = true, dayOrder = 1),
        RoutineEntity(id = 8, name = "Upper/Lower - Day 2: Lower Body", isTemplate = true, dayOrder = 2),
        RoutineEntity(id = 9, name = "Upper/Lower - Day 3: Upper Body", isTemplate = true, dayOrder = 3),
        RoutineEntity(id = 10, name = "Upper/Lower - Day 4: Lower Body", isTemplate = true, dayOrder = 4),
        
        // Template 3: Bro Split (5-day)
        RoutineEntity(id = 11, name = "Bro Split - Day 1: Chest", isTemplate = true, dayOrder = 1),
        RoutineEntity(id = 12, name = "Bro Split - Day 2: Back", isTemplate = true, dayOrder = 2),
        RoutineEntity(id = 13, name = "Bro Split - Day 3: Shoulders", isTemplate = true, dayOrder = 3),
        RoutineEntity(id = 14, name = "Bro Split - Day 4: Legs", isTemplate = true, dayOrder = 4),
        RoutineEntity(id = 15, name = "Bro Split - Day 5: Arms", isTemplate = true, dayOrder = 5),
        
        // Template 4: PPL + Upper/Lower Hybrid (5-day)
        RoutineEntity(id = 16, name = "PPL Hybrid - Day 1: Push", isTemplate = true, dayOrder = 1),
        RoutineEntity(id = 17, name = "PPL Hybrid - Day 2: Pull", isTemplate = true, dayOrder = 2),
        RoutineEntity(id = 18, name = "PPL Hybrid - Day 3: Legs", isTemplate = true, dayOrder = 3),
        RoutineEntity(id = 19, name = "PPL Hybrid - Day 4: Upper", isTemplate = true, dayOrder = 4),
        RoutineEntity(id = 20, name = "PPL Hybrid - Day 5: Lower", isTemplate = true, dayOrder = 5),
        
        // Template 5: Push-Pull-Cardio Mix (3-day)
        RoutineEntity(id = 21, name = "Push-Pull-Cardio - Day 1: Push", isTemplate = true, dayOrder = 1),
        RoutineEntity(id = 22, name = "Push-Pull-Cardio - Day 2: Pull", isTemplate = true, dayOrder = 2),
        RoutineEntity(id = 23, name = "Push-Pull-Cardio - Day 3: Full Body", isTemplate = true, dayOrder = 3),
        
        // Template 6: Beginner Full-Body (3-day)
        RoutineEntity(id = 24, name = "Beginner Full-Body - Day A", isTemplate = true, dayOrder = 1),
        RoutineEntity(id = 25, name = "Beginner Full-Body - Day B", isTemplate = true, dayOrder = 2),
        RoutineEntity(id = 26, name = "Beginner Full-Body - Day C", isTemplate = true, dayOrder = 3)
    )

    val routineExercises = listOf(
        
        // Sample User Routine (Routine ID: 100)
        RoutineExerciseEntity(routineId = 100, exerciseId = 7, targetSets = 3, targetReps = "10", orderIndex = 0), // Push-Ups
        RoutineExerciseEntity(routineId = 100, exerciseId = 27, targetSets = 3, targetReps = "10", orderIndex = 1), // Squats

        // Template 1: Push/Pull/Legs - Day 1: Push (Routine ID: 1)
        RoutineExerciseEntity(routineId = 1, exerciseId = 1, targetSets = 4, targetReps = "6-8", orderIndex = 0), // Barbell Bench Press
        RoutineExerciseEntity(routineId = 1, exerciseId = 4, targetSets = 3, targetReps = "8-10", orderIndex = 1), // Incline Dumbbell Press
        RoutineExerciseEntity(routineId = 1, exerciseId = 19, targetSets = 4, targetReps = "6-8", orderIndex = 2), // Overhead Press
        RoutineExerciseEntity(routineId = 1, exerciseId = 5, targetSets = 3, targetReps = "10-12", orderIndex = 3), // Dumbbell Flyes
        RoutineExerciseEntity(routineId = 1, exerciseId = 21, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Lateral Raises
        RoutineExerciseEntity(routineId = 1, exerciseId = 43, targetSets = 3, targetReps = "8-12", orderIndex = 5), // Tricep Dips
        
        // Template 1: Push/Pull/Legs - Day 2: Pull (Routine ID: 2)
        RoutineExerciseEntity(routineId = 2, exerciseId = 9, targetSets = 4, targetReps = "5-6", orderIndex = 0), // Deadlift
        RoutineExerciseEntity(routineId = 2, exerciseId = 10, targetSets = 3, targetReps = "8-10", orderIndex = 1), // Pull-Ups
        RoutineExerciseEntity(routineId = 2, exerciseId = 11, targetSets = 4, targetReps = "6-8", orderIndex = 2), // Barbell Rows
        RoutineExerciseEntity(routineId = 2, exerciseId = 13, targetSets = 3, targetReps = "8-10", orderIndex = 3), // Lat Pulldowns
        RoutineExerciseEntity(routineId = 2, exerciseId = 16, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Face Pulls
        RoutineExerciseEntity(routineId = 2, exerciseId = 39, targetSets = 3, targetReps = "10-12", orderIndex = 5), // Barbell Curls
        
        // Template 1: Push/Pull/Legs - Day 3: Legs (Routine ID: 3)
        RoutineExerciseEntity(routineId = 3, exerciseId = 27, targetSets = 4, targetReps = "6-8", orderIndex = 0), // Squats
        RoutineExerciseEntity(routineId = 3, exerciseId = 28, targetSets = 3, targetReps = "8-10", orderIndex = 1), // Romanian Deadlifts
        RoutineExerciseEntity(routineId = 3, exerciseId = 29, targetSets = 3, targetReps = "10-12", orderIndex = 2), // Bulgarian Split Squats
        RoutineExerciseEntity(routineId = 3, exerciseId = 32, targetSets = 3, targetReps = "10-12", orderIndex = 3), // Leg Curls
        RoutineExerciseEntity(routineId = 3, exerciseId = 33, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Leg Extensions
        RoutineExerciseEntity(routineId = 3, exerciseId = 34, targetSets = 3, targetReps = "15-20", orderIndex = 5), // Calf Raises
        
        // Template 1: Push/Pull/Legs - Day 4: Push (Routine ID: 4) - Same as Day 1
        RoutineExerciseEntity(routineId = 4, exerciseId = 2, targetSets = 4, targetReps = "8-10", orderIndex = 0), // Dumbbell Bench Press
        RoutineExerciseEntity(routineId = 4, exerciseId = 3, targetSets = 3, targetReps = "6-8", orderIndex = 1), // Incline Barbell Press
        RoutineExerciseEntity(routineId = 4, exerciseId = 20, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Dumbbell Shoulder Press
        RoutineExerciseEntity(routineId = 4, exerciseId = 6, targetSets = 3, targetReps = "10-12", orderIndex = 3), // Cable Chest Flyes
        RoutineExerciseEntity(routineId = 4, exerciseId = 22, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Front Raises
        RoutineExerciseEntity(routineId = 4, exerciseId = 45, targetSets = 3, targetReps = "10-12", orderIndex = 5), // Tricep Pushdowns
        
        // Template 1: Push/Pull/Legs - Day 5: Pull (Routine ID: 5) - Same as Day 2
        RoutineExerciseEntity(routineId = 5, exerciseId = 18, targetSets = 3, targetReps = "8-10", orderIndex = 0), // Chin-Ups
        RoutineExerciseEntity(routineId = 5, exerciseId = 12, targetSets = 4, targetReps = "8-10", orderIndex = 1), // Dumbbell Rows
        RoutineExerciseEntity(routineId = 5, exerciseId = 14, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Cable Rows
        RoutineExerciseEntity(routineId = 5, exerciseId = 15, targetSets = 3, targetReps = "10-12", orderIndex = 3), // T-Bar Rows
        RoutineExerciseEntity(routineId = 5, exerciseId = 17, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Shrugs
        RoutineExerciseEntity(routineId = 5, exerciseId = 40, targetSets = 3, targetReps = "10-12", orderIndex = 5), // Dumbbell Curls
        
        // Template 1: Push/Pull/Legs - Day 6: Legs (Routine ID: 6) - Same as Day 3
        RoutineExerciseEntity(routineId = 6, exerciseId = 30, targetSets = 4, targetReps = "8-10", orderIndex = 0), // Leg Press
        RoutineExerciseEntity(routineId = 6, exerciseId = 38, targetSets = 3, targetReps = "6-8", orderIndex = 1), // Sumo Deadlifts
        RoutineExerciseEntity(routineId = 6, exerciseId = 31, targetSets = 3, targetReps = "12-15", orderIndex = 2), // Walking Lunges
        RoutineExerciseEntity(routineId = 6, exerciseId = 35, targetSets = 3, targetReps = "10-12", orderIndex = 3), // Hip Thrusts
        RoutineExerciseEntity(routineId = 6, exerciseId = 37, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Step-Ups
        RoutineExerciseEntity(routineId = 6, exerciseId = 34, targetSets = 4, targetReps = "15-20", orderIndex = 5), // Calf Raises
        
        // Template 2: Upper/Lower - Day 1: Upper Body (Routine ID: 7)
        RoutineExerciseEntity(routineId = 7, exerciseId = 1, targetSets = 4, targetReps = "6-8", orderIndex = 0), // Barbell Bench Press
        RoutineExerciseEntity(routineId = 7, exerciseId = 11, targetSets = 4, targetReps = "6-8", orderIndex = 1), // Barbell Rows
        RoutineExerciseEntity(routineId = 7, exerciseId = 19, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Overhead Press
        RoutineExerciseEntity(routineId = 7, exerciseId = 13, targetSets = 3, targetReps = "8-10", orderIndex = 3), // Lat Pulldowns
        RoutineExerciseEntity(routineId = 7, exerciseId = 5, targetSets = 3, targetReps = "10-12", orderIndex = 4), // Dumbbell Flyes
        RoutineExerciseEntity(routineId = 7, exerciseId = 39, targetSets = 3, targetReps = "10-12", orderIndex = 5), // Barbell Curls
        RoutineExerciseEntity(routineId = 7, exerciseId = 43, targetSets = 3, targetReps = "8-12", orderIndex = 6), // Tricep Dips
        
        // Template 2: Upper/Lower - Day 2: Lower Body (Routine ID: 8)
        RoutineExerciseEntity(routineId = 8, exerciseId = 27, targetSets = 4, targetReps = "6-8", orderIndex = 0), // Squats
        RoutineExerciseEntity(routineId = 8, exerciseId = 28, targetSets = 4, targetReps = "6-8", orderIndex = 1), // Romanian Deadlifts
        RoutineExerciseEntity(routineId = 8, exerciseId = 29, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Bulgarian Split Squats
        RoutineExerciseEntity(routineId = 8, exerciseId = 32, targetSets = 3, targetReps = "10-12", orderIndex = 3), // Leg Curls
        RoutineExerciseEntity(routineId = 8, exerciseId = 33, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Leg Extensions
        RoutineExerciseEntity(routineId = 8, exerciseId = 35, targetSets = 3, targetReps = "10-12", orderIndex = 5), // Hip Thrusts
        RoutineExerciseEntity(routineId = 8, exerciseId = 34, targetSets = 4, targetReps = "15-20", orderIndex = 6), // Calf Raises
        
        // Template 2: Upper/Lower - Day 3: Upper Body (Routine ID: 9) - Variation
        RoutineExerciseEntity(routineId = 9, exerciseId = 2, targetSets = 4, targetReps = "8-10", orderIndex = 0), // Dumbbell Bench Press
        RoutineExerciseEntity(routineId = 9, exerciseId = 12, targetSets = 4, targetReps = "8-10", orderIndex = 1), // Dumbbell Rows
        RoutineExerciseEntity(routineId = 9, exerciseId = 20, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Dumbbell Shoulder Press
        RoutineExerciseEntity(routineId = 9, exerciseId = 10, targetSets = 3, targetReps = "8-10", orderIndex = 3), // Pull-Ups
        RoutineExerciseEntity(routineId = 9, exerciseId = 21, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Lateral Raises
        RoutineExerciseEntity(routineId = 9, exerciseId = 40, targetSets = 3, targetReps = "10-12", orderIndex = 5), // Dumbbell Curls
        RoutineExerciseEntity(routineId = 9, exerciseId = 45, targetSets = 3, targetReps = "10-12", orderIndex = 6), // Tricep Pushdowns
        
        // Template 2: Upper/Lower - Day 4: Lower Body (Routine ID: 10) - Variation
        RoutineExerciseEntity(routineId = 10, exerciseId = 9, targetSets = 4, targetReps = "5-6", orderIndex = 0), // Deadlift
        RoutineExerciseEntity(routineId = 10, exerciseId = 36, targetSets = 3, targetReps = "8-10", orderIndex = 1), // Goblet Squats
        RoutineExerciseEntity(routineId = 10, exerciseId = 31, targetSets = 3, targetReps = "10-12", orderIndex = 2), // Walking Lunges
        RoutineExerciseEntity(routineId = 10, exerciseId = 30, targetSets = 3, targetReps = "10-12", orderIndex = 3), // Leg Press
        RoutineExerciseEntity(routineId = 10, exerciseId = 37, targetSets = 3, targetReps = "12-15", orderIndex = 4), // Step-Ups
        RoutineExerciseEntity(routineId = 10, exerciseId = 38, targetSets = 3, targetReps = "8-10", orderIndex = 5), // Sumo Deadlifts
        RoutineExerciseEntity(routineId = 10, exerciseId = 34, targetSets = 3, targetReps = "15-20", orderIndex = 6), // Calf Raises
        
        // Template 6: Beginner Full-Body - Day A (Routine ID: 24)
        RoutineExerciseEntity(routineId = 24, exerciseId = 27, targetSets = 3, targetReps = "8-10", orderIndex = 0), // Squats
        RoutineExerciseEntity(routineId = 24, exerciseId = 1, targetSets = 3, targetReps = "8-10", orderIndex = 1), // Barbell Bench Press
        RoutineExerciseEntity(routineId = 24, exerciseId = 11, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Barbell Rows
        RoutineExerciseEntity(routineId = 24, exerciseId = 20, targetSets = 3, targetReps = "8-10", orderIndex = 3), // Dumbbell Shoulder Press
        RoutineExerciseEntity(routineId = 24, exerciseId = 49, targetSets = 3, targetReps = "30-60s", orderIndex = 4), // Planks
        
        // Template 6: Beginner Full-Body - Day B (Routine ID: 25)
        RoutineExerciseEntity(routineId = 25, exerciseId = 9, targetSets = 3, targetReps = "5-6", orderIndex = 0), // Deadlift
        RoutineExerciseEntity(routineId = 25, exerciseId = 4, targetSets = 3, targetReps = "8-10", orderIndex = 1), // Incline Dumbbell Press
        RoutineExerciseEntity(routineId = 25, exerciseId = 13, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Lat Pulldowns
        RoutineExerciseEntity(routineId = 25, exerciseId = 31, targetSets = 3, targetReps = "10-12", orderIndex = 3), // Walking Lunges
        RoutineExerciseEntity(routineId = 25, exerciseId = 39, targetSets = 3, targetReps = "10-12", orderIndex = 4), // Barbell Curls
        RoutineExerciseEntity(routineId = 25, exerciseId = 43, targetSets = 3, targetReps = "8-10", orderIndex = 5), // Tricep Dips
        
        // Template 6: Beginner Full-Body - Day C (Routine ID: 26)
        RoutineExerciseEntity(routineId = 26, exerciseId = 36, targetSets = 3, targetReps = "8-10", orderIndex = 0), // Goblet Squats
        RoutineExerciseEntity(routineId = 26, exerciseId = 7, targetSets = 3, targetReps = "8-12", orderIndex = 1), // Push-Ups
        RoutineExerciseEntity(routineId = 26, exerciseId = 12, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Dumbbell Rows
        RoutineExerciseEntity(routineId = 26, exerciseId = 28, targetSets = 3, targetReps = "8-10", orderIndex = 3), // Romanian Deadlifts
        RoutineExerciseEntity(routineId = 26, exerciseId = 40, targetSets = 3, targetReps = "10-12", orderIndex = 4), // Dumbbell Curls
        RoutineExerciseEntity(routineId = 26, exerciseId = 44, targetSets = 3, targetReps = "8-10", orderIndex = 5), // Overhead Tricep Extension
        RoutineExerciseEntity(routineId = 26, exerciseId = 50, targetSets = 3, targetReps = "15-20", orderIndex = 6), // Russian Twists

        // Template 3: Bro Split - Day 1: Chest (Routine ID: 11)
        RoutineExerciseEntity(routineId = 11, exerciseId = 1, targetSets = 4, targetReps = "8-10", orderIndex = 0), // Barbell Bench Press
        RoutineExerciseEntity(routineId = 11, exerciseId = 4, targetSets = 3, targetReps = "8-10", orderIndex = 1), // Incline Dumbbell Press
        RoutineExerciseEntity(routineId = 11, exerciseId = 6, targetSets = 3, targetReps = "10-12", orderIndex = 2), // Cable Chest Flyes
        
         // Template 3: Bro Split - Day 2: Back (Routine ID: 12)
        RoutineExerciseEntity(routineId = 12, exerciseId = 9, targetSets = 4, targetReps = "5", orderIndex = 0), // Deadlift
        RoutineExerciseEntity(routineId = 12, exerciseId = 10, targetSets = 3, targetReps = "MAX", orderIndex = 1), // Pull-Ups
        RoutineExerciseEntity(routineId = 12, exerciseId = 11, targetSets = 3, targetReps = "8-10", orderIndex = 2), // Barbell Rows

        // Template 3: Bro Split - Day 3: Shoulders (Routine ID: 13)
        RoutineExerciseEntity(routineId = 13, exerciseId = 19, targetSets = 4, targetReps = "8-10", orderIndex = 0), // Overhead Press
        RoutineExerciseEntity(routineId = 13, exerciseId = 21, targetSets = 4, targetReps = "12-15", orderIndex = 1), // Lateral Raises
        RoutineExerciseEntity(routineId = 13, exerciseId = 16, targetSets = 3, targetReps = "12-15", orderIndex = 2), // Face Pulls

        // Template 3: Bro Split - Day 4: Legs (Routine ID: 14)
        RoutineExerciseEntity(routineId = 14, exerciseId = 27, targetSets = 4, targetReps = "6-8", orderIndex = 0), // Squats
        RoutineExerciseEntity(routineId = 14, exerciseId = 30, targetSets = 3, targetReps = "10-12", orderIndex = 1), // Leg Press
        RoutineExerciseEntity(routineId = 14, exerciseId = 32, targetSets = 3, targetReps = "12-15", orderIndex = 2), // Leg Curls

        // Template 3: Bro Split - Day 5: Arms (Routine ID: 15)
        RoutineExerciseEntity(routineId = 15, exerciseId = 39, targetSets = 3, targetReps = "10-12", orderIndex = 0), // Barbell Curls
        RoutineExerciseEntity(routineId = 15, exerciseId = 42, targetSets = 3, targetReps = "8-10", orderIndex = 1), // Close-Grip Bench
        RoutineExerciseEntity(routineId = 15, exerciseId = 41, targetSets = 3, targetReps = "10-12", orderIndex = 2), // Hammer Curls

         // Template 4: PPL Hybrid - Day 1: Push (Routine ID: 16)
        RoutineExerciseEntity(routineId = 16, exerciseId = 1, targetSets = 3, targetReps = "8", orderIndex = 0),
        
         // Template 4: PPL Hybrid - Day 2: Pull (Routine ID: 17)
        RoutineExerciseEntity(routineId = 17, exerciseId = 11, targetSets = 3, targetReps = "8", orderIndex = 0),

         // Template 4: PPL Hybrid - Day 3: Legs (Routine ID: 18)
        RoutineExerciseEntity(routineId = 18, exerciseId = 27, targetSets = 3, targetReps = "8", orderIndex = 0),

         // Template 4: PPL Hybrid - Day 4: Upper (Routine ID: 19)
        RoutineExerciseEntity(routineId = 19, exerciseId = 19, targetSets = 3, targetReps = "10", orderIndex = 0),

         // Template 4: PPL Hybrid - Day 5: Lower (Routine ID: 20)
        RoutineExerciseEntity(routineId = 20, exerciseId = 28, targetSets = 3, targetReps = "10", orderIndex = 0),

        // Template 5: Push-Pull-Cardio - Day 1: Push (Routine ID: 21)
        RoutineExerciseEntity(routineId = 21, exerciseId = 7, targetSets = 3, targetReps = "MAX", orderIndex = 0),

        // Template 5: Push-Pull-Cardio - Day 2: Pull (Routine ID: 22)
        RoutineExerciseEntity(routineId = 22, exerciseId = 10, targetSets = 3, targetReps = "MAX", orderIndex = 0),

        // Template 5: Push-Pull-Cardio - Day 3: Full Body (Routine ID: 23)
        RoutineExerciseEntity(routineId = 23, exerciseId = 27, targetSets = 3, targetReps = "10", orderIndex = 0)
    )
}