USE [master]
GO

/*******************************************************************************  
   Drop database if it exists  
********************************************************************************/  
IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'SWP391')  
BEGIN  
    ALTER DATABASE [SWP391] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;  
    DROP DATABASE [SWP391];  
END  
GO  

-- Tạo database mới
CREATE DATABASE [SWP391]  
GO  
USE [SWP391]  
GO  

/*******************************************************************************  
   Tạo bảng dữ liệu  
********************************************************************************/  

-- Bảng Role
CREATE TABLE Role (  
    RoleID INT PRIMARY KEY IDENTITY(1,1),  
    RoleName NVARCHAR(255) NOT NULL  
);  

-- Bảng User
CREATE TABLE [User] (  
    UserID INT PRIMARY KEY IDENTITY(1,1),  
    Username NVARCHAR(255) NOT NULL UNIQUE,  
    Password NVARCHAR(255) NOT NULL,  
    FullName NVARCHAR(255) NOT NULL,  
    Email NVARCHAR(255) UNIQUE,  
    PhoneNumber NVARCHAR(20) UNIQUE,  
    Address NVARCHAR(255) NULL,  
    RoleID INT,  
    Status BIT NOT NULL DEFAULT 1,  
    FOREIGN KEY (RoleID) REFERENCES Role(RoleID)  
);  

CREATE TABLE Feedback (
    FeedbackID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT, -- Link to the user who gave feedback
    Content NVARCHAR(MAX) NOT NULL,
    Rating INT,   -- You can add a rating system (e.g., 1-5 stars)
    FeedbackDate DATETIME DEFAULT GETDATE(),
    Status BIT NOT NULL DEFAULT 1, -- e.g., to manage visibility
    FOREIGN KEY (UserID) REFERENCES [User](UserID)
);
-- Bảng CalendarEvent để lưu trữ các sự kiện lịch
CREATE TABLE CalendarEvent (
    EventID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    Title NVARCHAR(255) NOT NULL,
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NULL, -- Có thể để NULL nếu sự kiện kéo dài cả ngày hoặc không có thời gian kết thúc cụ thể
    AllDay BIT NOT NULL DEFAULT 0, -- 1 nếu sự kiện kéo dài cả ngày, 0 nếu có thời gian cụ thể
    Description NVARCHAR(MAX) NULL, -- Mô tả chi tiết về sự kiện (tùy chọn)
    Status BIT NOT NULL DEFAULT 1, -- Trạng thái sự kiện (1: Active, 0: Deleted)
    FOREIGN KEY (UserID) REFERENCES [User](UserID)
);
GO

-- Trigger mã hóa mật khẩu trước khi insert
GO  
CREATE TRIGGER EncryptPasswordBeforeInsert  
ON [User]  
FOR INSERT  
AS  
BEGIN  
    UPDATE [User]  
    SET Password = CONVERT(NVARCHAR(255), HASHBYTES('SHA2_256', INSERTED.Password), 2)  
    FROM INSERTED  
    WHERE [User].UserID = INSERTED.UserID;  
END;  
GO  

-- Bảng Station  
CREATE TABLE Station (  
    StationID INT PRIMARY KEY IDENTITY(1,1),  
    StationName NVARCHAR(255) NOT NULL,  
    Address NVARCHAR(255) NOT NULL  
);  
CREATE TABLE StationCoordinates (
    StationID INT PRIMARY KEY,
    Latitude DECIMAL(9, 6) NOT NULL, -- Vĩ độ
    Longitude DECIMAL(9, 6) NOT NULL, -- Kinh độ
    FOREIGN KEY (StationID) REFERENCES Station(StationID)
);
-- Bảng Train  
CREATE TABLE Train (  
    TrainID INT PRIMARY KEY IDENTITY(1,1),  
    TrainName NVARCHAR(255) NOT NULL  
);  

-- Bảng Carriage  
CREATE TABLE Carriage (  
    CarriageID INT PRIMARY KEY IDENTITY(1,1),  
    CarriageNumber INT NOT NULL,  
    CarriageType NVARCHAR(50) NOT NULL,  
    TrainID INT,  
    Capacity INT NOT NULL DEFAULT 1,  
    FOREIGN KEY (TrainID) REFERENCES Train(TrainID)  
);  

-- Bảng Seat  
CREATE TABLE Seat (  
    SeatID INT PRIMARY KEY IDENTITY(1,1),  
    SeatNumber INT NOT NULL,  
    Status NVARCHAR(50) NOT NULL CHECK (Status IN ('Available', 'Booked', 'Reserved', 'Out of Service')),  
    SeatType NVARCHAR(50) NOT NULL,  
    CarriageID INT,  
    FOREIGN KEY (CarriageID) REFERENCES Carriage(CarriageID)  
);  

-- Bảng Route  
CREATE TABLE Route (  
    RouteID INT PRIMARY KEY IDENTITY(1,1),  
    DepartureStationID INT,  
    ArrivalStationID INT,  
    Distance INT NOT NULL,  
    BasePrice DECIMAL(10,2) NOT NULL,  
    FOREIGN KEY (DepartureStationID) REFERENCES Station(StationID),  
    FOREIGN KEY (ArrivalStationID) REFERENCES Station(StationID)  
);  

-- Bảng Trip với TripType  
CREATE TABLE Trip (
    TripID INT PRIMARY KEY IDENTITY(1,1),
    TrainID INT,
    RouteID INT,
    DepartureTime DATETIME NOT NULL,
    ArrivalTime DATETIME NOT NULL,
    TripStatus NVARCHAR(50) NOT NULL CHECK (TripStatus IN ('Scheduled', 'Departed', 'Arrived', 'Cancelled')),
    TripType INT NOT NULL DEFAULT 1 CHECK (TripType IN (1,2)),  -- 1=One-way, 2=Round-trip
    RoundTripReference INT NULL,
    FOREIGN KEY (TrainID) REFERENCES Train(TrainID),
    FOREIGN KEY (RouteID) REFERENCES Route(RouteID),
    FOREIGN KEY (RoundTripReference) REFERENCES Trip(TripID)
);


-- Bảng Booking  
CREATE TABLE Booking (  
    BookingID INT PRIMARY KEY IDENTITY(1,1),  
    UserID INT,  
    TripID INT Null,  
    RoundTripTripID INT NULL,  
    BookingDate DATETIME DEFAULT CURRENT_TIMESTAMP,  
    TotalPrice DECIMAL(10,2) NOT NULL,  
    PaymentStatus NVARCHAR(50) NOT NULL CHECK (PaymentStatus IN ('Refund', 'Paid', 'Cancelled')),  
    BookingStatus NVARCHAR(50) NOT NULL CHECK (BookingStatus IN ('Active', 'Expired')),  
    FOREIGN KEY (UserID) REFERENCES [User](UserID),
    FOREIGN KEY (TripID) REFERENCES Trip(TripID),
    FOREIGN KEY (RoundTripTripID) REFERENCES Trip(TripID)
);

CREATE TABLE Refund ( 
    RefundID INT PRIMARY KEY IDENTITY(1,1),
	UserID INT,
	BankAccountID NVARCHAR(50) Null, 
	BankName NVARCHAR(50) Null,
	RefundDate DATETIME DEFAULT CURRENT_TIMESTAMP,
	ConfirmRefundDate DATETIME Null,
	TotalRefund DECIMAL(20,2) NOT NULL,
	RefundStatus NVARCHAR(50) NOT NULL CHECK (RefundStatus IN ('Complete', 'Wait')),
	FOREIGN KEY (UserID) REFERENCES [User](UserID)
);
-- Bảng Ticket  
CREATE TABLE Ticket (  
    TicketID INT PRIMARY KEY IDENTITY(1,1), 
	RefundID INT Null,
	PassengerName NVARCHAR(255),
	PassengerType NVARCHAR(50),
    CCCD NVARCHAR(20) NOT NULL,  
    BookingID INT,  
    SeatID INT,  
    TripID INT,
    TicketPrice DECIMAL(10,2) NOT NULL,  
    TicketStatus NVARCHAR(50) NOT NULL CHECK (TicketStatus IN ('Used', 'Unused', 'Refunded')),  
    FOREIGN KEY (BookingID) REFERENCES Booking(BookingID),  
    FOREIGN KEY (SeatID) REFERENCES Seat(SeatID),  
    FOREIGN KEY (TripID) REFERENCES Trip(TripID) ,
	FOREIGN KEY (RefundID) REFERENCES Refund(RefundID)
);  
CREATE TABLE [dbo].[CategoryBlog](
    [categoryBlog_id] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
      [categoryBlogName] [nvarchar](100) NULL,
    [status] [bit] NULL
);
-- Bảng Blog  
CREATE TABLE [dbo].[Blog](
    [blog_id] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [title] [nvarchar](max) NULL,
    [UserID] [int] NULL,
    [update_date] [date] NULL,
    [content] [nvarchar](max) NULL,
    [thumbnail] [nvarchar](max) NULL,
    [brief_infor] [nvarchar](max) NULL,
    [categoryBlog_id] [int] NULL,
    [status] [bit] NULL,
    FOREIGN KEY ([UserID]) REFERENCES [User]([UserID]),
    FOREIGN KEY ([categoryBlog_id]) REFERENCES [CategoryBlog]([categoryBlog_id])
);



CREATE TABLE CategoryRule (
    CategoryRuleID INT PRIMARY KEY IDENTITY(1,1),
    CategoryRuleName NVARCHAR(255) NOT NULL UNIQUE,
    Content NVARCHAR(MAX) NOT NULL,
    Img NVARCHAR(255) NULL,
    Update_Date date Not null,
    Status BIT NOT NULL DEFAULT 1
);
	
CREATE TABLE [Rule] (
    RuleID INT PRIMARY KEY IDENTITY(1,1),
    Title NVARCHAR(255) NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    Img NVARCHAR(255) NULL,
    Update_Date date Not Null,
    Status BIT NOT NULL DEFAULT 1,
    UserID INT,
    CategoryRuleID INT,
    FOREIGN KEY (UserID) REFERENCES [User](UserID),
    FOREIGN KEY (CategoryRuleID) REFERENCES CategoryRule(CategoryRuleID)
);
GO 
-- Thêm dữ liệu cho bảng Role
INSERT INTO Role (RoleName) VALUES ('Admin');
INSERT INTO Role (RoleName) VALUES ('Employer');
INSERT INTO Role (RoleName) VALUES ('Customer');

-- Thêm dữ liệu cho bảng User
-- Người dùng với vai trò Admin
INSERT INTO [User] (Username, Password, FullName, Email, PhoneNumber,Address, RoleID, Status) 
VALUES ('admin', 'admin123', N'Admin', 'admin@example.com', '0123456789', N'Lạng Sơn', 1,1),
('customer', 'customer123', N'Tuấn Anh', 'Hyun@gmail.com', '0980654321',N'Hà Nội', 3,1),
('dungdung', '123456@', N'Mạnh Dũng', 'dungnmhe173094@fpt.edu.vn', '0866435003','Vinh Phuc', 3,1),
('employer', 'employer123', 'Nhân viên 0', 'Nv00@gmail.com', '0912345678', N'Sài Gòn', 2,1),
('employee1', 'employee123', N'Nhân viên 1', 'Nv01@gmail.com', '0778901234', N'Hưng Yên', 2, 1),
('employee2', 'employee123', N'Nhân viên 2', 'Nv02@gmail.com', '0987654021', N'Hà Nội', 2, 1),
('employee3', 'employee123', N'Nhân viên 3', 'Nv03@gmail.com', '0948271036', N'Hải Phòng', 2, 1),
('customer4', 'customer123', N'Anh Long', 'jda89@gmail.com', '0973501246', N'Huế', 3, 1),
('customer5', 'customer123', N'Ông Bảy', 'ronaldinho@gmail.com', '0918726453', N'Nha Trang', 3, 1),
('customer6', 'customer123', N'Chú Mười', 'mess3@gmail.com', '0965432109', N'Hải Dương', 3, 1),
('customer7', 'customer123', N'Phương Tuấn', 'j97@gmail.com', '0932185764', N'Bến Tre', 3, 1),
('customer8', 'customer123', N'Quang Huy', 'huyhuyhuyhu1910@gmail.com', '0991023456', N'Hà Nội', 3, 1),
('customer9', 'customer123', N'Ông Liêm', 'MrLiem03@gmail.com', '0956781234', N'Bắc Ninh', 3, 1),
('customer10', 'customer123', N'Tuấn Hưng', 'Hung@gmail.com', '0923467810', N'Hà Nội', 3, 1),
('customer11', 'customer123', N'Khánh Phương', 'Phuong0132@gmail.com', '0909876543', N'Hà Nội', 3, 1),
('customer12', 'customer123', N'Sơn Tùng', 'TungST25@gmail.com', '0945132078', N'Thái Bình', 3, 1),
('customer13', 'customer123', N'Phương Ly', 'LyPH@gmail.com', '0971029384', N'Thanh Hóa', 3, 1);
-- Insert additional data into the Feedback table
INSERT INTO Feedback (UserID, Content, Rating, FeedbackDate, Status)
VALUES
(2, N'Dịch vụ đặt vé trực tuyến rất tiện lợi, giao diện thân thiện và dễ sử dụng. Tuy nhiên, tôi mong muốn có thêm tùy chọn thanh toán qua ví điện tử khác ngoài các phương thức hiện có.', 4, '2025-03-20 10:30:00', 1),
(3, N'Chuyến tàu SE1 từ Hà Nội đến Sài Gòn rất đúng giờ, nhân viên phục vụ nhiệt tình. Tuy nhiên, ghế ngồi hơi chật, cần cải thiện để thoải mái hơn trên hành trình dài.', 3, '2025-03-21 14:15:00', 1),
(9, N'Tôi rất hài lòng với chính sách hoàn trả vé rõ ràng và nhanh chóng. Đội ngũ hỗ trợ khách hàng phản hồi kịp thời khi tôi gặp vấn đề với vé.', 5, '2025-03-22 09:45:00', 1),
(10, N'Hệ thống đặt vé bị lỗi khi tôi cố gắng thanh toán bằng thẻ tín dụng, cần khắc phục để tránh gián đoạn trải nghiệm người dùng.', 2, '2025-03-23 16:20:00', 1),
(11, N'Tàu sạch sẽ, hành trình suôn sẻ, nhưng thông tin cập nhật chuyến đi trên ứng dụng chưa thực sự chi tiết. Mong có thêm thông báo thời gian thực.', 4, '2025-03-24 08:00:00', 1),
(12, N'Chuyến đi khứ hồi từ Hà Nội đến Sài Gòn rất thuận tiện, giá vé hợp lý. Tôi đánh giá cao dịch vụ chăm sóc khách hàng qua hotline.', 5, '2025-03-24 13:10:00', 1),
(13, N'Tôi gặp khó khăn khi tìm thông tin về chính sách đổi vé trên website. Cần cải thiện hướng dẫn để rõ ràng hơn cho khách hàng.', 3, '2025-03-23 11:25:00', 1),
(14, N'Dịch vụ tốt, nhưng tôi nghĩ nên có thêm ưu đãi cho khách hàng thường xuyên để tăng sự gắn kết.', 4, '2025-03-22 17:50:00', 1),
(15, N'Tàu đến trễ 30 phút so với lịch trình, cần cải thiện độ chính xác về thời gian để khách hàng tin tưởng hơn.', 2, '2025-03-21 19:00:00', 1),
(16, N'Tôi rất ấn tượng với quy trình đặt vé nhanh chóng và đội ngũ nhân viên hỗ trợ nhiệt tình. Đây là lựa chọn tuyệt vời cho các chuyến đi dài.', 5, '2025-03-20 15:35:00', 1);


-- Insert sample data into the CalendarEvent table
INSERT INTO CalendarEvent (UserID, Title, StartDate, EndDate, AllDay, Description, Status)
VALUES
    -- Event 1: Full-day event for Admin
    (1, N'Họp Ban Quản Lý', '2025-03-25 00:00:00', NULL, 1, 
     N'Cuộc họp toàn thể ban quản lý để thảo luận kế hoạch vận hành quý 2 năm 2025.', 1),

    -- Event 2: Specific time event for Customer
    (2, N'Đặt vé tàu Hà Nội - Sài Gòn', '2025-03-26 09:00:00', '2025-03-26 10:00:00', 0, 
     N'Nhắc nhở đặt vé tàu cho chuyến đi công tác từ Hà Nội đến Sài Gòn.', 1),

    -- Event 3: Multi-day event for Employer
    (4, N'Tập huấn nhân viên', '2025-03-27 08:00:00', '2025-03-28 17:00:00', 0, 
     N'Chương trình tập huấn kỹ năng phục vụ khách hàng cho nhân viên mới.', 1),

    -- Event 4: Full-day event for Customer
    (3, N'Ngày nghỉ lễ', '2025-04-01 00:00:00', NULL, 1, 
     N'Ngày nghỉ lễ quốc gia, không có lịch tàu hoạt động.', 1),

    -- Event 5: Specific time event for Admin
    (1, N'Kiểm tra hệ thống đặt vé', '2025-03-29 14:00:00', '2025-03-29 16:00:00', 0, 
     N'Kiểm tra và bảo trì hệ thống đặt vé trực tuyến để đảm bảo hoạt động ổn định.', 1),

    -- Event 6: Full-day event for Customer
    (9, N'Khám phá Đà Nẵng', '2025-03-30 00:00:00', NULL, 1, 
     N'Lên kế hoạch du lịch Đà Nẵng bằng tàu hỏa.', 1),

    -- Event 7: Specific time event for Employer
    (5, N'Giao ca trực ga Hà Nội', '2025-03-31 07:00:00', '2025-03-31 15:00:00', 0, 
     N'Lịch giao ca trực tại ga Hà Nội cho nhân viên.', 1),

    -- Event 8: Multi-day event for Customer
    (10, N'Chuyến đi khứ hồi Nha Trang', '2025-04-02 08:00:00', '2025-04-04 20:00:00', 0, 
     N'Lịch trình chuyến đi khứ hồi từ Sài Gòn đến Nha Trang và ngược lại.', 1),

    -- Event 9: Specific time event for Admin
    (1, N'Đánh giá hiệu suất nhân viên', '2025-04-05 09:00:00', '2025-04-05 11:00:00', 0, 
     N'Cuộc họp đánh giá hiệu suất làm việc của nhân viên trong tháng 3.', 1);
GO

-- Thêm dữ liệu cho bảng Station
INSERT INTO Station (StationName, Address) 
VALUES (N'Ga Hà Nội', N'120 Lê Duẩn, Hoàn Kiếm, Hà Nội'),
       (N'Ga Sài Gòn',N'1 Nguyễn Thông, Phường 9, Quận 3, TP. Hồ Chí Minh'),
       (N'Ga Đà Nẵng', N'202 Hải Phòng, Quận Thanh Khê, Đà Nẵng'),
       (N'Ga Nha Trang', N'17 Thái Nguyên, Phường Phước Tân, Nha Trang, Khánh Hòa'),
       (N'Ga Huế', N'2 Bùi Thị Xuân, Phường Phú Thuận, TP. Huế, Thừa Thiên Huế'),
       (N'Ga Vinh', N'1 Lê Lợi, TP. Vinh, Nghệ An'),
       (N'Ga Đồng Hới', N'Đường Lý Thường Kiệt, Đồng Hới, Quảng Bình'),
       (N'Ga Quảng Ngãi', N'Lê Lợi, TP. Quảng Ngãi, Quảng Ngãi'),
       (N'Ga Thanh Hóa', N'19 Dương Đình Nghệ, Phường Đông Thọ, TP. Thanh Hóa, Thanh Hóa'),
       (N'Ga Hải Phòng', N'75 Lương Khánh Thiện, Quận Ngô Quyền, TP. Hải Phòng'),
       (N'Ga Nam Định', N'267 Trần Hưng Đạo, TP. Nam Định, Nam Định'),
       (N'Ga Lạng Sơn', N'Đường Trần Đăng Ninh, TP. Lạng Sơn, Lạng Sơn'),
       (N'Ga Bắc Giang', N'Đường Nguyễn Văn Cừ, TP. Bắc Giang, Bắc Giang');
	   -- Ví dụ thêm tọa độ cho các ga (thay thế bằng tọa độ chính xác)
INSERT INTO StationCoordinates (StationID, Latitude, Longitude) VALUES
(1, 21.0285, 105.8542), -- Ga Hà Nội
(2, 10.7769, 106.6959), -- Ga Sài Gòn
(3, 16.0544, 108.2022), -- Ga Đà Nẵng
(4, 12.2406, 109.1967), -- Ga Nha Trang
(5, 16.4633, 107.5909), -- Ga Huế
(6, 18.5802, 105.6728), -- Ga Vinh
(10, 20.8591, 106.6859), -- Ga Hải Phòng
(13, 21.2693, 106.1896); -- Ga Bắc Giang

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

