/* Originally by David Barton, (c) Mediavault, 1997 */
package dev.jcps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

/**
 * The main class for the Invade game.
 * This class extends the Panel class and implements the JavaAppletAdapter interface.
 * It contains the game logic and rendering code.
 *
 * <p>See {@link JavaAppletAdapter}</p>
 */
public class Invade extends Panel implements JavaAppletAdapter, MouseListener, MouseMotionListener, KeyListener {
    private static final double NS_PER_UPDATE = 1000000000.0 / 60.0; // 60 updates per second
    private final int[] sinx = new int[InvadeConstants.SIN_TABLE_SIZE];
    private final transient Image[] pImages = new Image[InvadeConstants.NUM_IMAGES];
    private final int[] baddies;
    private final int[] baddiex;
    private final int[] baddiey;
    private final int[] downer;
    private final int[] speed;
    int aaa;
    int totalBaddies;
    int deadBaddies;
    int baddieDraw;
    int gameSpeed = InvadeConstants.INITIAL_GAME_SPEED;
    String bigString;
    int stance;
    int gameStatus = InvadeConstants.STATE_HIGH_SCORES;
    ArrayList<String> script = new ArrayList<>();
    int t1;
    int[] mx = new int[50];
    int[] my = new int[50];
    int wid;
    int level;
    int newHighScore = -1;
    int[] highScores = new int[InvadeConstants.MAX_HIGH_SCORES];
    String[] highNames = new String[InvadeConstants.MAX_HIGH_SCORES];
    Font f = new Font("Helvetica, Arial", Font.PLAIN, InvadeConstants.FONT_SIZE_NORMAL);
    Font f2 = new Font("Helvetica, Arial", Font.BOLD, InvadeConstants.FONT_SIZE_LARGE);
    FontMetrics fm;
    char typedChar;
    String typestring;
    int typed;
    int sx;
    int bullet;
    int ly;
    int sy;
    int dance;
    int score;
    int nWidth;
    int nHeight;
    int ii;
    String textLine;
    int iPressed;
    int[] starX;
    int[] starY;
    int[] bx;
    int[] by;
    Random rnd;
    private boolean bAllLoaded;
    private int i;
    private transient Image mImage;
    private transient Graphics mG;
    private Dimension mDimImage;
    private long lastTime;

    /**
     *
     */
    public Invade() {
        this.rnd = new SecureRandom();
        this.fm = this.getFontMetrics(this.f2);
        this.baddies = new int[InvadeConstants.MAX_BADDIES];
        this.baddiex = new int[InvadeConstants.MAX_BADDIES];
        this.baddiey = new int[InvadeConstants.MAX_BADDIES];
        this.downer = new int[InvadeConstants.MAX_BADDIES];
        this.speed = new int[InvadeConstants.MAX_BADDIES];
        this.iPressed = -1;
        this.starX = new int[InvadeConstants.MAX_STARS];
        this.starY = new int[InvadeConstants.MAX_STARS];
        this.bx = new int[InvadeConstants.MAX_BULLETS];
        this.by = new int[InvadeConstants.MAX_BULLETS];

        lastTime = System.nanoTime();
    }

    /**
     * @param args command line arguments
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Invade");
        Invade invade = new Invade();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(InvadeConstants.WINDOW_WIDTH, InvadeConstants.WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.add(invade);
        invade.addMouseListener(invade);
        invade.addMouseMotionListener(invade);
        invade.addKeyListener(invade);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        invade.init();
        invade.run();
    }

    public void stop() {
        //Application.exit
    }

    /**
     * @param g the specified Graphics window
     */
    @Override
    public void paint(Graphics g) {
        if (this.mImage != null) {
            g.drawImage(this.mImage, 0, 0, null);
        }

    }

    /**
     * The main game loop function
     */
    public void gameloop() {
        updateStars();
        updateBullets();
        updateBaddies();
        checkCollisions();
        updatePlayer();
        drawScore();
    }

