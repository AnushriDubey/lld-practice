// ============================================
// 1. ENUMS & VALUE OBJECTS
// ============================================

enum SeatStatus {
    AVAILABLE,      // Can be booked
    BLOCKED,        // Temporarily held (5-10 min)
    BOOKED         // Confirmed booking
}

enum BookingStatus {
    PENDING,        // Created, awaiting payment
    CONFIRMED,      // Payment successful
    CANCELLED,      // User cancelled
    EXPIRED         // Booking expired
}

enum SeatType {
    REGULAR(100),
    PREMIUM(200),
    VIP(300);
    
    private final int basePrice;
    
    SeatType(int basePrice) {
        this.basePrice = basePrice;
    }
    
    public int getBasePrice() { return basePrice; }
}

enum ShowType {
    REGULAR_2D(1.0),
    REGULAR_3D(1.5),
    IMAX_2D(1.8),
    IMAX_3D(2.0);
    
    private final double priceMultiplier;
    
    ShowType(double multiplier) {
        this.priceMultiplier = multiplier;
    }
    
    public double getMultiplier() { return priceMultiplier; }
}

// Value object for seat identification
@Value
@AllArgsConstructor
public class SeatId {
    String showId;
    String seatNumber;  // "A1", "B5", etc.
    
    public static SeatId of(String showId, String seatNumber) {
        return new SeatId(showId, seatNumber);
    }
}

// ============================================
// 2. CORE ENTITIES
// ============================================

@Entity
@Getter
@Setter
public class City {
    @Id
    private String id;
    private String name;
    private String state;
    
    // For nearby search
    private double latitude;
    private double longitude;
}

@Entity
@Getter
@Setter
public class Theatre {
    @Id
    private String id;
    private String name;
    
    @ManyToOne
    private City city;
    
    @Embedded
    private Address address;
    
    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL)
    private List<Screen> screens;
    
    private double latitude;
    private double longitude;
}

@Embeddable
@Value
@Builder
public class Address {
    String street;
    String city;
    String pincode;
}

@Entity
@Getter
@Setter
public class Screen {
    @Id
    private String id;
    
    @ManyToOne
    private Theatre theatre;
    
    private String screenName;  // "Screen 1", "IMAX Hall"
    
    private int totalSeats;
    
    // Screen layout - fixed physical seats
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Seat> seats;
}

@Entity
@Getter
@Setter
public class Seat {
    @Id
    private String id;
    
    @ManyToOne
    private Screen screen;
    
    private String seatNumber;  // "A1", "B5"
    private String rowLabel;    // "A", "B", "C"
    private int columnNumber;   // 1, 2, 3
    
    @Enumerated(EnumType.STRING)
    private SeatType seatType;
    
    // No status here! Status is per show
}

@Entity
@Getter
@Setter
public class Movie {
    @Id
    private String id;
    private String title;
    private String description;
    private int durationMinutes;
    private String language;
    private String genre;
    
    @ElementCollection
    private List<String> cast;
    
    private LocalDate releaseDate;
}

@Entity
@Getter
@Setter
public class Show {
    @Id
    private String id;
    
    @ManyToOne
    private Movie movie;
    
    @ManyToOne
    private Screen screen;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    private ShowType showType;
    
    private LocalDate showDate;
    
    // IMPORTANT: Show-specific seat availability
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<ShowSeat> showSeats;
    
    @Version
    private Long version;  // Optimistic locking
    
    // Factory method
    public static Show create(Movie movie, Screen screen, 
                             LocalDateTime startTime, ShowType showType) {
        Show show = new Show();
        show.id = UUID.randomUUID().toString();
        show.movie = movie;
        show.screen = screen;
        show.startTime = startTime;
        show.endTime = startTime.plusMinutes(movie.getDurationMinutes());
        show.showType = showType;
        show.showDate = startTime.toLocalDate();
        
        // Initialize show seats from screen seats
        show.showSeats = screen.getSeats().stream()
            .map(seat -> ShowSeat.create(show, seat))
            .collect(Collectors.toList());
        
        return show;
    }
}

// KEY: Separate entity for show-specific seat status
@Entity
@Getter
@Setter
public class ShowSeat {
    @Id
    private String id;
    