INSERT INTO Route (DepartureStationID, ArrivalStationID, Distance, BasePrice) 
VALUES 
(1, 2, 2440, 1100000.00),
(2,1,2440,1100000.00),
(2, 3, 935, 450000.00),
(3, 4, 523, 340000.00),
(4, 5, 627, 360000.00),
(5, 6, 369, 150000.00),
(6, 7, 203, 100000.00),
(7, 8, 406, 280000.00),
(8, 1, 928, 725000.00),
(8, 9, 753, 420000.00),
(9, 10, 250, 180000.00),
(10, 11, 88, 100000.00),
(11, 12, 218, 120000.00),
(12, 13, 100, 100000.00),
(13, 8, 942, 470000.00),
(9, 12, 317, 260000.00);

-- Thêm dữ liệu cho bảng Trip (10 dòng)
-- Thêm chuyến đi một chiều

-- Cập nhật RoundTripReference cho chuyến đi khứ hồi (chuyến về tham chiếu chuyến đi)

DECLARE @CurrentDate DATE = '2025-03-06';
DECLARE @EndDate    DATE = '2025-03-30';

WHILE @CurrentDate <= @EndDate
BEGIN
    -- 1) Thêm chuyến “đi”
    INSERT INTO Trip (TrainID, RouteID, DepartureTime, ArrivalTime, TripStatus, TripType)
    VALUES (
        1,  -- TrainID
        1,  -- RouteID
        DATEADD(HOUR, 8, CAST(@CurrentDate AS DATETIME)),  -- 8h sáng
        DATEADD(HOUR, 10, CAST(@CurrentDate AS DATETIME)), -- 10h
        'Scheduled',
        2   -- Round-trip
    );
    DECLARE @TripID_Go INT = SCOPE_IDENTITY();

    -- 2) Thêm chuyến “về”
    INSERT INTO Trip (TrainID, RouteID, DepartureTime, ArrivalTime, TripStatus, TripType)
    VALUES (
        2,  -- TrainID
        2,  -- RouteID
        DATEADD(HOUR, 18, CAST(@CurrentDate AS DATETIME)),  -- 18h
        DATEADD(HOUR, 20, CAST(@CurrentDate AS DATETIME)),  -- 20h
        'Scheduled',
        2   -- Round-trip
    );
    DECLARE @TripID_Return INT = SCOPE_IDENTITY();

    -- 3) Update RoundTripReference
    UPDATE Trip
    SET RoundTripReference = @TripID_Return
    WHERE TripID = @TripID_Go;

    UPDATE Trip
    SET RoundTripReference = @TripID_Go
    WHERE TripID = @TripID_Return;

    -- Tăng ngày
    SET @CurrentDate = DATEADD(DAY, 1, @CurrentDate);
END;




-- Thêm dữ liệu cho bảng Booking (10 dòng)
DECLARE @OutboundBookingID INT, @ReturnBookingID INT;

BEGIN TRANSACTION;




    -- Cập nhật RoundTripTripID cho cả 2 bản ghi để tham chiếu lẫn nhau
    UPDATE Booking
    SET RoundTripTripID = CASE
                            WHEN BookingID = @OutboundBookingID THEN @ReturnBookingID
                            WHEN BookingID = @ReturnBookingID THEN @OutboundBookingID
                          END
    WHERE BookingID IN (@OutboundBookingID, @ReturnBookingID);

COMMIT TRANSACTION;
INSERT INTO Trip (TrainID, RouteID, DepartureTime, ArrivalTime, TripStatus, TripType) 
VALUES 
(1, 2, '2025-03-18 08:30:00', '2025-03-18 10:30:00', 'Scheduled', 1),
(2, 1, '2025-03-18 09:15:00', '2025-03-18 11:45:00', 'Scheduled', 1),
(3, 4, '2025-03-18 10:00:00', '2025-03-18 12:00:00', 'Scheduled', 1),
(4, 5, '2025-03-18 11:45:00', '2025-03-18 14:15:00', 'Scheduled', 1),
(5, 6, '2025-03-18 13:00:00', '2025-03-18 15:30:00', 'Scheduled', 1),
(1, 6, '2025-03-18 14:20:00', '2025-03-18 17:20:00', 'Scheduled', 1),
(2, 7, '2025-03-18 15:10:00', '2025-03-18 18:10:00', 'Scheduled', 1),
(3, 8, '2025-03-18 16:00:00', '2025-03-18 19:00:00', 'Scheduled', 1),
(4, 9, '2025-03-18 17:30:00', '2025-03-18 20:00:00', 'Scheduled', 1),
(5, 10, '2025-03-18 18:45:00', '2025-03-18 21:15:00', 'Scheduled', 1),
(1, 11, '2025-03-18 19:20:00', '2025-03-18 22:20:00', 'Scheduled', 1),
(2, 12, '2025-03-18 20:00:00', '2025-03-18 23:00:00', 'Scheduled', 1),
(3, 13, '2025-03-18 21:15:00', '2025-03-18 23:45:00', 'Scheduled', 1),
(4, 14, '2025-03-18 22:30:00', '2025-03-18 23:30:00', 'Scheduled', 1),
(5, 15, '2025-03-18 08:45:00', '2025-03-18 11:45:00', 'Scheduled', 1),
(1, 10, '2025-03-18 19:45:00', '2025-03-18 22:45:00', 'Scheduled', 1),
(2, 11, '2025-03-18 20:30:00', '2025-03-18 23:30:00', 'Scheduled', 1),
(3, 12, '2025-03-18 21:00:00', '2025-03-18 23:00:00', 'Scheduled', 1),
(4, 13, '2025-03-18 22:15:00', '2025-03-18 23:45:00', 'Scheduled', 1),
(5, 14, '2025-03-18 08:15:00', '2025-03-18 11:15:00', 'Scheduled', 1);

INSERT INTO CategoryBlog (categoryBlogName, status)  
VALUES  
(N'Tin tức ngành đường sắt', 1),  
(N'Hướng dẫn mua vé', 1),  
(N'Khuyến mãi & Ưu đãi', 1),  
(N'Cẩm nang du lịch', 1);
-- Thêm dữ liệu cho bảng Blog
INSERT INTO Blog (title, UserID, update_date, content, thumbnail, brief_infor, categoryBlog_id, status)  
VALUES  
(N'Cách đặt vé tàu online nhanh chóng', 2, '2025-02-27', 
N'<h1>Hướng Dẫn Chi Tiết C&aacute;ch Đặt V&eacute; T&agrave;u Trực Tuyến Tr&ecirc;n Website Ch&iacute;nh Thức</h1>

<p>Bạn muốn đặt v&eacute; t&agrave;u nhanh ch&oacute;ng m&agrave; kh&ocirc;ng cần phải đến trực tiếp nh&agrave; ga? Với sự ph&aacute;t triển của c&ocirc;ng nghệ, giờ đ&acirc;y bạn c&oacute; thể dễ d&agrave;ng đặt v&eacute; t&agrave;u trực tuyến chỉ với v&agrave;i thao t&aacute;c đơn giản ngay tại nh&agrave;. Trong b&agrave;i viết n&agrave;y, ch&uacute;ng t&ocirc;i sẽ hướng dẫn bạn từng bước để đặt v&eacute; t&agrave;u một c&aacute;ch nhanh ch&oacute;ng, tiện lợi v&agrave; an to&agrave;n.</p>

<h2>1. Truy Cập Website Đặt V&eacute; Ch&iacute;nh Thức :Online Booking Ticket Train</h2>

<p>Trước ti&ecirc;n, bạn cần truy cập v&agrave;o trang web ch&iacute;nh thức của online booking ticket train. Đ&acirc;y l&agrave; những trang web cung cấp th&ocirc;ng tin ch&iacute;nh x&aacute;c về lịch tr&igrave;nh t&agrave;u, gi&aacute; v&eacute;, v&agrave; c&aacute;c ch&iacute;nh s&aacute;ch hỗ trợ h&agrave;nh kh&aacute;ch.</p>

<h2>2. Chọn Chuyến Đi Ph&ugrave; Hợp</h2>

<p>Sau khi truy cập v&agrave;o website, bạn cần thực hiện c&aacute;c bước sau:</p>

<ul>
	<li>
	<p><strong>Nhập th&ocirc;ng tin h&agrave;nh tr&igrave;nh:</strong> Chọn ga đi, ga đến, ng&agrave;y khởi h&agrave;nh v&agrave; số lượng h&agrave;nh kh&aacute;ch.</p>
	</li>
	<li>
	<p><strong>Lựa chọn loại t&agrave;u v&agrave; ghế ngồi:</strong> Hệ thống sẽ hiển thị c&aacute;c chuyến t&agrave;u c&oacute; sẵn, bao gồm giờ khởi h&agrave;nh, loại ghế (ghế cứng, ghế mềm, giường nằm), v&agrave; gi&aacute; v&eacute; tương ứng.</p>
	</li>
	<li>
	<p><strong>Kiểm tra lại th&ocirc;ng tin:</strong> Trước khi tiếp tục, h&atilde;y chắc chắn rằng bạn đ&atilde; chọn đ&uacute;ng chuyến t&agrave;u theo nhu cầu.</p>
	</li>
</ul>

<h2>3. Nhập Th&ocirc;ng Tin H&agrave;nh Kh&aacute;ch</h2>

<p>Để ho&agrave;n tất việc đặt v&eacute;, bạn cần nhập đầy đủ v&agrave; ch&iacute;nh x&aacute;c th&ocirc;ng tin của h&agrave;nh kh&aacute;ch đi t&agrave;u:</p>

<ul>
	<li>
	<p><strong>Họ v&agrave; t&ecirc;n đầy đủ</strong></p>
	</li>
	<li>
	<p><strong>Số CMND/CCCD hoặc hộ chiếu</strong></p>
	</li>
	<li>
	<p><strong>Số điện thoại li&ecirc;n hệ</strong></p>
	</li>
	<li>
	<p><strong>Email để nhận th&ocirc;ng tin v&eacute;</strong></p>
	</li>
</ul>

<p>Việc nhập đ&uacute;ng th&ocirc;ng tin rất quan trọng để tr&aacute;nh sai s&oacute;t khi nhận v&eacute; điện tử hoặc l&agrave;m thủ tục l&ecirc;n t&agrave;u.</p>

<h2>4. Thanh To&aacute;n V&eacute; T&agrave;u</h2>

<p>Sau khi nhập th&ocirc;ng tin h&agrave;nh kh&aacute;ch, bạn sẽ tiến h&agrave;nh thanh to&aacute;n. Hiện nay, c&aacute;c trang đặt v&eacute; trực tuyến hỗ trợ nhiều phương thức thanh to&aacute;n tiện lợi như:</p>

<ul>
	<li>
	<p><strong>Thanh to&aacute;n bằng thẻ ng&acirc;n h&agrave;ng (ATM nội địa, Visa, Mastercard, JCB, v.v.)</strong></p>
	</li>
	<li>
	<p><strong>V&iacute; điện tử (MoMo, ZaloPay, ViettelPay, v.v.)</strong></p>
	</li>
	<li>
	<p><strong>Chuyển khoản ng&acirc;n h&agrave;ng</strong></p>
	</li>
</ul>

<p>Hệ thống sẽ tự động x&aacute;c nhận thanh to&aacute;n v&agrave; gửi v&eacute; điện tử về email hoặc tin nhắn SMS sau khi giao dịch ho&agrave;n tất.</p>

<h2>5. Nhận V&eacute; V&agrave; Kiểm Tra Lại Th&ocirc;ng Tin</h2>

<p>Sau khi thanh to&aacute;n th&agrave;nh c&ocirc;ng, bạn sẽ nhận được v&eacute; điện tử dưới dạng file PDF hoặc m&atilde; QR. Bạn n&ecirc;n:</p>

<ul>
	<li>
	<p>Kiểm tra kỹ lại th&ocirc;ng tin tr&ecirc;n v&eacute; (họ t&ecirc;n, số ghế, giờ khởi h&agrave;nh, ga đi - ga đến).</p>
	</li>
	<li>
	<p>Lưu lại v&eacute; tr&ecirc;n điện thoại hoặc in ra giấy để sử dụng khi l&ecirc;n t&agrave;u.</p>
	</li>
</ul>

<h2>6. L&ecirc;n T&agrave;u V&agrave; H&agrave;nh Tr&igrave;nh</h2>

<p>V&agrave;o ng&agrave;y khởi h&agrave;nh, bạn chỉ cần đến ga trước giờ t&agrave;u chạy &iacute;t nhất 30 ph&uacute;t để l&agrave;m thủ tục. Khi l&ecirc;n t&agrave;u, nh&acirc;n vi&ecirc;n sẽ kiểm tra v&eacute; điện tử của bạn bằng c&aacute;ch qu&eacute;t m&atilde; QR hoặc kiểm tra th&ocirc;ng tin tr&ecirc;n hệ thống.</p>

<h2>Lời Kết</h2>

<p>Việc đặt v&eacute; t&agrave;u trực tuyến kh&ocirc;ng chỉ gi&uacute;p tiết kiệm thời gian m&agrave; c&ograve;n mang lại sự tiện lợi v&agrave; an to&agrave;n cho h&agrave;nh kh&aacute;ch. Chỉ cần l&agrave;m theo c&aacute;c bước tr&ecirc;n, bạn c&oacute; thể dễ d&agrave;ng sở hữu một tấm v&eacute; t&agrave;u m&agrave; kh&ocirc;ng cần phải xếp h&agrave;ng tại nh&agrave; ga. H&atilde;y tận dụng tiện &iacute;ch n&agrave;y để c&oacute; những chuyến đi thuận lợi v&agrave; thoải m&aacute;i!</p>

<p>Ch&uacute;c bạn c&oacute; một h&agrave;nh tr&igrave;nh vui vẻ v&agrave; an to&agrave;n!</p>

<p>&nbsp;</p>
', 
'img/booking.jpg', 
N'Hướng dẫn chi tiết cách đặt vé tàu online, giúp bạn tiết kiệm thời gian và đảm bảo có vé cho hành trình của mình.', 
2, 1),  

(N'Lịch trình tàu Tết 2025', 2, '2025-02-25', 
N'<h1>Cập Nhật Lịch Tr&igrave;nh T&agrave;u Tết Nguy&ecirc;n Đ&aacute;n 2025 Tr&ecirc;n Hệ Thống Đặt V&eacute; Online</h1>

<p>Tết Nguy&ecirc;n Đ&aacute;n l&agrave; thời điểm nhu cầu đi lại tăng cao, đặc biệt l&agrave; đối với những người về qu&ecirc; sum họp gia đ&igrave;nh hay đi du lịch trong dịp lễ lớn nhất năm. Để gi&uacute;p h&agrave;nh kh&aacute;ch c&oacute; kế hoạch di chuyển thuận lợi, hệ thống đặt v&eacute; trực tuyến của ch&uacute;ng t&ocirc;i cung cấp đầy đủ th&ocirc;ng tin về lịch tr&igrave;nh t&agrave;u Tết 2025, bao gồm c&aacute;c tuyến đường ch&iacute;nh, thời gian khởi h&agrave;nh, gi&aacute; v&eacute; v&agrave; những lưu &yacute; quan trọng.</p>

<h2>1. C&aacute;c Tuyến Đường Ch&iacute;nh V&agrave; Lịch Tr&igrave;nh T&agrave;u</h2>

<p>Hệ thống đặt v&eacute; online cung cấp nhiều tuyến t&agrave;u phục vụ dịp Tết Nguy&ecirc;n Đ&aacute;n, trong đ&oacute; c&aacute;c tuyến trọng điểm bao gồm:</p>

<h3>Tuyến Bắc - Nam (H&agrave; Nội - TP. Hồ Ch&iacute; Minh)</h3>

<ul>
	<li>
	<p><strong>SE1, SE3, SE5, SE7</strong>: Chạy suốt từ H&agrave; Nội v&agrave;o TP. Hồ Ch&iacute; Minh v&agrave; ngược lại.</p>
	</li>
	<li>
	<p><strong>Thời gian khởi h&agrave;nh</strong>: C&aacute;c chuyến t&agrave;u xuất ph&aacute;t từ 06:00 đến 23:00 hằng ng&agrave;y.</p>
	</li>
	<li>
	<p><strong>Thời gian di chuyển</strong>: Khoảng 30 - 35 giờ t&ugrave;y theo loại t&agrave;u.</p>
	</li>
</ul>

<h3>Tuyến H&agrave; Nội - Đ&agrave; Nẵng</h3>

<ul>
	<li>
	<p><strong>SE19, SE21</strong>: Phục vụ h&agrave;nh kh&aacute;ch từ H&agrave; Nội đi Đ&agrave; Nẵng v&agrave; ngược lại.</p>
	</li>
	<li>
	<p><strong>Thời gian khởi h&agrave;nh</strong>: 18:00 - 22:00.</p>
	</li>
	<li>
	<p><strong>Thời gian di chuyển</strong>: Khoảng 16 - 18 giờ.</p>
	</li>
</ul>

<h3>Tuyến TP. Hồ Ch&iacute; Minh - Nha Trang</h3>

<ul>
	<li>
	<p><strong>SNT1, SNT2</strong>: Chạy từ TP. Hồ Ch&iacute; Minh đến Nha Trang.</p>
	</li>
	<li>
	<p><strong>Thời gian khởi h&agrave;nh</strong>: 20:00 - 23:00.</p>
	</li>
	<li>
	<p><strong>Thời gian di chuyển</strong>: Khoảng 7 - 9 giờ.</p>
	</li>
</ul>

<h3>Tuyến H&agrave; Nội - L&agrave;o Cai (Sa Pa)</h3>

<ul>
	<li>
	<p><strong>SP1, SP3</strong>: Chuy&ecirc;n phục vụ du kh&aacute;ch đi du lịch Sa Pa.</p>
	</li>
	<li>
	<p><strong>Thời gian khởi h&agrave;nh</strong>: 21:30 - 22:00.</p>
	</li>
	<li>
	<p><strong>Thời gian di chuyển</strong>: Khoảng 8 - 9 giờ.</p>
	</li>
</ul>

<h2>2. Gi&aacute; V&eacute; T&agrave;u Tết 2025 Tr&ecirc;n Hệ Thống Đặt V&eacute; Online</h2>

<p>Hệ thống đặt v&eacute; trực tuyến của ch&uacute;ng t&ocirc;i cung cấp c&aacute;c mức gi&aacute; v&eacute; dao động từ <strong>200.000 - 1.000.000 VNĐ</strong>, ph&ugrave; hợp với nhiều nhu cầu kh&aacute;c nhau. Gi&aacute; v&eacute; c&oacute; thể thay đổi t&ugrave;y v&agrave;o thời điểm đặt v&eacute; v&agrave; hạng v&eacute;.</p>

<table>
	<thead>
		<tr>
			<th>Tuyến Đường</th>
			<th>Loại V&eacute;</th>
			<th>Gi&aacute; V&eacute; (VNĐ)</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>H&agrave; Nội - TP. HCM</td>
			<td>Ghế cứng</td>
			<td>600.000 - 1.000.000</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>Ghế mềm</td>
			<td>800.000 - 1.200.000</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>Giường nằm</td>
			<td>1.200.000 - 1.800.000</td>
		</tr>
		<tr>
			<td>H&agrave; Nội - Đ&agrave; Nẵng</td>
			<td>Ghế mềm</td>
			<td>500.000 - 900.000</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>Giường nằm</td>
			<td>1.000.000 - 1.500.000</td>
		</tr>
		<tr>
			<td>TP. HCM - Nha Trang</td>
			<td>Ghế mềm</td>
			<td>300.000 - 600.000</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>Giường nằm</td>
			<td>800.000 - 1.200.000</td>
		</tr>
		<tr>
			<td>H&agrave; Nội - L&agrave;o Cai</td>
			<td>Giường nằm</td>
			<td>400.000 - 900.000</td>
		</tr>
	</tbody>
</table>

<h2>3. Hướng Dẫn Đặt V&eacute; T&agrave;u Tết Tr&ecirc;n Hệ Thống Online</h2>

<p>H&agrave;nh kh&aacute;ch c&oacute; thể đặt v&eacute; dễ d&agrave;ng tr&ecirc;n hệ thống đặt v&eacute; trực tuyến của ch&uacute;ng t&ocirc;i với c&aacute;c bước đơn giản sau:</p>

<ol>
	<li>
	<p><strong>Truy cập v&agrave;o website ch&iacute;nh thức</strong> của hệ thống đặt v&eacute;.</p>
	</li>
	<li>
	<p><strong>Nhập th&ocirc;ng tin h&agrave;nh tr&igrave;nh</strong>: Ga đi, ga đến, ng&agrave;y khởi h&agrave;nh, số lượng h&agrave;nh kh&aacute;ch.</p>
	</li>
	<li>
	<p><strong>Chọn chuyến t&agrave;u v&agrave; loại v&eacute;</strong> ph&ugrave; hợp với nhu cầu.</p>
	</li>
	<li>
	<p><strong>Nhập th&ocirc;ng tin h&agrave;nh kh&aacute;ch</strong>, đảm bảo ch&iacute;nh x&aacute;c để tr&aacute;nh sai s&oacute;t.</p>
	</li>
	<li>
	<p><strong>Thanh to&aacute;n v&eacute; trực tuyến</strong> qua thẻ ng&acirc;n h&agrave;ng, v&iacute; điện tử hoặc chuyển khoản.</p>
	</li>
	<li>
	<p><strong>Nhận v&eacute; điện tử</strong> qua email hoặc tin nhắn SMS.</p>
	</li>
</ol>

<h2>4. Những Lưu &Yacute; Quan Trọng Khi Đặt V&eacute; T&agrave;u Tết</h2>

<ul>
	<li>
	<p><strong>Đặt v&eacute; sớm</strong> để tr&aacute;nh t&igrave;nh trạng hết v&eacute; hoặc tăng gi&aacute; v&agrave;o dịp cao điểm.</p>
	</li>
	<li>
	<p><strong>Kiểm tra kỹ th&ocirc;ng tin v&eacute;</strong> trước khi thanh to&aacute;n.</p>
	</li>
	<li>
	<p><strong>C&oacute; mặt tại ga &iacute;t nhất 45 ph&uacute;t</strong> trước giờ khởi h&agrave;nh để l&agrave;m thủ tục.</p>
	</li>
	<li>
	<p><strong>Mang theo giấy tờ t&ugrave;y th&acirc;n</strong> để x&aacute;c minh danh t&iacute;nh khi l&ecirc;n t&agrave;u.</p>
	</li>
</ul>

<h2>Kết Luận</h2>

<p>Hệ thống đặt v&eacute; online gi&uacute;p h&agrave;nh kh&aacute;ch đặt v&eacute; nhanh ch&oacute;ng, tiện lợi với gi&aacute; v&eacute; hợp l&yacute;. Việc nắm r&otilde; lịch tr&igrave;nh, gi&aacute; v&eacute; v&agrave; c&aacute;ch đặt v&eacute; sẽ gi&uacute;p bạn c&oacute; một chuyến đi su&ocirc;n sẻ. H&atilde;y l&ecirc;n kế hoạch sớm v&agrave; đặt v&eacute; ngay để c&oacute; một m&ugrave;a Tết vui vẻ v&agrave; an to&agrave;n b&ecirc;n gia đ&igrave;nh!</p>

<p>Ch&uacute;c bạn c&oacute; một h&agrave;nh tr&igrave;nh su&ocirc;n sẻ v&agrave; trọn vẹn!</p>
', 
'img/calendar.jpg', 
N'Thông tin chi tiết về lịch trình tàu dịp Tết 2025, giúp bạn chủ động sắp xếp kế hoạch di chuyển hợp lý.', 
1, 1),  

