tasks.bootJar {enabled = false}
tasks.jar {enabled = true}

dependencies {
    implementation(project(":survey-common"))

    // JPA 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}