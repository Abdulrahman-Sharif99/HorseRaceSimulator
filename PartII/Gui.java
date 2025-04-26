package PartII;

import PartI.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class Gui extends JFrame {
    private Race race;
    private final Map<Horse, List<RaceResult>> horseStatsMap = new HashMap<>();
    private boolean isRaceInProgress = false;
    private JFrame raceWindow;
    private StatsTab statsTab;
    private BettingTab bettingTab;
    private AddHorseTab addHorseTab;
    private String currentWeather;
    private String currenttrackshape;
    private int racelength = 0;

    public Gui() {
        setTitle("Race Track Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        addHorseTab = new AddHorseTab();
        AddHorseTab addHorseTab = new AddHorseTab();
        statsTab = new StatsTab();
        AdjustRaceTab adjustRaceTab = new AdjustRaceTab();
        bettingTab = new BettingTab();

        tabbedPane.setMinimumSize(new Dimension(700, 500));

        adjustRaceTab.setOnRaceAdjusted((newRace, trackDetails) -> {
            this.race = newRace;
            race.setTrackShape(trackDetails[0]);
            this.currenttrackshape = adjustRaceTab.getTrackShape();
            race.setWeatherCondition(trackDetails[1]);
            currentWeather = trackDetails[1];
            this.racelength = adjustRaceTab.getRacelength();
            addHorseTab.setWeatherCondition(currentWeather);
            addHorseTab.setRace(race);
            addHorseTab.setHorseStatsMap(horseStatsMap);
            statsTab.setHorseStatsMap(horseStatsMap);
            bettingTab.setRace(race);
            bettingTab.setHorseStatsMap(horseStatsMap);
            addHorseTab.setRaceAdjusted(true);
            JOptionPane.showMessageDialog(this, "Race adjusted! You can now add horses.");
            statsTab.updateStats(race);
        });
        
        addHorseTab.setOnHorseAdded(() -> {
            List<Horse> horses = addHorseTab.getHorses();
            for (Horse horse : horses) {
                switch (currentWeather) {
                    case "Stormy" -> horse.setConfidence(horse.getConfidence() - 0.2);
                    case "Rainy" -> horse.setConfidence(horse.getConfidence() - 0.1);
                    case "Foggy" -> horse.setConfidence(horse.getConfidence() - 0.05);
                    case "Sunny" -> horse.setConfidence(horse.getConfidence() + 0.1);
                    case "Dry" -> horse.setConfidence(horse.getConfidence() + 0.075);
                    case "Clear" -> horse.setConfidence(horse.getConfidence() + 0.05);
                    case "Muddy" -> horse.setConfidence(horse.getConfidence() - 0.1);
                    case "Icy" -> horse.setConfidence(horse.getConfidence() - 0.2);
                    default -> horse.setConfidence(horse.getConfidence() + 0.00);
                }
            }
            statsTab.updateStats(race);
            bettingTab.refreshHorseList(); 
        });

        bettingTab.setOnBetPlaced(() -> addHorseTab.setBetPlaced(true));

        
        addHorseTab.setOnRaceStarted((racePanel, onFinished) -> {
            if (!bettingTab.hasPlacedBet()) {
                JOptionPane.showMessageDialog(this, "You must place a bet before starting the race!");
                return;
            }
            else if(bettingTab.hasPlacedBet()){
                startRace(racePanel, onFinished, bettingTab.getSelectedHorseForBet(), bettingTab.getTotalBetAmount());
            }
        });

        tabbedPane.addTab("Adjust Race", adjustRaceTab);
        tabbedPane.addTab("Add Horse", addHorseTab);
        tabbedPane.addTab("Stats", statsTab);
        tabbedPane.addTab("Betting", bettingTab);

        add(tabbedPane, BorderLayout.CENTER);
        setSize(750, 550);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startRace(RacePanel racePanel, Consumer<Runnable> onFinished, Horse selectedHorse, double totalBetAmount){
        if (isRaceInProgress) {
            return;
        }
        racePanel.setTrackLength(racelength);
        racePanel.setTrackShape(currenttrackshape);
        isRaceInProgress = true;
        raceWindow = new JFrame("Race in Progress");
        raceWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        raceWindow.setLayout(new BorderLayout());
        raceWindow.add(racePanel, BorderLayout.CENTER);
        raceWindow.setSize(800, 600);
        raceWindow.setLocationRelativeTo(null);
        raceWindow.setVisible(true);

        List<Horse> horsesInRace = addHorseTab.getHorses();
        for (int i = 0; i < horsesInRace.size(); i++) {
            Horse horse = horsesInRace.get(i);
            if (horse != null) {
                try {
                    race.addHorse(horse, i + 1);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error adding horse: " + e.getMessage());
                }
            }
        }
    
        new Thread(() -> {
            try {
                race.startRace(() -> {
                    SwingUtilities.invokeLater(racePanel::repaint);
                });
    
                SwingUtilities.invokeLater(() -> {
                    isRaceInProgress = false;
                    raceWindow.dispose();

                    for (int lane = 0; lane < race.getLanes().size(); lane++) {
                        Horse horse = race.getLanes().get(lane);
                        if (horse != null) {
                                boolean hasWon = horse.getDistanceTravelled() >= race.getRaceLength() && !horse.hasFallen();
        
                                double baseSpeed = horse.getDistanceTravelled() / (double) race.getRaceLength();
                                double equipmentSpeed = addHorseTab.SpeedModifier(
                                    addHorseTab.getSaddleComboBox(),
                                    addHorseTab.getHorseshoeComboBox(),
                                    addHorseTab.getBreedComboBox()
                                );
        
                                double totalSpeed = baseSpeed + equipmentSpeed;
        
                                if ("Dry".equalsIgnoreCase(race.getWeatherCondition())) {
                                    totalSpeed += 0.1;
                                }
                                else if("Muddy".equalsIgnoreCase(race.getWeatherCondition())){
                                    totalSpeed -= 0.1;
                                }
                                else if("Icy".equalsIgnoreCase(race.getWeatherCondition())){
                                    totalSpeed -= 0.2;
                                }
                                else if("Foggy".equalsIgnoreCase(race.getWeatherCondition())){
                                    totalSpeed -= 0.05;
                                }
        
                                RaceResult result = new RaceResult(
                                    race.getRaceLength(),
                                    hasWon,
                                    horse.hasFallen(),
                                    totalSpeed,
                                    race.getTrackShape(),
                                    race.getWeatherCondition()
                                );
                            horseStatsMap.computeIfAbsent(horse, k -> new ArrayList<>()).add(result);
                        }
                    }
    
                    statsTab.updateStats(race);

                    String winnerName = race.getWinner() != null ? race.getWinner().getName() : "No winner (all horses fell)";
                    JOptionPane.showMessageDialog(this, "The winner of the race is .... " + winnerName + "!");
                    addHorseTab.setBetPlaced(false);

                    onFinished.accept(() -> {
                        addHorseTab.setBetPlaced(false);
                        bettingTab.notifyRaceFinished(race.getWinner(), race);
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::new);
    }
}

class RaceResult {
    private final double time;
    private final boolean win;
    private final boolean fall;
    private double avgSpeed;
    private final String trackShape;
    private final String weatherCondition;

    public RaceResult(double time, boolean win, boolean fall, double avgSpeed, String trackShape, String weatherCondition) {
        this.time = time;
        this.win = win;
        this.fall = fall;
        this.avgSpeed = avgSpeed;
        this.trackShape = trackShape;
        this.weatherCondition = weatherCondition;
    }

    public double getTime() {
        return time;
    }

    public boolean isWin() {
        return win;
    }

    public boolean isFall() {
        return fall;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public String getTrackShape() {
        return trackShape;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }
    public double setSpeed(double speed) {
        return speed;
    }
    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}