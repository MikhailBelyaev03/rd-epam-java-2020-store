drop schema if exists store cascade;

-- Инициализируем схему store
create schema store;

-- Инициалтзируем таблицы, описанные в физической модели.
-- У таблиц должен быть префикс "st_".

-- Создадим таблицу st_product
create table store.st_product (
	id uuid primary key not null,
	name varchar not null, 
	description text not null,
	external_product_id uuid not null
);

-- Создадим таблицу st_catalog
create table store.st_catalog (
	id uuid primary key not null,
	product_id uuid references store.st_product(id) not null,
	quantity int8 not null
);

-- Создадим таблицу st_client_order
create table store.st_client_order (
	id uuid primary key not null,
	amount int8 not null,
	status varchar not null,
	payment_id uuid not null
);

-- Создадим таблицу st_client_order_items
create table store.st_clisent_order_items (
	id uuid primary key not null,
	product_id uuid references store.st_product(id) not null,
	order_id uuid references store.st_client_order(id) not null,
	quantity int8 not null
);

-- Создадим таблицу st_supplier_order
create table store.st_supplier_order (
	id uuid primary key not null,
	amount int8 not null,
	status varchar not null,
	payment_callback_url varchar not null,
	payment_id uuid not null
);

-- Создадим таблицу st_supplier_order_items
create table store.st_supplier_order_items (
	id uuid primary key not null,
	product_id uuid references store.st_product(id) not null,
	order_id uuid references store.st_supplier_order(id) not null,
	quantity int8 not null
);

-- Создадим таблицу st_payment
create table store.st_payment (
	id uuid primary key not null,
	orgn_shop varchar(13) not null,
	kpp_shop varchar(9) not null,
	inn_shop varchar(12) not null,
	payment_account_shop varchar(10) not null,
	orgn_client varchar(13) not null,
	kpp_client varchar(9) not null,
	inn_client varchar(12) not null,
	payment_account_client varchar not null,
	key varchar not null,
	amount int8 not null,
	callback_url varchar not null,
	status varchar not null,
	supplier_order_id uuid not null,
	client_order_id uuid not null
);

-- Добавим внешние ключи в таблицы st_supplier_order и st_payment
alter table store.st_payment
add foreign key (supplier_order_id)
references store.st_supplier_order(id);

-- Добавим внешние ключи в таблицы st_client_order и st_payment
alter table store.st_payment
add foreign key (client_order_id)
references store.st_client_order(id);

-- Инициализируем пользователя store_user с одноименным паролем и следующими правами на схему store:
-- select, insert, update, delete, truncate
-- использовать sequences
-- запуск functions
select * from pg_shadow;

-- Создадим пользователя
create user store_user with password 'store_user';

-- Добавим ему права select, insert, update, delete, truncate на схему store
grant select, insert, update, delete, truncate on all tables in schema store to store_user;

-- Добавим ему права на использование sequences в схеме store
grant all privileges on all sequences in schema store to store_user;

-- Добавим ему права на запуск functions в схеме store
grant all privileges on all functions in schema store to store_user;

-- Добавим стабовые данные для проверки
insert into store.st_product (id, name, description, external_product_id) values
('c12ab749-87bc-4099-adc4-f17e2c1d6e5a', 'Куртка', 'Верхняя одежда', '2e8e29dc-a44b-4a6a-851f-3d7e917d2b42'),
('f0756d4b-34c6-484c-9fc5-71a0f1631c8e', 'Ботинки', 'Обувь', '64529260-4b60-4410-a47a-78f69cb36825');

insert into store.st_catalog (id, product_id, quantity) values
('950a02cd-5d49-4987-94a5-18bb6cef3f94', 'c12ab749-87bc-4099-adc4-f17e2c1d6e5a','5'),
('e992f376-c566-4204-9ce2-e635eb28801c','f0756d4b-34c6-484c-9fc5-71a0f1631c8e','8');

insert into store.st_client_order (id, amount, status, payment_id) values
('1f452b44-35e7-4743-8aed-0908de6c10db', '100', 'Готов', 'd85aa00f-b832-40cd-bd6e-6bb4092bb91d'),
('cd0a874c-68ae-44b7-a600-11a1f7e41ec7', '200', 'В процессе', '2e5c9652-03b2-448b-9479-d1118f8c2239');

insert into store.st_clisent_order_items (id, product_id, order_id, quantity) values
('07b41a91-ec23-4d96-a160-8e053ccc137d', 'c12ab749-87bc-4099-adc4-f17e2c1d6e5a',
'1f452b44-35e7-4743-8aed-0908de6c10db', '10'),
('acfecc60-80af-4678-a6b9-9eb9b5b8cee1', 'f0756d4b-34c6-484c-9fc5-71a0f1631c8e',
'cd0a874c-68ae-44b7-a600-11a1f7e41ec7', '15');

insert into store.st_supplier_order (id, amount, status, payment_callback_url, payment_id) values
('1a5ce931-bad3-45c5-a4ca-d22a6fd9c453', '500','В процессе', 'https://jacket.com', 'd85aa00f-b832-40cd-bd6e-6bb4092bb91d'),
('8b592226-df75-4609-8ca2-bb2af4d98b18', '100','Готов', 'https://shoes.com', '2e5c9652-03b2-448b-9479-d1118f8c2239');

insert into store.st_supplier_order_items (id, product_id, order_id, quantity) values
('7344f860-1b4a-4fb3-ad4c-dc2981733bff', 'c12ab749-87bc-4099-adc4-f17e2c1d6e5a',
'1a5ce931-bad3-45c5-a4ca-d22a6fd9c453', '500'),
('1ec4b841-26fd-45a9-b9e4-3edf6717e0a9', 'f0756d4b-34c6-484c-9fc5-71a0f1631c8e',
'8b592226-df75-4609-8ca2-bb2af4d98b18', '100');

insert into store.st_payment (id, orgn_shop, kpp_shop, inn_shop, payment_account_shop,
orgn_client, kpp_client, inn_client, payment_account_client,
key, amount, callback_url, status, supplier_order_id, client_order_id) values
('d85aa00f-b832-40cd-bd6e-6bb4092bb91d', '5044687244080', '929945885', '704541383710', '1111',
'1085352563933', '711201844', '044906427958', '3333',
'1234', '200', 'https://t-shot.com', 'Не оплачен', '1a5ce931-bad3-45c5-a4ca-d22a6fd9c453', '1f452b44-35e7-4743-8aed-0908de6c10db'),
('2e5c9652-03b2-448b-9479-d1118f8c2239', '1177221583736', '730301089', '174868163911', '2222',
'1068652878139', '579944516', '497171148858', '4444',
'4321', '100', 'https://trainings.com', 'Оплачен', '8b592226-df75-4609-8ca2-bb2af4d98b18', 'cd0a874c-68ae-44b7-a600-11a1f7e41ec7');