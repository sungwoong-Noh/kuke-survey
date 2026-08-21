dependencies {
    implementation(project(":survey-core"))
    implementation(project(":survey-infra"))
    implementation(project(":survey-common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    // OpenAPI 3.0 / Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    runtimeOnly("com.h2database:h2")
}