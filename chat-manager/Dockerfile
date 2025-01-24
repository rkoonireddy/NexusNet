FROM maven:3-amazoncorretto-17 AS build  
COPY ./ /usr/src/app/ 
RUN mvn -B package -DfinalName=chatmanager -DskipTests --file /usr/src/app/pom.xml

FROM amazoncorretto:17
COPY --from=build /usr/src/app/target/chatmanager.jar /usr/app/app.jar  
EXPOSE 8082  
CMD ["java","-jar","/usr/app/app.jar"] 
