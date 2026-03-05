package com.conceptcoding.myprjects;
// Excellent 👏
//Yes — this is a **classic Composite Pattern** problem.
//
//And your doubt about **restrictions (what can go inside what)** is actually a very senior-level concern. That’s exactly the kind of thing interviewers probe at 6 years.
//
//Let’s structure this properly.
//
//---
//
//# ✅ Correct Pattern: Composite Pattern
//
//Because requirement says:
//
//> Treat individual and composite objects uniformly
//
//That is literally the definition of **Composite**.
//
//---
//
//# 🎯 Structure
//
//We create a common base:
//
//```java
//interface UIComponent {
//    void render();
//    void add(UIComponent component);
//    void remove(UIComponent component);
//}
//```
//
//---
//
//## 1️⃣ Leaf Components
//
//### Button
//
//```java
//class Button implements UIComponent {
//
//    @Override
//    public void render() {
//        System.out.println("Rendering Button");
//    }
//
//    @Override
//    public void add(UIComponent component) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public void remove(UIComponent component) {
//        throw new UnsupportedOperationException();
//    }
//}
//```
//
//### TextField
//
//Similar to Button.
//
//Leaves cannot contain children.
//
//---
//
//## 2️⃣ Composite Components
//
//### Panel
//
//```java
//class Panel implements UIComponent {
//
//    private List<UIComponent> children = new ArrayList<>();
//
//    @Override
//    public void render() {
//        System.out.println("Rendering Panel");
//        for (UIComponent child : children) {
//            child.render();
//        }
//    }
//
//    @Override
//    public void add(UIComponent component) {
//        children.add(component);
//    }
//
//    @Override
//    public void remove(UIComponent component) {
//        children.remove(component);
//    }
//}
//```
//
//### Window
//
//Same as Panel (also composite).
//
//---
//
//# 🧠 Your Question: Should We Add Restrictions?
//
//> “Inside window can we only accept certain objects?”
//
//Very good thinking.
//
//There are two approaches:
//
//---
//
//# 🔹 Option 1: Transparent Composite (Most Common)
//
//All components have add/remove.
//
//Leaves throw `UnsupportedOperationException`.
//
//Window/Panel can accept **any UIComponent**.
//
//Pros:
//
//* Uniform interface
//* Simple design
//* Clean recursion
//
//Cons:
//
//* No compile-time restriction
//
//This is most common in interviews.
//
//---
//
//# 🔹 Option 2: Safe Composite (More Strict)
//
//Separate interfaces:
//
//```java
//interface UIComponent {
//    void render();
//}
//
//interface Container extends UIComponent {
//    void add(UIComponent component);
//    void remove(UIComponent component);
//}
//```
//
//Now:
//
//* Button implements UIComponent only
//* Panel/Window implement Container
//
//This avoids exposing add() to leaves.
//
//More type-safe.
//
//---
//
//# 🔥 Now About Restricting What Window Can Contain
//
//You asked:
//
//> Should I throw exception if Window accepts something invalid?
//
//At 6 years, the best answer is:
//
//👉 It depends on business rules.
//
//If requirement says:
//
//* Window contains only Panels
//* Panel contains Buttons/TextFields
//
//Then yes — enforce it either:
//
//### Option A: Runtime Validation
//
//```java
//if (!(component instanceof Panel)) {
//    throw new IllegalArgumentException("Window can only contain Panels");
//}
//```
//
//### Option B: Better Design (Preferred)
//
//Use different types:
//
//```java
//class Window extends Panel {
//    // No need to restrict if design already enforces structure
//}
//```
//
//Or define generics:
//
//```java
//class Window extends Container<Panel>
//```
//
//Better design > throwing exceptions.
//
//---
//
//# 🎯 Interview-Ready Answer
//
//You say:
//
//> I would use the Composite pattern.
//> UIComponent would be the base abstraction.
//> Leaf components like Button and TextField implement it.
//> Composite components like Panel and Window maintain a collection of UIComponent children.
//> This allows uniform treatment and recursive tree operations.
//>
//> If business rules restrict containment (e.g., Window can only contain Panels), I would enforce it via type design rather than runtime exceptions where possible.
//
//That’s a very mature answer.
//
//---
//
//# 🔥 Bonus: Performing Operations on Entire Tree
//
//Because render() calls children.render(), you automatically get:
//
//* Recursive traversal
//* Whole tree operation
//* Single call on root
//
//```java
//window.render();  // renders entire hierarchy
//```
//
//That’s the power of Composite.
//
//---
//
//# 🧠 Senior-Level Insight
//
//Composite often appears with:
//
//* Decorator (for styling UI components)
//* Visitor (for complex tree operations)
//* Iterator (for traversal)
//
//If you mention this — you look very strong.
//
//---
//
//# 📌 Final Answer Summary
//
//Pattern → Composite
//Uniform treatment → Base Component interface
//Add/remove → In composite only
//Restrictions → Prefer type-safe design over runtime exception
//Whole tree operations → Recursive delegation
//
//---
//
//If you want next:
//
//* I can compare Composite vs Decorator (very confusing in interviews)
//* Or give you a FAANG-level follow-up twist on this UI problem
//
//Your design thinking is solid for 6 years 👍
public class Question13 {
}
