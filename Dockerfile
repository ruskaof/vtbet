FROM gradle:jdk21 as build
COPY . /sources
WORKDIR /sources
RUN gradle clean build

FROM openjdk:21-jdk as runner
WORKDIR /app
COPY --from=build /sources/vtbet-main/build/libs/* .
CMD java -jar vtbet-main.jar
