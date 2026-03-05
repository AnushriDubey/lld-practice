package com.conceptcoding.myprjects;

// ✅ Yes — 100% correct.
//This is a textbook **Composite Design Pattern** problem.
//
//You understood it properly 👍
//
//---
//
//# ✅ Correct Pattern: Composite
//
//This problem literally matches the definition of Composite:
//
//> Compose objects into tree structures to represent part-whole hierarchies.
//> Clients treat individual objects and compositions uniformly.
//
//---
//
//# 🧠 Mapping to Scenario
//
//| Pattern Role | Your System                   |
//| ------------ | ----------------------------- |
//| Component    | `Shape`                       |
//| Leaf         | `Circle`, `Rectangle`, `Line` |
//| Composite    | `Group`                       |
//| Client       | Drawing editor                |
//
//You need:
//
//* Recursive structure
//* Nested groups
//* Same `draw()` call on everything
//* Tree representation
//
//That is pure Composite.
//
//---
//
//# 🏗 Clean Design Structure
//
//## 1️⃣ Component Interface
//
//```java
//interface Shape {
//    void draw();
//}
//```
//
//---
//
//## 2️⃣ Leaf Classes
//
//### 🔵 Circle
//
//```java
//class Circle implements Shape {
//
//    @Override
//    public void draw() {
//        System.out.println("Drawing Circle");
//    }
//}
//```
//
//---
//
//### ▭ Rectangle
//
//```java
//class Rectangle implements Shape {
//
//    @Override
//    public void draw() {
//        System.out.println("Drawing Rectangle");
//    }
//}
//```
//
//---
//
//### ➖ Line
//
//```java
//class Line implements Shape {
//
//    @Override
//    public void draw() {
//        System.out.println("Drawing Line");
//    }
//}
//```
//
//---
//
//## 3️⃣ Composite Class (Group)
//
//This is where tree behavior comes.
//
//```java
//import java.util.ArrayList;
//import java.util.List;
//
//class Group implements Shape {
//
//    private List<Shape> shapes = new ArrayList<>();
//
//    public void addShape(Shape shape) {
//        shapes.add(shape);
//    }
//
//    public void removeShape(Shape shape) {
//        shapes.remove(shape);
//    }
//
//    @Override
//    public void draw() {
//        System.out.println("Drawing Group:");
//        for (Shape shape : shapes) {
//            shape.draw();   // Recursive call
//        }
//    }
//}
//```
//
//Notice:
//
//* `Group` implements `Shape`
//* It stores `List<Shape>`
//* `draw()` calls `draw()` recursively
//* Uniform treatment
//
//---
//
//# 🚀 Client Code Example
//
//```java
//public class Main {
//
//    public static void main(String[] args) {
//
//        Shape circle1 = new Circle();
//        Shape rectangle = new Rectangle();
//        Shape line = new Line();
//
//        Group group1 = new Group();
//        group1.addShape(rectangle);
//        group1.addShape(line);
//
//        Group group2 = new Group();
//        group2.addShape(new Circle());
//
//        Group nestedGroup = new Group();
//        nestedGroup.addShape(new Rectangle());
//
//        group2.addShape(nestedGroup);
//
//        Group drawing = new Group();
//        drawing.addShape(circle1);
//        drawing.addShape(group1);
//        drawing.addShape(group2);
//
//        drawing.draw();
//    }
//}
//```
//
//---
//
//# 🧠 Output Conceptually
//
//```
//Drawing Group:
//Drawing Circle
//Drawing Group:
//Drawing Rectangle
//Drawing Line
//Drawing Group:
//Drawing Circle
//Drawing Group:
//Drawing Rectangle
//```
//
//That is recursive composition.
//
//---
//
//# 🔥 Real-World Examples
//
//Composite is used in:
//
//* **Java SE** → `java.awt.Container`
//* **Spring Framework** → Bean definitions hierarchy
//* **React** → Component tree
//* File systems (File & Directory)
//
//---
//
//# 🧠 Advanced Interview Insight
//
//There are 2 ways to design Composite:
//
//### 1️⃣ Transparent Composite
//
//Put `add()` and `remove()` in `Shape` interface.
//Leaves throw exception.
//
//### 2️⃣ Safe Composite (Recommended)
//
//Only composite (`Group`) has `add/remove`.
//
//You described the **Safe Composite** — good choice.
//
//---
//
//# 🎯 Interview-Level Answer (Clean Version)
//
//> I would use the Composite pattern.
//> `Shape` would be the component interface with `draw()`.
//> `Circle`, `Rectangle`, and `Line` are leaf nodes.
//> `Group` is a composite that holds a list of `Shape` and implements recursive composition.
//> This allows treating individual shapes and groups uniformly.
//
//---
//
//You are recognizing patterns very quickly now 🔥
//You’ve correctly identified:
//
//* UI toolkit → Composite
//* Stock dashboard → Observer
//* Request pipeline → Chain of Responsibility
//
//If you want, next I can:
//
//* 🔥 Combine Composite + Visitor (graphics editor advanced version)
//* 🔥 Add Undo/Redo using Command pattern
//* 🔥 Make it thread-safe
//* 🔥 Show real-world system design variant
//
//What level do you want next? 😄
public class Question19 {
}