    private void updateStars() {
        for (int j = 0; j < 30; j++) {
            mG.drawImage(pImages[4], starX[j], starY[j], null);
            starY[j] = (starY[j] + 1) % nHeight;
        }
    }

    private void updateBullets() {
        for (int j = 0; j < 30; j++) {
            if (bx[j] != -1) {
                mG.drawImage(pImages[5], bx[j], by[j], null);
                by[j] -= 4;
                if (by[j] < 0) {
                    bx[j] = -1;
                }
            }
        }
    }

    private void updateBaddies() {
        if (baddieDraw == 0) {
            dance = sinx[stance];
            stance = (stance + 1) % InvadeConstants.BADDIE_HEIGHT;
        }
        baddieDraw = (baddieDraw + 1) % gameSpeed;

        for (int j = 0; j < InvadeConstants.MAX_BADDIES; j++) {
            if (baddies[j] == 1) {
                mG.drawImage(pImages[6], baddiex[j] + dance, baddiey[j] + downer[j], null);
                if (baddieDraw == 0) {
                    downer[j] += speed[j];
                }
                if (baddiey[j] + downer[j] > nHeight) {
                    resetBaddie(j);
                }
            }
        }
    }

    private void resetBaddie(int index) {
        baddiey[index] = InvadeConstants.BADDIE_INITIAL_Y2;
        downer[index] = 0;
        speed[index] = rnd.nextInt(3) + 1;
        baddiex[index] = rnd.nextInt(nWidth) + 1;
    }

    private void checkCollisions() {
        for (int j = 0; j < 30; j++) {
            if (bx[j] != -1) {
                checkBulletCollision(j);
            }
        }

        for (int j = 0; j < InvadeConstants.MAX_BADDIES; j++) {
            if (baddies[j] == 1 && checkPlayerCollision(j)) {
                gameStatus = InvadeConstants.STATE_GAME_OVER;
                return;
            }
        }
    }

    private void checkBulletCollision(int bulletIndex) {
        for (int j = 0; j < InvadeConstants.MAX_BADDIES; j++) {
            if (baddies[j] == 1 && isBulletHittingBaddie(bulletIndex, j)) {
                baddies[j] = 0;
                bx[bulletIndex] = -1;
                score += InvadeConstants.SCORE_PER_BADDIE;
                deadBaddies++;
                if (deadBaddies == totalBaddies) {
                    gameStatus = InvadeConstants.STATE_LEVEL_COMPLETE;
                }
                return;
            }
        }
    }

    private boolean isBulletHittingBaddie(int bulletIndex, int baddieIndex) {
        return bx[bulletIndex] >= baddiex[baddieIndex] + dance &&
                bx[bulletIndex] <= baddiex[baddieIndex] + InvadeConstants.BADDIE_WIDTH + dance &&
                by[bulletIndex] >= baddiey[baddieIndex] + downer[baddieIndex] &&
                by[bulletIndex] <= baddiey[baddieIndex] + InvadeConstants.BADDIE_HEIGHT + downer[baddieIndex];
    }

    private boolean checkPlayerCollision(int baddieIndex) {
        int baddieLeft = baddiex[baddieIndex] + dance;
        int baddieRight = baddieLeft + InvadeConstants.BADDIE_WIDTH;
        int baddieTop = baddiey[baddieIndex] + downer[baddieIndex];
        int baddieBottom = baddieTop + InvadeConstants.BADDIE_HEIGHT;

        return (isOverlapping(baddieLeft, baddieTop, baddieBottom) ||
                isOverlapping(baddieRight, baddieTop, baddieBottom));
    }

    private boolean isOverlapping(int x, int top, int bottom) {
        return x > sx && x < sx + InvadeConstants.PLAYER_WIDTH &&
                ((top > sy && top < sy + InvadeConstants.PLAYER_HEIGHT) ||
                        (bottom > sy && bottom < sy + InvadeConstants.PLAYER_HEIGHT));
    }

