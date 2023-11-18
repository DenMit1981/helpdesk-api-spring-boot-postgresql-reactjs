INSERT INTO users (id, first_name, last_name, role, email, password) VALUES (1, 'Den', 'Mit', 'ROLE_EMPLOYEE', 'user1_mogilev@yopmail.com', '$2a$10$BtSb1vIX7synMgLIKximAeNZlpTAae4kZlZMR9xx7wxXZK2s0B4dC'),(2, 'Peter', 'Bubu', 'ROLE_EMPLOYEE', 'user2_mogilev@yopmail.com', '$2a$10$BtSb1vIX7synMgLIKximAeNZlpTAae4kZlZMR9xx7wxXZK2s0B4dC'),(3, 'Asya', 'Asyna', 'ROLE_MANAGER', 'manager1_mogilev@yopmail.com', '$2a$10$BtSb1vIX7synMgLIKximAeNZlpTAae4kZlZMR9xx7wxXZK2s0B4dC'),(4, 'Ivan', 'Ivanov', 'ROLE_MANAGER', 'manager2_mogilev@yopmail.com', '$2a$10$BtSb1vIX7synMgLIKximAeNZlpTAae4kZlZMR9xx7wxXZK2s0B4dC'),(5, 'Inna', 'Inina', 'ROLE_ENGINEER', 'engineer1_mogilev@yopmail.com', '$2a$10$BtSb1vIX7synMgLIKximAeNZlpTAae4kZlZMR9xx7wxXZK2s0B4dC'),(6, 'Roman', 'Romin', 'ROLE_ENGINEER', 'engineer2_mogilev@yopmail.com', '$2a$10$BtSb1vIX7synMgLIKximAeNZlpTAae4kZlZMR9xx7wxXZK2s0B4dC');
SELECT setval('user_id_seq', (SELECT MAX(id) from users));

INSERT INTO ticket (id, name, description, created_on, desired_resolution_date, category, status, urgency) VALUES (1, 'task 1', 'this is a ticket1', '2022-02-12', '2022-03-12', 'APPLICATION_AND_SERVICES', 'NEW', 'CRITICAL'),(2, 'ticket 2', 'Yes', '2022-01-14', '2022-02-14', 'BENEFITS_AND_PAPER_WORK', 'DONE', 'HIGH'), (3, 'hard_task', 'Ygggfgfgfg', '2022-02-15', '2022-03-15', 'HARDWARE_AND_SOFTWARE', 'IN_PROGRESS', 'LOW'), (4, 'new task', 'AAAAAAA!', '2022-03-15', '2022-04-15', 'PEOPLE_MANAGEMENT', 'DONE', 'AVERAGE'), (5, 'ticket 5', 'No interesting', '2022-03-12', '2022-04-12', 'SECURITY_AND_ACCESS', 'APPROVED', 'LOW');
SELECT setval('ticket_id_seq', (SELECT MAX(id) from ticket));

INSERT INTO history (id, date, action, description) VALUES (1, '2022-01-05 23:15:30', 'Ticket was created', 'Oooooooo'),(2, '2022-03-04 21:12:17', 'Ticket is created', 'Ticket was created'),(3, '2022-02-02 12:01:03', 'Ticket was created', 'Wow!'),(4, '2021-12-12 07:03:12', 'Ticket was created', 'Noooo!'),(5, '2022-04-01 10:03:52', 'Ticket was created', 'Ticket was created');
SELECT setval('history_id_seq', (SELECT MAX(id) from history));

INSERT INTO comment (id, text, date) VALUES (1, 'Oooooooo', '2022-02-01 23:15:30'),(2, 'Heeellooo', '2022-03-03 21:12:17'),(3, 'It is bad', '2022-04-04 12:01:03'),(4, 'Okey', '2022-02-04 07:03:12'),(5, 'Not good', '2022-04-01 10:03:52');
SELECT setval('comment_id_seq', (SELECT MAX(id) from comment));

UPDATE ticket SET assignee_id = 5, approver_id = 3, owner_id = 1 WHERE id = 1;
UPDATE ticket SET assignee_id = 6, approver_id = 4, owner_id = 2 WHERE id = 2;
UPDATE ticket SET assignee_id = 6, approver_id = 4, owner_id = 1 WHERE id = 3;
UPDATE ticket SET assignee_id = 5, approver_id = 3, owner_id = 4 WHERE id = 4;
UPDATE ticket SET assignee_id = 6, approver_id = 4, owner_id = 2 WHERE id = 5;

UPDATE history SET ticket_id = 1, user_id = 1 WHERE id = 1;
UPDATE history SET ticket_id = 2, user_id = 6 WHERE id = 2;
UPDATE history SET ticket_id = 3, user_id = 3 WHERE id = 3;
UPDATE history SET ticket_id = 4, user_id = 5 WHERE id = 4;
UPDATE history SET ticket_id = 5, user_id = 4 WHERE id = 5;

UPDATE comment SET ticket_id = 1, user_id = 1 WHERE id = 1;
UPDATE comment SET ticket_id = 2, user_id = 6 WHERE id = 2;
UPDATE comment SET ticket_id = 3, user_id = 3 WHERE id = 3;
UPDATE comment SET ticket_id = 2, user_id = 5 WHERE id = 4;
UPDATE comment SET ticket_id = 5, user_id = 4 WHERE id = 5;

