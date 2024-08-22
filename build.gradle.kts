import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

object Versions {
    const val jdk = "17"
}

plugins {
    application
    idea
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

allprojects {
    group = "com.example"

    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    tasks {
        // JSR 305チェックを明示的に有効にする
        withType<KotlinCompile>().configureEach {
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-java-parameters")
            kotlinOptions.jvmTarget = Versions.jdk
        }
        withType<Test>().configureEach {
            useJUnitPlatform()
            val javaToolchains = project.extensions.getByType<JavaToolchainService>()
            javaLauncher.set(
                javaToolchains.launcherFor {
                    languageVersion.set(JavaLanguageVersion.of(Versions.jdk.toInt()))
                }
            )
        }

        // 共通部品を入れるプロジェクト以外はBootJarを生成可能にする
        // Jarファイル名: kotlin-gradle-multi-project-[api|batch].jar
        // 実行クラス名: com.example.[api|batch].[Api|Batch]Application
        withType<BootJar>().configureEach {
            if (this.project == rootProject || this.project.name == "common") {
                enabled = false
            } else {
                mainClass.set("${rootProject.group}.${this.project.name}.${this.project.name.capitalize()}Application")
            }
        }
        withType<Jar>().configureEach {
            if (this.project == rootProject) {
                enabled = false
            } else {
                enabled = true
                archiveBaseName.set("${rootProject.name}-${this.project.name}")
            }
        }
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
        implementation("org.springframework.boot:spring-boot-gradle-plugin:2.6.6")

        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        }
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }
}

project(":common") {
    dependencies {
        implementation("org.springframework:spring-web")
    }

    tasks.bootJar {
        enabled = false
    }
    tasks.bootRun {
        enabled = false
    }
    tasks.jar {
        enabled = true
    }

    // commonに作成したテストクラスを共通部品として他のプロジェクトでも取り込みたい場合
    val testCompile by configurations.creating
    configurations.create("testArtifacts") {
        extendsFrom(testCompile)
    }
    tasks.register("testJar", Jar::class.java) {
        archiveClassifier.set("test")
        from(sourceSets["test"].output)
    }
    artifacts {
        add("testArtifacts", tasks.named<Jar>("testJar"))
    }
}

project(":api") {
    dependencies {
        // 共通プロジェクトへの依存を追加
        implementation(project(":common"))
        implementation("org.springframework.boot:spring-boot-starter-web")
    }

    springBoot {
        buildInfo()
    }
}

project(":batch") {
    dependencies {
        // 共通プロジェクトへの依存を追加
        implementation(project(":common"))
        implementation("org.springframework.boot:spring-boot-starter-web")
    }

    springBoot {
        buildInfo()
    }
}