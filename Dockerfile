FROM amazoncorretto:11-alpine-jdk

WORKDIR /opt/app
ARG SPRING_ACTIVE_PROFILE
ENV SPRING_ACTIVE_PROFILE=${SPRING_ACTIVE_PROFILE}
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
#RUN ./mvnw install

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
