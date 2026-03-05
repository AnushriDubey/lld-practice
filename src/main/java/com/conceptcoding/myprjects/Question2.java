package com.conceptcoding.myprjects;
//
//# **Question 2 Answer Review** ⚠️
//
//Your answer: **Strategy Design Pattern**
//
//        ---
//
//        ## **⚠️ PARTIALLY CORRECT! You're close, but missing something important.**
//
//Let me explain:
//
//        ---
//
//        ## **🎯 What You Got Right:**
//
//        ✅ **Strategy Pattern** - YES, this is part of the solution!
//        ✅ **Common Interface** - YES, correct!
//        ✅ **Interchangeable implementations** - YES, correct!
//
//        **Your thinking:**
//        > "For each payment gateway provider we can have one dedicated class implementing the common interface that will allow us to interchange between payment gateways"
//
//This is **good**, but there's a **critical problem** you missed...
//
//        ---
//
//        ## **❌ The Problem You Missed:**
//
//Look at the scenario again:
//
//        ```java
//// Stripe API
//StripeClient.charge(amount, cardToken)
//
//// PayPal API
//PayPalAPI.createPayment(amount, currency, payerId)
//
//// Razorpay API
//RazorpaySDK.processPayment(orderAmount, customerDetails)
//```
//
//        **Notice:** Each gateway has a **DIFFERENT API structure!**
//
//        - Different method names (charge vs createPayment vs processPayment)
//- Different parameters (cardToken vs payerId vs customerDetails)
//- Different return types
//
//**You can't just implement an interface directly** because the APIs are incompatible!
//
//        ---
//
//        ## **✅ Complete Solution: Strategy + Adapter**
//
//You need **TWO patterns**:
//
//        1. **Adapter Pattern** - Convert incompatible APIs to your common interface
//2. **Strategy Pattern** - Switch between different payment implementations
//
//---
//
//        ## **📊 Here's the Complete Design:**
//
//        ```java
// ═══════════════════════════════════════════════════════
// STRATEGY PATTERN - Common Interface
// ═══════════════════════════════════════════════════════

interface PaymentStrategy {
    PaymentResult processPayment(Money amount, PaymentDetails details);
    boolean supportsRefund();
    RefundResult refund(String transactionId, Money amount);
}

// ═══════════════════════════════════════════════════════
// ADAPTER PATTERN - Adapt each gateway to our interface
// ═══════════════════════════════════════════════════════

// Adapter 1: Stripe
class StripePaymentAdapter implements PaymentStrategy {
    private final StripeClient stripeClient;  // Third-party library

    public StripePaymentAdapter(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public PaymentResult processPayment(Money amount, PaymentDetails details) {
        try {
            // Adapt our interface to Stripe's API
            String cardToken = details.getCardToken();

            // Call Stripe's charge method (different name, different params)
            StripeResponse response = stripeClient.charge(
                    amount.getAmountInCents(),  // Stripe wants cents
                    cardToken
            );

            // Convert Stripe response to our PaymentResult
            return PaymentResult.success(
                    response.getTransactionId(),
                    response.getStatus()
            );
        } catch (StripeException e) {
            return PaymentResult.failure(e.getMessage());
        }
    }

    @Override
    public boolean supportsRefund() {
        return true;
    }

    @Override
    public RefundResult refund(String transactionId, Money amount) {
        StripeRefund refund = stripeClient.refund(transactionId);
        return new RefundResult(refund.getId(), refund.getStatus());
    }
}

// Adapter 2: PayPal
class PayPalPaymentAdapter implements PaymentStrategy {
    private final PayPalAPI paypalApi;  // Third-party library

    public PayPalPaymentAdapter(PayPalAPI paypalApi) {
        this.paypalApi = paypalApi;
    }

