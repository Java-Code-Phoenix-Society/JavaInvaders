/* Originally by David Barton, (c) Mediavault, 1997 */
package dev.jcps;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

import static dev.jcps.InvadeConstants.SCORE_DISPLAY_LENGTH;

/**
 *
 */
public class Invade extends Panel implements JavaAppletAdapter {
    int aaa;
    int t;
    int n;
    String ts;
    String ns;
    int totalBaddies;
    int deadBaddies;
    int baddieDraw;
    int gameSpeed = InvadeConstants.INITIAL_GAME_SPEED;
    String bigString;
    int nS;
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
    int mousey;
    int mousex;
    int omousex;
    int omousey;
    int bullet;
    int ly;
    int sy;
    int dance;
    int score;
    int nWidth;
    int nHeight;
    int ii;
    String textline;
    int iPressed;
    int[] starx;
    int[] stary;
    int[] bx;
    int[] by;
    Random rnd;
    private boolean m_fAllLoaded;
    private int[] sinx = new int[InvadeConstants.SIN_TABLE_SIZE];
    private Graphics m_Graphics;
    private Image[] m_Images = new Image[InvadeConstants.NUM_IMAGES];
    private int i;
    private int[] xs;
    private int[] ys;
    private int[] exs;
    private int[] eys;
    private int[] hxs;
    private int[] hys;
    private int[] baddies;
    private int[] baddiex;
    private int[] baddiey;
    private int[] downer;
    private int[] speed;
    private Image m_image;
    private Graphics m_g;
    private Dimension m_dimImage;

    /**
     *
     */
    public Invade() {
        this.rnd = new Random();
        this.fm = this.getFontMetrics(this.f2);
        this.xs = new int[]{0, 0, 0};
        this.ys = new int[]{10, 90, 170};
        this.exs = new int[]{217, 266, 475, 368, 480, 325};
        this.eys = new int[]{263, 353, 324, 273, 221, 196};
        this.hxs = new int[]{98, 121, 320, 237, 345, 180};
        this.hys = new int[]{160, 287, 222, 195, 131, 102};
        this.baddies = new int[InvadeConstants.MAX_BADDIES];
        this.baddiex = new int[InvadeConstants.MAX_BADDIES];
        this.baddiey = new int[InvadeConstants.MAX_BADDIES];
        this.downer = new int[InvadeConstants.MAX_BADDIES];
        this.speed = new int[InvadeConstants.MAX_BADDIES];
        this.iPressed = -1;
        this.starx = new int[InvadeConstants.MAX_STARS];
        this.stary = new int[InvadeConstants.MAX_STARS];
        this.bx = new int[InvadeConstants.MAX_BULLETS];
        this.by = new int[InvadeConstants.MAX_BULLETS];
    }

    /**
     *
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
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        invade.init();
        invade.run();
    }

    public void stop() {
        //Application.exit
    }

    /**
     *
     * @param g the specified Graphics window
     */
    public void paint(Graphics g) {
        if (this.m_image != null) {
            g.drawImage(this.m_image, 0, 0, null);
        }

    }

