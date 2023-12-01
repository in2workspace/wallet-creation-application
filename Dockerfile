# temp build
FROM docker.io/gradle:8.4.0 AS TEMP_BUILD
ARG SKIP_TESTS=false
# Copy project files
COPY build.gradle settings.gradle /home/gradle/src/
COPY src /home/gradle/src/src
COPY gradle /home/gradle/src/gradle
COPY config/checkstyle /home/gradle/src/config/checkstyle
COPY waltid/configs /home/gradle/src/waltid/configs
COPY service-matrix.properties /home/gradle/src/
WORKDIR /home/gradle/src
RUN if [ "$SKIP_TESTS" = "true" ]; then \
    gradle build --no-daemon -x test; \
  else \
    gradle build --no-daemon; \
  fi

# build image
FROM eclipse-temurin:17.0.8.1_1-jre-jammy
WORKDIR /app
COPY --from=TEMP_BUILD /home/gradle/src/build/libs/*.jar /app/
COPY --from=TEMP_BUILD /home/gradle/src/service-matrix.properties /app/
COPY --from=TEMP_BUILD /home/gradle/src/waltid/configs /app/waltid/configs
ENTRYPOINT ["java", "-jar", "/app/wallet-creation-application-0.0.1.jar"]