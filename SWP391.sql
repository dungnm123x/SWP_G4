
USE [master]
GO

/*******************************************************************************
   Drop database if it exists
********************************************************************************/

IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'SWP391')
BEGIN
	ALTER DATABASE [SWP391] SET OFFLINE WITH ROLLBACK IMMEDIATE;
	ALTER DATABASE [SWP391] SET ONLINE;
	DROP DATABASE [SWP391];
END

GO
CREATE DATABASE [SWP391]
GO
USE  [SWP391]

GO
CREATE TABLE Role (
    RoleID INT PRIMARY KEY IDENTITY(1,1),
    RoleName NVARCHAR(255) NOT NULL
);


CREATE TABLE [User] (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    Username NVARCHAR(255) NOT NULL UNIQUE,
    Password NVARCHAR(255) NOT NULL,
    FullName NVARCHAR(255) NOT NULL,
    Email NVARCHAR(255) UNIQUE,
    PhoneNumber NVARCHAR(20) UNIQUE,
	Address NVARCHAR(255) NULL,
    RoleID INT,
    FOREIGN KEY (RoleID) REFERENCES Role(RoleID),  
);

CREATE TABLE Station (
    StationID INT PRIMARY KEY IDENTITY(1,1),
    StationName NVARCHAR(255) NOT NULL,
    Address NVARCHAR(255) NOT NULL
);

CREATE TABLE Train (
    TrainID INT PRIMARY KEY IDENTITY(1,1),
    TrainName NVARCHAR(255) NOT NULL,
);


CREATE TABLE Carriage (
    CarriageID INT PRIMARY KEY IDENTITY(1,1),
    CarriageNumber INT NOT NULL,
    CarriageType NVARCHAR(50) NOT NULL,
    TrainID INT,
    Capacity INT NOT NULL,
    CONSTRAINT FK_Carriage_Train FOREIGN KEY (TrainID) REFERENCES Train(TrainID)
);

CREATE TABLE Seat (
    SeatID INT PRIMARY KEY IDENTITY(1,1),
    SeatNumber INT NOT NULL,
    Status NVARCHAR(50) NOT NULL CHECK (Status IN ('Available', 'Booked', 'Reserved', 'Out of Service')),
    SeatType NVARCHAR(50) NOT NULL,
    CarriageID INT,
    CONSTRAINT FK_Seat_Carriage FOREIGN KEY (CarriageID) REFERENCES Carriage(CarriageID)
);


CREATE TABLE Route (
    RouteID INT PRIMARY KEY IDENTITY(1,1),
    DepartureStationID INT,
    ArrivalStationID INT,
    Distance INT NOT NULL,
    BasePrice DECIMAL(10,2) NOT NULL,
    CONSTRAINT FK_Route_DepartureStation FOREIGN KEY (DepartureStationID) REFERENCES Station(StationID),
    CONSTRAINT FK_Route_ArrivalStation FOREIGN KEY (ArrivalStationID) REFERENCES Station(StationID)
);


CREATE TABLE Trip (
    TripID INT PRIMARY KEY IDENTITY(1,1),
    TrainID INT,
    RouteID INT,
    DepartureTime DATETIME NOT NULL,
    ArrivalTime DATETIME NOT NULL,
    TripStatus NVARCHAR(50) NOT NULL CHECK (TripStatus IN ('Scheduled', 'Completed', 'Delayed')),
    CONSTRAINT FK_Trip_Train FOREIGN KEY (TrainID) REFERENCES Train(TrainID),
    CONSTRAINT FK_Trip_Route FOREIGN KEY (RouteID) REFERENCES Route(RouteID)
);


CREATE TABLE Booking (
    BookingID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT,
    BookingDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    TotalPrice DECIMAL(10,2) NOT NULL,
    PaymentStatus NVARCHAR(50) NOT NULL CHECK (PaymentStatus IN ('Pending', 'Paid', 'Cancelled')),
    BookingStatus NVARCHAR(50) NOT NULL CHECK (BookingStatus IN ('Active', 'Expired')),
    CONSTRAINT FK_Booking_User FOREIGN KEY (UserID) REFERENCES [User](UserID)
);


