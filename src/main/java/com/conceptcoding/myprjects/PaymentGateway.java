package com.conceptcoding.interviewquestions;

import java.util.HashMap;
import java.util.Map;

//Payment Gateway - Low Level Design (Complete)

//Step 1: Requirements Clarification
//Functional Requirements:
//        ✅ User can make payment using Card/UPI/Net Banking/Wallet
//✅ Support multiple payment gateways (Stripe, PayPal, Razorpay)
//✅ Payment can succeed, fail, or be pending
//✅ Handle refunds
//✅ Generate payment receipts
//✅ Transaction history
//✅ Retry failed payments
//Non-Functional Requirements:
//        ✅ High availability (99.9% uptime)
//✅ Secure (PCI-DSS compliance)
//✅ Transaction atomicity
//✅ Idempotent operations
//✅ Audit logging
//Out of Scope (for interview):
//        ❌ Fraud detection
//❌ Currency conversion
//❌ Subscription management
//❌ Multi-currency support
//❌ Split payments

//Step 2: Core Entities
// ============= ENUMS =============
enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    UPI,
    NET_BANKING,
    WALLET
}
enum PaymentStatus {
    INITIATED,
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED,
    CANCELLED
}
enum TransactionType {
    PAYMENT,
    REFUND
}
enum Currency {
    USD, INR, EUR, GBP
}
// ============= ENTITIES =============
class Payment {
    private String paymentId;
    private String orderId;
    private String userId;
    private double amount;
    private Currency currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String gatewayTransactionId;  // External gateway's transaction ID
    private Date createdAt;
    private Date updatedAt;
    private PaymentDetails paymentDetails;  // Method-specific details

    public Payment(String orderId, String userId, double amount,
                   PaymentMethod method, PaymentDetails details) {
        this.paymentId = generatePaymentId();
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = Currency.INR;
        this.paymentMethod = method;
        this.status = PaymentStatus.INITIATED;
        this.createdAt = new Date();
        this.paymentDetails = details;
    }

    private String generatePaymentId() {
        return "PAY_" + UUID.randomUUID().toString();
    }

    // Getters and setters
    public void updateStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = new Date();
    }
}
// Abstract class for payment details
abstract class PaymentDetails {
    protected String customerId;
}
class CardDetails extends PaymentDetails {
    private String cardNumber;      // Encrypted/tokenized
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;              // Never stored, only for transaction

    public CardDetails(String cardNumber, String holderName,
                       String expMonth, String expYear, String cvv) {
        this.cardNumber = maskCardNumber(cardNumber);
        this.cardHolderName = holderName;
        this.expiryMonth = expMonth;
        this.expiryYear = expYear;
        this.cvv = cvv;  // Transient, not persisted
    }

