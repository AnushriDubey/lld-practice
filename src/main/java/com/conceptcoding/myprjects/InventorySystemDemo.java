package com.conceptcoding.myprjects;


/**
 * I've created a complete inventory management system with these key features:
 * Core Components:
 *
 * Enums - ProductType, Currency, TransactionType
 * Value Objects - Money class for currency handling
 * Entities - Product with full properties (expiry, location, supplier, threshold)
 * Transaction Logging - Complete audit trail of all inventory changes
 * Observer Pattern - For low stock and expiry notifications
 * Manager Layer - All inventory operations and business logic
 * Service Layer - Higher-level operations like order processing
 *
 * Key Features:
 *
 * Thread-safe quantity updates with synchronized methods
 * Low stock threshold tracking and alerts
 * Product expiry date management
 * Warehouse location tracking
 * Supplier information
 * Complete transaction history
 * Multiple search options (by name, type, price range, ID)
 * Reports for low stock and expired products
 * Observer notifications for critical events
 *
 * Search Capabilities:
 *
 * By product ID
 * By name (partial match)
 * By product type
 * By price range
 * Low stock products
 * Expired products
 *
 * Business Operations:
 *
 * Add/Remove products
 * Update stock (increase/decrease)
 * Process orders
 * Receive shipments
 * Generate reports
 * View transaction history
 *
 *
 *
 * **/
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// Enums
enum ProductType {
    FRUITS, INSTANT_FOOD, VEGETABLE, DAIRY, HEALTH
}

enum Currency {
    GBP, EUR, INR
}

enum TransactionType {
    ADD, REMOVE, RESTOCK, ADJUSTMENT
}

// Exceptions
class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}

class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String message) {
        super(message);
    }
}

// Value Objects
class Money {
    private final Currency currency;
    private final double amount;

    public Money(Currency currency, double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Currency getCurrency() { return currency; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return String.format("%s %.2f", currency, amount);
    }
}

// Entities
class Product {
    private final String productId;
    private String name;
    private String description;
    private ProductType type;
    private Money price;
    private int quantity;
    private int lowStockThreshold;
    private LocalDateTime expiryDate;
    private String supplierId;
    private String warehouseLocation;

    public Product(String productId, String name, String description,
                   ProductType type, Money price, int initialQuantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.quantity = initialQuantity;
        this.lowStockThreshold = 10; // default
    }

    // Synchronized quantity operations
    public synchronized boolean updateQuantity(int change) throws InsufficientStockException {
        if (quantity + change < 0) {
            throw new InsufficientStockException(
                    "Insufficient stock for " + name + ". Available: " + quantity + ", Requested: " + Math.abs(change)
            );
        }
        quantity += change;
        return true;
    }

    public synchronized boolean removeQuantity(int amount) throws InsufficientStockException {
        return updateQuantity(-amount);
    }

    public synchronized void addQuantity(int amount) {
        quantity += amount;
    }

    // Check if stock is low
    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    // Check if product is expired
    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }
    public Money getPrice() { return price; }
    public void setPrice(Money price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int threshold) { this.lowStockThreshold = threshold; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String location) { this.warehouseLocation = location; }

    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', type=%s, price=%s, quantity=%d, location='%s'}",
                productId, name, type, price, quantity, warehouseLocation);
    }
}

// Inventory Transaction Log
class InventoryTransaction {
    private final String transactionId;
    private final String productId;
    private final TransactionType type;
    private final int quantityChange;
    private final LocalDateTime timestamp;
    private final String performedBy;
    private final String notes;

