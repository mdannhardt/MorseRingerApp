package mike.dannhardt.morseCodeRinger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Mike on 10/4/2015.
 */
public class CallDetectService extends Service {
    private MorsePlayer m_MorsePlayer;

    private SensorManager mSensorManager;
    private long gestureStartTime = 0;
    private int gestureCnt = 0;

    // Watch for the user to gesture to play the last message.
    // Gesture is from level to rolled three times. ( flat, rolled x three times)
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent se) {
            float pitch = se.values[1];
            float roll = se.values[2];

            long deltaTime = System.currentTimeMillis() - gestureStartTime;
            if (deltaTime > 6000 )
                gestureCnt = 0;

            switch(gestureCnt)
            {
                case 0: // wait for initial position, flat
                    if ( roll < 10 && roll > -10  ) {
                        gestureStartTime = System.currentTimeMillis();
                        gestureCnt = 1;
                    }
                    break;
                case 1: case 3:
                if ( roll > 60 || roll < -60 ) {
                    gestureCnt++;
                }
                break;
                case 2: case 4:
                if ( roll < 10 && roll > -10 ) {
                    gestureCnt++;
                }
                if ( gestureCnt == 5) {
                    Intent localIntent = new Intent(Constants.SMS_MSG);
                    localIntent.putExtra(Constants.SMS_PLAY_ACTION, "play");
                    // Broadcasts the Intent to receivers in this app.
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(localIntent);
                    gestureCnt = 0;
                    gestureStartTime = 0;
                }
                break;
            }
     /*       switch(gestureCnt)
            {
                case 0: // wait for initial position
                    if ( pitch > 10 ) {
                        gestureStartTime = System.currentTimeMillis();
                        gestureCnt = 1;
                    }
                    break;
                case 1: case 3:
                if ( pitch < -90 ) {
                    gestureCnt++;
                }
                break;
                case 2: case 4:
                if ( pitch > 10 ) {
                    gestureCnt++;
                }
                if ( gestureCnt == 5) {
                    Intent localIntent = new Intent(Constants.SMS_MSG);
                    localIntent.putExtra(Constants.SMS_PLAY_ACTION, "play");
                    // Broadcasts the Intent to receivers in this app.
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(localIntent);
                    gestureCnt = 0;
                    gestureStartTime = 0;
                }
                break;
            }*/
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private final class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                try {
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(Constants.SCREEN_ON));
                    if ( mSensorManager != null)
                        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                try {
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(Constants.SCREEN_OFF));
                    if (mSensorManager != null )
                        mSensorManager.unregisterListener(mSensorListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int res = super.onStartCommand(intent, flags, startId);
        Log.i("CallDetectService", "CallDetectService : onStartCommand();");

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ServiceReceiver();
        registerReceiver(mReceiver, filter);

        try {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            mSensorManager.registerListener(mSensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
          //  e.printStackTrace();
            Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                    // Puts the status into the Intent
                    .putExtra(Constants.EXTRA_TEST_STRING, "sensor manager create failed");
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // shutdown thread
        m_MorsePlayer.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO should I be doing something here?
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("CallDetectService", "CallDetectService : onCreate();");

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        String preamble = settings.getString(Constants.PREF_PREAMBLE, Constants.DFT_PREABMPLE);
        Integer wpm = settings.getInt(Constants.PREF_WPR, Constants.DFT_WPM);
        Integer tone = settings.getInt(Constants.PREF_TONE, Constants.DFT_TONE);

        // Start a thread to listen for incoming calls and play morse code lookup.
        m_MorsePlayer = new MorsePlayer(getBaseContext(), preamble, wpm, tone);
        Thread tMorsePlayer = new Thread(m_MorsePlayer);
        tMorsePlayer.start();
    }
}
