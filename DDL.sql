#criando database
create database Voltz;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS cryptoAsset;
DROP TABLE IF EXISTS wallet;
DROP TABLE IF EXISTS wallet_cryptoAsset;
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS userCompanyRelation;
DROP TABLE IF EXISTS company_cryptoAsset;
DROP TABLE IF EXISTS market;


#criando usuario
use Voltz;
create table users(
	id int not null auto_increment,
    name VARCHAR(255) not null,
    email VARCHAR(255) not null,
    password VARCHAR(255) not null,
    primary key(id),
    unique key uq_user_id(id),
    unique key uq_user_email(email)
);

use Voltz;
create table company(
	id int not null auto_increment,
    name VARCHAR(255) not null,
    identifier VARCHAR(255) not null,
    primary key(id),
    unique key uq_user_id(id),
    unique key uq_user_identifier(identifier)
);

use Voltz;
create table cryptoAsset(
	id int not null auto_increment,
    name VARCHAR(255) not null,
    symbol VARCHAR(10) not null,
    quantity double not null default 0.0,
    price double not null,
    primary key(id),
    unique key uq_user_id(id),
    unique key uq_user_symbol(symbol)
);

use Voltz;
create table wallet(
	id int not null auto_increment,
    user_id int not null, 
    primary key(id),
    unique key uq_user_id(id)
);

use Voltz;
create table wallet_cryptoAsset(
	wallet_id int not null, 
    crypto_asset_id int not null, 
    quantity double not null default 0.0,
    primary key(wallet_id, crypto_asset_id)
);

use Voltz;
create table transaction(
	id int not null auto_increment,
    crypto_asset_id int not null, 
    amount double not null,
    type VARCHAR(50) not null CHECK (type IN ('BUY', 'SELL')),
    timestamp datetime not null default current_timestamp,
    user_id int null,  
    primary key(id),
    unique key uq_user_id(id)
);

use Voltz;
create table userCompanyRelation(
	user_id int not null,
    company_id int not null,
    invested_amount double not null default 0.0,
    start_date date not null,
    primary key(user_id, company_id)
);

use Voltz;
create table company_cryptoAsset(
	company_id int not null, 
    crypto_asset_id int not null, 
    quantity double not null default 0.0,
    primary key(company_id, crypto_asset_id)
);

use Voltz;
create table market(
	symbol VARCHAR(10) not null, 
    price double not null,
    last_updated datetime not null default current_timestamp,
    primary key(symbol),
    unique key uq_user_symbol(symbol)
);

#adicionando as fks
#tabela wallet
ALTER TABLE wallet
ADD FOREIGN KEY (user_id) REFERENCES users(id);

#tabela wallet_cryptoAsset
ALTER TABLE wallet_cryptoAsset
ADD FOREIGN KEY (wallet_id) REFERENCES wallet(id);
ALTER TABLE wallet_cryptoAsset
ADD FOREIGN KEY (crypto_asset_id) REFERENCES cryptoAsset(id);

#tabela transaction
ALTER TABLE transaction
ADD FOREIGN KEY (crypto_asset_id) REFERENCES cryptoAsset(id);
ALTER TABLE transaction
ADD FOREIGN KEY (user_id) REFERENCES users(id);

#tabela userCompanyRelation
ALTER TABLE userCompanyRelation
ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE userCompanyRelation
ADD FOREIGN KEY (company_id) REFERENCES company(id);

#tabela company_cryptoAsset
ALTER TABLE company_cryptoAsset
ADD FOREIGN KEY (company_id) REFERENCES company(id);
ALTER TABLE company_cryptoAsset
ADD FOREIGN KEY (crypto_asset_id) REFERENCES cryptoAsset(id);

#tabela market
ALTER TABLE market
ADD FOREIGN KEY (symbol) REFERENCES cryptoAsset(symbol);