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
CREATE TABLE AdminAuthorization (
    AuthorizationID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    AuthorizedBy INT NOT NULL,  -- Người phân quyền
    AuthorizationDate DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES [User](UserID),
    FOREIGN KEY (AuthorizedBy) REFERENCES [User](UserID)
);
GO
CREATE TABLE Feedback (
    FeedbackID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT, -- Link to the user who gave feedback
    Content NVARCHAR(MAX) NOT NULL,
    Rating INT,   -- You can add a rating system (e.g., 1-5 stars)
    FeedbackDate DATETIME DEFAULT GETDATE(),
    Status BIT NOT NULL DEFAULT 1, -- e.g., to manage visibility
    FOREIGN KEY (UserID) REFERENCES [User](UserID)
);


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
    TripID INT,  
    RoundTripTripID INT NULL,  
    BookingDate DATETIME DEFAULT CURRENT_TIMESTAMP,  
    TotalPrice DECIMAL(10,2) NOT NULL,  
    PaymentStatus NVARCHAR(50) NOT NULL CHECK (PaymentStatus IN ('Refund', 'Paid', 'Cancelled')),  
    BookingStatus NVARCHAR(50) NOT NULL CHECK (BookingStatus IN ('Active', 'Expired')),  
    FOREIGN KEY (UserID) REFERENCES [User](UserID),
    FOREIGN KEY (TripID) REFERENCES Trip(TripID),
    FOREIGN KEY (RoundTripTripID) REFERENCES Trip(TripID)
);


-- Bảng Ticket  
CREATE TABLE Ticket (  
    TicketID INT PRIMARY KEY IDENTITY(1,1),  
	PassengerName NVARCHAR(255),
    CCCD NVARCHAR(20) NOT NULL,  
    BookingID INT,  
    SeatID INT,  
    TripID INT,  
    TicketPrice DECIMAL(10,2) NOT NULL,  
    TicketStatus NVARCHAR(50) NOT NULL CHECK (TicketStatus IN ('Used', 'Unused', 'Refunded')),  
    FOREIGN KEY (BookingID) REFERENCES Booking(BookingID),  
    FOREIGN KEY (SeatID) REFERENCES Seat(SeatID),  
    FOREIGN KEY (TripID) REFERENCES Trip(TripID)  
);  