(N'Ưu đãi 30% khi đặt vé trước', 2, '2025-02-20', 
N'<h1>Ưu Đ&atilde;i Đặt V&eacute; T&agrave;u Trước &ndash; Giảm Gi&aacute; L&ecirc;n Đến 30%</h1>

<p>Bạn đang l&ecirc;n kế hoạch cho chuyến đi sắp tới? Đừng bỏ lỡ cơ hội nhận ưu đ&atilde;i hấp dẫn l&ecirc;n đến 30% khi đặt v&eacute; t&agrave;u trước. Chương tr&igrave;nh khuyến m&atilde;i n&agrave;y &aacute;p dụng cho nhiều tuyến đường v&agrave; c&oacute; số lượng v&eacute; giới hạn. H&atilde;y đọc ngay b&agrave;i viết để biết c&aacute;ch đặt v&eacute; v&agrave; tận dụng ưu đ&atilde;i đặc biệt n&agrave;y!</p>

<h2>1. Th&ocirc;ng Tin Chương Tr&igrave;nh Khuyến M&atilde;i</h2>

<ul>
	<li>
	<p><strong>Thời gian &aacute;p dụng</strong>: Từ ng&agrave;y 01/04/2025 đến 30/06/2025.</p>
	</li>
	<li>
	<p><strong>Mức giảm gi&aacute;</strong>: Giảm từ 10% đến 30% gi&aacute; v&eacute; t&ugrave;y v&agrave;o thời gian đặt v&eacute; trước.</p>
	</li>
	<li>
	<p><strong>Tuyến đường &aacute;p dụng</strong>: To&agrave;n bộ c&aacute;c tuyến đường sắt Bắc &ndash; Nam v&agrave; nội địa.</p>
	</li>
	<li>
	<p><strong>Loại v&eacute; &aacute;p dụng</strong>: V&eacute; ghế ngồi mềm, ghế ngồi cứng v&agrave; giường nằm.</p>
	</li>
	<li>
	<p><strong>H&igrave;nh thức đặt v&eacute;</strong>: Online qua hệ thống đặt v&eacute; ch&iacute;nh thức.</p>
	</li>
</ul>

<h2>2. C&aacute;ch Nhận Ưu Đ&atilde;i Giảm Gi&aacute;</h2>

<p>Để được hưởng ưu đ&atilde;i l&ecirc;n đến 30%, bạn cần:</p>

<ul>
	<li>
	<p><strong>Đặt v&eacute; trước &iacute;t nhất 30 ng&agrave;y</strong> so với ng&agrave;y khởi h&agrave;nh.</p>
	</li>
	<li>
	<p><strong>Chọn tuyến đường v&agrave; loại v&eacute;</strong> thuộc danh s&aacute;ch khuyến m&atilde;i.</p>
	</li>
	<li>
	<p><strong>Thanh to&aacute;n trực tuyến</strong> ngay sau khi đặt v&eacute; để x&aacute;c nhận khuyến m&atilde;i.</p>
	</li>
</ul>

<h3>Bảng Giảm Gi&aacute; Theo Thời Gian Đặt V&eacute;</h3>

<table>
	<thead>
		<tr>
			<th>Thời Gian Đặt V&eacute; Trước</th>
			<th>Mức Giảm Gi&aacute;</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>Tr&ecirc;n 30 ng&agrave;y</td>
			<td>30%</td>
		</tr>
		<tr>
			<td>20 - 29 ng&agrave;y</td>
			<td>20%</td>
		</tr>
		<tr>
			<td>10 - 19 ng&agrave;y</td>
			<td>10%</td>
		</tr>
	</tbody>
</table>

<h2>3. Hướng Dẫn Đặt V&eacute; Online</h2>

<ol>
	<li>
	<p><strong>Truy cập website đặt v&eacute; t&agrave;u ch&iacute;nh thức</strong>.</p>
	</li>
	<li>
	<p><strong>Nhập th&ocirc;ng tin chuyến đi</strong>: Ga đi, ga đến, ng&agrave;y khởi h&agrave;nh, số lượng h&agrave;nh kh&aacute;ch.</p>
	</li>
	<li>
	<p><strong>Chọn chuyến t&agrave;u v&agrave; loại v&eacute;</strong> ph&ugrave; hợp.</p>
	</li>
	<li>
	<p><strong>&Aacute;p dụng m&atilde; giảm gi&aacute;</strong> nếu c&oacute;.</p>
	</li>
	<li>
	<p><strong>Thanh to&aacute;n trực tuyến</strong> v&agrave; nhận v&eacute; điện tử qua email hoặc SMS.</p>
	</li>
</ol>

<h2>4. Những Lưu &Yacute; Quan Trọng</h2>

<ul>
	<li>
	<p><strong>Chương tr&igrave;nh chỉ &aacute;p dụng cho v&eacute; đặt trước v&agrave; kh&ocirc;ng &aacute;p dụng ho&agrave;n/hủy v&eacute;.</strong></p>
	</li>
	<li>
	<p><strong>Số lượng v&eacute; khuyến m&atilde;i c&oacute; hạn</strong>, h&atilde;y đặt sớm để đảm bảo nhận ưu đ&atilde;i.</p>
	</li>
	<li>
	<p><strong>Ưu đ&atilde;i kh&ocirc;ng &aacute;p dụng v&agrave;o c&aacute;c ng&agrave;y lễ, Tết</strong>.</p>
	</li>
	<li>
	<p><strong>Kh&aacute;ch h&agrave;ng cần nhập th&ocirc;ng tin ch&iacute;nh x&aacute;c</strong> để tr&aacute;nh lỗi khi x&aacute;c nhận v&eacute;.</p>
	</li>
</ul>

<h2>Kết Luận</h2>

<p>Đặt v&eacute; t&agrave;u sớm kh&ocirc;ng chỉ gi&uacute;p bạn tiết kiệm chi ph&iacute; m&agrave; c&ograve;n đảm bảo chỗ ngồi trong m&ugrave;a cao điểm. H&atilde;y nhanh tay tận dụng chương tr&igrave;nh ưu đ&atilde;i n&agrave;y v&agrave; l&ecirc;n kế hoạch cho chuyến đi ngay h&ocirc;m nay!</p>

<p>🚆 <strong>Đặt v&eacute; ngay để kh&ocirc;ng bỏ lỡ cơ hội!</strong> 🎟️</p>
', 
'img/sale.png', 
N'Cơ hội tiết kiệm chi phí với ưu đãi giảm giá 30% khi đặt vé tàu sớm – thông tin chi tiết trong bài viết.', 
3, 1),  

(N'Những điểm đến hấp dẫn khi đi tàu', 2, '2025-02-18', 
N'<h1>Những Điểm Đến Hấp Dẫn Khi Du Lịch Bằng T&agrave;u Hỏa</h1>

<p>Đi t&agrave;u kh&ocirc;ng chỉ l&agrave; một phương tiện di chuyển m&agrave; c&ograve;n mang lại những trải nghiệm độc đ&aacute;o khi ngắm nh&igrave;n cảnh quan tuyệt đẹp suốt h&agrave;nh tr&igrave;nh. Từ những b&atilde;i biển hoang sơ, những d&atilde;y n&uacute;i h&ugrave;ng vĩ đến c&aacute;c th&agrave;nh phố sầm uất, h&atilde;y c&ugrave;ng kh&aacute;m ph&aacute; danh s&aacute;ch những điểm đến đ&aacute;ng gh&eacute; thăm nhất khi đi t&agrave;u hỏa.</p>

<h2>1. Đ&agrave; Nẵng &ndash; Th&agrave;nh Phố Biển Xinh Đẹp</h2>

<ul>
	<li>
	<p><strong>Ga đến</strong>: Ga Đ&agrave; Nẵng</p>
	</li>
	<li>
	<p><strong>Điểm nổi bật</strong>: Biển Mỹ Kh&ecirc;, B&aacute;n đảo Sơn Tr&agrave;, Cầu Rồng, Ngũ H&agrave;nh Sơn.</p>
	</li>
	<li>
	<p><strong>L&yacute; do n&ecirc;n đi t&agrave;u</strong>: Tuyến đường sắt Bắc - Nam qua đ&egrave;o Hải V&acirc;n mang đến khung cảnh thi&ecirc;n nhi&ecirc;n tuyệt đẹp.</p>
	</li>
</ul>

<h2>2. Nha Trang &ndash; Thi&ecirc;n Đường Biển Đảo</h2>

<ul>
	<li>
	<p><strong>Ga đến</strong>: Ga Nha Trang</p>
	</li>
	<li>
	<p><strong>Điểm nổi bật</strong>: Vịnh Nha Trang, Đảo B&igrave;nh Ba, VinWonders, Th&aacute;p B&agrave; Ponagar.</p>
	</li>
	<li>
	<p><strong>L&yacute; do n&ecirc;n đi t&agrave;u</strong>: Ga Nha Trang nằm ngay trung t&acirc;m th&agrave;nh phố, thuận tiện cho việc di chuyển.</p>
	</li>
</ul>

<h2>3. Sa Pa &ndash; V&ugrave;ng Đất Mờ Sương</h2>

<ul>
	<li>
	<p><strong>Ga đến</strong>: Ga L&agrave;o Cai (sau đ&oacute; di chuyển bằng xe đến Sa Pa)</p>
	</li>
	<li>
	<p><strong>Điểm nổi bật</strong>: N&uacute;i Fansipan, Bản C&aacute;t C&aacute;t, Thung lũng Mường Hoa.</p>
	</li>
	<li>
	<p><strong>L&yacute; do n&ecirc;n đi t&agrave;u</strong>: H&agrave;nh tr&igrave;nh H&agrave; Nội - L&agrave;o Cai bằng t&agrave;u đ&ecirc;m gi&uacute;p bạn tiết kiệm thời gian v&agrave; c&oacute; cơ hội ngắm cảnh dọc tuyến.</p>
	</li>
</ul>

<h2>4. Huế &ndash; Cố Đ&ocirc; Trầm Mặc</h2>

<ul>
	<li>
	<p><strong>Ga đến</strong>: Ga Huế</p>
	</li>
	<li>
	<p><strong>Điểm nổi bật</strong>: Đại Nội Huế, Ch&ugrave;a Thi&ecirc;n Mụ, Lăng Tự Đức, S&ocirc;ng Hương.</p>
	</li>
	<li>
	<p><strong>L&yacute; do n&ecirc;n đi t&agrave;u</strong>: Cung đường đ&egrave;o Hải V&acirc;n giữa Đ&agrave; Nẵng - Huế mang lại tầm nh&igrave;n ngoạn mục.</p>
	</li>
</ul>

<h2>5. Đ&agrave; Lạt &ndash; Th&agrave;nh Phố Ng&agrave;n Hoa</h2>

<ul>
	<li>
	<p><strong>Ga đến</strong>: Ga Đ&agrave; Lạt (t&agrave;u du lịch từ Ga Trại M&aacute;t)</p>
	</li>
	<li>
	<p><strong>Điểm nổi bật</strong>: Hồ Xu&acirc;n Hương, Đồi ch&egrave; Cầu Đất, L&agrave;ng C&ugrave; Lần.</p>
	</li>
	<li>
	<p><strong>L&yacute; do n&ecirc;n đi t&agrave;u</strong>: Trải nghiệm t&agrave;u cổ Đ&agrave; Lạt tr&ecirc;n tuyến đường sắt duy nhất c&ograve;n hoạt động tại đ&acirc;y.</p>
	</li>
</ul>

<h2>6. Hội An &ndash; Phố Cổ Trầm Lắng</h2>

<ul>
	<li>
	<p><strong>Ga đến</strong>: Ga Đ&agrave; Nẵng (di chuyển th&ecirc;m 30km đến Hội An)</p>
	</li>
	<li>
	<p><strong>Điểm nổi bật</strong>: Phố cổ Hội An, Ch&ugrave;a Cầu, C&ugrave; Lao Ch&agrave;m.</p>
	</li>
	<li>
	<p><strong>L&yacute; do n&ecirc;n đi t&agrave;u</strong>: H&agrave;nh tr&igrave;nh qua đ&egrave;o Hải V&acirc;n gi&uacute;p bạn chi&ecirc;m ngưỡng vẻ đẹp thi&ecirc;n nhi&ecirc;n.</p>
	</li>
</ul>

<h2>7. Phan Thiết &ndash; Th&agrave;nh Phố Biển Y&ecirc;n B&igrave;nh</h2>

<ul>
	<li>
	<p><strong>Ga đến</strong>: Ga Phan Thiết</p>
	</li>
	<li>
	<p><strong>Điểm nổi bật</strong>: Đồi C&aacute;t Bay, Biển Mũi N&eacute;, Hải Đăng K&ecirc; G&agrave;.</p>
	</li>
	<li>
	<p><strong>L&yacute; do n&ecirc;n đi t&agrave;u</strong>: Tuyến t&agrave;u TP. Hồ Ch&iacute; Minh - Phan Thiết chỉ mất khoảng 4 giờ, nhanh ch&oacute;ng v&agrave; tiện lợi.</p>
	</li>
</ul>

<h2>Kết Luận</h2>

<p>Du lịch bằng t&agrave;u hỏa kh&ocirc;ng chỉ gi&uacute;p bạn tận hưởng h&agrave;nh tr&igrave;nh thư gi&atilde;n m&agrave; c&ograve;n đưa bạn đến những điểm đến tuyệt vời. H&atilde;y l&ecirc;n kế hoạch ngay h&ocirc;m nay để c&oacute; chuyến đi đ&aacute;ng nhớ!</p>

<p>🚆 <strong>Đặt v&eacute; t&agrave;u ngay để kh&aacute;m ph&aacute; những điểm đến hấp dẫn n&agrave;y!</strong></p>
', 
'img/anh6.jpg', 
N'Danh sách những điểm du lịch hấp dẫn khi đi tàu – từ thiên nhiên hùng vĩ đến thành phố sôi động.', 
4, 1);  
-- Cập nhật bảng CategoryRule với nội dung chi tiết hơn
INSERT INTO CategoryRule (CategoryRuleName, Content, Img, Update_Date, Status)
VALUES 
(N'Các Điều Kiện & Điều Khoản', 
 N'<p style="margin-left:48px; text-align:center"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="color:red">C&aacute;c Điều Kiện &amp; Điều Khoản</span></strong></span></span></span></p>

<p style="margin-left:48px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt"><span style="color:#333333">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span></strong><strong><span style="font-size:14pt"><span style="color:#333333">Quy định vận chuyển</span></span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt"><span style="color:#333333">Đường sắt Việt Nam &aacute;p dụng một số quy định vận chuyển để bảo đảm an to&agrave;n v&agrave; tiện &iacute;ch cho h&agrave;nh kh&aacute;ch đi t&agrave;u. H&agrave;nh kh&aacute;ch vui l&ograve;ng xem chi tiết&nbsp;</span></span><a href="http://localhost:9999/SWP391_G4/rule-list" style="color:#0563c1; text-decoration:underline" target="_parent"><strong><span style="font-size:14pt"><span style="color:#166987">tại đ&acirc;y</span></span></strong></a><span style="font-size:14pt"><span style="color:#333333">.</span></span></span></span></span></span></p>

<p style="margin-left:48px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt"><span style="color:#333333">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span></strong><strong><span style="font-size:14pt"><span style="color:#333333">Điều kiện sử dụng hệ thống mua v&eacute; trực tuyến</span></span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt"><span style="color:#333333">Với Online Booking Ticket Trai, việc mua v&eacute; trực tuyến thật dễ d&agrave;ng. Chỉ với v&agrave;i nhấp chuột, h&agrave;nh kh&aacute;ch đ&atilde; c&oacute; thể l&ecirc;n kế hoạch cho chuyến đi của m&igrave;nh. H&agrave;nh kh&aacute;ch vui l&ograve;ng xem chi tiết&nbsp;</span></span><a href="http://localhost:9999/SWP391_G4/rule-list" style="color:#0563c1; text-decoration:underline" target="_parent"><strong><span style="font-size:14pt"><span style="color:#166987">tại đ&acirc;y</span></span></strong></a><span style="font-size:14pt"><span style="color:#333333">.</span></span></span></span></span></span></p>

<p style="margin-left:48px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt"><span style="color:#333333">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span></strong><strong><span style="font-size:14pt"><span style="color:#333333">Điều khoản sử dụng website online booking ticket train</span></span></strong></span></span></span></span></p>

<p style="margin-left:48px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt"><span style="color:#333333">Để đảm bảo quyền lợi cho người d&ugrave;ng website Online Booking Ticket Train, ch&uacute;ng t&ocirc;i &aacute;p dụng c&aacute;c&nbsp;</span></span><a href="http://localhost:9999/SWP391_G4/rule-list" style="color:#0563c1; text-decoration:underline" target="_parent"><strong><span style="font-size:14pt"><span style="color:#166987">điều khoản sau</span></span></strong></a><span style="font-size:14pt"><span style="color:#333333">&nbsp;đối với người d&ugrave;ng truy cập v&agrave; sử dụng Website thương mại điện tử của Online Booking Ticket Train.</span></span></span></span></span></span></p>
 Điều khoản này quy định quyền lợi và nghĩa vụ của khách hàng khi tham gia sử dụng các dịch vụ do công ty cung cấp. Những điều khoản này áp dụng đối với mọi giao dịch giữa khách hàng và công ty, bao gồm việc đăng ký tài khoản, lựa chọn dịch vụ, thanh toán, quyền và nghĩa vụ trong việc hủy hoặc hoàn lại dịch vụ. Các điều kiện này sẽ được cập nhật định kỳ và khách hàng cần đồng ý với những điều kiện mới khi tiếp tục sử dụng dịch vụ. Ngoài ra, các điều kiện áp dụng cho các chương trình khuyến mãi, giảm giá, hoặc ưu đãi đặc biệt cũng sẽ được đề cập rõ ràng trong phần này. Mọi khách hàng đều có quyền yêu cầu thông tin rõ ràng về các điều kiện, và yêu cầu hoàn lại dịch vụ nếu có bất kỳ điều gì không minh bạch.', 
 NULL, GETDATE(), 1),
