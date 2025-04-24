package PartII;

import PartI.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RacePanel extends JPanel {
    private List<Horse> horses;
    private int panelWidth = 800;
    private int panelHeight = 600;
    private int trackPadding = 60;
    private int laneWidth = 20;
    private int totalTrackDistance = 100;
    private String trackShape = "oval"; // default
    private Race race;

    public RacePanel(Race r,List<Horse> horses) {
        this.race = r;
        this.horses = horses;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawRaceDetails(g2d); 

        switch (trackShape.toLowerCase()) {
            case "oval":
                drawOvalTrack(g2d);
                drawHorsesOval(g2d);
                break;
            case "straight":
                drawStraightTrack(g2d);
                drawHorsesStraight(g2d);
                break;
            case "figure-eight":
            case "figure-8":
                drawStraightTrack(g2d);
                drawHorsesStraight(g2d);
                break;
            default:
                drawOvalTrack(g2d);
                drawHorsesOval(g2d);
        }
    }

    private void drawRaceDetails(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));

        String details = "Weather Condition: " + race.getWeatherCondition() + " | Track: " + getTrackSymbol() + " | Shape: " + capitalize(trackShape) +
                         " | Horses: " + horses.size() +
                         " | Distance: " + totalTrackDistance + " units";

        g2d.drawString(details, 20, 25);
    }

    private String getTrackSymbol() {
        switch (trackShape.toLowerCase()) {
            case "oval": return "🏟️";
            case "straight": return "||";
            case "figure-8": return "||";
            default: return "🛤️";
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }

    private void drawOvalTrack(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        int greenRadius = panelWidth - 2 * trackPadding;
        g2d.fillOval(trackPadding, trackPadding, greenRadius, panelHeight - 2 * trackPadding);

        for (int i = 0; i < horses.size(); i++) {
            int inset = trackPadding + i * laneWidth;
            int w = panelWidth - 2 * inset;
            int h = panelHeight - 2 * inset;
            g2d.setColor(Color.WHITE);
            g2d.drawOval(inset, inset, w, h);
        }

        g2d.setColor(Color.BLACK);
        int outerInset = trackPadding;
        int centerY = panelHeight / 2;
        int x = panelWidth - outerInset - 10;
        int y = centerY - (panelHeight - 2 * outerInset) / 2 - 10;
        
        g2d.drawString("🏁", x, y);
    }

    private void drawHorsesOval(Graphics2D g2d) {
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;

        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);

            int laneInset = trackPadding + i * laneWidth;
            int laneWidthOval = panelWidth - 2 * laneInset;
            int laneHeightOval = panelHeight - 2 * laneInset;

            double distanceOffset = i * 2.5; 
            double progress = (horse.getDistanceTravelled() + distanceOffset) / (double) totalTrackDistance;
            progress = Math.min(progress, 1.0);

            double angle = 2 * Math.PI * progress;

            int xRadius = laneWidthOval / 2;
            int yRadius = laneHeightOval / 2;

            int x = (int) (centerX + xRadius * Math.cos(angle));
            int y = (int) (centerY + yRadius * Math.sin(angle));

            Color color = getColorFromCoat(horse.getCoatColor());
            g2d.setColor(color);
            g2d.fillOval(x - 4, y - 4, 8, 8);

            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(horse.getSymbol()), x - 3, y - 6);
        }
    }

    private void drawStraightTrack(Graphics2D g2d) {
        for (int i = 0; i < horses.size(); i++) {
            int y = 50 + i * 40;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(50, y, panelWidth - 100, 30);

            g2d.setColor(Color.BLACK);
            g2d.drawString("🏁", panelWidth - 55, y + 20);
        }
    }

    private void drawHorsesStraight(Graphics2D g2d) {
        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);
            int x = 50 + (int) ((panelWidth - 100) * horse.getDistanceTravelled() / totalTrackDistance);
            int y = 50 + i * 40;

            Color color = getColorFromCoat(horse.getCoatColor());
            g2d.setColor(color);
            g2d.fillOval(x, y, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(horse.getSymbol()), x + 5, y - 5);
        }
    }


    private Color getColorFromCoat(String coat) {
        if (coat == null) return Color.BLACK;
        switch (coat.toLowerCase()) {
            case "chestnut": return new Color(205, 92, 92);
            case "black": return Color.BLACK;
            case "grey": return Color.GRAY;
            case "bay": return new Color(139, 69, 19);
            case "white": return Color.WHITE;
            default: return Color.DARK_GRAY;
        }
    }

    public void updateHorseProgress(List<Horse> updatedHorses) {
        this.horses = updatedHorses;
        repaint();
    }

    public void setTotalTrackDistance(int distance) {
        this.totalTrackDistance = distance;
    }

    public void setTrackShape(String trackShape) {
        this.trackShape = trackShape;
        repaint();
    }
}
