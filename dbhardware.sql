CREATE DATABASE dbhardware;
USE dbhardware;

CREATE TABLE Addresses(
	address_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    street VARCHAR(50),
    city VARCHAR(50),
    zip_code VARCHAR(6)
) AUTO_INCREMENT = 20001;

INSERT INTO Addresses (street, city, zip_code)
VALUES
	('123 Silicon Ave', 'Makati City', '1200'),
	('45 Innovation Drive', 'Quezon City', '1101'),
	('678 Memory Lane', 'Taguig City', '1630'),
	('89 Circuit Blvd', 'Pasig City', '1605'),
	('55 Tech Park Road', 'Mandaluyong City', '1550'),
	('321 Hardware Street', 'Cebu City', '6000'),
	('77 Storage Way', 'Davao City', '8000'),
	('98 Data Drive', 'Iloilo City', '5000'),
	('12 Flash Court', 'Baguio City', '2600'),
    ('123 Rome Ave', 'Baguio City', '1100'),
    ('88 Korea Drive Ave', 'Commonwealth City', '9000');
    
CREATE TABLE Vehicles(
	vehicle_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(20) NOT NULL,
    type ENUM('Car', 'Motor', 'Truck'),
    status ENUM('Available', 'Occupied'),
    active_status ENUM('Active', 'Inactive') NOT NULL
) AUTO_INCREMENT = 9000;

INSERT INTO Vehicles (plate_number, type, status, active_status)
VALUES
    ('ABC123', 'Car', 'Available', 'Active'),
    ('XYZ789', 'Motor', 'Available', 'Active'),
    ('TRK456', 'Truck', 'Available', 'Active'),
    ('MTR321', 'Motor', 'Available', 'Active'),
    ('CAR654', 'Car', 'Available', 'Active'),
    ('TRK987', 'Truck', 'Available', 'Active'),
    ('MTR159', 'Motor', 'Available', 'Active'),
    ('CAR753', 'Car', 'Available', 'Active'),
    ('TRK852', 'Truck', 'Available', 'Active'),
    ('CAR951', 'Car', 'Available', 'Active'),
    ('MTR357', 'Motor', 'Available', 'Active');

CREATE TABLE Delivery_Info(
	delivery_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT NOT NULL,
    shipping_datetime DATETIME,
    arrival_datetime DATETIME,
    status ENUM('Shipping', 'Complete'),
    CONSTRAINT delivery_info_fk_vehicles FOREIGN KEY (vehicle_id) REFERENCES Vehicles(vehicle_id)
) AUTO_INCREMENT = 8001;

INSERT INTO Delivery_Info (vehicle_id, shipping_datetime, arrival_datetime, status)
VALUES
    (9000, '2025-10-01 09:30:00', '2025-10-01 10:30:00', 'Complete'), 
	(9000, '2025-09-20 08:00:00', '2025-09-20 10:00:00', 'Complete'), 
	(9001, '2025-09-22 10:00:00', '2025-09-22 11:15:00', 'Complete'), 
	(9001, '2025-10-03 12:40:00', '2025-10-03 13:45:00', 'Complete'), 
	(9002, '2025-09-25 07:20:00', '2025-09-25 08:30:00', 'Complete'), 
	(9003, '2025-10-10 07:00:00', '2025-10-10 09:00:00', 'Complete'), 
	(9003, '2025-09-28 05:30:00', '2025-09-28 07:30:00', 'Complete'),
	(9004, '2025-09-28 07:45:00', '2025-09-28 09:00:00', 'Complete'), 
	(9004, '2025-10-05 12:00:00', '2025-10-05 14:00:00', 'Complete'), 
	(9005, '2025-09-26 12:15:00', '2025-09-26 15:15:00', 'Complete'), 
	(9005, '2025-10-07 07:30:00', '2025-10-07 08:45:00', 'Complete');
    
   -- Delivery_Info Records for 12 Customer Orders (8012 through 8023)
