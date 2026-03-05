com.carrental
├── domain
│   ├── model (entities, value objects, aggregates)
│   ├── repository (interfaces)
│   ├── service (domain services)
│   └── event (domain events)
├── application
│   ├── dto (request/response)
│   ├── service (application services/use cases)
│   └── command (CQRS commands)
├── infrastructure
│   ├── persistence (repository implementations)
│   ├── messaging (event publishers)
│   ├── external (payment gateway, notifications)
│   └── config
└── api
    └── rest (controllers)
	

// Value Objects - immutable, no identity
@Value
@Builder
public class VehicleId {
    String value;
    
    public static VehicleId generate() {
        return new VehicleId(UUID.randomUUID().toString());
    }
    
    public static VehicleId from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("VehicleId cannot be empty");
        }
        return new VehicleId(value);
    }
}

@Value
@Builder
public class Money {
    BigDecimal amount;
    Currency currency;
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException();
        }
        return new Money(amount.add(other.amount), currency);
    }
    
    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }
    
    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }
    
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
}

@Value
@Builder
public class DateRange {
    LocalDateTime startDate;
    LocalDateTime endDate;
    
    public long getDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    public boolean overlaps(DateRange other) {
        return !this.endDate.isBefore(other.startDate) 
            && !this.startDate.isAfter(other.endDate);
    }
    
    public void validate() {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
    }
}

@Value
public class VehicleSpecification {
    String brand;
    String model;
    int seatingCapacity;
    FuelType fuelType;
    TransmissionType transmissionType;
    List<Feature> features;
}

public enum Feature {
    GPS, BLUETOOTH, SUNROOF, LEATHER_SEATS, 
    BACKUP_CAMERA, CRUISE_CONTROL
}

// Aggregate Root - Vehicle
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle {
    @EmbeddedId
    private VehicleId id;
    
    @Embedded
    private VehicleSpecification specification;
    
    @Embedded
    private Money dailyRate;
    
    private String registrationNumber;
    
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    
    private String currentLocation;
    
    @Version
    private Long version;  // Optimistic locking
    
    @Embedded
    private AuditInfo auditInfo;
    
    // Business logic methods
    public void markAsRented() {
        if (status != VehicleStatus.AVAILABLE) {
            throw new VehicleNotAvailableException(id);
        }
        this.status = VehicleStatus.RENTED;
        registerEvent(new VehicleRentedEvent(id));
    }
    
    public void markAsAvailable() {
        if (status == VehicleStatus.MAINTENANCE) {
            throw new VehicleInMaintenanceException(id);
        }
        this.status = VehicleStatus.AVAILABLE;
        registerEvent(new VehicleAvailableEvent(id));
    }
    
    public void sendToMaintenance() {
        this.status = VehicleStatus.MAINTENANCE;
        registerEvent(new VehicleMaintenanceScheduledEvent(id));
    }
    
    public boolean isAvailableForPeriod(DateRange period) {
        return status == VehicleStatus.AVAILABLE;
    }
    
    // Domain events
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearEvents() {
        domainEvents.clear();
    }
}

