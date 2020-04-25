insert into store.st_product (id,name,description,external_product_id) values
('e1712476-8127-11ea-a5f1-001e101f0000','Глушитель','деталь для отвода выхлопных газов','e1712476-8127-11ea-a5f1-001e101f0000'),
('53d091e8-7e69-446b-8913-e6962d695de9','Руль','деталь для управления автомобилем','53d091e8-7e69-446b-8913-e6962d695de9');

insert into store.st_catalog(id,product_id, quantity) values
('6fc0f041-338f-4210-a47f-e8e9dcc93a5a','e1712476-8127-11ea-a5f1-001e101f0000',100),
('829f376d-7503-4cfa-a21d-ae7e3f3b42ae','53d091e8-7e69-446b-8913-e6962d695de9',20);

insert into store.st_supplier_order(id,amount,status,payment_callback_url, payment_id) values
('10b8ebfb-c439-4fd8-a1b3-663734511975',100000,'обрабатывается','http://car.ru', 'e372a36c-c06a-4fcb-a476-91bae0e1c22a'),
('43213c79-0e45-413a-b9d5-37164645cd5b',50000,'готов','http://car.ru', '556d4c09-2ef0-4a59-b04f-aef4af78232f');

insert into store.st_client_order(id,amount,status, payment_id) values
('5ad8e8a5-5973-4c75-b0f3-c3c29428fa1a','100000','обрабатывается', 'e372a36c-c06a-4fcb-a476-91bae0e1c22a'),
('556d8eee-7e64-4c10-929a-56bdc0de4aa3','50000','готов', '556d4c09-2ef0-4a59-b04f-aef4af78232f');

insert into store.st_payment(id,ogrn_shop,kpp_shop,inn_shop,payment_account_shop,ogrn_client,kpp_client,
inn_client, payment_account_client, key, amount, callback_url, status , supplier_order_id, client_order_id) values
('e372a36c-c06a-4fcb-a476-91bae0e1c22a','1','2', '3', '4','5','6', '7','8',
'b2d09b73eb5ad0228f9cb2e51485a45f',100000,'http://car.ru','не оплачен','10b8ebfb-c439-4fd8-a1b3-663734511975',NULL),
('556d4c09-2ef0-4a59-b04f-aef4af78232f','10','20', '30', '40','50','60', '70','80',
'556d4c09-2ef0-4a59-b04f-aef4af78232f',50000,'http://car.ru','оплачен',NULL,'556d8eee-7e64-4c10-929a-56bdc0de4aa3');

insert into store.st_supplier_order_items(id, product_id, order_id, quantity) values
('3f0798e2-44a2-442d-9161-b4c5ad0ae28b', 'e1712476-8127-11ea-a5f1-001e101f0000','10b8ebfb-c439-4fd8-a1b3-663734511975',10),
('7716ed7f-145e-4429-9acd-5b27a1d96a54', '53d091e8-7e69-446b-8913-e6962d695de9','43213c79-0e45-413a-b9d5-37164645cd5b',2);

insert into store.st_client_order_items(id, product_id, order_id, quantity) values
('c96195a2-155e-4222-90ed-ecd32f62eb82', 'e1712476-8127-11ea-a5f1-001e101f0000','5ad8e8a5-5973-4c75-b0f3-c3c29428fa1a',10),
('d6f7cfcf-7bcc-4e1a-ae2f-339192e07960', '53d091e8-7e69-446b-8913-e6962d695de9','556d8eee-7e64-4c10-929a-56bdc0de4aa3',2);