INSERT INTO Delivery_Info (vehicle_id, shipping_datetime, arrival_datetime, status)
VALUES
	-- All new deliveries are Complete and use available vehicles
    (9000, '2025-10-01 10:00:00', '2025-10-01 11:30:00', 'Complete'), -- 8012 (delivery_id)
    (9001, '2025-09-20 08:30:00', '2025-09-20 10:15:00', 'Complete'), -- 8013
    (9002, '2025-09-28 07:30:00', '2025-09-28 09:15:00', 'Complete'), -- 8014
    (9003, '2025-10-05 11:30:00', '2025-10-05 13:00:00', 'Complete'), -- 8015
    (9004, '2025-10-10 10:00:00', '2025-10-10 11:45:00', 'Complete'), -- 8016
    (9005, '2025-11-01 09:00:00', '2025-11-01 10:30:00', 'Complete'), -- 8017
    (9000, '2025-11-05 12:00:00', '2025-11-05 13:45:00', 'Complete'), -- 8018
    (9001, '2025-11-10 08:00:00', '2025-11-10 09:30:00', 'Complete'), -- 8019
    (9002, '2025-11-15 14:00:00', '2025-11-15 15:30:00', 'Complete'), -- 8020
    (9003, '2025-10-25 09:30:00', '2025-10-25 11:00:00', 'Complete'), -- 8021
    (9004, '2025-11-20 07:00:00', '2025-11-20 08:30:00', 'Complete'), -- 8022
    (9005, '2025-11-25 11:00:00', '2025-11-25 12:30:00', 'Complete'); -- 8023
    
    
CREATE TABLE Suppliers(
	supplier_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(80) NOT NULL UNIQUE,
    phone_number VARCHAR(11) NOT NULL,
    email VARCHAR(50) UNIQUE,
    address_id INT NOT NULL,
    status ENUM('Active', 'Inactive')  NOT NULL, 
    CONSTRAINT supplies_fk_addresses FOREIGN KEY (address_id) REFERENCES Addresses(address_id)
) AUTO_INCREMENT = 10010;

INSERT INTO Suppliers (company_name, phone_number, email, address_id, status)
VALUES
	('AMD Authorized Distributor', '09171234567', 'sales@amdpartners.com', 20001, 'Active'),
	('Intel Technology Supplier', '09181234567', 'info@intel-supplier.com', 20002, 'Active'),
	('Corsair & G.SKILL Memory Distributors', '09192345678', 'contact@memorydist.com', 20003, 'Active'),
	('Gigabyte Authorized Partner', '09203456789', 'support@gigabytepartner.com', 20004, 'Active'),
	('ASUS Component Supplier', '09214567890', 'sales@asusparts.com', 20005, 'Active'),
	('ASRock and MSI Suppliers', '09225678901', 'orders@msisupplier.com', 20006, 'Active'),
	('Samsung Storage Solutions', '09236789012', 'samsung@storagesol.com', 20007, 'Active'),
	('Seagate Technology Partner', '09247890123', 'service@seagatepartner.com', 20008, 'Active'),
	('Crucial Memory Supplier', '09258901234', 'sales@crucialdist.com', 20009, 'Active');

CREATE TABLE Products (
    product_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    brand VARCHAR(20) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    category ENUM('CPU','GPU','Motherboard','Memory (RAM)', 'Storage Device'),
    status ENUM('Active', 'Inactive')  NOT NULL
) AUTO_INCREMENT = 1001;