(N'Phương thức thanh toán', 
 N'Phương thức thanh toán là các cách thức mà khách hàng có thể sử dụng để thực hiện thanh toán cho các dịch vụ do công ty cung cấp. Các phương thức thanh toán bao gồm thẻ tín dụng, thẻ ghi nợ, chuyển khoản qua ngân hàng, và các hệ thống thanh toán trực tuyến như PayPal, MoMo, ZaloPay, v.v. Mỗi phương thức thanh toán có ưu điểm và hạn chế riêng. Ví dụ, thanh toán qua thẻ tín dụng nhanh chóng nhưng có thể mất phí giao dịch, trong khi chuyển khoản ngân hàng có thể mất vài ngày để hoàn tất. Quy trình thanh toán cũng bao gồm các bước xác nhận đơn hàng, kiểm tra tính hợp lệ của các thông tin thanh toán, và xử lý giao dịch qua các cổng thanh toán an toàn. Khách hàng cần đảm bảo rằng thông tin thanh toán của mình là chính xác để tránh các sự cố trong quá trình giao dịch. Các điều kiện hoàn tiền, hủy giao dịch hoặc tranh chấp giao dịch sẽ được giải quyết theo quy trình rõ ràng đã quy định trong điều khoản này.', 
 NULL, GETDATE(), 1),
(N'Chính sách hoàn trả vé', 
 N'<p style="text-align:center"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:20pt">Ch&iacute;nh s&aacute;ch ho&agrave;n trả v&eacute;, đổi v&eacute;</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">I. Điều kiện ho&agrave;n trả v&eacute;, đổi v&eacute;:</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Người thực hiện giao dịch đổi v&eacute;, trả v&eacute; điện tử phải l&agrave; người mua v&eacute; điện tử hoặc l&agrave; một trong c&aacute;c h&agrave;nh kh&aacute;ch đi tầu tr&ecirc;n c&ugrave;ng một lần giao dịch mua v&eacute; điện tử. C&aacute;c v&eacute; điện tử được coi l&agrave; c&ugrave;ng một giao dịch mua v&eacute; nếu được ghi tr&ecirc;n c&ugrave;ng một h&oacute;a đơn điện tử.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">V&eacute; điện tử đi ngay kh&ocirc;ng c&oacute; th&ocirc;ng tin sẽ kh&ocirc;ng được đổi, trả lại. Trong những trường hợp t&agrave;u gặp sự cố, Online Booking Ticket Train sẽ quy định danh s&aacute;ch c&aacute;c v&eacute; đi ngay được đổi, trả tr&ecirc;n hệ thống.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Điều kiện &aacute;p dụng: H&agrave;nh kh&aacute;ch hoặc người mua v&eacute; c&oacute; quyền trả v&eacute; v&agrave; c&aacute;c dịch vụ đi k&egrave;m theo c&aacute;c quy định của ng&agrave;nh Đường sắt. Khi đổi v&eacute;, trả v&eacute; h&agrave;nh kh&aacute;ch sẽ phải ho&agrave;n trả to&agrave;n bộ c&aacute;c dịch vụ đi k&egrave;m theo v&eacute; v&agrave; chịu nộp một khoản ph&iacute; được t&iacute;nh bằng tổng ph&iacute; đổi v&eacute;, trả v&eacute; v&agrave; ph&iacute; hủy dịch vụ đi k&egrave;m v&eacute; theo quy định. H&agrave;nh kh&aacute;ch cũng c&oacute; thể hủy dịch vụ v&agrave; trả ph&iacute; hủy dịch vụ theo quy định.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đối với trẻ em vị th&agrave;nh ni&ecirc;n, chỉ người bảo l&atilde;nh mua v&eacute; mới được thực hiện đổi, trả v&eacute; tại Cửa v&eacute;. Trong trường hợp người mua l&agrave; c&ocirc;ng ty, cần cung cấp giấy giới thiệu về người thực hiện đổi, trả v&eacute; (c&oacute; đ&oacute;ng dấu) v&agrave; giấy tờ t&ugrave;y th&acirc;n để thực hiện đổi, trả v&eacute; cho v&eacute; trẻ vị th&agrave;nh ni&ecirc;n.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Người mua v&eacute; cung cấp giấy tờ t&ugrave;y th&acirc;n c&oacute; thể thực hiện đổi, trả v&eacute; cho những v&eacute; m&igrave;nh mua.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đối với c&aacute;c v&eacute; bị kh&oacute;a cấm trả tr&ecirc;n hệ thống (v&eacute; đi ngay, v&eacute; b&aacute;o mất&hellip;), trong một số trường hợp đặc biệt, Online Booking Ticket Train c&oacute; thể thiết lập để được ph&eacute;p trả v&eacute; tr&ecirc;n hệ thống.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">II. Quy định về thời gian ho&agrave;n trả v&eacute;, đổi v&eacute;:</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Qu&yacute; kh&aacute;ch đều c&oacute; quyền thay đổi hoặc chấm dứt giao dịch trước khi thanh to&aacute;n tiền v&eacute; tại c&aacute;c ga hoặc tại c&aacute;c Đại l&yacute;, bưu cục b&aacute;n v&eacute; t&agrave;u hỏa. Trường hợp đ&atilde; thanh to&aacute;n tiền v&eacute;, nếu Qu&yacute; kh&aacute;ch muốn thay đổi hoặc chấm dứt giao dịch th&igrave; thời gian trả, đổi v&eacute; &aacute;p dụng theo c&aacute;c quy định hiện h&agrave;nh của ng&agrave;nh đường sắt.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Quy định cụ thể:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">H&agrave;nh kh&aacute;ch c&oacute; quyền trả lại v&eacute;, đổi v&eacute; trước giờ t&agrave;u chạy. Doanh nghiệp quy định cụ thể mức khấu trừ tương ứng với thời gian trả lại v&eacute;, đổi v&eacute; v&agrave; c&aacute;c nội dung kh&aacute;c c&oacute; li&ecirc;n quan đến việc trả lại v&eacute;, đổi v&eacute; của h&agrave;nh kh&aacute;ch.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><em><span style="font-size:14pt">1.Thời gian trả lại v&eacute;, đổi v&eacute;:</span></em></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">&nbsp;1.1. T&agrave;u kh&aacute;ch trong nước (bao gồm t&agrave;u kh&aacute;ch Thống nhất v&agrave; t&agrave;u kh&aacute;ch Khu đoạn chạy suốt tr&ecirc;n tuyến đường sắt Bắc - Nam):</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a) Đối với v&eacute; c&aacute; nh&acirc;n: Trước giờ t&agrave;u chạy l&agrave; 12 giờ.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b) Đối với v&eacute; tập thể:&nbsp;Trước giờ t&agrave;u chạy l&agrave; 12 giờ.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c) Đối với tập thể mua v&eacute; trọn toa, trọn cụm toa xe thực hiện theo c&aacute;c thỏa thuận trong hợp đồng mua v&eacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">&nbsp;1.2. T&agrave;u Li&ecirc;n vận quốc tế: thực hiện theo quy định của Tổ chức OSZD, cụ thể:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a) Đối với v&eacute; c&aacute; nh&acirc;n: Trước giờ t&agrave;u chạy l&agrave; 12 giờ.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b) Đối với v&eacute; tập thể&nbsp;: Trước giờ t&agrave;u chạy l&agrave; 12 giờ.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c) Nếu kh&ocirc;ng tu&acirc;n thủ thời hạn tr&ecirc;n v&agrave; trong trường hợp t&agrave;u chưa chạy th&igrave; chỉ trả lại tiền v&eacute; kh&aacute;ch (c&aacute; nh&acirc;n, tập thể), kh&ocirc;ng trả lại tiền v&eacute; nằm.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">1.3. T&ugrave;y từng giai đoạn, thời điểm trong năm, C&ocirc;ng ty VTĐS sẽ điều chỉnh thời gian trả lại v&eacute;, đổi v&eacute; ph&ugrave; hợp với t&igrave;nh h&igrave;nh thực tế.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><em><span style="font-size:14pt">2. Quy định về điều kiện trả v&eacute;, đổi v&eacute;:</span></em></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">2.1. Đối với c&aacute;c t&agrave;u Thống Nhất v&agrave; t&agrave;u Khu đoạn:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">2.1.1. Trường hợp đổi v&eacute;:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- Chỉ &aacute;p dụng đổi v&eacute; đối với v&eacute; c&aacute; nh&acirc;n.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- H&agrave;nh kh&aacute;ch được đổi v&eacute; khi c&oacute; đủ c&aacute;c điều kiện sau:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">+ Thời gian đổi v&eacute; thực hiện theo quy định tại Mục 1 n&ecirc;u tr&ecirc;n;</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">+ C&oacute; c&ugrave;ng ga đi, ga đến;</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">+ Đổi v&eacute; 1 lần duy nhất;</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">+ Bố tr&iacute; được chỗ theo y&ecirc;u cầu của h&agrave;nh kh&aacute;ch;</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">+ Kh&ocirc;ng được đổi v&eacute; trong trường hợp thay đổi th&ocirc;ng tin c&aacute; nh&acirc;n ghi tr&ecirc;n v&eacute; đ&atilde; mua.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- Sau khi đ&atilde; đổi v&eacute;, nếu h&agrave;nh kh&aacute;ch c&oacute; nhu cầu trả lại v&eacute; th&igrave; mức khấu trừ trả v&eacute; đối với c&aacute;c v&eacute; đ&atilde; đổi l&agrave; 20% số tiền in tr&ecirc;n v&eacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- C&aacute;c nh&acirc;n vi&ecirc;n b&aacute;n v&eacute; khi đổi v&eacute; cho h&agrave;nh kh&aacute;ch viết th&ecirc;m th&ocirc;ng tin &quot;v&eacute; đ&atilde; đổi&quot; (đối với v&eacute; cứng) bằng b&uacute;t kh&ocirc;ng tẩy x&oacute;a được v&agrave;o sau tấm v&eacute; đ&atilde; đổi của h&agrave;nh kh&aacute;ch v&agrave; đ&oacute;ng dấu kh&aacute;ch vận.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">2.1.2. Trường hợp trả lại v&eacute;:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a) Đối với v&eacute; c&aacute; nh&acirc;n: Người mua v&eacute; hoặc người đi t&agrave;u được trả v&eacute; khi c&oacute; đủ c&aacute;c điều kiện sau:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- Đối với v&eacute; điện tử:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">+ C&oacute; cung cấp th&ocirc;ng tin: Th&ocirc;ng tin c&aacute; nh&acirc;n tr&ecirc;n giấy tờ t&ugrave;y th&acirc;n của người mua v&eacute; hoặc người đi t&agrave;u tr&ugrave;ng khớp với th&ocirc;ng tin in tr&ecirc;n Thẻ l&ecirc;n t&agrave;u v&agrave; th&ocirc;ng tin ghi nhận tr&ecirc;n hệ thống b&aacute;n v&eacute; điện tử.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">+ Kh&ocirc;ng cung cấp th&ocirc;ng tin c&aacute; nh&acirc;n (v&eacute; đi ngay): Kh&ocirc;ng được trả lại v&eacute;, trừ trường hợp khi xảy ra sự cố g&acirc;y tắc đường chạy t&agrave;u v&igrave; nguy&ecirc;n nh&acirc;n bất khả kh&aacute;ng v&agrave; ốm đau đột xuất th&igrave; c&aacute;c v&eacute; n&agrave;y được giải quyết theo quy định của Doanh nghiệp.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- Đối với v&eacute; cứng: V&eacute; c&ograve;n nguy&ecirc;n vẹn, kh&ocirc;ng bị r&aacute;ch n&aacute;t; Kh&ocirc;ng bị tẩy, x&oacute;a, sửa chữa v&agrave; c&ograve;n đủ c&aacute;c th&ocirc;ng tin tr&ecirc;n v&eacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b) Đối với v&eacute; tập thể: Tập thể được trả v&eacute; khi c&oacute; đủ c&aacute;c điều kiện sau:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- Đối với tập thể l&agrave; đơn vị, tổ chức: C&oacute; c&ocirc;ng văn đề nghị hoặc giấy giới thiệu của đơn vị, tổ chức. Người đại diện cho đơn vị, tổ chức khi đến l&agrave;m thủ tục trả v&eacute; phải mang theo giấy giới thiệu, giấy tờ t&ugrave;y th&acirc;n, danh s&aacute;ch tập thể đi t&agrave;u.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- Đối với tập thể kh&ocirc;ng phải l&agrave; đơn vị, tổ chức: Giấy tờ t&ugrave;y th&acirc;n của trưởng đo&agrave;n đại diện, danh s&aacute;ch tập thể đi t&agrave;u.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c) Đối với tập thể mua v&eacute; trọn toa, trọn cụm toa xe thực hiện theo c&aacute;c thỏa thuận trong hợp đồng mua v&eacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">2.2.&nbsp; Đối với t&agrave;u Li&ecirc;n vận quốc tế:</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">H&agrave;nh kh&aacute;ch được trả v&eacute; khi: V&eacute; c&ograve;n nguy&ecirc;n vẹn, kh&ocirc;ng bị r&aacute;ch n&aacute;t; kh&ocirc;ng bị tẩy, x&oacute;a, sửa chữa v&agrave; c&ograve;n đủ c&aacute;c th&ocirc;ng tin tr&ecirc;n v&eacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">2.3. Doanh nghiệp sẽ căn cứ t&igrave;nh h&igrave;nh thực tế để quy định thời gian đổi, trả cho từng m&aacute;c t&agrave;u, từng tuyến đường trong c&aacute;c thời điểm cụ thể.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">III. Mức ph&iacute; trả lại v&eacute;, đổi v&eacute;:</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- Ph&iacute; trả v&eacute; được x&aacute;c định căn cứ tr&ecirc;n c&aacute;c quy định về ph&iacute; trả v&eacute; của ng&agrave;nh đường sắt hoặc theo c&aacute;c quy định cụ thể đối với c&aacute;c v&eacute; mua c&oacute; &aacute;p dụng c&aacute;c chương tr&igrave;nh khuyến m&atilde;i giảm gi&aacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- Doanh nghiệp sẽ căn cứ t&igrave;nh h&igrave;nh thực tế để điều chỉnh mức ph&iacute; trả lại v&eacute;, đổi v&eacute; cho từng m&aacute;c t&agrave;u, từng tuyến đường trong c&aacute;c thời điểm cụ thể.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">- T&agrave;u Li&ecirc;n vận quốc tế thực hiện theo quy định của tổ chức Li&ecirc;n vận quốc tế OSZD.</span></span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">IV. C&aacute;ch thức lấy lại tiền đổi, trả v&eacute;:</span></strong></span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">C&oacute; hai h&igrave;nh thức lấy lại tiền đổi, trả v&eacute;: Tiền mặt, chuyển về t&agrave;i khoản thanh to&aacute;n.</span></span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">1. Trường hợp khi mua v&eacute; trực tiếp tại c&aacute;c điểm b&aacute;n v&eacute; hoặc mua v&eacute; qua Website v&agrave; thanh to&aacute;n trả sau bằng tiền mặt tại c&aacute;c điểm thanh to&aacute;n (ATM/Internet Banking/Mobile Banking, nộp tiền mặt tại ga, tại c&aacute;c Đại l&yacute; b&aacute;n v&eacute; t&agrave;u hỏa, tại c&aacute;c Bưu cục (VNPOST), c&aacute;c điểm giao dịch của Ng&acirc;n h&agrave;ng VIB, qua c&aacute;c tiện &iacute;ch bằng ứng dụng PAYOO,&hellip; hoặc nộp tiền mặt tại c&aacute;c đại l&yacute; thu hộ ủy quyền của Đường sắt Việt Nam), khi thực hiện việc đổi, trả v&eacute; sẽ được nhận tiền mặt trực tiếp tại c&aacute;c điểm b&aacute;n v&eacute;&nbsp; tại ga hoặc đại l&yacute; (căn cứ quy định cụ thể của ng&agrave;nh đường sắt).</span></span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">2. Trường hợp khi mua v&eacute; v&agrave; thanh to&aacute;n bằng h&igrave;nh thức thanh to&aacute;n trực tuyến:</span></span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">2.1. Thanh to&aacute;n qua c&aacute;c cổng thanh to&aacute;n trực tuyến: Napas, Ng&acirc;n lượng, Payoo, VnPay, ZaloPay, MoMo, Epay v&agrave; ShopeePay: Tiền chuyển về t&agrave;i khoản đ&atilde; thanh to&aacute;n khi mua v&eacute;.</span></span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đối với giao dịch ho&agrave;n tiền thẻ quốc tế: Sau khi VTĐS thực hiện thao t&aacute;c ho&agrave;n tiền tr&ecirc;n cổng trước 17h chiều h&agrave;ng ng&agrave;y, ng&agrave;y h&ocirc;m sau NHTT (ng&acirc;n h&agrave;ng trung t&acirc;m) sẽ đối chiếu số liệu v&agrave; tổng hợp dữ liệu của ng&agrave;y h&ocirc;m trước v&agrave; gửi đi TCTQT (tổ chức thẻ quốc tế), tại thời điểm n&agrave;y hệ thống sẽ tự động tr&iacute;ch nợ khoản tiền của VTĐS. Sau khoảng thời gian T+4 kể từ ng&agrave;y gửi dữ liệu đi, tiền sẽ được ho&agrave;n về ph&iacute;a đầu NHPH (ng&acirc;n h&agrave;ng ph&aacute;t h&agrave;nh). Tuy nhi&ecirc;n, thời gian NHPH ho&agrave;n tiền cho Kh&aacute;ch h&agrave;ng l&agrave;&nbsp; t&ugrave;y theo ch&iacute;nh s&aacute;ch của từng ng&acirc;n h&agrave;ng.</span></span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đối với giao dịch ho&agrave;n tiền thẻ nội địa: Sau khi VTĐS thực hiện ho&agrave;n tiền v&agrave;o ng&agrave;y h&ocirc;m trước, ng&agrave;y h&ocirc;m sau tiền đ&atilde; được tr&iacute;ch nợ từ VTĐS để ho&agrave;n về ph&iacute;a ng&acirc;n h&agrave;ng ph&aacute;t h&agrave;nh. Tại thời điểm trước ng&agrave;y 1/10/2017, thời gian NHPH ho&agrave;n tiền cho Kh&aacute;ch h&agrave;ng l&agrave; t&ugrave;y theo ch&iacute;nh s&aacute;ch của từng Ng&acirc;n h&agrave;ng, mới đ&acirc;y (ng&agrave;y 1/10/2017) theo quy định TCTV (tổ chức th&agrave;nh vi&ecirc;n)&nbsp; mới giữa Napas v&agrave; NHPH th&igrave; NHPH sẽ phải ho&agrave;n tiền cho KH trong v&ograve;ng 2 ng&agrave;y kể từ ng&agrave;y nhận được tiền ho&agrave;n.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><em><u><span style="font-size:14pt">Lưu &yacute;:</span></u></em><em>&nbsp;</em><span style="font-size:14pt">Thẻ Quốc tế: T&ugrave;y từng ng&acirc;n h&agrave;ng ph&aacute;t h&agrave;nh thẻ c&oacute; ng&acirc;n h&agrave;ng sẽ trừ v&agrave;o kỳ sao k&ecirc; của th&aacute;ng kế tiếp, c&oacute; ng&acirc;n h&agrave;ng sẽ th&ocirc;ng b&aacute;o bằng tin nhắn số tiền ho&agrave;n trả.</span></span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">2.2. Thanh to&aacute;n qua c&aacute;c v&iacute; điện tử: Vimo, Momo, VNPay, ZaloPay &hellip;: Tiền chuyển về t&agrave;i khoản của kh&aacute;ch h&agrave;ng tr&ecirc;n v&iacute; điện tử.</span></span></span></span></p>
.', 
 NULL, GETDATE(), 1),
