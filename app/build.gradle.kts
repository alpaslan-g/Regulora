plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.regulora"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.regulora"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core Android & Kotlin
    implementation("androidx.core:core-ktx:1.12.0") // Recommended to add/update core-ktx
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2") // Already have this

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01")) // Recommended: Use Compose BOM
    implementation("androidx.compose.material:material-icons-core:1.6.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3") // For Compose Material 3 components

    // Material Components for Android (for XML themes like Theme.Material3.*)
    implementation("com.google.android.material:material:1.11.0") // Or latest version

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.6.0") // Already have this

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1") // Already have this

    // MQTT
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

    // JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Android Test Dependencies (example)
    // testImplementation("junit:junit:4.13.2")
    // androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    // androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug Dependencies (example)
    // debugImplementation("androidx.compose.ui:ui-tooling")
    // debugImplementation("androidx.compose.ui:ui-test-manifest")
}
