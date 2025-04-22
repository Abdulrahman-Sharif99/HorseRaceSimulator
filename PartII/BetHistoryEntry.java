package PartII;

import PartI.Horse;

public class BetHistoryEntry {
    private final Horse horse;
    private final double amount;
    private final boolean won;
    private final double winnings;

    public BetHistoryEntry(Horse horse, double amount, boolean won, double winnings) {
        this.horse = horse;
        this.amount = amount;
        this.won = won;
        this.winnings = winnings;
    }

    public String toString() {
        if (won) {
            return horse.getName() + " - Bet: £" + String.format("%.2f", amount) +
                   " - WIN! Winnings: £" + String.format("%.2f", winnings);
        } else {
            return horse.getName() + " - Bet: £" + String.format("%.2f", amount) + " - LOST";
        }
    }
}
