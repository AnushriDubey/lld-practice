package com.conceptcoding.myprjects; /**
## **🎮 Question 5: Coffee Shop System**
**Scenario:**
Coffee shop sells:
- Base drinks: Espresso, Decaf, DarkRoast
- Add-ons: Milk, Soy, Mocha, Whip
Customer can order:
- "Espresso with Milk and Whip"
- "DarkRoast with Mocha and Soy and Whip"
- Any combination
**Requirements:**
- Each add-on increases the price
- Easy to add new add-ons
- Calculate total price dynamically
- Don't create separate class for every combination
**Question:** Which design pattern(s) would you use?
Solution:
Decorator Design pattern
One BaseDrink Interface with two method getDescription(), getPrice().
and one DrinkDecorator extending BaseDrink

Show less
1:09 AM

Validated decorator pattern solution and verified structural correctness.
12s











Excellent! Your solution is 100% CORRECT! ✅

The Decorator Pattern is the perfect and classic solution for this problem. Let me implement it in Java with the structure you described:

Coffee Shop - Decorator Pattern
Code 
Perfect! Your solution is absolutely correct! 🎉

✅ Your Solution Analysis
What you got RIGHT:

✅ Decorator Pattern - Perfect choice!
✅ BaseDrink interface with getDescription() and getPrice() - Correct!
✅ DrinkDecorator as abstract decorator - Correct!
Minor clarification:

You said "DrinkDecorator extending BaseDrink" - technically it implements BaseDrink (since it's an interface), but your concept is 100% correct!
🎯 Why Decorator Pattern is Perfect Here:
Problem Solved:
Without Decorator:
❌ Need 100+ classes for combinations
   - EspressoWithMilk
   - EspressoWithMilkAndWhip
   - EspressoWithMilkAndWhipAndMocha
   - ... (combinatorial explosion!)

With Decorator:
✅ Just 7 base classes + N add-ons
   - 3 base drinks
   - 1 decorator base
   - 6 concrete decorators
   - Infinite combinations!
Key Benefits:
No class explosion - Avoid creating separate classes for every combination
Easy to extend - Add new add-ons without modifying existing code
Dynamic composition - Build drinks at runtime
Flexible pricing - Each decorator adds its price
Open/Closed Principle - Open for extension, closed for modification
🏗️ Structure (Exactly as you described):
BaseDrink (Interface)
    ├─ getDescription()
    └─ getPrice()
        │
        ├── Concrete Base Drinks
        │   ├── Espresso
        │   ├── Decaf
        │   └── DarkRoast
        │
        └── DrinkDecorator (Abstract)
            └── Concrete Decorators
                ├── Milk
                ├── Soy
                ├── Mocha
                └── Whip
💡 How It Works:
java
// Start with base drink
BaseDrink drink = new Espresso();  // $1.99

// Wrap with decorators
drink = new Milk(drink);    // $1.99 + $0.30 = $2.29
drink = new Mocha(drink);   // $2.29 + $0.50 = $2.79
drink = new Whip(drink);    // $2.79 + $0.25 = $3.04

// Final result
drink.getDescription(); // "Espresso, Milk, Mocha, Whipped Cream"
drink.getPrice();       // 3.04
Your solution is textbook perfect - this is actually the classic example used in the Gang of Four Design Patterns book! 
**/


// ======================== BASE COMPONENT ========================
/**
 * BaseDrink: Component interface
 * Defines operations for both concrete components and decorators
 */
interface BaseDrink {
    String getDescription();
    double getPrice();
}

// ======================== CONCRETE COMPONENTS ========================
/**
 * Concrete base drinks (without any add-ons)
 */
class Espresso implements BaseDrink {
    @Override
    public String getDescription() {
        return "Espresso";
    }
    
    @Override
    public double getPrice() {
        return 1.99;
    }
}

class Decaf implements BaseDrink {
    @Override
    public String getDescription() {
        return "Decaf Coffee";
    }
    
    @Override
    public double getPrice() {
        return 1.79;
    }
}

class DarkRoast implements BaseDrink {
    @Override
    public String getDescription() {
        return "Dark Roast Coffee";
    }
    
    @Override
    public double getPrice() {
        return 1.89;
    }
}

class HouseBlend implements BaseDrink {
    @Override
    public String getDescription() {
        return "House Blend Coffee";
    }
    
    @Override
    public double getPrice() {
        return 1.69;
    }
}

// ======================== DECORATOR (ABSTRACT) ========================
/**
 * DrinkDecorator: Abstract decorator
 * Implements BaseDrink and maintains a reference to wrapped drink
 */
abstract class DrinkDecorator implements BaseDrink {
    protected BaseDrink drink; // Reference to wrapped drink
    
    public DrinkDecorator(BaseDrink drink) {
        this.drink = drink;
    }
    
    @Override
    public String getDescription() {
        return drink.getDescription();
    }
    
    @Override
    public double getPrice() {
        return drink.getPrice();
    }
}

// ======================== CONCRETE DECORATORS (ADD-ONS) ========================
/**
 * Each add-on is a concrete decorator that adds its own price and description
 */
class Milk extends DrinkDecorator {
    public Milk(BaseDrink drink) {
        super(drink);
    }
    
    @Override
    public String getDescription() {
        return drink.getDescription() + ", Milk";
    }
    
    @Override
    public double getPrice() {
        return drink.getPrice() + 0.30;
    }
}

class Soy extends DrinkDecorator {
    public Soy(BaseDrink drink) {
        super(drink);
    }
    