(N'Chính sách Bảo Mật Thông Tin', 
 N'<p style="text-align:center"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt"><span style="color:red">CH&Iacute;NH S&Aacute;CH BẢO MẬT TH&Ocirc;NG TIN KH&Aacute;CH H&Agrave;NG CỦA TỔNG C&Ocirc;NG TY ĐƯỜNG SẮT VIỆT NAM</span></span></strong></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">I.&nbsp;</span></strong><strong><span style="font-size:14pt">Giới thiệu chung về Đường sắt Việt Nam:</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Tổng c&ocirc;ng ty Đường sắt Việt Nam l&agrave; c&ocirc;ng ty tr&aacute;ch nhiệm hữu hạn một th&agrave;nh vi&ecirc;n do Nh&agrave; nước đầu tư 100% vốn điều lệ, hoạt động ph&ugrave; hợp với Luật đường sắt, Luật doanh nghiệp, Luật quản l&yacute;, sử dụng vốn, c&aacute;c quy định của ph&aacute;p luật c&oacute; li&ecirc;n quan v&agrave; Điều lệ n&agrave;y, c&oacute; trụ sở tại số 118 đường L&ecirc; Duẩn, quận Ho&agrave;n Kiếm, th&agrave;nh phố H&agrave; Nội (sau đ&acirc;y gọi tắt l&agrave; Đường sắt Việt Nam). Điện thoại: 024.39425972; fax: 024.39422866.</span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">II.&nbsp;</span></strong><strong><span style="font-size:14pt">Cam kết của Đường sắt Việt Nam về bảo vệ th&ocirc;ng tin c&aacute; nh&acirc;n</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Ch&iacute;nh s&aacute;ch bảo mật n&agrave;y c&ocirc;ng bố c&aacute;ch thức m&agrave; Tổng c&ocirc;ng ty Đường sắt Việt Nam (Sau đ&acirc;y gọi tắt l&agrave; &ldquo; Đường sắt Việt Nam&rdquo; hoặc &ldquo;Ch&uacute;ng t&ocirc;i&rdquo;) thu thập, lưu trữ v&agrave; xử l&yacute; th&ocirc;ng tin hoặc dữ liệu c&aacute; nh&acirc;n (&ldquo;Th&ocirc;ng tin c&aacute; nh&acirc;n&rdquo;) của c&aacute;c Kh&aacute;ch h&agrave;ng của m&igrave;nh th&ocirc;ng qua wesite www.dsvn.vn. Ch&uacute;ng t&ocirc;i cam kết sẽ bảo mật c&aacute;c Th&ocirc;ng tin c&aacute; nh&acirc;n của Kh&aacute;ch h&agrave;ng, sẽ nỗ lực hết sức v&agrave; sử dụng c&aacute;c biện ph&aacute;p th&iacute;ch hợp để c&aacute;c th&ocirc;ng tin m&agrave; Kh&aacute;ch h&agrave;ng cung cấp cho ch&uacute;ng t&ocirc;i trong qu&aacute; tr&igrave;nh sử dụng website n&agrave;y được bảo mật v&agrave; bảo vệ khỏi sự truy cập tr&aacute;i ph&eacute;p.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Tuy nhi&ecirc;n, Đường sắt Việt Nam kh&ocirc;ng đảm bảo ngăn chặn được tất cả c&aacute;c truy cập tr&aacute;i ph&eacute;p. Trong trường hợp truy cập tr&aacute;i ph&eacute;p nằm ngo&agrave;i khả năng kiểm so&aacute;t của ch&uacute;ng t&ocirc;i, Đường sắt Việt Nam sẽ kh&ocirc;ng chịu tr&aacute;ch nhiệm dưới bất kỳ h&igrave;nh thức n&agrave;o đối với bất kỳ khiếu nại, tranh chấp hoặc thiệt hại n&agrave;o ph&aacute;t sinh từ hoặc li&ecirc;n quan đến truy cập tr&aacute;i ph&eacute;p đ&oacute;. Kh&aacute;ch h&agrave;ng được khuyến nghị để nắm r&otilde; những quyền lợi của m&igrave;nh khi sử dụng c&aacute;c dịch vụ của Đường sắt Việt Nam được cung cấp tr&ecirc;n website n&agrave;y. Đường sắt Việt Nam đưa ra c&aacute;c cam kết dưới đ&acirc;y ph&ugrave; hợp với c&aacute;c quy định của ph&aacute;p luật Việt Nam, trong đ&oacute; bao gồm c&aacute;c c&aacute;ch thức m&agrave; ch&uacute;ng t&ocirc;i sử dụng để bảo mật th&ocirc;ng tin của Kh&aacute;ch h&agrave;ng.</span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">III.&nbsp;</span></strong><strong><span style="font-size:14pt">Mục đ&iacute;ch thu thập Th&ocirc;ng tin c&aacute; nh&acirc;n</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đường sắt Việt Nam thu thập Th&ocirc;ng tin c&aacute; nh&acirc;n của Kh&aacute;ch h&agrave;ng cho một hoặc một số mục đ&iacute;ch như sau:</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. Thực hiện v&agrave; quản l&yacute; việc đặt chỗ v&agrave; xuất v&eacute; cho Kh&aacute;ch h&agrave;ng (bao gồm cả đặt chỗ trực tuyến);</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Thực hiện v&agrave; quản l&yacute; việc sử dụng dịch vụ vận chuyển h&agrave;ng h&oacute;a;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c. Thực hiện v&agrave; quản l&yacute; hoạt động tiếp thị, cung cấp th&ocirc;ng tin khuyến mại tới Kh&aacute;ch h&agrave;ng như gửi c&aacute;c cập nhật mới nhất về th&ocirc;ng tin khuyến mại v&agrave; ch&agrave;o gi&aacute; mới li&ecirc;n quan đến sản phẩm v&agrave; dịch vụ của ch&uacute;ng t&ocirc;i;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">d. Cung cấp giải ph&aacute;p n&acirc;ng cấp hoặc thay đổi dịch vụ nhằm phục vụ nhu cầu Kh&aacute;ch h&agrave;ng;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">e. Quản l&yacute;, ph&acirc;n t&iacute;ch, đ&aacute;nh gi&aacute; số liệu để x&acirc;y dựng ch&iacute;nh s&aacute;ch b&aacute;n v&agrave; ch&iacute;nh s&aacute;ch phục vụ Kh&aacute;ch h&agrave;ng ph&ugrave; hợp;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">f. Tiếp nhận th&ocirc;ng tin, g&oacute;p &yacute;, đề xuất, khiếu nại của Kh&aacute;ch h&agrave;ng nhằm cải thiện chất lượng dịch vụ của Đường sắt Việt Nam;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">g. Li&ecirc;n hệ với Kh&aacute;ch h&agrave;ng để giải quyết c&aacute;c y&ecirc;u cầu của Kh&aacute;ch h&agrave;ng;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">h. Đảm bảo an ninh, an to&agrave;n đường sắt v&agrave; n&acirc;ng cao t&iacute;nh an to&agrave;n đối với c&aacute;c giao dịch thanh to&aacute;n trực tuyến.</span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">IV.&nbsp;</span></strong><strong><span style="font-size:14pt">Loại th&ocirc;ng tin thu thập</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Những loại Th&ocirc;ng tin c&aacute; nh&acirc;n m&agrave; Đường sắt Việt Nam thu thập từ Kh&aacute;ch h&agrave;ng của m&igrave;nh bao gồm:</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. Th&ocirc;ng tin c&aacute; nh&acirc;n như họ v&agrave; t&ecirc;n, ng&agrave;y sinh, số chứng minh nh&acirc;n d&acirc;n, số hộ chiếu hoặc giấy tờ x&aacute;c minh nh&acirc;n th&acirc;n kh&aacute;c;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Th&ocirc;ng tin li&ecirc;n lạc như số điện thoại, địa chỉ email, số fax;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c. Th&ocirc;ng tin về thanh to&aacute;n nếu thanh to&aacute;n bằng thẻ t&iacute;n dụng hoặc thẻ ghi nợ như t&ecirc;n chủ thẻ, số thẻ, ng&agrave;y hết hạn;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">d. Th&ocirc;ng tin về doanh nghiệp của Kh&aacute;ch h&agrave;ng như t&ecirc;n doanh nghiệp, địa chỉ doanh nghiệp, chức danh;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">e. C&aacute;c th&ocirc;ng tin kh&aacute;c phục vụ Chương tr&igrave;nh Kh&aacute;ch h&agrave;ng thường xuy&ecirc;n như địa chỉ nh&agrave; ri&ecirc;ng, số điện thoại di động, địa chỉ email c&aacute; nh&acirc;n, th&oacute;i quen, sở th&iacute;ch v&agrave; c&aacute;c th&ocirc;ng tin li&ecirc;n quan đến c&aacute;c nhu cầu đặc biệt của Kh&aacute;ch h&agrave;ng.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">f. C&aacute;c Th&ocirc;ng tin c&aacute; nh&acirc;n tr&ecirc;n được y&ecirc;u cầu kh&aacute;c nhau đối với những dịch vụ cụ thể, bao gồm những th&ocirc;ng tin bắt buộc phải cung cấp hoặc t&ugrave;y chọn. Kh&aacute;ch h&agrave;ng c&oacute; quyền từ chối hoặc kh&ocirc;ng cung cấp đầy đủ c&aacute;c th&ocirc;ng tin được y&ecirc;u cầu. Trong trường hợp đ&oacute;, Đường sắt Việt Nam kh&ocirc;ng thể cung cấp cho Kh&aacute;ch h&agrave;ng những dịch vụ đầy đủ v&agrave; chất lượng.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">g. Để c&oacute; thể nhận được sự phục vụ, Kh&aacute;ch h&agrave;ng cần đảm bảo t&iacute;nh ch&iacute;nh x&aacute;c v&agrave; đầy đủ của c&aacute;c th&ocirc;ng tin cung cấp cho ch&uacute;ng t&ocirc;i. Nếu c&oacute; bất kỳ sự thay đổi n&agrave;o về Th&ocirc;ng tin c&aacute; nh&acirc;n Kh&aacute;ch h&agrave;ng, xin vui l&ograve;ng th&ocirc;ng b&aacute;o cho ch&uacute;ng t&ocirc;i th&ocirc;ng qua c&aacute;c h&igrave;nh thức được c&ocirc;ng bố tr&ecirc;n website.</span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">V.&nbsp;</span></strong><strong><span style="font-size:14pt">Phương ph&aacute;p thu thập th&ocirc;ng tin</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đường sắt Việt Nam thu thập c&aacute;c Th&ocirc;ng tin c&aacute; nh&acirc;n của Kh&aacute;ch h&agrave;ng th&ocirc;ng qua dịch vụ đặt v&eacute; trực tuyến hoặc c&aacute;c chương tr&igrave;nh khuyến mại cụ thể tại website dsvn.vn.</span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">VI.&nbsp;</span></strong><strong><span style="font-size:14pt">Thời gian lưu trữ th&ocirc;ng tin thu thập</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đường sắt Việt Nam sẽ lưu trữ c&aacute;c Th&ocirc;ng tin c&aacute; nh&acirc;n do Kh&aacute;ch h&agrave;ng cung cấp tr&ecirc;n c&aacute;c hệ thống nội bộ của ch&uacute;ng t&ocirc;i trong qu&aacute; tr&igrave;nh cung cấp dịch vụ cho Kh&aacute;ch h&agrave;ng hoặc cho đến khi ho&agrave;n th&agrave;nh mục đ&iacute;ch thu thập hoặc khi Kh&aacute;ch h&agrave;ng c&oacute; y&ecirc;u cầu hủy c&aacute;c th&ocirc;ng tin đ&atilde; cung cấp.</span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">VII.&nbsp;</span></strong><strong><span style="font-size:14pt">Việc c&ocirc;ng bố th&ocirc;ng tin thu thập</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="color:#000000"><span style="font-family:&quot;Times New Roman&quot;"><span style="font-size:14pt">Đường sắt Việt Nam c&oacute; thể tiết lộ dữ liệu c&aacute; nh&acirc;n cho b&ecirc;n thứ ba như: c&aacute;c nh&acirc;n vi&ecirc;n của Đường sắt Việt Nam được ph&eacute;p tiếp cận dữ liệu c&aacute; nh&acirc;n, c&aacute;c đơn vị v&agrave; c&ocirc;ng ty th&agrave;nh vi&ecirc;n trong Tổng c&ocirc;ng ty Đường sắt Việt Nam, đối t&aacute;c kinh doanh (nh&acirc;n vi&ecirc;n đại l&yacute;, c&ocirc;ng ty cho thu&ecirc; &ocirc; t&ocirc;, kh&aacute;ch sạn, c&ocirc;ng ty ph&aacute;t h&agrave;nh thẻ ng&acirc;n h&agrave;ng, v.v&hellip;), nh&agrave; cung cấp dịch vụ hoặc c&aacute;c đối t&aacute;c dịch vụ lữ h&agrave;nh nhằm thực hiện hợp đồng dịch vụ (đặt v&eacute; v&agrave; lữ h&agrave;nh, dịch vụ hỗ trợ v&agrave; chăm s&oacute;c kh&aacute;ch h&agrave;ng, chương tr&igrave;nh kh&aacute;ch h&agrave;ng thường xuy&ecirc;n, v.v&hellip;), nhằm thực hiện c&aacute;c nghĩa vụ ph&aacute;p l&yacute; của Đường sắt Việt Nam (bảo đảm an ninh v&agrave; an chạy t&agrave;u, th&ocirc;ng tin số lượng h&agrave;nh kh&aacute;ch, v.v&hellip;), thực hiện c&aacute;c quyền lợi hợp ph&aacute;p của Đường sắt Việt Nam (tiếp thị trực tiếp từ Đường sắt Việt Nam hoặc từ c&aacute;c đối t&aacute;c của Đường sắt Việt Nam&nbsp;<em>sau khi nhận được sự chấp thuận của kh&aacute;ch h&agrave;ng</em>, v.v&hellip;) v&agrave; nhằm mục đ&iacute;ch cung cấp cho Kh&aacute;ch h&agrave;ng c&aacute;c dịch vụ tốt nhất.</span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="color:#000000"><span style="font-family:&quot;Times New Roman&quot;"><span style="font-size:14pt">Ngo&agrave;i ra, khi được c&aacute;c cơ quan Nh&agrave; nước c&oacute; thẩm quyền (cơ quan hải quan, di tr&uacute;, cảnh s&aacute;t, v.v&hellip;)&nbsp; y&ecirc;u cầu, Đường sắt Việt Nam c&oacute; thể phải cung cấp th&ocirc;ng tin c&aacute; nh&acirc;n của Qu&yacute; kh&aacute;ch cho c&aacute;c cơ quan n&agrave;y v&igrave; c&aacute;c mục đ&iacute;ch an to&agrave;n an ninh quốc gia v&agrave; c&aacute;c mục đ&iacute;ch kh&aacute;c trong phạm vi được y&ecirc;u cầu hoặc&nbsp;theo luật định.</span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="color:#000000"><span style="font-family:&quot;Times New Roman&quot;"><span style="font-size:14pt">Đường sắt Việt Nam sẽ nỗ lực hết sức để đảm bảo rằng nh&acirc;n vi&ecirc;n, c&aacute;n bộ, đại l&yacute;, tư vấn hoặc c&aacute;c b&ecirc;n thứ ba kh&aacute;c được n&ecirc;u ở tr&ecirc;n tham gia v&agrave;o việc thu thập, xử l&yacute; v&agrave; cung cấp th&ocirc;ng tin c&aacute; nh&acirc;n hiểu r&otilde; tầm quan trọng v&agrave; tu&acirc;n thủ Ch&iacute;nh s&aacute;ch Bảo Mật Th&ocirc;ng Tin n&agrave;y.</span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="color:#000000"><span style="font-family:&quot;Times New Roman&quot;"><span style="font-size:14pt">Việc cung cấp th&ocirc;ng tin c&aacute; nh&acirc;n cho c&aacute;c nh&agrave; thầu phụ sẽ được thực hiện tr&ecirc;n cơ sở hợp đồng nhằm đảm bảo th&ocirc;ng tin được bảo vệ ở mức ph&ugrave; hợp với Ch&iacute;nh s&aacute;ch Bảo Mật Th&ocirc;ng Tin n&agrave;y v&agrave; ph&aacute;p luật hiện h&agrave;nh.</span></span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">VIII.&nbsp;</span></strong><strong><span style="font-size:14pt">Quyền của Kh&aacute;ch h&agrave;ng đối với c&aacute;c Th&ocirc;ng tin c&aacute; nh&acirc;n được thu thập</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Bất kỳ Kh&aacute;ch h&agrave;ng n&agrave;o tự nguyện cung cấp Th&ocirc;ng tin c&aacute; nh&acirc;n cho Đường sắt Việt Nam đều c&oacute; c&aacute;c quyền như sau:</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. Y&ecirc;u cầu xem lại c&aacute;c th&ocirc;ng tin được thu thập;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Y&ecirc;u cầu sao ch&eacute;p lại c&aacute;c th&ocirc;ng tin được thu thập;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c. Y&ecirc;u cầu chỉnh sửa, bổ sung th&ocirc;ng tin được thu thập: Kh&aacute;ch h&agrave;ng c&oacute; thể thực hiện th&ocirc;ng qua hệ thống hỗ trợ kh&aacute;ch h&agrave;ng của ch&uacute;ng t&ocirc;i, điện thoại đường d&acirc;y n&oacute;ng 19006469 hoặc Tổng đ&agrave;i hỗ trợ kh&aacute;ch h&agrave;ng khu vực miền Bắc: 1900 0109, khu vực miền Nam: 1900 1520;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">d. Y&ecirc;u cầu dừng việc thu thập th&ocirc;ng tin;</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">e. Y&ecirc;u cầu x&oacute;a c&aacute;c th&ocirc;ng tin đ&atilde; được thu thập.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">f. Kh&aacute;ch h&agrave;ng c&oacute; thể thực hiện c&aacute;c quyền tr&ecirc;n bằng c&aacute;ch gọi đến tổng đ&agrave;i hỗ trợ kh&aacute;ch h&agrave;ng hoặc li&ecirc;n hệ với ch&uacute;ng t&ocirc;i qua email (<span style="color:#0563c1"><u>hanhchinh@vr.com.vn;</u></span>&nbsp;</span><a href="mailto:cntttk.khn@gmail.com" style="color:#0563c1; text-decoration:underline"><span style="font-size:14pt">cntttk.khn@gmail.com</span></a><span style="color:#0563c1"><u>;</u></span>&nbsp;<a href="mailto:cntt-tk@saigonrailway.vn" style="color:#0563c1; text-decoration:underline"><span style="font-size:14pt">cntt-tk@saigonrailway.vn</span></a><span style="font-size:14pt">) hoặc địa chỉ li&ecirc;n lạc được c&ocirc;ng bố tr&ecirc;n website của Đường sắt Việt Nam.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">g. Trường hợp Kh&aacute;ch h&agrave;ng cung cấp cho Đường sắt Việt Nam c&aacute;c Th&ocirc;ng tin c&aacute; nh&acirc;n kh&ocirc;ng ch&iacute;nh x&aacute;c hoặc kh&ocirc;ng đầy đủ để x&aacute;c nhận được nh&acirc;n th&acirc;n Kh&aacute;ch h&agrave;ng, ch&uacute;ng t&ocirc;i kh&ocirc;ng thể bảo vệ được quyền bảo mật của Kh&aacute;ch h&agrave;ng theo quy định tr&ecirc;n.</span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">IX.&nbsp;</span></strong><strong><span style="font-size:14pt">Việc sử dụng Cookies tr&ecirc;n website của Đường sắt Việt Nam</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Cookies chỉ được Đường sắt Việt Nam sử dụng để lưu lại trạng th&aacute;i về Ng&ocirc;n ngữ v&agrave; Thị trường trong lần truy cập cuối c&ugrave;ng của Kh&aacute;ch h&agrave;ng tại m&aacute;y t&iacute;nh truy cập nhằm mục đ&iacute;ch trả lại trạng th&aacute;i n&agrave;y cho Kh&aacute;ch h&agrave;ng khi truy cập lần sau.</span></span></span></span></p>

<p style="margin-left:72px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">X.&nbsp;</span></strong><strong><span style="font-size:14pt">Việc cập nhật v&agrave; ng&ocirc;n ngữ của Ch&iacute;nh s&aacute;ch bảo mật</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đường sắt Việt Nam sẽ chỉnh sửa Ch&iacute;nh s&aacute;ch bảo mật n&agrave;y v&agrave;o bất kỳ thời điểm n&agrave;o khi cần thiết, bản Ch&iacute;nh s&aacute;ch bảo mật cập nhật sẽ được c&ocirc;ng bố tr&ecirc;n website của ch&uacute;ng t&ocirc;i v&agrave; sẽ được ghi ng&agrave;y để Kh&aacute;ch h&agrave;ng nhận biết được bản mới nhất. Theo quy định ph&aacute;p luật, ng&ocirc;n ngữ được ưu ti&ecirc;n sử dụng v&agrave; tham chiếu l&agrave; tiếng Việt. Trong trường hợp c&oacute; sự m&acirc;u thuẫn trong c&aacute;ch giải th&iacute;ch giữa bản tiếng Việt v&agrave; c&aacute;c ng&ocirc;n ngữ kh&aacute;c th&igrave; bản tiếng Việt sẽ được ưu ti&ecirc;n tham chiếu.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Ch&iacute;nh s&aacute;ch bảo mật n&agrave;y được cập nhật đến ng&agrave;y 31 th&aacute;ng 12 năm 2014.</span></span></span></span></p>
', 
 NULL, GETDATE(), 1);

-- Cập nhật bảng Rule với nội dung chi tiết hơn
INSERT INTO [Rule] (Title, Content, Img, Update_Date, Status, UserID, CategoryRuleID)
VALUES 
(N'Quy định vận chuyển', 
 N'Quy định vận chuyển được áp dụng đối với tất cả các dịch vụ vận chuyển do công ty cung cấp. Quy định này bao gồm thông tin chi tiết về các phương thức vận chuyển, từ giao hàng nhanh qua các công ty vận chuyển lớn đến các hình thức giao hàng trực tiếp tại cửa. Các khách hàng cần cung cấp thông tin chính xác về địa chỉ giao hàng và thời gian nhận hàng để đảm bảo việc vận chuyển diễn ra suôn sẻ. Ngoài ra, công ty sẽ thông báo cho khách hàng về tình trạng vận chuyển qua email hoặc SMS, bao gồm thông tin về lộ trình, thời gian dự kiến giao hàng và các thay đổi (nếu có). Trong trường hợp khách hàng không nhận được hàng trong thời gian quy định, công ty cam kết sẽ tiến hành điều tra và xử lý yêu cầu bồi thường nếu có lỗi từ phía công ty hoặc đối tác vận chuyển. Các trường hợp bất khả kháng như thời tiết xấu hoặc tình trạng giao thông tắc nghẽn sẽ được xử lý theo từng trường hợp cụ thể và có thể dẫn đến việc giao hàng bị trễ.', 
 NULL, GETDATE(), 1, 1, 1),
