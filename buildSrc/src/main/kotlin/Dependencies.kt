object PluginIds { // please keep this sorted in sections
    // Kotlin
    const val Kotlin = "kotlin"
    const val KotlinSpring = "plugin.spring"
    const val Kapt = "kapt"

    // 3rd Party
    const val Flyway = "org.flywaydb.flyway"
    const val Idea = "idea"
    const val TaskTree = "com.dorongold.task-tree"
    const val TestLogger = "com.adarshr.test-logger"
    const val DependencyAnalysis = "com.autonomousapps.dependency-analysis"
    const val GoryLenkoGitProps = "com.gorylenko.gradle-git-properties"

    const val SpringDependency = "io.spring.dependency-management"
    const val SpringBoot = "org.springframework.boot"
    const val Protobuf = "com.google.protobuf"
    const val Grpc = "grpc"
    const val GrpcKt = "grpckt"

    // Provenance
    const val ProvenanceDownloadProtos = "io.provenance.download-protos"
}

object PluginVersions { // please keep this sorted in sections
    // Kotlin
    const val Kotlin = "1.5.30"

    // 3rd Party
    const val Flyway = "7.15.0"
    const val TaskTree = "1.5"
    const val TestLogger = "2.1.1"
    const val DependencyAnalysis = "0.56.0"
    const val GoryLenkoGitProps = "1.5.2"

    const val SpringDependency = "1.0.11.RELEASE"
    const val SpringBoot = "2.4.3"
    const val Protobuf = "0.8.17"
}

object Versions {
    // kotlin
    const val Kotlin = PluginVersions.Kotlin
    const val KotlinXCoroutines = "1.5.2"

    // 3rd Party
    const val ApacheCommonsText = "1.9"
    const val ApacheHttpClient = "4.5.12"
    const val BouncyCastle = "1.69"
    const val Exposed = "0.34.1"
    const val Flyway = PluginVersions.Flyway
    const val Jackson = "2.11.2"
    const val JacksonProtobuf = "0.9.12"
    const val Json = "20211205"
    const val KaseChange = "1.3.0"
    const val Ktor = "1.6.7"
    const val Logback = "0.1.5"
    const val SpringBoot = PluginVersions.SpringBoot
    const val Swagger = "3.0.0"
    const val Protobuf = "3.19.1"
    const val Grpc = "1.40.1"
    const val KotlinGrpc = "1.2.0"
    const val Postgres = "42.2.23"

    // Testing
    const val Jupiter = "5.7.1"
    const val SpringMockk = "3.0.1"
    const val Kotest = "4.4.3"

    // external protos
    const val Provenance = "v1.7.5"
    const val Cosmos = "v0.44.3"
    const val Wasmd = "v0.19.0"
    const val Ibc = "v1.1.0"
}