INSERT INTO Products (name, description, brand, price, quantity, category, status)
VALUES
	('ASROCK B450M-HDV R4.0 MATX Motherboard', 'Supports AMD AM4 Socket Ryzen™ 2000, 3000, 4000 G-Series, 5000 and 5000 G-Series Desktop Processors', 'ASRock', 3150.00, 40, 'Motherboard', 'Active'),
    ('AMD Ryzen 5 5600X', '6-Core 12-Thread Unlocked Desktop Processor, up to 4.6GHz Max Boost', 'AMD', 11750.00, 25, 'CPU', 'Active'),
	('Intel Core i7-12700K', '12-Core Processor (8P+4E), up to 5.0GHz with Intel UHD Graphics 770', 'Intel', 17500.00, 30, 'CPU', 'Active'),
	('MSI B550M PRO-VDH WIFI Motherboard', 'AMD AM4, DDR4, WiFi, Bluetooth, and PCIe 4.0 ready', 'MSI', 5500.00, 20, 'Motherboard', 'Active'),
	('Gigabyte AORUS GeForce RTX 3060 Elite 12G', '12GB GDDR6, RGB Fusion 2.0, Windforce 3X Cooling System', 'Gigabyte', 24999.00, 15, 'GPU', 'Active'),
	('ASUS Dual GeForce RTX 4070 12GB', '12GB GDDR6X, Axial-tech Fans, Dual BIOS', 'ASUS', 39999.00, 10, 'GPU', 'Active'),
	('Corsair Vengeance LPX 16GB (2x8GB) DDR4 3200MHz', 'High-performance memory optimized for AMD and Intel', 'Corsair', 2899.00, 50, 'Memory (RAM)', 'Active'),
	('G.SKILL Trident Z RGB 32GB (2x16GB) DDR4 3600MHz', 'RGB lighting and high performance', 'G.SKILL', 5899.00, 35, 'Memory (RAM)', 'Active'),
	('Samsung 970 EVO Plus 1TB NVMe SSD', 'Up to 3500MB/s read speed, V-NAND technology', 'Samsung', 5999.00, 60, 'Storage Device', 'Active'),
	('Seagate Barracuda 2TB HDD', '7200RPM SATA 6Gb/s 256MB cache, 3.5-inch internal drive', 'Seagate', 3250.00, 80, 'Storage Device', 'Active'),
	('Crucial P5 Plus 2TB PCIe Gen4 NVMe SSD', 'Read speeds up to 6600MB/s, ideal for gaming and productivity', 'Crucial', 8999.00, 40, 'Storage Device', 'Active');

CREATE TABLE Supplier_Shipment(
	supplier_shipment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	product_id INT NOT NULL,
    supplier_id INT NOT NULL,
    product_cost DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    delivery_id INT NOT NULL,
    CONSTRAINT supplier_shipment_fk_suppliers FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id),
    CONSTRAINT supplier_shipment_fk_products FOREIGN KEY (product_id) REFERENCES Products(product_id),
    CONSTRAINT supplier_shipment_fk_delivery_info FOREIGN KEY (delivery_id) REFERENCES Delivery_Info(delivery_id)
) AUTO_INCREMENT = 1;

INSERT INTO Supplier_Shipment (product_id, supplier_id, product_cost, quantity, delivery_id)
VALUES
	(1001, 10015, 2800.00, 40, 8001), 
	(1002, 10010, 10500.00, 25, 8002), 
	(1003, 10011, 16000.00, 30, 8003), 
	(1004, 10015, 5000.00, 20, 8004), 
	(1005, 10013, 22500.00, 15, 8005), 
	(1006, 10014, 37000.00, 10, 8006), 
	(1007, 10012, 2600.00, 50, 8007),
	(1008, 10012, 5300.00, 35, 8008), 
	(1009, 10016, 5200.00, 60, 8009), 
	(1010, 10017, 2900.00, 80, 8010), 
	(1011, 10018, 8200.00, 40, 8011);

CREATE TABLE Customers (
    customer_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(11) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, 
    address_id INT NOT NULL,
    status ENUM('Active', 'Inactive')  NOT NULL,
    CONSTRAINT customers_fk_addresses FOREIGN KEY (address_id) REFERENCES Addresses(address_id)
) AUTO_INCREMENT = 6001;

INSERT INTO Customers (first_name, last_name, email, phone_number, password, address_id, status)
VALUES
	('Allysa', 'Chong', 'allysa_chong@gmail.com', '09123456789', 'RandomPass', 20010, 'Active'),
    ('Fiona', 'Maningas', 'fiona_maningas@gmail.com', '09173456789', 'PassPass', 20011, 'Active');