    @ManyToOne
    private Show show;
    
    @ManyToOne
    private Seat seat;
    
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
    
    private String blockedBy;  // User ID who blocked it
    private LocalDateTime blockedAt;
    
    @Version
    private Long version;  // Optimistic locking
    
    public static ShowSeat create(Show show, Seat seat) {
        ShowSeat showSeat = new ShowSeat();
        showSeat.id = UUID.randomUUID().toString();
        showSeat.show = show;
        showSeat.seat = seat;
        showSeat.status = SeatStatus.AVAILABLE;
        return showSeat;
    }
    
    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }
    
    public void block(String userId) {
        if (!isAvailable()) {
            throw new SeatNotAvailableException(seat.getSeatNumber());
        }
        this.status = SeatStatus.BLOCKED;
        this.blockedBy = userId;
        this.blockedAt = LocalDateTime.now();
    }
    
    public void book() {
        if (status != SeatStatus.BLOCKED) {
            throw new InvalidSeatStateException("Seat must be blocked before booking");
        }
        this.status = SeatStatus.BOOKED;
    }
    
    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.blockedBy = null;
        this.blockedAt = null;
    }
    
    public boolean isBlockExpired() {
        if (status != SeatStatus.BLOCKED) return false;
        return LocalDateTime.now().isAfter(blockedAt.plusMinutes(10));
    }
}

@Entity
@Getter
@Setter
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
}

@Entity
@Getter
@Setter
public class Booking {
    @Id
    private String id;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Show show;
    
    @ManyToMany
    private List<ShowSeat> seats;
    
    private int totalAmount;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    @Embedded
    private PaymentInfo paymentInfo;
    
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    
    @Version
    private Long version;
    
    public static Booking create(User user, Show show, 
                                List<ShowSeat> seats, int totalAmount) {
        Booking booking = new Booking();
        booking.id = "BK" + System.currentTimeMillis();
        booking.user = user;
        booking.show = show;
        booking.seats = seats;
        booking.totalAmount = totalAmount;
        booking.status = BookingStatus.PENDING;
        booking.createdAt = LocalDateTime.now();
        booking.expiresAt = LocalDateTime.now().plusMinutes(10);
        return booking;
    }
    
    public void confirm(PaymentInfo payment) {
        if (status != BookingStatus.PENDING) {
            throw new InvalidBookingStateException();
        }
        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new BookingExpiredException(id);
        }
        
        this.status = BookingStatus.CONFIRMED;
        this.paymentInfo = payment;
    }
    
    public void cancel() {
        if (status == BookingStatus.CONFIRMED) {
            // Check if cancellation allowed (e.g., 2 hours before show)
            if (show.getStartTime().minusHours(2).isBefore(LocalDateTime.now())) {
                throw new CancellationNotAllowedException();
            }
        }
        this.status = BookingStatus.CANCELLED;
    }
    
    public boolean isExpired() {
        return status == BookingStatus.PENDING 
            && LocalDateTime.now().isAfter(expiresAt);
    }
}

@Embeddable
@Value
@Builder
public class PaymentInfo {
    String paymentId;
    String method;
    int amount;
    LocalDateTime paidAt;
}

// ============================================
// 3. SERVICES
// ============================================

@Service
@Transactional
@Slf4j
public class BookingService {
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;
    private final PricingService pricingService;
    
    // Step 1: Block seats (temporary hold)
    public List<ShowSeat> blockSeats(String showId, List<String> seatNumbers, 
                                     String userId) {
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new ShowNotFoundException(showId));
        
        List<ShowSeat> seatsToBlock = showSeatRepository
            .findByShowAndSeatNumbers(showId, seatNumbers);
        
        // Check all seats available
        for (ShowSeat showSeat : seatsToBlock) {
            if (!showSeat.isAvailable()) {
                throw new SeatNotAvailableException(showSeat.getSeat().getSeatNumber());
            }
        }
        
        // Block all seats atomically
        for (ShowSeat showSeat : seatsToBlock) {
            showSeat.block(userId);
        }
        
        showSeatRepository.saveAll(seatsToBlock);
        
