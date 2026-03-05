package com.conceptcoding.myprjects;

// 1. Enums
enum FuelType {
    ELECTRIC, PETROL, DIESEL, CNG
}

enum BookingStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED
}

enum PaymentStatus {
    PENDING, SUCCESS, FAILED, REFUNDED
}

// 2. Car - single source of truth
class Car {
    private String vehicleNo;
    private String brand;
    private String model;
    private int seater;
    private FuelType fuelType;
    private int pricePerDay;
    private boolean available;
    private int currentKms;
    
    Car(String vehicleNo, String brand, String model, int seater, 
        FuelType fuelType, int pricePerDay) {
        this.vehicleNo = vehicleNo;
        this.brand = brand;
        this.model = model;
        this.seater = seater;
        this.fuelType = fuelType;
        this.pricePerDay = pricePerDay;
        this.available = true;
        this.currentKms = 0;
    }
    
    void markUnavailable() { this.available = false; }
    void markAvailable() { this.available = true; }
    
    boolean isAvailable() { return available; }
    int getPricePerDay() { return pricePerDay; }
    int getSeater() { return seater; }
    String getVehicleNo() { return vehicleNo; }
    // ... other getters
}

// 3. CarManager - manages car inventory
class CarManager {
    private List<Car> cars;
    
    CarManager() {
        this.cars = new ArrayList<>();
    }
    
    void addCar(Car car) {
        cars.add(car);
    }
    
    void removeCar(String vehicleNo) {
        cars.removeIf(c -> c.getVehicleNo().equals(vehicleNo));
    }
    
    List<Car> getAvailableCars() {
        return cars.stream()
            .filter(Car::isAvailable)
            .collect(Collectors.toList());
    }
    
    List<Car> getAvailableCars(int seater, FuelType fuelType) {
        return cars.stream()
            .filter(Car::isAvailable)
            .filter(c -> c.getSeater() == seater)
            .filter(c -> c.getFuelType() == fuelType)
            .collect(Collectors.toList());
    }
    
    Car getCarByVehicleNo(String vehicleNo) {
        return cars.stream()
            .filter(c -> c.getVehicleNo().equals(vehicleNo))
            .findFirst()
            .orElseThrow(() -> new CarNotFoundException(vehicleNo));
    }
}

// 4. User
class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String drivingLicense;
    
    User(String userId, String name, String email, String phone, String drivingLicense) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.drivingLicense = drivingLicense;
    }
    
    String getUserId() { return userId; }
    String getName() { return name; }
    String getDrivingLicense() { return drivingLicense; }
}

// 5. Booking - complete information
class Booking {
    private String bookingId;
    private Car car;
    private User user;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int totalPrice;
    private BookingStatus status;
    private Payment payment;
    private LocalDateTime createdAt;
    
    private Booking(BookingBuilder builder) {
        this.bookingId = builder.bookingId;
        this.car = builder.car;
        this.user = builder.user;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.totalPrice = builder.totalPrice;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    void confirmBooking(Payment payment) {
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            this.status = BookingStatus.CONFIRMED;
            this.payment = payment;
            car.markUnavailable();
        }
    }
    
    void cancelBooking() {
        this.status = BookingStatus.CANCELLED;
        car.markAvailable();
    }
    
    void completeBooking() {
        this.status = BookingStatus.COMPLETED;
        car.markAvailable();
    }
    
    // Getters
    String getBookingId() { return bookingId; }
    Car getCar() { return car; }
    BookingStatus getStatus() { return status; }
    int getTotalPrice() { return totalPrice; }
    
    // Builder pattern
    static class BookingBuilder {
        private String bookingId;
        private Car car;
        private User user;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private int totalPrice;
        
        BookingBuilder setBookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }
        
        BookingBuilder setCar(Car car) {
            this.car = car;
            return this;
        }
        
        BookingBuilder setUser(User user) {
            this.user = user;
            return this;
        }
        
