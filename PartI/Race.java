package PartI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Race {
    private String weatherCondition = "Dry";
    private String trackShape = "Oval";
    private int raceLength;
    private List<Horse> lanes;

    public Race(int distance, int numberOfLanes) {
        if (distance <= 0 || numberOfLanes <= 0) {
            throw new IllegalArgumentException("Track length and lanes must be positive.");
        }
        this.raceLength = distance;
        lanes = new ArrayList<>(numberOfLanes);
        for (int i = 0; i < numberOfLanes; i++) {
            lanes.add(null);
        }
    }

    public void addHorse(Horse theHorse, int laneNumber) {
        if (theHorse == null || laneNumber < 1 || laneNumber > lanes.size()) {
            throw new IllegalArgumentException("Invalid horse or lane.");
        }
        if (lanes.contains(theHorse)) throw new IllegalArgumentException("Horse already in race.");
        if (lanes.get(laneNumber - 1) != null) throw new IllegalArgumentException("Lane occupied.");
        lanes.set(laneNumber - 1, theHorse);
    }

    public void startRace(Runnable onRaceProgress) {
        boolean finished = false;
    
        // Reset the race: move all horses back to the starting position
        for (Horse horse : lanes) {
            if (horse != null) {
                horse.goBackToStart();
            }
        }
        printRace();  // Initial race state
    
        while (!finished) {
            // Move each horse based on race conditions
            for (Horse horse : lanes) {
                if (horse != null) {
                    moveHorseWithConditions(horse);
                }
            }
    
            // Notify GUI to repaint after each progress update
            onRaceProgress.run();
    
            // Print race progress to console (for debugging purposes)
            printRace();
    
            // Check if any horse has won the race
            Horse winner = getWinner();
            if (winner != null) {
                System.out.println("And the winner is ... " + winner.getName());
                finished = true;
            }
    
            // Check if all horses have fallen
            if (allHorsesFallen()) {
                System.out.println("No Horse was able to finish the race!");
                finished = true;
            }
    
            try {
                // Sleep to simulate time between updates, controlling the speed of the race
                TimeUnit.MILLISECONDS.sleep(100);  // Adjust the sleep time as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        // After the race is finished, print the winner or a message about fallen horses
        Horse winner = getWinner();
        if (winner != null) {
            System.out.println("\n🏆 Winner: " + winner.getName() + "!");
        } else {
            System.out.println("\n😢 All horses have fallen. No winner.");
        }
    } 

    public void moveHorseWithConditions(Horse horse) {
        if (horse.hasFallen()) return;

        double fallChance = 0.1 * Math.pow(horse.getConfidence(), 2);
        double moveChance = horse.getConfidence();

        // Weather Modifiers
        if (weatherCondition.equalsIgnoreCase("Muddy")) {
            moveChance *= 0.7; fallChance *= 1.2;
        } else if (weatherCondition.equalsIgnoreCase("Icy")) {
            moveChance *= 0.6; fallChance *= 1.5;
        }

        // Track Shape Modifiers
        if (trackShape.equalsIgnoreCase("Figure-eight")) {
            moveChance *= 0.85;
        } else if (trackShape.equalsIgnoreCase("Custom")) {
            moveChance *= 0.8;
            fallChance *= 1.3;
        }

        if (Math.random() < moveChance) horse.moveForward();
        if (Math.random() < fallChance) horse.fall();
    }

    public Horse getWinner() {
        for (Horse horse : lanes) {
            if (horse != null && horse.getDistanceTravelled() >= raceLength && !horse.hasFallen()) {
                horse.increaseConfidence(0.2); 
                return horse;
            }
        }
        return null;
    }

    public boolean allHorsesFallen() {
        for (Horse horse : lanes) {
            if (horse != null && !horse.hasFallen()) return false;
        }
        return true;
    }

    public void printRace() {
        System.out.print("\n\n\n");
        // Use proper Unicode symbols for weather and track
        String weatherSymbol = "";
        switch(weatherCondition.toLowerCase()) {
            case "dry": weatherSymbol = "☀️"; break;
            case "muddy": weatherSymbol = "🌧️"; break;
            case "icy": weatherSymbol = "❄️"; break;
        }
        
        String trackSymbol = "";
        switch(trackShape.toLowerCase()) {
            case "oval": trackSymbol = "🟢"; break;
            case "figure-eight": trackSymbol = "∞"; break;
            case "custom": trackSymbol = "🔶"; break;
        }
        
        System.out.println(weatherSymbol + " Weather: " + weatherCondition + " | " + 
                          trackSymbol + " Track Shape: " + trackShape);
        System.out.println("=".repeat(raceLength + 10));
        
        for (Horse horse : lanes) {
            if (horse != null) printLane(horse);
        }
        System.out.println("=".repeat(raceLength + 10));
    }

    private void printLane(Horse horse) {
        int before = horse.getDistanceTravelled();
        int after = raceLength - before;
        
        // Use proper Unicode box-drawing characters
        System.out.print("│" + " ".repeat(before));
        
        // Display horse symbol with fall indicator
        if (horse.hasFallen()) {
            System.out.print("\u001B[31m✗\u001B[0m");  // Red X for fallen horses
        } else {
            System.out.print(horse.getSymbol());  // Normal horse symbol
        }
        
        System.out.print(" ".repeat(after) + "│ ");
        System.out.println(horse.getName() + " (Conf: " + String.format("%.2f", horse.getConfidence()) + ")");
    }

    public void setRaceLength(int length) {
        if (length > 0) this.raceLength = length;
    }

    public void setTrackShape(String shape) {
        this.trackShape = shape;
    }

    public void setWeatherCondition(String condition) {
        this.weatherCondition = condition;
    }

    public String getWeatherCondition() {
        return this.weatherCondition;
    }
    public String getTrackShape() {
        return this.trackShape;
    }

    public int getRaceLength(){
        return this.raceLength;
    }
    public List<Horse> getLanes() {
        return this.lanes;
    }

    public int getNumberOfHorses() {
        int count = 0;
        for (Horse horse : lanes) {
            if (horse != null) count++;
        }
        return count;
    }

    public Object getLeadingHorse() {
        Horse leadingHorse = null;
        for (Horse horse : lanes) {
            if (horse != null && (leadingHorse == null || horse.getDistanceTravelled() > leadingHorse.getDistanceTravelled())) {
                leadingHorse = horse;
            }
        }
        return leadingHorse;
    }

    public int getLaneCount() {
        return lanes.size(); 
    }
}