tasks.bootJar {enabled = false}
tasks.jar {enabled = true}

// survey-common/build.gradle.kts
dependencies {
    // Jackson 어노테이션 의존성 (버전은 스프링 부트가 자동 관리하므로 생략 가능)
    implementation("com.fasterxml.jackson.core:jackson-annotations")
}