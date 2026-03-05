package com.conceptcoding.myprjects;

//# **Question 1 Answer Review** ✅
//
//Your answer: **Observer Design Pattern**
//
//        ---
//
//        ## **✅ CORRECT! But let me refine your understanding:**
//
//You're **absolutely right** that Observer is the primary pattern here! Let me break down your answer and improve it:
//
//        ---
//
//        ## **📊 Your Understanding (Good!):**
//
//        > "I will maintain per user subscriber list and let update go to those medium where they want"
//
//        **Translation:**
//        - Each user has a list of notification channels they're subscribed to
//        - When event happens, notify only their chosen channels
//
//**This is correct!** ✅
//
//        ---
//
//        ## **🎯 Let's Make It More Precise:**
//
//        ### **Complete Design Using Observer:**
//
//        ```java
// ═══════════════════════════════════════════════════════
// OBSERVER PATTERN COMPONENTS
// ═══════════════════════════════════════════════════════

// Subject (Observable) - The thing being observed
interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(Event event);
}

// Observer - Things that react to changes
interface NotificationObserver {
    void update(OrderEvent event);
    NotificationChannel getChannel();
}

// ═══════════════════════════════════════════════════════
// CONCRETE SUBJECT (Order Events)
// ═══════════════════════════════════════════════════════

class OrderEventPublisher implements Subject {
    // Store observers per user
    private Map<UserId, List<NotificationObserver>> userObservers;

    public OrderEventPublisher() {
        this.userObservers = new ConcurrentHashMap<>();
    }

    @Override
    public void attach(UserId userId, NotificationObserver observer) {
        userObservers
                .computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                .add(observer);
    }

