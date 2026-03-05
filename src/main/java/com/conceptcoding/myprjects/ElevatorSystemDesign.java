package com.conceptcoding.myprjects;

// 1. Simple enums
enum Direction {
    UP, DOWN, IDLE
}

enum ElevatorState {
    IDLE, MOVING, STOPPED, DOOR_OPEN, DOOR_CLOSED
}

enum DoorStatus {
    OPEN, CLOSED
}

// 2. Request - represents a call for elevator
class Request {
    private int floor;
    private Direction direction;
    
    Request(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
    }
    
    int getFloor() { return floor; }
    Direction getDirection() { return direction; }
}

// 3. Elevator - manages single elevator
class Elevator {
    private String id;
    private int currentFloor;
    private Direction currentDirection;
    private ElevatorState state;
    private DoorStatus doorStatus;
    private Queue<Integer> destinationQueue;  // Where it needs to go
    private int capacity;
    private int currentLoad;
    
    Elevator(String id, int capacity) {
        this.id = id;
        this.currentFloor = 0;
        this.currentDirection = Direction.IDLE;
        this.state = ElevatorState.IDLE;
        this.doorStatus = DoorStatus.CLOSED;
        this.destinationQueue = new PriorityQueue<>();
        this.capacity = capacity;
        this.currentLoad = 0;
    }
    
    // Add destination (from inside elevator)
    void addDestination(int floor) {
        if (floor == currentFloor) {
            openDoor();
            return;
        }
        destinationQueue.offer(floor);
        if (state == ElevatorState.IDLE) {
            move();
        }
    }
    
    void move() {
        if (destinationQueue.isEmpty()) {
            state = ElevatorState.IDLE;
            currentDirection = Direction.IDLE;
            return;
        }
        
        state = ElevatorState.MOVING;
        int nextFloor = destinationQueue.peek();
        
        // Determine direction
        if (nextFloor > currentFloor) {
            currentDirection = Direction.UP;
            moveUp();
        } else {
            currentDirection = Direction.DOWN;
            moveDown();
        }
    }
    
    private void moveUp() {
        currentFloor++;
        System.out.println("Elevator " + id + " moving to floor " + currentFloor);
        
        if (currentFloor == destinationQueue.peek()) {
            arriveAtFloor();
        } else {
            // Simulate movement delay
            move();
        }
    }
    
    private void moveDown() {
        currentFloor--;
        System.out.println("Elevator " + id + " moving to floor " + currentFloor);
        
        if (currentFloor == destinationQueue.peek()) {
            arriveAtFloor();
        } else {
            move();
        }
    }
    
    private void arriveAtFloor() {
        destinationQueue.poll();
        stop();
        openDoor();
    }
    
    void stop() {
        state = ElevatorState.STOPPED;
        System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
    }
    
    void openDoor() {
        doorStatus = DoorStatus.OPEN;
        state = ElevatorState.DOOR_OPEN;
        System.out.println("Elevator " + id + " door opened");
        
        // Auto-close after delay (in real system)
        // Timer to call closeDoor()
    }
    
    void closeDoor() {
        doorStatus = DoorStatus.CLOSED;
        state = ElevatorState.DOOR_CLOSED;
        System.out.println("Elevator " + id + " door closed");
        move();  // Continue to next destination
    }
    
    boolean canTakeRequest(int floor, Direction direction) {
        // Check if elevator is moving in same direction
        if (currentDirection == direction) {
            if (direction == Direction.UP && floor > currentFloor) {
                return true;
            }
            if (direction == Direction.DOWN && floor < currentFloor) {
                return true;
            }
        }
        return state == ElevatorState.IDLE;
    }
    
    int getCurrentFloor() { return currentFloor; }
    Direction getCurrentDirection() { return currentDirection; }
    ElevatorState getState() { return state; }
    boolean isFull() { return currentLoad >= capacity; }
}

// 4. Calling Panel (outside elevator on each floor)
class CallingPanel {
    private int floor;
    private ElevatorController controller;
    
    CallingPanel(int floor, ElevatorController controller) {
        this.floor = floor;
        this.controller = controller;
    }
    
    void pressUpButton() {
        System.out.println("Up button pressed on floor " + floor);
        controller.requestElevator(new Request(floor, Direction.UP));
    }
    
    void pressDownButton() {
        System.out.println("Down button pressed on floor " + floor);
        controller.requestElevator(new Request(floor, Direction.DOWN));
    }
}

// 5. Elevator Panel (inside elevator)
class ElevatorPanel {
    private Elevator elevator;
    
    ElevatorPanel(Elevator elevator) {
        this.elevator = elevator;
    }
    
    void pressFloorButton(int floor) {
        System.out.println("Floor button " + floor + " pressed");
        elevator.addDestination(floor);
    }
    
    void pressOpenButton() {
        elevator.openDoor();
    }
    
    void pressCloseButton() {
        elevator.closeDoor();
    }
}

// 6. Elevator Controller - assigns requests to elevators
class ElevatorController {
    private List<Elevator> elevators;
    private ElevatorSelectionStrategy strategy;
    
    ElevatorController(List<Elevator> elevators, ElevatorSelectionStrategy strategy) {
        this.elevators = elevators;
        this.strategy = strategy;
    }
    
    void requestElevator(Request request) {
        Elevator selectedElevator = strategy.selectElevator(elevators, request);
        if (selectedElevator != null) {
            selectedElevator.addDestination(request.getFloor());
        } else {
            System.out.println("No elevator available");
        }
    }
}

// 7. Strategy for selecting elevator
interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, Request request);
}

class NearestElevatorStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        Elevator nearest = null;
        int minDistance = Integer.MAX_VALUE;
        
        for (Elevator elevator : elevators) {
            if (elevator.canTakeRequest(request.getFloor(), request.getDirection()) 
                && !elevator.isFull()) {
                
                int distance = Math.abs(elevator.getCurrentFloor() - request.getFloor());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = elevator;
                }
            }
        }
        
        return nearest;
    }
}

// 8. Building - puts it all together
class Building {
    private List<Elevator> elevators;
    private List<CallingPanel> callingPanels;
    private ElevatorController controller;
    
    Building(int numElevators, int numFloors) {
        elevators = new ArrayList<>();
        callingPanels = new ArrayList<>();
        
        // Create elevators
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator("E" + (i+1), 10));
        }
        
        // Create controller
        controller = new ElevatorController(elevators, new NearestElevatorStrategy());
        
        // Create calling panels for each floor
        for (int i = 0; i < numFloors; i++) {
            callingPanels.add(new CallingPanel(i, controller));
        }
    }
    
    CallingPanel getCallingPanel(int floor) {
        return callingPanels.get(floor);
    }
}

// 9. Main
class Main {
    public static void main(String[] args) {
        Building building = new Building(3, 10);  // 3 elevators, 10 floors
        
        // Someone on floor 0 wants to go up
        building.getCallingPanel(0).pressUpButton();
        
        // Someone on floor 5 wants to go down
        building.getCallingPanel(5).pressDownButton();
    }
}