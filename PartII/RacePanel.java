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
    private int totalTrackDistance;
    private String trackShape = "oval";
    private Race race;
    private int STEPS = 1000;
    private int baseRadius = 200;

    public RacePanel(Race r, List<Horse> horses) {
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
                drawFigureEightTrack(g2d);
                drawHorsesFigureEight(g2d);
                break;
            default:
                drawOvalTrack(g2d);
                drawHorsesOval(g2d);
                break;
        }
    }

    private void drawRaceDetails(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        String details = "Weather Condition: " + race.getWeatherCondition() + " | Track: " + capitalize(trackShape) +
                         " | Horses: " + horses.size() +
                         " | Distance: " + totalTrackDistance + " units";
        g2d.drawString(details, 20, 25);
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
            g2d.setColor(Color.BLACK);
            g2d.drawString("🏁", inset, inset);
        }
    }

    private void drawHorsesOval(Graphics2D g2d) {
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;

        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);

            int laneInset = trackPadding + i * laneWidth;
            int laneWidthOval = panelWidth - 2 * laneInset;
            int laneHeightOval = panelHeight - 2 * laneInset;

            double progress = (horse.getDistanceTravelled() %totalTrackDistance) / (double) totalTrackDistance;
            progress = Math.min(progress, 1.0);

            double angle = 2 * Math.PI * progress;

            int xRadius = laneWidthOval / 2;
            int yRadius = laneHeightOval / 2;

            int x = (int) (centerX + xRadius * Math.cos(angle));
            int y = (int) (centerY + yRadius * Math.sin(angle));

            if (horse.hasFallen()) {
                g2d.setColor(Color.RED);
                g2d.drawString("X", x - 3, y - 6);
            }
            else{
                Color color = getColorFromCoat(horse.getCoatColor());
                g2d.setColor(color);
                g2d.fillOval(x - 4, y - 4, 8, 8);
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(horse.getSymbol()), x - 3, y - 6);
            }
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

            if (horse.hasFallen()) {
                g2d.setColor(Color.RED);
                g2d.drawString("X", x + 5, y - 5);
            } else {
                Color color = getColorFromCoat(horse.getCoatColor());
                g2d.setColor(color);
                g2d.fillOval(x, y, 20, 20);
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(horse.getSymbol()), x + 5, y - 5);
            }
        }
    }

    private void drawFigureEightTrack(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
    
        for (int lane = 0; lane < horses.size(); lane++) {
            double laneOffset = lane * laneWidth * 0.5;
    
            g2d.setColor(Color.BLACK);
            for (int i = 0; i < STEPS - 1; i++) {
                double angle1 = 2 * Math.PI * i / STEPS;
                double angle2 = 2 * Math.PI * (i + 1) / STEPS;
    
                int x1 = (int) ((Math.cos(angle1) * (baseRadius + laneOffset)) + centerX);
                int y1 = (int) ((Math.sin(2 * angle1) * (baseRadius + laneOffset) / 2) + centerY);
                int x2 = (int) ((Math.cos(angle2) * (baseRadius + laneOffset)) + centerX);
                int y2 = (int) ((Math.sin(2 * angle2) * (baseRadius + laneOffset) / 2) + centerY);
    
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    
        g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2d.drawString("🏁", centerX - 10, centerY + 11);
    }
    

    private void drawHorsesFigureEight(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
    
        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);
    
            double progress = (horse.getDistanceTravelled() % totalTrackDistance) / (double) totalTrackDistance;
            progress = Math.min(progress, 1.0);
    
            if (progress == 0.0) {
                if (horse.hasFallen()) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("X", centerX - 3, centerY - 3);
                } else {
                    Color color = getColorFromCoat(horse.getCoatColor());
                    g2d.setColor(color);
                    g2d.fillOval(centerX - 4, centerY - 4, 8, 8);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.valueOf(horse.getSymbol()), centerX - 3, centerY - 6);
                }
                continue;
            }
    
            if (progress == 0.5) {
                progress += 0.0001;
            } else if (progress == 1.0) {
                progress -= 0.0001;
            }
    
            int lane;
            double adjustedProgress;
            if (progress < 0.5) {
                lane = i;
                adjustedProgress = progress * 2;
            } else {
                lane = horses.size() - 1 - i;
                adjustedProgress = (progress - 0.5) * 2;
            }
    
            double laneOffset = lane * laneWidth * 0.5;
    
            double angle = 2 * Math.PI * adjustedProgress;
    
            double xBase = Math.cos(angle);
            double yBase = Math.sin(2 * angle) / 2.0;
    
            double xPath = xBase * (baseRadius + laneOffset);
            double yPath = yBase * (baseRadius + laneOffset);
    
            double dx_dphi = -Math.sin(angle);
            double dy_dphi = Math.cos(2 * angle);
    
            double magnitude = Math.sqrt(dx_dphi * dx_dphi + dy_dphi * dy_dphi);
    
            double offsetX = (laneOffset / 2) * (dy_dphi / magnitude);
            double offsetY = (laneOffset / 2) * (-dx_dphi / magnitude);
    
            int x = (int) (centerX + xPath + offsetX);
            int y = (int) (centerY + yPath + offsetY);
    
            x = Math.max(trackPadding, Math.min(panelWidth - trackPadding, x));
            y = Math.max(trackPadding, Math.min(panelHeight - trackPadding, y));
    
            if (horse.hasFallen()) {
                g2d.setColor(Color.RED);
                g2d.drawString("X", x - 3, y - 3);
            } else {
                Color color = getColorFromCoat(horse.getCoatColor());
                g2d.setColor(color);
                g2d.fillOval(x - 4, y - 4, 8, 8);
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(horse.getSymbol()), x - 3, y - 6);
            }
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

    public void setTrackLength(int length) {
        this.totalTrackDistance = length;
    }
    public void resetRace(List<Horse> newHorses) {
        this.horses = newHorses;
        repaint();
    }
}