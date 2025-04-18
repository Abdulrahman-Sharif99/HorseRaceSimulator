package PartII;

import PartI.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Gui extends JFrame {
    private Race race;
    private final Map<Horse, List<RaceResult>> horseStatsMap = new HashMap<>();
    private boolean isRaceInProgress = false;
    private JFrame raceWindow;

    public Gui() {
        setTitle("Race Track Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        AdjustRaceTab adjustRaceTab = new AdjustRaceTab();
        AddHorseTab addHorseTab = new AddHorseTab();
        StatsTab statsTab = new StatsTab();

        adjustRaceTab.setOnRaceAdjusted((newRace, trackDetails) -> {
            this.race = newRace;
            race.setTrackShape(trackDetails[0]);
            race.setWeatherCondition(trackDetails[1]);

            addHorseTab.setRace(race);
            addHorseTab.setHorseStatsMap(horseStatsMap);
            statsTab.setHorseStatsMap(horseStatsMap);

            JOptionPane.showMessageDialog(this, "Race adjusted! You can now add horses.");
            statsTab.updateStats(race);
        });

        addHorseTab.setOnHorseAdded(() -> statsTab.updateStats(race));

        addHorseTab.setOnRaceStarted((racePanel, onFinished) -> {
            if (isRaceInProgress) {
                JOptionPane.showMessageDialog(this, "Race is already in progress!");
                return;
            }
            
            if (raceWindow != null) {
                raceWindow.dispose();
            }
            
            isRaceInProgress = true;

            raceWindow = new JFrame("🏁 Race In Progress");
            raceWindow.add(racePanel);
            raceWindow.setSize(900, 300);
            raceWindow.setLocationRelativeTo(this);
            raceWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            raceWindow.setVisible(true);

            new Thread(() -> {
                long start = System.currentTimeMillis();
                race.startRace(racePanel::repaint);
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                SwingUtilities.invokeLater(() -> {
                    long end = System.currentTimeMillis();
                    double timeInSec = (end - start) / 1000.0;
                    double speed = race.getRaceLength() / timeInSec;
                    String shape = race.getTrackShape();
                    String weather = race.getWeatherCondition();

                    for (Horse h : race.getLanes()) {
                        if (h != null) {
                            if (h == race.getWinner()) h.increaseConfidence();
                            else if (h.hasFallen()) h.fall();

                            horseStatsMap.get(h).add(new RaceResult(
                                timeInSec, 
                                h == race.getWinner(), 
                                h.hasFallen(), 
                                speed, 
                                shape, 
                                weather
                            ));
                        }
                    }

                    String msg = race.getWinner() != null
                            ? "🏆 Winner: " + race.getWinner().getName()
                            : "😢 All horses have fallen!";
                    
                    JOptionPane.showMessageDialog(this, msg);
                    raceWindow.dispose();
                    isRaceInProgress = false;
                    statsTab.updateStats(race);
                    if (onFinished != null) onFinished.accept(() -> {});
                });
            }).start();
        });

        tabbedPane.addTab("Adjust Race", adjustRaceTab);
        tabbedPane.addTab("Add Horse", addHorseTab);
        tabbedPane.addTab("Stats", statsTab);

        add(tabbedPane, BorderLayout.CENTER);
        setSize(650, 450);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::new);
    }
}

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