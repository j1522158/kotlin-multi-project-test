plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "kotlin-multi-project-test"
// apiとbatchとcommonの3つのprojectを含める
include("api", "batch", "common")
include("api")
