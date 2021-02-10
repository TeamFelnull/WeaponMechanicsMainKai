package me.deecaad.core.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This final utility class consists of static methods that operate on or
 * return numbers. This class also contains methods for randomization, and for
 * translating <i>complicated</i> numbers into more readable forms.
 */
public final class NumberUtils {

    public static final long HOUR_IN_TICKS = 72000;

    // Generally used for enchantments in lore
    private static final TreeMap<Integer, String> NUMERALS;

    // Used to display the amount of time passed
    private static final TreeMap<Integer, String> TIME;

    static {
        NUMERALS = new TreeMap<>();
        NUMERALS.put(1000, "M");
        NUMERALS.put(900, "CM");
        NUMERALS.put(500, "D");
        NUMERALS.put(400, "CD");
        NUMERALS.put(100, "C");
        NUMERALS.put(90, "XC");
        NUMERALS.put(50, "L");
        NUMERALS.put(40, "XL");
        NUMERALS.put(10, "X");
        NUMERALS.put(9, "IX");
        NUMERALS.put(5, "V");
        NUMERALS.put(4, "IV");
        NUMERALS.put(1, "I");

        // Each integer is the number of seconds in the unit.
        TIME = new TreeMap<>();
        TIME.put(31536000, "y");
        TIME.put(86400, "d");
        TIME.put(3600, "h");
        TIME.put(60, "m");
        TIME.put(1, "s");
    }

    // Don't let anyone instanitate this class.
    private NumberUtils() {
    }

    public static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    /**
     * Threadsafe method to generate
     * a random integer [0, length).
     * Useful for getting random elements
     * from collections and arrays.
     *
     * @param length The upper bound
     * @return The random number
     */
    public static int random(int length) {
        return ThreadLocalRandom.current().nextInt(length);
    }

    /**
     * Get a random element in the given array
     *
     * @param arr The array to pull from
     * @param <T> The type of the array
     * @return Random element from the array
     */
    public static <T> T random(T[] arr) {
        return arr[random(arr.length)];
    }

    /**
     * Gets a random element from the given list
     *
     * @param list The list to pull from
     * @param <T>  The type of the array
     * @return Random element from the list
     */
    public static <T> T random(List<T> list) {
        return list.get(random(list.size()));
    }

