package com.conceptcoding.myprjects;

//Good instinct 👍 — **Decorator** can be used here, but it is **NOT the main pattern** for this problem.
//
//This question is actually testing whether you can identify **multiple patterns together**.
//
//Let’s analyze properly like a 6-year candidate.
//
//---
//
//# 🔎 Requirements Analysis
//
//We have:
//
//### 1️⃣ Complex object with many optional parts
//
//→ Sounds like **Builder**
//
//### 2️⃣ Same creation process but variations in steps
//
//→ Sounds like **Template Method**
//
//### 3️⃣ Many optional toppings
//
//→ Sounds like **Decorator**
//
//So the correct answer is:
//
//> Combination of **Builder + Template Method + (optional) Decorator**
//
//Now let’s break it clearly.
//
//---
//
//# ✅ 1️⃣ Builder Pattern (Primary)
//
//Why?
//
//* Pizza has many optional components:
//
//  * base
//  * sauce
//  * toppings
//  * size
//* We want readable construction
//* Avoid telescoping constructors
//
//---
//
//### ❌ Bad Way
//
//```java
//new Pizza("thin", "tomato", true, false, true, false, "large");
//```
//
//Unreadable.
//
//---
//
//### ✅ Builder Way
//
//```java
//Pizza pizza = Pizza.builder()
//        .base("Thin Crust")
//        .sauce("Tomato")
//        .addTopping("Cheese")
//        .addTopping("Olives")
//        .size("Large")
//        .build();
//```
//
//Much cleaner.
//
//---
//
//### Example Implementation
//
//```java
//public class Pizza {
//
//    private String base;
//    private String sauce;
//    private List<String> toppings;
//    private String size;
//
//    private Pizza(Builder builder) {
//        this.base = builder.base;
//        this.sauce = builder.sauce;
//        this.toppings = builder.toppings;
//        this.size = builder.size;
//    }
//
//    public static class Builder {
//        private String base;
//        private String sauce;
//        private List<String> toppings = new ArrayList<>();
//        private String size;
//
//        public Builder base(String base) {
//            this.base = base;
//            return this;
//        }
//
//        public Builder sauce(String sauce) {
//            this.sauce = sauce;
//            return this;
//        }
//
//        public Builder addTopping(String topping) {
//            this.toppings.add(topping);
//            return this;
//        }
//
//        public Builder size(String size) {
//            this.size = size;
//            return this;
//        }
//
//        public Pizza build() {
//            return new Pizza(this);
//        }
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//}
//```
//
//---
//
//# ✅ 2️⃣ Template Method (For Process Flow)
//
//Requirement:
//
//> Same creation steps but variation in implementation
//
//This is classic **Template Method**.
//
//---
//
//### Base Class
//
//```java
//abstract class PizzaPreparation {
//
//    public final void preparePizza() {
//        prepareBase();
//        addSauce();
//        addToppings();
//        bake();
//        pack();
//    }
//
//    protected abstract void prepareBase();
//    protected abstract void addSauce();
//    protected abstract void addToppings();
//
//    protected void bake() {
//        System.out.println("Baking at 180 degrees");
//    }
//
//    protected void pack() {
//        System.out.println("Packing pizza");
//    }
//}
//```
//
//---
//
//### Specialty Pizza
//
//```java
//class SecretRecipePizza extends PizzaPreparation {
//
//    @Override
//    protected void prepareBase() {
//        System.out.println("Preparing secret dough");
//    }
//
//    @Override
//    protected void addSauce() {
//        System.out.println("Adding secret sauce");
//    }
//
//    @Override
//    protected void addToppings() {
//        System.out.println("Adding chef special toppings");
//    }
//}
//```
//
//Now:
//
//```java
//new SecretRecipePizza().preparePizza();
//```
//
//---
//
//# 🤔 Where Does Decorator Fit?
//
//You suggested:
//
//> ThinCrustPizza, ThickCrustPizza and Decorator for toppings
//
//Yes — Decorator works well for toppings pricing.
//
//Example:
//
//```java
//abstract class Pizza {
//    abstract double cost();
//}
//```
//
//Base pizza:
//
//```java
//class ThinCrustPizza extends Pizza {
//    public double cost() { return 100; }
//}
//```
//
//Decorator:
//
//```java
//abstract class ToppingDecorator extends Pizza {
//    protected Pizza pizza;
//
//    public ToppingDecorator(Pizza pizza) {
//        this.pizza = pizza;
//    }
//}
//```
//
//Concrete topping:
//
//```java
//class Cheese extends ToppingDecorator {
//
//    public Cheese(Pizza pizza) {
//        super(pizza);
//    }
//
//    public double cost() {
//        return pizza.cost() + 20;
//    }
//}
//```
//
//Usage:
//
//```java
//Pizza pizza = new Cheese(new Olives(new ThinCrustPizza()));
//```
//
//That’s perfect for pricing.
//
//---
//
//# 🎯 So What’s The Best Interview Answer?
//
//You say:
//
//> I would use Builder pattern to construct a complex Pizza object with many optional components.
//>
//> Since the pizza creation process follows fixed steps but allows variation in implementation, I would use Template Method pattern to define the preparation workflow.
//>
//> Additionally, if toppings need dynamic behavior like cost calculation, I could use Decorator to wrap base pizzas with toppings.
//
//That’s a complete answer.
//
//---
//
//# 🔥 Pattern Breakdown
//
//| Requirement              | Pattern         |
//| ------------------------ | --------------- |
//| Many optional parts      | Builder         |
//| Same steps, variations   | Template Method |
//| Dynamic toppings pricing | Decorator       |
//
//---
//
//# 💡 Important Correction
//
//If you ONLY say Decorator:
//
//Interviewers will ask:
//
//> How will you handle size? Sauce? Readable construction?
//
//Then you’ll struggle.
//
//So Builder is primary here.
//
//---
//
//# 🧠 Senior-Level Insight
//
//In real-world:
//
//* Domino’s app → Builder
//* Pizza making workflow → Template
//* Add-ons pricing → Decorator
//
//---
//
//If you want next:
//
//* I can give you a tricky FAANG twist on this pizza problem
//* Or compare Builder vs Factory in this context
//* Or give you full combined architecture
//
//You’re thinking well — just need pattern precision.
public class Question15 {
}