-- Bảng OrderDetail  
CREATE TABLE OrderDetail (  
    OrderID INT PRIMARY KEY IDENTITY(1,1),  
    TicketID INT,  
    UserID INT,  
    SeatID INT,  
    TripID INT,  
    OrderDate DATETIME DEFAULT CURRENT_TIMESTAMP,  
    OrderStatus NVARCHAR(50) NOT NULL CHECK (OrderStatus IN ('Pending', 'Completed', 'Cancelled')),  
    FOREIGN KEY (TicketID) REFERENCES Ticket(TicketID),  
    FOREIGN KEY (UserID) REFERENCES [User](UserID),  
    FOREIGN KEY (SeatID) REFERENCES Seat(SeatID),  
    FOREIGN KEY (TripID) REFERENCES Trip(TripID)  
);
-- Bảng Blog  
CREATE TABLE [dbo].[Blog](
	[blog_id] [int] IDENTITY(1,1) NOT NULL,
	[title] [nvarchar](max) NULL,
	[UserID] [int] NULL,
	[update_date] [date] NULL,
	[content] [nvarchar](max) NULL,
	[thumbnail] [nvarchar](max) NULL,
	[brief_infor] [nvarchar](max) NULL,
	[categoryBlog_id] [int] NULL,
	[status] [bit] NULL,
	FOREIGN KEY (UserID) REFERENCES [User](UserID), 
 CONSTRAINT [PK_Blog] PRIMARY KEY CLUSTERED 
(
	[blog_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
CREATE TABLE [dbo].[CategoryBlog](
	[categoryBlog_id] [int] IDENTITY(1,1) NOT NULL,
	[categoryBlogName] [nvarchar](100) NULL,
	[status] [bit] NULL,
 CONSTRAINT [PK_CategoryBlog] PRIMARY KEY CLUSTERED 
(
	[categoryBlog_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
Go
ALTER TABLE Blog
ADD CONSTRAINT FK_CategoryBlog
FOREIGN KEY (categoryBlog_id)
REFERENCES CategoryBlog(categoryBlog_id);
GO
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
VALUES ('admin', 'admin123', 'Admin User', 'admin@example.com', '0123456789','Vinh Phuc', 1,1),
('customer', 'customer123', 'Customer User', 'customer@example.com', '0987654321','Ha Noi', 3,1),
('dungdung', '123456@', N'Nguyễn Mạnh Dũng', 'dungnmhe173094@fpt.edu.vn', '0866435003','Vinh Phuc', 3,1),
('employer', 'employer123', 'Employer User', 'employer@example.com', '0912345678','Hai Phong', 2,1);

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
(1, 2, 150, 200000.00),
(2,1,150,200000.00),
(2, 3, 100, 150000.00),
(3, 4, 200, 300000.00),
(4, 5, 180, 250000.00),
(5, 6, 220, 350000.00),
(6, 7, 160, 200000.00),
(7, 8, 140, 180000.00),
(8, 1, 190, 280000.00); -- Sửa ArrivalStationID để đảm bảo tồn tại


-- Thêm dữ liệu cho bảng Trip (10 dòng)
-- Thêm chuyến đi một chiều
INSERT INTO Trip (TrainID, RouteID, DepartureTime, ArrivalTime, TripStatus, TripType) 
VALUES 
(1, 1, '2025-02-15 08:00:00', '2025-02-15 10:00:00', 'Scheduled', 1), -- Chuyến đi
(2, 2, '2025-02-15 18:00:00', '2025-02-15 20:00:00', 'Scheduled', 2), -- Chuyến về
(3, 3, '2025-02-17 07:00:00', '2025-02-17 09:00:00', 'Scheduled', 1),
(4, 4, '2025-02-18 10:00:00', '2025-02-18 12:00:00', 'Scheduled', 1),
(5, 5, '2025-02-19 06:30:00', '2025-02-19 08:30:00', 'Scheduled', 1),
(1, 6, '2025-02-20 07:45:00', '2025-02-20 09:45:00', 'Scheduled', 1),
(2, 7, '2025-02-21 08:15:00', '2025-02-21 10:15:00', 'Scheduled', 1),
(3, 8, '2025-02-22 09:30:00', '2025-02-22 11:30:00', 'Scheduled', 1);

-- Cập nhật RoundTripReference cho chuyến đi khứ hồi (chuyến về tham chiếu chuyến đi)

DECLARE @CurrentDate DATE = '2025-03-06';
DECLARE @EndDate    DATE = '2025-03-15';

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
N'Bạn muốn đặt vé tàu nhanh chóng mà không cần ra ga? Trong bài viết này, chúng tôi sẽ hướng dẫn bạn từng bước cách đặt vé tàu trực tuyến trên website chính thức. Từ việc lựa chọn chuyến đi, nhập thông tin hành khách đến bước thanh toán an toàn, tất cả sẽ được trình bày chi tiết để bạn có thể đặt vé dễ dàng ngay tại nhà.', 
'thumbnail1.jpg', 
N'Hướng dẫn chi tiết cách đặt vé tàu online, giúp bạn tiết kiệm thời gian và đảm bảo có vé cho hành trình của mình.', 
2, 1),  

(N'Lịch trình tàu Tết 2025', 2, '2025-02-25', 
N'Tết Nguyên Đán là thời điểm nhu cầu đi lại tăng cao, vì vậy việc nắm rõ lịch trình tàu là rất quan trọng. Trong bài viết này, chúng tôi sẽ cập nhật chi tiết lịch trình tàu Tết 2025, bao gồm các tuyến đường chính, thời gian khởi hành, giá vé, và những lưu ý quan trọng giúp bạn có một chuyến đi suôn sẻ.', 
'thumbnail2.jpg', 
N'Thông tin chi tiết về lịch trình tàu dịp Tết 2025, giúp bạn chủ động sắp xếp kế hoạch di chuyển hợp lý.', 
1, 1),  

(N'Ưu đãi 30% khi đặt vé trước', 2, '2025-02-20', 
N'Bạn đang lên kế hoạch cho chuyến đi sắp tới? Đừng bỏ lỡ cơ hội nhận ưu đãi lên đến 30% khi đặt vé tàu trước. Chương trình khuyến mãi này áp dụng cho nhiều tuyến đường và có số lượng vé giới hạn. Hãy đọc ngay bài viết để biết cách đặt vé và tận dụng ưu đãi đặc biệt này!', 
'thumbnail3.jpg', 
N'Cơ hội tiết kiệm chi phí với ưu đãi giảm giá 30% khi đặt vé tàu sớm – thông tin chi tiết trong bài viết.', 
3, 1),  

(N'Những điểm đến hấp dẫn khi đi tàu', 2, '2025-02-18', 
N'Đi tàu không chỉ là một phương tiện di chuyển mà còn là một trải nghiệm tuyệt vời để khám phá những điểm đến thú vị. Từ những bãi biển tuyệt đẹp, vùng núi hùng vĩ đến các thành phố sầm uất, hãy cùng khám phá danh sách những địa điểm đáng ghé thăm nhất khi đi tàu trong bài viết này.', 
'thumbnail4.jpg', 
N'Danh sách những điểm du lịch hấp dẫn khi đi tàu – từ thiên nhiên hùng vĩ đến thành phố sôi động.', 
4, 1);  
-- Cập nhật bảng CategoryRule với nội dung chi tiết hơn
INSERT INTO CategoryRule (CategoryRuleName, Content, Img, Update_Date, Status)
VALUES 
(N'Các Điều Kiện & Điều Khoản', 
 N'Nội dung về các điều kiện và điều khoản sử dụng dịch vụ của công ty. Điều khoản này quy định quyền lợi và nghĩa vụ của khách hàng khi tham gia sử dụng các dịch vụ do công ty cung cấp. Những điều khoản này áp dụng đối với mọi giao dịch giữa khách hàng và công ty, bao gồm việc đăng ký tài khoản, lựa chọn dịch vụ, thanh toán, quyền và nghĩa vụ trong việc hủy hoặc hoàn lại dịch vụ. Các điều kiện này sẽ được cập nhật định kỳ và khách hàng cần đồng ý với những điều kiện mới khi tiếp tục sử dụng dịch vụ. Ngoài ra, các điều kiện áp dụng cho các chương trình khuyến mãi, giảm giá, hoặc ưu đãi đặc biệt cũng sẽ được đề cập rõ ràng trong phần này. Mọi khách hàng đều có quyền yêu cầu thông tin rõ ràng về các điều kiện, và yêu cầu hoàn lại dịch vụ nếu có bất kỳ điều gì không minh bạch.', 
 NULL, GETDATE(), 1),
(N'Phương thức thanh toán', 
 N'Phương thức thanh toán là các cách thức mà khách hàng có thể sử dụng để thực hiện thanh toán cho các dịch vụ do công ty cung cấp. Các phương thức thanh toán bao gồm thẻ tín dụng, thẻ ghi nợ, chuyển khoản qua ngân hàng, và các hệ thống thanh toán trực tuyến như PayPal, MoMo, ZaloPay, v.v. Mỗi phương thức thanh toán có ưu điểm và hạn chế riêng. Ví dụ, thanh toán qua thẻ tín dụng nhanh chóng nhưng có thể mất phí giao dịch, trong khi chuyển khoản ngân hàng có thể mất vài ngày để hoàn tất. Quy trình thanh toán cũng bao gồm các bước xác nhận đơn hàng, kiểm tra tính hợp lệ của các thông tin thanh toán, và xử lý giao dịch qua các cổng thanh toán an toàn. Khách hàng cần đảm bảo rằng thông tin thanh toán của mình là chính xác để tránh các sự cố trong quá trình giao dịch. Các điều kiện hoàn tiền, hủy giao dịch hoặc tranh chấp giao dịch sẽ được giải quyết theo quy trình rõ ràng đã quy định trong điều khoản này.', 
 NULL, GETDATE(), 1),
(N'Chính sách hoàn trả vé', 
 N'Chính sách hoàn trả vé được áp dụng trong các trường hợp khách hàng muốn hủy vé sau khi đã thanh toán. Chính sách này quy định các điều kiện hoàn trả, bao gồm thời gian áp dụng hoàn trả (thường là trong vòng 30 ngày kể từ ngày mua vé), các khoản phí hoàn trả có thể bị trừ (nếu có), và quy trình thực hiện yêu cầu hoàn trả. Ví dụ, nếu khách hàng yêu cầu hoàn vé trong vòng 24 giờ sau khi mua, toàn bộ số tiền sẽ được hoàn lại. Tuy nhiên, nếu yêu cầu hoàn vé sau thời gian này, công ty có thể thu phí hủy vé. Chính sách này cũng bao gồm các trường hợp đặc biệt, chẳng hạn như khách hàng không thể sử dụng vé do lý do bất khả kháng (ví dụ: thiên tai, dịch bệnh, sự cố ngoài ý muốn). Khi đó, công ty sẽ xem xét hoàn lại vé theo từng trường hợp cụ thể, và có thể yêu cầu giấy tờ chứng minh tình huống khẩn cấp. Quy trình yêu cầu hoàn trả vé sẽ được hướng dẫn rõ ràng trên website, và khách hàng cần điền đầy đủ thông tin trong biểu mẫu yêu cầu hoàn vé.', 
 NULL, GETDATE(), 1),
(N'Chính sách Bảo Mật Thông Tin', 
 N'Chính sách bảo mật thông tin cá nhân là cam kết của công ty trong việc bảo vệ dữ liệu cá nhân của khách hàng. Các thông tin như tên, địa chỉ, số điện thoại, thông tin thanh toán và các dữ liệu nhạy cảm khác sẽ chỉ được sử dụng với mục đích cung cấp dịch vụ và không bao giờ được chia sẻ cho bên thứ ba mà không có sự đồng ý của khách hàng. Công ty cam kết tuân thủ các quy định của pháp luật về bảo vệ thông tin cá nhân, và sử dụng các biện pháp bảo mật tiên tiến như mã hóa dữ liệu, hệ thống tường lửa và các công cụ giám sát để ngăn chặn truy cập trái phép. Khách hàng cũng có quyền yêu cầu quyền truy cập vào thông tin cá nhân của mình, yêu cầu sửa chữa hoặc xóa bỏ dữ liệu khi không còn cần thiết. Chính sách này cũng sẽ mô tả về cách thức công ty thông báo cho khách hàng trong trường hợp có sự cố bảo mật xảy ra, và những biện pháp khắc phục mà công ty sẽ thực hiện.', 
 NULL, GETDATE(), 1);

 -- Thêm 3 Employee vào bảng User
INSERT INTO [User] (Username, Password, FullName, Email, PhoneNumber, Address, RoleID, Status)
VALUES
    ('employee1', 'employee123', 'Employee One', 'employee1@example.com', '0111111111', 'Employee Address 1', 2, 1),
    ('employee2', 'employee123', 'Employee Two', 'employee2@example.com', '0222222222', 'Employee Address 2', 2, 1),
    ('employee3', 'employee123', 'Employee Three', 'employee3@example.com', '0333333333', 'Employee Address 3', 2, 1);

-- Thêm 10 Customer vào bảng User
INSERT INTO [User] (Username, Password, FullName, Email, PhoneNumber, Address, RoleID, Status)
VALUES
    ('customer4', 'customer123', 'Customer Four', 'customer4@example.com', '0444444444', 'Customer Address 4', 3, 1),
    ('customer5', 'customer123', 'Customer Five', 'customer5@example.com', '0555555555', 'Customer Address 5', 3, 1),
    ('customer6', 'customer123', 'Customer Six', 'customer6@example.com', '0666666666', 'Customer Address 6', 3, 1),
    ('customer7', 'customer123', 'Customer Seven', 'customer7@example.com', '0777777777', 'Customer Address 7', 3, 1),
    ('customer8', 'customer123', 'Customer Eight', 'customer8@example.com', '0888888888', 'Customer Address 8', 3, 1),
    ('customer9', 'customer123', 'Customer Nine', 'customer9@example.com', '0999999999', 'Customer Address 9', 3, 1),
    ('customer10', 'customer123', 'Customer Ten', 'customer10@example.com', '1010101010', 'Customer Address 10', 3, 1),
    ('customer11', 'customer123', 'Customer Eleven', 'customer11@example.com', '1111111111', 'Customer Address 11', 3, 1),
    ('customer12', 'customer123', 'Customer Twelve', 'customer12@example.com', '1212121212', 'Customer Address 12', 3, 1),
    ('customer13', 'customer123', 'Customer Thirteen', 'customer13@example.com', '1313131313', 'Customer Address 13', 3, 1);

-- Cập nhật bảng Rule với nội dung chi tiết hơn
INSERT INTO [Rule] (Title, Content, Img, Update_Date, Status, UserID, CategoryRuleID)
VALUES 
(N'Quy định vận chuyển', 
 N'Quy định vận chuyển được áp dụng đối với tất cả các dịch vụ vận chuyển do công ty cung cấp. Quy định này bao gồm thông tin chi tiết về các phương thức vận chuyển, từ giao hàng nhanh qua các công ty vận chuyển lớn đến các hình thức giao hàng trực tiếp tại cửa. Các khách hàng cần cung cấp thông tin chính xác về địa chỉ giao hàng và thời gian nhận hàng để đảm bảo việc vận chuyển diễn ra suôn sẻ. Ngoài ra, công ty sẽ thông báo cho khách hàng về tình trạng vận chuyển qua email hoặc SMS, bao gồm thông tin về lộ trình, thời gian dự kiến giao hàng và các thay đổi (nếu có). Trong trường hợp khách hàng không nhận được hàng trong thời gian quy định, công ty cam kết sẽ tiến hành điều tra và xử lý yêu cầu bồi thường nếu có lỗi từ phía công ty hoặc đối tác vận chuyển. Các trường hợp bất khả kháng như thời tiết xấu hoặc tình trạng giao thông tắc nghẽn sẽ được xử lý theo từng trường hợp cụ thể và có thể dẫn đến việc giao hàng bị trễ.', 
 NULL, GETDATE(), 1, 1, 1),
(N'Điều kiện sử dụng hệ thống mua vé trực tuyến', 
 N'Điều kiện sử dụng hệ thống mua vé trực tuyến là những yêu cầu mà khách hàng cần đáp ứng khi sử dụng nền tảng mua vé của công ty. Khách hàng phải tạo tài khoản, cung cấp thông tin cá nhân chính xác, và đồng ý với các điều khoản và điều kiện khi sử dụng hệ thống. Sau khi đăng ký, khách hàng có thể chọn dịch vụ, thanh toán trực tuyến và nhận vé điện tử. Các khách hàng có thể lưu trữ vé trong tài khoản và sử dụng chúng cho các dịch vụ tương lai. Điều khoản này cũng bao gồm việc khách hàng phải chịu trách nhiệm đối với việc bảo mật tài khoản và mật khẩu, và không được phép chia sẻ tài khoản của mình với người khác. Hệ thống cũng cung cấp các chức năng quản lý vé như việc hủy hoặc thay đổi vé nếu có sự cố, và khách hàng phải tuân theo các điều kiện áp dụng cho việc thay đổi hoặc hủy vé. Các quy định về thanh toán và hoàn trả cũng được quy định rõ ràng trong điều kiện này.', 
 NULL, GETDATE(), 1, 2, 1),
(N'Điều khoản sử dụng website Ticket Train Booking', 
 N'Điều khoản sử dụng website Ticket Train Booking cung cấp các thông tin chi tiết về việc sử dụng dịch vụ trên website của công ty. Khi khách hàng truy cập vào website, họ đồng ý với các điều khoản và điều kiện sử dụng mà công ty đã đặt ra. Điều này bao gồm việc khách hàng không được phép sử dụng website cho các mục đích phi pháp, không tải lên các nội dung xâm phạm bản quyền, và không can thiệp vào hoạt động của website. Công ty cũng cam kết bảo vệ thông tin cá nhân của khách hàng theo chính sách bảo mật đã được công bố. Các quyền và nghĩa vụ của khách hàng trong việc sử dụng các dịch vụ trên website cũng sẽ được giải thích rõ ràng trong phần này, bao gồm việc tuân thủ các quy định về thanh toán, hoàn tiền, và sử dụng các dịch vụ bổ sung nếu có.', 
 NULL, GETDATE(), 1, 2, 1),
(N'Chính sách hoàn trả vé, đổi vé', 
 N'Chính sách hoàn trả vé và đổi vé bao gồm các quy định liên quan đến việc yêu cầu hoàn trả vé sau khi thanh toán hoặc đổi vé trong các trường hợp cần thiết. Các khách hàng có quyền yêu cầu hoàn tiền nếu vé không sử dụng được trong thời gian quy định, hoặc khi có sự thay đổi về lịch trình từ phía công ty. Chính sách này cũng bao gồm các điều kiện đổi vé, ví dụ như đổi vé trong vòng 24 giờ trước giờ khởi hành sẽ không tính phí. Tuy nhiên, nếu đổi vé muộn hơn, khách hàng có thể phải chịu một khoản phí dịch vụ. Quy trình đổi và hoàn vé được thực hiện qua các kênh hỗ trợ khách hàng và thông qua hệ thống của công ty, và khách hàng cần tuân thủ quy trình này để yêu cầu hoàn trả hoặc đổi vé.', 
 NULL, GETDATE(), 1, 2, 3),
(N'Quy định thời gian hoàn tiền', 
 N'Quy định thời gian hoàn tiền bao gồm các điều kiện và thời gian yêu cầu hoàn lại tiền cho khách hàng trong các trường hợp như hủy vé, không thể sử dụng dịch vụ do lý do bất khả kháng, hoặc khi khách hàng yêu cầu hoàn tiền sau khi sử dụng dịch vụ. Thời gian hoàn tiền có thể thay đổi tùy theo phương thức thanh toán mà khách hàng đã sử dụng. Ví dụ, nếu thanh toán qua thẻ tín dụng, thời gian hoàn tiền có thể từ 5 đến 10 ngày làm việc, trong khi thanh toán qua chuyển khoản ngân hàng có thể mất từ 7 đến 15 ngày. Chính sách hoàn tiền cũng quy định về các trường hợp không đủ điều kiện hoàn tiền, chẳng hạn như khi khách hàng không tuân thủ quy định về hủy vé hoặc thay đổi dịch vụ trong thời gian quy định.', 
 NULL, GETDATE(), 1, 2, 3);

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