// Aggregate Root - Booking
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {
    @EmbeddedId
    private BookingId id;
    
    @Embedded
    private VehicleId vehicleId;
    
    @Embedded
    private UserId userId;
    
    @Embedded
    private DateRange rentalPeriod;
    
    @Embedded
    private Money totalAmount;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    @Embedded
    private PaymentInfo paymentInfo;
    
    @Version
    private Long version;
    
    @Embedded
    private AuditInfo auditInfo;
    
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    // Factory method
    public static Booking create(VehicleId vehicleId, UserId userId, 
                                 DateRange period, Money amount) {
        period.validate();
        
        Booking booking = new Booking();
        booking.id = BookingId.generate();
        booking.vehicleId = vehicleId;
        booking.userId = userId;
        booking.rentalPeriod = period;
        booking.totalAmount = amount;
        booking.status = BookingStatus.PENDING;
        booking.auditInfo = AuditInfo.create();
        
        booking.registerEvent(new BookingCreatedEvent(booking.id, vehicleId, userId));
        return booking;
    }
    
    // State transitions with invariants
    public void confirm(PaymentInfo paymentInfo) {
        assertStatus(BookingStatus.PENDING);
        assertPaymentSuccess(paymentInfo);
        
        this.status = BookingStatus.CONFIRMED;
        this.paymentInfo = paymentInfo;
        this.auditInfo = auditInfo.update();
        
        registerEvent(new BookingConfirmedEvent(id, vehicleId));
    }
    
    public void cancel(String reason) {
        assertCancellable();
        
        BookingStatus previousStatus = this.status;
        this.status = BookingStatus.CANCELLED;
        this.auditInfo = auditInfo.update();
        
        registerEvent(new BookingCancelledEvent(id, vehicleId, reason, previousStatus));
    }
    
    public void complete() {
        assertStatus(BookingStatus.CONFIRMED);
        
        if (LocalDateTime.now().isBefore(rentalPeriod.getEndDate())) {
            throw new BookingNotYetCompletedException(id);
        }
        
        this.status = BookingStatus.COMPLETED;
        this.auditInfo = auditInfo.update();
        
        registerEvent(new BookingCompletedEvent(id, vehicleId));
    }
    
    // Invariant checks
    private void assertStatus(BookingStatus expected) {
        if (this.status != expected) {
            throw new InvalidBookingStateException(id, expected, this.status);
        }
    }
    
    private void assertCancellable() {
        if (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED) {
            throw new BookingNotCancellableException(id, status);
        }
    }
    
    private void assertPaymentSuccess(PaymentInfo paymentInfo) {
        if (paymentInfo.getStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentNotSuccessfulException(paymentInfo.getPaymentId());
        }
    }
    
    // Events
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearEvents() {
        domainEvents.clear();
    }
}

@Embeddable
@Value
@Builder
public class AuditInfo {
    LocalDateTime createdAt;
    String createdBy;
    LocalDateTime updatedAt;
    String updatedBy;
    
    public static AuditInfo create() {
        String currentUser = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();
        return new AuditInfo(now, currentUser, now, currentUser);
    }
    
    public AuditInfo update() {
        String currentUser = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        return new AuditInfo(createdAt, createdBy, LocalDateTime.now(), currentUser);
    }
}

@Embeddable
@Value
@Builder
public class PaymentInfo {
    String paymentId;
    PaymentMethod method;
    PaymentStatus status;
    LocalDateTime processedAt;
    String transactionReference;
}



// Base event
public interface DomainEvent {
    String getEventId();
    LocalDateTime getOccurredAt();
    String getAggregateId();
}

@Value
@Builder
public class BookingCreatedEvent implements DomainEvent {
    String eventId = UUID.randomUUID().toString();
    LocalDateTime occurredAt = LocalDateTime.now();
    BookingId bookingId;
    VehicleId vehicleId;
    UserId userId;
    
    @Override
    public String getAggregateId() {
        return bookingId.getValue();
    }
}

@Value
@Builder
public class BookingConfirmedEvent implements DomainEvent {
    String eventId = UUID.randomUUID().toString();
    LocalDateTime occurredAt = LocalDateTime.now();
    BookingId bookingId;
    VehicleId vehicleId;
    
    @Override
    public String getAggregateId() {
        return bookingId.getValue();
    }
}

@Value
@Builder
public class BookingCancelledEvent implements DomainEvent {
    String eventId = UUID.randomUUID().toString();
    LocalDateTime occurredAt = LocalDateTime.now();
    BookingId bookingId;
    VehicleId vehicleId;
    String reason;
    BookingStatus previousStatus;
    
    @Override
    public String getAggregateId() {
        return bookingId.getValue();
    }
}

@Value
@Builder
public class VehicleRentedEvent implements DomainEvent {
    String eventId = UUID.randomUUID().toString();
    LocalDateTime occurredAt = LocalDateTime.now();
    VehicleId vehicleId;
    
    @Override
    public String getAggregateId() {
        return vehicleId.getValue();
    }
}

// Base event
public interface DomainEvent {
    String getEventId();
    LocalDateTime getOccurredAt();
    String getAggregateId();
}