        BookingBuilder setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }
        
        BookingBuilder setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }
        
        BookingBuilder setTotalPrice(int totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }
        
        Booking build() {
            return new Booking(this);
        }
    }
}

// 6. Payment
class Payment {
    private String paymentId;
    private String bookingId;
    private PaymentMethod method;
    private int amount;
    private PaymentStatus status;
    private LocalDateTime timestamp;
    
    Payment(String paymentId, String bookingId, PaymentMethod method, 
            int amount, PaymentStatus status) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
    
    PaymentStatus getStatus() { return status; }
    String getPaymentId() { return paymentId; }
}

// 7. PaymentMethod - Strategy pattern
interface PaymentMethod {
    PaymentStatus processPayment(int amount);
    String getMethodName();
}

class CreditCardPayment implements PaymentMethod {
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    
    CreditCardPayment(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }
    
    @Override
    public PaymentStatus processPayment(int amount) {
        // Simulate payment processing
        System.out.println("Processing credit card payment of " + amount);
        // Call payment gateway API
        return PaymentStatus.SUCCESS;
    }
    
    @Override
    public String getMethodName() {
        return "Credit Card";
    }
}

class UPIPayment implements PaymentMethod {
    private String upiId;
    
    UPIPayment(String upiId) {
        this.upiId = upiId;
    }
    
    @Override
    public PaymentStatus processPayment(int amount) {
        System.out.println("Processing UPI payment of " + amount);
        return PaymentStatus.SUCCESS;
    }
    
    @Override
    public String getMethodName() {
        return "UPI";
    }
}

// 8. Pricing Strategy
interface PriceCalculatorStrategy {
    int calculatePrice(Car car, long days);
}

class StandardPricingStrategy implements PriceCalculatorStrategy {
    @Override
    public int calculatePrice(Car car, long days) {
        return car.getPricePerDay() * (int)days;
    }
}

class WeekendPricingStrategy implements PriceCalculatorStrategy {
    @Override
    public int calculatePrice(Car car, long days) {
        int basePrice = car.getPricePerDay() * (int)days;
        return (int)(basePrice * 1.2);  // 20% surge on weekends
    }
}

// 9. PricingService
class PricingService {
    private PriceCalculatorStrategy strategy;
    
    PricingService(PriceCalculatorStrategy strategy) {
        this.strategy = strategy;
    }
    
    int calculatePrice(Car car, LocalDateTime start, LocalDateTime end) {
        long days = ChronoUnit.DAYS.between(start, end);
        if (days == 0) days = 1;  // Minimum 1 day
        return strategy.calculatePrice(car, days);
    }
}

// 10. PaymentService
class PaymentService {
    Payment processPayment(PaymentMethod method, int amount, String bookingId) {
        String paymentId = "PAY" + System.currentTimeMillis();
        
        try {
            PaymentStatus status = method.processPayment(amount);
            return new Payment(paymentId, bookingId, method, amount, status);
        } catch (Exception e) {
            return new Payment(paymentId, bookingId, method, amount, PaymentStatus.FAILED);
        }
    }
}

// 11. BookingManager
class BookingManager {
    private List<Booking> bookings;
    
    BookingManager() {
        this.bookings = new ArrayList<>();
    }
    
    void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
    Booking getBookingById(String bookingId) {
        return bookings.stream()
            .filter(b -> b.getBookingId().equals(bookingId))
            .findFirst()
            .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }
    
    List<Booking> getUserBookings(String userId) {
        return bookings.stream()
            .filter(b -> b.getUser().getUserId().equals(userId))
            .collect(Collectors.toList());
    }
}

// 12. BookingService - CORE ORCHESTRATOR
class BookingService {
    private BookingManager bookingManager;
    private CarManager carManager;
    private PricingService pricingService;
    private PaymentService paymentService;
    
    BookingService(BookingManager bookingManager, CarManager carManager,
                   PricingService pricingService, PaymentService paymentService) {
        this.bookingManager = bookingManager;
        this.carManager = carManager;
        this.pricingService = pricingService;
        this.paymentService = paymentService;
    }
    
