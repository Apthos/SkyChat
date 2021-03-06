package net.apthos.skychat;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class Pixel {

    private static final Set<Character> W2 = ImmutableSet.of
            ('i', '\'', ':', ';', '!', '|', ',', '.');

    private static final Set<Character> W3 = ImmutableSet.of
            ('`', 'l');

    private static final Set<Character> W4 = ImmutableSet.of
            ('I', 't', '[', ']');

    private static final Set<Character> W5 = ImmutableSet.of
            ('f', 'k', '*', '(', ')', '{', '}', '"', '<', '>');

    private static final Set<Character> W6 = ImmutableSet.of
            ('a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'F', 'g', 'G', 'h', 'H', 'j',
            'J', 'K', 'L', 'm', 'M', 'N', 'n', 'O', 'o', 'p', 'P', 'Q', 'q', 'r', 'R', 's',
                    'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x', 'X', 'y' , 'Y', 'z', 'Z', '1',
                    '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '_', '=', '+', '#',
                    '$', '%', '^', '&', '\\', '?');

    public static final int DISPLAY_WIDTH = 320;

    public static int getPixelWidth(char c, boolean bold) {
        int x = 0;
        if (bold) x++;

        if (W2.contains(c)) {
            x += 2;
        } else if (W3.contains(c)) {
            x += 3;
        } else if (W4.contains(c)) {
            x += 4;
        } else if (W5.contains(c)) {
            x += 5;
        } else if (W6.contains(c)) {
            x += 6;
        } else {
            x += 7;
        }
        return x;
    }

}
