INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);
INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)
VALUES (1, 1, 'name', 'a@a.com', 'password'), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));
