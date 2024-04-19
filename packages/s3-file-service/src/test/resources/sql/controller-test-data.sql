CREATE TABLE `files` (
                         `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                         `file_name` VARCHAR(255) NOT NULL,
                         `file_url` VARCHAR(255) NOT NULL,
                         `entity_type` VARCHAR(50),
                         `type_id` BIGINT,
                         `email` VARCHAR(255),
                         `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         `modified_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

insert into `files` (`id`, `file_name`, `file_url`, `entity_type`, `type_id`, `email`, `created_date`, `modified_date`)
values (null, 'test1.png', 'https://s3.ap-northeast-2.amazonaws.com/test-s3/image/test1.png', 'BOARD', 1L, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);