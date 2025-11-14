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
    status ENUM('Available', 'Occupied')
) AUTO_INCREMENT = 9000;

INSERT INTO Vehicles (plate_number, type, status)
VALUES
    ('ABC123', 'Car', 'Available'),
    ('XYZ789', 'Motor', 'Available'),
    ('TRK456', 'Truck', 'Available'),
    ('MTR321', 'Motor', 'Available'),
    ('CAR654', 'Car', 'Available'),
    ('TRK987', 'Truck', 'Available'),
    ('MTR159', 'Motor', 'Occupied'),
    ('CAR753', 'Car', 'Occupied'),
    ('TRK852', 'Truck', 'Occupied'),
    ('CAR951', 'Car', 'Occupied'),
    ('MTR357', 'Motor', 'Occupied');

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
    
CREATE TABLE Suppliers(
	supplier_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(80) NOT NULL UNIQUE,
    phone_number VARCHAR(11) NOT NULL,
    email VARCHAR(50) UNIQUE,
    address_id INT NOT NULL,
    CONSTRAINT supplies_fk_addresses FOREIGN KEY (address_id) REFERENCES Addresses(address_id)
) AUTO_INCREMENT = 10010;

INSERT INTO Suppliers (company_name, phone_number, email, address_id)
VALUES
	('AMD Authorized Distributor', '09171234567', 'sales@amdpartners.com', 20001),
	('Intel Technology Supplier', '09181234567', 'info@intel-supplier.com', 20002),
	('Corsair & G.SKILL Memory Distributors', '09192345678', 'contact@memorydist.com', 20003),
	('Gigabyte Authorized Partner', '09203456789', 'support@gigabytepartner.com', 20004),
	('ASUS Component Supplier', '09214567890', 'sales@asusparts.com', 20005),
	('ASRock and MSI Suppliers', '09225678901', 'orders@msisupplier.com', 20006),
	('Samsung Storage Solutions', '09236789012', 'samsung@storagesol.com', 20007),
	('Seagate Technology Partner', '09247890123', 'service@seagatepartner.com', 20008),
	('Crucial Memory Supplier', '09258901234', 'sales@crucialdist.com', 20009);

CREATE TABLE Products (
    product_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    brand VARCHAR(20) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    category ENUM('CPU','GPU','Motherboard','Memory (RAM)', 'Storage Device'),
    CONSTRAINT product_name_unique UNIQUE (name)
) AUTO_INCREMENT = 1001;

INSERT INTO Products (name, description, brand, price, quantity, category)
VALUES
	('ASROCK B450M-HDV R4.0 MATX Motherboard', 'Supports AMD AM4 Socket Ryzen™ 2000, 3000, 4000 G-Series, 5000 and 5000 G-Series Desktop Processors', 'ASRock', 3150.00, 40, 'Motherboard'),
    ('AMD Ryzen 5 5600X', '6-Core 12-Thread Unlocked Desktop Processor, up to 4.6GHz Max Boost', 'AMD', 11750.00, 25, 'CPU'),
	('Intel Core i7-12700K', '12-Core Processor (8P+4E), up to 5.0GHz with Intel UHD Graphics 770', 'Intel', 17500.00, 30, 'CPU'),
	('MSI B550M PRO-VDH WIFI Motherboard', 'AMD AM4, DDR4, WiFi, Bluetooth, and PCIe 4.0 ready', 'MSI', 5500.00, 20, 'Motherboard'),
	('Gigabyte AORUS GeForce RTX 3060 Elite 12G', '12GB GDDR6, RGB Fusion 2.0, Windforce 3X Cooling System', 'Gigabyte', 24999.00, 15, 'GPU'),
	('ASUS Dual GeForce RTX 4070 12GB', '12GB GDDR6X, Axial-tech Fans, Dual BIOS', 'ASUS', 39999.00, 10, 'GPU'),
	('Corsair Vengeance LPX 16GB (2x8GB) DDR4 3200MHz', 'High-performance memory optimized for AMD and Intel', 'Corsair', 2899.00, 50, 'Memory (RAM)'),
	('G.SKILL Trident Z RGB 32GB (2x16GB) DDR4 3600MHz', 'RGB lighting and high performance', 'G.SKILL', 5899.00, 35, 'Memory (RAM)'),
	('Samsung 970 EVO Plus 1TB NVMe SSD', 'Up to 3500MB/s read speed, V-NAND technology', 'Samsung', 5999.00, 60, 'Storage Device'),
	('Seagate Barracuda 2TB HDD', '7200RPM SATA 6Gb/s 256MB cache, 3.5-inch internal drive', 'Seagate', 3250.00, 80, 'Storage Device'),
	('Crucial P5 Plus 2TB PCIe Gen4 NVMe SSD', 'Read speeds up to 6600MB/s, ideal for gaming and productivity', 'Crucial', 8999.00, 40, 'Storage Device');

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
	(1001, 10015, 2800.00, 50, 8001), 
	(1002, 10010, 10500.00, 30, 8002), 
	(1003, 10011, 16000.00, 25, 8003), 
	(1004, 10015, 5000.00, 40, 8004), 
	(1005, 10013, 22500.00, 15, 8005), 
	(1006, 10014, 37000.00, 12, 8006), 
	(1007, 10012, 2600.00, 60, 8007),
	(1008, 10012, 5300.00, 40, 8008), 
	(1009, 10016, 5200.00, 70, 8009), 
	(1010, 10017, 2900.00, 90, 8010), 
	(1011, 10018, 8200.00, 45, 8011);

CREATE TABLE Customers (
    customer_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(11) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, 
    address_id INT NOT NULL,
    CONSTRAINT customers_fk_addresses FOREIGN KEY (address_id) REFERENCES Addresses(address_id)
) AUTO_INCREMENT = 6001;

INSERT INTO Customers (first_name, last_name, email, phone_number, password, address_id)
VALUES
	('Allysa', 'Chong', 'allysa_chong@gmail.com', '09123456789', 'RandomPass', 20010),
    ('Fiona', 'Maningas', 'fiona_maningas@gmail.com', '09173456789', 'PassPass', 20011);

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

CREATE TABLE Order_Details(
	order_detail_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    product_id INT NOT NULL,
    order_id INT NOT NULL,
    CONSTRAINT orderdetails_fk_products FOREIGN KEY (product_id) REFERENCES Products(product_id),
    CONSTRAINT orderdetails_fk_orders FOREIGN KEY (order_id) REFERENCES Orders(order_id)
) AUTO_INCREMENT = 3000;

CREATE TABLE Shipping(
	shipping_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    delivery_id INT NOT NULL,
    CONSTRAINT shipping_fk_orders FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    CONSTRAINT shipping_fk_delivery_info FOREIGN KEY (delivery_id) REFERENCES Delivery_Info(delivery_id)
) AUTO_INCREMENT = 2000;

CREATE TABLE Returns (
    return_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_detail_id INT NOT NULL,
    product_quantity INT NOT NULL,
    return_date DATETIME NOT NULL,
    return_reason VARCHAR(255),
    is_resellable BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT returns_fk_order_details FOREIGN KEY (order_detail_id) REFERENCES Order_Details(order_detail_id)
) AUTO_INCREMENT = 9000;

