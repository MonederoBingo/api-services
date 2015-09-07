UPDATE client
SET can_receive_promo_sms = FALSE;


UPDATE client
SET can_receive_promo_sms = TRUE
FROM client_user
WHERE client.client_id = client_user.client_id;