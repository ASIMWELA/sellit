INSERT  INTO roles(name, uuid) VALUES('ROLE_CUSTOMER', '5c36ac12fa-9'),('ROLE_ADMIN', '7f3e41-d25c5'),('ROLE_PROVIDER', '1030-a3a590c');
INSERT INTO users(uuid,user_name, first_name, last_name, email, mobile_number,is_enabled, password, is_provider) VALUES('bhKlurMwe6YA','Jdoe','John','Doe','jdoe@gmail.com', '0888082078',true,'$2y$12$wgDA0c0cHP88epxFCgr7Neg2Jr18XNfpJEl1ZZP60hC1b6bCSSfyq', false);
INSERT INTO user_roles(user_id, role_id) VALUES(1, 2);