    @Override
    public PaymentResult processPayment(Money amount, PaymentDetails details) {
        try {
            // Adapt our interface to PayPal's API
            String payerId = details.getPayerId();
            String currency = amount.getCurrency();

            // Call PayPal's createPayment method (different name, different params)
            PayPalPayment payment = paypalApi.createPayment(
                    amount.getAmount(),  // PayPal wants decimal amount
                    currency,
                    payerId
            );

            // Execute the payment (PayPal requires two-step process)
            PayPalExecution execution = paypalApi.executePayment(
                    payment.getId(),
                    payerId
            );

            // Convert PayPal response to our PaymentResult
            return PaymentResult.success(
                    execution.getId(),
                    execution.getState()
            );
        } catch (PayPalException e) {
            return PaymentResult.failure(e.getMessage());
        }
    }

    @Override
    public boolean supportsRefund() {
        return true;
    }

    @Override
    public RefundResult refund(String transactionId, Money amount) {
        PayPalRefund refund = paypalApi.refundSale(transactionId, amount.getAmount());
        return new RefundResult(refund.getId(), refund.getState());
    }
}

// Adapter 3: Razorpay
class RazorpayPaymentAdapter implements PaymentStrategy {
    private final RazorpaySDK razorpaySDK;  // Third-party library

    public RazorpayPaymentAdapter(RazorpaySDK razorpaySDK) {
        this.razorpaySDK = razorpaySDK;
    }

    @Override
    public PaymentResult processPayment(Money amount, PaymentDetails details) {
        try {
            // Adapt our interface to Razorpay's API
            CustomerDetails customerDetails = new CustomerDetails(
                    details.getCustomerEmail(),
                    details.getCustomerPhone()
            );

            // Call Razorpay's processPayment method (different params structure)
            RazorpayOrder order = razorpaySDK.processPayment(
                    amount.getAmountInPaise(),  // Razorpay wants paise (1/100 of rupee)
                    customerDetails
            );

            // Convert Razorpay response to our PaymentResult
            return PaymentResult.success(
                    order.getOrderId(),
                    order.getStatus()
            );
        } catch (RazorpayException e) {
            return PaymentResult.failure(e.getCode() + ": " + e.getDescription());
        }
    }

    @Override
    public boolean supportsRefund() {
        return true;
    }

    @Override
    public RefundResult refund(String transactionId, Money amount) {
        RazorpayRefund refund = razorpaySDK.refund(transactionId);
        return new RefundResult(refund.getId(), refund.getStatus());
    }
}

// Adapter 4: Square
class SquarePaymentAdapter implements PaymentStrategy {
    private final SquareClient squareClient;

    public SquarePaymentAdapter(SquareClient squareClient) {
        this.squareClient = squareClient;
    }

    @Override
    public PaymentResult processPayment(Money amount, PaymentDetails details) {
        try {
            // Square's API structure
            SquarePaymentRequest request = new SquarePaymentRequest()
                    .setSourceId(details.getCardNonce())
                    .setAmountMoney(
                            new SquareMoney()
                                    .setAmount(amount.getAmountInCents())
                                    .setCurrency(amount.getCurrency())
                    );

            SquarePaymentResponse response = squareClient.getPaymentsApi()
                    .createPayment(request);

            return PaymentResult.success(
                    response.getPayment().getId(),
                    response.getPayment().getStatus()
            );
        } catch (SquareException e) {
            return PaymentResult.failure(e.getMessage());
        }
    }

    @Override
    public boolean supportsRefund() {
        return true;
    }

    @Override
    public RefundResult refund(String transactionId, Money amount) {
        SquareRefund refund = squareClient.getRefundsApi()
                .refundPayment(transactionId, amount.getAmountInCents());
        return new RefundResult(refund.getId(), refund.getStatus());
    }
}

// ═══════════════════════════════════════════════════════
// CLIENT CODE (Uses Strategy Pattern)
// ═══════════════════════════════════════════════════════

@Service
class PaymentService {
    private final PaymentStrategy paymentStrategy;  // Strategy interface

    public PaymentService(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;  // Inject strategy
    }