(N'Điều kiện sử dụng hệ thống mua vé trực tuyến', 
 N'<p style="text-align:center"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="color:red">C&aacute;c Điều Kiện &amp; Điều Khoản</span></strong></span></span></span></p>

<p style="text-align:center"><span style="font-size:16pt"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><a name="DieuKienSuDung"><strong><span style="font-size:14pt">***Điều Kiện Sử Dụng Hệ Thống Mua V&eacute; Trực Tuyến</span></strong></a><strong><span style="font-size:14pt">***</span></strong></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Phạm vi &aacute;p dụng</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Điều kiện dưới đ&acirc;y &aacute;p dụng ri&ecirc;ng cho chức năng đặt chỗ, mua v&eacute; trực tuyến tại Website. Khi sử dụng chức năng để đặt chỗ v&agrave; mua v&eacute;, Qu&yacute; kh&aacute;ch mặc nhi&ecirc;n đ&atilde; chấp thuận v&agrave; tu&acirc;n thủ tất cả c&aacute;c chỉ dẫn, điều khoản, điều kiện v&agrave; lưu &yacute; đăng tải tr&ecirc;n Website, bao gồm nhưng kh&ocirc;ng giới hạn bởi Điều kiện Sử dụng n&ecirc;u ở đ&acirc;y. Nếu Qu&yacute; kh&aacute;ch kh&ocirc;ng c&oacute; &yacute; định mua v&eacute; trực tuyến hay kh&ocirc;ng đồng &yacute; với bất kỳ điều khoản 3 hay điều kiện n&agrave;o n&ecirc;u trong Điều kiện Sử dụng, xin h&atilde;y DỪNG VIỆC SỬ DỤNG chức năng n&agrave;y.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Điều kiện sử dụng t&iacute;nh năng mua v&eacute; trực tuyến</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. T&iacute;nh năng mua v&eacute; trực tuyến chỉ sử dụng cho mục đ&iacute;ch c&aacute; nh&acirc;n v&agrave; phi thương mại. Qu&yacute; kh&aacute;ch kh&ocirc;ng được ph&eacute;p sửa đổi, sao ch&eacute;p, phổ biến, dịch chuyển, hiển thị, vận h&agrave;nh, nh&acirc;n bản, c&ocirc;ng bố, nhượng quyền, tạo c&aacute;c li&ecirc;n kết, chuyển giao hoặc tổ chức kinh doanh từ c&aacute;c th&ocirc;ng tin, phần mềm, sản phẩm hay dịch vụ c&oacute; được từ t&iacute;nh năng mua v&eacute; trực tuyến.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. T&iacute;nh năng mua v&eacute; trực tuyến sẽ trợ gi&uacute;p Qu&yacute; kh&aacute;ch tra cứu th&ocirc;ng tin v&agrave; ho&agrave;n tất c&aacute;c thao t&aacute;c mua sản phẩm dịch vụ một c&aacute;ch hợp lệ. Tuy nhi&ecirc;n nếu lạm dụng t&iacute;nh năng n&agrave;y, Qu&yacute; kh&aacute;ch c&oacute; thể sẽ bị từ chối truy cập hoặc sử dụng. Trong trường hợp c&oacute; bất kỳ thiệt hại n&agrave;o ph&aacute;t sinh do việc vi phạm Quy định sử dụng trang Web, ch&uacute;ng t&ocirc;i c&oacute; quyền đ&igrave;nh chỉ hoặc kh&oacute;a t&agrave;i khoản của Qu&yacute; kh&aacute;ch vĩnh viễn.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c. Qu&yacute; kh&aacute;ch phải khẳng định v&agrave; bảo đảm rằng qu&yacute; kh&aacute;ch đ&atilde; đủ năng lực h&agrave;nh vi d&acirc;n sự khi sử dụng chức năng mua v&eacute; trực tuyến, đảm bảo việc tu&acirc;n thủ Điều kiện Sử dụng n&ecirc;u ra ở đ&acirc;y v&agrave; c&oacute; đủ quyền v&agrave; năng lực theo qui định của ph&aacute;p luật để thực thi c&aacute;c h&agrave;nh vi li&ecirc;n quan đến việc sử dụng t&iacute;nh năng n&agrave;y. Qu&yacute; kh&aacute;ch chấp nhận nghĩa vụ về mặt t&agrave;i ch&iacute;nh đối với mọi h&agrave;nh vi sử dụng Website của ch&iacute;nh m&igrave;nh hoặc bất cứ c&aacute; nh&acirc;n hay tổ chức n&agrave;o sử dụng t&ecirc;n v&agrave; t&agrave;i khoản của qu&yacute; kh&aacute;ch. Qu&yacute; kh&aacute;ch phải tự kiểm so&aacute;t việc sử dụng t&iacute;nh năng mua v&eacute; trực tuyến sử dụng t&agrave;i khoản do Qu&yacute; kh&aacute;ch đăng k&yacute;. Qu&yacute; kh&aacute;ch phải đảm bảo rằng mọi th&ocirc;ng tin do Qu&yacute; kh&aacute;ch cung cấp khi sử dụng t&iacute;nh năng n&agrave;y l&agrave; đ&uacute;ng v&agrave; ch&iacute;nh x&aacute;c.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Quy định đặt chỗ</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. Qu&yacute; kh&aacute;ch c&oacute; thể đặt chỗ cho tối đa 10 kh&aacute;ch (kh&ocirc;ng bao gồm trẻ sơ sinh) trong mỗi lần thực hiện.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Trẻ em dưới 10 tuổi tại thời điểm khởi h&agrave;nh phải được đặt chỗ đi c&ugrave;ng người lớn.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c. Trẻ em dưới 6 tuổi: Miễn v&eacute; v&agrave; sử dụng chung chỗ của người lớn đi k&egrave;m.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">d. Trẻ em từ 6 đến dưới 10 tuổi: Giảm 25% gi&aacute; v&eacute;</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">e. Người cao tuổi từ 60 tuổi trở l&ecirc;n: Giảm 15% gi&aacute; v&eacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">f. Sinh vi&ecirc;n: Giảm 10% gi&aacute; v&eacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">g. Gi&aacute; v&eacute; tr&ecirc;n Website chỉ &aacute;p dụng cho c&aacute;c giao dịch mua v&eacute; tr&ecirc;n Website tại thời điểm mua v&eacute;.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">h. Đặt chỗ của Qu&yacute; kh&aacute;ch sẽ kh&ocirc;ng được đảm bảo đến khi thanh to&aacute;n th&agrave;nh c&ocirc;ng.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">4.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Quy định về c&aacute;c khoản ph&iacute;</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. Tổng số tiền thanh to&aacute;n đ&atilde; bao gồm c&aacute;c thuế, bảo hiểm h&agrave;nh kh&aacute;ch phải trả tr&ecirc;n to&agrave;n bộ h&agrave;nh tr&igrave;nh.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Ngo&agrave;i ra, Đường sắt Việt Nam kh&ocirc;ng chịu tr&aacute;ch nhiệm đối với c&aacute;c khoản ph&iacute; c&oacute; thể ph&aacute;t sinh theo ch&iacute;nh s&aacute;ch của ng&acirc;n h&agrave;nh ph&aacute;t h&agrave;nh thẻ.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">5.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Quy định về thanh to&aacute;n</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. Kh&aacute;ch h&agrave;ng c&oacute; thể chọn&nbsp; h&igrave;nh thức thanh to&aacute;n:&nbsp;<a href="http://localhost:9999/SWP391_G4/cancel-ticket" style="color:#0563c1; text-decoration:underline" target="_self">trực tuyến</a>&nbsp;</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Trong trường hợp thanh to&aacute;n trực tuyến, kh&aacute;ch h&agrave;ng c&oacute; thể được y&ecirc;u cầu xuất tr&igrave;nh thẻ thanh to&aacute;n c&ugrave;ng giấy tờ t&ugrave;y th&acirc;n để đối chiếu khi ra ga lấy v&eacute;. Trong trường hợp kh&ocirc;ng x&aacute;c thực được thẻ theo y&ecirc;u cầu, Qu&yacute; kh&aacute;ch sẽ phải mua v&eacute; mới hoặc bị từ chối chuy&ecirc;n chở.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c. Trong trường hợp Qu&yacute; kh&aacute;ch lựa chọn h&igrave;nh thức &quot;Trả sau&quot;, Qu&yacute; kh&aacute;ch c&oacute; thể thực hiện việc thanh to&aacute;n tại ga, bưu cục hoặc c&aacute;c đại l&yacute; của Đường sắt Việt Nam.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">6.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Quy định trả v&eacute;, hủy bỏ giao dịch</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. Qu&yacute; kh&aacute;ch đều c&oacute; quyền thay đổi hoặc chấm dứt giao dịch trước khi thanh to&aacute;n tiền v&eacute; tại c&aacute;c ga hoặc tại c&aacute;c Đại l&yacute;, bưu cục b&aacute;n v&eacute; t&agrave;u hỏa, trường hợp đ&atilde; thanh to&aacute;n tiền v&eacute; nếu Qu&yacute; kh&aacute;ch muốn thay đổi hoặc chấm dứt giao dịch th&igrave; phải chịu mức ph&iacute; trả v&eacute;, đổi v&eacute; hiện h&agrave;nh m&agrave; ng&agrave;nh đường sắt quy định. Qu&yacute; kh&aacute;ch c&oacute; thể xem quy định chi tiết về Ch&iacute;nh s&aacute;ch ho&agrave;n trả v&eacute;, đổi v&eacute;&nbsp;<a href="http://localhost:9999/SWP391_G4/categoryRule-detail?categoryRuleID=3" style="color:#0563c1; text-decoration:underline" target="_parent">tại đ&acirc;y</a></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Trong trường hợp Đường sắt Việt Nam thay đổi lịch chạy t&agrave;u, kh&aacute;ch h&agrave;ng sẽ được th&ocirc;ng b&aacute;o qua địa chỉ thư điện tử kh&aacute;ch h&agrave;ng đ&atilde; đăng k&yacute; khi mua v&eacute; tr&ecirc;n website của Online Booking Ticket Train.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">c. Để kiểm tra lại chi tiết giờ t&agrave;u chạy, Qu&yacute; kh&aacute;ch c&oacute; thể tra cứu giờ t&agrave;u tr&ecirc;n website Online Booking Ticket Train hoặc vui l&ograve;ng li&ecirc;n hệ trung t&acirc;m hỗ trợ kh&aacute;ch h&agrave;ng theo số 19006469 để được giải đ&aacute;p.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">7.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Nghi&ecirc;m cấm sử dụng</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">a. Qu&yacute; kh&aacute;ch kh&ocirc;ng được ph&eacute;p sử dụng t&iacute;nh năng mua v&eacute; trực tuyến để đặt chỗ tr&aacute;i ph&eacute;p, giữ chỗ khống, đặt chỗ với c&aacute;c th&ocirc;ng tin giả mạo hoặc thiếu trung thực.</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Qu&yacute; kh&aacute;ch kh&ocirc;ng được ph&eacute;p: - Sử dụng t&iacute;nh năng mua v&eacute; trực tuyến v&agrave;o mục đ&iacute;ch kinh doanh dịch vụ b&aacute;n v&eacute;; hoặc - Sử dụng t&iacute;nh năng mua v&eacute; trực tuyến cho c&aacute;c h&agrave;nh động tr&aacute;i ph&aacute;p luật; hoặc - Sử dụng t&iacute;nh năng mua v&eacute; trực tuyến g&acirc;y cản trở đến sự truy cập v&agrave; sử dụng dịch vụ n&agrave;y của người kh&aacute;c;</span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">b. Đường sắt Việt Nam c&oacute; quyền huỷ bỏ y&ecirc;u cầu sử dụng t&iacute;nh năng n&agrave;y của Qu&yacute; kh&aacute;ch m&agrave; kh&ocirc;ng cần bất kỳ th&ocirc;ng b&aacute;o n&agrave;o nếu thấy rằng Qu&yacute; kh&aacute;ch đang vi phạm những quy định n&agrave;y.</span></span></span></span></p>
', 
 NULL, GETDATE(), 1, 2, 1),
(N'Điều khoản sử dụng website Ticket Train Booking', 
 N'<p style="text-align:center"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="color:red">C&aacute;c Điều Kiện &amp; Điều Khoản</span></strong></span></span></span></p>

<p style="text-align:center"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><a name="DieuKhoanSuDung"><strong><span style="font-size:14pt">***Điều khoản sử dụng website Online Booking Ticket Train</span></strong></a><strong><span style="font-size:14pt">***</span></strong></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">WEBSITE N&Agrave;Y THUỘC QUYỀN SỞ HỮU V&Agrave; QUẢN L&Yacute; Group 4 - SE1871-Ks v&agrave; đồng thời của TỔNG C&Ocirc;NG TY&nbsp; ĐƯỜNG SẮT VIỆT NAM (Đường sắt Việt Nam). KHI TRUY CẬP, SỬ DỤNG WEBSITE N&Agrave;Y, QU&Yacute; KH&Aacute;CH Đ&Atilde; MẶC NHI&Ecirc;N ĐỒNG &Yacute; VỚI C&Aacute;C ĐIỀU KHOẢN V&Agrave; ĐIỀU KIỆN ĐỀ RA Ở Đ&Acirc;Y. DO VẬY ĐỀ NGHỊ QU&Yacute; KH&Aacute;CH ĐỌC V&Agrave; NGHI&Ecirc;N CỨU KỸ TRƯỚC KHI SỬ DỤNG TIẾP</span></strong></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt"><span style="color:#333333">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span></strong><strong><span style="font-size:14pt"><span style="color:#333333">Chấp thuận c&aacute;c Điều kiện Sử dụng</span></span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt"><span style="color:#333333">Khi sử dụng Website&nbsp;</span></span></span></span></span></span><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><a name="DieuKhoanSuDung"><strong><span style="font-size:14pt">Online Booking Ticket Train</span></strong></a></span></span></span><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt"><span style="color:#333333">&nbsp;(sau đ&acirc;y gọi tắt l&agrave; &ldquo;Website&rdquo;), Qu&yacute; kh&aacute;ch đ&atilde; mặc nhi&ecirc;n chấp thuận c&aacute;c điều khoản v&agrave; điều kiện sử dụng (sau đ&acirc;y gọi tắt l&agrave; &ldquo;Điều kiện Sử dụng&rdquo;) được quy định dưới đ&acirc;y. Để biết được c&aacute;c sửa đổi mới nhất, Qu&yacute; kh&aacute;ch n&ecirc;n thường xuy&ecirc;n kiểm tra lại &ldquo;Điều kiện Sử dụng&rdquo;. Online Booking Ticket Train c&oacute; quyền thay đổi, điều chỉnh, th&ecirc;m hay bớt c&aacute;c nội dung của &ldquo;Điều kiện Sử dụng&rdquo; tại bất kỳ thời điểm n&agrave;o. Nếu Qu&yacute; kh&aacute;ch vẫn tiếp tục sử dụng Website sau khi c&oacute; c&aacute;c thay đổi như vậy th&igrave; c&oacute; nghĩa l&agrave; Qu&yacute; kh&aacute;ch đ&atilde; chấp thuận c&aacute;c thay đổi đ&oacute;.</span></span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt"><span style="color:#333333">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span></strong><strong><span style="font-size:14pt"><span style="color:#333333">T&iacute;nh chất của th&ocirc;ng tin hiển thị</span></span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt"><span style="color:#333333">C&aacute;c nội dung hiển thị tr&ecirc;n Website nhằm mục đ&iacute;ch cung cấp th&ocirc;ng tin về Đường sắt Việt Nam, về dịch vụ vận chuyển h&agrave;nh kh&aacute;ch, h&agrave;nh l&yacute; v&agrave; h&agrave;ng h&oacute;a, dịch vụ kh&aacute;ch sạn, cũng như c&aacute;c dịch vụ kh&aacute;c do đối t&aacute;c của Đường sắt Việt Namvề du lịch, lữ h&agrave;nh, ... cung cấp (sau đ&acirc;y được gọi chung l&agrave; &ldquo;Nh&agrave; Cung Cấp&rdquo;).</span></span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt"><span style="color:#333333">Li&ecirc;n</span></span></strong><strong><span style="font-size:14pt">&nbsp;kết đến Website kh&aacute;c</span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Website cung cấp một số li&ecirc;n kết tới trang Web hoặc nguồn dữ liệu kh&aacute;c. Qu&yacute; kh&aacute;ch tự chịu tr&aacute;ch nhiệm khi sử dụng c&aacute;c li&ecirc;n kết n&agrave;y. Đường sắt Việt Nam kh&ocirc;ng tiến h&agrave;nh thẩm định hay x&aacute;c thực nội dung, t&iacute;nh ch&iacute;nh x&aacute;c, quan điểm thể hiện tại c&aacute;c trang Web v&agrave; nguồn dữ liệu li&ecirc;n kết n&agrave;y. Đường sắt Việt Nam từ chối bất cứ tr&aacute;ch nhiệm ph&aacute;p l&yacute; n&agrave;o li&ecirc;n quan tới t&iacute;nh ch&iacute;nh x&aacute;c, nội dung thể hiện, mức độ an to&agrave;n v&agrave; việc cho hiển thị hay che đi c&aacute;c th&ocirc;ng tin tr&ecirc;n c&aacute;c trang Web v&agrave; nguồn dữ liệu n&oacute;i tr&ecirc;n.</span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">4.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt"><span style="color:#333333">Li&ecirc;n kết từ Website kh&aacute;c</span></span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt"><span style="color:#333333">Đường sắt Việt Nam kh&ocirc;ng cho ph&eacute;p bất kỳ nh&agrave; cung cấp</span></span><span style="font-size:14pt">&nbsp;dịch vụ internet n&agrave;o được ph&eacute;p &ldquo;đặt to&agrave;n bộ&rdquo; hay &ldquo;nh&uacute;ng&rdquo; bất kỳ th&agrave;nh tố n&agrave;o của Website n&agrave;y sang một trang kh&aacute;c hoặc sử dụng c&aacute;c kỹ thuật l&agrave;m thay đổi giao diện / hiển thị mặc định của Website.</span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">5.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">MIỄN TRỪ TR&Aacute;CH NHIỆM</span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">TH&Ocirc;NG TIN HIỂN THỊ TẠI WEBSITE N&Agrave;Y KH&Ocirc;NG ĐI K&Egrave;M BẤT KỲ ĐẢM BẢO HAY CAM KẾT TR&Aacute;CH NHIỆM DƯỚI BẤT KỲ H&Igrave;NH THỨC N&Agrave;O TỪ PH&Iacute;A ĐƯỜNG SẮT VIỆT NAM HAY NH&Agrave; CUNG CẤP KH&Aacute;C, CH&Iacute;NH THỨC HAY H&Agrave;M &Yacute;, BAO GỒM NHƯNG KH&Ocirc;NG GIỚI HẠN VỀ SỰ PH&Ugrave; HỢP CỦA SẢN PHẨM, DỊCH VỤ M&Agrave; NGƯỜI MUA Đ&Atilde; LỰA CHỌN. ĐƯỜNG SẮT VIỆT NAM V&Agrave; C&Aacute;C NH&Agrave; CUNG CẤP KH&Aacute;C CŨNG TỪ CHỐI TR&Aacute;CH NHIỆM HAY ĐƯA RA ĐẢM BẢO RẰNG WEBSITE SẼ KH&Ocirc;NG C&Oacute; LỖI VẬN H&Agrave;NH, AN TO&Agrave;N, KH&Ocirc;NG BỊ GI&Aacute;N ĐOẠN HAY BẤT CỨ ĐẢM BẢO N&Agrave;O VỀ T&Iacute;NH CH&Iacute;NH X&Aacute;C, ĐẦY ĐỦ V&Agrave; Đ&Uacute;NG HẠN CỦA C&Aacute;C TH&Ocirc;NG TIN HIỂN THỊ. KHI TRUY CẬP V&Agrave;O WEBSITE N&Agrave;Y, QU&Yacute; KH&Aacute;CH MẶC NHI&Ecirc;N ĐỒNG &Yacute; RẰNG ĐƯỜNG SẮT VIỆT NAM, C&Ugrave;NG VỚI ĐỐI T&Aacute;C KH&Aacute;C, VI&Ecirc;N CHỨC, C&Aacute;N BỘ QUẢN L&Yacute; V&Agrave; NGƯỜI ĐẠI DIỆN CỦA HỌ KH&Ocirc;NG CHỊU BẤT CỨ TR&Aacute;CH NHIỆM N&Agrave;O LI&Ecirc;N QUAN ĐẾN THƯƠNG TẬT, MẤT M&Aacute;T, KHIẾU KIỆN, THIỆT HẠI TRỰC TIẾP HOẶC THIỆT HẠI GI&Aacute;N TIẾP DO KH&Ocirc;NG LƯỜNG TRƯỚC HOẶC DO HẬU QUẢ ĐỂ LẠI DƯỚI BẤT KỲ H&Igrave;NH THỨC N&Agrave;O PH&Aacute;T SINH TỪ HAY C&Oacute; LI&Ecirc;N QUAN ĐẾN VIỆC: (1) SỬ DỤNG C&Aacute;C TH&Ocirc;NG TIN TR&Ecirc;N WEBSITE N&Agrave;Y; (2) C&Aacute;C TRUY CẬP KẾT NỐI TỪ WEBSITE N&Agrave;Y;(3) ĐĂNG K&Yacute; TH&Agrave;NH VI&Ecirc;N, ĐĂNG K&Yacute; NHẬN THƯ ĐIỆN TỬ HAY THAM GIA V&Agrave;O CHƯƠNG TR&Igrave;NH KHUYẾN MẠI CỦA ĐƯỜNG SẮT VIỆT NAM; (4) ĐƯỜNG SẮT VIỆT NAM HAY MỘT NH&Agrave; CUNG CẤP N&Agrave;O Đ&Oacute; C&Oacute; THỰC HIỆN VIỆC CUNG CẤP DỊCH VỤ HAY KH&Ocirc;NG THẬM CH&Iacute; TRONG TRƯỜNG HỢP ĐƯỜNG SẮT VIỆT NAM HAY NH&Agrave; CUNG CẤP Đ&Oacute; Đ&Atilde; ĐƯỢC CẢNH B&Aacute;O VỀ KHẢ NĂNG XẢY RA THIỆT HẠI; V&Agrave; (5) C&Aacute;C HẠN CHẾ LI&Ecirc;N QUAN ĐẾN ĐẶT CHỖ TRỰC TUYẾN M&Ocirc; TẢ TẠI Đ&Acirc;Y. C&aacute;c điều kiện v&agrave; hạn chế n&ecirc;u tr&ecirc;n chỉ c&oacute; hiệu lực trong khu&ocirc;n khổ ph&aacute;p luật hiện h&agrave;nh.</span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">6.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Quyền sở hữu tr&iacute; tuệ</span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Website n&agrave;y v&agrave; mọi nội dung xếp đặt, hiển thị đều thuộc sở hữu v&agrave; l&agrave; t&agrave;i sản độc quyền khai th&aacute;c của Đường sắt Việt Nam v&agrave; c&aacute;c nh&agrave; cung cấp l&agrave; đối t&aacute;c kinh doanh của Đường sắt Việt Nam. Mọi sử dụng, tr&iacute;ch dẫn phải kh&ocirc;ng g&acirc;y thiệt hại cho Đường sắt Việt Nam v&agrave; đều phải tu&acirc;n thủ c&aacute;c điều kiện sau: (1) Chỉ sử dụng cho mục đ&iacute;ch c&aacute; nh&acirc;n, phi thương mại; (2) c&aacute;c sao ch&eacute;p hoặc tr&iacute;ch dẫn đều phải giữ nguy&ecirc;n dấu hiệu bản quyền hoặc c&aacute;c yết thị về quyền sở hữu tr&iacute; tuệ như đ&atilde; thể hiện trong phi&ecirc;n bản gốc; v&agrave; (3) mọi sản phẩm, c&ocirc;ng nghệ hay quy tr&igrave;nh được sử dụng hay hiển thị tại Website n&agrave;y đều c&oacute; thể li&ecirc;n đới đến bản quyền hay sở hữu tr&iacute; tuệ kh&aacute;c của Đường sắt Việt Nam v&agrave; c&aacute;c nh&agrave; cung cấp c&oacute; li&ecirc;n quan m&agrave; kh&ocirc;ng thể chuyển nhượng được. Tất cả c&aacute;c nội dung được cung cấp tại Website n&agrave;y kh&ocirc;ng được ph&eacute;p nh&acirc;n bản, hiển thị, c&ocirc;ng bố, phổ biến, đưa tin tức hay lưu h&agrave;nh cho bất cứ ai, dưới bất kỳ h&igrave;nh thức n&agrave;o, kể cả tr&ecirc;n c&aacute;c Website độc lập kh&aacute;c m&agrave; kh&ocirc;ng được sự chấp thuận của Đường sắt Việt Nam. Mọi h&igrave;nh ảnh, thương hiệu, nh&atilde;n hiệu hay biểu tượng tr&igrave;nh b&agrave;y tại Website n&agrave;y được bảo vệ bởi Luật sở hữu tr&iacute; tuệ v&agrave; c&aacute;c điều luật c&oacute; li&ecirc;n quan kh&aacute;c v&agrave; việc Qu&yacute; kh&aacute;ch sử dụng Website n&agrave;y kh&ocirc;ng cho ph&eacute;p Qu&yacute; kh&aacute;ch được quyền sử dụng, nh&acirc;n bản hay sở hữu.</span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">7.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Điều chỉnh v&agrave; sửa đổi</span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Đường sắt Việt Nam c&oacute; quyền thay đổi, chỉnh sửa hoặc chấm dứt hoạt động của Website n&agrave;y v&agrave;o bất cứ thời điểm n&agrave;o.</span></span></span></span></span></p>

<p style="margin-left:24px; text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><strong><span style="font-size:14pt">8.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></strong><strong><span style="font-size:14pt">Luật điều chỉnh v&agrave; cơ quan giải quyết tranh chấp</span></strong></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:medium"><span style="background-color:white"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000"><span style="font-size:14pt">Điều kiện Sử dụng n&agrave;y được điều chỉnh bởi luật của nước Cộng ho&agrave; x&atilde; hội chủ nghĩa Việt Nam. T&ograve;a &aacute;n nước Cộng h&ograve;a x&atilde; hội chủ nghĩa Việt Nam l&agrave; cơ quan duy nhất c&oacute; thẩm quyền giải quyết tất cả c&aacute;c tranh chấp c&oacute; li&ecirc;n quan.</span></span></span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="font-family:&quot;Times New Roman&quot;,&quot;serif&quot;"><span style="color:#000000">&nbsp;</span></span></span></p>
', 
 NULL, GETDATE(), 1, 2, 1),
