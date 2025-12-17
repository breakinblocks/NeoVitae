package com.breakinblocks.neovitae.incense;

/**
 * Handles calculation of incense bonuses based on road distance and tranquility.
 */
public class IncenseAltarHandler {
    // Incense bonus maximum applied for each tier of roads
    public static final double[] INCENSE_BONUSES = new double[]{0.2, 0.6, 1.2, 2.0, 3.0, 4.5};

    // Tranquility required for each bonus tier
    public static final double[] TRANQUILITY_REQUIRED = new double[]{0, 6, 14.14, 28, 44.09, 83.14};

    // Number of road rings required for each tier
    public static final int[] ROADS_REQUIRED = new int[]{0, 1, 4, 6, 8, 10, 12};

    /**
     * Gets the maximum incense bonus possible based on the number of road rings.
     *
     * @param roads Number of complete road rings around the altar
     * @return Maximum possible bonus from roads
     */
    public static double getMaxIncenseBonusFromRoads(int roads) {
        double previousBonus = 0;
        for (int i = 0; i < INCENSE_BONUSES.length; i++) {
            if (roads >= ROADS_REQUIRED[i]) {
                previousBonus = INCENSE_BONUSES[i];
            } else {
                return previousBonus;
            }
        }
        return previousBonus;
    }

    /**
     * Calculates the actual incense bonus based on tranquility and road distance.
     *
     * @param tranquility The calculated tranquility value
     * @param roads       Number of complete road rings
     * @return The incense bonus multiplier
     */
    public static double getIncenseBonus(double tranquility, int roads) {
        double maxBonus = getMaxIncenseBonusFromRoads(roads);
        double possibleBonus = 0;

        for (int i = 0; i < INCENSE_BONUSES.length; i++) {
            if (tranquility >= TRANQUILITY_REQUIRED[i]) {
                possibleBonus = INCENSE_BONUSES[i];
            } else if (i >= 1) {
                // Interpolate between tiers
                double prevRequired = TRANQUILITY_REQUIRED[i - 1];
                double currRequired = TRANQUILITY_REQUIRED[i];
                double prevBonus = INCENSE_BONUSES[i - 1];
                double currBonus = INCENSE_BONUSES[i];

                possibleBonus = prevBonus + (currBonus - prevBonus) * (tranquility - prevRequired) / (currRequired - prevRequired);
                break;
            }
        }

        return Math.min(maxBonus, possibleBonus);
    }
}
