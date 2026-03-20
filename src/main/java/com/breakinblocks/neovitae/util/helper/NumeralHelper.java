package com.breakinblocks.neovitae.util.helper;

public class NumeralHelper {

    private static final String[] ROMAN_NUMERALS = {
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    };

    public static String toRoman(int number) {
        if (number < 1 || number > 10) {
            return String.valueOf(number);
        }
        return ROMAN_NUMERALS[number - 1];
    }
}
