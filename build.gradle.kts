plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Logger
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    // Lombok
    implementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")

    // Project Reactor
    implementation("io.projectreactor:reactor-core:3.6.7")
    testImplementation("io.projectreactor:reactor-test:3.6.7")

    // Vavr
    implementation("io.vavr:vavr:0.10.4")

    // JSON con Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

    // Driver para SQLite
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")

    //Hikari
    implementation ("com.zaxxer:HikariCP:6.0.0")

    // MyBatis
    implementation("org.mybatis:mybatis:3.5.13")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.11.0")
    implementation("com.jakewharton.retrofit:retrofit2-reactor-adapter:2.1.0")

    // Dagger
    implementation("com.google.dagger:dagger:2.51.1")
    annotationProcessor("com.google.dagger:dagger-compiler:2.51.1")
    testImplementation("com.google.dagger:dagger:2.51.1")
    testAnnotationProcessor("com.google.dagger:dagger-compiler:2.51.1")

    // Test
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
    testImplementation("org.mockito:mockito-core:5.12.0")

    implementation ("io.vavr:vavr:0.10.3")


}

tasks.test {
    useJUnitPlatform()
}
