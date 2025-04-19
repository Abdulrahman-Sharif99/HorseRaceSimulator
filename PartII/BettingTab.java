package PartII;

import PartI.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BettingTab extends JPanel {

    private JComboBox<String> horseComboBox;
    private JTextField betAmountField;
    private JLabel oddsLabel;
    private JButton placeBetButton;

    private Map<Horse, List<RaceResult>> horseStatsMap;
    private Race currentRace;
    private Horse selectedHorse;

    private double totalBetAmount = 0;

    public BettingTab() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Place Your Bet"));
        initializeUI();
        attachEventListeners();
    }

    public void setRace(Race race) {
        this.currentRace = race;
        populateHorseComboBox();
    }

    public void setHorseStatsMap(Map<Horse, List<RaceResult>> statsMap) {
        this.horseStatsMap = statsMap;
        populateHorseComboBox();
    }

    public boolean hasPlacedBet() {
        return totalBetAmount > 0;
    }

    public double getTotalBetAmount() {
        return totalBetAmount;
    }

    public Horse getSelectedHorseForBet() {
        return selectedHorse;
    }

    private void initializeUI() {
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
    }

    private void attachEventListeners() {
        horseComboBox.addActionListener(e -> updateOdds());

        placeBetButton.addActionListener(e -> {
            if (selectedHorse == null) {
                showMessage("Please select a horse.");
                return;
            }

            String input = betAmountField.getText().trim();
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    showMessage("Bet amount must be positive.");
                    return;
                }
                totalBetAmount += amount;
                showMessage("Bet placed on " + selectedHorse.getName() + " for £" + amount);
                betAmountField.setText("");
            } catch (NumberFormatException ex) {
                showMessage("Invalid bet amount. Please enter a valid number.");
            }
        });
    }

    private void populateHorseComboBox() {
        horseComboBox.removeAllItems();
        selectedHorse = null;
        oddsLabel.setText("Odds: N/A");

        if (currentRace == null || horseStatsMap == null) return;

        for (Horse horse : currentRace.getLanes()) {
            if (horse != null && horseStatsMap.containsKey(horse)) {
                horseComboBox.addItem(horse.getName());
            }
        }
    }

    private void updateOdds() {
        String selectedName = (String) horseComboBox.getSelectedItem();
        if (selectedName == null || horseStatsMap == null) return;

        for (Horse horse : horseStatsMap.keySet()) {
            if (horse.getName().equals(selectedName)) {
                selectedHorse = horse;
                List<RaceResult> results = horseStatsMap.getOrDefault(horse, List.of());
                double odds = calculateOdds(horse, results);
                oddsLabel.setText("Odds: 1:" + String.format("%.2f", odds));
                break;
            }
        }
    }

    private double calculateOdds(Horse horse, List<RaceResult> results) {
        if (results == null || results.isEmpty()) return 5.0;

        long wins = results.stream().filter(RaceResult::isWin).count();
        double winRate = (double) wins / results.size();
        double confidence = horse.getConfidence();

        double odds = 1.0 / (winRate + (confidence / 10.0));
        return Math.max(odds * 10.0, 1.1); // Adjusted scaling for better display
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