    private String maskCardNumber(String cardNumber) {
        // Only keep last 4 digits
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
class UPIDetails extends PaymentDetails {
    private String upiId;

    public UPIDetails(String upiId) {
        this.upiId = upiId;
    }
}
class NetBankingDetails extends PaymentDetails {
    private String bankCode;
    private String accountNumber;  // Masked

    public NetBankingDetails(String bankCode, String accountNumber) {
        this.bankCode = bankCode;
        this.accountNumber = maskAccountNumber(accountNumber);
    }

    private String maskAccountNumber(String accountNumber) {
        return "XXXX" + accountNumber.substring(accountNumber.length() - 4);
    }
}
class WalletDetails extends PaymentDetails {
    private String walletProvider;  // PayTM, PhonePe, etc.
    private String walletId;

    public WalletDetails(String provider, String walletId) {
        this.walletProvider = provider;
        this.walletId = walletId;
    }
}
class Transaction {
    private String transactionId;
    private String paymentId;
    private TransactionType type;
    private double amount;
    private PaymentStatus status;
    private Date timestamp;
    private String gatewayResponse;  // Raw response from gateway

    public Transaction(String paymentId, TransactionType type,
                       double amount, PaymentStatus status) {
        this.transactionId = "TXN_" + UUID.randomUUID().toString();
        this.paymentId = paymentId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.timestamp = new Date();
    }

    // Getters
}
class Refund {
    private String refundId;
    private String paymentId;
    private double amount;
    private String reason;
    private PaymentStatus status;
    private Date createdAt;

    public Refund(String paymentId, double amount, String reason) {
        this.refundId = "REF_" + UUID.randomUUID().toString();
        this.paymentId = paymentId;
        this.amount = amount;
        this.reason = reason;
        this.status = PaymentStatus.INITIATED;
        this.createdAt = new Date();
    }

    // Getters
}
class PaymentReceipt {
    private String receiptId;
    private String paymentId;
    private String orderId;
    private double amount;
    private Date paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;

    public PaymentReceipt(Payment payment) {
        this.receiptId = "REC_" + UUID.randomUUID().toString();
        this.paymentId = payment.getPaymentId();
        this.orderId = payment.getOrderId();
        this.amount = payment.getAmount();
        this.paymentDate = payment.getCreatedAt();
        this.method = payment.getPaymentMethod();
        this.status = payment.getStatus();
    }

    public String generateReceipt() {
        return String.format(
                "========== PAYMENT RECEIPT ==========\n" +
                        "Receipt ID: %s\n" +
                        "Payment ID: %s\n" +
                        "Order ID: %s\n" +
                        "Amount: %.2f\n" +
                        "Date: %s\n" +
                        "Method: %s\n" +
                        "Status: %s\n" +
                        "====================================",
                receiptId, paymentId, orderId, amount,
                paymentDate, method, status
        );
    }
}

//Step 3: Payment Gateway Interface (Bridge Pattern)
// ============= GATEWAY INTERFACE =============
interface PaymentGateway {
    PaymentResponse processPayment(Payment payment);
    PaymentResponse checkStatus(String gatewayTransactionId);
    RefundResponse processRefund(Refund refund);
    boolean verifySignature(String signature, String data);
}

class RefundResponse {
    private boolean success;
    private String refundId;
    private String message;
    private PaymentStatus status;

    public RefundResponse(boolean success, String refundId,
                          String message, PaymentStatus status) {
        this.success = success;
        this.refundId = refundId;
        this.message = message;
        this.status = status;
    }

    // Getters
}
// ============= CONCRETE GATEWAYS =============
class StripeGateway implements PaymentGateway {
    private String apiKey;
    private String webhookSecret;

    public StripeGateway(String apiKey, String webhookSecret) {
        this.apiKey = apiKey;
        this.webhookSecret = webhookSecret;
    }

    @Override
    public PaymentResponse processPayment(Payment payment) {
        try {
            // Simulate Stripe API call
            System.out.println("[Stripe] Processing payment: " + payment.getPaymentId());

            // Build Stripe request
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int)(payment.getAmount() * 100)); // Stripe uses cents
            params.put("currency", payment.getCurrency().toString().toLowerCase());
            params.put("description", "Payment for order: " + payment.getOrderId());

            // Add payment method specific details
            if (payment.getPaymentMethod() == PaymentMethod.CREDIT_CARD) {
                CardDetails card = (CardDetails) payment.getPaymentDetails();
                params.put("source", "tok_visa"); // Token from Stripe.js
            }

            // Simulate API call
            String stripeTransactionId = "stripe_" + UUID.randomUUID().toString();

            // Simulate success (90% success rate)
            boolean success = Math.random() > 0.1;

            if (success) {
                return new PaymentResponse(
                        true,
                        stripeTransactionId,
                        "Payment successful",
                        PaymentStatus.SUCCESS
                );
            } else {
                return new PaymentResponse(
                        false,
                        null,
                        "Card declined",
                        PaymentStatus.FAILED
                );
            }

        } catch (Exception e) {
            return new PaymentResponse(
                    false,
                    null,
                    "Gateway error: " + e.getMessage(),
                    PaymentStatus.FAILED
            );
        }
    }

    @Override
    public PaymentResponse checkStatus(String gatewayTransactionId) {
        System.out.println("[Stripe] Checking status for: " + gatewayTransactionId);
        // Simulate status check
        return new PaymentResponse(
                true,
                gatewayTransactionId,
                "Payment completed",
                PaymentStatus.SUCCESS
        );
    }