(N'Chính sách hoàn trả vé, đổi vé', 
 N'<p style="text-align:center"><span style="font-size:16pt"><span style="color:#000000"><strong><span style="font-size:20pt">Ch&iacute;nh s&aacute;ch ho&agrave;n trả v&eacute;, đổi v&eacute;</span></strong></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><strong><span style="font-size:14pt">I. Điều kiện ho&agrave;n trả v&eacute;, đổi v&eacute;:</span></strong></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Người thực hiện giao dịch đổi v&eacute;, trả v&eacute; điện tử phải l&agrave; người mua v&eacute; điện tử hoặc l&agrave; một trong c&aacute;c h&agrave;nh kh&aacute;ch đi tầu tr&ecirc;n c&ugrave;ng một lần giao dịch mua v&eacute; điện tử. C&aacute;c v&eacute; điện tử được coi l&agrave; c&ugrave;ng một giao dịch mua v&eacute; nếu được ghi tr&ecirc;n c&ugrave;ng một h&oacute;a đơn điện tử.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">V&eacute; điện tử đi ngay kh&ocirc;ng c&oacute; th&ocirc;ng tin sẽ kh&ocirc;ng được đổi, trả lại. Trong những trường hợp t&agrave;u gặp sự cố, C&ocirc;ng ty VTĐS sẽ quy định danh s&aacute;ch c&aacute;c v&eacute; đi ngay được đổi, trả tr&ecirc;n hệ thống.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Điều kiện &aacute;p dụng: H&agrave;nh kh&aacute;ch hoặc người mua v&eacute; c&oacute; quyền trả v&eacute; v&agrave; c&aacute;c dịch vụ đi k&egrave;m theo c&aacute;c quy định của ng&agrave;nh Đường sắt. Khi đổi v&eacute;, trả v&eacute; h&agrave;nh kh&aacute;ch sẽ phải ho&agrave;n trả to&agrave;n bộ c&aacute;c dịch vụ đi k&egrave;m theo v&eacute; v&agrave; chịu nộp một khoản ph&iacute; được t&iacute;nh bằng tổng ph&iacute; đổi v&eacute;, trả v&eacute; v&agrave; ph&iacute; hủy dịch vụ đi k&egrave;m v&eacute; theo quy định. H&agrave;nh kh&aacute;ch cũng c&oacute; thể hủy dịch vụ v&agrave; trả ph&iacute; hủy dịch vụ theo quy định.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Đối với trẻ em vị th&agrave;nh ni&ecirc;n, chỉ người bảo l&atilde;nh mua v&eacute; mới được thực hiện đổi, trả v&eacute; tại Cửa v&eacute;. Trong trường hợp người mua l&agrave; c&ocirc;ng ty, cần cung cấp giấy giới thiệu về người thực hiện đổi, trả v&eacute; (c&oacute; đ&oacute;ng dấu) v&agrave; giấy tờ t&ugrave;y th&acirc;n để thực hiện đổi, trả v&eacute; cho v&eacute; trẻ vị th&agrave;nh ni&ecirc;n.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Người mua v&eacute; cung cấp giấy tờ t&ugrave;y th&acirc;n c&oacute; thể thực hiện đổi, trả v&eacute; cho những v&eacute; m&igrave;nh mua.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Đối với c&aacute;c v&eacute; bị kh&oacute;a cấm trả tr&ecirc;n hệ thống (v&eacute; đi ngay, v&eacute; b&aacute;o mất&hellip;), trong một số trường hợp đặc biệt, C&ocirc;ng ty VTĐS c&oacute; thể thiết lập để được ph&eacute;p trả v&eacute; tr&ecirc;n hệ thống.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><strong><span style="font-size:14pt">II. Quy định về thời gian ho&agrave;n trả v&eacute;, đổi v&eacute;:</span></strong></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Qu&yacute; kh&aacute;ch đều c&oacute; quyền thay đổi hoặc chấm dứt giao dịch trước khi thanh to&aacute;n tiền v&eacute; tại c&aacute;c ga hoặc tại c&aacute;c Đại l&yacute;, bưu cục b&aacute;n v&eacute; t&agrave;u hỏa. Trường hợp đ&atilde; thanh to&aacute;n tiền v&eacute;, nếu Qu&yacute; kh&aacute;ch muốn thay đổi hoặc chấm dứt giao dịch th&igrave; thời gian trả, đổi v&eacute; &aacute;p dụng theo c&aacute;c quy định hiện h&agrave;nh của ng&agrave;nh đường sắt.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Quy định cụ thể:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">H&agrave;nh kh&aacute;ch c&oacute; quyền trả lại v&eacute;, đổi v&eacute; trước giờ t&agrave;u chạy. Doanh nghiệp quy định cụ thể mức khấu trừ tương ứng với thời gian trả lại v&eacute;, đổi v&eacute; v&agrave; c&aacute;c nội dung kh&aacute;c c&oacute; li&ecirc;n quan đến việc trả lại v&eacute;, đổi v&eacute; của h&agrave;nh kh&aacute;ch.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><strong><em><span style="font-size:14pt">1.Thời gian trả lại v&eacute;, đổi v&eacute;:</span></em></strong></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">&nbsp;1.1. T&agrave;u kh&aacute;ch trong nước (bao gồm t&agrave;u kh&aacute;ch Thống nhất v&agrave; t&agrave;u kh&aacute;ch Khu đoạn chạy suốt tr&ecirc;n tuyến đường sắt Bắc - Nam):</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">a) Đối với v&eacute; c&aacute; nh&acirc;n: Trước giờ t&agrave;u chạy l&agrave; 4 giờ.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">b) Đối với v&eacute; tập thể: trước giờ t&agrave;u chạy tối thiểu l&agrave; 24 giờ.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">c) Đối với tập thể mua v&eacute; trọn toa, trọn cụm toa xe thực hiện theo c&aacute;c thỏa thuận trong hợp đồng mua v&eacute;.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">&nbsp;1.2. T&agrave;u Li&ecirc;n vận quốc tế: thực hiện theo quy định của Tổ chức OSZD, cụ thể:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">a) Đối với v&eacute; c&aacute; nh&acirc;n: Trước giờ t&agrave;u chạy l&agrave; 6 giờ.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">b) Đối với v&eacute; tập thể&nbsp;: Trước giờ t&agrave;u chạy tối thiểu l&agrave; 5 ng&agrave;y.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">c) Nếu kh&ocirc;ng tu&acirc;n thủ thời hạn tr&ecirc;n v&agrave; trong trường hợp t&agrave;u chưa chạy th&igrave; chỉ trả lại tiền v&eacute; kh&aacute;ch (c&aacute; nh&acirc;n, tập thể), kh&ocirc;ng trả lại tiền v&eacute; nằm.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">1.3. T&ugrave;y từng giai đoạn, thời điểm trong năm, C&ocirc;ng ty VTĐS sẽ điều chỉnh thời gian trả lại v&eacute;, đổi v&eacute; ph&ugrave; hợp với t&igrave;nh h&igrave;nh thực tế.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><strong><em><span style="font-size:14pt">2. Quy định về điều kiện trả v&eacute;, đổi v&eacute;:</span></em></strong></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">2.1. Đối với c&aacute;c t&agrave;u Thống Nhất v&agrave; t&agrave;u Khu đoạn:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">2.1.1. Trường hợp đổi v&eacute;:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- Chỉ &aacute;p dụng đổi v&eacute; đối với v&eacute; c&aacute; nh&acirc;n.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- H&agrave;nh kh&aacute;ch được đổi v&eacute; khi c&oacute; đủ c&aacute;c điều kiện sau:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">+ Thời gian đổi v&eacute; thực hiện theo quy định tại Mục 1 n&ecirc;u tr&ecirc;n;</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">+ C&oacute; c&ugrave;ng ga đi, ga đến;</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">+ Đổi v&eacute; 1 lần duy nhất;</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">+ Bố tr&iacute; được chỗ theo y&ecirc;u cầu của h&agrave;nh kh&aacute;ch;</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">+ Kh&ocirc;ng được đổi v&eacute; trong trường hợp thay đổi th&ocirc;ng tin c&aacute; nh&acirc;n ghi tr&ecirc;n v&eacute; đ&atilde; mua.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- Sau khi đ&atilde; đổi v&eacute;, nếu h&agrave;nh kh&aacute;ch c&oacute; nhu cầu trả lại v&eacute; th&igrave; mức khấu trừ trả v&eacute; đối với c&aacute;c v&eacute; đ&atilde; đổi l&agrave; 20% số tiền in tr&ecirc;n v&eacute;.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- C&aacute;c nh&acirc;n vi&ecirc;n b&aacute;n v&eacute; khi đổi v&eacute; cho h&agrave;nh kh&aacute;ch viết th&ecirc;m th&ocirc;ng tin &quot;v&eacute; đ&atilde; đổi&quot; (đối với v&eacute; cứng) bằng b&uacute;t kh&ocirc;ng tẩy x&oacute;a được v&agrave;o sau tấm v&eacute; đ&atilde; đổi của h&agrave;nh kh&aacute;ch v&agrave; đ&oacute;ng dấu kh&aacute;ch vận.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">2.1.2. Trường hợp trả lại v&eacute;:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">a) Đối với v&eacute; c&aacute; nh&acirc;n: Người mua v&eacute; hoặc người đi t&agrave;u được trả v&eacute; khi c&oacute; đủ c&aacute;c điều kiện sau:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- Đối với v&eacute; điện tử:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">+ C&oacute; cung cấp th&ocirc;ng tin: Th&ocirc;ng tin c&aacute; nh&acirc;n tr&ecirc;n giấy tờ t&ugrave;y th&acirc;n của người mua v&eacute; hoặc người đi t&agrave;u tr&ugrave;ng khớp với th&ocirc;ng tin in tr&ecirc;n Thẻ l&ecirc;n t&agrave;u v&agrave; th&ocirc;ng tin ghi nhận tr&ecirc;n hệ thống b&aacute;n v&eacute; điện tử.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">+ Kh&ocirc;ng cung cấp th&ocirc;ng tin c&aacute; nh&acirc;n (v&eacute; đi ngay): Kh&ocirc;ng được trả lại v&eacute;, trừ trường hợp khi xảy ra sự cố g&acirc;y tắc đường chạy t&agrave;u v&igrave; nguy&ecirc;n nh&acirc;n bất khả kh&aacute;ng v&agrave; ốm đau đột xuất th&igrave; c&aacute;c v&eacute; n&agrave;y được giải quyết theo quy định của Doanh nghiệp.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- Đối với v&eacute; cứng: V&eacute; c&ograve;n nguy&ecirc;n vẹn, kh&ocirc;ng bị r&aacute;ch n&aacute;t; Kh&ocirc;ng bị tẩy, x&oacute;a, sửa chữa v&agrave; c&ograve;n đủ c&aacute;c th&ocirc;ng tin tr&ecirc;n v&eacute;.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">b) Đối với v&eacute; tập thể: Tập thể được trả v&eacute; khi c&oacute; đủ c&aacute;c điều kiện sau:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- Đối với tập thể l&agrave; đơn vị, tổ chức: C&oacute; c&ocirc;ng văn đề nghị hoặc giấy giới thiệu của đơn vị, tổ chức. Người đại diện cho đơn vị, tổ chức khi đến l&agrave;m thủ tục trả v&eacute; phải mang theo giấy giới thiệu, giấy tờ t&ugrave;y th&acirc;n, danh s&aacute;ch tập thể đi t&agrave;u.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- Đối với tập thể kh&ocirc;ng phải l&agrave; đơn vị, tổ chức: Giấy tờ t&ugrave;y th&acirc;n của trưởng đo&agrave;n đại diện, danh s&aacute;ch tập thể đi t&agrave;u.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">c) Đối với tập thể mua v&eacute; trọn toa, trọn cụm toa xe thực hiện theo c&aacute;c thỏa thuận trong hợp đồng mua v&eacute;.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">2.2.&nbsp; Đối với t&agrave;u Li&ecirc;n vận quốc tế:</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">H&agrave;nh kh&aacute;ch được trả v&eacute; khi: V&eacute; c&ograve;n nguy&ecirc;n vẹn, kh&ocirc;ng bị r&aacute;ch n&aacute;t; kh&ocirc;ng bị tẩy, x&oacute;a, sửa chữa v&agrave; c&ograve;n đủ c&aacute;c th&ocirc;ng tin tr&ecirc;n v&eacute;.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">2.3. Doanh nghiệp sẽ căn cứ t&igrave;nh h&igrave;nh thực tế để quy định thời gian đổi, trả cho từng m&aacute;c t&agrave;u, từng tuyến đường trong c&aacute;c thời điểm cụ thể.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><strong><span style="font-size:14pt">III. Mức ph&iacute; trả lại v&eacute;, đổi v&eacute;:</span></strong></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- Ph&iacute; trả v&eacute; được x&aacute;c định căn cứ tr&ecirc;n c&aacute;c quy định về ph&iacute; trả v&eacute; của ng&agrave;nh đường sắt hoặc theo c&aacute;c quy định cụ thể đối với c&aacute;c v&eacute; mua c&oacute; &aacute;p dụng c&aacute;c chương tr&igrave;nh khuyến m&atilde;i giảm gi&aacute;.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- Doanh nghiệp sẽ căn cứ t&igrave;nh h&igrave;nh thực tế để điều chỉnh mức ph&iacute; trả lại v&eacute;, đổi v&eacute; cho từng m&aacute;c t&agrave;u, từng tuyến đường trong c&aacute;c thời điểm cụ thể.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">- T&agrave;u Li&ecirc;n vận quốc tế thực hiện theo quy định của tổ chức Li&ecirc;n vận quốc tế OSZD.</span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><strong><span style="font-size:14pt">IV. C&aacute;ch thức lấy lại tiền đổi, trả v&eacute;:</span></strong></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">C&oacute; hai h&igrave;nh thức lấy lại tiền đổi, trả v&eacute;: Tiền mặt, chuyển về t&agrave;i khoản thanh to&aacute;n.</span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">1. Trường hợp khi mua v&eacute; trực tiếp tại c&aacute;c điểm b&aacute;n v&eacute; hoặc mua v&eacute; qua Website v&agrave; thanh to&aacute;n trả sau bằng tiền mặt tại c&aacute;c điểm thanh to&aacute;n (ATM/Internet Banking/Mobile Banking, nộp tiền mặt tại ga, tại c&aacute;c Đại l&yacute; b&aacute;n v&eacute; t&agrave;u hỏa, tại c&aacute;c Bưu cục (VNPOST), c&aacute;c điểm giao dịch của Ng&acirc;n h&agrave;ng VIB, qua c&aacute;c tiện &iacute;ch bằng ứng dụng PAYOO,&hellip; hoặc nộp tiền mặt tại c&aacute;c đại l&yacute; thu hộ ủy quyền của Đường sắt Việt Nam), khi thực hiện việc đổi, trả v&eacute; sẽ được nhận tiền mặt trực tiếp tại c&aacute;c điểm b&aacute;n v&eacute;&nbsp; tại ga hoặc đại l&yacute; (căn cứ quy định cụ thể của ng&agrave;nh đường sắt).</span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">2. Trường hợp khi mua v&eacute; v&agrave; thanh to&aacute;n bằng h&igrave;nh thức thanh to&aacute;n trực tuyến:</span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">2.1. Thanh to&aacute;n qua c&aacute;c cổng thanh to&aacute;n trực tuyến: Napas, Ng&acirc;n lượng, Payoo, VnPay, ZaloPay, MoMo, Epay v&agrave; ShopeePay: Tiền chuyển về t&agrave;i khoản đ&atilde; thanh to&aacute;n khi mua v&eacute;.</span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Đối với giao dịch ho&agrave;n tiền thẻ quốc tế: Sau khi VTĐS thực hiện thao t&aacute;c ho&agrave;n tiền tr&ecirc;n cổng trước 17h chiều h&agrave;ng ng&agrave;y, ng&agrave;y h&ocirc;m sau NHTT (ng&acirc;n h&agrave;ng trung t&acirc;m) sẽ đối chiếu số liệu v&agrave; tổng hợp dữ liệu của ng&agrave;y h&ocirc;m trước v&agrave; gửi đi TCTQT (tổ chức thẻ quốc tế), tại thời điểm n&agrave;y hệ thống sẽ tự động tr&iacute;ch nợ khoản tiền của VTĐS. Sau khoảng thời gian T+4 kể từ ng&agrave;y gửi dữ liệu đi, tiền sẽ được ho&agrave;n về ph&iacute;a đầu NHPH (ng&acirc;n h&agrave;ng ph&aacute;t h&agrave;nh). Tuy nhi&ecirc;n, thời gian NHPH ho&agrave;n tiền cho Kh&aacute;ch h&agrave;ng l&agrave;&nbsp; t&ugrave;y theo ch&iacute;nh s&aacute;ch của từng ng&acirc;n h&agrave;ng.</span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Đối với giao dịch ho&agrave;n tiền thẻ nội địa: Sau khi VTĐS thực hiện ho&agrave;n tiền v&agrave;o ng&agrave;y h&ocirc;m trước, ng&agrave;y h&ocirc;m sau tiền đ&atilde; được tr&iacute;ch nợ từ VTĐS để ho&agrave;n về ph&iacute;a ng&acirc;n h&agrave;ng ph&aacute;t h&agrave;nh. Tại thời điểm trước ng&agrave;y 1/10/2017, thời gian NHPH ho&agrave;n tiền cho Kh&aacute;ch h&agrave;ng l&agrave; t&ugrave;y theo ch&iacute;nh s&aacute;ch của từng Ng&acirc;n h&agrave;ng, mới đ&acirc;y (ng&agrave;y 1/10/2017) theo quy định TCTV (tổ chức th&agrave;nh vi&ecirc;n)&nbsp; mới giữa Napas v&agrave; NHPH th&igrave; NHPH sẽ phải ho&agrave;n tiền cho KH trong v&ograve;ng 2 ng&agrave;y kể từ ng&agrave;y nhận được tiền ho&agrave;n.</span></span></span></p>

<p style="text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><em><u><span style="font-size:14pt">Lưu &yacute;:</span></u></em><em>&nbsp;</em><span style="font-size:14pt">Thẻ Quốc tế: T&ugrave;y từng ng&acirc;n h&agrave;ng ph&aacute;t h&agrave;nh thẻ c&oacute; ng&acirc;n h&agrave;ng sẽ trừ v&agrave;o kỳ sao k&ecirc; của th&aacute;ng kế tiếp, c&oacute; ng&acirc;n h&agrave;ng sẽ th&ocirc;ng b&aacute;o bằng tin nhắn số tiền ho&agrave;n trả.</span></span></span></p>

<p style="margin-left:9px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">2.2. Thanh to&aacute;n qua c&aacute;c v&iacute; điện tử: Vimo, Momo, VNPay, ZaloPay &hellip;: Tiền chuyển về t&agrave;i khoản của kh&aacute;ch h&agrave;ng tr&ecirc;n v&iacute; điện tử.</span></span></span></p>

<p style="margin-left:19px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000">&nbsp;</span></span></p>

<p style="margin-left:19px; text-align:justify"><span style="font-size:16pt"><span style="color:#000000"><span style="font-size:14pt">Qu&yacute; kh&aacute;ch c&oacute; thể tham khảo th&ecirc;m &ldquo;Quy tr&igrave;nh trả v&eacute;&rdquo; được hướng dẫn&nbsp;</span><a href="https://youtu.be/ZPL8oM5i4Fg" style="color:#0563c1; text-decoration:underline" target="_parent"><span style="font-size:14pt">tại đ&acirc;y</span></a><span style="font-size:14pt">.</span></span></span></p>
', 
 NULL, GETDATE(), 1, 2, 3),
