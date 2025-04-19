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

    public Gui() {
        setTitle("Race Track Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        AdjustRaceTab adjustRaceTab = new AdjustRaceTab();
        AddHorseTab addHorseTab = new AddHorseTab();
        statsTab = new StatsTab();
        bettingTab = new BettingTab();

        // Set up tabbed pane with larger minimum size
        tabbedPane.setMinimumSize(new Dimension(700, 500));
        
        adjustRaceTab.setOnRaceAdjusted((newRace, trackDetails) -> {
            this.race = newRace;
            race.setTrackShape(trackDetails[0]);
            race.setWeatherCondition(trackDetails[1]);
            addHorseTab.setRace(race);
            addHorseTab.setHorseStatsMap(horseStatsMap);
            statsTab.setHorseStatsMap(horseStatsMap);
            bettingTab.setRace(race);
            bettingTab.setHorseStatsMap(horseStatsMap);
            JOptionPane.showMessageDialog(this, "Race adjusted! You can now add horses.");
            statsTab.updateStats(race);
        });

        addHorseTab.setOnHorseAdded(() -> statsTab.updateStats(race));
        addHorseTab.setOnRaceStarted((racePanel, onFinished) -> {
            if (!bettingTab.hasPlacedBet()) {
                JOptionPane.showMessageDialog(this, "You must place a bet before starting the race!");
                return;
            }
            startRace(racePanel, onFinished, bettingTab.getSelectedHorseForBet(), bettingTab.getTotalBetAmount());
        });

        tabbedPane.addTab("Adjust Race", adjustRaceTab);
        tabbedPane.addTab("Add Horse", addHorseTab);
        tabbedPane.addTab("Stats", statsTab);
        tabbedPane.addTab("Betting", bettingTab);

        add(tabbedPane, BorderLayout.CENTER);
        setSize(750, 550);  // Increased size
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startRace(RacePanel racePanel, Consumer<Runnable> onFinished, Horse selectedHorse, double totalBetAmount) {
        if (isRaceInProgress) {
            JOptionPane.showMessageDialog(this, "A race is already in progress!");
            return;
        }

        isRaceInProgress = true;
        raceWindow = new JFrame("Race in Progress");
        raceWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        raceWindow.setLayout(new BorderLayout());
        raceWindow.add(racePanel, BorderLayout.CENTER);
        raceWindow.setSize(800, 600);
        raceWindow.setLocationRelativeTo(null);
        raceWindow.setVisible(true);

        onFinished.accept(() -> {
            isRaceInProgress = false;
            raceWindow.dispose();
            JOptionPane.showMessageDialog(this, "Race finished! Check the stats tab for results.");
            statsTab.updateStats(race);
        });
    }

    // ... rest of the Gui class remains unchanged ...
}


// RaceResult class
class RaceResult {
    private final double time;
    private final boolean win;
    private final boolean fall;
    private final double avgSpeed;
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
}