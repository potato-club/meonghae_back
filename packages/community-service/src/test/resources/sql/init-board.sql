insert into `board` (`id`, `email`, `title`, `content`, `type`, `likes`, `has_image`, `created_date`)
values
    (1, 'test1@example.com', 'Test Title 1', 'Test Content 1', 'SHOW', 1, 0, NOW()),
    (2, 'test2@example.com', 'Test Title 2', 'Test Content 2', 'SHOW', 0, 0, NOW()),
    (3, 'test3@example.com', 'Test Title 3', 'Test Content 3', 'MISSING', 0, 0, NOW());

insert into `board_comments` (`id`, `comment`, `email`, `board_id`, `updated`)
values
    (1, 'Test Comment 1', 'tester@example.com', (SELECT id FROM `board` WHERE `title` = 'Test Title 1'), 0),
    (2, 'Test Comment 2', 'tester2@example.com', (SELECT id FROM `board` WHERE `title` = 'Test Title 1'), 0);

insert into `board_comments` (`id`, `comment`, `email`, `board_id`, `updated`, `parent_id`)
values
    (3, 'Child Comment 1', 'tester2@example.com', (SELECT id FROM `board` WHERE `title` = 'Test Title 1'), 0, 2),
    (4, 'Child Comment 2', 'tester3@example.com', (SELECT id FROM `board` WHERE `title` = 'Test Title 1'), 0, 2);

insert into `board_like` (`id`, `email`, `board_id`, `status`)
values (1, 'tester@example.com', (SELECT id FROM `board` WHERE `title` = 'Test Title 1'), 1);