    Booking createBooking(String vehicleNo, User user, LocalDateTime start, 
                         LocalDateTime end, PaymentMethod paymentMethod) {
        // 1. Get car and check availability
        Car car = carManager.getCarByVehicleNo(vehicleNo);
        if (!car.isAvailable()) {
            throw new CarNotAvailableException(vehicleNo);
        }
        
        // 2. Calculate price
        int totalPrice = pricingService.calculatePrice(car, start, end);
        
        // 3. Create booking
        String bookingId = "BK" + System.currentTimeMillis();
        Booking booking = new Booking.BookingBuilder()
            .setBookingId(bookingId)
            .setCar(car)
            .setUser(user)
            .setStartDate(start)
            .setEndDate(end)
            .setTotalPrice(totalPrice)
            .build();
        
        // 4. Process payment
        Payment payment = paymentService.processPayment(paymentMethod, totalPrice, bookingId);
        
        // 5. Confirm booking if payment successful
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            booking.confirmBooking(payment);
            bookingManager.addBooking(booking);
            System.out.println("Booking confirmed! Booking ID: " + bookingId);
        } else {
            throw new PaymentFailedException("Payment failed for booking " + bookingId);
        }
        
        return booking;
    }
    
    void cancelBooking(String bookingId) {
        Booking booking = bookingManager.getBookingById(bookingId);
        booking.cancelBooking();
        System.out.println("Booking cancelled: " + bookingId);
    }
    
    void completeBooking(String bookingId) {
        Booking booking = bookingManager.getBookingById(bookingId);
        booking.completeBooking();
        System.out.println("Booking completed: " + bookingId);
    }
}

// 13. CarRentalService - Main facade
class CarRentalService {
    private CarManager carManager;
    private BookingService bookingService;
    
    CarRentalService(CarManager carManager, BookingService bookingService) {
        this.carManager = carManager;
        this.bookingService = bookingService;
    }
    
    List<Car> searchAvailableCars() {
        return carManager.getAvailableCars();
    }
    
    List<Car> searchAvailableCars(int seater, FuelType fuelType) {
        return carManager.getAvailableCars(seater, fuelType);
    }
    
    Booking bookCar(String vehicleNo, User user, LocalDateTime start, 
                    LocalDateTime end, PaymentMethod paymentMethod) {
        return bookingService.createBooking(vehicleNo, user, start, end, paymentMethod);
    }
    
    void cancelBooking(String bookingId) {
        bookingService.cancelBooking(bookingId);
    }
}

// 14. Main - Demo
class Main {
    public static void main(String[] args) {
        // Initialize
        CarManager carManager = new CarManager();
        BookingManager bookingManager = new BookingManager();
        PricingService pricingService = new PricingService(new StandardPricingStrategy());
        PaymentService paymentService = new PaymentService();
        
        BookingService bookingService = new BookingService(
            bookingManager, carManager, pricingService, paymentService
        );
        
        CarRentalService rentalService = new CarRentalService(carManager, bookingService);
        
        // Add cars
        Car car1 = new Car("KA01AB1234", "Toyota", "Innova", 7, FuelType.DIESEL, 2000);
        Car car2 = new Car("KA02CD5678", "Maruti", "Swift", 5, FuelType.PETROL, 1000);
        carManager.addCar(car1);
        carManager.addCar(car2);
        
        // Search cars
        List<Car> available = rentalService.searchAvailableCars();
        System.out.println("Available cars: " + available.size());
        
        // Create user
        User user = new User("U001", "John Doe", "john@email.com", "9876543210", "DL123456");
        
        // Book car
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(3);
        PaymentMethod payment = new UPIPayment("john@upi");
        
        Booking booking = rentalService.bookCar("KA01AB1234", user, start, end, payment);
        System.out.println("Booking created: " + booking.getBookingId());
    }
}