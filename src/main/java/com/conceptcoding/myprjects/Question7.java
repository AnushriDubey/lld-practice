package com.conceptcoding.myprjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * ## **🎮 Question 7: Game Character System**
 * **Scenario:**
 * RPG game with characters that have:
 * - Weapons (can be changed)
 * - Armor (can be changed)
 * - Special abilities (can be upgraded)
 * - Movement types (walk, fly, swim)
 * **Requirements:**
 * - Characters can change weapons at runtime
 * - Characters can learn new abilities
 * - Some characters can fly, some swim, some both
 * - Avoid deep inheritance hierarchies
 * **Question:** Which design pattern(s) would you use?
 * **/

//Recommended Pattern Combination:
//1. Strategy Pattern ✓ (You're right!)
//For movement types - since characters can have different movement behaviors:
//javainterface MovementStrategy {
//    void move();
//}
//
//class WalkMovement implements MovementStrategy {
//    public void move() { /* walking logic */ }
//}
//
//class FlyMovement implements MovementStrategy {
//    public void move() { /* flying logic */ }
//}
//
//class SwimMovement implements MovementStrategy {
//    public void move() { /* swimming logic */ }
//}
//2. Decorator Pattern
//For abilities - since characters can learn new abilities dynamically:
//javaabstract class Character {
//    abstract void useAbility();
//}
//
//abstract class AbilityDecorator extends Character {
//    protected Character character;
//}
//
//class FireballAbility extends AbilityDecorator {
//    // Adds fireball ability
//}
//3. Composition over Inheritance
//For weapons and armor - since they can be changed at runtime:
//javaclass GameCharacter {
//    private Weapon weapon;
//    private Armor armor;
//    private MovementStrategy movement;
//
//    public void setWeapon(Weapon w) { this.weapon = w; }
//    public void setArmor(Armor a) { this.armor = a; }
//}
//4. Optional: Observer Pattern
//For ability upgrades notifications or stat changes
//
//Why Multiple Patterns?

//FeaturePatternReasonMovement typesStrategyBehavior varies, needs runtime switchingAbilitiesDecoratorAdd/stack abilities dynamicallyWeapons/ArmorCompositionSimple swapping, no behavior change
//Would you like me to create a complete implementation showing all these patterns working together?Claude is AI and can make mistakes. Please double-check responses.
//🎮 Question 7: Game Character System**
//        **Scenario:**
//RPG game with characters that have:
//        - Weapons (can be changed)
//- Armor (can be changed)
//- Special abilities (can be upgraded)
//- Movement types (walk, fly, swim)
//**Requirements:**
//        - Characters can change weapons at runtime
//- Characters can learn new abilities
//- Some characters can fly, some swim, some both
//- Avoid deep inheritance hierarchies
//**Question:** Which design pattern(s) would you use?
//        for this low level design question I am thinking of using stratgey pattern to use these weapons, Armor, Movement types and for Special abilities decorator design pattren does this sunds good? in java I will write code
//
//Show less
//3:22 PM
//Your approach is excellent! You've correctly identified the right patterns. Let me refine it slightly and provide a complete implementation.
//
//Pattern Selection Analysis
//Your choices: ✅ Strategy Pattern - Perfect for weapons, armor, movement (interchangeable behaviors) ✅ Decorator Pattern - Good idea, but for abilities, I'd suggest Composite Pattern or simple list of abilities since:
//
//Decorator adds behavior by wrapping, creating many wrapper objects
//Abilities are more like a collection that can be added/removed
//Composite is better if abilities can be combined
//Alternative for abilities: Simple composition with an AbilityManager or list
//
//Let me show you both approaches:
//
//Complete Implementation
//java
// ============= STRATEGY PATTERN FOR WEAPONS =============

interface WeaponBehavior {
    void use();
    int getDamage();
}

class SwordWeapon implements WeaponBehavior {
    @Override
    public void use() {
        System.out.println("Swinging sword! Slash attack!");
    }

    @Override
    public int getDamage() {
        return 50;
    }
}

class BowWeapon implements WeaponBehavior {
    @Override
    public void use() {
        System.out.println("Shooting arrow! Ranged attack!");
    }

    @Override
    public int getDamage() {
        return 40;
    }
}

class MagicStaffWeapon implements WeaponBehavior {
    @Override
    public void use() {
        System.out.println("Casting spell! Magic attack!");
    }

    @Override
    public int getDamage() {
        return 60;
    }
}

class NoWeapon implements WeaponBehavior {
    @Override
    public void use() {
        System.out.println("Punching with fists!");
    }

    @Override
    public int getDamage() {
        return 10;
    }
}

// ============= STRATEGY PATTERN FOR ARMOR =============

interface ArmorBehavior {
    int getDefense();
    String getType();
}

class HeavyArmor implements ArmorBehavior {
    @Override
    public int getDefense() {
        return 80;
    }

    @Override
    public String getType() {
        return "Heavy Plate Armor";
    }
}

class LightArmor implements ArmorBehavior {
    @Override
    public int getDefense() {
        return 40;
    }

