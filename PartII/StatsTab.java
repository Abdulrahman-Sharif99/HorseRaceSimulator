package PartII;

import PartI.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;

public class StatsTab extends JPanel {
    private JTextArea statsArea;
    private Map<Horse, List<RaceResult>> horseStatsMap;
    private Map<String, Double> fastestTimes; // Key: "weather_trackShape", Value: fastest time

    public StatsTab() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Horse & Race Stats"));

        statsArea = new JTextArea();
        statsArea.setEditable(false);
        add(new JScrollPane(statsArea), BorderLayout.CENTER);
        
        fastestTimes = new HashMap<>();
    }

    public void setHorseStatsMap(Map<Horse, List<RaceResult>> map) {
        this.horseStatsMap = map;
        updateFastestTimes();
    }

    private void updateFastestTimes() {
        fastestTimes.clear();
        if (horseStatsMap == null) return;

        for (List<RaceResult> results : horseStatsMap.values()) {
            for (RaceResult result : results) {
                String key = result.getWeatherCondition() + "_" + result.getTrackShape();
                double currentTime = result.getTime();
                
                // Update fastest time for this weather/track combination
                if (!fastestTimes.containsKey(key) || currentTime < fastestTimes.get(key)) {
                    fastestTimes.put(key, currentTime);
                }
            }
        }
    }

    public void updateStats(Race race) {
        if (horseStatsMap == null || horseStatsMap.isEmpty()) return;
    
        StringBuilder sb = new StringBuilder();
        Map<String, List<RaceResult>> mergedStats = new LinkedHashMap<>();
    
        // 1. Display individual horse statistics only
        sb.append("=== Horse Statistics ===\n");
        for (Map.Entry<Horse, List<RaceResult>> entry : horseStatsMap.entrySet()) {
            Horse h = entry.getKey();
            List<RaceResult> results = entry.getValue();
            String key = h.getName() + "::" + h.getSymbol();
    
            mergedStats.computeIfAbsent(key, k -> new java.util.ArrayList<>()).addAll(results);
        }
    
        for (Map.Entry<String, List<RaceResult>> entry : mergedStats.entrySet()) {
            String key = entry.getKey();
            List<RaceResult> results = entry.getValue();
            String[] parts = key.split("::");
            String name = parts[0];
            char symbol = parts[1].charAt(0);
    
            long wins = results.stream().filter(RaceResult::isWin).count();
            long total = results.size();
            double avgSpeed = results.stream().mapToDouble(RaceResult::getAvgSpeed).average().orElse(0);
            double avgTime = results.stream().mapToDouble(RaceResult::getTime).average().orElse(0);
            double confidence = horseStatsMap.keySet().stream()
                    .filter(h -> h.getName().equals(name) && h.getSymbol() == symbol)
                    .findFirst()
                    .map(Horse::getConfidence)
                    .orElse(0.0);
    
            sb.append(name).append(" (").append(symbol).append(")\n")
              .append(" - Races: ").append(total)
              .append(", Wins: ").append(wins)
              .append(", Win %: ").append(String.format("%.2f", (100.0 * wins / Math.max(total, 1))))
              .append("\n - Avg Speed: ").append(String.format("%.2f", avgSpeed))
              .append(" m/s, Avg Time: ").append(String.format("%.2f", avgTime)).append(" s\n")
              .append(" - Current Confidence: ").append(String.format("%.2f", confidence)).append("\n");
    
            // Compute best speed per track shape
            Map<String, Double> bestSpeeds = new HashMap<>();
            for (RaceResult result : results) {
                String shape = result.getTrackShape();
                double speed = result.getAvgSpeed();
                bestSpeeds.merge(shape, speed, Math::max);
            }
    
            sb.append(" - Best Speeds by Track Type:\n");
            for (Map.Entry<String, Double> speedEntry : bestSpeeds.entrySet()) {
                sb.append("    • ").append(speedEntry.getKey())
                  .append(": ").append(String.format("%.2f", speedEntry.getValue()))
                  .append(" m/s\n");
            }
    
            sb.append("\n");
        }
    
        statsArea.setText(sb.toString());
    }    
}


