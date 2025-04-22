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
    private JLabel balanceLabel;
    private JButton placeBetButton;

    private Map<Horse, List<RaceResult>> horseStatsMap;
    private Race race;
    private Horse selectedHorseForBet;
    private Player player =  new Player(1000.0);

    private boolean BetPlaced = false;
    private Runnable onBetPlaced;

    private DefaultListModel<String> historyListModel;
    private JList<String> historyList;


    public BettingTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Place Your Bet"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        horseComboBox = new JComboBox<>();
        betAmountField = new JTextField(10);
        oddsLabel = new JLabel("Odds: N/A");
        balanceLabel = new JLabel("Balance: £" + String.format("%.2f", player.getBalance()));
        placeBetButton = new JButton("Place Bet");
        topPanel.add(balanceLabel);
        formPanel.add(new JLabel("Select Horse:"));
        formPanel.add(horseComboBox);
        formPanel.add(new JLabel("Bet Amount (£):"));
        formPanel.add(betAmountField);
        formPanel.add(oddsLabel);
        formPanel.add(placeBetButton);
        add(formPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        JScrollPane historyScrollPane = new JScrollPane(historyList);
        historyScrollPane.setPreferredSize(new Dimension(250, 0));
        historyScrollPane.setBorder(BorderFactory.createTitledBorder("Betting History"));
        add(historyScrollPane, BorderLayout.EAST);

        horseComboBox.addActionListener(e -> updateOdds());

        placeBetButton.addActionListener(e -> {
            if (selectedHorseForBet == null) {
                JOptionPane.showMessageDialog(this, "Please select a horse.");
                return;
            }

            try {
                double betAmount = Double.parseDouble(betAmountField.getText());

                if (betAmount < Player.MIN_BET_AMOUNT && betAmount > 0) {
                    JOptionPane.showMessageDialog(this, "Minimum bet is £5.00.");
                    return;
                } else if (betAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Bet amount must be greater than zero.");
                    return;
                }

                if (player == null) {
                    JOptionPane.showMessageDialog(this, "Player not initialized.");
                    return;
                }

                boolean success = player.placeBet(selectedHorseForBet, betAmount);
                if (success) {
                    String formatted = String.format("%.2f", betAmount);
                    JOptionPane.showMessageDialog(this, "Bet placed on " + selectedHorseForBet.getName() + " for £" + formatted);
                    updateBalanceDisplay();
                    betAmountField.setText("");
                    BetPlaced = true;
                    if (onBetPlaced != null) {
                        onBetPlaced.run();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance to place this bet.");
                }

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

    public void setPlayer(Player player) {
        this.player = player;
        updateBalanceDisplay();
    }

    private void updateBalanceDisplay() {
        if (player != null) {
            balanceLabel.setText("Balance: £" + String.format("%.2f", player.getBalance()));
        }
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
        updateOdds();
    }

    private double updateOdds() {
        String selectedName = (String) horseComboBox.getSelectedItem();
        if (selectedName != null && horseStatsMap != null) {
            for (Map.Entry<Horse, List<RaceResult>> entry : horseStatsMap.entrySet()) {
                if (entry.getKey().getName().equals(selectedName)) {
                    selectedHorseForBet = entry.getKey();
                    double odds = calculateOdds(selectedHorseForBet, entry.getValue());
                    oddsLabel.setText("Odds: 1:" + String.format("%.2f", odds));
                    return odds;
                }
            }
        }
        oddsLabel.setText("Odds: N/A");
        selectedHorseForBet = null;
        return 0.0;
    }

    private double calculateOdds(Horse horse, List<RaceResult> results) {
        if (results == null || results.isEmpty()) return 2.0;
        long wins = results.stream().filter(RaceResult::isWin).count();
        double winPercentage = 100.0 * wins / results.size();
        double confidence = horse.getConfidence();
        double odds = 100.0 / (winPercentage + confidence * 10);
        return Math.max(odds, 1.1);
    }

    public boolean hasPlacedBet() {
        return player != null && player.hasPlacedAnyBet();
    }

    public Player getPlayer() {
        return player;
    }

    public Horse getSelectedHorseForBet() {
        return selectedHorseForBet;
    }

    public boolean isBetPlaced() {
        return BetPlaced;
    }

    public double getTotalBetAmount() {
        return player != null ? player.getBets().values().stream().mapToDouble(Double::doubleValue).sum() : 0.0;
    }

    public void refreshHorseList() {
        horseComboBox.removeAllItems();
        if (race != null) {
            for (Horse h : race.getLanes()) {
                if (h != null) {
                    horseComboBox.addItem(h.getName());
                }
            }
        }
    }

    public void notifyRaceFinished(Horse winner, Race race) {
        if (selectedHorseForBet == null) return;
        Double betAmount = player.getBets().get(selectedHorseForBet);
        if (race.allHorsesFallen()) {
            player.addWinnings(betAmount);
            player.recordBetResult(selectedHorseForBet, betAmount, false, 0.0, race);
            historyListModel.addElement(player.getBetHistory().get(player.getBetHistory().size() - 1).toString());
        
            JOptionPane.showMessageDialog(this,
                    "All horses have fallen. No winners this time.\nYour bet has been refunded.",
                    "DNF", JOptionPane.INFORMATION_MESSAGE);
            player.clearBets();
            updateBalanceDisplay();
            return;
        }
        
        if (selectedHorseForBet.equals(winner)) {
            double odds = updateOdds();
            double winnings = betAmount * odds;
            player.addWinnings(winnings);
            player.recordBetResult(selectedHorseForBet, betAmount, true, winnings, race);
            historyListModel.addElement(player.getBetHistory().get(player.getBetHistory().size() - 1).toString());
        
            JOptionPane.showMessageDialog(this,
                    "🎉 Congratulations! Your horse " + winner.getName() + " won!\n" +
                    "You won £" + String.format("%.2f", winnings) + "!",
                    "You Win!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            player.recordBetResult(selectedHorseForBet, betAmount, false, 0.0, race);
            historyListModel.addElement(player.getBetHistory().get(player.getBetHistory().size() - 1).toString());
        
            JOptionPane.showMessageDialog(this,
                    "😞 Sorry, your horse " + selectedHorseForBet.getName() + " lost.\n" +
                    "Better luck next time!",
                    "You Lose", JOptionPane.INFORMATION_MESSAGE);
        }
        
    
        player.clearBets();
        updateBalanceDisplay();
    }
    

    public void setOnBetPlaced(Runnable onBetPlaced) {
        this.onBetPlaced = onBetPlaced;
    }
}