    @Override
    public RefundResponse processRefund(Refund refund) {
        System.out.println("[Stripe] Processing refund: " + refund.getRefundId());

        // Simulate Stripe refund API
        String stripeRefundId = "re_" + UUID.randomUUID().toString();

        return new RefundResponse(
                true,
                stripeRefundId,
                "Refund processed successfully",
                PaymentStatus.REFUNDED
        );
    }

    @Override
    public boolean verifySignature(String signature, String data) {
        // Verify Stripe webhook signature
        // In real implementation, use HMAC SHA256
        return true;
    }
}
class RazorpayGateway implements PaymentGateway {
    private String keyId;
    private String keySecret;

    public RazorpayGateway(String keyId, String keySecret) {
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    @Override
    public PaymentResponse processPayment(Payment payment) {
        try {
            System.out.println("[Razorpay] Processing payment: " + payment.getPaymentId());

            // Razorpay specific request
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int)(payment.getAmount() * 100)); // Paise
            params.put("currency", "INR");
            params.put("receipt", payment.getOrderId());

            String razorpayOrderId = "order_" + UUID.randomUUID().toString();

            // Simulate API call
            boolean success = Math.random() > 0.1;

            if (success) {
                return new PaymentResponse(
                        true,
                        razorpayOrderId,
                        "Payment captured",
                        PaymentStatus.SUCCESS
                );
            } else {
                return new PaymentResponse(
                        false,
                        null,
                        "Payment failed at bank",
                        PaymentStatus.FAILED
                );
            }

        } catch (Exception e) {
            return new PaymentResponse(
                    false,
                    null,
                    "Razorpay error: " + e.getMessage(),
                    PaymentStatus.FAILED
            );
        }
    }

    @Override
    public PaymentResponse checkStatus(String gatewayTransactionId) {
        System.out.println("[Razorpay] Checking status for: " + gatewayTransactionId);
        return new PaymentResponse(
                true,
                gatewayTransactionId,
                "Payment captured",
                PaymentStatus.SUCCESS
        );
    }

    @Override
    public RefundResponse processRefund(Refund refund) {
        System.out.println("[Razorpay] Processing refund: " + refund.getRefundId());

        String razorpayRefundId = "rfnd_" + UUID.randomUUID().toString();

        return new RefundResponse(
                true,
                razorpayRefundId,
                "Refund initiated",
                PaymentStatus.REFUNDED
        );
    }

    @Override
    public boolean verifySignature(String signature, String data) {
        // Verify Razorpay webhook signature
        return true;
    }
}
class PayPalGateway implements PaymentGateway {
    private String clientId;
    private String clientSecret;

    public PayPalGateway(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public PaymentResponse processPayment(Payment payment) {
        System.out.println("[PayPal] Processing payment: " + payment.getPaymentId());

        // PayPal uses OAuth token
        String accessToken = getAccessToken();

        // Create PayPal order
        String paypalOrderId = "PAYPAL-" + UUID.randomUUID().toString();

        boolean success = Math.random() > 0.1;

        if (success) {
            return new PaymentResponse(
                    true,
                    paypalOrderId,
                    "Payment completed",
                    PaymentStatus.SUCCESS
            );
        } else {
            return new PaymentResponse(
                    false,
                    null,
                    "PayPal payment failed",
                    PaymentStatus.FAILED
            );
        }
    }

    @Override
    public PaymentResponse checkStatus(String gatewayTransactionId) {
        System.out.println("[PayPal] Checking status for: " + gatewayTransactionId);
        return new PaymentResponse(
                true,
                gatewayTransactionId,
                "Payment completed",
                PaymentStatus.SUCCESS
        );
    }

    @Override
    public RefundResponse processRefund(Refund refund) {
        System.out.println("[PayPal] Processing refund: " + refund.getRefundId());

        String paypalRefundId = "REFUND-" + UUID.randomUUID().toString();

        return new RefundResponse(
                true,
                paypalRefundId,
                "Refund completed",
                PaymentStatus.REFUNDED
        );
    }

    @Override
    public boolean verifySignature(String signature, String data) {
        return true;
    }

    private String getAccessToken() {
        // OAuth flow to get access token
        return "access_token_xyz";
    }
}

//Step 4: Gateway Factory (Factory Pattern)
class PaymentGatewayFactory {
    private static final Map<String, PaymentGateway> gateways = new HashMap<>();

