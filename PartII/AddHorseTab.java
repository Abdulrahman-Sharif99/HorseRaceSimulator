package PartII;

import PartI.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AddHorseTab extends JPanel {
    private JTextField horseNameField;
    private JComboBox<String> horseSymbol;
    private JSlider confidenceSlider;
    private JComboBox<Integer> laneComboBox;
    private JButton addHorseButton;
    private JButton startRaceButton;

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

        addHorseButton = new JButton("Add Horse");
        startRaceButton = new JButton("Start Race");

        formPanel.add(new JLabel("Horse Name:"));
        formPanel.add(horseNameField);

        formPanel.add(new JLabel("Horse Symbol:"));
        formPanel.add(horseSymbol);

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
            horseStatsMap.putIfAbsent(horse, new java.util.ArrayList<>());

            if (onHorseAdded != null) onHorseAdded.run();

            JOptionPane.showMessageDialog(this, "Horse added!");
            horseNameField.setText("");
            confidenceSlider.setValue(50);
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
}