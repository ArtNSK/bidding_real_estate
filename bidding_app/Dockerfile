FROM maven:3.6.3-jdk-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
COPY /lib/bidding_library-1.0.0.jar /usr/src/app/lib/bidding_library-1.0.0.jar
RUN mvn -f /usr/src/app/pom.xml clean package

FROM gcr.io/distroless/java
COPY --from=build /usr/src/app/target/bidding_app-1.0.0.jar /usr/app/bidding_app-1.0.0.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","/usr/app/bidding_app-1.0.0.jar"]