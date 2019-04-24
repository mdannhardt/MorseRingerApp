package mike.dannhardt.morseCodeRinger;

/**
 * Created by Mike on 10/3/2015.
 */

public final class Constants {
    public static final String PREFS_NAME = "MorseRingerPrefsFile";
    public static final String PREF_PREAMBLE = "PreambleString";
    public static final String PREF_WPR = "WordsPerMinute";
    public static final String PREF_TONE = "Tone";
    public static final String PREF_ANNC = "AnncRun";

    public static final String DFT_PREABMPLE = "^^ ";
    public static final Integer DFT_WPM = 11;
    public static final Integer DFT_TONE = 700;

    // Defines a custom Intent actions from Setup
    public static final String BROADCAST_ACTION =
            "mike.dannhardt.morseCodeRinger.BROADCAST";

    // Phone state changed
    public static final String PHONE_STATE_CHG =
            "mike.dannhardt.morseCodeRinger.PHONE_STATE";

    // Phone state changed
    public static final String SMS_RCVD =
            "mike.dannhardt.morseCodeRinger.SMS_RCVD";

    // Screen turned ON
    public static final String SCREEN_ON =
            "mike.dannhardt.morseCodeRinger.SCREEN_ON";

    // Screen turned OFF
    public static final String SCREEN_OFF =
            "mike.dannhardt.morseCodeRinger.SCREEN_OFF";

    // Play last SMS message
    public static final String SMS_MSG =
            "mike.dannhardt.morseCodeRinger.SMS_MSG";

    // Defines the keys for the "extra" in Intents
    public static final String SMS_PLAY_ACTION =
            "mike.dannhardt.morseCodeRinger.SMS_PLAY_ACTION";

    public static final String EXTRA_TEST_STRING =
            "mike.dannhardt.morseCodeRinger.TEST_STRING";

    public static final String EXTRA_SET_SPEED =
            "mike.dannhardt.morseCodeRinger.SET_SPEED";

    public static final String EXTRA_SET_TONE =
            "mike.dannhardt.morseCodeRinger.SET_FREQ";

    public static final String EXTRA_SET_PREAMBLE =
            "mike.dannhardt.morseCodeRinger.SET_PREAMBLE";

    public static final String EXTRA_SMS_DATA =
            "mike.dannhardt.morseCodeRinger.EXTRA_SMS_DATA";

    public static final String EXTRA_PHONE_STATE =
            "mike.dannhardt.morseCodeRinger.EXTRA_PHONE_STATE";

    public static final String EXTRA_FROM_NUMBER =
            "mike.dannhardt.morseCodeRinger.EXTRA_FROM_NUMBER";

    public static final String EXTRA_ANNC_START =
            "mike.dannhardt.morseCodeRinger.EXTRA_ANNC_START";
}