    @Override
    public String getType() {
        return "Light Leather Armor";
    }
}

class MagicRobe implements ArmorBehavior {
    @Override
    public int getDefense() {
        return 30;
    }

    @Override
    public String getType() {
        return "Magic Robe";
    }
}

// ============= STRATEGY PATTERN FOR MOVEMENT =============

interface MovementBehavior {
    void move();
}

class WalkMovement implements MovementBehavior {
    @Override
    public void move() {
        System.out.println("Walking on ground");
    }
}

class FlyMovement implements MovementBehavior {
    @Override
    public void move() {
        System.out.println("Flying through the air");
    }
}

class SwimMovement implements MovementBehavior {
    @Override
    public void move() {
        System.out.println("Swimming in water");
    }
}

// For characters that can do multiple movement types
class MultiMovement implements MovementBehavior {
    private List<MovementBehavior> movements;

    public MultiMovement(MovementBehavior... movements) {
        this.movements = new ArrayList<>(Arrays.asList(movements));
    }

    @Override
    public void move() {
        System.out.println("Can perform multiple movement types:");
        for (MovementBehavior movement : movements) {
            movement.move();
        }
    }

    public void addMovement(MovementBehavior movement) {
        movements.add(movement);
    }
}

// ============= ABILITIES (COMPOSITION APPROACH) =============

interface Ability {
    void use();
    String getName();
    int getManaCost();
    boolean canUpgrade();
    void upgrade();
}

class FireballAbility implements Ability {
    private int level = 1;

    @Override
    public void use() {
        System.out.println("Casting Fireball (Level " + level + ")! Damage: " + (50 * level));
    }

    @Override
    public String getName() {
        return "Fireball";
    }

    @Override
    public int getManaCost() {
        return 20 * level;
    }

    @Override
    public boolean canUpgrade() {
        return level < 5;
    }

    @Override
    public void upgrade() {
        if (canUpgrade()) {
            level++;
            System.out.println("Fireball upgraded to level " + level);
        }
    }
}

class HealAbility implements Ability {
    private int level = 1;

    @Override
    public void use() {
        System.out.println("Casting Heal (Level " + level + ")! Healing: " + (30 * level));
    }

    @Override
    public String getName() {
        return "Heal";
    }

    @Override
    public int getManaCost() {
        return 15 * level;
    }

    @Override
    public boolean canUpgrade() {
        return level < 5;
    }

    @Override
    public void upgrade() {
        if (canUpgrade()) {
            level++;
            System.out.println("Heal upgraded to level " + level);
        }
    }
}

class ShieldAbility implements Ability {
    private int level = 1;

    @Override
    public void use() {
        System.out.println("Activating Shield (Level " + level + ")! Defense boost: " + (20 * level));
    }

    @Override
    public String getName() {
        return "Shield";
    }

    @Override
    public int getManaCost() {
        return 10 * level;
    }

    @Override
    public boolean canUpgrade() {
        return level < 5;
    }

    @Override
    public void upgrade() {
        if (canUpgrade()) {
            level++;
            System.out.println("Shield upgraded to level " + level);
        }
    }
}

// ============= MAIN CHARACTER CLASS =============

abstract class GameCharacter {
    protected String name;
    protected int health;
    protected int mana;

    // Strategy pattern - behaviors can be changed at runtime
    protected WeaponBehavior weaponBehavior;
    protected ArmorBehavior armorBehavior;
    protected MovementBehavior movementBehavior;

    // Composition pattern - abilities can be added/removed
    protected List<Ability> abilities;

    public GameCharacter(String name) {
        this.name = name;
        this.health = 100;
        this.mana = 100;
        this.abilities = new ArrayList<>();
    }

    // Runtime weapon change
    public void setWeapon(WeaponBehavior weapon) {
        this.weaponBehavior = weapon;
        System.out.println(name + " equipped: " + weapon.getClass().getSimpleName());
    }

    // Runtime armor change
    public void setArmor(ArmorBehavior armor) {
        this.armorBehavior = armor;
        System.out.println(name + " wearing: " + armor.getType());
    }

    // Runtime movement change (e.g., learned to fly)
    public void setMovement(MovementBehavior movement) {
        this.movementBehavior = movement;
    }

    // Learn new ability
    public void learnAbility(Ability ability) {
        abilities.add(ability);
        System.out.println(name + " learned " + ability.getName());
    }

    // Upgrade ability
    public void upgradeAbility(String abilityName) {
        for (Ability ability : abilities) {
            if (ability.getName().equals(abilityName)) {
                ability.upgrade();
                return;
            }
        }
        System.out.println("Ability not found: " + abilityName);
    }

    // Use weapon
    public void attack() {
        if (weaponBehavior != null) {
            weaponBehavior.use();
        } else {
            System.out.println("No weapon equipped!");
        }
    }

    // Use movement
    public void performMove() {
        if (movementBehavior != null) {
            movementBehavior.move();
        } else {
            System.out.println("Cannot move!");
        }
    }

