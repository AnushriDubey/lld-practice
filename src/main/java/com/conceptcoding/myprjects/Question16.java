package com.conceptcoding.myprjects;

// Very good thinking 👏 — but this is **not** a Bridge problem.
//
//This question is almost a textbook example of the **State Pattern**.
//
//Let’s break it down carefully like a senior interviewer would.
//
//---
//
//# 🔎 Key Requirement
//
//> Behavior changes based on state
//> Clean state transitions
//> Each state has different capabilities
//
//That sentence alone screams:
//
//👉 **State Design Pattern**
//
//---
//
//# ❌ Why NOT Bridge?
//
//Bridge is used when:
//
//* You want to decouple abstraction from implementation
//* Both can vary independently
//
//Example:
//
//RemoteControl ↔ Device
//Shape ↔ DrawingAPI
//
//In subscription:
//
//* Tier is not an implementation detail
//* Tier changes behavior of the same object at runtime
//
//That is not Bridge. That is **State**.
//
//---
//
//# ✅ Correct Pattern: State Pattern
//
//Because:
//
//* User has current subscription tier
//* Behavior changes depending on tier
//* Transitions between tiers are allowed
//
//---
//
//# 🎯 Structure
//
//## 1️⃣ State Interface
//
//```java
//public interface SubscriptionState {
//
//    void watchVideo();
//    void downloadVideo();
//    void showAds();
//    void upgrade(User user);
//    void downgrade(User user);
//}
//```
//
//---
//
//## 2️⃣ Concrete States
//
//### 🆓 Free Tier
//
//```java
//public class FreeState implements SubscriptionState {
//
//    @Override
//    public void watchVideo() {
//        System.out.println("Watching in 480p with Ads");
//    }
//
//    @Override
//    public void downloadVideo() {
//        System.out.println("Download not available in Free tier");
//    }
//
//    @Override
//    public void showAds() {
//        System.out.println("Showing Ads...");
//    }
//
//    @Override
//    public void upgrade(User user) {
//        user.setState(new BasicState());
//        System.out.println("Upgraded to Basic Tier");
//    }
//
//    @Override
//    public void downgrade(User user) {
//        System.out.println("Already at lowest tier");
//    }
//}
//```
//
//---
//
//### 💎 Premium Tier
//
//```java
//public class PremiumState implements SubscriptionState {
//
//    @Override
//    public void watchVideo() {
//        System.out.println("Watching in 4K, No Ads");
//    }
//
//    @Override
//    public void downloadVideo() {
//        System.out.println("Download Available");
//    }
//
//    @Override
//    public void showAds() {
//        System.out.println("No Ads for Premium");
//    }
//
//    @Override
//    public void upgrade(User user) {
//        System.out.println("Already at highest tier");
//    }
//
//    @Override
//    public void downgrade(User user) {
//        user.setState(new BasicState());
//        System.out.println("Downgraded to Basic Tier");
//    }
//}
//```
//
//---
//
//## 3️⃣ Context (User)
//
//```java
//public class User {
//
//    private SubscriptionState state;
//
//    public User() {
//        this.state = new FreeState(); // default
//    }
//
//    public void setState(SubscriptionState state) {
//        this.state = state;
//    }
//
//    public void watchVideo() {
//        state.watchVideo();
//    }
//
//    public void downloadVideo() {
//        state.downloadVideo();
//    }
//
//    public void upgrade() {
//        state.upgrade(this);
//    }
//
//    public void downgrade() {
//        state.downgrade(this);
//    }
//}
//```
//
//---
//
//## 4️⃣ Usage
//
//```java
//public class Main {
//    public static void main(String[] args) {
//
//        User user = new User();
//
//        user.watchVideo();  // Free behavior
//        user.downloadVideo();
//
//        user.upgrade();     // Move to Basic
//        user.watchVideo();
//
//        user.upgrade();     // Move to Premium
//        user.watchVideo();
//
//        user.downgrade();   // Back to Basic
//    }
//}
//```
//
//---
//
//# 🔥 Why State is Perfect Here
//
//Without State, you'd write:
//
//```java
//if (tier == FREE) {
//   ...
//} else if (tier == PREMIUM) {
//   ...
//}
//```
//
//Problems:
//
//* Huge if-else chains
//* Hard to extend
//* Violates Open-Closed Principle
//
//State pattern:
//
//* Encapsulates behavior per tier
//* Clean transitions
//* Easy to add new tier (FamilyState)
//
//---
//
//# 🧠 Senior-Level Insight
//
//This problem is:
//
//> Behavioral change based on internal state
//
//That is the exact definition of State Pattern.
//
//---
//
//# 🎯 Interview-Level Answer
//
//You say:
//
//> I would use the State pattern. The subscription tier would be modeled as different state implementations. The User acts as the context and delegates behavior to the current state. Each tier encapsulates its own rules and allowed transitions, avoiding large conditional logic.
//
//That’s a clean senior answer.
//
//---
//
//# 🔥 Where Bridge Might Apply (Optional Insight)
//
//Bridge would be useful if:
//
//* Subscription tier AND streaming engine varied independently
//* Example: Tier ↔ VideoDeliveryStrategy
//
//But that’s a different problem.
//
//---
//
//# 📌 Pattern Summary
//
//| Requirement                              | Pattern           |
//| ---------------------------------------- | ----------------- |
//| Behavior changes dynamically             | State             |
//| Clean transitions                        | State             |
//| Avoid if-else                            | State             |
//| Independent abstraction & implementation | Bridge (NOT here) |
//
//---
//
//You’re thinking architecturally (which is good), but here the simpler and correct abstraction is State.
//
//If you want, I can now:
//
//* Compare State vs Strategy (very commonly confused)
//* Or give you a FAANG-style twist on this subscription system
//* Or show how this would be implemented in a real Spring Boot system
//
//Your pattern thinking is improving nicely 👍
public class Question16 {
}
