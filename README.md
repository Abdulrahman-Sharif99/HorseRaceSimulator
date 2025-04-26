# Horse Race Simulator
# How to Run the Program
Prerequisites:
- Java JDK 17 or later (install from Oracle or OpenJDK).
- A terminal (Command Prompt, PowerShell, or Bash).

Running GUi:
1) navigate your way to project folder (horse-race-simulator).
2) Compile the program with UTF-8 encoding: javac -encoding UTF-8 PartII/*.java
3) Run GUI: java PartII.Gui

Using GUI:
1)Adjust Race Settings:
    -Go to Adjust Race Tab
    -Set:
         -Race length
         -Number of lanes
         -Weather Condition
         -Track shape
    -Click "Adjust Race" button to confirm.
2) Add Horses:
    -Switch to the "Add Horse" tab.
    -Enter:
        -Horse name
        -Symbol (♘, ♞, etc.)
        -Breed
        -coat color
        -equipment
        -Confidence level (via slider)
        -Lane number
    -Click "Add Horse" to register each horse.
3) Place Bet:
    -Switch to "Betting" tab
    -Select horse
    -Place a minimum bet of £5.00
    -Click "Place Bet" button to cofirm bet.
4)Start the Race:
    -Return to the "Add Horse" tab.
    -Click "Start Race" at the bottom.
    -Watch the race unfold in a new window!

Key Methods:
1) SwingUtilities.invokeLater(racePanel::repaint) does Part 2 - Graphical Version of the race
2) race.startRace(() -> {SwingUtilities.invokeLater(racePanel::repaint);}); does Part 1 startRace.

For best results use a UTF-8-compatible terminal


