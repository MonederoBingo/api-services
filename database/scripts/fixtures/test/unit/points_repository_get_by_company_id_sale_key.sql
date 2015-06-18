INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');
INSERT INTO client (client_id, phone) VALUES (1, 'A');
INSERT INTO points (company_id, client_id, sale_key, sale_amount, points_to_earn, required_amount, earned_points, date)
VALUES (1, 1, 'A123', 100, 10, 100, 10, now());
