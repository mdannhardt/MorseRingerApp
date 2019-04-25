package mike.dannhardt.morseCodeRinger;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Mike on 10/3/2015.
 */

/**
* Listener to detect incoming calls.
*/

public class MorsePlayer implements Runnable {


    private Context m_context;
    private AudioManager am;

    private String TAG = "MorsePlayer";
    private final int SAMPLE_RATE = 8000;
    private int wpmSpeed;
    private int toneHertz;
    private byte[] ditSnd;
    private byte[] dahSnd;
    private byte[] pauseInnerSnd;
    private AKASignaler signaler = AKASignaler.getInstance();
    private String currentMessage;  // message to play in morse
    private String preambleMessage;
    private boolean playCallerId;
    private int playMessageLoopCnt;
    private boolean flgRun;
    private boolean announceOnRun;
    private boolean screenOn = true;
    private String lastSmsMsg;

    // Constructor: prepare to play morse code at SPEED wpm and HERTZ frequency
    public MorsePlayer(Context context, String preamble, int speed, int hertz) {
        Log.i(TAG, "MorsePlayer()");
        preambleMessage = "^^ ";
        m_context = context;
        setPreambleMessage(preamble);
        setSpeed(speed);
        setTone(hertz);
        buildSounds();

        am = (AudioManager) m_context.getSystemService(Context.AUDIO_SERVICE);

        IntentFilter filter = new IntentFilter(Constants.BROADCAST_ACTION);
        filter.addAction(Constants.PHONE_STATE_CHG);
        filter.addAction(Constants.SMS_RCVD);
        filter.addAction(Constants.SMS_MSG);
        filter.addAction(Constants.SCREEN_OFF);
        filter.addAction(Constants.SCREEN_ON);
        LocalBroadcastManager.getInstance(m_context).registerReceiver(mMessageReceiver, filter);
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message;
            int val;
            switch (intent.getAction()) {
                case Constants.SCREEN_OFF:
                    screenOn = false;
                    break;
                case Constants.SCREEN_ON:
                    screenOn = true;
                    playMessageLoopCnt = 0;
                    break;
                case Constants.PHONE_STATE_CHG:
                    int ringState = intent.getIntExtra(Constants.EXTRA_PHONE_STATE, 0);
                    String incomingNumber = intent.getStringExtra(Constants.EXTRA_FROM_NUMBER);
                    switch (ringState) {
                        case TelephonyManager.CALL_STATE_RINGING:
                            // called when someone is ringing to this phone
                            Log.i(TAG, "oMorsePlayer: onReceive(ring) Caller: " + incomingNumber);
                            String caller = contactNameByPhoneNumber(m_context, incomingNumber);
                            playCallerId = true;
                            playMessageLoopCnt = 2;
                            setMessage("de " + caller);
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        default:
                            Log.i(TAG, "MorsePlayer: onReceive(ringState) Terminate!: " + ringState);
                            playCallerId = false;
                            break;
                    }
                    break;
                case Constants.SMS_RCVD:
                    String smsFrom = intent.getStringExtra(Constants.EXTRA_FROM_NUMBER);
                    Log.i(TAG, "MorsePlayer: onReceive(sms)");
                    String caller = contactNameByPhoneNumber(m_context, smsFrom);

                    if (!screenOn) {
                        playMessageLoopCnt = 2;
                        setMessage("SMS de " + caller);
                    }

                    lastSmsMsg = "de " + caller + " - ";
                    lastSmsMsg += intent.getStringExtra(Constants.EXTRA_SMS_DATA);
                    lastSmsMsg += " - k";

                    break;
                case Constants.SMS_MSG:
                    String action = intent.getStringExtra(Constants.SMS_PLAY_ACTION);
                    if (action.equals("stop"))
                        playMessageLoopCnt = 0;
                    else if (action.equals("play")) {
                        playMessageLoopCnt = 1;
                        if (lastSmsMsg != null && lastSmsMsg.length() > 0)
                            setMessage(lastSmsMsg);
                        else
                            setMessage("No SMS MSG");
                    }
                    break;
                case Constants.BROADCAST_ACTION:    // Intents from SETUP
                    if (intent.hasExtra(Constants.EXTRA_TEST_STRING)) {
                        message = intent.getStringExtra(Constants.EXTRA_TEST_STRING);
                        if (message != null && message.length() > 0) {
                            Log.i(TAG, "MorsePlayer: onReceive(test)");
                            playMessageLoopCnt = 1;
                            setMessage(message);
                        }
                    }
                    if (intent.hasExtra(Constants.EXTRA_SET_PREAMBLE)) {
                        message = intent.getStringExtra(Constants.EXTRA_SET_PREAMBLE);
                        if (message != null && message.length() > 0) {
                            Log.i(TAG, "MorsePlayer: onReceive(preamble)");
                            setPreambleMessage(message);
                        }
                    }
                    if (intent.hasExtra(Constants.EXTRA_SET_SPEED)) {
                        val = intent.getIntExtra(Constants.EXTRA_SET_SPEED, Constants.DFT_WPM);
                        Log.i(TAG, "MorsePlayer: onReceive(speed)");
                        setSpeed(val);
                        buildSounds();
                    }
                    if (intent.hasExtra(Constants.EXTRA_SET_TONE)) {
                        val = intent.getIntExtra(Constants.EXTRA_SET_TONE, Constants.DFT_TONE);
                        Log.i(TAG, "MorsePlayer: onReceive(tone)");
                        setTone(val);
                        buildSounds();
                    }
                    if (intent.hasExtra(Constants.EXTRA_ANNC_START)) {
                        announceOnRun = intent.getBooleanExtra(Constants.EXTRA_ANNC_START, true);
                        Log.i(TAG, "MorsePlayer: onReceive(annc start)");
                    }
                    break;
                default:
            }
        }
    };

    @Override
    public void run() {
        Log.i(TAG, "run()");

        if ( announceOnRun ) {
            setMessage("de ka4cdn");
            playMessageLoopCnt = 1;
            playMorse();
        }

        flgRun = true;
        while (flgRun) {
            // look for an incoming phone call
            try {
                if (currentMessage.length()>0)
                    playMorse();
                Thread.sleep(1000);
            } catch (Exception ex ) {
                Log.i(TAG, "MorsePlayer.run() exception: " + ex.getMessage());
                currentMessage = "";
            }
        }
        Log.i(TAG, "stop()");
        setMessage("73 * i");
        playMessageLoopCnt = 1;
        playMorse();
    }

    public void stop() {
        flgRun = false;
    }

    private String contactNameByPhoneNumber(Context c, String phoneNumber) {
        ContentResolver cr = c.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor;
        String contactName = phoneNumber;
        try {
            cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if ( cursor != null ) {
                if(cursor.moveToFirst())
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

                if(!cursor.isClosed())
                    cursor.close();
            }
        }
        catch (Exception ex) {
            String error = ex.getMessage();
        }

        return contactName;
    }

    // Generate 'dit','dah' and empty sinewave tones of the proper lengths.
    private void buildSounds() {
        // where (1200 / wpm) = element length in milliseconds
        double duration = (double) ((1200 / wpmSpeed) * .001);
        int numSamples = (int) (duration * SAMPLE_RATE - 1);
        double sineMagnitude = 1; // starting with a dummy value for absolute normalized value of sine wave
        double CUTOFF = 0.001; // threshold for whether sine wave is near zero crossing
        double phaseAngle = 2 * Math.PI / (SAMPLE_RATE/toneHertz);
        while (sineMagnitude > CUTOFF){
            numSamples++;
            //check to see if  is near zero-crossing to avoid clicks when sound cuts off
            sineMagnitude = Math.abs(Math.sin(phaseAngle* numSamples));
        }
        double[] sample = new double[numSamples];
        ditSnd = new byte[2 * numSamples];
        dahSnd = new byte[6 * numSamples];
        pauseInnerSnd = new byte[2 * numSamples];

        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(phaseAngle * i);
        }
        // convert to 16 bit pcm sound array; assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            final short val = (short) ((dVal * 32767)); // scale to maximum amplitude
            // in 16 bit wav PCM, first byte is the low order byte
            ditSnd[idx++] = (byte) (val & 0x00ff);
            ditSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        for (int i = 0; i < (dahSnd.length); i++) {
            dahSnd[i] = ditSnd[i % ditSnd.length];
        }
        for (int i = 0; i < (pauseInnerSnd.length); i++) {
            pauseInnerSnd[i] = 0;
        }
    }

    public void setMessage(String message) {
        currentMessage = message;
    }

    public void setPreambleMessage(String preamble) {
        preambleMessage = preamble;
    }

    private void setSpeed(int speed) {
        wpmSpeed = speed;
    }

    private void setTone(int hertz) {
        toneHertz = hertz;
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
        // Pause playback
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
            am.abandonAudioFocus(afChangeListener);
            // Stop playback
        }
        }
    };

    // The main method of this class; runs exactly once in a standalone thread.
    public void playMorse() {
        Log.i(TAG, "Now playing morse code...");
        MorseBit[] callerIdPattern = MorseConverter.pattern(currentMessage);
        MorseBit[] preamblePattern = MorseConverter.pattern(preambleMessage);

        int msgSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        try {
            // Create an audioTrack
            signaler.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    msgSize, AudioTrack.MODE_STREAM);

            // Request transient audio focus for playback
            am.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            // Start the playback
            signaler.audioTrack.play();

            // Fill the audiotrack's buffer.
            for (MorseBit bit : preamblePattern)
                writeBit( bit); // blocking

            // Now the caller name or message. Play until no longer ringing OR
            // the set play count (for example SMS announcements).
            while (playCallerId || playMessageLoopCnt > 0) {
                for (MorseBit bit : callerIdPattern) {
                    if (!(playCallerId || playMessageLoopCnt > 0) )
                        break;
                    writeBit( bit); // blocking
                }

                // Word space after playing caller ID
                writeBit(MorseBit.WORD_GAP);

                playMessageLoopCnt--;
                Log.i(TAG, "All data pushed to audio buffer; thread quitting.");

                // Signal back to the SetupIntent screen to change Play Last SMS to STOP Last SMS
                Intent localIntent = new Intent(Constants.SMS_MSG);
                localIntent.putExtra(Constants.SMS_PLAY_ACTION, "finished");
                LocalBroadcastManager.getInstance(m_context).sendBroadcast(localIntent);
            }
        } catch (Exception ex ) {

        }

        // Abandon audio focus when playback complete
        signaler.killAudioTrack();
        am.abandonAudioFocus(afChangeListener);
        currentMessage = "";
    }

    private void writeBit(MorseBit bit) {
        switch (bit) {
            case GAP:
                signaler.audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length);
                break;
            case DIT:
                signaler.audioTrack.write(ditSnd, 0, ditSnd.length);
                break;
            case DA:
                signaler.audioTrack.write(dahSnd, 0, dahSnd.length);
                break;
            case LETTER_GAP:
                for (int i = 0; i < 3; i++)
                    signaler.audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length);
                break;
            case WORD_GAP:
                for (int i = 0; i < 7; i++)
                    signaler.audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length);
                break;
            default:
                break;
        }
    }
}

