plugins {
    java
    id("org.springframework.boot") version "3.3.2" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}


allprojects {

    group = "com.kuke"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }

        dependencies {
            implementation("org.springframework.boot:spring-boot-starter")
            testImplementation("org.springframework.boot:spring-boot-starter-test")
            testRuntimeOnly("org.junit.platform:junit-platform-launcher")

            compileOnly("org.projectlombok:lombok")
            annotationProcessor("org.projectlombok:lombok")
            testCompileOnly("org.projectlombok:lombok")
            testAnnotationProcessor("org.projectlombok:lombok")

        }


    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}


