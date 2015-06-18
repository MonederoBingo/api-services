INSERT INTO client(client_id, phone) VALUES(1, '6141112233');
INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key)
VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')));
