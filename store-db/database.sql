create schema store;

create user store_user password 'store_user';

grant select, insert, update ,delete, truncate on all tables in schema store to store_user;

grant all privileges on all sequences in schema store to store_user;

grant all privileges on all functions in schema store to store_user;


CREATE TABLE store.st_product ( 
  id uuid primary key,
  name varchar,
  description text,
  external_product_id uuid
);

CREATE table store.st_catalog ( 
  id uuid primary key,
  product_id uuid,
  quantity int8,
  foreign key(product_id) references store.st_product(id)
);

CREATE TABLE store.st_supplier_order( 
  id uuid primary key,
  amount int8,
  status varchar,
  payment_callback_url varchar,
  payment_id uuid
);

CREATE TABLE store.st_client_order ( 
  id uuid primary key,
  amount int8,
  status varchar,
  payment_id uuid
) ;


CREATE TABLE store.st_supplier_order_items ( 
  id uuid primary key,
  product_id uuid,
  order_id uuid,
  quantity int8,
  foreign key(product_id) references store.st_product(id),
  foreign key(order_id) references store.st_supplier_order(id)
);


CREATE TABLE store.st_client_order_items ( 
  id uuid primary key,
  product_id uuid,
  order_id uuid,
  quantity int8,
  foreign key(product_id) references store.st_product(id),
  foreign key(order_id) references store.st_client_order(id)
);


CREATE TABLE store.st_payment ( 
  id uuid primary key,
  ogrn_shop varchar,
  kpp_shop varchar,
  inn_shop varchar,
  payment_account_shop varchar,
  ogrn_client varchar,
  kpp_client varchar,
  inn_client varchar ,
  payment_account_client varchar,
  key varchar,
  amount int8,
  callback_url varchar,
  status varchar,
  supplier_order_id uuid unique,
  client_order_id uuid unique,
  foreign key(supplier_order_id) references store.st_supplier_order(id),
  foreign key(client_order_id) references store.st_client_order(id)
);


insert into store.st_product (id,name,description,external_product_id) values
('9701b1a0-6c5b-407a-be81-3c6b6a0d433e','Принтер','Принтер печатает','9701b1a0-6e5b-407a-be81-3c6b6a0d433e'),
('1701b1a0-6c5b-407a-be81-3c6b6a0d433e','Сканер','Сканер сканирует','9701b1a0-6c5b-407a-be81-3c6e6a0d433e');

insert into store.st_catalog(id,product_id, quantity) values
('9701b1a1-6c5b-407a-be81-3c6b6a0d433e','9701b1a0-6c5b-407a-be81-3c6b6a0d433e','10'),
('9701b1a0-6c5b-407a-be81-3c6b6a0d431e','1701b1a0-6c5b-407a-be81-3c6b6a0d433e','5');

insert into store.st_supplier_order(id,amount,status,payment_callback_url, payment_id) values
('9701b1a0-6c5b-407a-be72-3c6b6a0d433e','30000','В процессе','https:/shop.ru', '9799b1a0-6c5b-407a-be81-3c6b6a0d433e'),
('9701b1a0-6c5b-407a-be72-3c6b6a0d213e','50000','Завершен', 'https:/shop.ru', '20a256fe-0382-4614-9120-656153048d43');

insert into store.st_client_order(id,amount,status, payment_id) values
('9701b1a0-6c5b-407a-be72-3c6b6a0d111e','10000','В процессе', '9722b1a0-6c5b-407a-be81-3c6b6a0d433e'),
('9701b1a0-6c5b-407a-be72-3c6b6a0d222e','20000','Завершен', '54eb7c9b-c9c8-43dc-93ae-85c49a138982');

insert into store.st_supplier_order_items(id, product_id, order_id, quantity) values
('da92a3a9-08ba-4aad-af93-2651b1a39cd4', '9701b1a0-6c5b-407a-be81-3c6b6a0d433e','9701b1a0-6c5b-407a-be72-3c6b6a0d433e','3'),
('da93a3a9-08ba-4aad-af93-2651b1a39cd4', '1701b1a0-6c5b-407a-be81-3c6b6a0d433e','9701b1a0-6c5b-407a-be72-3c6b6a0d213e','5');


insert into store.st_client_order_items(id, product_id, order_id, quantity) values
('da92a3a1-08ba-4aad-af93-2651b1a39cd4', '1701b1a0-6c5b-407a-be81-3c6b6a0d433e','9701b1a0-6c5b-407a-be72-3c6b6a0d111e','1'),
('da93a3a2-08ba-4aad-af93-2651b1a39cd4', '1701b1a0-6c5b-407a-be81-3c6b6a0d433e','9701b1a0-6c5b-407a-be72-3c6b6a0d111e','2');

insert into store.st_payment(id,ogrn_shop,kpp_shop,inn_shop,payment_account_shop,ogrn_client,kpp_client, inn_client, payment_account_client, key, amount, callback_url, status , supplier_order_id, client_order_id) values
('da92a3a1-08ba-4aad-af93-2651b1a61cd4','111','222', '333', '444','555','666', '777', '888','02b1be0d48924c327124732726097157','30000','https://shop.ru','Совершен','9701b1a0-6c5b-407a-be72-3c6b6a0d433e',NULL),
('da92a3a1-08ba-4aad-af93-2651b1a62cd4','000','001', '002', '003','004','005', '006', '007','02b1be0d48924c328124732726097157','20000','https://shop.ru','Совершен',NULL,'9701b1a0-6c5b-407a-be72-3c6b6a0d222e')