@Value
@Builder
public class BookingCreatedEvent implements DomainEvent {
    String eventId = UUID.randomUUID().toString();
    LocalDateTime occurredAt = LocalDateTime.now();
    BookingId bookingId;
    VehicleId vehicleId;
    UserId userId;
    
    @Override
    public String getAggregateId() {
        return bookingId.getValue();
    }
}

@Value
@Builder
public class BookingConfirmedEvent implements DomainEvent {
    String eventId = UUID.randomUUID().toString();
    LocalDateTime occurredAt = LocalDateTime.now();
    BookingId bookingId;
    VehicleId vehicleId;
    
    @Override
    public String getAggregateId() {
        return bookingId.getValue();
    }
}

@Value
@Builder
public class BookingCancelledEvent implements DomainEvent {
    String eventId = UUID.randomUUID().toString();
    LocalDateTime occurredAt = LocalDateTime.now();
    BookingId bookingId;
    VehicleId vehicleId;
    String reason;
    BookingStatus previousStatus;
    
    @Override
    public String getAggregateId() {
        return bookingId.getValue();
    }
}

@Value
@Builder
public class VehicleRentedEvent implements DomainEvent {
    String eventId = UUID.randomUUID().toString();
    LocalDateTime occurredAt = LocalDateTime.now();
    VehicleId vehicleId;
    
    @Override
    public String getAggregateId() {
        return vehicleId.getValue();
    }
}


// Domain service for complex pricing logic
@Service
public class PricingDomainService {
    
    public Money calculateRentalPrice(Vehicle vehicle, DateRange period, 
                                     List<PricingRule> rules) {
        Money basePrice = vehicle.getDailyRate()
            .multiply(BigDecimal.valueOf(period.getDays()));
        
        // Apply rules in order
        Money finalPrice = basePrice;
        for (PricingRule rule : rules) {
            finalPrice = rule.apply(finalPrice, vehicle, period);
        }
        
        return finalPrice;
    }
}

// Pricing rules - Strategy pattern
public interface PricingRule {
    Money apply(Money currentPrice, Vehicle vehicle, DateRange period);
    int getPriority();
}

@Component
public class WeekendSurchargeRule implements PricingRule {
    private static final BigDecimal WEEKEND_MULTIPLIER = BigDecimal.valueOf(1.2);
    
    @Override
    public Money apply(Money currentPrice, Vehicle vehicle, DateRange period) {
        // Check if rental includes weekend
        if (includesWeekend(period)) {
            return currentPrice.multiply(WEEKEND_MULTIPLIER);
        }
        return currentPrice;
    }
    
    private boolean includesWeekend(DateRange period) {
        LocalDateTime current = period.getStartDate();
        while (current.isBefore(period.getEndDate())) {
            DayOfWeek day = current.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                return true;
            }
            current = current.plusDays(1);
        }
        return false;
    }
    
    @Override
    public int getPriority() {
        return 1;
    }
}

@Component
public class LongTermDiscountRule implements PricingRule {
    @Override
    public Money apply(Money currentPrice, Vehicle vehicle, DateRange period) {
        long days = period.getDays();
        if (days >= 7) {
            return currentPrice.multiply(BigDecimal.valueOf(0.85)); // 15% discount
        } else if (days >= 3) {
            return currentPrice.multiply(BigDecimal.valueOf(0.95)); // 5% discount
        }
        return currentPrice;
    }
    
    @Override
    public int getPriority() {
        return 2;
    }
}

// Domain service for availability checking
@Service
public class VehicleAvailabilityService {
    private final BookingRepository bookingRepository;
    
    public boolean isAvailable(VehicleId vehicleId, DateRange requestedPeriod) {
        List<Booking> existingBookings = bookingRepository
            .findConfirmedBookingsByVehicle(vehicleId);
        
        return existingBookings.stream()
            .noneMatch(booking -> booking.getRentalPeriod().overlaps(requestedPeriod));
    }
    
