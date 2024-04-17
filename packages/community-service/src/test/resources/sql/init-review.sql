insert into `review` (`id`, `title`, `content`, `email`, `has_image`, `rating`, `catalog`,
                      `likes`, `dislikes`, `created_date`)
values
    (1, 'Test Review1', 'Test Content1', 'test1@example.com', true, 5, 'COLLAR', 3, 0, NOW()),
    (2, 'Test Review2', 'Test Content2', 'test2@example.com', false, 1, 'COLLAR', 0, 3, NOW()),
    (3, 'Test Review3', 'Test Content3', 'test3@example.com', true, 4, 'COLLAR', 2, 1, NOW());

insert into `review_reaction` (`id`, `email`, `review_id`, `recommend_status`)
values
    (1, 'tester1@example.com', 1, true),
    (2, 'tester2@example.com', 1, true),
    (3, 'tester3@example.com', 1, true),
    (4, 'tester1@example.com', 2, false),
    (5, 'tester2@example.com', 2, false),
    (6, 'tester3@example.com', 2, false),
    (7, 'tester1@example.com', 3, true),
    (8, 'tester2@example.com', 3, false),
    (9, 'tester3@example.com', 3, true);