(N'Quy định thời gian hoàn tiền', 
 N'Quy định thời gian hoàn tiền bao gồm các điều kiện và thời gian yêu cầu hoàn lại tiền cho khách hàng trong các trường hợp như hủy vé, không thể sử dụng dịch vụ do lý do bất khả kháng, hoặc khi khách hàng yêu cầu hoàn tiền sau khi sử dụng dịch vụ. Thời gian hoàn tiền có thể thay đổi tùy theo phương thức thanh toán mà khách hàng đã sử dụng. Ví dụ, nếu thanh toán qua thẻ tín dụng, thời gian hoàn tiền có thể từ 5 đến 10 ngày làm việc, trong khi thanh toán qua chuyển khoản ngân hàng có thể mất từ 7 đến 15 ngày. Chính sách hoàn tiền cũng quy định về các trường hợp không đủ điều kiện hoàn tiền, chẳng hạn như khi khách hàng không tuân thủ quy định về hủy vé hoặc thay đổi dịch vụ trong thời gian quy định.', 
 NULL, GETDATE(), 1, 2, 3);

INSERT INTO Booking (UserID, TripID, RoundTripTripID, TotalPrice, PaymentStatus, BookingStatus)
VALUES
-- Chuyến một chiều
(2, 31, NULL, 1100000.00, 'Paid', 'Active'),  -- Kim Soo Hyun, Trip 31 (2025-03-18 08:15:00)
(3, 54, NULL, 720000.00, 'Paid', 'Active'),  -- Nguyễn Mạnh Dũng, Trip 32 (2025-03-18 09:15:00)
(9, 33, NULL, 1825000.00, 'Paid', 'Active'),  -- Nhân viên 0, Trip 33 (2025-03-18 10:00:00)
(10, 62, NULL, 200000.00, 'Paid', 'Active'),  -- Nhân viên 1, Trip 34 (2025-03-18 11:45:00)
(11, 35, NULL,2200000.00, 'Paid', 'Active'),  -- Nhân viên 2, Trip 35 (2025-03-18 13:00:00)

-- Chuyến khứ hồi (liên kết RoundTripTripID)
(12, 1, 2, 2200000.00, 'Paid', 'Active'),     -- Nhân viên 3, Trip 1 (2025-03-06 08:00:00) và Trip 2 (2025-03-06 18:00:00)
(13, 3, 4, 2200000.00, 'Paid', 'Active'),     -- Anh Long, Trip 3 (2025-03-07 08:00:00) và Trip 4 (2025-03-07 18:00:00)
(14, 5, 6, 2200000.00, 'Paid', 'Active'),     -- Bảy Chọ, Trip 5 (2025-03-08 08:00:00) và Trip 6 (2025-03-08 18:00:00)
(15, 7, 8, 2200000.00, 'Paid', 'Active'),    -- Sun Lì, Trip 7 (2025-03-09 08:00:00) và Trip 8 (2025-03-09 18:00:00)
(16, 9, 10, 2200000.00, 'Paid', 'Active');   -- Phương Tuấn, Trip 9 (2025-03-10 08:00:00) và Trip 10 (2025-03-10 18:00:00);

INSERT INTO Ticket (PassengerName,PassengerType, CCCD, BookingID, SeatID, TripID, TicketPrice, TicketStatus)
VALUES
(N'Thủy Phạm',N'Người lớn', '023456789012', 1, 1, 31, 1100000.00, 'Unused'),

-- BookingID 2 (chuyến một chiều)
(N'Mạnh Dũng',N'Người lớn', '098765432101', 2, 3, 54, 360000.00, 'Unused'),
(N'Mạnh Lực',N'Người lớn', '098765432102', 2, 4, 54, 360000.00, 'Unused'),

-- BookingID 3 (chuyến một chiều)
(N'Hải Lê',N'Người lớn', '111222333444', 3, 5, 59, 725000.00, 'Unused'),
(N'Huấn Đặng',N'Người lớn', '111222333445', 3, 6, 33, 1100000.00, 'Unused'),

-- BookingID 4 (chuyến một chiều)
(N'Nga Trần',N'Người lớn', '555666777888', 4, 7, 62, 100000.00, 'Unused'),
(N'Ngô Trung',N'Người lớn', '555666777889', 4, 8, 62, 100000.00, 'Unused'),

-- BookingID 5 (chuyến một chiều)
(N'Phong Nguyễn',N'Người lớn', '999000111222', 5, 9, 35, 1100000.00, 'Unused'),
(N'Vượng Phạm',N'Người lớn', '999000111223', 5, 10, 35, 1100000.00, 'Unused'),

-- BookingID 6 (chuyến khứ hồi)
(N'Khoa Lê',N'Người lớn', '444555666777', 6, 11, 1, 1100000.00, 'Unused'),
(N'Khôi Phùng',N'Người lớn', '444555666778', 6, 12, 2, 1100000.00, 'Unused'),

-- BookingID 7 (chuyến khứ hồi)
(N'Tùng Chu',N'Người lớn', '777888999000', 7, 13, 3, 1100000.00, 'Unused'),
(N'Quang Trần',N'Người lớn', '777888999001', 7, 14, 4, 1100000.00, 'Unused'),

-- BookingID 8 (chuyến khứ hồi)
(N'Ngọc Kem',N'Người lớn', '222333444555', 8, 15, 5, 1100000.00, 'Unused'),
(N'Tiến Hoàng',N'Người lớn', '222333444556', 8, 16, 6, 1100000.00, 'Unused'),

-- BookingID 9 (chuyến khứ hồi)
(N'Tài Nguyễn',N'Người lớn', '666777888999', 9, 17, 7, 1100000.00, 'Unused'),
(N'Trang Huy',N'Người lớn', '666777888991', 9, 18, 8, 1100000.00, 'Unused'),

-- BookingID 10 (chuyến khứ hồi)
(N'Phương Tuấn',N'Người lớn', '333444555666', 10, 19, 9, 1100000.00, 'Unused'),
(N'Thiên An',N'Người lớn', '333444555667', 10, 20, 10, 1100000.00, 'Unused');

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[AddSeatsForNewCarriage]
    @CarriageID INT,
    @CarriageType NVARCHAR(50),  
    @TrainID INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @NumberOfSeats INT;
    DECLARE @SeatNumber INT;
    DECLARE @SeatType NVARCHAR(50);

    -- Xác định số ghế dựa trên loại toa
    IF @CarriageType = N'Toa VIP'  
        SET @NumberOfSeats = 12;
    ELSE IF @CarriageType = N'Toa Thường' 
        SET @NumberOfSeats = 10;
    ELSE
        RETURN;

    -- Đặt loại ghế
    IF @CarriageType = N'Toa VIP'
        SET @SeatType = N'Toa VIP'; 
    ELSE
        SET @SeatType = N'Toa Thường'; 

    -- Vòng lặp để thêm ghế
    SET @SeatNumber = 1;
    WHILE @SeatNumber <= @NumberOfSeats
    BEGIN
        INSERT INTO Seat
            (SeatNumber, Status, SeatType, CarriageID)
        VALUES
            (@SeatNumber, 'Available', @SeatType, @CarriageID);

        SET @SeatNumber = @SeatNumber + 1;
    END
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[DeleteCarriageAndSeats]
    @CarriageID INT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM Seat WHERE CarriageID = @CarriageID AND Status <> 'Available')
    BEGIN
        -- Ném ra lỗi (RAISERROR) hoặc trả về mã lỗi
        RAISERROR('Không thể xóa toa.  Có ghế không ở trạng thái Available.', 16, 1); -- 16 là severity level (lỗi do người dùng), 1 là state.
        RETURN;  -- Kết thúc procedure, không thực hiện xóa.
    END

    BEGIN TRANSACTION;  -- Bắt đầu transaction

    BEGIN TRY
        -- Xóa ghế của toa (chỉ các ghế 'Available')
        DELETE FROM Seat
        WHERE CarriageID = @CarriageID;

        -- Xóa toa
        DELETE FROM Carriage
        WHERE CarriageID = @CarriageID;

        COMMIT TRANSACTION; -- Commit transaction nếu thành công
    END TRY
    BEGIN CATCH
        -- Nếu có lỗi xảy ra trong khối TRY
        IF @@TRANCOUNT > 0  -- Kiểm tra xem transaction có đang active không
            ROLLBACK TRANSACTION; -- Rollback transaction

        -- Xử lý lỗi (ghi log, ném exception, ...)
        THROW;  -- Ném lại lỗi (hoặc xử lý theo cách khác, nhưng RAISERROR ở trên đã thông báo rồi)
    END CATCH
END;
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[UpdateCarriageAndSeats]
    @CarriageID INT,
    @CarriageNumber VARCHAR(50),
    @CarriageType NVARCHAR(50), 
    @NewCapacity INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRANSACTION;

    BEGIN TRY
        -- Lấy số lượng ghế hiện tại
        DECLARE @CurrentCapacity INT;
        SELECT @CurrentCapacity = Capacity FROM Carriage WHERE CarriageID = @CarriageID;

        -- Cập nhật thông tin toa tàu
        UPDATE Carriage
        SET CarriageNumber = @CarriageNumber,
            CarriageType = @CarriageType,
            Capacity = @NewCapacity
        WHERE CarriageID = @CarriageID;


        -- Điều chỉnh số lượng ghế
        IF @NewCapacity > @CurrentCapacity  -- Thêm ghế
        BEGIN
            DECLARE @SeatsToAdd INT = @NewCapacity - @CurrentCapacity;
            DECLARE @SeatNumber INT;
			      -- Tìm số ghế lớn nhất hiện tại + 1 của toa đó
            SELECT @SeatNumber = ISNULL(MAX(CAST(SeatNumber AS INT)), 0) + 1 FROM Seat WHERE CarriageID = @CarriageID;

            WHILE @SeatsToAdd > 0
            BEGIN
                 DECLARE @SeatType NVARCHAR(50);
                IF @CarriageType = N'Toa VIP'  
                    SET @SeatType = N'Toa VIP';
                ELSE
                    SET @SeatType = N'Toa Thường';

                INSERT INTO Seat (SeatNumber, Status, SeatType, CarriageID)
                VALUES (CAST(@SeatNumber AS VARCHAR(10)), 'Available', @SeatType, @CarriageID);

                SET @SeatNumber = @SeatNumber + 1;
                SET @SeatsToAdd = @SeatsToAdd - 1;
            END
        END
        ELSE IF @NewCapacity < @CurrentCapacity  -- Xóa ghế
        BEGIN
            DECLARE @SeatsToDelete INT = @CurrentCapacity - @NewCapacity;

            -- Xóa những ghế có ID lớn nhất (giả sử ghế mới thêm vào có ID lớn hơn)
            -- Cách 1 (dùng CTE, an toàn hơn, hoạt động tốt cả khi SeatNumber không phải số)

            ;WITH SeatsToDelete AS (
                SELECT TOP (@SeatsToDelete) SeatID
                FROM Seat
                WHERE CarriageID = @CarriageID
                ORDER BY CAST(SeatNumber AS INT) DESC -- Sắp xếp theo số ghế (dạng số) giảm dần.
            )
            DELETE FROM SeatsToDelete;
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        THROW;  -- Rethrow the error
    END CATCH
END;
GO
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[AddTrainWithCarriages] (
    @p_TrainName NVARCHAR(255),  -- Use NVARCHAR for Vietnamese
    @p_TotalCarriages INT,
    @p_VipCarriages INT
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @v_TrainID INT;
    DECLARE @v_CarriageNumber INT; -- Keep as INT for automatic incrementing
    DECLARE @v_CarriageType NVARCHAR(20); -- Use NVARCHAR
    DECLARE @v_Capacity INT;
    DECLARE @v_Counter INT;

    -- Start transaction
    BEGIN TRANSACTION;

    BEGIN TRY
        -- Insert the train
        INSERT INTO Train (TrainName) VALUES (@p_TrainName);
        SET @v_TrainID = SCOPE_IDENTITY(); -- Get the generated TrainID

        -- Insert carriages
        SET @v_Counter = 1;  -- Counter for carriages
        WHILE @v_Counter <= @p_TotalCarriages
        BEGIN
            -- Determine carriage type and capacity
            IF @v_Counter <= @p_VipCarriages
            BEGIN
                SET @v_CarriageType = N'Toa VIP';  -- Use N'...' for Unicode
                SET @v_Capacity = 12;
            END
            ELSE
            BEGIN
                SET @v_CarriageType = N'Toa Thường'; -- Use N'...' for Unicode
                SET @v_Capacity = 10;
            END;

            -- Carriage Number:  Just use the counter!
            SET @v_CarriageNumber = @v_Counter;

            -- Insert carriage
            INSERT INTO Carriage (TrainID, CarriageNumber, CarriageType, Capacity)
            VALUES (@v_TrainID, @v_CarriageNumber, @v_CarriageType, @v_Capacity);

            -- Get the generated CarriageID
            DECLARE @v_CarriageID INT = SCOPE_IDENTITY();

            -- Insert seats for the carriage
            DECLARE @v_SeatCounter INT = 1;
            WHILE @v_SeatCounter <= @v_Capacity
            BEGIN
                -- Insert Seat, setting SeatType based on CarriageType
                INSERT INTO Seat (CarriageID, SeatNumber, Status, SeatType)
                VALUES (@v_CarriageID, @v_SeatCounter, N'Available', @v_CarriageType);
                SET @v_SeatCounter = @v_SeatCounter + 1;
            END;

            SET @v_Counter = @v_Counter + 1;
        END;

        -- Commit transaction
        COMMIT TRANSACTION;

        -- Return the new TrainID
        SELECT @v_TrainID;

    END TRY
    BEGIN CATCH
        -- If an error occurred, rollback and re-throw
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER EnsureAtLeastOneAdmin
ON [User]
FOR UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra nếu có bản ghi nào trong bảng Inserted (bản ghi sau khi update) thay đổi RoleID từ 1 (Admin) sang giá trị khác
    IF EXISTS (
        SELECT 1 
        FROM INSERTED i
        JOIN DELETED d ON i.UserID = d.UserID
        WHERE d.RoleID = 1 AND i.RoleID != 1  -- Từ Admin sang vai trò khác
    )
    BEGIN
        -- Đếm số lượng Admin còn lại trong bảng User (ngoại trừ bản ghi đang được cập nhật)
        DECLARE @AdminCount INT;
        SELECT @AdminCount = COUNT(*) 
        FROM [User] 
        WHERE RoleID = 1 
        AND UserID NOT IN (SELECT UserID FROM INSERTED);

        -- Nếu không còn Admin nào khác (số lượng = 0)
        IF @AdminCount = 0
        BEGIN
            -- Hủy bỏ cập nhật và thông báo lỗi
            RAISERROR ('Không thể thay đổi vai trò. Hệ thống cần ít nhất một Admin.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END
    END
END;
GO
-- Stored procedure to delete a train IF it's not used in any trips
CREATE OR ALTER PROCEDURE DeleteTrainIfUnused (@TrainID INT)
AS
BEGIN
    SET NOCOUNT ON;

    -- Check if the train is used in any trips
    IF NOT EXISTS (SELECT 1 FROM Trip WHERE TrainID = @TrainID)
    BEGIN
        -- Start a transaction to ensure atomicity
        BEGIN TRANSACTION;

        BEGIN TRY
            -- Delete seats associated with the train's carriages
            DELETE FROM Seat
            WHERE CarriageID IN (SELECT CarriageID FROM Carriage WHERE TrainID = @TrainID);

            -- Delete carriages associated with the train
            DELETE FROM Carriage WHERE TrainID = @TrainID;

            -- Delete the train itself
            DELETE FROM Train WHERE TrainID = @TrainID;

            -- Commit the transaction if all deletions are successful
            COMMIT TRANSACTION;
        END TRY
        BEGIN CATCH
            -- If any error occurred, rollback the transaction
            IF @@TRANCOUNT > 0
                ROLLBACK TRANSACTION;

            -- Re-throw the error  (or handle it as needed)
             THROW;
            -- Optional:  Return an error code or message
            -- SELECT -1 AS ErrorCode, ERROR_MESSAGE() AS ErrorMessage;

        END CATCH
    END
    -- Optional:  Could return 0 if the train was not deleted (because it's used)
    -- ELSE
    --   SELECT 0 AS Result;  -- Train was used, not deleted.
END;
GO
GO
