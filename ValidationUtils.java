package src;

import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.JTextField;
import java.util.function.Predicate;

public class ValidationUtils {

    public static final Color ERROR_COLOR = new Color(255, 150, 150);
    public static final Color DEFAULT_COLOR = UIManager.getColor("TextField.background");

    // Validate a text field using a predicate
    public static boolean validateTextField(JTextField field, Predicate<String> validator) {
        String text = field.getText().trim();
        boolean isValid = validator.test(text);
        if (!isValid) {
            field.setBackground(ERROR_COLOR);
        } else {
            field.setBackground(DEFAULT_COLOR);
        }
        return isValid;
    }

    // Validate a combo box
    public static boolean validateComboBox(JComboBox<?> combo, int invalidIndex) {
        boolean isValid = combo.getSelectedIndex() != invalidIndex;
        if (!isValid) {
            combo.setBackground(ERROR_COLOR);
        } else {
            combo.setBackground(DEFAULT_COLOR);
        }
        return isValid;
    }
}
