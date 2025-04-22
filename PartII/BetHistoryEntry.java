package PartII;

import PartI.*;
    
public class BetHistoryEntry {
    private final Horse horse;
    private final double amount;
    private final boolean won;
    private final double winnings;
    private final Race race; 

    public BetHistoryEntry(Horse horse, double amount, boolean won, double winnings, Race race) {
        this.horse = horse;
        this.amount = amount;
        this.won = won;
        this.winnings = winnings;
        this.race = race;
    }

    public String toString() {
        if (race.allHorsesFallen()) {
            return "All horses have fallen. No winners this time.\nYour bet has been refunded.";
        }
        if (won) {
            return "Horse: " + horse.getName() + " - Bet: £" + String.format("%.2f", amount) +
                    " - WIN! Winnings: £" + String.format("%.2f", winnings);
        } else {
            return "Horse: " + horse.getName() + " - Bet: £" + String.format("%.2f", amount) + " - LOST";
        }
    }
}
