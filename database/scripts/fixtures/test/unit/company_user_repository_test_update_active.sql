INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');

INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, must_change_password, api_key)
VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), FALSE, '1234', FALSE, 'ASDF');