package PartII;

import PartI.Horse;
import java.util.*;

public class Player {
    private double balance;
    private Map<Horse, Double> bets;
    private final List<BetHistoryEntry> betHistory;

    public static final double MIN_BET_AMOUNT = 5.0;

    public Player(double startingBalance) {
        this.balance = startingBalance;
        this.bets = new HashMap<>();
        this.betHistory = new ArrayList<>();
    }

    public double getBalance() {
        return balance;
    }

    public boolean placeBet(Horse horse, double amount) {
        if (amount < MIN_BET_AMOUNT) {
            return false;
        }

        if (amount <= balance) {
            balance -= amount;
            bets.put(horse, bets.getOrDefault(horse, 0.0) + amount);
            return true;
        }

        return false;
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
        return bets.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public void clearBets() {
        bets.clear();
    }

    public void recordBetResult(Horse horse, double amount, boolean won, double winnings) {
        betHistory.add(new BetHistoryEntry(horse, amount, won, winnings));
    }

    public List<BetHistoryEntry> getBetHistory() {
        return betHistory;
    }
}