    public InventoryTransaction(String productId, TransactionType type,
                                int quantityChange, String performedBy, String notes) {
        this.transactionId = UUID.randomUUID().toString();
        this.productId = productId;
        this.type = type;
        this.quantityChange = quantityChange;
        this.timestamp = LocalDateTime.now();
        this.performedBy = performedBy;
        this.notes = notes;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getProductId() { return productId; }
    public TransactionType getType() { return type; }
    public int getQuantityChange() { return quantityChange; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getPerformedBy() { return performedBy; }
    public String getNotes() { return notes; }

    @Override
    public String toString() {
        return String.format("Transaction{id='%s', product='%s', type=%s, change=%d, time=%s}",
                transactionId, productId, type, quantityChange, timestamp);
    }
}

// Observer Pattern for notifications
interface InventoryObserver {
    void onLowStock(Product product);
    void onProductExpired(Product product);
    void onStockUpdated(Product product, int oldQuantity, int newQuantity);
}

class InventoryNotifier implements InventoryObserver {
    @Override
    public void onLowStock(Product product) {
        System.out.println("ALERT: Low stock for " + product.getName() +
                ". Current quantity: " + product.getQuantity());
    }

    @Override
    public void onProductExpired(Product product) {
        System.out.println("WARNING: Product " + product.getName() + " has expired!");
    }

    @Override
    public void onStockUpdated(Product product, int oldQuantity, int newQuantity) {
        System.out.println("Stock updated for " + product.getName() +
                ": " + oldQuantity + " -> " + newQuantity);
    }
}

// Inventory Manager
class InventoryManager {
    private final Map<String, Product> productMap; // productId -> Product
    private final Map<ProductType, List<Product>> typeToProductMap;
    private final List<InventoryTransaction> transactionHistory;
    private final List<InventoryObserver> observers;

    public InventoryManager() {
        this.productMap = new HashMap<>();
        this.typeToProductMap = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    // Observer management
    public void addObserver(InventoryObserver observer) {
        observers.add(observer);
    }

    private void notifyLowStock(Product product) {
        for (InventoryObserver observer : observers) {
            observer.onLowStock(product);
        }
    }

    private void notifyProductExpired(Product product) {
        for (InventoryObserver observer : observers) {
            observer.onProductExpired(product);
        }
    }

    private void notifyStockUpdated(Product product, int oldQty, int newQty) {
        for (InventoryObserver observer : observers) {
            observer.onStockUpdated(product, oldQty, newQty);
        }
    }

    // Product CRUD operations
    public void addProduct(Product product) {
        productMap.put(product.getProductId(), product);
        typeToProductMap.computeIfAbsent(product.getType(), k -> new ArrayList<>()).add(product);

        logTransaction(product.getProductId(), TransactionType.ADD,
                product.getQuantity(), "SYSTEM", "Initial product addition");
    }

    public void removeProduct(String productId) throws ProductNotFoundException {
        Product product = getProductById(productId);
        productMap.remove(productId);

        List<Product> typeList = typeToProductMap.get(product.getType());
        if (typeList != null) {
            typeList.remove(product);
        }

        logTransaction(productId, TransactionType.REMOVE, -product.getQuantity(),
                "SYSTEM", "Product removed from inventory");
    }

    // Search operations
    public Product getProductById(String productId) throws ProductNotFoundException {
        Product product = productMap.get(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found");
        }
        return product;
    }

    public List<Product> getProductsByName(String name) {
        return productMap.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByType(ProductType type) {
        return typeToProductMap.getOrDefault(type, Collections.emptyList());
    }

    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice, Currency currency) {
        return productMap.values().stream()
                .filter(p -> p.getPrice().getCurrency() == currency)
                .filter(p -> p.getPrice().getAmount() >= minPrice && p.getPrice().getAmount() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts() {
        return productMap.values().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    public List<Product> getExpiredProducts() {
        return productMap.values().stream()
                .filter(Product::isExpired)
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    // Stock management
    public void updateStock(String productId, int quantityChange, String performedBy, String notes)
            throws ProductNotFoundException, InsufficientStockException {
        Product product = getProductById(productId);
        int oldQuantity = product.getQuantity();

        product.updateQuantity(quantityChange);

        TransactionType transactionType = quantityChange > 0 ?
                TransactionType.RESTOCK : TransactionType.REMOVE;
        logTransaction(productId, transactionType, quantityChange, performedBy, notes);

        // Notify observers
        notifyStockUpdated(product, oldQuantity, product.getQuantity());

        if (product.isLowStock()) {
            notifyLowStock(product);
        }

        if (product.isExpired()) {
            notifyProductExpired(product);
        }
    }

    public void restockProduct(String productId, int quantity, String performedBy)
            throws ProductNotFoundException {
        try {
            updateStock(productId, quantity, performedBy, "Restocking");
        } catch (InsufficientStockException e) {
            // Won't happen for positive quantities
        }
    }

    public void removeStock(String productId, int quantity, String performedBy)
            throws ProductNotFoundException, InsufficientStockException {
        updateStock(productId, -quantity, performedBy, "Stock removal");
    }

    // Transaction logging
    private void logTransaction(String productId, TransactionType type,
                                int quantityChange, String performedBy, String notes) {
        InventoryTransaction transaction = new InventoryTransaction(
                productId, type, quantityChange, performedBy, notes
        );
        transactionHistory.add(transaction);
    }

    public List<InventoryTransaction> getTransactionHistory(String productId) {
        return transactionHistory.stream()
                .filter(t -> t.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    public List<InventoryTransaction> getAllTransactions() {
        return new ArrayList<>(transactionHistory);
    }

    // Reports
    public Map<ProductType, Integer> getInventoryValueByType() {
        Map<ProductType, Integer> valueMap = new HashMap<>();
        for (ProductType type : ProductType.values()) {
            int totalQuantity = typeToProductMap.getOrDefault(type, Collections.emptyList())
                    .stream()
                    .mapToInt(Product::getQuantity)
                    .sum();
            valueMap.put(type, totalQuantity);
        }
        return valueMap;
    }
}

// Service Layer
class InventoryService {
    private final InventoryManager inventoryManager;

    public InventoryService() {
        this.inventoryManager = new InventoryManager();
        // Add default notifier
        this.inventoryManager.addObserver(new InventoryNotifier());
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    // Delegated methods with additional business logic
    public void processOrder(String productId, int quantity, String customerId)
            throws ProductNotFoundException, InsufficientStockException {
        inventoryManager.removeStock(productId, quantity, "Order-" + customerId);
        System.out.println("Order processed successfully for customer: " + customerId);
    }

    public void receivedShipment(String productId, int quantity, String supplierId)
            throws ProductNotFoundException {
        inventoryManager.restockProduct(productId, quantity, "Supplier-" + supplierId);
        System.out.println("Shipment received from supplier: " + supplierId);
    }

    public void generateLowStockReport() {
        List<Product> lowStockProducts = inventoryManager.getLowStockProducts();
        System.out.println("\n=== LOW STOCK REPORT ===");
        if (lowStockProducts.isEmpty()) {
            System.out.println("No products are low on stock.");
        } else {
            for (Product product : lowStockProducts) {
                System.out.println(product);
            }
        }
    }

    public void generateExpiredProductsReport() {
        List<Product> expiredProducts = inventoryManager.getExpiredProducts();
        System.out.println("\n=== EXPIRED PRODUCTS REPORT ===");
        if (expiredProducts.isEmpty()) {
            System.out.println("No expired products.");
        } else {
            for (Product product : expiredProducts) {
                System.out.println(product);
            }
        }
    }
}

// Example Usage
public class InventorySystemDemo {
    public static void main(String[] args) {
        try {
            InventoryService service = new InventoryService();
            InventoryManager manager = service.getInventoryManager();

            // Add products
            Product apple = new Product("P001", "Apple", "Fresh red apples",
                    ProductType.FRUITS, new Money(Currency.INR, 150.0), 50);
            apple.setWarehouseLocation("A-1");
            apple.setLowStockThreshold(15);

            Product milk = new Product("P002", "Milk", "Fresh dairy milk",
                    ProductType.DAIRY, new Money(Currency.INR, 60.0), 100);
            milk.setWarehouseLocation("B-2");
            milk.setExpiryDate(LocalDateTime.now().plusDays(5));

            Product noodles = new Product("P003", "Instant Noodles", "Quick meal",
                    ProductType.INSTANT_FOOD, new Money(Currency.INR, 20.0), 200);
            noodles.setWarehouseLocation("C-3");

            manager.addProduct(apple);
            manager.addProduct(milk);
            manager.addProduct(noodles);

            System.out.println("=== All Products ===");
            manager.getAllProducts().forEach(System.out::println);

            // Process an order
            System.out.println("\n=== Processing Order ===");
            service.processOrder("P001", 40, "CUST123");

            // Receive shipment
            System.out.println("\n=== Receiving Shipment ===");
            service.receivedShipment("P001", 30, "SUP456");

            // Search products
            System.out.println("\n=== Search by Name 'milk' ===");
            manager.getProductsByName("milk").forEach(System.out::println);

            System.out.println("\n=== Products by Type DAIRY ===");
            manager.getProductsByType(ProductType.DAIRY).forEach(System.out::println);

            // Generate reports
            service.generateLowStockReport();
            service.generateExpiredProductsReport();

            // View transaction history
            System.out.println("\n=== Transaction History for Apple ===");
            manager.getTransactionHistory("P001").forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