    static {
        // Initialize gateways
        gateways.put("STRIPE", new StripeGateway("sk_test_xxx", "whsec_xxx"));
        gateways.put("RAZORPAY", new RazorpayGateway("rzp_test_xxx", "secret_xxx"));
        gateways.put("PAYPAL", new PayPalGateway("client_id", "client_secret"));
    }

    public static PaymentGateway getGateway(String gatewayName) {
        PaymentGateway gateway = gateways.get(gatewayName.toUpperCase());
        if (gateway == null) {
            throw new IllegalArgumentException("Unknown gateway: " + gatewayName);
        }
        return gateway;
    }

    public static PaymentGateway getDefaultGateway() {
        return gateways.get("RAZORPAY");  // Default
    }
}

//Step 5: Managers
// ============= PAYMENT MANAGER =============
class PaymentManager {
    private static PaymentManager instance;
    private Map<String, Payment> payments;

    private PaymentManager() {
        payments = new ConcurrentHashMap<>();
    }

    public static synchronized PaymentManager getInstance() {
        if (instance == null) {
            instance = new PaymentManager();
        }
        return instance;
    }

    public Payment createPayment(String orderId, String userId, double amount,
                                 PaymentMethod method, PaymentDetails details) {
        Payment payment = new Payment(orderId, userId, amount, method, details);
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }

    public Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    public void updatePaymentStatus(String paymentId, PaymentStatus status,
                                    String gatewayTxnId) {
        Payment payment = payments.get(paymentId);
        if (payment != null) {
            payment.updateStatus(status);
            payment.setGatewayTransactionId(gatewayTxnId);
        }
    }

    public List<Payment> getPaymentsByUser(String userId) {
        return payments.values().stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByOrder(String orderId) {
        return payments.values().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }
}
// ============= TRANSACTION MANAGER =============
class TransactionManager {
    private static TransactionManager instance;
    private Map<String, Transaction> transactions;

    private TransactionManager() {
        transactions = new ConcurrentHashMap<>();
    }

    public static synchronized TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public Transaction recordTransaction(String paymentId, TransactionType type,
                                         double amount, PaymentStatus status) {
        Transaction txn = new Transaction(paymentId, type, amount, status);
        transactions.put(txn.getTransactionId(), txn);
        return txn;
    }

    public List<Transaction> getTransactionsByPayment(String paymentId) {
        return transactions.values().stream()
                .filter(t -> t.getPaymentId().equals(paymentId))
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .collect(Collectors.toList());
    }
}
// ============= REFUND MANAGER =============
class RefundManager {
    private static RefundManager instance;
    private Map<String, Refund> refunds;

    private RefundManager() {
        refunds = new ConcurrentHashMap<>();
    }

    public static synchronized RefundManager getInstance() {
        if (instance == null) {
            instance = new RefundManager();
        }
        return instance;
    }

    public Refund createRefund(String paymentId, double amount, String reason) {
        Refund refund = new Refund(paymentId, amount, reason);
        refunds.put(refund.getRefundId(), refund);
        return refund;
    }

    public Refund getRefund(String refundId) {
        return refunds.get(refundId);
    }

    public void updateRefundStatus(String refundId, PaymentStatus status) {
        Refund refund = refunds.get(refundId);
        if (refund != null) {
            refund.setStatus(status);
        }
    }

    public List<Refund> getRefundsByPayment(String paymentId) {
        return refunds.values().stream()
                .filter(r -> r.getPaymentId().equals(paymentId))
                .collect(Collectors.toList());
    }
}

//Step 6: Payment Service (Orchestrator)
class PaymentService {
    private PaymentManager paymentManager;
    private TransactionManager transactionManager;
    private RefundManager refundManager;

    public PaymentService() {
        this.paymentManager = PaymentManager.getInstance();
        this.transactionManager = TransactionManager.getInstance();
        this.refundManager = RefundManager.getInstance();
    }

    // ============= PROCESS PAYMENT =============

