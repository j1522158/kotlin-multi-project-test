
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.util.*

object Versions {
    const val JDK: String = "17"
}

plugins {
    application
    idea
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

allprojects {
    group = "com.example"

    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    // TODO 非推奨
    tasks {
        // JSR 305チェックを明示的に有効にする
        withType<KotlinCompile>().configureEach {
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-java-parameters")
            kotlinOptions.jvmTarget = Versions.JDK
        }
        withType<Test>().configureEach {
            useJUnitPlatform()
            val javaToolchains = project.extensions.getByType<JavaToolchainService>()
            javaLauncher.set(
                javaToolchains.launcherFor {
                    languageVersion.set(JavaLanguageVersion.of(Versions.JDK.toInt()))
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
                mainClass.set("${rootProject.group}.${this.project.name}.${this.project.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }}Application")
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
        implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.0")

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