    /**
     * The main game loop function
     */
    public void gameloop() {
        int[] stars;
        int it;
        for (this.i = 0; this.i < 30; ++this.i) {
            this.m_g.drawImage(this.m_Images[4], this.starx[this.i], this.stary[this.i], null);
            this.stary[this.i]++;
            stars = this.stary;
            it = this.i;
            stars[it] %= this.nHeight;
            if (this.bx[this.i] != -1) {
                for (this.ii = 0; this.ii < InvadeConstants.MAX_BADDIES; ++this.ii) {
                    if (this.baddies[this.ii] == 1 && this.bx[this.i] >= this.baddiex[this.ii] + this.dance &&
                            this.bx[this.i] <= this.baddiex[this.ii] + InvadeConstants.BADDIE_WIDTH + this.dance &&
                            this.by[this.i] >= this.baddiey[this.ii] + this.downer[this.ii] &&
                            this.by[this.i] <= this.baddiey[this.ii] + InvadeConstants.BADDIE_HEIGHT + this.downer[this.ii]) {
                        this.baddies[this.ii] = 0;
                        this.bx[this.i] = -1;
                        this.score += InvadeConstants.SCORE_PER_BADDIE;
                        ++this.deadBaddies;
                        if (this.deadBaddies == this.totalBaddies) {
                            this.gameStatus = InvadeConstants.STATE_LEVEL_COMPLETE;
                        }
                    }
                }
            }

            if (this.bx[this.i] != -1) {
                this.m_g.drawImage(this.m_Images[5], this.bx[this.i], this.by[this.i], null);
                stars = this.by;
                it = this.i;
                stars[it] -= 4;
                if (this.by[this.i] < 0) {
                    this.bx[this.i] = -1;
                }
            }
        }

        if (this.baddieDraw == 0) {
            this.dance = this.sinx[this.nS];
            ++this.nS;
            this.nS %= InvadeConstants.BADDIE_HEIGHT;
        }

        ++this.baddieDraw;
        this.baddieDraw %= this.gameSpeed;

        for (this.i = 0; this.i < InvadeConstants.MAX_BADDIES; ++this.i) {
            if (this.baddies[this.i] == 1) {
                this.m_g.drawImage(this.m_Images[6], this.baddiex[this.i] + this.dance,
                        this.baddiey[this.i] + this.downer[this.i], null);
                if (this.baddiey[this.i] + this.downer[this.i] > this.nHeight) {
                    this.baddiey[this.i] = InvadeConstants.BADDIE_INITIAL_Y2;
                    this.downer[this.i] = 0;
                    this.speed[this.i] = rnd.nextInt(3) + 1;
                    this.baddiex[this.i] = rnd.nextInt(this.nWidth) + 1;
                }

                if (this.baddiex[this.i] + this.dance > this.sx &&
                        this.baddiex[this.i] + this.dance < this.sx + InvadeConstants.PLAYER_WIDTH &&
                        this.baddiey[this.i] + this.downer[this.i] + InvadeConstants.BADDIE_HEIGHT > this.sy &&
                        this.baddiey[this.i] + this.downer[this.i] +  InvadeConstants.BADDIE_HEIGHT < this.sy + InvadeConstants.PLAYER_HEIGHT &&
                        this.baddies[this.i] != -1) {
                    this.gameStatus = InvadeConstants.STATE_GAME_OVER;
                }

                if (this.baddiex[this.i] + this.dance + 25 > this.sx &&
                        this.baddiex[this.i] + this.dance + 25 < this.sx + InvadeConstants.PLAYER_WIDTH &&
                        this.baddiey[this.i] + this.downer[this.i] + InvadeConstants.BADDIE_HEIGHT  > this.sy &&
                        this.baddiey[this.i] + this.downer[this.i] + InvadeConstants.BADDIE_HEIGHT  < this.sy + InvadeConstants.PLAYER_HEIGHT &&
                        this.baddies[this.i] != -1) {
                    this.gameStatus = InvadeConstants.STATE_GAME_OVER;
                }

                if (this.baddiex[this.i] + this.dance > this.sx &&
                        this.baddiex[this.i] + this.dance < this.sx + InvadeConstants.PLAYER_WIDTH &&
                        this.baddiey[this.i] + this.downer[this.i] > this.sy &&
                        this.baddiey[this.i] + this.downer[this.i] < this.sy + InvadeConstants.PLAYER_HEIGHT &&
                        this.baddies[this.i] != -1) {
                    this.gameStatus = InvadeConstants.STATE_GAME_OVER;
                }

                if (this.baddiex[this.i] + this.dance + 25 > this.sx &&
                        this.baddiex[this.i] + this.dance + 25 < this.sx + InvadeConstants.PLAYER_WIDTH &&
                        this.baddiey[this.i] + this.downer[this.i] > this.sy &&
                        this.baddiey[this.i] + this.downer[this.i] < this.sy + InvadeConstants.PLAYER_HEIGHT &&
                        this.baddies[this.i] != -1) {
                    this.gameStatus = InvadeConstants.STATE_GAME_OVER;
                }
            }

            if (this.baddieDraw == 0) {
                stars = this.downer;
                it = this.i;
                stars[it] += this.speed[this.i];
            }
        }

        this.m_g.drawImage(this.m_Images[this.aaa], this.sx, this.sy, null);
        ++this.aaa;
        this.aaa %= 4;
        this.m_g.setColor(new Color(255, 71, 84));
        this.m_g.drawString(" Score : " + this.score + "    Level : " + this.level, 0, 10);
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
        try {
            // Open a connection to the scores.txt file
            URLConnection connection = (new URL((URL) this.getCodeBase(), InvadeConstants.SCORES_FILE)).openConnection();
            DataInputStream stream = new DataInputStream(connection.getInputStream());

            while ((this.textline = stream.readLine()) != null) {
                // TODO: work out score loading
                this.script.add(this.textline.trim());
            }
        } catch (Exception e) {
            // If an exception occurs, clear the script and add a default message
            this.script.clear();
            this.script.add("Could not retrieve script.");
        }

        // Get the first line of the script as a single string
        this.bigString = this.script.get(0);
        this.script.clear();

        String xscore = "";
        for (int k = 0; k < 10; k++) {
            try {
                // Extract the name from the string (up to 9 characters)
                this.highNames[k] = this.bigString.length() < 15 ? "Your name?" :
                        this.bigString.substring(k * 15, k * 15 + 9);
            } catch (Exception e) {
                this.highNames[k] = "Your name?";
            }
            try {
                // Extract the score from the string (up to 5 digits)
                xscore = this.bigString.substring(k * 15 + 9, k * 15 + 14);
            } catch (Exception e) {
                xscore = "";
            }
            try {
                // Parse the score as an integer
                this.highScores[k] = Integer.parseInt(xscore.trim());
            } catch (NumberFormatException e) {
                this.highScores[k] = 0;
            }
        }

    }

