//## **🎮 Question 6: Vehicle Manufacturing**
//**Scenario:**
//Manufacturing plant produces vehicles:
//- Cars (economy, luxury, sports)
//- Bikes (standard, racing)
//- Trucks (light, heavy)
//Each type has different:
//- Engines
//- Wheels
//- Interiors
//- Electronics
//**Requirements:**
//- Create complete vehicle with all compatible parts
//- Ensure parts are compatible (can't put car engine in bike)
//- Easy to add new vehicle types
//**Question:** Which design pattern(s) would you use?
//
//
//
//
//
//
//
//
//
//
//
//
//
//I need to correct your solution! Let me evaluate what you proposed:
//❌ Your Solution Analysis
//What you suggested:
//
//Strategy Pattern for engines/parts
//Builder Pattern to create objects
//Enum for vehicle types
//
//Issues with Your Approach:
//1. Strategy Pattern - INCORRECT ❌
//
//Strategy is for selecting algorithms/behaviors at runtime
//Examples: Different payment methods, sorting strategies, compression algorithms
//NOT for creating families of related objects
//Strategy doesn't ensure part compatibility
//
//2. Builder Pattern - PARTIALLY CORRECT ⚠️
//
//Builder is good for step-by-step construction
//But it doesn't solve the compatibility problem
//Builder alone won't prevent putting a car engine in a bike
//
//3. Enum - OK ✅
//
//Fine as a supporting element
//
//
//✅ CORRECT ANSWER: Abstract Factory Pattern
//This is a textbook Abstract Factory Pattern scenario! Here's why:
//Key Requirements Point to Abstract Factory:
//
//✅ "Create families of related products" (Engine + Wheels + Interior + Electronics)
//✅ "Ensure parts are compatible" (car parts only with cars)
//✅ "Easy to add new vehicle types" (add new factory)
//
//Why Not Strategy?
//Strategy Pattern is for:
//❌ Selecting payment method (CreditCard, PayPal, Crypto)
//❌ Choosing sort algorithm (QuickSort, MergeSort)
//❌ Compression strategy (ZIP, RAR, GZIP)
//
//NOT for creating compatible object families!
//Let me implement the correct solution using Abstract Factory (with optional Builder):Vehicle Manufacturing - Abstract Factory PatternCode // ======================== PRODUCT INTERFACES ========================
///**
// * Abstract products - define interfaces for each part type
// */
//interface Engine {
//    String getSpecifications();
//    int getHorsepower();
//}
//
//interface Wheels {
//    String g🎯 Summary: Correct vs. Your Solution
//❌ Your Proposed Solution:
//Strategy + Builder + Enum
//└─ Strategy for engines/parts ❌ WRONG USE
//└─ Builder for assembly ⚠️ SECONDARY
//└─ Enum for types ✅ OK
//✅ CORRECT Solution:
//Abstract Factory (Primary) + Builder (Optional) + Enum
//└─ Abstract Factory creates compatible part families ✅
//└─ Builder assembles the vehicle ✅
//└─ Enum for vehicle types ✅
//🔑 Key Differences:
//AspectStrategy PatternAbstract FactoryPurposeSelect algorithm at runtimeCreate families of related objectsUse CasePayment methods, sortingVehicle parts, UI themes, databasesCompatibilityDoesn't ensureAutomatically ensuresExampleChoose compression (ZIP/RAR)Create compatible GUI (Windows/Mac)
//📊 When to Use Each:
//Strategy Pattern:
//java// Select behavior at runtime
//PaymentStrategy strategy = new CreditCardPayment();
//checkout.processPayment(strategy);
//Abstract Factory Pattern:
//java// Create compatible family of products
//VehicleFactory factory = new CarFactory();
//Engine engine = factory.createEngine();      // Car engine
//Wheels wheels = factory.createWheels();      // Car wheels (4)
//// All parts are guaranteed compatible!
//✅ Why Abstract Factory Wins:
//
//Automatic Compatibility - CarFactory can ONLY create car parts
//No Mix-ups - Impossible to put bike engine in car
//Easy Extension - Add ElectricCarFactory without touching existing code
//Family Consistency - All parts from same family
//
//Your Builder suggestion is good as a secondary pattern to assemble the vehicle, but Abstract Factory must be the primary pattern to ensure part compatibility! 🎯Claude is AI and can make mistakes. Please double-check responses.
//
//
//
//
//
//
//
//
//
//
//
//
//
//// ======================== PRODUCT INTERFACES ========================
///**
// * Abstract products - define interfaces for each part type
// */
//interface Engine {
//    String getSpecifications();
//    int getHorsepower();
//}
//
//interface Wheels {
//    String getType();
//    int getCount();
//}
//
//interface Interior {
//    String getDescription();
//    int getSeats();
//}
//
//interface Electronics {
//    String getFeatures();
//}
//
//// ======================== VEHICLE ENUM ========================
//enum VehicleType {
//    ECONOMY_CAR, LUXURY_CAR, SPORTS_CAR,
//    STANDARD_BIKE, RACING_BIKE,
//    LIGHT_TRUCK, HEAVY_TRUCK
//}
//
//// ======================== CAR PRODUCTS ========================
//class EconomyCarEngine implements Engine {
//    @Override
//    public String getSpecifications() {
//        return "1.6L 4-Cylinder Economy Engine";
//    }
//
//    @Override
//    public int getHorsepower() {
//        return 120;
//    }
//}
//
//class LuxuryCarEngine implements Engine {
//    @Override
//    public String getSpecifications() {
//        return "3.0L V6 Luxury Engine";
//    }
//
//    @Override
//    public int getHorsepower() {
//        return 280;
//    }
//}
//
//class SportsCarEngine implements Engine {
//    @Override
//    public String getSpecifications() {
//        return "5.0L V8 Twin-Turbo Sports Engine";
//    }
//
//    @Override
//    public int getHorsepower() {
//        return 500;
//    }
//}
//
//class CarWheels implements Wheels {
//    private String size;
//
//    public CarWheels(String size) {
//        this.size = size;
//    }
//
//    @Override
//    public String getType() {
//        return size + " Alloy Car Wheels";
//    }
//
//    @Override
//    public int getCount() {
//        return 4;
//    }
//}
//
//class CarInterior implements Interior {
//    private String type;
//
//    public CarInterior(String type) {
//        this.type = type;
//    }
//
//    @Override
//    public String getDescription() {
//        return type + " Car Interior";
//    }
//
//    @Override
//    public int getSeats() {
//        return 5;
//    }
//}
//
//class CarElectronics implements Electronics {
//    private String features;
//
//    public CarElectronics(String features) {
//        this.features = features;
//    }
//
//    @Override
//    public String getFeatures() {
//        return features;
//    }
//}
//
//// ======================== BIKE PRODUCTS ========================
//class StandardBikeEngine implements Engine {
//    @Override
//    public String getSpecifications() {
//        return "500cc Single Cylinder Engine";
//    }
//
//    @Override
//    public int getHorsepower() {
//        return 45;
//    }
//}
//
//class RacingBikeEngine implements Engine {
//    @Override
//    public String getSpecifications() {
//        return "1000cc Inline-4 Racing Engine";
//    }
//
//    @Override
//    public int getHorsepower() {
//        return 200;
//    }
//}
//
//class BikeWheels implements Wheels {
//    @Override
//    public String getType() {
//        return "Sport Bike Wheels";
//    }
//
//    @Override
//    public int getCount() {
//        return 2;
//    }
//}
//
//class BikeInterior implements Interior {
//    @Override
//    public String getDescription() {
//        return "Racing Seat with Tank Grip";
//    }
//
//    @Override
//    public int getSeats() {
//        return 2;
//    }
//}
//
//class BikeElectronics implements Electronics {
//    private boolean isRacing;
//
//    public BikeElectronics(boolean isRacing) {
//        this.isRacing = isRacing;
//    }
//
//    @Override
//    public String getFeatures() {
//        return isRacing ? "Digital Dashboard, Traction Control, ABS, Quick Shifter"
//                        : "Basic Speedometer, ABS";
//    }
//}
//
//// ======================== TRUCK PRODUCTS ========================
//class LightTruckEngine implements Engine {
//    @Override
//    public String getSpecifications() {
//        return "2.5L Diesel Light Truck Engine";
//    }
//
//    @Override
//    public int getHorsepower() {
//        return 150;
//    }
//}
//
//class HeavyTruckEngine implements Engine {
//    @Override
//    public String getSpecifications() {
//        return "6.7L Diesel Heavy Duty Engine";
//    }
//
//    @Override
//    public int getHorsepower() {
//        return 400;
//    }
//}
//
//class TruckWheels implements Wheels {
//    private String type;
//
//    public TruckWheels(String type) {
//        this.type = type;
//    }
//
//    @Override
//    public String getType() {
//        return type + " Truck Wheels";
//    }
//
//    @Override
//    public int getCount() {
//        return 6;
//    }
//}
//
//class TruckInterior implements Interior {
//    @Override
//    public String getDescription() {
//        return "Durable Utility Interior";
//    }
//
//    @Override
//    public int getSeats() {
//        return 3;
//    }
//}
//
//class TruckElectronics implements Electronics {
//    @Override
//    public String getFeatures() {
//        return "GPS, Backup Camera, Load Monitor, Air Brake System";
//    }
//}
//
//// ======================== ABSTRACT FACTORY ========================
///**
// * Abstract Factory - defines interface for creating family of products
// */
//interface VehicleFactory {
//    Engine createEngine();
//    Wheels createWheels();
//    Interior createInterior();
//    Electronics createElectronics();
//    String getVehicleType();
//}
//
//// ======================== CONCRETE FACTORIES ========================
//class EconomyCarFactory implements VehicleFactory {
//    @Override
//    public Engine createEngine() {
//        return new EconomyCarEngine();
//    }
//
//    @Override
//    public Wheels createWheels() {
//        return new CarWheels("15-inch");
//    }
//
//    @Override
//    public Interior createInterior() {
//        return new CarInterior("Cloth");
//    }
//
//    @Override
//    public Electronics createElectronics() {
//        return new CarElectronics("Basic Radio, Manual AC");
//    }
//
//    @Override
//    public String getVehicleType() {
//        return "Economy Car";
//    }
//}
//
//class LuxuryCarFactory implements VehicleFactory {
//    @Override
//    public Engine createEngine() {
//        return new LuxuryCarEngine();
//    }
//
//    @Override
//    public Wheels createWheels() {
//        return new CarWheels("18-inch");
//    }
//
//    @Override
//    public Interior createInterior() {
//        return new CarInterior("Leather with Heated Seats");
//    }
//
//    @Override
//    public Electronics createElectronics() {
//        return new CarElectronics("Premium Sound, Auto Climate, Navigation, 360 Camera");
//    }
//
//    @Override
//    public String getVehicleType() {
//        return "Luxury Car";
//    }
//}
//
//class SportsCarFactory implements VehicleFactory {
//    @Override
//    public Engine createEngine() {
//        return new SportsCarEngine();
//    }
//
//    @Override
//    public Wheels createWheels() {
//        return new CarWheels("20-inch Performance");
//    }
//
//    @Override
//    public Interior createInterior() {
//        return new CarInterior("Carbon Fiber Racing Seats");
//    }
//
//    @Override
//    public Electronics createElectronics() {
//        return new CarElectronics("Digital Cockpit, Performance Monitor, Launch Control");
//    }
//
//    @Override
//    public String getVehicleType() {
//        return "Sports Car";
//    }
//}
//
//class StandardBikeFactory implements VehicleFactory {
//    @Override
//    public Engine createEngine() {
//        return new StandardBikeEngine();
//    }
//
//    @Override
//    public Wheels createWheels() {
//        return new BikeWheels();
//    }
//
//    @Override
//    public Interior createInterior() {
//        return new BikeInterior();
//    }
//
//    @Override
//    public Electronics createElectronics() {
//        return new BikeElectronics(false);
//    }
//
//    @Override
//    public String getVehicleType() {
//        return "Standard Bike";
//    }
//}
//
//class RacingBikeFactory implements VehicleFactory {
//    @Override
//    public Engine createEngine() {
//        return new RacingBikeEngine();
//    }
//
//    @Override
//    public Wheels createWheels() {
//        return new BikeWheels();
//    }
//
//    @Override
//    public Interior createInterior() {
//        return new BikeInterior();
//    }
//
//    @Override
//    public Electronics createElectronics() {
//        return new BikeElectronics(true);
//    }
//
//    @Override
//    public String getVehicleType() {
//        return "Racing Bike";
//    }
//}
//
//class LightTruckFactory implements VehicleFactory {
//    @Override
//    public Engine createEngine() {
//        return new LightTruckEngine();
//    }
//
//    @Override
//    public Wheels createWheels() {
//        return new TruckWheels("Light Duty");
//    }
//
//    @Override
//    public Interior createInterior() {
//        return new TruckInterior();
//    }
//
//    @Override
//    public Electronics createElectronics() {
//        return new TruckElectronics();
//    }
//
//    @Override
//    public String getVehicleType() {
//        return "Light Truck";
//    }
//}
//
//class HeavyTruckFactory implements VehicleFactory {
//    @Override
//    public Engine createEngine() {
//        return new HeavyTruckEngine();
//    }
//
//    @Override
//    public Wheels createWheels() {
//        return new TruckWheels("Heavy Duty");
//    }
//
//    @Override
//    public Interior createInterior() {
//        return new TruckInterior();
//    }
//
//    @Override
//    public Electronics createElectronics() {
//        return new TruckElectronics();
//    }
//
//    @Override
//    public String getVehicleType() {
//        return "Heavy Truck";
//    }
//}
//
//// ======================== VEHICLE (PRODUCT) ========================
//class Vehicle {
//    private Engine engine;
//    private Wheels wheels;
//    private Interior interior;
//    private Electronics electronics;
//    private String type;
//
//    public Vehicle(Engine engine, Wheels wheels, Interior interior,
//                   Electronics electronics, String type) {
//        this.engine = engine;
//        this.wheels = wheels;
//        this.interior = interior;
//        this.electronics = electronics;
//        this.type = type;
//    }
//
//    public void showSpecifications() {
//        System.out.println("\n" + "=".repeat(60));
//        System.out.println("🚗 " + type.toUpperCase() + " SPECIFICATIONS");
//        System.out.println("=".repeat(60));
//        System.out.println("🔧 ENGINE: " + engine.getSpecifications());
//        System.out.println("   Power: " + engine.getHorsepower() + " HP");
//        System.out.println("\n🛞 WHEELS: " + wheels.getType());
//        System.out.println("   Count: " + wheels.getCount());
//        System.out.println("\n🪑 INTERIOR: " + interior.getDescription());
//        System.out.println("   Seats: " + interior.getSeats());
//        System.out.println("\n📱 ELECTRONICS: " + electronics.getFeatures());
//        System.out.println("=".repeat(60));
//    }
//}
//
//// ======================== BUILDER (OPTIONAL) ========================
///**
// * Builder Pattern - Assembles vehicle from factory-created parts
// * This is SECONDARY to Abstract Factory
// */
//class VehicleBuilder {
//    private VehicleFactory factory;
//
//    public VehicleBuilder(VehicleFactory factory) {
//        this.factory = factory;
//    }
//
//    public Vehicle build() {
//        // Factory ensures all parts are compatible
//        Engine engine = factory.createEngine();
//        Wheels wheels = factory.createWheels();
//        Interior interior = factory.createInterior();
//        Electronics electronics = factory.createElectronics();
//
//        return new Vehicle(engine, wheels, interior, electronics,
//                          factory.getVehicleType());
//    }
//}
//
//// ======================== FACTORY PROVIDER ========================
//class VehicleFactoryProvider {
//    public static VehicleFactory getFactory(VehicleType type) {
//        switch (type) {
//            case ECONOMY_CAR: return new EconomyCarFactory();
//            case LUXURY_CAR: return new LuxuryCarFactory();
//            case SPORTS_CAR: return new SportsCarFactory();
//            case STANDARD_BIKE: return new StandardBikeFactory();
//            case RACING_BIKE: return new RacingBikeFactory();
//            case LIGHT_TRUCK: return new LightTruckFactory();
//            case HEAVY_TRUCK: return new HeavyTruckFactory();
//            default: throw new IllegalArgumentException("Unknown vehicle type");
//        }
//    }
//}
//
//// ======================== DEMO ========================
//public class VehicleManufacturingDemo {
//    public static void main(String[] args) {
//        System.out.println("🏭 VEHICLE MANUFACTURING PLANT 🏭\n");
//
//        // Manufacture different vehicles
//        System.out.println("--- Manufacturing Economy Car ---");
//        VehicleFactory factory1 = VehicleFactoryProvider.getFactory(VehicleType.ECONOMY_CAR);
//        Vehicle economyCar = new VehicleBuilder(factory1).build();
//        economyCar.showSpecifications();
//
//        System.out.println("\n--- Manufacturing Luxury Car ---");
//        VehicleFactory factory2 = VehicleFactoryProvider.getFactory(VehicleType.LUXURY_CAR);
//        Vehicle luxuryCar = new VehicleBuilder(factory2).build();
//        luxuryCar.showSpecifications();
//
//        System.out.println("\n--- Manufacturing Sports Car ---");
//        VehicleFactory factory3 = VehicleFactoryProvider.getFactory(VehicleType.SPORTS_CAR);
//        Vehicle sportsCar = new VehicleBuilder(factory3).build();
//        sportsCar.showSpecifications();
//
//        System.out.println("\n--- Manufacturing Racing Bike ---");
//        VehicleFactory factory4 = VehicleFactoryProvider.getFactory(VehicleType.RACING_BIKE);
//        Vehicle racingBike = new VehicleBuilder(factory4).build();
//        racingBike.showSpecifications();
//
//        System.out.println("\n--- Manufacturing Heavy Truck ---");
//        VehicleFactory factory5 = VehicleFactoryProvider.getFactory(VehicleType.HEAVY_TRUCK);
//        Vehicle heavyTruck = new VehicleBuilder(factory5).build();
//        heavyTruck.showSpecifications();
//
//        // Show benefits
//        System.out.println("\n\n" + "=".repeat(60));
//        System.out.println("✅ ABSTRACT FACTORY BENEFITS:");
//        System.out.println("=".repeat(60));
//        System.out.println("1. ✓ Parts are AUTOMATICALLY compatible");
//        System.out.println("   (CarFactory only creates car parts)");
//        System.out.println("\n2. ✓ Easy to add new vehicle types");
//        System.out.println("   (Just create new factory class)");
//        System.out.println("\n3. ✓ Client code doesn't know concrete classes");
//        System.out.println("   (Works with interfaces only)");
//        System.out.println("\n4. ✓ Consistent product families");
//        System.out.println("   (All parts match vehicle type)");
//        System.out.println("=".repeat(60));
//
//        System.out.println("\n" + "=".repeat(60));
//        System.out.println("❌ WHY NOT STRATEGY PATTERN?");
//        System.out.println("=".repeat(60));
//        System.out.println("Strategy is for selecting algorithms/behaviors:");
//        System.out.println("  • Payment methods (Credit, PayPal, Crypto)");
//        System.out.println("  • Sorting algorithms (Quick, Merge, Bubble)");
//        System.out.println("  • Compression (ZIP, RAR, GZIP)");
//        System.out.println("\nNOT for creating compatible object families!");
//        System.out.println("=".repeat(60));
//    }
//}