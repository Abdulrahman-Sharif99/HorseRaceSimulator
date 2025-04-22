package PartII;

import PartI.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AddHorseTab extends JPanel {
    private final List<Horse> horses = new ArrayList<>();
    private JTextField horseNameField;
    private JComboBox<String> horseSymbol;
    private JSlider confidenceSlider;
    private JComboBox<Integer> laneComboBox;
    private JButton addHorseButton;
    private JButton startRaceButton;
    private JComboBox<String> breedComboBox;
    private JComboBox<String> coatColorComboBox;
    JComboBox<String> saddleComboBox;
    private JComboBox<String> horseshoeComboBox;
    private JComboBox<String> accessoryComboBox;


    private Race race;
    private Map<Horse, List<RaceResult>> horseStatsMap;

    private Runnable onHorseAdded;
    private BiConsumer<RacePanel, Consumer<Runnable>> onRaceStarted;

    private boolean isRaceInProgress = false;

    public AddHorseTab() {
        setLayout(new BorderLayout());

        // Panel setup for adding horse
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Horse"));

        horseNameField = new JTextField();
        horseSymbol = new JComboBox<>(new String[]{"♘", "♞", "♕", "♛", "🐎"});
        confidenceSlider = new JSlider(0, 100, 50);
        confidenceSlider.setMajorTickSpacing(25);
        confidenceSlider.setMinorTickSpacing(5);
        confidenceSlider.setPaintTicks(true);
        confidenceSlider.setPaintLabels(true);

        laneComboBox = new JComboBox<>();
        breedComboBox = new JComboBox<>(new String[]{"Thoroughbred", "Arabian", "Mustang"});
        coatColorComboBox = new JComboBox<>(new String[]{"Black", "Chestnut", "Grey", "Bay", "Palomino"});
        saddleComboBox = new JComboBox<>(new String[]{"Standard", "Lightweight", "Heavy"});
        horseshoeComboBox = new JComboBox<>(new String[]{"Basic", "Grip-enhanced", "Speed-boosting"});
        accessoryComboBox = new JComboBox<>(new String[]{"None", "Blinkers", "Decorative Ribbon"});


        addHorseButton = new JButton("Add Horse");
        startRaceButton = new JButton("Start Race");

        addHorseButton.setEnabled(false);
        startRaceButton.setEnabled(false);

        formPanel.add(new JLabel("Horse Name:"));
        formPanel.add(horseNameField);

        formPanel.add(new JLabel("Horse Symbol:"));
        formPanel.add(horseSymbol);

        formPanel.add(new JLabel("Breed:"));
        formPanel.add(breedComboBox);

        formPanel.add(new JLabel("Coat Color:"));
        formPanel.add(coatColorComboBox);

        formPanel.add(new JLabel("Saddle:"));
        formPanel.add(saddleComboBox);

        formPanel.add(new JLabel("Horseshoes:"));
        formPanel.add(horseshoeComboBox);

        formPanel.add(new JLabel("Accessory:"));
        formPanel.add(accessoryComboBox);

        formPanel.add(new JLabel("Confidence:"));
        formPanel.add(confidenceSlider);

        formPanel.add(new JLabel("Lane Number:"));
        formPanel.add(laneComboBox);

        formPanel.add(new JLabel(""));
        formPanel.add(addHorseButton);

        add(formPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(startRaceButton);
        add(bottom, BorderLayout.SOUTH);

        addHorseButton.addActionListener(e -> {
            if (race == null) {
                JOptionPane.showMessageDialog(this, "Please adjust the race first.");
                return;
            }
        
            String name = horseNameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a horse name.");
                return;
            }
        
            char symbol = ((String) horseSymbol.getSelectedItem()).charAt(0);
            double confidence = confidenceSlider.getValue() / 100.0;
            int lane = (int) laneComboBox.getSelectedItem();
        
            Horse horse = new Horse(symbol, name, confidence);
            race.addHorse(horse, lane);
            horses.add(horse); 
            horseStatsMap.putIfAbsent(horse, new java.util.ArrayList<>());
        
            if (onHorseAdded != null) onHorseAdded.run();
        
            JOptionPane.showMessageDialog(this, "Horse added!");
            horseNameField.setText("");
            confidenceSlider.setValue(50);

            String breed = (String) breedComboBox.getSelectedItem();
            String coatColor = (String) coatColorComboBox.getSelectedItem();
            String saddle = (String) saddleComboBox.getSelectedItem();
            String horseshoe = (String) horseshoeComboBox.getSelectedItem();
            String accessory = (String) accessoryComboBox.getSelectedItem();

            // Apply visual customization
            horse.setBreed(breed);
            horse.setCoatColor(coatColor);

            // Apply performance effects based on equipment
            applyEquipmentEffects(horse, saddle, horseshoe, accessory);
            SpeedModifier(saddle,horseshoe);

            // Add horse
            race.addHorse(horse, lane);
            horses.add(horse);
            horseStatsMap.putIfAbsent(horse, new ArrayList<>());

        });
        

        startRaceButton.addActionListener(e -> {
            if (isRaceInProgress) {
                JOptionPane.showMessageDialog(this, "Race is already in progress!");
                return;
            }

            if (race == null || race.getLanes().stream().noneMatch(h -> h != null)) {
                JOptionPane.showMessageDialog(this, "Add horses first!");
                return;
            }

            isRaceInProgress = true;
            for (Horse h : race.getLanes()) if (h != null) h.goBackToStart();

            RacePanel racePanel = new RacePanel(race);
            if (onRaceStarted != null) {
                onRaceStarted.accept(racePanel, (finisher) -> {
                    isRaceInProgress = false;
                    finisher.run();
                });
            }
        });
    }

    public void setRace(Race race) {
        this.race = race;
        laneComboBox.removeAllItems();
        for (int i = 1; i <= race.getLaneCount(); i++) laneComboBox.addItem(i);
    }

    public void setHorseStatsMap(Map<Horse, List<RaceResult>> map) {
        this.horseStatsMap = map;
    }

    public void setOnHorseAdded(Runnable callback) {
        this.onHorseAdded = callback;
    }

    public void setOnRaceStarted(BiConsumer<RacePanel, Consumer<Runnable>> callback) {
        this.onRaceStarted = callback;
    }

    public void setRaceAdjusted(boolean adjusted) {
        addHorseButton.setEnabled(adjusted);
    }

    public void setBetPlaced(boolean isBetPlaced){
        startRaceButton.setEnabled(isBetPlaced);
    }

    public List<Horse> getHorses() {
        return horses;
    }

    private void applyEquipmentEffects(Horse horse, String saddle, String horseshoe, String accessory) {
        double confidenceMod = 0.0;
    
        switch (saddle) {
            case "Heavy" -> confidenceMod += 0.1;
        }
    
        switch (horseshoe) {
            case "Grip-enhanced" -> confidenceMod += 0.1;
        }
    
        if ("Blinkers".equals(accessory)) confidenceMod += 0.05;
        horse.setConfidence(horse.getConfidence()+ confidenceMod);
    }

    public double SpeedModifier(String saddle, String horseshoe) {
        double speedMod = 0.0;
        if (saddle.equals("Lightweight")) speedMod += 0.1;
        if (horseshoe.equals("Speed-boosting")) speedMod += 0.3;
        return speedMod;
    }

    public String getSaddleComboBox() {
        return saddleComboBox.getSelectedItem().toString();
    }

    public String getHorseshoeComboBox() {
        return horseshoeComboBox.getSelectedItem().toString();
    }
}