        log.info("Blocked {} seats for user: {}", seatsToBlock.size(), userId);
        return seatsToBlock;
    }
    
    // Step 2: Create booking with payment
    public Booking createBooking(String userId, String showId, 
                                 List<String> seatNumbers, 
                                 PaymentMethod paymentMethod) {
        
        // Load show and seats
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new ShowNotFoundException(showId));
        
        List<ShowSeat> blockedSeats = showSeatRepository
            .findByShowAndSeatNumbersAndBlockedBy(showId, seatNumbers, userId);
        
        if (blockedSeats.size() != seatNumbers.size()) {
            throw new SeatsNotBlockedException();
        }
        
        // Check if any seat block expired
        if (blockedSeats.stream().anyMatch(ShowSeat::isBlockExpired)) {
            throw new SeatBlockExpiredException();
        }
        
        // Calculate price
        int totalAmount = pricingService.calculateTotalPrice(show, blockedSeats);
        
        // Create booking
        User user = new User();  // Load from repository
        user.setId(userId);
        
        Booking booking = Booking.create(user, show, blockedSeats, totalAmount);
        bookingRepository.save(booking);
        
        // Process payment
        try {
            PaymentResult paymentResult = paymentService.processPayment(
                paymentMethod, totalAmount, booking.getId()
            );
            
            PaymentInfo paymentInfo = PaymentInfo.builder()
                .paymentId(paymentResult.getPaymentId())
                .method(paymentMethod.name())
                .amount(totalAmount)
                .paidAt(LocalDateTime.now())
                .build();
            
            // Confirm booking
            booking.confirm(paymentInfo);
            
            // Mark seats as booked
            for (ShowSeat seat : blockedSeats) {
                seat.book();
            }
            
            bookingRepository.save(booking);
            showSeatRepository.saveAll(blockedSeats);
            
            log.info("Booking confirmed: {}", booking.getId());
            return booking;
            
        } catch (PaymentException e) {
            // Release seats if payment fails
            releaseSeats(blockedSeats);
            throw new BookingPaymentFailedException(e);
        }
    }
    
    public void cancelBooking(String bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException(bookingId));
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException();
        }
        
        booking.cancel();
        
        // Release seats
        for (ShowSeat seat : booking.getSeats()) {
            seat.release();
        }
        
        bookingRepository.save(booking);
        showSeatRepository.saveAll(booking.getSeats());
        
        // Initiate refund
        paymentService.initiateRefund(booking.getPaymentInfo().getPaymentId());
    }
    
    private void releaseSeats(List<ShowSeat> seats) {
        for (ShowSeat seat : seats) {
            seat.release();
        }
        showSeatRepository.saveAll(seats);
    }
}

@Service
public class PricingService {
    
    public int calculateTotalPrice(Show show, List<ShowSeat> seats) {
        int total = 0;
        
        for (ShowSeat showSeat : seats) {
            Seat seat = showSeat.getSeat();
            int basePrice = seat.getSeatType().getBasePrice();
            double multiplier = show.getShowType().getMultiplier();
            
            int seatPrice = (int) (basePrice * multiplier);
            total += seatPrice;
        }
        
        return total;
    }
}

@Service
public class SearchService {
    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    
    public List<Theatre> searchTheatres(String cityId, String movieTitle) {
        return theatreRepository.findTheatresShowingMovie(cityId, movieTitle);
    }
    
    public List<Show> searchShows(String cityId, String movieId, LocalDate date) {
        return showRepository.findShowsByCityMovieAndDate(cityId, movieId, date);
    }
    
    public Map<String, List<Show>> getShowsByTheatre(String cityId, 
                                                     String movieId, 
                                                     LocalDate date) {
        List<Show> shows = searchShows(cityId, movieId, date);
        
        return shows.stream()
            .collect(Collectors.groupingBy(
                show -> show.getScreen().getTheatre().getId()
            ));
    }
}

// Background job to release expired seat blocks
@Component
@Slf4j
public class SeatBlockExpiryJob {
    private final ShowSeatRepository showSeatRepository;
    
    @Scheduled(fixedRate = 60000)  // Every minute
    public void releaseExpiredBlocks() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(10);
        
        List<ShowSeat> expiredSeats = showSeatRepository
            .findBlockedSeatsOlderThan(expiryTime);
        
        for (ShowSeat seat : expiredSeats) {
            seat.release();
        }
        