    private void updatePlayer() {
        mG.drawImage(pImages[aaa], sx, sy, null);
        aaa = (aaa + 1) % 4;
    }

    private void drawScore() {
        mG.setColor(new Color(255, 71, 84));
        mG.drawString(" Score : " + score + "    Level : " + level, 0, 10);
    }

    /**
     * Loads high scores from a text file.
     * <p>
     * This method attempts to load high scores from a text file named "scores.txt"
     * located in the same directory as the application. The file is expected to
     * contain a list of names and scores, with each line containing a name (up to
     * 9 characters) followed by a score (up to 5 digits).
     * <p>
     * If the file cannot be loaded or the data is malformed, default values are
     * used for the high scores and names.
     */
    public void loadscores() {
        String filePath = "scores.txt";  // Adjust this path as needed
        Properties scores = new Properties();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            scores.load(reader);
        } catch (IOException e) {
            System.err.println("Error loading scores: " + e.getMessage());
        }

        for (int l = 0; l < 10; l++) {
            String key = "score" + l;
            String entry = scores.getProperty(key, "Your name?=0");
            String[] parts = entry.split("=");

            if (parts.length == 2) {
                highNames[l] = parts[0].trim();
                try {
                    highScores[l] = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    highScores[l] = 0;
                }
            } else {
                highNames[l] = "Your name?";
                highScores[l] = 0;
            }
        }
    }

    /**
     * @param g the specified Graphics window
     */
    @Override
    public void update(Graphics g) {
        long now = System.nanoTime();
        double delta = (now - lastTime) / NS_PER_UPDATE;
        if (delta >= 1) {
            updateGameState();
            lastTime = now;
        }
        paint(g);
    }

    public void updateGameState() {
        this.mG.setColor(new Color(15, 23, 52));
        this.mG.fillRect(0, 0, this.nWidth, this.nHeight);

        if (!this.bAllLoaded) {
            this.mG.setColor(new Color(255, 71, 84));
            this.mG.setFont(this.f2);
            this.mG.drawString("Loading graphics...", 20, 20);
        } else {
            switch (this.gameStatus) {
                case InvadeConstants.STATE_PLAYING:
                    this.gameloop();
                    break;

                case InvadeConstants.STATE_GAME_OVER:
                    checkForHighScore();
                    if (this.gameStatus != InvadeConstants.STATE_NEW_HIGH_SCORE) {
                        displayGameOver();
                        this.gameSpeed = InvadeConstants.INITIAL_GAME_SPEED;
                    }
                    break;

                case InvadeConstants.STATE_LEVEL_COMPLETE:
                    displayLevelComplete();
                    resetLevel();
                    break;

                case InvadeConstants.STATE_NEW_HIGH_SCORE:
                    displayNewHighScore();
                    break;

                default:
                case InvadeConstants.STATE_HIGH_SCORES:
                    displayHighScores();
                    break;
            }
        }
    }

    private void checkForHighScore() {
        for (int l = 0; l < InvadeConstants.MAX_HIGH_SCORES; l++) {
            if (this.score > this.highScores[l]) {
                // Shift down lower scores
                for (int j = InvadeConstants.MAX_HIGH_SCORES - 1; j > l; j--) {
                    this.highScores[j] = this.highScores[j - 1];
                    this.highNames[j] = this.highNames[j - 1];
                }
                this.highScores[l] = this.score;
                this.newHighScore = l;
                this.gameStatus = InvadeConstants.STATE_NEW_HIGH_SCORE;
                this.typestring = "";
                break;
            }
        }
        if (this.gameStatus != InvadeConstants.STATE_NEW_HIGH_SCORE) {
            this.loadscores();
        }
    }

    private void displayHighScores() {
        this.mG.setColor(new Color(255, 71, 84));
        this.mG.setFont(this.f2);
        this.mG.drawString("HIGH SCORES", 100, 20);

        for (int j = 0; j < 10; j++) {
            if (j < 9) {
                this.mG.drawString(" " + (j + 1) + "  -  " + this.highNames[j], 80, 60 + j * 19);
            } else {
                this.mG.drawString((j + 1) + "  -  " + this.highNames[j], 75, 60 + j * 19);
            }
            this.mG.drawString(" - " + this.highScores[j], 200, 60 + j * 19);
        }

        this.mG.drawString("Click to Play", 110, 290);
        this.mG.setFont(this.f);
    }

    private void displayGameOver() {
        this.mG.setColor(new Color(255, 71, 84));
        this.mG.setFont(this.f2);
        this.wid = this.fm.stringWidth("GAME OVER");
        this.mG.drawString("GAME OVER", (300 - this.wid) / 2, 160);
        this.mG.setFont(this.f);
    }

    private void resetLevel() {
        for (int k = 0; k < 30; k++) {
            this.bx[k] = -1;
        }
    }

    private void displayNewHighScore() {
        this.mG.setColor(new Color(255, 71, 84));
        this.mG.drawString("A NEW HIGH SCORE", 0, 50);
        this.mG.drawString("TYPE HERE > ", 0, 110);
        this.mG.drawString("" + this.typestring + "<", 120, 110);
    }

    private void displayLevelComplete() {
        this.mG.setColor(new Color(255, 71, 84));
        this.mG.setFont(this.f2);
        this.wid = this.fm.stringWidth("LEVEL " + (this.level + 1));
        this.mG.drawString("LEVEL " + (this.level + 1), (300 - this.wid) / 2, 160);
        this.mG.setFont(this.f);
    }

    /**
     * Creates or updates an off-screen image buffer for rendering graphics.
     * <p>
     * This method retrieves the current size of the component and stores it in
     * the `nWidth` and `nHeight` fields. If the current size differs from the
     * size of the existing off-screen image buffer (`m_dimImage`), or if the
     * buffer has not been created yet, a new image buffer is created with the
     * current size.
     * <p>
     * The off-screen image buffer is used to render graphics without causing
     * flickering on the screen. The `m_image` field holds the off-screen image,
     * and `m_g` is a graphics object associated with that image, which can be
     * used for drawing operations.
     */
    private void makeBuffer() {
        Dimension dimension = this.getSize();
        this.nWidth = dimension.width;
        this.nHeight = dimension.height;
        if (this.mDimImage == null || this.mDimImage.width != this.nWidth || this.mDimImage.height != this.nHeight) {
            this.mDimImage = new Dimension(this.nWidth, this.nHeight);
            this.mImage = this.createImage(this.nWidth, this.nHeight);
            this.mG = this.mImage.getGraphics();
        }
    }

    public void run() {
        if (!this.bAllLoaded) {
            this.repaint();
            MediaTracker mediaTracker = new MediaTracker(this);
            this.pImages[0] = this.getImage(this.getDocumentBase(), InvadeConstants.PROFILE_IMAGE);
            mediaTracker.addImage(this.pImages[0], 0);
            this.pImages[1] = this.getImage(this.getDocumentBase(), InvadeConstants.PROFILE2_IMAGE);
            mediaTracker.addImage(this.pImages[1], 0);
            this.pImages[2] = this.getImage(this.getDocumentBase(), InvadeConstants.PROFILE3_IMAGE);
            mediaTracker.addImage(this.pImages[2], 0);
            this.pImages[3] = this.getImage(this.getDocumentBase(), InvadeConstants.PROFILE4_IMAGE);
            mediaTracker.addImage(this.pImages[3], 0);
            this.pImages[4] = this.getImage(this.getDocumentBase(), InvadeConstants.FLAKE_IMAGE);
            mediaTracker.addImage(this.pImages[4], 0);
            this.pImages[5] = this.getImage(this.getDocumentBase(), InvadeConstants.BULLET_IMAGE);
            mediaTracker.addImage(this.pImages[5], 0);
            this.pImages[6] = this.getImage(this.getDocumentBase(), InvadeConstants.BADDIE_IMAGE);
            mediaTracker.addImage(this.pImages[6], 0);

            try {
                mediaTracker.waitForAll();
                this.bAllLoaded = !mediaTracker.isErrorAny();
            } catch (InterruptedException ignored) {
                // Empty
            }

            if (!this.bAllLoaded) {
                this.stop();
                return;
            }
        }

        while (true) {
            try {
                this.repaint();
                Thread.sleep(InvadeConstants.SLEEP_DURATION);
            } catch (InterruptedException e) {
                this.stop();
            }
        }
    }

    public void init() {
        this.typestring = "";
        this.totalBaddies = 10;
        this.deadBaddies = 0;
        this.level = 0;
        this.stance = 0;
        int i1 = 0;

        do {
            this.sinx[i1] = InvadeConstants.SIN_TABLE_OFFSET + (short) (Math.sin(this.stance * 3.14159 / 180.0) * InvadeConstants.SIN_TABLE_AMPLITUDE);
            this.stance += InvadeConstants.SIN_TABLE_ANGLE_INCREMENT;
            ++i1;
        } while (i1 <= 40);

        this.stance = 0;
        this.dance = 0;
        this.setSize(300, 320);
        this.sx = InvadeConstants.PLAYER_INITIAL_X;
        this.makeBuffer();
        this.t1 = 44;

        for (this.ly = 0; this.ly < 22; ++this.ly) {
            this.mx[this.ly] = 0;
            this.my[this.ly] = this.ly * 25;
        }

        for (this.ly = 22; this.ly < 50; ++this.ly) {
            this.mx[this.ly] = 87;
            this.my[this.ly] = (this.ly - 22) * 25;
        }

        for (this.i = 0; this.i < 30; ++this.i) {
            this.starX[this.i] = (int) rnd.nextDouble(this.nWidth);
            this.starY[this.i] = (int) rnd.nextDouble(this.nHeight);
            this.bx[this.i] = -1;
            this.by[this.i] = -1;
        }

        for (this.i = 0; this.i < 5; ++this.i) {
            this.baddies[this.i] = 1;
            this.baddiex[this.i] = this.i * InvadeConstants.BADDIE_SPACING + 20;
            this.baddiey[this.i] = InvadeConstants.BADDIE_INITIAL_Y1;
            this.downer[this.i] = 0;
            this.speed[this.i] = rnd.nextInt(3) + 1;
        }

        for (this.i = 5; this.i < InvadeConstants.MAX_BADDIES; ++this.i) {
            this.baddies[this.i] = 1;
            this.baddiex[this.i] = (this.i - 5) * InvadeConstants.BADDIE_SPACING + 15 + 20;
            this.baddiey[this.i] = InvadeConstants.BADDIE_INITIAL_Y2;
            this.downer[this.i] = 0;
            this.speed[this.i] = rnd.nextInt(3) + 1;
        }

        this.loadscores();
        this.mG.setFont(this.f);
    }

    public boolean handleMouseEvent(MouseEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_MOVED) {
            this.sy = evt.getY() - 15;
            this.sx = evt.getX() - InvadeConstants.PLAYER_HEIGHT;

            // Bound checking
            this.sx = Math.max(0, Math.min(this.sx, this.nWidth - 77));
            this.sy = Math.max(InvadeConstants.PLAYER_MIN_Y, Math.min(this.sy, this.nHeight - 30));

            return true;
        } else if (evt.getID() == MouseEvent.MOUSE_PRESSED) {
            handleMousePress();
            return true;
        }
        return false;
    }

    public boolean handleKeyEvent(KeyEvent evt) {
        if (evt.getID() == KeyEvent.KEY_PRESSED) {
            int keyCode = evt.getKeyCode();
            if (keyCode == KeyEvent.VK_ENTER) {
                this.updateHighScores();
                return true;
            } else if (keyCode != KeyEvent.VK_UP && keyCode != KeyEvent.VK_DOWN &&
                    keyCode != KeyEvent.VK_LEFT && keyCode != KeyEvent.VK_RIGHT) {
                handleKeyPress(evt);
                return true;
            }
        }
        return false;
    }

    private void handleMousePress() {
        if (this.gameStatus != InvadeConstants.STATE_NEW_HIGH_SCORE) {
            if (this.gameStatus != InvadeConstants.STATE_PLAYING) {
                initializeGame();
            } else if (this.bx[this.bullet] == -1) {
                fireBullet();
            }
        }
    }

    private void handleKeyPress(KeyEvent evt) {
        char keyChar = evt.getKeyChar();
        int keyCode = evt.getKeyCode();

        if (this.gameStatus == InvadeConstants.STATE_NEW_HIGH_SCORE) {
            if (keyCode == KeyEvent.VK_ENTER) {
                this.updateHighScores();
            } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
                if (!typestring.isEmpty()) {
                    typestring = typestring.substring(0, typestring.length() - 1);
                }
            } else if (keyChar != KeyEvent.CHAR_UNDEFINED && typestring.length() < InvadeConstants.MAX_NAME_LENGTH) {
                if (keyChar == ' ') {
                    keyChar = '_';
                }
                typestring += keyChar;
            }
        }

        this.typedChar = keyChar;
        this.typed = keyCode;
    }

    private void initializeGame() {
        if (this.gameStatus == InvadeConstants.STATE_GAME_OVER) {
            this.score = 0;
            this.level = 0;
            this.gameSpeed = InvadeConstants.INITIAL_GAME_SPEED;
        } else {
            ++this.level;
            if (this.gameSpeed > InvadeConstants.MIN_GAME_SPEED) {
                this.gameSpeed -= 1;
            }
        }

        this.gameStatus = InvadeConstants.STATE_PLAYING;
        initializeBaddies();
    }

    private void initializeBaddies() {
        for (int k = 0; k < InvadeConstants.MAX_BADDIES; ++k) {
            this.baddies[k] = 1;
            this.baddiex[k] = (k % 5) * InvadeConstants.BADDIE_SPACING + 20;
            this.baddiey[k] = (k < 5) ? InvadeConstants.BADDIE_INITIAL_Y1 : InvadeConstants.BADDIE_INITIAL_Y2;
            this.downer[k] = 0;
            this.speed[k] = rnd.nextInt(3) + 1;
        }
        this.deadBaddies = 0;
    }

    private void fireBullet() {
        this.bx[this.bullet] = this.sx + InvadeConstants.PLAYER_HEIGHT;
        this.by[this.bullet] = this.sy - 10;
        ++this.bullet;
        this.bullet %= 30;
    }

    private void updateHighScores() {
        this.highNames[this.newHighScore] = this.typestring;
        this.gameStatus = InvadeConstants.STATE_HIGH_SCORES;
        this.saveScores();
        this.newHighScore = -1;
        this.score = 0;
        this.level = 0;
        this.typestring = "";

        for (this.i = 0; this.i < 30; ++this.i) {
            this.bx[this.i] = -1;
        }
    }

    public void saveScores() {
        String filePath = InvadeConstants.SCORES_FILE;
        Properties scores = new Properties();

        for (int j = 0; j < 10; j++) {
            String key = "score" + j;
            String value = highNames[j] + "=" + highScores[j];
            scores.setProperty(key, value);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            scores.store(writer, "High Scores");
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // unused
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        this.handleKeyEvent(e);
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // unused
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // unused
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        this.handleMouseEvent(e);
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // unused
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // unused
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // un-used
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // un-used
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        this.handleMouseEvent(e);
    }
}