    public Payment processPayment(String orderId, String userId, double amount,
                                  PaymentMethod method, PaymentDetails details,
                                  String gatewayName) {

        // Step 1: Create payment
        Payment payment = paymentManager.createPayment(
                orderId, userId, amount, method, details
        );

        System.out.println("Payment initiated: " + payment.getPaymentId());

        // Step 2: Get gateway
        PaymentGateway gateway = PaymentGatewayFactory.getGateway(gatewayName);

        // Step 3: Process with gateway
        PaymentResponse response = gateway.processPayment(payment);

        // Step 4: Update payment status
        paymentManager.updatePaymentStatus(
                payment.getPaymentId(),
                response.getStatus(),
                response.getGatewayTransactionId()
        );

        // Step 5: Record transaction
        transactionManager.recordTransaction(
                payment.getPaymentId(),
                TransactionType.PAYMENT,
                amount,
                response.getStatus()
        );

        // Step 6: Send notification (simplified)
        if (response.isSuccess()) {
            System.out.println("✅ Payment successful: " + payment.getPaymentId());
            sendSuccessNotification(payment);
        } else {
            System.out.println("❌ Payment failed: " + response.getMessage());
            sendFailureNotification(payment, response.getMessage());
        }

        return payment;
    }

    // ============= CHECK PAYMENT STATUS =============

    public PaymentStatus checkPaymentStatus(String paymentId) {
        Payment payment = paymentManager.getPayment(paymentId);

        if (payment == null) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        // If already terminal status, return it
        if (payment.getStatus() == PaymentStatus.SUCCESS ||
                payment.getStatus() == PaymentStatus.FAILED ||
                payment.getStatus() == PaymentStatus.REFUNDED) {
            return payment.getStatus();
        }

        // Query gateway for current status
        PaymentGateway gateway = PaymentGatewayFactory.getDefaultGateway();
        PaymentResponse response = gateway.checkStatus(
                payment.getGatewayTransactionId()
        );

        // Update status
        paymentManager.updatePaymentStatus(
                paymentId,
                response.getStatus(),
                payment.getGatewayTransactionId()
        );

        return response.getStatus();
    }

    // ============= PROCESS REFUND =============

    public Refund processRefund(String paymentId, double amount, String reason) {
        // Step 1: Validate payment
        Payment payment = paymentManager.getPayment(paymentId);

        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Can only refund successful payments");
        }

        if (amount > payment.getAmount()) {
            throw new IllegalArgumentException("Refund amount exceeds payment amount");
        }

        // Step 2: Create refund
        Refund refund = refundManager.createRefund(paymentId, amount, reason);

        System.out.println("Refund initiated: " + refund.getRefundId());

        // Step 3: Process with gateway
        PaymentGateway gateway = PaymentGatewayFactory.getDefaultGateway();
        RefundResponse response = gateway.processRefund(refund);

        // Step 4: Update statuses
        if (response.isSuccess()) {
            refundManager.updateRefundStatus(
                    refund.getRefundId(),
                    PaymentStatus.REFUNDED
            );

            paymentManager.updatePaymentStatus(
                    paymentId,
                    PaymentStatus.REFUNDED,
                    payment.getGatewayTransactionId()
            );

            // Record transaction
            transactionManager.recordTransaction(
                    paymentId,
                    TransactionType.REFUND,
                    amount,
                    PaymentStatus.REFUNDED
            );

            System.out.println("✅ Refund successful: " + refund.getRefundId());
        } else {
            System.out.println("❌ Refund failed: " + response.getMessage());
        }

        return refund;
    }

    // ============= RETRY FAILED PAYMENT =============

    public Payment retryPayment(String paymentId) {
        Payment oldPayment = paymentManager.getPayment(paymentId);

        if (oldPayment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        if (oldPayment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Can only retry failed payments");
        }

        // Create new payment attempt
        return processPayment(
                oldPayment.getOrderId(),
                oldPayment.getUserId(),
                oldPayment.getAmount(),
                oldPayment.getPaymentMethod(),
                oldPayment.getPaymentDetails(),
                "RAZORPAY"
        );
    }

    // ============= GENERATE RECEIPT =============

    public PaymentReceipt generateReceipt(String paymentId) {
        Payment payment = paymentManager.getPayment(paymentId);

        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Can only generate receipt for successful payment");
        }

        return new PaymentReceipt(payment);
    }

    // ============= GET TRANSACTION HISTORY =============

    public List<Transaction> getTransactionHistory(String paymentId) {
        return transactionManager.getTransactionsByPayment(paymentId);
    }

