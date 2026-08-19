tasks.bootJar {enabled = false}
tasks.jar {enabled = true}

dependencies {
    implementation(project(":survey-common"))
    implementation(project(":survey-core"))
}
