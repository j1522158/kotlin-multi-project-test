
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
        withType<KotlinCompile>().configureEach {
            compilerOptions {
                // JSR 305チェックを明示的に有効にする
                freeCompilerArgs.addAll("-Xjsr305=strict", "-java-parameters")
                jvmTarget.set(JvmTarget.fromTarget(Versions.JDK))
            }
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

        implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
        compileOnly("org.projectlombok:lombok")
        developmentOnly("org.springframework.boot:spring-boot-devtools")
        runtimeOnly("com.mysql:mysql-connector-j")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.3")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")

        // Swagger SpringFoxはSpring3系には未対応
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
        // Okio
        implementation("com.squareup.okio:okio:3.4.0")
        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        // Arrow
        // implementation("io.arrow-kt:arrow-core")

        // KotlinクラスをJSONにシリアライズ/デシリアライズするためのサポート機能
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        // KotsetのJUnit5ランナー
        testImplementation("io.kotest:kotest-runner-junit5:5.4.2")
        testImplementation("io.kotest:kotest-assertions-core:5.4.2")
        testImplementation("io.kotest:kotest-property:5.4.2")

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