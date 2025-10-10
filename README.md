📘 README — Local Installation Guide for chatop API project

1. Clone the repository
    git clone ''

2. Create the database with this script:

  CREATE DATABASE chatop_db;
  CREATE USER 'chatop'@'localhost' IDENTIFIED BY 'your_password';
  GRANT ALL PRIVILEGES ON chatop_db.* TO 'chatop';
   
    CREATE TABLE `USERS` (
   `id` integer PRIMARY KEY AUTO_INCREMENT,
   `email` varchar(255),
   `name` varchar(255),
   `password` varchar(255),
   `created_at` timestamp,
   `updated_at` timestamp
 );

 CREATE TABLE `RENTALS` (
   `id` integer PRIMARY KEY AUTO_INCREMENT,
   `name` varchar(255),
   `surface` numeric,
   `price` numeric,
   `pictureusers` varchar(255),
   `description` varchar(2000),
   `owner_id` integer NOT NULL,
   `created_at` timestamp,
   `updated_at` timestamp
 );

 CREATE TABLE `MESSAGES` (
   `id` integer PRIMARY KEY AUTO_INCREMENT,
   `rental_id` integer,
   `user_id` integer,
   `message` varchar(2000),
   `created_at` timestamp,
   `updated_at` timestamp
 );

 CREATE UNIQUE INDEX `USERS_index` ON `USERS` (`email`);

 ALTER TABLE `RENTALS` ADD FOREIGN KEY (`owner_id`) REFERENCES `USERS` (`id`);

 ALTER TABLE `MESSAGES` ADD FOREIGN KEY (`user_id`) REFERENCES `USERS` (`id`);

 ALTER TABLE `MESSAGES` ADD FOREIGN KEY (`rental_id`) REFERENCES `RENTALS` (`id`);

 3. Configure external "application.properties"
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
  
