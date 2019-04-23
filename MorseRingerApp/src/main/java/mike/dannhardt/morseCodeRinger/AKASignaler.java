package mike.dannhardt.morseCodeRinger;

import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by Mike on 10/3/2015.
 */
public class AKASignaler {
    private static final AKASignaler instance = new AKASignaler();
    public AudioTrack audioTrack = null;  //does this need to be public?

    private String TAG = "AKASignaler";

    private AKASignaler() {
    }

    public static AKASignaler getInstance() {
        return instance;
    }

    public void killAudioTrack() {
        if (audioTrack != null) {
            Log.i(TAG, "stopping all sound and releasing audioTrack resources...");
            audioTrack.stop();
            audioTrack.flush(); //flush is necessary even though audiotrack is released
            //on next line. Without this, the message keeps playing.
            audioTrack.release();  // release underlying audio resources
            audioTrack = null;  // release object for garbage collection
        }
        else {
            Log.i(TAG, "Null audioTrack, nothing to kill off. What a pity.");
        }

    }
}