    public List<VehicleId> findAvailableVehicles(DateRange period, 
                                                 VehicleSpecification spec) {
        // Complex query - might need database-level filtering
        return bookingRepository.findAvailableVehicles(period, spec);
    }
}

// Command - CQRS write model
@Value
@Builder
public class CreateBookingCommand {
    VehicleId vehicleId;
    UserId userId;
    DateRange rentalPeriod;
    PaymentMethod paymentMethod;
    String idempotencyKey;  // For retry safety
}

@Value
@Builder
public class CancelBookingCommand {
    BookingId bookingId;
    UserId userId;
    String reason;
}

// Application Service - Use Case
@Service
@Transactional
@Slf4j
public class BookingApplicationService {
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final VehicleAvailabilityService availabilityService;
    private final PricingDomainService pricingService;
    private final PaymentGateway paymentGateway;
    private final DomainEventPublisher eventPublisher;
    private final IdempotencyService idempotencyService;
    private final DistributedLockService lockService;
    
    public BookingResult createBooking(CreateBookingCommand command) {
        // 1. Idempotency check
        Optional<BookingResult> cached = idempotencyService
            .getCachedResult(command.getIdempotencyKey());
        if (cached.isPresent()) {
            log.info("Returning cached booking result for key: {}", 
                command.getIdempotencyKey());
            return cached.get();
        }
        
        // 2. Acquire distributed lock to prevent double booking
        String lockKey = "booking:vehicle:" + command.getVehicleId().getValue();
        return lockService.executeWithLock(lockKey, Duration.ofSeconds(10), () -> {
            return doCreateBooking(command);
        });
    }
    
    private BookingResult doCreateBooking(CreateBookingCommand command) {
        try {
            // 3. Load vehicle
            Vehicle vehicle = vehicleRepository.findById(command.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(command.getVehicleId()));
            
            // 4. Check availability
            if (!availabilityService.isAvailable(command.getVehicleId(), 
                                                 command.getRentalPeriod())) {
                throw new VehicleNotAvailableException(command.getVehicleId());
            }
            
            // 5. Calculate price
            List<PricingRule> rules = loadActivePricingRules();
            Money totalAmount = pricingService.calculateRentalPrice(
                vehicle, command.getRentalPeriod(), rules);
            
            // 6. Create booking aggregate
            Booking booking = Booking.create(
                command.getVehicleId(),
                command.getUserId(),
                command.getRentalPeriod(),
                totalAmount
            );
            
            // 7. Process payment
            PaymentResult paymentResult = paymentGateway.processPayment(
                PaymentRequest.builder()
                    .amount(totalAmount)
                    .paymentMethod(command.getPaymentMethod())
                    .reference(booking.getId().getValue())
                    .idempotencyKey(command.getIdempotencyKey() + ":payment")
                    .build()
            );
            
            // 8. Confirm booking if payment successful
            PaymentInfo paymentInfo = PaymentInfo.builder()
                .paymentId(paymentResult.getPaymentId())
                .method(command.getPaymentMethod())
                .status(paymentResult.getStatus())
                .processedAt(LocalDateTime.now())
                .transactionReference(paymentResult.getTransactionReference())
                .build();
            
            booking.confirm(paymentInfo);
            
            // 9. Update vehicle status
            vehicle.markAsRented();
            
            // 10. Save
            bookingRepository.save(booking);
            vehicleRepository.save(vehicle);
            
            // 11. Publish domain events
            publishEvents(booking);
            publishEvents(vehicle);
            
            // 12. Cache result
            BookingResult result = BookingResult.success(booking);
            idempotencyService.cacheResult(command.getIdempotencyKey(), result);
            
            log.info("Booking created successfully: {}", booking.getId());
            return result;
            
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock conflict for vehicle: {}", 
                command.getVehicleId());
            throw new ConcurrentBookingException(command.getVehicleId());
        } catch (PaymentException e) {
            log.error("Payment failed for booking", e);
            throw new BookingPaymentFailedException(e);
        }
    }
    
    public void cancelBooking(CancelBookingCommand command) {
        Booking booking = bookingRepository.findById(command.getBookingId())
            .orElseThrow(() -> new BookingNotFoundException(command.getBookingId()));
        
        // Authorization check
        if (!booking.getUserId().equals(command.getUserId())) {
            throw new UnauthorizedBookingAccessException(command.getBookingId());
        }
        
        // Cancel booking
        booking.cancel(command.getReason());
        
        // Update vehicle
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
            .orElseThrow(() -> new VehicleNotFoundException(booking.getVehicleId()));
        vehicle.markAsAvailable();
        
        // Save
        bookingRepository.save(booking);
        vehicleRepository.save(vehicle);
        
        // Publish events
        publishEvents(booking);
        publishEvents(vehicle);
        
        // Trigger refund asynchronously
        eventPublisher.publish(new RefundInitiatedEvent(booking.getId()));
        
        log.info("Booking cancelled: {}", booking.getId());
    }
    
    private void publishEvents(Object aggregate) {
        if (aggregate instanceof Booking) {
            Booking booking = (Booking) aggregate;
            booking.getDomainEvents().forEach(eventPublisher::publish);
            booking.clearEvents();
        } else if (aggregate instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) aggregate;
            vehicle.getDomainEvents().forEach(eventPublisher::publish);
            vehicle.clearEvents();
        }
    }
    
    private List<PricingRule> loadActivePricingRules() {
        // Load from database or config
        return List.of(new WeekendSurchargeRule(), new LongTermDiscountRule());
    }
}

