plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlinAndroid)
    `maven-publish`
}

android {
    namespace = "dev.widebars.strings"
    compileSdk = libs.versions.app.build.compileSDKVersion.get().toInt()

    publishing {
        singleVariant("release") {}
    }
}

publishing.publications {
    create<MavenPublication>("release") {
        afterEvaluate {
            from(components["release"])
        }
    }
}