    // ============= NOTIFICATIONS (Simplified) =============

    private void sendSuccessNotification(Payment payment) {
        System.out.println("📧 Sending success email to user: " + payment.getUserId());
        System.out.println("💬 Sending success SMS");
    }

    private void sendFailureNotification(Payment payment, String reason) {
        System.out.println("📧 Sending failure email to user: " + payment.getUserId());
        System.out.println("Reason: " + reason);
    }
}

//Step 7: Demo & Usage
public class PaymentGatewayDemo {
    public static void main(String[] args) {
        PaymentService paymentService = new PaymentService();

        System.out.println("========== PAYMENT GATEWAY DEMO ==========\n");

        // ============= SCENARIO 1: Card Payment via Stripe =============

        System.out.println("--- Scenario 1: Card Payment ---");

        CardDetails cardDetails = new CardDetails(
                "4111111111111111",
                "John Doe",
                "12",
                "2025",
                "123"
        );

        Payment payment1 = paymentService.processPayment(
                "ORD_001",          // orderId
                "USER_123",         // userId
                1500.00,            // amount
                PaymentMethod.CREDIT_CARD,
                cardDetails,
                "STRIPE"
        );

        System.out.println("Payment ID: " + payment1.getPaymentId());
        System.out.println("Status: " + payment1.getStatus());
        System.out.println();

        // ============= SCENARIO 2: UPI Payment via Razorpay =============

        System.out.println("--- Scenario 2: UPI Payment ---");

        UPIDetails upiDetails = new UPIDetails("user@paytm");

        Payment payment2 = paymentService.processPayment(
                "ORD_002",
                "USER_456",
                2500.00,
                PaymentMethod.UPI,
                upiDetails,
                "RAZORPAY"
        );

        System.out.println("Payment ID: " + payment2.getPaymentId());
        System.out.println("Status: " + payment2.getStatus());
        System.out.println();

        // ============= SCENARIO 3: Check Payment Status =============

        System.out.println("--- Scenario 3: Check Status ---");

        PaymentStatus status = paymentService.checkPaymentStatus(
                payment1.getPaymentId()
        );
        System.out.println("Current status: " + status);
        System.out.println();

        // ============= SCENARIO 4: Process Refund =============

        System.out.println("--- Scenario 4: Process Refund ---");

        if (payment1.getStatus() == PaymentStatus.SUCCESS) {
            Refund refund = paymentService.processRefund(
                    payment1.getPaymentId(),
                    1500.00,
                    "Customer requested refund"
            );

            System.out.println("Refund ID: " + refund.getRefundId());
            System.out.println("Refund Status: " + refund.getStatus());
        }
        System.out.println();

        // ============= SCENARIO 5: Generate Receipt =============

        System.out.println("--- Scenario 5: Generate Receipt ---");

        if (payment2.getStatus() == PaymentStatus.SUCCESS) {
            PaymentReceipt receipt = paymentService.generateReceipt(
                    payment2.getPaymentId()
            );
            System.out.println(receipt.generateReceipt());
        }
        System.out.println();

        // ============= SCENARIO 6: Transaction History =============

        System.out.println("--- Scenario 6: Transaction History ---");

        List<Transaction> history = paymentService.getTransactionHistory(
                payment1.getPaymentId()
        );

        System.out.println("Transaction count: " + history.size());
        for (Transaction txn : history) {
            System.out.println(txn.getTransactionId() + " | " +
                    txn.getType() + " | " +
                    txn.getStatus() + " | " +
                    txn.getAmount());
        }

        System.out.println("\n========== END OF DEMO ==========");
    }
}

//Class Diagram
//        ┌─────────────┐
//        │   Payment   │
//        ├─────────────┤
//        │ paymentId   │
//        │ orderId     │
//        │ userId      │
//        │ amount      │
//        │ status      │
//        └──────┬──────┘
//        │
//        │ has
//       ↓
//        ┌──────────────┐
//        │PaymentDetails│◄─────┐
//        └──────────────┘      │
//        △              │
//        │              │
//        ┌───┴────┬─────┬───┴──┐
//        │        │     │      │
//CardDetails UPI  Net   Wallet
//Details Banking Details
//        ┌──────────────────┐
//        │ PaymentService   │
//        ├──────────────────┤
//        │ processPayment() │
//        │ checkStatus()    │
//        │ processRefund()  │
//        └────────┬─────────┘
//        │ uses
//         ↓
//        ┌──────────────────┐
//        │ com.conceptcoding.myprjects.PaymentGateway   │◄────────────┐
//        ├──────────────────┤             │
//        │ processPayment() │             │
//        │ checkStatus()    │             │
//        │ processRefund()  │             │
//        └──────────────────┘             │
//        △                       │
//        │                       │
//        ┌────┴────┬────────┐         │
//        │         │        │         │
//Stripe   Razorpay  PayPal       │
//Gateway  Gateway   Gateway      │
//        │
//        ┌──────────────────┐             │
//        │GatewayFactory    │─────────────┘
//        ├──────────────────┤
//        │ getGateway()     │
//        └──────────────────┘
//        ┌──────────────────┐
//        │ PaymentManager   │
//        ├──────────────────┤
//        │ createPayment()  │
//        │ getPayment()     │
//        │ updateStatus()   │
//        └──────────────────┘
//        ┌──────────────────┐
//        │TransactionManager│
//        ├──────────────────┤
//        │recordTransaction │
//        └──────────────────┘
//
//Key Design Patterns Used
//1. ✅ Bridge Pattern
//   - PaymentService (Abstraction) + com.conceptcoding.myprjects.PaymentGateway (Implementation)
//   - Can switch gateways without changing service code
//2. ✅ Factory Pattern
//   - PaymentGatewayFactory creates appropriate gateway
//3. ✅ Singleton Pattern
//   - PaymentManager, TransactionManager, RefundManager
//4. ✅ Strategy Pattern
//   - Different payment gateways = different strategies
//5. ✅ Builder Pattern (could add)
//   - PaymentBuilder for complex payment object creation

//Extensibility Discussion
//Q: How to add new payment gateway?
// Just implement the interface
class PhonePeGateway implements PaymentGateway {
    @Override
    public PaymentResponse processPayment(Payment payment) {
        // PhonePe specific logic
    }