CREATE TABLE Ticket (
    TicketID INT PRIMARY KEY IDENTITY(1,1),
    CCCD NVARCHAR(20) NOT NULL,
    BookingID INT,
    SeatID INT,
    TripID INT,
    TicketPrice DECIMAL(10,2) NOT NULL,
    TicketStatus NVARCHAR(50) NOT NULL CHECK (TicketStatus IN ('Used', 'Unused', 'Refunded')),
    CONSTRAINT FK_Ticket_Booking FOREIGN KEY (BookingID) REFERENCES Booking(BookingID),
    CONSTRAINT FK_Ticket_Seat FOREIGN KEY (SeatID) REFERENCES Seat(SeatID),
    CONSTRAINT FK_Ticket_Trip FOREIGN KEY (TripID) REFERENCES Trip(TripID)
);


CREATE TABLE OrderDetail (
    OrderID INT PRIMARY KEY IDENTITY(1,1),
    TicketID INT,
    UserID INT,
    SeatID INT,
    TripID INT,
    OrderDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    OrderStatus NVARCHAR(50) NOT NULL CHECK (OrderStatus IN ('Pending', 'Completed', 'Cancelled')),
    CONSTRAINT FK_OrderDetail_Ticket FOREIGN KEY (TicketID) REFERENCES Ticket(TicketID),
    CONSTRAINT FK_OrderDetail_User FOREIGN KEY (UserID) REFERENCES [User](UserID),
    CONSTRAINT FK_OrderDetail_Seat FOREIGN KEY (SeatID) REFERENCES Seat(SeatID),
    CONSTRAINT FK_OrderDetail_Trip FOREIGN KEY (TripID) REFERENCES Trip(TripID)
);
Go
-- Thêm dữ liệu cho bảng Role
INSERT INTO Role (RoleName) VALUES ('Admin');
INSERT INTO Role (RoleName) VALUES ('Employer');
INSERT INTO Role (RoleName) VALUES ('Customer');

-- Thêm dữ liệu cho bảng User
-- Người dùng với vai trò Admin
INSERT INTO [User] (Username, Password, FullName, Email, PhoneNumber,Address, RoleID) 
VALUES ('admin', 'admin123', 'Admin User', 'admin@example.com', '0123456789','Vinh Phuc', 1),
('customer', 'customer123', 'Customer User', 'customer@example.com', '0987654321','Ha Noi', 3),
('employer', 'employer123', 'Employer User', 'employer@example.com', '0912345678','Hai Phong', 2);

-- Thêm dữ liệu cho bảng Station
INSERT INTO Station (StationName, Address) 
VALUES (N'Ga Hà Nội', N'120 Lê Duẩn, Hoàn Kiếm, Hà Nội'),
       (N'Ga Sài Gòn',N'1 Nguyễn Thông, Phường 9, Quận 3, TP. Hồ Chí Minh'),
       (N'Ga Đà Nẵng', N'202 Hải Phòng, Quận Thanh Khê, Đà Nẵng'),
       (N'Ga Nha Trang', N'17 Thái Nguyên, Phường Phước Tân, Nha Trang, Khánh Hòa'),
       (N'Ga Huế', N'2 Bùi Thị Xuân, Phường Phú Thuận, TP. Huế, Thừa Thiên Huế'),
       (N'Ga Vinh', N'1 Lê Lợi, TP. Vinh, Nghệ An'),
       (N'Ga Đồng Hới', N'Đường Lý Thường Kiệt, Đồng Hới, Quảng Bình'),
       (N'Ga Quảng Ngãi', N'Lê Lợi, TP. Quảng Ngãi, Quảng Ngãi');

-- Thêm dữ liệu cho bảng Train
INSERT INTO Train (TrainName) VALUES
(N'SE1'),
(N'SE2'),
(N'NA1'),
(N'SPT1'),
(N'DN1');


-- Thêm dữ liệu cho bảng Carriage (10 dòng)
INSERT INTO Carriage (CarriageNumber, CarriageType, TrainID, Capacity) VALUES
-- Toa của tàu SE1
(1, N'Toa VIP', 1, 12),
(2, N'Toa thường', 1, 10),
(3, N'Toa thường', 1, 10),

