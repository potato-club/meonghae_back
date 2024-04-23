CREATE TABLE `users` (
                         `uid` VARCHAR(255) PRIMARY KEY,
                         `email` VARCHAR(255) NOT NULL UNIQUE,
                         `nickname` VARCHAR(255) NOT NULL,
                         `age` INT,
                         `birth` DATE,
                         `user_role` VARCHAR(50),
                         `deleted` BOOLEAN,
                         `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         `modified_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `fcmtoken` (
                         `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                         `email` VARCHAR(255) NOT NULL UNIQUE,
                         `token` VARCHAR(255) NOT NULL,
                         `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         `modified_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

insert into `users` (`uid`, `email`, `nickname`, `age`, `birth`, `user_role`, `deleted`, `created_date`, `modified_date`)
values ('123-456-789', 'test@test.com', 'Test-User', 25, '2000-01-01', 'USER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);