    /**
     * Threadsafe method to generate
     * a random integer [min, max]
     *
     * @param min minimum size of the number
     * @param max maximum size of the number
     * @return random int between min and max
     */
    public static int random(int min, int max) {
        if (min == max) return min;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Threadsafe method to generate
     * a random double [min, max)
     *
     * @param min minimum size of the number
     * @param max maximum size of the number
     * @return random double between min and max
     */
    public static double random(double min, double max) {
        if (min <= max) return min;
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * Checks if a chance (Which should be a
     * number [0, 1]) was "successful". The
     * behavior of this method will change
     * because of random number comparison
     *
     * @param chance The percentage chance to be successful
     * @return If the chance was successful or not
     */
    public static boolean chance(double chance) {
        if (chance <= 0.0) {
            return false;
        } else if (chance >= 1.0) {
            return true;
        } else {
            return ThreadLocalRandom.current().nextDouble() < chance;
        }
    }

    /**
     * Shorthand for calling both <code>Math.min</code> and
     * <code>Math.max</code>.
     *
     * @param min   The minimum number the value can be
     * @param value The actual value to compare
     * @param max   The maximum number the value can be
     * @return Whichever bound [min, max]
     */
    public static int minMax(int min, int value, int max) {
        if (min > value) {
            return min;
        } else if (max < value) {
            return max;
        } else {
            return value;
        }
    }

    /**
     * Shorthand for calling both <code>Math.min</code> and
     * <code>Math.max</code>.
     *
     * @param min   The minimum number the value can be
     * @param value The actual value to compare
     * @param max   The maximum number the value can be
     * @return Whichever bound [min, max]
     */
    public static float minMax(float min, float value, float max) {
        if (min > value) {
            return min;
        } else if (max < value) {
            return max;
        } else {
            return value;
        }
    }

    /**
     * Shorthand for calling both <code>Math.min</code> and
     * <code>Math.max</code>.
     *
     * @param min   The minimum number the value can be
     * @param value The actual value to compare
     * @param max   The maximum number the value can be
     * @return Whichever bound [min, max]
     */
    public static double minMax(double min, double value, double max) {
        if (min > value) {
            return min;
        } else if (max < value) {
            return max;
        } else {
            return value;
        }
    }

    /**
     * Determines if two doubles are close enough
     * in value to be considered equal. This is
     * important in math with doubles because of
     * inaccuracies with doubles
     *
     * @param a First double
     * @param b Second double
     * @return If they are equal
     */
    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < 1e-10;
    }

    /**
     * Linear interpolation function. Finds a number between <code>min</code>
     * and <code>max</code> using <code>factor</code>. <code>Factor</code> should
     * be a number [0, 1], where values approaching 1 will be closer to the
     * <code>max</code> and values approaching 0 will be closer to the
     * <code>min</code>.
     *
     * If the factor is 0.50, then lerp will return a number exactly between
     * min and max.
     *
     * @param min Minimum value
     * @param max Maximum value
     * @param factor Factor
     * @return Interpolated number
     */
    public static double lerp(double min, double max, double factor) {
        return min + factor * (max - min);
    }

    /**
     * Recursive function that translates an
     * <code>int</code> number to a <code>String
     * </code> roman numeral.
     *
     * @param from Integer to translate
     * @return Roman numeral translation
     */
    public static String toRomanNumeral(int from) {
        int numeral = NUMERALS.floorKey(from);
        if (from == numeral) {
            return NUMERALS.get(from);
        }
        return NUMERALS.get(numeral) + toRomanNumeral(from - numeral);
    }

    /**
     * Recursive function that translates an
     * <code>int</code> amount of seconds into
     * the smallest possible combination of
     * years, days, hours, minutes, and seconds
     *
     * @param seconds The number of seconds
     * @return Simplified number
     */
    public static String toTime(int seconds) {
        int unit = TIME.floorKey(seconds);
        int amount = seconds / unit;
        if (seconds % unit == 0) {
            return amount + TIME.get(unit);
        }
        return amount + TIME.get(unit) + " " + toTime(seconds - amount * unit);
    }

    /**
     * @param lastMillis the last millis something happened
     * @param amount the amount of millis required to pass since last millis
     * @return true only if enough millis have passed since last millis
     */
    public static boolean hasMillisPassed(long lastMillis, long amount) {
        return System.currentTimeMillis() - lastMillis > amount;
    }

    /**
     * Rounds the value to given amount of significands.
     * Will also strip trailing zeros.
     *
     * @param value the version value to be rounded
     * @param significands the amount of significands in return value
     * @return value when rounded to decimals
     */
    public static double getAsRounded(double value, int significands) {
        if (value % 1 == 0) {
            return (int) value;
        }
        int intValue = (int) value;
        BigDecimal bigDecimal = new BigDecimal(value - intValue, new MathContext(significands, RoundingMode.HALF_UP));
        bigDecimal = bigDecimal.add(new BigDecimal(intValue));
        bigDecimal = bigDecimal.stripTrailingZeros();
        return Double.parseDouble(bigDecimal.toPlainString());
    }

    /**
     * Converts the given integer <code>i</code> to binary,
     * then either cuts off or adds onto the binary string
     * based on how many <code>bits</code> are needed
     *
     * @see Integer#toBinaryString(int) 
     * 
     * @param i Integer to translate to binary
     * @param bits How many bits to show
     * @return Binary String
     */
    public static String toBinary(int i, int bits) {
        String binary = Integer.toBinaryString(i);

        // If the binary string contains more bits than defined,
        // then return a substring containing only the asked for bits
        if (binary.length() > bits) {
            return binary.substring(binary.length() - bits);
        }

        // Not enough bits are in the binary string, so we are
        // going to add zeroes until we have the desired amount
        StringBuilder builder = new StringBuilder(bits);
        int bound = bits - binary.length();
        while (builder.length() < bound) {
            builder.append(0);
        }

        return builder.append(binary).toString();
    }
}