        if (!expiredSeats.isEmpty()) {
            showSeatRepository.saveAll(expiredSeats);
            log.info("Released {} expired seat blocks", expiredSeats.size());
        }
    }
}

// ============================================
// 4. REPOSITORIES
// ============================================

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, String> {
    
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId " +
           "AND ss.seat.seatNumber IN :seatNumbers")
    List<ShowSeat> findByShowAndSeatNumbers(
        @Param("showId") String showId,
        @Param("seatNumbers") List<String> seatNumbers
    );
    
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId " +
           "AND ss.seat.seatNumber IN :seatNumbers " +
           "AND ss.status = 'BLOCKED' AND ss.blockedBy = :userId")
    List<ShowSeat> findByShowAndSeatNumbersAndBlockedBy(
        @Param("showId") String showId,
        @Param("seatNumbers") List<String> seatNumbers,
        @Param("userId") String userId
    );
    
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.status = 'BLOCKED' " +
           "AND ss.blockedAt < :expiryTime")
    List<ShowSeat> findBlockedSeatsOlderThan(@Param("expiryTime") LocalDateTime expiryTime);
}

@Repository
public interface ShowRepository extends JpaRepository<Show, String> {
    
    @Query("SELECT s FROM Show s " +
           "WHERE s.screen.theatre.city.id = :cityId " +
           "AND s.movie.id = :movieId " +
           "AND s.showDate = :date " +
           "ORDER BY s.startTime")
    List<Show> findShowsByCityMovieAndDate(
        @Param("cityId") String cityId,
        @Param("movieId") String movieId,
        @Param("date") LocalDate date
    );
}

// ============================================
// 5. API LAYER
// ============================================

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final SearchService searchService;
    
    // Search shows
    @GetMapping("/shows")
    public Map<String, List<Show>> searchShows(
            @RequestParam String cityId,
            @RequestParam String movieId,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
        
        return searchService.getShowsByTheatre(cityId, movieId, date);
    }
    
    // Block seats
    @PostMapping("/block-seats")
    public ResponseEntity<SeatBlockResponse> blockSeats(
            @RequestBody BlockSeatsRequest request,
            @AuthenticationPrincipal UserDetails user) {
        
        List<ShowSeat> blockedSeats = bookingService.blockSeats(
            request.getShowId(),
            request.getSeatNumbers(),
            user.getUsername()
        );
        
        return ResponseEntity.ok(SeatBlockResponse.from(blockedSeats));
    }
    
    // Create booking
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal UserDetails user) {
        
        Booking booking = bookingService.createBooking(
            user.getUsername(),
            request.getShowId(),
            request.getSeatNumbers(),
            request.getPaymentMethod()
        );
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(BookingResponse.from(booking));
    }
    
    // Cancel booking
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable String bookingId,
            @AuthenticationPrincipal UserDetails user) {
        
        bookingService.cancelBooking(bookingId, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
```

---

## **🎯 Key Improvements**

### **What Changed:**
1. ✅ **ShowSeat entity** - Separate status per show (critical!)
2. ✅ **Seat blocking** - 10-minute temporary hold
3. ✅ **Expiry job** - Auto-release expired blocks
4. ✅ **Optimistic locking** - Prevent race conditions
5. ✅ **Two-step booking** - Block → Pay → Confirm
6. ✅ **Proper pricing** - Seat type + show type multipliers
7. ✅ **Screen entity** - Physical layout separate from shows
8. ✅ **City hierarchy** - For location-based search

---

## **📋 Booking Flow**
```
1. User searches shows → GET /api/bookings/shows
2. User selects seats → POST /api/bookings/block-seats
   - Seats blocked for 10 minutes
3. User enters payment → POST /api/bookings
   - Payment processed
   - If success → Booking confirmed, seats booked
   - If fail → Seats released
4. Background job releases expired blocks every minute



💡 Critical Design Decisions
Why ShowSeat?

Same physical seat used by different shows
Each show needs independent availability tracking

Why Blocking?

User needs time to enter payment details
Prevents others from booking while user is paying
Auto-expires if user abandons

Why Optimistic Locking?

Multiple users might select same seats
@Version ensures last-write-wins doesn't corrupt data