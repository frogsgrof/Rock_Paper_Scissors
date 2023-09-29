import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {

    ImageIcon rockIcon, paperIcon, szrIcon;
    Font headerFont, subheaderFont, statsFont, resultsFont;
    Color lightBlue = new Color(173, 233, 255),
            skyBlue = new Color(142, 225, 255);
    int WIDTH, HEIGHT;
    JTextField playerWinTF,
            computerWinTF,
            tieTF;

    // This text area is public so that I can request focus to it in the main method after showing the frame.
    public JTextArea resultsTA;

    int gameCounter = 0,
            playerWinCounter = 0,
            computerWinCounter = 0,
            tieCounter = 0;
    Random ran = new Random(); // for move generation
    Strategy leastUsed, mostUsed, lastUsed, random, cheat, copycat;
    Move lastMove; // stores the most recent move by the player

    // for keeping track of the number of times the player has used each move:
    HashMap<Move, Integer> moveCounts = new HashMap<>();

    public RockPaperScissorsFrame() {
        super();

        // gets assets
        getImages();
        getFonts();

        // sets up strategy logic
        createStrategies();
        moveCounts.put(Move.ROCK, 0);
        moveCounts.put(Move.PAPER, 0);
        moveCounts.put(Move.SCISSORS, 0);

        // sets look and feel to the system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        // sets screen size to initialize variables for the screen width and height
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        WIDTH = toolkit.getScreenSize().width * 3 / 4;
        HEIGHT = toolkit.getScreenSize().height * 7 / 8;

        /*
        Sets size and location. The location looks odd, but the x-coordinate is equal to the screen width / 8,
        and the y-coordinate is equal to the screen height / 16. I just simplified it to do math based off the frame
        width and height instead.
        */
        setSize(new Dimension(WIDTH, HEIGHT));
        setLocation(WIDTH / 6, HEIGHT / 14);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        setTitle("Rock Paper Scissors");
        setIconImage(szrIcon.getImage());
        getContentPane().setBackground(lightBlue);

        // creates a header for the top of the page
        JLabel mainLbl = new JLabel("Rock Paper Scissors");
        mainLbl.setFont(headerFont);
        mainLbl.setAlignmentX(Box.CENTER_ALIGNMENT);
        mainLbl.setPreferredSize(new Dimension(mainLbl.getPreferredSize().width, 80));

        // rock, paper, scissors and quit buttons and the panel that contains them
        JButton rockBtn = createGameButton(rockIcon, Move.ROCK);
        JButton paperBtn = createGameButton(paperIcon, Move.PAPER);
        JButton szrBtn = createGameButton(szrIcon, Move.SCISSORS);

        JButton quitBtn = new JButton("Quit");
        quitBtn.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        quitBtn.setFocusPainted(false);
        quitBtn.setFont(statsFont);
        quitBtn.setOpaque(false);

        JPanel btnPnl = new JPanel();
        btnPnl.setBackground(skyBlue);
        btnPnl.setBorder(new LineBorder(Color.WHITE, 8, true));
        btnPnl.setOpaque(true);
        btnPnl.setLayout(new BoxLayout(btnPnl, BoxLayout.Y_AXIS));

        // adds the buttons to a Box object
        Box btnBox = new Box(BoxLayout.X_AXIS);
        btnBox.add(Box.createHorizontalGlue());
        btnBox.add(rockBtn);
        btnBox.add(Box.createHorizontalGlue());
        btnBox.add(paperBtn);
        btnBox.add(Box.createHorizontalGlue());
        btnBox.add(szrBtn);
        btnBox.add(Box.createHorizontalGlue());
        btnBox.add(quitBtn);
        btnBox.add(Box.createHorizontalGlue());

        // adds the box to the button panel
        btnPnl.add(Box.createVerticalStrut(20));
        btnPnl.add(btnBox);
        btnPnl.add(Box.createVerticalStrut(20));
        btnPnl.setMaximumSize(new Dimension(WIDTH * 5 / 8, btnPnl.getSize().height));

        // JLabels, JTextFields for the stats panel
        JLabel playerLbl = new JLabel("Player Wins");
        JLabel computerLbl = new JLabel("Computer Wins");
        JLabel tieLbl = new JLabel("Ties");
        playerWinTF = new JTextField(playerWinCounter);
        computerWinTF = new JTextField(computerWinCounter);
        tieTF = new JTextField(tieCounter);

        // creates 3 smaller panels and adds the labels and text fields to them
        JPanel playerPnl = createStatsPanel(playerLbl, playerWinTF);
        JPanel computerPnl = createStatsPanel(computerLbl, computerWinTF);
        JPanel tiePnl = createStatsPanel(tieLbl, tieTF);

        // creates the stats panel and adds the smaller panels to it
        JPanel statsPnl = new JPanel();
        statsPnl.setOpaque(false);
        statsPnl.setLayout(new BoxLayout(statsPnl, BoxLayout.X_AXIS));
        statsPnl.add(Box.createHorizontalGlue());
        statsPnl.add(playerPnl);
        statsPnl.add(Box.createHorizontalGlue());
        statsPnl.add(computerPnl);
        statsPnl.add(Box.createHorizontalGlue());
        statsPnl.add(tiePnl);
        statsPnl.add(Box.createHorizontalGlue());

        // creates the last panel, with the text area and scroll pane
        resultsTA = new JTextArea();
        resultsTA.setFont(resultsFont);
        resultsTA.setMargin(new Insets(5, 10, 5, 10));
        resultsTA.setAlignmentX(CENTER_ALIGNMENT);
        resultsTA.setEditable(false);
        resultsTA.setLineWrap(true);
        resultsTA.setWrapStyleWord(true);

        JScrollPane resultsSP = new JScrollPane(resultsTA);
        resultsSP.setOpaque(false);
        resultsSP.getViewport().setOpaque(false);
        resultsSP.setPreferredSize(new Dimension(WIDTH * 3 / 4, HEIGHT / 3));
        resultsSP.setMaximumSize(new Dimension(WIDTH * 3 / 4, HEIGHT / 3));

        JPanel resultsPnl = new JPanel();
        resultsPnl.setLayout(new BoxLayout(resultsPnl, BoxLayout.Y_AXIS));
        resultsPnl.setOpaque(false);
        resultsPnl.add(resultsSP);

        // adds everything into the frame
        add(Box.createVerticalGlue());
        add(mainLbl);
        add(Box.createVerticalGlue());
        add(btnPnl);
        add(Box.createVerticalGlue());
        add(statsPnl);
        add(Box.createVerticalGlue());
        add(resultsPnl);
        add(Box.createVerticalGlue());
    }

    /**
     * Loads in all image assets.
     */
    private void getImages() {
        String directory = System.getProperty("user.dir") + "//images//";
        rockIcon = new ImageIcon(directory + "//rock.png");
        paperIcon = new ImageIcon(directory + "//paper.png");
        szrIcon = new ImageIcon(directory + "//scissors.png");
    }

    /**
     * Loads in all font assets.
     */
    private void getFonts() {
        File directory = new File(System.getProperty("user.dir") + "//fonts//");
        try {
            headerFont = Font.createFont(Font.TRUETYPE_FONT, new File(directory + "//header.otf"))
                    .deriveFont(Font.PLAIN, 60f);
            subheaderFont = Font.createFont(Font.TRUETYPE_FONT, new File(directory + "//angular.ttf"))
                    .deriveFont(Font.PLAIN, 34f);
            statsFont = Font.createFont(Font.TRUETYPE_FONT, new File(directory + "//angular.ttf"))
                    .deriveFont(Font.PLAIN, 26f);
            resultsFont = Font.createFont(Font.TRUETYPE_FONT, new File(directory + "//angular.ttf"))
                    .deriveFont(Font.PLAIN, 20f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates rock, paper and scissor buttons with a uniform appearance.
     * @param icon ImageIcon that goes on the button
     * @param move the Move value (rock, paper, or scissors) associated with the button
     * @return JButton
     */
    private JButton createGameButton(ImageIcon icon, Move move) {
        JButton btn = new JButton(icon);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> play(move));
        return btn;
    }

    /**
     * Creates a JPanel to go inside of the stats panel, adding its corresponding JLabel and JTextField.
     * @param label JLabel for the top of this panel
     * @param textField JTextField for displaying a running score
     * @return JPanel
     */
    private JPanel createStatsPanel(JLabel label, JTextField textField) {
        JPanel pnl = new JPanel();
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        label.setAlignmentX(Box.CENTER_ALIGNMENT);
        textField.setAlignmentX(Box.CENTER_ALIGNMENT);

        label.setFont(subheaderFont);
        textField.setFont(statsFont);
        textField.setColumns(3);
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setText("0");
        textField.setEditable(false);
        textField.setMaximumSize(new Dimension(50, 50));
        textField.setOpaque(false);
        textField.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        // creates a temporary JPanel for the label, in order to force the labels to appear as the same width
        JPanel lblPnl = new JPanel();
        lblPnl.setOpaque(false);
        lblPnl.setLayout(new BoxLayout(lblPnl, BoxLayout.X_AXIS));
        lblPnl.add(Box.createHorizontalGlue());
        lblPnl.add(label);
        lblPnl.add(Box.createHorizontalGlue());
        lblPnl.setPreferredSize(new Dimension(WIDTH / 4, 50));

        pnl.add(Box.createVerticalGlue());
        pnl.add(lblPnl);
        pnl.add(Box.createVerticalGlue());
        pnl.add(textField);
        pnl.add(Box.createVerticalGlue());
        pnl.setPreferredSize(new Dimension(WIDTH / 4, 90));
        pnl.setMaximumSize(new Dimension(WIDTH / 3, 100));

        return pnl;
    }

    /**
     * Takes a move and returns the move that will win against it.
     * @param opponent the opponent's move
     * @return the move that wins against the opponent's move
     */
    private Move getWinningMove(Move opponent) {
        return switch (opponent) {
            case ROCK -> Move.PAPER;
            case PAPER -> Move.SCISSORS;
            case SCISSORS -> Move.ROCK;
        };
    }

    /**
     * Initializes anonymous instances of the Strategy interface.
     * @see Strategy
     */
    private void createStrategies() {

        leastUsed = player -> {
            int min = Integer.MAX_VALUE;
            Move leastUsed = Move.ROCK;
            for (Move move : moveCounts.keySet())
                if (moveCounts.get(move) <= min) {
                    min = moveCounts.get(move);
                    leastUsed = move;
                }
            return getWinningMove(leastUsed);
        };

        mostUsed = player -> {
            Move mostUsed = Move.ROCK;
            int max = 0;
            for (Move move : moveCounts.keySet())
                if (moveCounts.get(move) >= max) {
                    max = moveCounts.get(move);
                    mostUsed = move;
                }
            return getWinningMove(mostUsed);
        };

        lastUsed = player -> lastMove;

        random = player -> {
            int i = ran.nextInt(3);
            return switch (i) {
                case 1 -> Move.PAPER;
                case 2 -> Move.SCISSORS;
                default -> Move.ROCK;
            };
        };

        cheat = this::getWinningMove;

        copycat = player -> player;
    }

    /**
     * Randomly determines a strategy to use and calls the determineMove() method on the resulting Strategy object,
     * then updates the UI to show the results.
     * @param player the player's move, determined by the button that was pressed
     */
    private void play(Move player) {
        int i = ran.nextInt(10);

        // If this is the first round, the only strategies the computer can choose from are random, cheat and copycat.
        String strategy;
        Move move = gameCounter == 0 ?
                (switch (i) {
                    case 0, 1, 2, 3, 4 -> {
                        strategy = "random";
                        yield random;
                    }
                    case 5 -> {
                        strategy = "cheat";
                        yield cheat;
                    }
                    default -> {
                        strategy = "copycat";
                        yield copycat;
                    }
                }).determineMove(player) : (switch (i) {
            case 0, 1 -> {
                strategy = "least used";
                yield leastUsed;
            }
            case 2, 3 -> {
                strategy = "most used";
                yield mostUsed;
            }
            case 4, 5 -> {
                strategy = "last used";
                yield lastUsed;
            }
            case 6, 7 -> {
                strategy = "random";
                yield random;
            }
            case 8 -> {
                strategy = "cheat";
                yield cheat;
            }
            default -> {
                strategy = "copycat";
                yield copycat;
            }
        }).determineMove(player);

        // updates game counter, move counter and most recent move
        gameCounter++;
        moveCounts.replace(player, moveCounts.get(player) + 1);
        lastMove = player;

        resultsTA.append(gameCounter + ": ");

        if (move == player) { // tie
            tieCounter++;
            tieTF.setText(String.valueOf(tieCounter));
            resultsTA.append("Both chose " + switch(move) {
                case ROCK -> "rock";
                case PAPER -> "paper";
                case SCISSORS -> "scissors";
            } + ".");

        } else if (move == getWinningMove(player)) { // computer won
            computerWinCounter++;
            computerWinTF.setText(String.valueOf(computerWinCounter));
            resultsTA.append(switch (move) {
                case ROCK -> "Rock";
                case PAPER -> "Paper";
                case SCISSORS -> "Scissors";
            } + " beats " + switch (player) {
                case ROCK -> "rock";
                case PAPER -> "paper";
                case SCISSORS -> "scissors";
            } + ". Computer wins!");

        } else { // player won
            playerWinCounter++;
            playerWinTF.setText(String.valueOf(playerWinCounter));
            resultsTA.append(switch (player) {
                case ROCK -> "Rock";
                case PAPER -> "Paper";
                case SCISSORS -> "Scissors";
            } + " beats " + switch (move) {
                case ROCK -> "rock";
                case PAPER -> "paper";
                case SCISSORS -> "scissors";
            } + ". Player wins.");
        }

        resultsTA.append(" (Strategy: " + strategy + ")\n");
    }
}