    @Override
    public String getDescription() {
        return drink.getDescription() + ", Soy";
    }
    
    @Override
    public double getPrice() {
        return drink.getPrice() + 0.40;
    }
}

class Mocha extends DrinkDecorator {
    public Mocha(BaseDrink drink) {
        super(drink);
    }
    
    @Override
    public String getDescription() {
        return drink.getDescription() + ", Mocha";
    }
    
    @Override
    public double getPrice() {
        return drink.getPrice() + 0.50;
    }
}

class Whip extends DrinkDecorator {
    public Whip(BaseDrink drink) {
        super(drink);
    }
    
    @Override
    public String getDescription() {
        return drink.getDescription() + ", Whipped Cream";
    }
    
    @Override
    public double getPrice() {
        return drink.getPrice() + 0.25;
    }
}

class Caramel extends DrinkDecorator {
    public Caramel(BaseDrink drink) {
        super(drink);
    }
    
    @Override
    public String getDescription() {
        return drink.getDescription() + ", Caramel";
    }
    
    @Override
    public double getPrice() {
        return drink.getPrice() + 0.45;
    }
}

class Vanilla extends DrinkDecorator {
    public Vanilla(BaseDrink drink) {
        super(drink);
    }
    
    @Override
    public String getDescription() {
        return drink.getDescription() + ", Vanilla";
    }
    
    @Override
    public double getPrice() {
        return drink.getPrice() + 0.35;
    }
}

// ======================== ORDER SYSTEM ========================
class CoffeeOrder {
    private BaseDrink drink;
    private int orderNumber;
    private static int orderCounter = 1;
    
    public CoffeeOrder(BaseDrink drink) {
        this.drink = drink;
        this.orderNumber = orderCounter++;
    }
    
    public void printReceipt() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("☕ ORDER #" + orderNumber);
        System.out.println("=".repeat(50));
        System.out.println("Item: " + drink.getDescription());
        System.out.println("Price: $" + String.format("%.2f", drink.getPrice()));
        System.out.println("=".repeat(50));
    }
}

// ======================== DEMO ========================
public class CoffeeShopDemo {
    public static void main(String[] args) {
        System.out.println("☕ WELCOME TO JAVA COFFEE SHOP ☕\n");
        
        // Order 1: Simple Espresso
        System.out.println("--- Customer 1 ---");
        BaseDrink order1 = new Espresso();
        new CoffeeOrder(order1).printReceipt();
        
        // Order 2: Espresso with Milk and Whip
        System.out.println("\n--- Customer 2 ---");
        BaseDrink order2 = new Espresso();
        order2 = new Milk(order2);
        order2 = new Whip(order2);
        new CoffeeOrder(order2).printReceipt();
        
        // Order 3: DarkRoast with Mocha, Soy, and Whip
        System.out.println("\n--- Customer 3 ---");
        BaseDrink order3 = new DarkRoast();
        order3 = new Mocha(order3);
        order3 = new Soy(order3);
        order3 = new Whip(order3);
        new CoffeeOrder(order3).printReceipt();
        
        // Order 4: Decaf with everything!
        System.out.println("\n--- Customer 4 (The Indecisive One) ---");
        BaseDrink order4 = new Decaf();
        order4 = new Milk(order4);
        order4 = new Soy(order4);
        order4 = new Mocha(order4);
        order4 = new Whip(order4);
        order4 = new Caramel(order4);
        order4 = new Vanilla(order4);
        new CoffeeOrder(order4).printReceipt();
        
        // Order 5: Double Mocha (demonstrating multiple same add-ons)
        System.out.println("\n--- Customer 5 (Chocoholic) ---");
        BaseDrink order5 = new Espresso();
        order5 = new Mocha(order5);
        order5 = new Mocha(order5); // Double mocha!
        order5 = new Whip(order5);
        new CoffeeOrder(order5).printReceipt();
        
        // Demonstrate ease of adding new drinks/add-ons
        System.out.println("\n\n" + "=".repeat(50));
        System.out.println("✅ BENEFITS OF DECORATOR PATTERN:");
        System.out.println("=".repeat(50));
        System.out.println("1. ✓ No class explosion (no EspressoWithMilk class needed)");
        System.out.println("2. ✓ Easy to add new add-ons (just create new decorator)");
        System.out.println("3. ✓ Dynamic pricing calculation");
        System.out.println("4. ✓ Any combination possible");
        System.out.println("5. ✓ Can add same decorator multiple times (double mocha)");
        System.out.println("6. ✓ Follows Open/Closed Principle");
        System.out.println("=".repeat(50));
        
        // Show structure
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📐 STRUCTURE:");
        System.out.println("=".repeat(50));
        System.out.println("BaseDrink (Interface)");
        System.out.println("├── Espresso (Concrete Component)");
        System.out.println("├── Decaf (Concrete Component)");
        System.out.println("├── DarkRoast (Concrete Component)");
        System.out.println("└── DrinkDecorator (Abstract Decorator)");
        System.out.println("    ├── Milk (Concrete Decorator)");
        System.out.println("    ├── Soy (Concrete Decorator)");
        System.out.println("    ├── Mocha (Concrete Decorator)");
        System.out.println("    ├── Whip (Concrete Decorator)");
        System.out.println("    ├── Caramel (Concrete Decorator)");
        System.out.println("    └── Vanilla (Concrete Decorator)");
        System.out.println("=".repeat(50));
    }
}