    /**
     *
     * @param g the specified Graphics window
     */
    @Override
    public void update(Graphics g) {
        this.m_g.setColor(new Color(15, 23, 52));
        this.m_g.fillRect(0, 0, this.nWidth, this.nHeight);
        if (!this.m_fAllLoaded) {
            this.m_g.setColor(new Color(255, 71, 84));
            this.m_g.setFont(this.f2);
            this.m_g.drawString("Loading graphics...", 20, 20);
        } else {
            label59:
            switch (this.gameStatus) {
                case InvadeConstants.STATE_PLAYING:
                    this.gameloop();
                    break;
                case InvadeConstants.STATE_GAME_OVER:
                    this.loadscores();

                    for (this.i = 0; this.i < 10; ++this.i) {
                        if (this.score > this.highScores[this.i]) {
                            this.newHighScore = this.i;
                            this.t = this.highScores[this.i];
                            this.ts = this.highNames[this.i];
                            this.highScores[this.i] = this.score;
                            ++this.i;

                            while (this.i < 10) {
                                this.n = this.highScores[this.i];
                                this.ns = this.highNames[this.i];
                                this.highScores[this.i] = this.t;
                                this.highNames[this.i] = this.ts;
                                this.t = this.n;
                                this.ts = this.ns;
                                ++this.i;
                            }

                            this.gameStatus = InvadeConstants.STATE_NEW_HIGH_SCORE;
                        }
                    }

                    if (this.gameStatus != InvadeConstants.STATE_NEW_HIGH_SCORE) {
                        this.m_g.setColor(new Color(255, 71, 84));
                        this.m_g.setFont(this.f2);
                        this.wid = this.fm.stringWidth("GAME OVER");
                        this.m_g.drawString("GAME OVER", (300 - this.wid) / 2, 160);
                        this.m_g.setFont(this.f);
                        this.gameSpeed = InvadeConstants.INITIAL_GAME_SPEED;
                    }
                    break;
                case InvadeConstants.STATE_LEVEL_COMPLETE:
                    this.m_g.setColor(new Color(255, 71, 84));
                    this.m_g.setFont(this.f2);
                    this.wid = this.fm.stringWidth("LEVEL " + (this.level + 1));
                    this.m_g.drawString("LEVEL " + (this.level + 1), (300 - this.wid) / 2, 160);
                    this.m_g.setFont(this.f);
                    this.i = 0;

                    while (true) {
                        if (this.i >= 30) {
                            break label59;
                        }

                        this.bx[this.i] = -1;
                        ++this.i;
                    }
                case InvadeConstants.STATE_NEW_HIGH_SCORE:
                    this.m_g.setColor(new Color(255, 71, 84));
                    this.m_g.drawString("A NEW HIGH SCORE", 0, 50);
                    this.m_g.drawString("TYPE HERE > ", 0, 110);
                    this.m_g.drawString(" " + this.typestring + "<", 120, 110);
                    break;
                case InvadeConstants.STATE_HIGH_SCORES:
                    this.m_g.setColor(new Color(255, 71, 84));
                    this.m_g.setFont(this.f2);
                    this.m_g.drawString("HIGH SCORES", 100, 20);
                    int counter = 0;

                    do {
                        if (counter < 9) {
                            this.m_g.drawString(" " + (counter + 1) + "  -  " + this.highNames[counter],
                                    80, 60 + counter * 19);
                        } else {
                            this.m_g.drawString(counter + 1 + "  -  " + this.highNames[counter],
                                    75, 60 + counter * 19);
                        }

                        this.m_g.drawString(" - " + this.highScores[counter], 200, 60 + counter * 19);
                        ++counter;
                    } while (counter < 10);

                    this.m_g.drawString("Click to Play", 110, 290);
                    this.m_g.setFont(this.f);
            }
        }

        this.paint(g);
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
        if (this.m_dimImage == null || this.m_dimImage.width != this.nWidth || this.m_dimImage.height != this.nHeight) {
            this.m_dimImage = new Dimension(this.nWidth, this.nHeight);
            this.m_image = this.createImage(this.nWidth, this.nHeight);
            this.m_g = this.m_image.getGraphics();
        }
    }

