package dev.jcps;

/**
 * This class contains constant values used in the Invade game.
 * It eliminates magic numbers and improves code readability and maintainability.
 */
public final class InvadeConstants {
    // Prevent instantiation
    private InvadeConstants() {}

    // Game window dimensions
    public static final int WINDOW_WIDTH = 300;
    public static final int WINDOW_HEIGHT = 320;

    // Number of images
    public static final int NUM_IMAGES = 8;

    // Font sizes
    public static final int FONT_SIZE_NORMAL = 13;
    public static final int FONT_SIZE_LARGE = 16;

    // Game elements counts
    public static final int MAX_BADDIES = 10;
    public static final int MAX_STARS = 30;
    public static final int MAX_BULLETS = 30;
    public static final int MAX_HIGH_SCORES = 10;

    // Player related constants
    public static final int PLAYER_WIDTH = 63;
    public static final int PLAYER_HEIGHT = 35;
    public static final int PLAYER_INITIAL_X = 127;
    public static final int PLAYER_MIN_Y = 170;

    // Baddie related constants
    public static final int BADDIE_WIDTH = 25;
    public static final int BADDIE_HEIGHT = 41;
    public static final int BADDIE_SPACING = 55;
    public static final int BADDIE_INITIAL_Y1 = -90;
    public static final int BADDIE_INITIAL_Y2 = -30;

    // Game speed related constants
    public static final int INITIAL_GAME_SPEED = 4;
    public static final int MIN_GAME_SPEED = 1;
    public static final int SLEEP_DURATION = 5;

    // Scoring related constants
    public static final int SCORE_PER_BADDIE = 10;

    // Sin table related constants
    public static final int SIN_TABLE_SIZE = 41;
    public static final int SIN_TABLE_ANGLE_INCREMENT = 9;
    public static final int SIN_TABLE_AMPLITUDE = 11;
    public static final int SIN_TABLE_OFFSET = 10;

    // High score related constants
    public static final int MAX_NAME_LENGTH = 9;
    public static final int SCORE_DISPLAY_LENGTH = 5;

    // File names
    public static final String SCORES_FILE = "scores.txt";
    public static final String PROFILE_IMAGE = "profile.gif";
    public static final String PROFILE2_IMAGE = "profile2.gif";
    public static final String PROFILE3_IMAGE = "profile3.gif";
    public static final String PROFILE4_IMAGE = "profile4.gif";
    public static final String FLAKE_IMAGE = "flake.jpg";
    public static final String BULLET_IMAGE = "bull.gif";
    public static final String BADDIE_IMAGE = "bad1.gif";

    // Game states
    public static final int STATE_PLAYING = 0;
    public static final int STATE_GAME_OVER = 1;
    public static final int STATE_LEVEL_COMPLETE = 2;
    public static final int STATE_NEW_HIGH_SCORE = 3;
    public static final int STATE_HIGH_SCORES = 4;
}