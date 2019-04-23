package mike.dannhardt.morseCodeRinger;

/**
 * Created by Mike on 10/3/2015.
 */

import java.util.HashMap;

/** Class that implements the text to morse code conversion */
class MorseConverter {

    private static final HashMap<Character, MorseBit[]> morse_map = new HashMap<Character, MorseBit[]>(){
        private static final long serialVersionUID = 1L;

        {
            put('A', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA});
            put('B', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('C', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DIT});
            put('D', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT});
            put('E', new MorseBit[] { MorseBit.DIT});
            put('F', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DIT});
            put('G', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT});
            put('H', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('I', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('J', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DA});
            put('K', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA});
            put('L', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('M', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA});
            put('N', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT});
            put('O', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DA});
            put('P', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DIT});
            put('Q', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DA});
            put('R', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT});
            put('S', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT});
            put('T', new MorseBit[] { MorseBit.DA});
            put('U', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA});
            put('V', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DA});
            put('W', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DA});
            put('X', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DA});
            put('Y', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DA});
            put('Z', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('0', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DA});
            put('1',new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DA});
            put('2', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DA});
            put('3', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DA});
            put('4', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP, MorseBit.DA});
            put('5', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('6', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('7', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('8',new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP, MorseBit.DIT});
            put('9',new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DIT});
            put('/', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DIT});
            put('.', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DIT,
                    MorseBit.GAP, MorseBit.DA});
            put(',', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT,MorseBit.GAP, MorseBit.DA,
                    MorseBit.GAP, MorseBit.DA});
            put('?', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DIT,
                    MorseBit.GAP, MorseBit.DIT});
            //BT
            put('=', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP, MorseBit.DA});
            put('-', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP, MorseBit.DA});
            //AR
            put('+', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DIT});
            //KN
            put('~', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DIT});
            //SK
            put('*', new MorseBit[] { MorseBit.DIT, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DIT, MorseBit.GAP, MorseBit.DA, MorseBit.GAP, MorseBit.DIT,
                    MorseBit.GAP, MorseBit.DA});
            //CT
            put('^', new MorseBit[] { MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP,
                    MorseBit.DA, MorseBit.GAP, MorseBit.DIT, MorseBit.GAP, MorseBit.DA});
        }
    };

    private static final MorseBit[] ERROR_GAP = new MorseBit[] { MorseBit.GAP };

    /** Return the pattern data for a given character */
    static MorseBit[] pattern(char c) {
        if (Character.isLetter(c))
            c = Character.toUpperCase(c);
        if (morse_map.containsKey(c))
            return morse_map.get(c);
        else
            return ERROR_GAP;
    }

    static MorseBit[] pattern(String str) {
        boolean lastWasWhitespace;
        int strlen = str.length();

        // Calculate how MorseBit our array needs to be.
        int len = 1;
        lastWasWhitespace = true;
        for (int i=0; i<strlen; i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!lastWasWhitespace) {
                    len++;
                    lastWasWhitespace = true;
                }
            } else {
                if (!lastWasWhitespace) {
                    len++;
                }
                lastWasWhitespace = false;
                len += pattern(c).length;
            }
        }

        // Generate the pattern array.  Note that we put an extra element of 0
        // in at the beginning, because the pattern always starts with the pause,
        // not with the vibration.
        MorseBit[] result = new MorseBit[len];
        result[0] = MorseBit.GAP;
        int pos = 1;
        lastWasWhitespace = true;
        for (int i=0; i<strlen; i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!lastWasWhitespace) {
                    result[pos] = MorseBit.WORD_GAP;
                    pos++;
                    lastWasWhitespace = true;
                }
            } else {
                if (!lastWasWhitespace) {
                    result[pos] = MorseBit.LETTER_GAP;
                    pos++;
                }
                lastWasWhitespace = false;
                MorseBit[] letter = pattern(c);
                System.arraycopy(letter, 0, result, pos, letter.length);
                pos += letter.length;
            }
        }
        return result;
    }
}