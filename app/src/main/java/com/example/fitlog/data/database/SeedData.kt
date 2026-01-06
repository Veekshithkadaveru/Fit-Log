package com.example.fitlog.data.database

import com.example.fitlog.data.database.entity.ExerciseEntity
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
}