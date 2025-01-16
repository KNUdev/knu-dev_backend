FROM openjdk:17-alpine AS builder

WORKDIR application

LABEL maintainer = "knudev@knu.ua"
LABEL application.name = "knudev"
LABEL version = "0.0.1"

ARG JAR_FILE=knudev-app/target/*.jar

COPY ${JAR_FILE} application.jar

RUN java -Djarmode=layertools -jar application.jar extract && ls -R application/

FROM openjdk:17-alpine AS runtime

WORKDIR application

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/security-dependencies/ ./
COPY --from=builder application/team-manager-dependencies/ ./
COPY --from=builder application/task-manager-dependencies/ ./
COPY --from=builder application/file-service-dependencies/ ./
COPY --from=builder application/application/ ./

CMD ["java", "org.springframework.boot.loader.launch.JarLauncher"]
