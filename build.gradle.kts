// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0-RC1" apply false
    kotlin("kapt") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" // this version matches your Kotlin version

}