// Query - CQRS read model
@Service
@Transactional(readOnly = true)
public class BookingQueryService {
    private final JdbcTemplate jdbcTemplate;
    
    public List<BookingDTO> findUserBookings(UserId userId, BookingStatus status) {
        String sql = """
            SELECT b.id, b.vehicle_id, v.brand, v.model, 
                   b.start_date, b.end_date, b.total_amount, b.status
            FROM bookings b
            JOIN vehicles v ON b.vehicle_id = v.id
            WHERE b.user_id = ? AND b.status = ?
            ORDER BY b.created_at DESC
            """;
        
        return jdbcTemplate.query(sql, 
            new Object[]{userId.getValue(), status.name()},
            new BookingDTORowMapper());
    }
    
    public BookingDetailsDTO getBookingDetails(BookingId bookingId) {
        // Fetch from optimized read model
        // Could be from a separate denormalized table or cache
        return jdbcTemplate.queryForObject(
            "SELECT * FROM booking_details_view WHERE id = ?",
            new Object[]{bookingId.getValue()},
            new BookingDetailsDTORowMapper()
        );
    }
}



// Repository implementation
@Repository
public class JpaBookingRepository implements BookingRepository {
    private final EntityManager entityManager;
    
    @Override
    public Optional<Booking> findById(BookingId id) {
        return Optional.ofNullable(
            entityManager.find(Booking.class, id)
        );
    }
    
    @Override
    public Booking save(Booking booking) {
        if (entityManager.contains(booking)) {
            return entityManager.merge(booking);
        } else {
            entityManager.persist(booking);
            return booking;
        }
    }
    
    @Override
    public List<Booking> findConfirmedBookingsByVehicle(VehicleId vehicleId) {
        return entityManager.createQuery(
            "SELECT b FROM Booking b WHERE b.vehicleId = :vehicleId " +
            "AND b.status = :status", Booking.class)
            .setParameter("vehicleId", vehicleId)
            .setParameter("status", BookingStatus.CONFIRMED)
            .getResultList();
    }
    
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Optional<Booking> findByIdForUpdate(BookingId id) {
        return Optional.ofNullable(
            entityManager.find(Booking.class, id, LockModeType.PESSIMISTIC_WRITE)
        );
    }
}

// Event Publisher
@Component
public class DomainEventPublisher {
    private final ApplicationEventPublisher springEventPublisher;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    
    public void publish(DomainEvent event) {
        // Publish locally (for immediate handlers)
        springEventPublisher.publishEvent(event);
        
        // Publish to message broker (for async processing)
        kafkaTemplate.send("domain-events", event.getAggregateId(), event);
    }
}

// Event Handler - Listen to events
@Component
@Slf4j
public class BookingEventHandler {
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;
    
