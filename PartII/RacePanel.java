package PartII;

import PartI.Horse;
import PartI.Race;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RacePanel extends JPanel {
    private final Race race;
    private final Font horseFont = new Font("Monospaced", Font.PLAIN, 16);
    private final Font infoFont = new Font("SansSerif", Font.PLAIN, 14);

    public RacePanel(Race race) {
        this.race = race;
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Horse> horses = race.getLanes();
        int raceLength = race.getRaceLength();
        int padding = 40;
        int laneHeight = 30;
        int startY = 60;

        // Draw weather and track info
        g.setFont(infoFont);
        g.setColor(Color.BLACK);
        g.drawString(getWeatherSymbol() + " Weather: " + race.getWeatherCondition(), padding, 30);
        g.drawString(getTrackSymbol() + " Track Shape: " + race.getTrackShape(), padding + 250, 30);

        // Draw top border
        g.drawLine(padding, startY - 5, padding + raceLength, startY - 5);

        // Draw each lane
        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);
            int y = startY + i * laneHeight;

            if (horse != null) {
                // Draw lane border
                g.drawLine(padding, y + 15, padding + raceLength, y + 15);

                // Draw horse position
                int x = padding + horse.getDistanceTravelled();
                g.setFont(horseFont);

                if (horse.hasFallen()) {
                    g.setColor(Color.RED);
                    g.drawString("✗", x, y);
                } else {
                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(horse.getSymbol()), x, y);
                }

                // Draw horse info
                g.setFont(infoFont);
                g.drawString(horse.getName() + " (Conf: " + String.format("%.2f", horse.getConfidence()) + ")",
                             padding + raceLength + 10, y);
            }
        }

        // Draw bottom border
        g.drawLine(padding, startY + horses.size() * laneHeight - 5,
                  padding + raceLength, startY + horses.size() * laneHeight - 5);
    }

    private String getWeatherSymbol() {
        switch (race.getWeatherCondition().toLowerCase()) {
            case "Wet": return "🌧️";
            case "Dry": return "🌤️";
            case "Sunny": return "☀️";
            default: return "🌦️";
        }
    }

    private String getTrackSymbol() {
        switch (race.getTrackShape().toLowerCase()) {
            case "oval": return "🏟️";
            case "straight": return "⬛";
            case "figure-8":
            case "figure-eight":
                return "∞";
            default: return "🛤️";
        }
    }
}