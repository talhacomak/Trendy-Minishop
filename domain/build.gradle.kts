plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.trendy.domain"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }

    testFixtures {
        enable = true
    }
    sourceSets {
        getByName("testFixtures") {
            java.srcDirs(
                "src/testFixtures/kotlin",
                "src/testFixtures/java"
            )
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.test.core)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.test.core)

    testFixturesImplementation(libs.junit)
    testFixturesImplementation(libs.coroutines.test)
    testFixturesImplementation(libs.truth)
    testFixturesImplementation(libs.mockito.kotlin)
    testFixturesImplementation(libs.androidx.test.core)
}