CREATE TABLE Cart(
	cart_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    product_id INT NOT NULL,
    customer_id INT NOT NULL,
    CONSTRAINT cart_fk_products FOREIGN KEY (product_id) REFERENCES Products(product_id),
    CONSTRAINT cart_fk_customers FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
) AUTO_INCREMENT = 4000;

CREATE TABLE Orders(
	order_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    payment_status ENUM ('Paid', 'Not Paid') NOT NULL,
    status ENUM('Pending', 'Shipping', 'Completed', 'Returned') NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    order_datetime DATETIME,
    customer_id INT NOT NULL,
    shipping_address_id INT NOT NULL,
    CONSTRAINT order_fk_customers FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    CONSTRAINT order_fk_address FOREIGN KEY (shipping_address_id) REFERENCES Addresses(address_id)
) AUTO_INCREMENT = 7000;

-- INSERT 12 NEW COMPLETED ORDERS (7000 to 7011)
INSERT INTO Orders (payment_status, status, total_price, order_datetime, customer_id, shipping_address_id)
VALUES
    ('Paid', 'Completed', 14649.00, '2025-10-01 09:00:00', 6001, 20010), -- 7000 (Q=1)
    ('Paid', 'Completed', 30898.00, '2025-09-20 07:45:00', 6002, 20011), -- 7001 (Q=1)
    ('Paid', 'Completed', 42898.00, '2025-09-28 07:00:00', 6001, 20010), -- 7002 (Q=1)
    ('Paid', 'Completed', 11499.00, '2025-10-05 11:00:00', 6002, 20011), -- 7003 (Q=1)
    ('Paid', 'Completed', 11998.00, '2025-10-10 09:00:00', 6001, 20010), -- 7004 (Q>1: 2x SSD)
    ('Paid', 'Completed', 8697.00, '2025-11-01 09:30:00', 6002, 20011), -- 7005 (Q>1: 3x RAM)
    ('Paid', 'Completed', 26499.00, '2025-11-05 11:30:00', 6001, 20010), -- 7006 (Q=1)
    ('Paid', 'Completed', 6400.00, '2025-11-10 08:30:00', 6002, 20011), -- 7007 (Q=1)
    ('Paid', 'Completed', 17298.00, '2025-11-15 13:00:00', 6001, 20010), -- 7008 (Q>1: 2x G.SKILL)
    ('Paid', 'Completed', 36749.00, '2025-10-25 10:00:00', 6002, 20011), -- 7009 (Q=1)
    ('Paid', 'Completed', 17745.00, '2025-11-20 06:30:00', 6001, 20010), -- 7010 (Q>1: 5x RAM)
    ('Paid', 'Completed', 57499.00, '2025-11-25 10:30:00', 6002, 20011); -- 7011 (Q=1)

CREATE TABLE Order_Details(
	order_detail_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    product_id INT NOT NULL,
    order_id INT NOT NULL,
    CONSTRAINT orderdetails_fk_products FOREIGN KEY (product_id) REFERENCES Products(product_id),
    CONSTRAINT orderdetails_fk_orders FOREIGN KEY (order_id) REFERENCES Orders(order_id)
) AUTO_INCREMENT = 3000;

