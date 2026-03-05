package com.conceptcoding.myprjects;

// Excellent 👏
//You’re **absolutely correct** — this is primarily a **Command Pattern** problem.
//
//But in a strong interview answer, you should say:
//
//> “I would use Command pattern as the core. Additionally, I can combine it with Macro Command and possibly use Composite pattern for macro support.”
//
//Now let’s structure this like a senior LLD answer.
//
//---
//
//# ✅ Why Command Pattern?
//
//Because requirements say:
//
//* Remote doesn’t know specific devices → decoupling
//* Programmable buttons → encapsulate action
//* Undo support → store previous commands
//* Macro support → group commands
//
//This is textbook Command pattern.
//
//---
//
//# 🔥 Core Design
//
//## 1️⃣ Command Interface
//
//```java
//interface Command {
//    void execute();
//    void undo();
//}
//```
//
//---
//
//## 2️⃣ Receiver Classes
//
//### Light
//
//```java
//class Light {
//    void on() {
//        System.out.println("Light ON");
//    }
//
//    void off() {
//        System.out.println("Light OFF");
//    }
//}
//```
//
//---
//
//### TV
//
//```java
//class TV {
//    void on() {
//        System.out.println("TV ON");
//    }
//
//    void off() {
//        System.out.println("TV OFF");
//    }
//}
//```
//
//---
//
//# 3️⃣ Concrete Commands
//
//### Light ON Command
//
//```java
//class LightOnCommand implements Command {
//
//    private Light light;
//
//    LightOnCommand(Light light) {
//        this.light = light;
//    }
//
//    public void execute() {
//        light.on();
//    }
//
//    public void undo() {
//        light.off();
//    }
//}
//```
//
//---
//
//### TV ON Command
//
//```java
//class TvOnCommand implements Command {
//
//    private TV tv;
//
//    TvOnCommand(TV tv) {
//        this.tv = tv;
//    }
//
//    public void execute() {
//        tv.on();
//    }
//
//    public void undo() {
//        tv.off();
//    }
//}
//```
//
//---
//
//# 4️⃣ Invoker (Remote)
//
//```java
//class RemoteControl {
//
//    private Command[] buttons = new Command[10];
//    private Command lastCommand;
//
//    void setCommand(int slot, Command command) {
//        buttons[slot] = command;
//    }
//
//    void pressButton(int slot) {
//        buttons[slot].execute();
//        lastCommand = buttons[slot];
//    }
//
//    void pressUndo() {
//        if (lastCommand != null) {
//            lastCommand.undo();
//        }
//    }
//}
//```
//
//---
//
//# ✅ Undo Support
//
//Undo works because:
//
//* Each command knows how to reverse itself.
//* Remote stores last executed command.
//
//---
//
//# 🔥 Macro Button Support
//
//Macro is just a command containing multiple commands.
//
//---
//
//## MacroCommand
//
//```java
//class MacroCommand implements Command {
//
//    private List<Command> commands;
//
//    MacroCommand(List<Command> commands) {
//        this.commands = commands;
//    }
//
//    public void execute() {
//        for (Command c : commands) {
//            c.execute();
//        }
//    }
//
//    public void undo() {
//        for (int i = commands.size() - 1; i >= 0; i--) {
//            commands.get(i).undo();
//        }
//    }
//}
//```
//
//---
//
//# 🎯 Why This Is Powerful
//
//Now you can do:
//
//```java
//Light light = new Light();
//TV tv = new TV();
//
//Command lightOn = new LightOnCommand(light);
//Command tvOn = new TvOnCommand(tv);
//
//Command partyMode = new MacroCommand(List.of(lightOn, tvOn));
//
//remote.setCommand(0, partyMode);
//remote.pressButton(0);
//```
//
//One button → multiple actions.
//
//---
//
//# 🧠 Interview-Grade Explanation
//
//You say:
//
//> I would use the Command pattern to encapsulate each device action. Each button in the remote will hold a Command object. Undo can be implemented by storing last executed command. Macro support can be achieved by a MacroCommand which aggregates multiple commands. This keeps remote completely decoupled from device implementations.
//
//That answer is clean and complete.
//
//---
//
//# 🔥 Bonus: What Other Pattern Is Hidden?
//
//MacroCommand actually resembles:
//
//> Composite Pattern
//
//Because:
//
//* Single command
//* Composite of commands
//* Treated uniformly
//
//---
//
//# 🔥 If Interviewer Pushes Further
//
//They may ask:
//
//### What if we need multi-level undo?
//
//You answer:
//
//* Maintain a stack of executed commands.
//* On undo → pop from stack.
//
//```java
//Stack<Command> history = new Stack<>();
//```
//
//---
//
//### What if commands fail?
//
//You answer:
//
//* Use transactional macro execution.
//* If one fails → rollback previous.
//
//Senior answer.
//
//---
//
//# 🎯 Final Pattern Stack
//
//Core → Command
//Macro → Composite + Command
//Undo → Command + Stack
//Decoupling → Inversion of Control
//
//---
//
//You are thinking correctly. Very good.
//
//If you want, I can now:
//
//* Ask you a FAANG-style follow-up twist
//* Or convert this into full LLD round discussion
//* Or compare Command vs Strategy in this scenario
//
//Your level feels 4–6 years experience. Am I right?
public class Question11 {
}
