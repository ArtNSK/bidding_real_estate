FROM maven:3.6.3-jdk-11 AS build
COPY src /usr/src/bot/src
COPY pom.xml /usr/src/bot
COPY /lib/bidding_library-1.0.0.jar /usr/src/bot/lib/bidding_library-1.0.0.jar
RUN mvn -f /usr/src/bot/pom.xml clean package

FROM gcr.io/distroless/java
COPY --from=build /usr/src/bot/target/bidding_telegram_bot-1.0.0.jar /usr/bot/bidding_telegram_bot-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/bot/bidding_telegram_bot-1.0.0.jar"]