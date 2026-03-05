package com.conceptcoding.myprjects;

// ✅ Correct — this is a textbook **Observer Design Pattern** scenario.
//
//You identified it perfectly 👍
//
//---
//
//# ✅ Correct Pattern: Observer
//
//### Why?
//
//Because:
//
//* One object → **Stock (Subject)**
//* Many dependent objects → **Displays (Observers)**
//* When stock price changes → all displays update automatically
//* Observers can be added/removed dynamically
//* Loose coupling between subject and observers
//
//This matches Observer definition exactly.
//
//---
//
//# 🧠 Pattern Mapping to Scenario
//
//| Concept             | Your System                              |
//| ------------------- | ---------------------------------------- |
//| Subject             | Stock                                    |
//| Observer            | ChartDisplay, TableDisplay, AlertDisplay |
//| notify()            | Triggered when price changes             |
//| register/unregister | User adds/removes displays               |
//
//---
//
//# 🏗 Clean Java Implementation
//
//---
//
//## 1️⃣ Observer Interface
//
//```java
//interface StockObserver {
//    void update(String stockName, double price);
//}
//```
//
//---
//
//## 2️⃣ Subject Interface
//
//```java
//interface StockSubject {
//    void registerObserver(StockObserver observer);
//    void removeObserver(StockObserver observer);
//    void notifyObservers();
//}
//```
//
//---
//
//## 3️⃣ Concrete Subject (Stock)
//
//```java
//import java.util.ArrayList;
//import java.util.List;
//
//class Stock implements StockSubject {
//
//    private String stockName;
//    private double price;
//    private List<StockObserver> observers = new ArrayList<>();
//
//    public Stock(String stockName, double price) {
//        this.stockName = stockName;
//        this.price = price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//        notifyObservers();
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    @Override
//    public void registerObserver(StockObserver observer) {
//        observers.add(observer);
//    }
//
//    @Override
//    public void removeObserver(StockObserver observer) {
//        observers.remove(observer);
//    }
//
//    @Override
//    public void notifyObservers() {
//        for (StockObserver observer : observers) {
//            observer.update(stockName, price);
//        }
//    }
//}
//```
//
//---
//
//## 4️⃣ Concrete Observers
//
//### 📊 Chart Display
//
//```java
//class ChartDisplay implements StockObserver {
//
//    @Override
//    public void update(String stockName, double price) {
//        System.out.println("Chart updated for " + stockName + " with price: " + price);
//    }
//}
//```
//
//---
//
//### 📋 Table Display
//
//```java
//class TableDisplay implements StockObserver {
//
//    @Override
//    public void update(String stockName, double price) {
//        System.out.println("Table updated for " + stockName + " with price: " + price);
//    }
//}
//```
//
//---
//
//### 🚨 Alert Display
//
//```java
//class AlertDisplay implements StockObserver {
//
//    @Override
//    public void update(String stockName, double price) {
//        if (price > 1000) {
//            System.out.println("Alert! " + stockName + " crossed 1000!");
//        }
//    }
//}
//```
//
//---
//
//# 🚀 Client Code
//
//```java
//public class Main {
//
//    public static void main(String[] args) {
//
//        Stock appleStock = new Stock("AAPL", 900);
//
//        StockObserver chart = new ChartDisplay();
//        StockObserver table = new TableDisplay();
//        StockObserver alert = new AlertDisplay();
//
//        appleStock.registerObserver(chart);
//        appleStock.registerObserver(table);
//        appleStock.registerObserver(alert);
//
//        appleStock.setPrice(950);
//        System.out.println("------");
//
//        appleStock.setPrice(1100);
//    }
//}
//```
//
//---
//
//# 🧠 Real-World Examples
//
//Observer is used in:
//
//* **Spring Framework** → ApplicationEventPublisher
//* **Java SE** → java.util.Observer (deprecated but concept exists)
//* **React** → State updates re-render components
//* **Apache Kafka** → Pub-sub model
//
//---
//
//# 🎯 Interview-Ready Answer
//
//> I would use the **Observer Pattern**.
//> The Stock acts as the Subject, and displays act as Observers.
//> When the stock price changes, the subject notifies all registered observers.
//> This ensures loose coupling and dynamic addition/removal of display components.
//
//---
//
//# ⚡ Advanced Discussion (Senior-Level Insight)
//
//You can improve this design by:
//
//* Making it thread-safe (CopyOnWriteArrayList)
//* Using async notification (ExecutorService)
//* Using event objects instead of primitives
//* Combining with MVC pattern
//
//---
//
//Since you’ve been working a lot with concurrency and real-world backend design,
//if you want, I can next show:
//
//* 🔥 Thread-safe Observer implementation
//* 🔥 Async notification version
//* 🔥 Event-driven architecture version
//* 🔥 How this maps to Kafka or Spring Events
//
//Just tell me 😄
public class Question18 {
}
