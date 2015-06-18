INSERT INTO client(client_id, phone) VALUES(1, 'A');
INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'A','B');
INSERT INTO company(company_id, name, url_image_logo) VALUES(2, 'X','Y');
INSERT INTO company_client_mapping(company_id, client_id) VALUES(1, 1);
INSERT INTO company_client_mapping(company_id, client_id) VALUES(2, 1);