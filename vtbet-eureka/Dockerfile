FROM gradle:jdk21 as build
COPY . /sources
WORKDIR /sources
RUN gradle clean bootJar

FROM openjdk:21-jdk as runner
WORKDIR /app
COPY --from=build /sources/build/libs/* .
CMD java -jar vtbet-eureka.jar