    public PaymentResult processPayment(Money amount, PaymentDetails details) {
        // Client code doesn't know which gateway is being used!
        return paymentStrategy.processPayment(amount, details);
    }

    public RefundResult refundPayment(String transactionId, Money amount) {
        if (!paymentStrategy.supportsRefund()) {
            throw new UnsupportedOperationException("Refund not supported");
        }
        return paymentStrategy.refund(transactionId, amount);
    }
}

// ═══════════════════════════════════════════════════════
// CONFIGURATION (Decides which strategy to use)
// ═══════════════════════════════════════════════════════

@Configuration
class PaymentConfig {

    @Value("${payment.provider}")
    private String paymentProvider;  // stripe, paypal, razorpay, square

    @Bean
    public PaymentStrategy paymentStrategy() {
        return switch (paymentProvider.toLowerCase()) {
            case "stripe" -> new StripePaymentAdapter(
                    new StripeClient(stripeApiKey)
            );
            case "paypal" -> new PayPalPaymentAdapter(
                    new PayPalAPI(paypalClientId, paypalSecret)
            );
            case "razorpay" -> new RazorpayPaymentAdapter(
                    new RazorpaySDK(razorpayKey, razorpaySecret)
            );
            case "square" -> new SquarePaymentAdapter(
                    new SquareClient(squareAccessToken)
            );
            default -> throw new IllegalArgumentException(
                    "Unknown payment provider: " + paymentProvider
            );
        };
    }
}

// ═══════════════════════════════════════════════════════
// USAGE
// ═══════════════════════════════════════════════════════

@RestController
class OrderController {
    private final PaymentService paymentService;

    @PostMapping("/orders")
    public Order createOrder(@RequestBody OrderRequest request) {
        Money amount = Money.of(request.getAmount(), "USD");
        PaymentDetails details = request.getPaymentDetails();

        // Client code doesn't know which gateway!
        PaymentResult result = paymentService.processPayment(amount, details);

        if (result.isSuccess()) {
            // Create order
            return orderService.createOrder(request, result);
        } else {
            throw new PaymentFailedException(result.getErrorMessage());
        }
    }
}
//```
//
//        ---
//
//        ## **🎯 Why Both Patterns Are Needed:**
//
//        | Pattern | Purpose | What It Does |
//        |---------|---------|--------------|
//        | **Adapter** | **Compatibility** | Converts each gateway's incompatible API to your common interface |
//        | **Strategy** | **Interchangeability** | Allows switching between different payment implementations at runtime |
//
//        ---
//
//        ## **📊 Visual Explanation:**
//
//        ```
//WITHOUT ADAPTER (Your original answer):
//        ══════════════════════════════════════════════════════════
//
//        ❌ This doesn't work because APIs are incompatible!
//
//PaymentStrategy (interface)
//  ├─ StripePayment implements PaymentStrategy
//  │    └─ How do you call StripeClient.charge()
//  │        when interface expects processPayment()?
//        │        INCOMPATIBLE! ❌
//
//
//WITH ADAPTER + STRATEGY (Correct answer):
//        ══════════════════════════════════════════════════════════
//
//        ✅ Adapter makes APIs compatible, Strategy makes them interchangeable
//
//         PaymentStrategy (interface)
//        ┌─────────────────────────┐
//        │ + processPayment()      │
//        │ + supportsRefund()      │
//        │ + refund()              │
//        └────────────┬────────────┘
//                     │
//        ┌────────────────────────┼────────────────────────┐
//                │                        │                      │
//        ┌───────▼──────────┐  ┌──────────▼────────┐  ┌──────────▼────────┐
//        │StripeAdapter     │  │ PayPalAdapter     │  │ RazorpayAdapter   │
//        ├──────────────────┤  ├───────────────────┤  ├───────────────────┤
//        │- stripeClient    │  │- paypalApi        │  │- razorpaySDK      │
//        ├──────────────────┤  ├───────────────────┤  ├───────────────────┤
//        │+processPayment() │  │+processPayment()  │  │+processPayment()  │
//        │  calls:          │  │  calls:           │  │  calls:           │
//        │  stripeClient    │  │  paypalApi        │  │  razorpaySDK      │
//        │  .charge()       │  │  .createPayment() │  │  .processPayment()│
//        └──────────────────┘  └───────────────────┘  └───────────────────┘
//        │                        │                        │
//        └────────────────────────┼────────────────────────┘
//        │
//Adapts different APIs to
//common PaymentStrategy interface
//```
//
//        ---
//
//        ## **🔍 Key Differences:**
//
//        ### **Your Answer (Strategy Only):**
//        ```java
// ❌ This won't work because APIs are incompatible
interface PaymentStrategy {
    void processPayment(Money amount);
}

class StripePayment implements PaymentStrategy {
    void processPayment(Money amount) {
        // How do you call StripeClient.charge(amount, cardToken)?
        // You need cardToken but interface doesn't provide it!
        // Parameters don't match! ❌
    }
}
//```
//
//        ### **Correct Answer (Adapter + Strategy):**
//        ```java
// ✅ Adapter wraps the incompatible API
class StripePaymentAdapter implements PaymentStrategy {
    private StripeClient stripeClient;  // Wraps third-party library

