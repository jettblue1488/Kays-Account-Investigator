plugins { id("com.android.application") }
android {
    namespace = "com.kays.accountinvestigator"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.kays.accountinvestigator"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
