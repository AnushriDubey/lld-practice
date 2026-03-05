package com.conceptcoding.myprjects;


//Good start! But there's a **better approach** than just interfaces. Let me show you why and the complete solution.
//
//        ---
//
//        ## **Why Simple Interfaces Are Not Enough**
//
//        ### **❌ Problem with Just Interfaces:**
//
//        ```java
//interface Flyable {
//    void fly();
//}
//
//interface Quackable {
//    void quack();
//}
//
//// ❌ PROBLEM: Code duplication!
//class MallardDuck implements Flyable, Quackable {
//    @Override
//    public void fly() {
//        System.out.println("Flying with wings");  // Duplicated code
//    }
//
//    @Override
//    public void quack() {
//        System.out.println("Quack quack");  // Duplicated code
//    }
//}
//
//class RedheadDuck implements Flyable, Quackable {
//    @Override
//    public void fly() {
//        System.out.println("Flying with wings");  // SAME CODE - duplicated!
//    }
//
//    @Override
//    public void quack() {
//        System.out.println("Quack quack");  // SAME CODE - duplicated!
//    }
//}
//```
//
//        **Problems:**
//        1. ❌ Code duplication (many ducks fly the same way)
//2. ❌ Can't change behavior at runtime (injured duck)
//        3. ❌ Hard to reuse behaviors
//
//---
//
//        ## **✅ Correct Solution: Strategy Pattern**
//
//        **Key Insight:** Encapsulate behaviors (flying, quacking) as separate strategy objects that can be composed.
//
//        ---
//
//        ## **Complete Implementation**
//
//        ```java
//// ==================== STRATEGY INTERFACES ====================
//
//// Flying behavior strategies
//interface FlyBehavior {
//    void fly();
//}
//
//// Quacking behavior strategies
//interface QuackBehavior {
//    void quack();
//}
//
//// ==================== CONCRETE STRATEGIES ====================
//
//// ============= FLY BEHAVIORS =============
//
//class FlyWithWings implements FlyBehavior {
//    @Override
//    public void fly() {
//        System.out.println("🦆 Flying with wings!");
//    }
//}
//
//class FlyWithPropeller implements FlyBehavior {
//    @Override
//    public void fly() {
//        System.out.println("🤖 Flying with propeller!");
//    }
//}
//
//class FlyNoWay implements FlyBehavior {
//    @Override
//    public void fly() {
//        System.out.println("❌ Can't fly!");
//    }
//}
//
//class FlySlowly implements FlyBehavior {
//    @Override
//    public void fly() {
//        System.out.println("🐥 Flying slowly (injured)");
//    }
//}
//
//// ============= QUACK BEHAVIORS =============
//
//class Quack implements QuackBehavior {
//    @Override
//    public void quack() {
//        System.out.println("🦆 Quack quack!");
//    }
//}
//
//class Squeak implements QuackBehavior {
//    @Override
//    public void quack() {
//        System.out.println("🛁 Squeak squeak!");
//    }
//}
//
//class Beep implements QuackBehavior {
//    @Override
//    public void quack() {
//        System.out.println("🤖 Beep beep!");
//    }
//}
//
//class MuteQuack implements QuackBehavior {
//    @Override
//    public void quack() {
//        System.out.println("🤫 <Silence>");
//    }
//}
//
//// ==================== DUCK BASE CLASS ====================
//
//abstract class Duck {
//    // HAS-A relationship (Composition, not inheritance)
//    protected FlyBehavior flyBehavior;
//    protected QuackBehavior quackBehavior;
//
//    // Delegates to behavior objects
//    public void performFly() {
//        flyBehavior.fly();  // Delegates to strategy
//    }
//
//    public void performQuack() {
//        quackBehavior.quack();  // Delegates to strategy
//    }
//
//    // RUNTIME BEHAVIOR CHANGE ✅
//    public void setFlyBehavior(FlyBehavior fb) {
//        this.flyBehavior = fb;
//    }
//
//    public void setQuackBehavior(QuackBehavior qb) {
//        this.quackBehavior = qb;
//    }
//
//    // Common behaviors
//    public void swim() {
//        System.out.println("💧 All ducks can swim!");
//    }
//
//    public abstract void display();
//}
//
//// ==================== CONCRETE DUCK TYPES ====================
//
//class MallardDuck extends Duck {
//    public MallardDuck() {
//        // Initialize with specific behaviors
//        flyBehavior = new FlyWithWings();
//        quackBehavior = new Quack();
//    }
//
//    @Override
//    public void display() {
//        System.out.println("🦆 I'm a Mallard Duck");
//    }
//}
//
//class RubberDuck extends Duck {
//    public RubberDuck() {
//        flyBehavior = new FlyNoWay();      // Can't fly
//        quackBehavior = new Squeak();      // Squeaks
//    }
//
//    @Override
//    public void display() {
//        System.out.println("🛁 I'm a Rubber Duck");
//    }
//}
//
//class DecoyDuck extends Duck {
//    public DecoyDuck() {
//        flyBehavior = new FlyNoWay();      // Can't fly
//        quackBehavior = new MuteQuack();   // Silent
//    }
//
//    @Override
//    public void display() {
//        System.out.println("🪵 I'm a Decoy Duck");
//    }
//}
//
//class RobotDuck extends Duck {
//    public RobotDuck() {
//        flyBehavior = new FlyWithPropeller();  // Mechanical flying
//        quackBehavior = new Beep();            // Beeps
//    }
//
//    @Override
//    public void display() {
//        System.out.println("🤖 I'm a Robot Duck");
//    }
//}
//
//class RedheadDuck extends Duck {
//    public RedheadDuck() {
//        flyBehavior = new FlyWithWings();
//        quackBehavior = new Quack();
//    }
//
//    @Override
//    public void display() {
//        System.out.println("🦆 I'm a Redhead Duck");
//    }
//}
//
//// ==================== DEMO ====================
//
//public class DuckSimulator {
//    public static void main(String[] args) {
//        System.out.println("========== DUCK SIMULATION ==========\n");
//
//        // ============= SCENARIO 1: Basic Behaviors =============
//
//        System.out.println("--- Scenario 1: Different Duck Types ---");
//
//        Duck mallard = new MallardDuck();
//        mallard.display();
//        mallard.performFly();
//        mallard.performQuack();
//        mallard.swim();
//        System.out.println();
//
//        Duck rubber = new RubberDuck();
//        rubber.display();
//        rubber.performFly();
//        rubber.performQuack();
//        rubber.swim();
//        System.out.println();
//
//        Duck decoy = new DecoyDuck();
//        decoy.display();
//        decoy.performFly();
//        decoy.performQuack();
//        decoy.swim();
//        System.out.println();
//
//        Duck robot = new RobotDuck();
//        robot.display();
//        robot.performFly();
//        robot.performQuack();
//        robot.swim();
//        System.out.println();
//
//        // ============= SCENARIO 2: Runtime Behavior Change =============
//
//        System.out.println("--- Scenario 2: Injured Duck (Runtime Change) ---");
//
//        Duck wildDuck = new MallardDuck();
//        wildDuck.display();
//
//        System.out.println("Before injury:");
//        wildDuck.performFly();
//
//        // Duck gets injured! Change behavior at runtime ✅
//        System.out.println("\n🩹 Duck got injured!");
//        wildDuck.setFlyBehavior(new FlySlowly());
//
//        System.out.println("After injury:");
//        wildDuck.performFly();
//
//        // Duck recovers!
//        System.out.println("\n💊 Duck recovered!");
//        wildDuck.setFlyBehavior(new FlyWithWings());
//
//        System.out.println("After recovery:");
//        wildDuck.performFly();
//        System.out.println();
//
//        // ============= SCENARIO 3: Reusing Behaviors =============
//
//        System.out.println("--- Scenario 3: Behavior Reuse ---");
//
//        Duck redhead = new RedheadDuck();
//        redhead.display();
//
//        // Redhead duck and Mallard duck share same behaviors
//        // No code duplication! ✅
//        redhead.performFly();  // Uses FlyWithWings (shared with Mallard)
//        redhead.performQuack();  // Uses Quack (shared with Mallard)
//        System.out.println();
//
//        // ============= SCENARIO 4: Dynamic Duck Creation =============
//
//        System.out.println("--- Scenario 4: Custom Duck ---");
//
//        // Create a custom duck with any combination of behaviors
//        Duck customDuck = new MallardDuck() {
//            {
//                // Anonymous initialization block
//                flyBehavior = new FlyWithPropeller();  // Robot flying
//                quackBehavior = new Beep();            // Robot sound
//            }
//
//            @Override
//            public void display() {
//                System.out.println("🎨 I'm a Custom Hybrid Duck");
//            }
//        };
//
//        customDuck.display();
//        customDuck.performFly();
//        customDuck.performQuack();
//        System.out.println();
//
//        // ============= SCENARIO 5: Duck Array =============
//
//        System.out.println("--- Scenario 5: Pond Simulation ---");
//
//        Duck[] pond = {
//                new MallardDuck(),
//                new RubberDuck(),
//                new DecoyDuck(),
//                new RobotDuck()
//        };
//
//        System.out.println("All ducks in the pond:");
//        for (Duck duck : pond) {
//            duck.display();
//            duck.performFly();
//            duck.performQuack();
//            System.out.println();
//        }
//
//        System.out.println("========== END OF SIMULATION ==========");
//    }
//}
//```
//
//        ---
//
//        ## **Output:**
//
//        ```
//        ========== DUCK SIMULATION ==========
//
//        --- Scenario 1: Different Duck Types ---
//        🦆 I'm a Mallard Duck
//        🦆 Flying with wings!
//        🦆 Quack quack!
//        💧 All ducks can swim!
//
//        🛁 I'm a Rubber Duck
//        ❌ Can't fly!
//        🛁 Squeak squeak!
//        💧 All ducks can swim!
//
//        🪵 I'm a Decoy Duck
//        ❌ Can't fly!
//        🤫 <Silence>
//💧 All ducks can swim!
//
//        🤖 I'm a Robot Duck
//        🤖 Flying with propeller!
//        🤖 Beep beep!
//        💧 All ducks can swim!
//
//        --- Scenario 2: Injured Duck (Runtime Change) ---
//        🦆 I'm a Mallard Duck
//Before injury:
//        🦆 Flying with wings!
//
//        🩹 Duck got injured!
//After injury:
//        🐥 Flying slowly (injured)
//
//💊 Duck recovered!
//After recovery:
//        🦆 Flying with wings!
//
//        --- Scenario 3: Behavior Reuse ---
//        🦆 I'm a Redhead Duck
//        🦆 Flying with wings!
//        🦆 Quack quack!
//
//        --- Scenario 4: Custom Duck ---
//        🎨 I'm a Custom Hybrid Duck
//        🤖 Flying with propeller!
//        🤖 Beep beep!
//
//        --- Scenario 5: Pond Simulation ---
//All ducks in the pond:
//        🦆 I'm a Mallard Duck
//        🦆 Flying with wings!
//        🦆 Quack quack!
//
//        🛁 I'm a Rubber Duck
//        ❌ Can't fly!
//        🛁 Squeak squeak!
//
//        🪵 I'm a Decoy Duck
//        ❌ Can't fly!
//        🤫 <Silence>
//
//🤖 I'm a Robot Duck
//        🤖 Flying with propeller!
//        🤖 Beep beep!
//
//        ========== END OF SIMULATION ==========
//        ```
//
//        ---
//
//        ## **Why Strategy Pattern > Simple Interfaces**
//
//        ### **Comparison:**
//
//        | Approach | Code Reuse | Runtime Change | Maintainability |
//        |----------|-----------|----------------|-----------------|
//        | **Interfaces only** | ❌ No (duplication) | ❌ No | ❌ Poor |
//        | **Strategy Pattern** | ✅ Yes | ✅ Yes | ✅ Excellent |
//
//        ---
//
//        ### **Example: Interfaces Only**
//
//        ```java
//// ❌ BAD: Every duck reimplements fly()
//
//interface Flyable {
//    void fly();
//}
//
//class MallardDuck implements Flyable {
//    public void fly() {
//        System.out.println("Flying with wings");  // Code here
//    }
//}
//
//class RedheadDuck implements Flyable {
//    public void fly() {
//        System.out.println("Flying with wings");  // DUPLICATE code
//    }
//}
//
//class WoodDuck implements Flyable {
//    public void fly() {
//        System.out.println("Flying with wings");  // DUPLICATE again!
//    }
//}
//
//// ❌ Can't change behavior at runtime
//MallardDuck duck = new MallardDuck();
//// No way to change fly behavior if duck gets injured!
//```
//
//        ---
//
//        ### **Strategy Pattern:**
//
//        ```java
//// ✅ GOOD: Behavior encapsulated, reusable
//
//class FlyWithWings implements FlyBehavior {
//    public void fly() {
//        System.out.println("Flying with wings");  // Code ONCE
//    }
//}
//
//class MallardDuck extends Duck {
//    public MallardDuck() {
//        flyBehavior = new FlyWithWings();  // Reuse
//    }
//}
//
//class RedheadDuck extends Duck {
//    public RedheadDuck() {
//        flyBehavior = new FlyWithWings();  // Reuse same behavior!
//    }
//}
//
//// ✅ Can change behavior at runtime
//Duck duck = new MallardDuck();
//duck.performFly();  // Flying with wings
//
//duck.setFlyBehavior(new FlyNoWay());  // Change at runtime!
//        duck.performFly();  // Can't fly!
//```
//
//        ---
//
//        ## **Class Diagram**
//
//        ```
//Duck (Abstract)
//       ┌────────────┐
//               │ flyBehavior│◄──────────┐
//        │ quackBehavior│◄────────┐│
//        ├────────────┤          ││
//        │ performFly()│          ││
//        │performQuack()│         ││
//        │setFlyBehavior()│       ││
//        │setQuackBehavior()│     ││
//        └────────────┘          ││
//        △                 ││
//        │                 ││
//        ┌─────┴─────┐           ││
//        │           │           ││
//MallardDuck RubberDuck      ││
//DecoyDuck   RobotDuck       ││
//        ││
//        ┌──────────────┐            ││
//        │ FlyBehavior  │◄───────────┘│
//        ├──────────────┤             │
//        │   fly()      │             │
//        └──────────────┘             │
//        △                      │
//        │                      │
//        ┌─────┴─────┐                │
//        │           │                │
//FlyWithWings FlyNoWay           │
//FlyWithPropeller FlySlowly      │
//        │
//        ┌──────────────┐             │
//        │QuackBehavior │◄────────────┘
//        ├──────────────┤
//        │   quack()    │
//        └──────────────┘
//        △
//        │
//        ┌─────┴─────┐
//        │           │
//Quack    Squeak
//Beep   MuteQuack
//```
//
//        ---
//
//        ## **Key Design Principles**
//
//        ### **1. Favor Composition over Inheritance**
//
//        ```java
//// ❌ BAD: Inheritance
//class Duck {
//    void fly() { }  // All ducks inherit this
//}
//
//class RubberDuck extends Duck {
//    @Override
//    void fly() {
//        // Do nothing - but RubberDuck still HAS fly() method!
//    }
//}
//
//// ✅ GOOD: Composition
//class Duck {
//    FlyBehavior flyBehavior;  // HAS-A relationship
//
//    void performFly() {
//        flyBehavior.fly();  // Delegate
//    }
//}
//```
//
//        ---
//
//        ### **2. Program to Interface, Not Implementation**
//
//        ```java
//// ❌ BAD: Concrete class
//class Duck {
//    FlyWithWings flyBehavior = new FlyWithWings();  // Tied to concrete class
//}
//
//// ✅ GOOD: Interface
//class Duck {
//    FlyBehavior flyBehavior;  // Interface reference
//
//    // Can be ANY FlyBehavior implementation
//}
//```
//
//        ---
//
//        ### **3. Encapsulate What Varies**
//
//        ```java
//// What varies? → Flying and quacking behaviors
//// Solution? → Separate them into strategy classes
//
//// Instead of:
//class Duck {
//    void fly() {
//        // Behavior varies here ❌
//    }
//}
//
//// Do this:
//interface FlyBehavior {
//    void fly();
//}
//
//class Duck {
//    FlyBehavior flyBehavior;  // Encapsulated ✅
//}
//```
//
//        ---
//
//        ## **Adding New Behaviors (Easy!)**
//
//        ### **Add New Flying Behavior:**
//
//        ```java
//class FlyWithJetPack implements FlyBehavior {
//    @Override
//    public void fly() {
//        System.out.println("🚀 Flying with jet pack!");
//    }
//}
//
//// Use it
//Duck superDuck = new MallardDuck();
//superDuck.setFlyBehavior(new FlyWithJetPack());
//        superDuck.performFly();  // 🚀 Flying with jet pack!
//```
//
//        ### **Add New Duck Type:**
//
//        ```java
//class CyberDuck extends Duck {
//    public CyberDuck() {
//        flyBehavior = new FlyWithJetPack();  // New behavior
//        quackBehavior = new Beep();
//    }
//
//    @Override
//    public void display() {
//        System.out.println("🦾 I'm a Cyber Duck");
//    }
//}
//```
//
//No changes to existing code! ✅
//
//        ---
//
//        ## **Interview Talking Points**
//
//        ### **What to say:**
//
//        **Interviewer:** "Which pattern would you use?"
//
//        **You:**
//
//        > "I'd use the **Strategy Pattern** with composition. Here's why:
//        >
//        > **Problem:** Different ducks have different behaviors, and we need runtime flexibility.
//>
//        > **Solution:**
//        > 1. Create strategy interfaces: `FlyBehavior` and `QuackBehavior`
//        > 2. Implement concrete strategies: `FlyWithWings`, `FlyNoWay`, `Quack`, `Squeak`, etc.
//> 3. Duck class HAS-A behavior (composition, not inheritance)
//> 4. Delegate behavior to strategy objects
//>
//        > **Benefits:**
//        > - ✅ No code duplication (behaviors reused across ducks)
//> - ✅ Runtime behavior change (injured duck can switch to `FlySlowly`)
//> - ✅ Easy to add new behaviors without modifying existing code
//> - ✅ Follows OCP (Open/Closed Principle)
//>
//        > **Key principle:** Favor composition over inheritance. Instead of 'Duck IS-A Flyable', we use 'Duck HAS-A FlyBehavior'."
//
//        ---
//
//        ### **If asked about interfaces:**
//
//        **You:**
//
//        > "Using just interfaces would cause code duplication. If 10 ducks fly the same way, we'd reimplement the flying logic 10 times. Strategy Pattern solves this by encapsulating behaviors in separate classes that can be reused."
//
//        ---
//
//        ## **Summary**
//
//        ### **Pattern Used:**
//        ✅ **Strategy Pattern** (primary)
//
//        ### **Why Not Just Interfaces:**
//        - ❌ Code duplication
//- ❌ No runtime behavior change
//- ❌ Hard to maintain
//
//### **Key Advantages:**
//        - ✅ Behavior encapsulation
//- ✅ Runtime flexibility
//- ✅ Code reuse
//- ✅ Easy extensibility
//
//### **Design Principles Applied:**
//        1. Encapsulate what varies
//2. Favor composition over inheritance
//3. Program to interface, not implementation
//
//This is the **classic Strategy Pattern example** from the "Head First Design Patterns" book! 🎯
public class Question9 {
}
