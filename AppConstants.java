package src;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

public class AppConstants {
    // Text field and document limits
    public static final int TEXT_FIELD_COLUMNS = 20;
    public static final int PPS_FIELD_LIMIT = 9;  // for PPS Number field
    // Font constants
    public static final String DEFAULT_FONT_NAME = "SansSerif";
    public static final int DEFAULT_FONT_STYLE = Font.BOLD;
    public static final int DEFAULT_FONT_SIZE = 16;
    public static final Font DEFAULT_FONT = new Font(DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);
    
    // Colors
    public static final Color ERROR_COLOR = new Color(255, 150, 150);
    public static final Color DEFAULT_COLOR = UIManager.getColor("TextField.background");
    
    // Error messages
    public static final String WRONG_FORMAT_MESSAGE = "Wrong values or format! Please check!";
    
    // Default file name and file extension
    public static final String DEFAULT_FILENAME = "new_Employee.dat";
    public static final String DAT_EXTENSION = ".dat";
    
    // Frame dimensions and positions
    public static final int FRAME_WIDTH = 760;
    public static final int FRAME_HEIGHT = 600;
    public static final int FRAME_X_POSITION = 250;
    public static final int FRAME_Y_POSITION = 200;
    
}
