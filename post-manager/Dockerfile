FROM maven:3-amazoncorretto-17 AS build  
COPY ./ /usr/src/app/ 
RUN mvn -B package -DfinalName=postmanager -DskipTests --file /usr/src/app/pom.xml

FROM amazoncorretto:17
COPY --from=build /usr/src/app/target/postmanager.jar /usr/app/app.jar  
EXPOSE 8081  
CMD ["java","-jar","/usr/app/app.jar"] 
