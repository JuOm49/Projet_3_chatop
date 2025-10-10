# 📘 README — Local Installation Guide for chatop API project


## 1. Clone the repository
    git clone ''



## 2. Create the database with this script:

     CREATE DATABASE chatop_db;
     CREATE USER 'chatop'@'localhost' IDENTIFIED BY 'your_password';
     GRANT ALL PRIVILEGES ON chatop_db.* TO 'chatop';
   
     CREATE TABLE USERS (
     id integer PRIMARY KEY AUTO_INCREMENT,
     email varchar(255),
     name varchar(255),
     password varchar(255),
     created_at timestamp,
     updated_at timestamp
     );

    CREATE TABLE RENTALS (
    id integer PRIMARY KEY AUTO_INCREMENT,
    name varchar(255),
    surface numeric,
    price numeric,
    pictureusers varchar(255),
    description varchar(2000),
    owner_id integer NOT NULL,
    created_at timestamp,
    updated_at timestamp
    );

    CREATE TABLE MESSAGES (
    id integer PRIMARY KEY AUTO_INCREMENT,
    rental_id integer,
    user_id integer,
    message varchar(2000),
    created_at timestamp,
    updated_at timestamp
    );

    CREATE UNIQUE INDEX USERS_index ON USERS ('email');

    ALTER TABLE RENTALS ADD FOREIGN KEY ('owner_id') REFERENCES USERS ('id');

    ALTER TABLE MESSAGES ADD FOREIGN KEY ('user_id') REFERENCES USERS ('id');

    ALTER TABLE MESSAGES ADD FOREIGN KEY ('rental_id') REFERENCES RENTALS ('id');



## 3. Configure external "application.properties"
Create the application.properties file and add the following lines: [...]
Insert your password, this which is used to access the Chatop_db database — and also define a jwt.secret, which will be used by the API to encode and decode authentication tokens. 

    spring.application.name=chatop
    server.port=3001
    spring.datasource.url=jdbc:mysql://localhost:3306/chatop_db
    spring.datasource.username=chatop
    spring.datasource.password=[your password]
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

    jwt.secret=[add_a_jwt_secret_here];

    app.images.base-url=http://localhost:3001/api/images/
    app.images.dir= E:/img-chatop/img



## 4. Run the API
   
    mvn clean install
   
   To launch the application, open the project in your IDE (e.g., IntelliJ) and locate the ChatopApplication class.
   From there, click the green Run button next to the main() method or use the IDE's run menu to execute ChatopApplication. This will start the Spring Boot application locally.
   Edit RUN with this: --spring.config.location=[path_of_application.properties_ex:_D:\JAR\configuration\application.properties]



## 6. Use Swagger
   The Swagger documentation for the API is available at: http://localhost:3001/swagger-ui/index.html.
   You can authenticate using a user token, which must be generated through the API.

