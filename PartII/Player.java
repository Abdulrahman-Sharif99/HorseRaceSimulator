package PartII;

import PartI.Horse;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private double balance;
    private Map<Horse, Double> bets; // Keeps track of which horse the player bet on and how much

    public static final double MIN_BET_AMOUNT = 5.0;

    public Player(double startingBalance) {
        this.balance = startingBalance;
        this.bets = new HashMap<>();
    }

    public double getBalance() {
        return balance;
    }

    public boolean placeBet(Horse horse, double amount) {
        if (amount < MIN_BET_AMOUNT) {
            return false; // Below minimum
        }

        if (amount <= balance) {
            balance -= amount;
            bets.put(horse, bets.getOrDefault(horse, 0.0) + amount);
            return true;
        }

        return false; // Insufficient balance
    }

    public void addWinnings(double amount) {
        this.balance += amount;
    }

    public Map<Horse, Double> getBets() {
        return bets;
    }

    public void resetBets() {
        bets.clear();
    }

    public String getBetSummary() {
        if (bets.isEmpty()) return "No bets placed.";
        StringBuilder sb = new StringBuilder("Bets:\n");
        for (Map.Entry<Horse, Double> entry : bets.entrySet()) {
            sb.append("- ").append(entry.getKey().getName())
              .append(": £").append(String.format("%.2f", entry.getValue()))
              .append("\n");
        }
        return sb.toString();
    }

    public boolean hasBetOn(Horse horse) {
        return bets.containsKey(horse);
    }

    public double getBetAmountOn(Horse horse) {
        return bets.getOrDefault(horse, 0.0);
    }

    public boolean hasPlacedAnyBet() {
        return !bets.isEmpty();
    }

    public double getTotalBetAmount() {
        return bets.values().stream().mapToInt(Double::intValue).sum();
    }

    public void clearBets() {
        bets.clear();
    }
}