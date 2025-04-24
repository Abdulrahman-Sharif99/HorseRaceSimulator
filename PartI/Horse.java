package PartI;
public class Horse {
    private char symbol;
    private String name;
    private int distanceTravelled;
    private boolean fallen;
    private double confidence;
    private String coatColor; 
    private String breed; 

    public Horse(char horseSymbol, String horseName, double horseConfidence) {
        this.symbol = horseSymbol;
        this.name = horseName;
        setConfidence(horseConfidence); // Validation included
    }

    public void fall() {
        this.fallen = true;
        setConfidence(Math.max(confidence - 0.1, 0));
    }

    public double getConfidence() {
        return this.confidence;
    }

    public int getDistanceTravelled() {
        return this.distanceTravelled;
    }

    public String getName() {
        return this.name;
    }

    public char getSymbol() {
        return this.symbol;
    }

    public void goBackToStart() {
        this.distanceTravelled = 0;
        this.fallen = false;
    }

    public boolean hasFallen() {
        return this.fallen;
    }

    public void moveForward() {
        if (!fallen) this.distanceTravelled += 1;
    }

    public void setConfidence(double newConfidence) {
        this.confidence = Math.min(Math.max(newConfidence, 0), 1);
    }

    public void setSymbol(char newSymbol) {
        this.symbol = newSymbol;
    }

    public void increaseConfidence(double a) {
        setConfidence(confidence + a);
    }

    public void setCoatColor(String coatColor) {
        this.coatColor = coatColor;
    }

    public String getCoatColor() {
        return coatColor;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
    public String getBreed() {
        return breed;
    }
}