-- Toa của tàu SE2
(1, N'Toa thường', 2, 10),
(2, N'Toa thường', 2, 10),
(3, N'Toa thường', 2, 10),

-- Toa của tàu NA1
(1, N'Toa thường', 3, 10),
(2, N'Toa VIP', 3, 12),
(3, N'Toa thường', 3, 10),

-- Toa của tàu SPT1
(1, N'Toa thường', 4, 10),
(2, N'Toa VIP', 4, 12),
(3, N'Toa thường', 4, 10),

-- Toa của tàu DN1
(1, N'Toa thường', 5, 10),
(2, N'Toa VIP', 5, 12),
(3, N'Toa thường', 5, 10);

-- Thêm dữ liệu cho bảng Seat (10 dòng)
-- Giả sử CarriageID tự tăng từ 1 đến 15 theo thứ tự đã chèn trước đó

-- Toa VIP (12 chỗ)
INSERT INTO Seat (SeatNumber, Status, SeatType, CarriageID) 
SELECT 
    number, 'Available', N'VIP', CarriageID 
FROM Carriage, (SELECT TOP 12 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS number FROM master.dbo.spt_values) AS Numbers 
WHERE CarriageType = N'Toa VIP';

-- Toa thường (10 chỗ)
INSERT INTO Seat (SeatNumber, Status, SeatType, CarriageID) 
SELECT 
    number, 'Available', N'Thường', CarriageID 
FROM Carriage, (SELECT TOP 10 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS number FROM master.dbo.spt_values) AS Numbers 
WHERE CarriageType = N'Toa thường';

-- Thêm dữ liệu cho bảng Route (10 dòng)
INSERT INTO Route (DepartureStationID, ArrivalStationID, Distance, BasePrice) 
VALUES 
(1, 2, 150, 20.00),
(2, 3, 100, 15.00),
(3, 4, 200, 30.00),
(4, 5, 180, 25.00),
(5, 6, 220, 35.00),
(6, 7, 160, 20.00),
(7, 8, 140, 18.00),
(8, 1, 190, 28.00); -- Sửa ArrivalStationID để đảm bảo tồn tại


-- Thêm dữ liệu cho bảng Trip (10 dòng)
INSERT INTO Trip (TrainID, RouteID, DepartureTime, ArrivalTime, TripStatus) 
VALUES 
(1, 1, '2025-02-15 08:00:00', '2025-02-15 10:00:00', 'Scheduled'),
(2, 2, '2025-02-16 09:00:00', '2025-02-16 11:00:00', 'Scheduled'),
(3, 3, '2025-02-17 07:00:00', '2025-02-17 09:00:00', 'Scheduled'),
(4, 4, '2025-02-18 10:00:00', '2025-02-18 12:00:00', 'Delayed'),
(5, 5, '2025-02-19 06:30:00', '2025-02-19 08:30:00', 'Completed'),
(1, 6, '2025-02-20 07:45:00', '2025-02-20 09:45:00', 'Scheduled'),
(2, 7, '2025-02-21 08:15:00', '2025-02-21 10:15:00', 'Scheduled'),
(3, 8, '2025-02-22 09:30:00', '2025-02-22 11:30:00', 'Completed');




-- Thêm dữ liệu cho bảng Booking (10 dòng)
INSERT INTO Booking (UserID, TotalPrice, PaymentStatus, BookingStatus) 
VALUES 
(2, 40.00, 'Paid', 'Active'),
(3, 30.00, 'Pending', 'Active'),
(2, 50.00, 'Paid', 'Expired');

-- Thêm dữ liệu cho bảng Ticket (10 dòng)
INSERT INTO Ticket (CCCD, BookingID, SeatID, TripID, TicketPrice, TicketStatus) 
VALUES 
('123456789012', 1, 1, 1, 20.00, 'Unused'),
('987654321098', 2, 2, 2, 30.00, 'Used');

-- Thêm dữ liệu cho bảng OrderDetail
INSERT INTO OrderDetail (TicketID, UserID, SeatID, TripID, OrderStatus) 
VALUES 
(1, 2, 1, 1, 'Pending'),
(2, 3, 2, 2, 'Completed');