// Perfect 👌
//I’ll give you a **clean, interview-ready complete solution** combining:
//
//* ✅ **Builder Pattern** (complex object creation)
//* ✅ **Template Method** (fixed preparation steps)
//* ✅ **Decorator Pattern** (dynamic toppings pricing)
//
//This will look like a proper senior-level design.
//
//---
//
//# 🧱 1️⃣ Builder Pattern (Construct Pizza Configuration)
//
//This builds the pizza configuration object cleanly.
//
//```java
//import java.util.ArrayList;
//import java.util.List;
//
//public class Pizza {
//
//    private final String base;
//    private final String sauce;
//    private final String size;
//    private final List<String> toppings;
//
//    private Pizza(Builder builder) {
//        this.base = builder.base;
//        this.sauce = builder.sauce;
//        this.size = builder.size;
//        this.toppings = builder.toppings;
//    }
//
//    public String getBase() { return base; }
//    public String getSauce() { return sauce; }
//    public String getSize() { return size; }
//    public List<String> getToppings() { return toppings; }
//
//    public static class Builder {
//        private String base;
//        private String sauce;
//        private String size;
//        private List<String> toppings = new ArrayList<>();
//
//        public Builder base(String base) {
//            this.base = base;
//            return this;
//        }
//
//        public Builder sauce(String sauce) {
//            this.sauce = sauce;
//            return this;
//        }
//
//        public Builder size(String size) {
//            this.size = size;
//            return this;
//        }
//
//        public Builder addTopping(String topping) {
//            this.toppings.add(topping);
//            return this;
//        }
//
//        public Pizza build() {
//            return new Pizza(this);
//        }
//    }
//}
//```
//
//---
//
//# 🍕 2️⃣ Template Method (Pizza Preparation Process)
//
//Defines the fixed algorithm.
//
//```java
//public abstract class PizzaPreparation {
//
//    public final void prepare(Pizza pizza) {
//        prepareBase(pizza);
//        addSauce(pizza);
//        addToppings(pizza);
//        bake();
//        pack();
//    }
//
//    protected abstract void prepareBase(Pizza pizza);
//    protected abstract void addSauce(Pizza pizza);
//    protected abstract void addToppings(Pizza pizza);
//
//    protected void bake() {
//        System.out.println("Baking pizza at 180°C...");
//    }
//
//    protected void pack() {
//        System.out.println("Packing pizza...");
//    }
//}
//```
//
//---
//
//### 🧑‍🍳 Concrete Implementation
//
//```java
//public class StandardPizzaPreparation extends PizzaPreparation {
//
//    @Override
//    protected void prepareBase(Pizza pizza) {
//        System.out.println("Preparing " + pizza.getBase() + " base");
//    }
//
//    @Override
//    protected void addSauce(Pizza pizza) {
//        System.out.println("Adding " + pizza.getSauce() + " sauce");
//    }
//
//    @Override
//    protected void addToppings(Pizza pizza) {
//        System.out.println("Adding toppings: " + pizza.getToppings());
//    }
//}
//```
//
//---
//
//# 💰 3️⃣ Decorator Pattern (Dynamic Pricing)
//
//Base abstraction:
//
//```java
//public abstract class PizzaCost {
//    public abstract double cost();
//    public abstract String description();
//}
//```
//
//---
//
//### Base Pizza Cost
//
//```java
//public class BasicPizza extends PizzaCost {
//
//    private final String size;
//
//    public BasicPizza(String size) {
//        this.size = size;
//    }
//
//    @Override
//    public double cost() {
//        return switch (size.toLowerCase()) {
//            case "small" -> 100;
//            case "medium" -> 150;
//            case "large" -> 200;
//            default -> 100;
//        };
//    }
//
//    @Override
//    public String description() {
//        return size + " Pizza";
//    }
//}
//```
//
//---
//
//### Topping Decorator
//
//```java
//public abstract class ToppingDecorator extends PizzaCost {
//
//    protected PizzaCost pizzaCost;
//
//    public ToppingDecorator(PizzaCost pizzaCost) {
//        this.pizzaCost = pizzaCost;
//    }
//}
//```
//
//---
//
//### Concrete Toppings
//
//```java
//public class Cheese extends ToppingDecorator {
//
//    public Cheese(PizzaCost pizzaCost) {
//        super(pizzaCost);
//    }
//
//    @Override
//    public double cost() {
//        return pizzaCost.cost() + 20;
//    }
//
//    @Override
//    public String description() {
//        return pizzaCost.description() + ", Cheese";
//    }
//}
//```
//
//```java
//public class Olives extends ToppingDecorator {
//
//    public Olives(PizzaCost pizzaCost) {
//        super(pizzaCost);
//    }
//
//    @Override
//    public double cost() {
//        return pizzaCost.cost() + 15;
//    }
//
//    @Override
//    public String description() {
//        return pizzaCost.description() + ", Olives";
//    }
//}
//```
//
//---
//
//# 🚀 4️⃣ Client Code
//
//```java
//public class Main {
//
//    public static void main(String[] args) {
//
//        // Step 1: Build Pizza Configuration
//        Pizza pizza = new Pizza.Builder()
//                .base("Thin Crust")
//                .sauce("Tomato")
//                .size("Large")
//                .addTopping("Cheese")
//                .addTopping("Olives")
//                .build();
//
//        // Step 2: Prepare Pizza (Template Method)
//        PizzaPreparation preparation = new StandardPizzaPreparation();
//        preparation.prepare(pizza);
//
//        // Step 3: Calculate Cost (Decorator)
//        PizzaCost cost = new BasicPizza(pizza.getSize());
//        cost = new Cheese(cost);
//        cost = new Olives(cost);
//
//        System.out.println("Order: " + cost.description());
//        System.out.println("Total Cost: ₹" + cost.cost());
//    }
//}
//```
//
//---
//
//# 🧠 What This Demonstrates
//
//| Concern                      | Pattern Used    |
//| ---------------------------- | --------------- |
//| Complex object creation      | Builder         |
//| Fixed process, varying steps | Template Method |
//| Dynamic toppings pricing     | Decorator       |
//
//---
//
//# 🎯 Final Interview Answer
//
//If interviewer asks:
//
//> Why combine patterns?
//
//You say:
//
//> Builder handles complex construction, Template Method defines the cooking workflow, and Decorator allows dynamic pricing without modifying base pizza classes. This keeps the design extensible and open for new pizza types or toppings.
//
//That’s a strong 6-year answer.
//
//---
//
//If you want next:
//
//* I can simplify this into a production-style architecture
//* Or show how Spring would model this
//* Or give you a tricky follow-up interviewer question
//
//Your pattern understanding is growing very nicely 👌