    // Use ability
    public void useAbility(String abilityName) {
        for (Ability ability : abilities) {
            if (ability.getName().equals(abilityName)) {
                if (mana >= ability.getManaCost()) {
                    ability.use();
                    mana -= ability.getManaCost();
                } else {
                    System.out.println("Not enough mana!");
                }
                return;
            }
        }
        System.out.println("Ability not found: " + abilityName);
    }

    public void displayStats() {
        System.out.println("\n=== " + name + " Stats ===");
        System.out.println("Health: " + health);
        System.out.println("Mana: " + mana);
        if (weaponBehavior != null) {
            System.out.println("Weapon Damage: " + weaponBehavior.getDamage());
        }
        if (armorBehavior != null) {
            System.out.println("Armor Defense: " + armorBehavior.getDefense());
        }
        System.out.println("Abilities: ");
        for (Ability ability : abilities) {
            System.out.println("  - " + ability.getName() + " (Mana: " + ability.getManaCost() + ")");
        }
    }

    // Template method - each character type implements this
    public abstract void specialMove();
}

// ============= CONCRETE CHARACTER TYPES =============

class Warrior extends GameCharacter {
    public Warrior(String name) {
        super(name);
        // Default setup
        setWeapon(new SwordWeapon());
        setArmor(new HeavyArmor());
        setMovement(new WalkMovement());
    }

    @Override
    public void specialMove() {
        System.out.println(name + " performs BERSERKER RAGE!");
    }
}

class Mage extends GameCharacter {
    public Mage(String name) {
        super(name);
        // Default setup
        setWeapon(new MagicStaffWeapon());
        setArmor(new MagicRobe());
        setMovement(new WalkMovement());
        // Mages start with fireball
        learnAbility(new FireballAbility());
    }

    @Override
    public void specialMove() {
        System.out.println(name + " performs ARCANE EXPLOSION!");
    }
}

class Dragon extends GameCharacter {
    public Dragon(String name) {
        super(name);
        // Dragons can fly and swim
        setMovement(new MultiMovement(new FlyMovement(), new SwimMovement(), new WalkMovement()));
        setWeapon(new NoWeapon()); // Uses claws
        setArmor(new HeavyArmor()); // Scales
        learnAbility(new FireballAbility());
    }

    @Override
    public void specialMove() {
        System.out.println(name + " breathes DRAGON FIRE!");
    }
}

// ============= DEMO =============

public class RPGGameDemo {
    public static void main(String[] args) {
        // Create a warrior
        GameCharacter warrior = new Warrior("Aragorn");
        warrior.displayStats();
        warrior.attack();
        warrior.performMove();

        System.out.println("\n--- Warrior changes weapon to Bow ---");
        warrior.setWeapon(new BowWeapon());
        warrior.attack();

        System.out.println("\n--- Warrior learns Heal ability ---");
        warrior.learnAbility(new HealAbility());
        warrior.useAbility("Heal");

        System.out.println("\n===================\n");

        // Create a mage
        GameCharacter mage = new Mage("Gandalf");
        mage.displayStats();
        mage.attack();
        mage.useAbility("Fireball");

        System.out.println("\n--- Mage upgrades Fireball ---");
        mage.upgradeAbility("Fireball");
        mage.useAbility("Fireball");

        System.out.println("\n--- Mage learns to fly ---");
        mage.setMovement(new FlyMovement());
        mage.performMove();

        System.out.println("\n===================\n");

        // Create a dragon
        GameCharacter dragon = new Dragon("Smaug");
        dragon.displayStats();
        dragon.performMove(); // Shows multiple movement types
        dragon.useAbility("Fireball");
        dragon.specialMove();

        System.out.println("\n--- Dragon learns Shield ---");
        dragon.learnAbility(new ShieldAbility());
        dragon.useAbility("Shield");
        dragon.upgradeAbility("Shield");

        dragon.displayStats();
    }
}
/*
* Why This Design Works
✅ Strategy Pattern for Equipment/Movement:

Change at runtime ✓
Easy to add new weapons/armor/movements ✓
No inheritance explosion ✓
        ✅ Composition for Abilities:

Add abilities dynamically ✓
Upgrade abilities ✓
No wrapper overhead ✓
Simpler than Decorator for this use case
        ✅ Avoids Deep Inheritance:

Only one level: GameCharacter → Warrior/Mage/Dragon
Behaviors are composed, not inherited
Decorator Alternative (if you prefer)
java
*
* */
// If you really want Decorator for abilities
abstract class CharacterDecorator extends GameCharacter {
    protected GameCharacter decoratedCharacter;

    public CharacterDecorator(GameCharacter character) {
        super(character.name);
        this.decoratedCharacter = character;
    }
}

class FireballDecorator extends CharacterDecorator {
    public FireballDecorator(GameCharacter character) {
        super(character);
    }

    @Override
    public void specialMove() {
        decoratedCharacter.specialMove();
        System.out.println("+ Fireball ability activated!");
    }
}
/**
 *
 * But I recommend composition because:
 *
 * Decorator creates wrapper chains
 * Harder to remove/upgrade abilities
 * More complex for this scenario
 */
