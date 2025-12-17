package com.breakinblocks.neovitae.util.helper;

/**
 * Utility class for converting numbers to Roman numerals.
 */
public class NumeralHelper {

    private static final String[] ROMAN_NUMERALS = {
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    };

    /**
     * Converts a number to a Roman numeral string.
     *
     * @param number The number to convert (1-10 supported)
     * @return The Roman numeral string
     */
    public static String toRoman(int number) {
        if (number < 1 || number > 10) {
            return String.valueOf(number);
        }
        return ROMAN_NUMERALS[number - 1];
    }
}
