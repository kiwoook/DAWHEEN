FROM openjdk:17-alpine AS builder
LABEL authors="ki wook"

COPY . /tmp
WORKDIR /tmp

RUN sed -i 's/\r$//' ./gradlew
RUN ./gradlew build

FROM openjdk:17-alpine
COPY --from=builder /tmp/build/libs/*.jar ./

CMD ["java", "-jar", "dawheen.jar"]