object Libraries {
    // Kotlin
    const val KotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.Kotlin}"
    const val KotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Kotlin}"
    const val KotlinXCoRoutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KotlinXCoroutines}"
    const val KotlinXCoRoutinesGuava = "org.jetbrains.kotlinx:kotlinx-coroutines-guava:${Versions.KotlinXCoroutines}"

    // 3rd Party
    const val Exposed = "org.jetbrains.exposed:exposed-core:${Versions.Exposed}"
    const val ExposedDao = "org.jetbrains.exposed:exposed-dao:${Versions.Exposed}"
    const val ExposedJavaTime = "org.jetbrains.exposed:exposed-jodatime:${Versions.Exposed}"
    const val ExposedJdbc = "org.jetbrains.exposed:exposed-jdbc:${Versions.Exposed}"
    const val FlywayCore = "org.flywaydb:flyway-core:${Versions.Flyway}"
    const val JacksonModuleKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.Jackson}"
    const val JacksonDatatype = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.Jackson}"
    const val JacksonProtobuf = "com.hubspot.jackson:jackson-datatype-protobuf:${Versions.JacksonProtobuf}"
    const val Postgres = "org.postgresql:postgresql:${Versions.Postgres}"
    const val BouncyCastle = "org.bouncycastle:bcprov-jdk15on:${Versions.BouncyCastle}"
    const val ApacheCommonsText = "org.apache.commons:commons-text:${Versions.ApacheCommonsText}"
    const val ApacheHttpClient = "org.apache.httpcomponents:httpclient:${Versions.ApacheHttpClient}"
    const val KtorClientCore = "io.ktor:ktor-client-core:${Versions.Ktor}"
    const val KtorClientEngine = "io.ktor:ktor-client-cio:${Versions.Ktor}"
    const val KaseChange = "net.pearx.kasechange:kasechange:${Versions.KaseChange}"
    const val Json = "org.json:json:${Versions.Json}"

    // Logging
    const val LogbackCore = "ch.qos.logback.contrib:logback-json-core:${Versions.Logback}"
    const val LogbackClassic = "ch.qos.logback.contrib:logback-json-classic:${Versions.Logback}"
    const val LogbackJackson = "ch.qos.logback.contrib:logback-jackson:${Versions.Logback}"

    // Protobuf
    const val ProtobufJavaUtil = "com.google.protobuf:protobuf-java-util:${Versions.Protobuf}"
    const val ProtobufKotlin = "com.google.protobuf:protobuf-kotlin:${Versions.Protobuf}"
    const val GrpcProtobuf = "io.grpc:grpc-protobuf:${Versions.Grpc}"
    const val GrpcStub = "io.grpc:grpc-stub:${Versions.Grpc}"
    const val GrpcKotlinStub = "io.grpc:grpc-kotlin-stub:${Versions.KotlinGrpc}"
    const val ProtocArtifact = "com.google.protobuf:protoc:${Versions.Protobuf}"
    const val GrpcArtifact = "io.grpc:protoc-gen-grpc-java:${Versions.Grpc}"
    const val GrpcKotlinArtifact = "io.grpc:protoc-gen-grpc-kotlin:${Versions.KotlinGrpc}:jdk7@jar"
    const val GrpcNetty = "io.grpc:grpc-netty:${Versions.Grpc}"

    // Spring
    const val SpringBootDevTools = "org.springframework.boot:spring-boot-devtools:${Versions.SpringBoot}"
    const val SpringBootStarterActuator = "org.springframework.boot:spring-boot-starter-actuator:${Versions.SpringBoot}"
    const val SpringBootStarterJdbc = "org.springframework.boot:spring-boot-starter-jdbc:${Versions.SpringBoot}"
    const val SpringBootConfigProcessor = "org.springframework.boot:spring-boot-configuration-processor:${Versions.SpringBoot}"
    const val SpringBootStarterValidation = "org.springframework.boot:spring-boot-starter-validation:${Versions.SpringBoot}"
    const val SpringBootStarterWeb = "org.springframework.boot:spring-boot-starter-web:${Versions.SpringBoot}"
    const val SpringBootStarterTest = "org.springframework.boot:spring-boot-starter-test:${Versions.SpringBoot}"

    const val Swagger = "io.springfox:springfox-boot-starter:${Versions.Swagger}"

    // Testing
    const val JunitJupiterApi = "org.junit.jupiter:junit-jupiter-api:${Versions.Jupiter}"
    const val JunitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.Jupiter}"
    const val SpringMockk = "com.ninja-squad:springmockk:${Versions.SpringMockk}"
    const val KotestAssert = "io.kotest:kotest-assertions-core:${Versions.Kotest}"
}

// gradle configurations
const val kapt = "kapt"
const val api = "api"
const val implementation = "implementation"
const val testCompileOnly = "testCompileOnly"
const val testImplementation = "testImplementation"
const val testRuntimeOnly = "testRuntimeOnly"
const val developmentOnly = "developmentOnly"
