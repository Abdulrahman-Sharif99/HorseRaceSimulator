package PartII;

import PartI.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class AdjustRaceTab extends JPanel {
    private JSpinner raceLengthSpinner;
    private JComboBox<Integer> laneCountComboBox;
    private JComboBox<String> weatherConditionComboBox;
    private JComboBox<String> trackShapeComboBox;
    private JButton adjustRaceButton;

    private BiConsumer<Race, String[]> onRaceAdjusted;

    public AdjustRaceTab() {
        setLayout(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Race Settings"));

        raceLengthSpinner = new JSpinner(new SpinnerNumberModel(50, 10, 200, 10));
        laneCountComboBox = new JComboBox<>();
        for (int i = 1; i <= 10; i++) laneCountComboBox.addItem(i);

        weatherConditionComboBox = new JComboBox<>(new String[]{"Wet", "Dry", "Sunny"});
        trackShapeComboBox = new JComboBox<>(new String[]{"Oval", "Figure-eight", "Straight"});

        adjustRaceButton = new JButton("Adjust Race");

        add(new JLabel("Race Length:"));
        add(raceLengthSpinner);

        add(new JLabel("Number of Lanes:"));
        add(laneCountComboBox);

        add(new JLabel("Weather Condition:"));
        add(weatherConditionComboBox);

        add(new JLabel("Track Shape:"));
        add(trackShapeComboBox);

        add(new JLabel(""));
        add(adjustRaceButton);

        adjustRaceButton.addActionListener(e -> {
            int raceLength = (int) raceLengthSpinner.getValue();
            int laneCount = (int) laneCountComboBox.getSelectedItem();
            String weather = (String) weatherConditionComboBox.getSelectedItem();
            String trackShape = (String) trackShapeComboBox.getSelectedItem();

            Race race = new Race(raceLength, laneCount);
            if (onRaceAdjusted != null) {
                onRaceAdjusted.accept(race, new String[]{trackShape, weather});
            }
        });
    }

    public void setOnRaceAdjusted(BiConsumer<Race, String[]> callback) {
        this.onRaceAdjusted = callback;
    }
}