-- INSERT Order_Details for the 12 orders (3000 to 3018)
INSERT INTO Order_Details (quantity, unit_price, product_id, order_id)
VALUES
    -- Order 7000 (Total: 14649.00)
    (1, 11750.00, 1002, 7000),  -- AMD Ryzen 5 5600X
    (1, 2899.00, 1007, 7000),   -- Corsair Vengeance LPX 16GB
    
    -- Order 7001 (Total: 30898.00)
    (1, 24999.00, 1005, 7001),  -- Gigabyte RTX 3060
    (1, 5899.00, 1008, 7001),   -- G.SKILL Trident Z RGB 32GB
    
    -- Order 7002 (Total: 42898.00)
    (1, 39999.00, 1006, 7002),  -- ASUS Dual GeForce RTX 4070
    (1, 2899.00, 1007, 7002),   -- Corsair Vengeance LPX 16GB
    
    -- Order 7003 (Total: 11499.00)
    (1, 5500.00, 1004, 7003),   -- MSI B550M PRO-VDH WIFI Motherboard
    (1, 5999.00, 1009, 7003),   -- Samsung 970 EVO Plus 1TB NVMe SSD

    -- Order 7004 (Total: 11998.00 - QTY > 1)
    (2, 5999.00, 1009, 7004),   -- Samsung 970 EVO Plus 1TB NVMe SSD (2x)

    -- Order 7005 (Total: 8697.00 - QTY > 1)
    (3, 2899.00, 1007, 7005),   -- Corsair Vengeance LPX 16GB (3x)
    
    -- Order 7006 (Total: 26499.00)
    (1, 17500.00, 1003, 7006),  -- Intel Core i7-12700K
    (1, 8999.00, 1011, 7006),   -- Crucial P5 Plus 2TB PCIe Gen4 NVMe SSD
    
    -- Order 7007 (Total: 6400.00)
    (1, 3150.00, 1001, 7007),   -- ASROCK B450M-HDV R4.0 MATX Motherboard
    (1, 3250.00, 1010, 7007),   -- Seagate Barracuda 2TB HDD
    
    -- Order 7008 (Total: 17298.00 - QTY > 1)
    (1, 5500.00, 1004, 7008),   -- MSI B550M PRO-VDH WIFI Motherboard
    (2, 5899.00, 1008, 7008),   -- G.SKILL Trident Z RGB 32GB (2x)
    
    -- Order 7009 (Total: 36749.00)
    (1, 11750.00, 1002, 7009),  -- AMD Ryzen 5 5600X
    (1, 24999.00, 1005, 7009),  -- Gigabyte AORUS GeForce RTX 3060 Elite 12G
    
    -- Order 7010 (Total: 17745.00 - QTY > 1)
    (5, 2899.00, 1007, 7010),   -- Corsair Vengeance LPX 16GB (5x)
    (1, 3250.00, 1010, 7010),   -- Seagate Barracuda 2TB HDD
    
    -- Order 7011 (Total: 57499.00)
    (1, 17500.00, 1003, 7011),  -- Intel Core i7-12700K
    (1, 39999.00, 1006, 7011);  -- ASUS Dual GeForce RTX 4070
    
    
CREATE TABLE Shipping(
	shipping_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    delivery_id INT NOT NULL,
    CONSTRAINT shipping_fk_orders FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    CONSTRAINT shipping_fk_delivery_info FOREIGN KEY (delivery_id) REFERENCES Delivery_Info(delivery_id)
) AUTO_INCREMENT = 2000;

-- INSERT Shipping records for the 12 orders (2000 to 2011)
INSERT INTO Shipping (order_id, delivery_id)
VALUES
    (7000, 8012), 
    (7001, 8013),
    (7002, 8014),
    (7003, 8015),
    (7004, 8016),
    (7005, 8017),
    (7006, 8018),
    (7007, 8019),
    (7008, 8020),
    (7009, 8021),
    (7010, 8022),
    (7011, 8023);
    
CREATE TABLE Returns (
    return_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_detail_id INT NOT NULL,
    product_quantity INT NOT NULL,
    return_date DATETIME NOT NULL,
    return_reason VARCHAR(255),
    is_resellable BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT returns_fk_order_details FOREIGN KEY (order_detail_id) REFERENCES Order_Details(order_detail_id)
) AUTO_INCREMENT = 9000;

-- VIEWS --
CREATE VIEW admin_product AS
SELECT product_id AS id, name, description, brand, FORMAT(price, 2) AS price, quantity, category, status
FROM Products
ORDER BY product_id;
    
CREATE VIEW admin_supplier_shipment AS
SELECT ss.supplier_shipment_id AS id, ss.product_id, ss.supplier_id, di.vehicle_id, FORMAT(ss.product_cost, 2) AS price, ss.quantity, di.shipping_datetime, di.arrival_datetime, di.status
FROM Supplier_Shipment ss
JOIN Delivery_Info di
	ON di.delivery_id = ss.delivery_id
ORDER BY di.status, di.shipping_datetime DESC, di.arrival_datetime DESC;
