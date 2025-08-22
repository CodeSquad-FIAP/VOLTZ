-- Usando o banco de dados Voltz
USE Voltz;

-- INSERT statements para popular as tabelas
INSERT INTO users (id, name, email, password) VALUES
(1, 'Scrooge McDuck', 'scrooge@ducktales.com', 'vault123'),
(2, 'Donald Duck', 'donald@ducktales.com', 'quackquack'),
(3, 'Daisy Duck', 'daisy@ducktales.com', 'flower123'),
(4, 'Carol', 'carol@ducktales.com', 'password');

INSERT INTO company (id, name, identifier) VALUES
(1, 'DuckCorp', 'CNPJ-001'),
(2, 'TreasureInc', 'CNPJ-002'),
(3, 'Duck Enterprises', 'CNPJ-010'),
(4, 'Duck Investments', 'CNPJ-011');

INSERT INTO cryptoAsset (id, name, symbol, quantity, price) VALUES
(1, 'Bitcoin', 'BTC', 2.0, 65000.0),
(2, 'Ethereum', 'ETH', 5.0, 3200.0),
(3, 'Cardano', 'ADA', 1000.0, 0.45),
(4, 'Solana', 'SOL', 0.0, 150.0);

INSERT INTO market (symbol, price) VALUES
('BTC', 65000.0),
('ETH', 3200.0),
('ADA', 0.45),
('SOL', 150.0);

INSERT INTO wallet (user_id) VALUES
(1),
(2),
(3),
(4);

INSERT INTO wallet_cryptoAsset (wallet_id, crypto_asset_id, quantity) VALUES
(1, 1, 2.0),
(1, 2, 5.0);

INSERT INTO company_cryptoAsset (company_id, crypto_asset_id, quantity) VALUES
(1, 1, 2.0),
(2, 2, 6.0),
(3, 3, 1000.0);

INSERT INTO transaction (crypto_asset_id, amount, type, user_id) VALUES
(1, 2.0, 'BUY', 1),
(2, 5.0, 'BUY', 1);

INSERT INTO userCompanyRelation (user_id, company_id, invested_amount, start_date) VALUES
(1, 1, 50000.00, '2025-05-01'),
(1, 2, 35000.00, '2025-05-02'),
(2, 1, 20000.00, '2025-05-03'),
(3, 2, 25000.00, '2025-05-04'),
-- Relações baseadas no HashMap
(2, 3, 0.0, CURDATE()),
(2, 4, 0.0, CURDATE()),
(3, 4, 0.0, CURDATE());

-- UPDATE statements para modificar dados existentes

UPDATE market SET price = 68000.0, last_updated = CURRENT_TIMESTAMP WHERE symbol = 'BTC';
UPDATE cryptoAsset SET price = 68000.0 WHERE symbol = 'BTC';

UPDATE users SET email = 'donald.duck@newemail.com' WHERE id = 2;

UPDATE wallet_cryptoAsset SET quantity = quantity + 0.5 WHERE wallet_id = 1 AND crypto_asset_id = 1;

-- DELETE statements para remover dados

DELETE FROM transaction WHERE id = 2;

DELETE FROM userCompanyRelation WHERE user_id = 2 AND company_id = 4;

-- SELECT statements para consultar os dados

SELECT name, email FROM users;

SELECT name, identifier FROM company;

SELECT name, symbol, quantity, price, (quantity * price) AS total_value
FROM cryptoAsset;

SELECT u.name AS user_name, ca.name AS asset_name, wca.quantity, ca.price
FROM users u
         JOIN wallet w ON u.id = w.user_id
         JOIN wallet_cryptoAsset wca ON w.id = wca.wallet_id
         JOIN cryptoAsset ca ON wca.crypto_asset_id = ca.id
WHERE u.id = 1;

SELECT t.id, u.name, ca.name, t.amount, t.type, t.timestamp
FROM transaction t
         JOIN users u ON t.user_id = u.id
         JOIN cryptoAsset ca ON t.crypto_asset_id = ca.id
WHERE t.type = 'BUY';

SELECT u.name, SUM(ucr.invested_amount) AS total_invested
FROM users u
         JOIN userCompanyRelation ucr ON u.id = ucr.user_id
GROUP BY u.name;

SELECT c.name AS company_name, ca.name AS asset_name, cca.quantity
FROM company c
         JOIN company_cryptoAsset cca ON c.id = cca.company_id
         JOIN cryptoAsset ca ON cca.crypto_asset_id = ca.id
ORDER BY c.name;