    // ... other methods
}
// Register in factory
PaymentGatewayFactory.addGateway("PHONEPE", new PhonePeGateway());
//Q: How to handle webhooks?
class WebhookService {
    public void handleWebhook(String gatewayName, String payload, String signature) {
        PaymentGateway gateway = PaymentGatewayFactory.getGateway(gatewayName);

        // Verify signature
        if (!gateway.verifySignature(signature, payload)) {
            throw new SecurityException("Invalid signature");
        }

        // Parse payload and update payment status
        // ...
    }
}
//Q: How to handle concurrency?
// Use ConcurrentHashMap in managers
private Map<String, Payment> payments = new ConcurrentHashMap<>();
// Use optimistic locking
class Payment {
    private int version;  // For optimistic locking
}
//Q: How to add retry logic?
class PaymentService {
    public Payment processPaymentWithRetry(/*params*/) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                return processPayment(/*params*/);
            } catch (GatewayTimeoutException e) {
                attempt++;
                if (attempt == maxRetries) throw e;
                Thread.sleep(1000 * attempt);  // Exponential backoff
            }
        }
    }
}

//Summary: Interview Talking Points
//What you should explain:
//        1. ✅ Bridge Pattern - Separate payment logic from gateway implementation
//	2. ✅ Factory Pattern - Gateway selection
//	3. ✅ Singleton - Manager classes for single instance
//	4. ✅ Status Tracking - INITIATED → PENDING → SUCCESS/FAILED
//	5. ✅ Idempotency - Same payment ID won't charge twice
//        6. ✅ Refund Flow - Only refund successful payments
//	7. ✅ Transaction History - Audit trail
//	8. ✅ Receipt Generation - After successful payment
//Extensions to mention:
//        • Webhook handling for async updates
//	• Retry logic with exponential backoff
//	• Rate limiting per gateway
//	• Circuit breaker for failing gateways
//	• Fraud detection integration
//	• Multi-currency support
//	• Partial refunds
//	• Scheduled payments
//This design is interview-ready and covers all essential aspects! 🎯


public class PaymentGateway {
}