    @EventListener
    @Async
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        log.info("Handling BookingConfirmedEvent: {}", event.getBookingId());
        
        // Send confirmation email
        notificationService.sendBookingConfirmation(event.getBookingId());
        
        // Track analytics
        analyticsService.trackBookingConfirmed(event);
    }
    
    @EventListener
    @Async
    public void handleBookingCancelled(BookingCancelledEvent event) {
        log.info("Handling BookingCancelledEvent: {}", event.getBookingId());
        
        // Send cancellation email
        notificationService.sendCancellationNotification(event.getBookingId());
        
        // Initiate refund
        if (event.getPreviousStatus() == BookingStatus.CONFIRMED) {
            // Trigger refund process
        }
    }
}

// Idempotency Service
@Service
public class IdempotencyService {
    private final RedisTemplate<String, BookingResult> redisTemplate;
    private static final Duration TTL = Duration.ofHours(24);
    
    public Optional<BookingResult> getCachedResult(String idempotencyKey) {
        return Optional.ofNullable(
            redisTemplate.opsForValue().get("idempotency:" + idempotencyKey)
        );
    }
    
    public void cacheResult(String idempotencyKey, BookingResult result) {
        redisTemplate.opsForValue().set(
            "idempotency:" + idempotencyKey, 
            result, 
            TTL
        );
    }
}

// Distributed Lock Service
@Service
public class DistributedLockService {
    private final RedissonClient redissonClient;
    
    public <T> T executeWithLock(String lockKey, Duration timeout, 
                                 Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean acquired = lock.tryLock(
                timeout.toMillis(), 
                TimeUnit.MILLISECONDS
            );
            
            if (!acquired) {
                throw new LockAcquisitionException(
                    "Failed to acquire lock: " + lockKey
                );
            }
            
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

// Payment Gateway with Circuit Breaker
@Service
public class PaymentGatewayAdapter implements PaymentGateway {
    private final RestTemplate restTemplate;
    private final CircuitBreaker circuitBreaker;
    
    @Override
    @Retry(name = "paymentGateway")
    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "paymentFallback")
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing payment: {}", request.getIdempotencyKey());
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Idempotency-Key", request.getIdempotencyKey());
        
        ResponseEntity<PaymentResponse> response = restTemplate.exchange(
            "/api/payments",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            PaymentResponse.class
        );
        
        return PaymentResult.from(response.getBody());
    }
    
    public PaymentResult paymentFallback(PaymentRequest request, Exception e) {
        log.error("Payment gateway failed, using fallback", e);
        // Queue for later processing or use backup gateway
        return PaymentResult.pending(request.getReference());
    }
}





@RestController
@RequestMapping("/api/v1/bookings")
@Slf4j
public class BookingController {
    private final BookingApplicationService bookingService;
    private final BookingQueryService queryService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody @Valid CreateBookingRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal user) {
        
        log.info("Creating booking for user: {}, vehicle: {}", 
            user.getUserId(), request.getVehicleId());
        
        CreateBookingCommand command = CreateBookingCommand.builder()
            .vehicleId(VehicleId.from(request.getVehicleId()))
            .userId(UserId.from(user.getUserId()))
            .rentalPeriod(DateRange.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build())
            .paymentMethod(request.getPaymentMethod())
            .idempotencyKey(idempotencyKey)
            .build();
        
        BookingResult result = bookingService.createBooking(command);
        
        return ResponseEntity
            .created(URI.create("/api/v1/bookings/" + result.getBookingId()))
            .body(BookingResponse.from(result));
    }
    
    @GetMapping("/{bookingId}")
    public BookingDetailsDTO getBooking(@PathVariable String bookingId) {
        return queryService.getBookingDetails(BookingId.from(bookingId));
    }
    
    @GetMapping
    public List<BookingDTO> getUserBookings(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) BookingStatus status) {
        return queryService.findUserBookings(
            UserId.from(user.getUserId()), 
            status
        );
    }
    
    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(
            @PathVariable String bookingId,
            @RequestBody Cancel
			
			