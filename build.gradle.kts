// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false // Kotlin 버전 업데이트
}

// 이 블록은 루트 프로젝트에만 적용됩니다
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}