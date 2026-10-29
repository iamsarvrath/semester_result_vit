# VIT Semester Result

Minimal Spring Boot + MySQL semester-result assignment website.

## Run it with Spring Boot

1. Start MySQL and run `database.sql`.
2. Open `src/main/resources/application.properties` and replace `YOUR_MYSQL_PASSWORD`.
3. Install JDK 17 and Maven.
4. From this folder, run:

   ```text
   mvn spring-boot:run
   ```

5. Open:

   ```text
   http://localhost:8081/
   ```

Spring Boot uses its built-in web server, so external Tomcat is not required.
The frontend is packaged inside the application and sends the request to `api/results`.
Spring Boot calculates the percentage, SGPA, and grade, then stores the result in
the `semester_results` MySQL table.
