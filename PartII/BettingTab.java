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
    private Race race;
    private double totalBetAmount = 0;
    private Horse selectedHorseForBet;

    public BettingTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Place Your Bet"));

        // Panel for form inputs
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        horseComboBox = new JComboBox<>();
        betAmountField = new JTextField(10);
        oddsLabel = new JLabel("Odds: N/A");
        placeBetButton = new JButton("Place Bet");

        // Row 1: Horse selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Horse:"), gbc);
        gbc.gridx = 1;
        formPanel.add(horseComboBox, gbc);

        // Row 2: Bet amount
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Bet Amount (£):"), gbc);
        gbc.gridx = 1;
        formPanel.add(betAmountField, gbc);

        // Row 3: Odds
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Odds:"), gbc);
        gbc.gridx = 1;
        formPanel.add(oddsLabel, gbc);

        // Row 4: Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(placeBetButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Listeners
        horseComboBox.addActionListener(e -> updateOdds());

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
        updateHorseList();
    }

    public void setHorseStatsMap(Map<Horse, List<RaceResult>> horseStatsMap) {
        this.horseStatsMap = horseStatsMap;
        updateHorseList();
    }

    private void updateHorseList() {
        horseComboBox.removeAllItems();
        if (race != null && horseStatsMap != null) {
            for (Horse horse : race.getLanes()) {
                if (horse != null) {
                    horseComboBox.addItem(horse.getName());
                }
            }
        }
        updateOdds(); // Refresh odds after list is updated
    }

    private void updateOdds() {
        String selectedName = (String) horseComboBox.getSelectedItem();
        if (selectedName != null && horseStatsMap != null) {
            for (Map.Entry<Horse, List<RaceResult>> entry : horseStatsMap.entrySet()) {
                if (entry.getKey().getName().equals(selectedName)) {
                    selectedHorseForBet = entry.getKey();
                    double odds = calculateOdds(selectedHorseForBet, entry.getValue());
                    oddsLabel.setText("Odds: 1:" + String.format("%.2f", odds));
                    return;
                }
            }
        }
        oddsLabel.setText("Odds: N/A");
        selectedHorseForBet = null;
    }

    private double calculateOdds(Horse horse, List<RaceResult> results) {
        if (results == null || results.isEmpty()) return 5.0;
        long wins = results.stream().filter(RaceResult::isWin).count();
        double winPercentage = 100.0 * wins / results.size();
        double confidence = horse.getConfidence();
        double odds = 100.0 / (winPercentage + confidence * 10);
        return Math.max(odds, 1.1);
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
