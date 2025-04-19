package PartII;

import PartI.*;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BettingTab extends JPanel {
    private JComboBox<String> horseComboBox;
    private JTextField betAmountField;
    private JLabel oddsLabel;
    private JButton placeBetButton;
    private Map<Horse, List<RaceResult>> horseStatsMap;
    private Race race;
    private double totalBetAmount = 0;
    private Horse selectedHorseForBet;

    public BettingTab() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Place Your Bet"));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        horseComboBox = new JComboBox<>();
        betAmountField = new JTextField();
        oddsLabel = new JLabel("Odds: N/A");
        placeBetButton = new JButton("Place Bet");

        formPanel.add(new JLabel("Select Horse:"));
        formPanel.add(horseComboBox);
        formPanel.add(new JLabel("Bet Amount (£):"));
        formPanel.add(betAmountField);
        formPanel.add(new JLabel("Odds:"));
        formPanel.add(oddsLabel);
        formPanel.add(new JLabel(""));
        formPanel.add(placeBetButton);

        add(formPanel, BorderLayout.CENTER);

        // Update odds when a horse is selected
        horseComboBox.addActionListener(e -> updateOdds());

        // Handle bet placement
        placeBetButton.addActionListener(e -> {
            if (selectedHorseForBet == null) {
                JOptionPane.showMessageDialog(this, "Please select a horse.");
                return;
            }
            try {
                double betAmount = Double.parseDouble(betAmountField.getText());
                if (betAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Bet amount must be positive.");
                    return;
                }
                totalBetAmount += betAmount;
                JOptionPane.showMessageDialog(this, "Bet placed on " + selectedHorseForBet.getName() + " for £" + betAmount);
                betAmountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid bet amount. Please enter a valid number.");
            }
        });
    }

    public void setRace(Race race) {
        this.race = race;
        updateHorses();
    }

    public void setHorseStatsMap(Map<Horse, List<RaceResult>> horseStatsMap) {
        this.horseStatsMap = horseStatsMap;
    }

    private void updateHorses() {
        horseComboBox.removeAllItems();
        if (race != null && horseStatsMap != null) {
            for (Horse horse : race.getLanes()) {
                if (horse != null) {
                    horseComboBox.addItem(horse.getName());
                }
            }
        }
    }

    private void updateOdds() {
        String selectedHorseName = (String) horseComboBox.getSelectedItem();
        if (selectedHorseName != null && horseStatsMap != null) {
            for (Map.Entry<Horse, List<RaceResult>> entry : horseStatsMap.entrySet()) {
                Horse horse = entry.getKey();
                if (horse.getName().equals(selectedHorseName)) {
                    double odds = calculateOdds(horse, entry.getValue());
                    oddsLabel.setText("Odds: 1:" + String.format("%.2f", odds));
                    selectedHorseForBet = horse;
                    break;
                }
            }
        }
    }

    private double calculateOdds(Horse horse, List<RaceResult> results) {
        if (results.isEmpty()) return 5.0;
        long wins = results.stream().filter(RaceResult::isWin).count();
        double winPercentage = 100.0 * wins / results.size();
        double confidence = horse.getConfidence();
        // Lower odds mean higher chances of winning
        double odds = 100.0 / (winPercentage + confidence * 10);
        return Math.max(odds, 1.1); // Ensure minimum odds of 1.1
    }

    public boolean hasPlacedBet() {
        return totalBetAmount > 0;
    }

    public double getTotalBetAmount() {
        return totalBetAmount;
    }

    public Horse getSelectedHorseForBet() {
        return selectedHorseForBet;
    }
}