    @Override
    public void detach(UserId userId, NotificationObserver observer) {
        List<NotificationObserver> observers = userObservers.get(userId);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers(OrderEvent event) {
        UserId userId = event.getOrder().getUserId();
        List<NotificationObserver> observers = userObservers.get(userId);

        if (observers != null) {
            // Notify each observer (each notification channel)
            for (NotificationObserver observer : observers) {
                try {
                    observer.update(event);  // Send notification
                } catch (Exception e) {
                    // One failure doesn't affect others ✅
                    logger.error("Failed to notify via {}",
                            observer.getChannel(), e);
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
// CONCRETE OBSERVERS (Notification Channels)
// ═══════════════════════════════════════════════════════

class EmailNotificationObserver implements NotificationObserver {
    private final EmailService emailService;

    @Override
    public void update(OrderEvent event) {
        Order order = event.getOrder();
        String email = getUserEmail(order.getUserId());

        emailService.send(
                email,
                "Order Confirmation",
                buildEmailBody(order)
        );
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }
}

class SMSNotificationObserver implements NotificationObserver {
    private final SMSService smsService;

    @Override
    public void update(OrderEvent event) {
        Order order = event.getOrder();
        String phone = getUserPhone(order.getUserId());

        smsService.send(
                phone,
                buildSMSMessage(order)
        );
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }
}

class PushNotificationObserver implements NotificationObserver {
    private final PushService pushService;

    @Override
    public void update(OrderEvent event) {
        Order order = event.getOrder();
        String deviceToken = getUserDeviceToken(order.getUserId());

        pushService.send(
                deviceToken,
                buildPushNotification(order)
        );
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }
}

class DashboardObserver implements NotificationObserver {
    private final DashboardService dashboardService;

    @Override
    public void update(OrderEvent event) {
        dashboardService.updateOrderStatus(event.getOrder());
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.DASHBOARD;
    }
}

class AnalyticsObserver implements NotificationObserver {
    private final AnalyticsService analyticsService;

    @Override
    public void update(OrderEvent event) {
        analyticsService.trackOrder(event.getOrder());
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.ANALYTICS;
    }
}

// ═══════════════════════════════════════════════════════
// USER PREFERENCE MANAGEMENT
// ═══════════════════════════════════════════════════════

@Service
class NotificationPreferenceService {
    private final OrderEventPublisher publisher;
    private final Map<NotificationChannel, NotificationObserver> observers;

    public NotificationPreferenceService(OrderEventPublisher publisher) {
        this.publisher = publisher;

        // Initialize all available observers
        this.observers = Map.of(
                NotificationChannel.EMAIL, new EmailNotificationObserver(),
                NotificationChannel.SMS, new SMSNotificationObserver(),
                NotificationChannel.PUSH, new PushNotificationObserver(),
                NotificationChannel.DASHBOARD, new DashboardObserver(),
                NotificationChannel.ANALYTICS, new AnalyticsObserver()
        );
    }

    // User subscribes to notification channels
    public void subscribe(UserId userId, NotificationChannel channel) {
        NotificationObserver observer = observers.get(channel);
        if (observer != null) {
            publisher.attach(userId, observer);
        }
    }

    // User unsubscribes from notification channels
    public void unsubscribe(UserId userId, NotificationChannel channel) {
        NotificationObserver observer = observers.get(channel);
        if (observer != null) {
            publisher.detach(userId, observer);
        }
    }

    // Set user's preferences
    public void setPreferences(UserId userId, Set<NotificationChannel> channels) {
        // Remove all existing subscriptions
        for (NotificationObserver observer : observers.values()) {
            publisher.detach(userId, observer);
        }

        // Subscribe to selected channels
        for (NotificationChannel channel : channels) {
            subscribe(userId, channel);
        }
    }
}

// ═══════════════════════════════════════════════════════
// USAGE
// ═══════════════════════════════════════════════════════

@Service
class OrderService {
    private final OrderEventPublisher publisher;

    public void createOrder(Order order) {
        // ... create order logic

        // Notify all subscribed observers
        publisher.notifyObservers(new OrderCreatedEvent(order));
    }
}

// Setting user preferences
//NotificationPreferenceService prefService = ...;

// User 1 wants email and SMS only
//        prefService.setPreferences(
//        userId1,
//        Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS)
//);

// User 2 wants all channels
//        prefService.setPreferences(
//        userId2,
//        Set.of(
//                NotificationChannel.EMAIL,
//        NotificationChannel.SMS,
//        NotificationChannel.PUSH,
//        NotificationChannel.DASHBOARD
//        )
//);
//
//// User 3 wants push only
//        prefService.setPreferences(
//        userId3,
//        Set.of(NotificationChannel.PUSH)
//);
//
//// When order is created, each user gets notified via their chosen channels!
//        ```
//
//        ---
//
//        ## **🎯 Why Observer Pattern Fits Perfectly:**
//
//        | Requirement | How Observer Solves It |
//        |-------------|------------------------|
//        | **Easy to add new notification types** | ✅ Just create new Observer class (Slack, Discord) |
//        | **Notifications independent** | ✅ Each observer handles errors independently |
//        | **User-specific channels** | ✅ Per-user subscription list |
//        | **One failure doesn't affect others** | ✅ Try-catch around each observer |
//        | **Loose coupling** | ✅ Subject doesn't know concrete observers |
//
//        ---
//
//        ## **📊 UML Diagram:**
//
//        ```
//        ┌─────────────────────────────────────┐
//        │        <<interface>>                │
//        │          Subject                    │
//        │  (OrderEventPublisher)              │
//        ├─────────────────────────────────────┤
//        │ + attach(observer)                  │
//        │ + detach(observer)                  │
//        │ + notifyObservers(event)            │
//        └──────────────┬──────────────────────┘
//                       │ notifies
//                       │
//                       ↓
//        ┌──────────────────────────────────────┐
//        │        <<interface>>                 │
//        │     NotificationObserver             │
//        ├──────────────────────────────────────┤
//        │ + update(event)                      │
//        │ + getChannel()                       │
//        └────┬─────────────────────────────────┘
//             │
//             ├──────────────────┬──────────────┬──────────────┬──────────────┐
//             │                  │              │              │              │
//        ┌────▼────┐  ┌──────────▼───┐  ┌──────▼─────┐  ┌───▼──────┐  ┌───▼──────┐
//        │  Email  │  │     SMS      │  │   Push     │  │Dashboard │  │Analytics │
//        │Observer │  │   Observer   │  │  Observer  │  │ Observer │  │ Observer │
//        └─────────┘  └──────────────┘  └────────────┘  └──────────┘  └──────────┘
//        ```
//
//        ---
//
//        ## **🔥 Additional Pattern (Bonus):**
//
//You could **ALSO** use **Strategy Pattern** for selecting notification content format:
//
//        ```java
// Strategy for formatting notification content
interface NotificationFormatter {
    String format(Order order);
}

class EmailFormatter implements NotificationFormatter {
    String format(Order order) {
        return """
            <html>
            <body>
                <h1>Order Confirmation</h1>
                <p>Order ID: %s</p>
                <p>Total: %s</p>
            </body>
            </html>
            """.formatted(order.getId(), order.getTotal());
    }
}

class SMSFormatter implements NotificationFormatter {
    String format(Order order) {
        return "Order %s confirmed. Total: %s"
                .formatted(order.getId(), order.getTotal());
    }
}

class EmailNotificationObserver implements NotificationObserver {
    private final EmailService emailService;
    private final NotificationFormatter formatter = new EmailFormatter();

    @Override
    public void update(OrderEvent event) {
        String content = formatter.format(event.getOrder());
        emailService.send(email, "Order Confirmation", content);
    }
}
//```
//
//        ---
//
//        ## **✅ Your Answer Summary:**
//
//        | Aspect | Your Answer | Correct? |
//        |--------|-------------|----------|
//        | **Pattern Name** | Observer | ✅ Perfect |
//        | **Per-user subscription** | Mentioned | ✅ Correct |
//        | **Selective channels** | Mentioned | ✅ Correct |
//        | **Implementation details** | Not mentioned | ⚠️ Could be more detailed |
//
//        ---
//
//        ## **🎯 Score: 8/10**
//
//        **What you got right:**
//        - ✅ Correctly identified Observer pattern
//- ✅ Understood per-user subscription lists
//- ✅ Understood selective channel notification
//
//**What to improve:**
//        - Add more structure: Subject, Observer interface, Concrete Observers
//- Mention error handling (one failure doesn't affect others)
//        - Show how easy to add new channels
//
//        ---
//
//        ## **🚀 Next Steps:**
//
//        Try another question! Here are good follow-ups:
//
//        **Question 2** (Payment Gateway) - Tests if you know **Adapter + Strategy**
//        **Question 5** (Coffee Shop) - Tests if you know **Decorator**
//        **Question 4** (Undo/Redo) - Tests if you know **Command**
//
//Pick one and give me your answer! 🎯
public class Question1 {
}