    void processPayment(Money amount, PaymentDetails details) {
        // Adapter converts our call to Stripe's format
        String cardToken = details.getCardToken();
        stripeClient.charge(amount.getAmountInCents(), cardToken);
        // ✅ Works!
    }
}
//```
//
//        ---
//
//        ## **🎯 When to Add New Gateway:**
//
//        **With Adapter + Strategy:**
//        ```java
// Just create a new adapter!
class NewGatewayAdapter implements PaymentStrategy {
    private NewGatewaySDK sdk;

    public PaymentResult processPayment(Money amount, PaymentDetails details) {
        // Adapt NewGateway's API to our interface
        return sdk.whateverMethodTheyHave(
                convertToTheirFormat(amount),
                convertToTheirFormat(details)
        );
    }
}

// Add to config
@Bean
public PaymentStrategy paymentStrategy() {
    return switch (paymentProvider) {
        // ... existing cases
        case "newgateway" -> new NewGatewayAdapter(new NewGatewaySDK());
    };
}

//// No changes to client code! ✅
//```
//
//        ---
//
//        ## **✅ Complete Answer Should Be:**
//
//        **Patterns:** Adapter + Strategy
//
//**Why:**
//        - **Adapter:** Each payment gateway has different API (different method names, parameters). Adapter converts their API to our common interface.
//        - **Strategy:** Once adapted, we can switch between gateways easily using Strategy pattern.
//
//        **Where:**
//        - **PaymentStrategy interface:** Common interface for all gateways
//- **StripeAdapter, PayPalAdapter, etc.:** Adapters that wrap third-party libraries
//- **PaymentService:** Context that uses the strategy
//- **Configuration:** Selects which strategy (adapter) to inject
//
//---
//
//        ## **🎯 Your Score: 6/10**
//
//        **What you got right:**
//        - ✅ Strategy pattern (partial credit)
//- ✅ Common interface
//- ✅ Interchangeable implementations
//
//**What you missed:**
//        - ❌ Adapter pattern (needed to convert incompatible APIs)
//- ❌ Didn't recognize the API incompatibility problem
//        - ❌ Didn't explain how to handle different method signatures
//
//        ---
//
//        ## **🚀 Key Lesson:**
//
//        **When you see:**
//        - "Different APIs with different method names/parameters"
//        - "Third-party libraries with incompatible interfaces"
//        - "Want to use our own interface"
//
//        **Think: Adapter Pattern first, then Strategy!**
//
//        ---
//
//        **Want to try another question?** 🎯
//
//Try these:
//        - **Question 5** (Coffee Shop) - Decorator
//- **Question 4** (Undo/Redo) - Command
//- **Question 8** (Report Generation) - Template Method
//
//Pick one! 💪
public class Question2 {
}