    public void run() {
        if (!this.m_fAllLoaded) {
            this.repaint();
            this.m_Graphics = this.getGraphics();
            MediaTracker mediaTracker = new MediaTracker(this);
            this.m_Images[0] = this.getImage(this.getDocumentBase(), InvadeConstants.PROFILE_IMAGE);
            mediaTracker.addImage(this.m_Images[0], 0);
            this.m_Images[1] = this.getImage(this.getDocumentBase(), InvadeConstants.PROFILE2_IMAGE);
            mediaTracker.addImage(this.m_Images[1], 0);
            this.m_Images[2] = this.getImage(this.getDocumentBase(), InvadeConstants.PROFILE3_IMAGE);
            mediaTracker.addImage(this.m_Images[2], 0);
            this.m_Images[3] = this.getImage(this.getDocumentBase(), InvadeConstants.PROFILE4_IMAGE);
            mediaTracker.addImage(this.m_Images[3], 0);
            this.m_Images[4] = this.getImage(this.getDocumentBase(), InvadeConstants.FLAKE_IMAGE);
            mediaTracker.addImage(this.m_Images[4], 0);
            this.m_Images[5] = this.getImage(this.getDocumentBase(), InvadeConstants.BULLET_IMAGE);
            mediaTracker.addImage(this.m_Images[5], 0);
            this.m_Images[6] = this.getImage(this.getDocumentBase(), InvadeConstants.BADDIE_IMAGE);
            mediaTracker.addImage(this.m_Images[6], 0);

            try {
                mediaTracker.waitForAll();
                this.m_fAllLoaded = !mediaTracker.isErrorAny();
            } catch (InterruptedException ignored) {
                // Empty
            }

            if (!this.m_fAllLoaded) {
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
        this.nS = 0;
        int i1 = 0;

        do {
            this.sinx[i1] = InvadeConstants.SIN_TABLE_OFFSET + (short) (Math.sin(this.nS * 3.14159 / 180.0) * InvadeConstants.SIN_TABLE_AMPLITUDE);
            this.nS += InvadeConstants.SIN_TABLE_ANGLE_INCREMENT;
            ++i1;
        } while (i1 <= 40);

        this.nS = 0;
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
            this.starx[this.i] = (int) rnd.nextDouble(this.nWidth);
            this.stary[this.i] = (int) rnd.nextDouble(this.nHeight);
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
        this.m_g.setFont(this.f);
    }

    public boolean handleEvent(Event evt) {
        if (evt.id == 503) {
            this.mousex = evt.x;
            this.sy = evt.y - 15;
            this.sx = this.mousex - InvadeConstants.PLAYER_HEIGHT;
            if (this.sx < 0) {
                this.sx = 0;
            }

            if (this.sx > this.nWidth - 77) {
                this.sx = this.nWidth - 77;
            }

            if (this.sy > this.nHeight - 30) {
                this.sy = this.nHeight - 30;
            }

            if (this.sy < InvadeConstants.PLAYER_MIN_Y) {
                this.sy = InvadeConstants.PLAYER_MIN_Y;
            }

            return true;
        } else if (evt.id == 501) {
            if (this.gameStatus != InvadeConstants.STATE_NEW_HIGH_SCORE) {
                if (this.gameStatus != InvadeConstants.STATE_PLAYING) {
                    if (this.gameStatus == InvadeConstants.STATE_GAME_OVER) {
                        this.score = 0;
                        this.level = 0;
                    } else {
                        ++this.level;
                        if (this.gameSpeed > InvadeConstants.MIN_GAME_SPEED) {
                            this.gameSpeed += -1;
                        }
                    }

                    this.gameStatus = InvadeConstants.STATE_PLAYING;

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

                    this.deadBaddies = 0;
                } else if (this.bx[this.bullet] == -1) {
                    this.bx[this.bullet] = this.sx + InvadeConstants.PLAYER_HEIGHT;
                    this.by[this.bullet] = this.sy - 10;
                    ++this.bullet;
                    this.bullet %= 30;
                }

            }
            return true;
        } else if (evt.id != 401 && evt.id != 1005 && evt.id != 1004 && evt.id != 1006 && evt.id != 1007) {
            return true;
        } else if (evt.key != 10) {
            if (evt.key < 1000 && evt.key != 0) {
                this.typedChar = (char) evt.key;
                this.typed = evt.key;
                if (evt.key == 8) {
                    int len = this.typestring.length() - 1;
                    if (len >= 0) {
                        this.typestring = this.typestring.substring(0, len);
                    }
                } else {
                    if (evt.key == 32) {
                        evt.key = 65;
                    }

                    if (this.typestring.length() < InvadeConstants.MAX_NAME_LENGTH) {
                        this.typestring = this.typestring + (char) evt.key;
                    }
                }
            }

            return true;
        } else {
            this.highNames[this.newHighScore] = this.typestring;
            this.gameStatus = InvadeConstants.STATE_HIGH_SCORES;
            this.bigString = "";
            this.ii = 1;

            for (this.i = 0; this.i < 10; ++this.i) {
                String scoresText = this.highNames[this.i] + "---------";
                int sl;
                String cName = scoresText.substring(0, InvadeConstants.MAX_NAME_LENGTH);
                this.bigString = this.bigString + cName;
                scoresText = "00000" + this.highScores[this.i];
                sl = scoresText.length();
                cName = scoresText.substring(sl - SCORE_DISPLAY_LENGTH,
                        sl - SCORE_DISPLAY_LENGTH + SCORE_DISPLAY_LENGTH);
                this.bigString = this.bigString + cName + this.ii;
            }

            this.newHighScore = 99;
            this.score = 0;
            this.level = 0;
            this.typestring = "";

            for (this.i = 0; this.i < 30; ++this.i) {
                this.bx[this.i] = -1;
            }

            return true;
        }
    }
}