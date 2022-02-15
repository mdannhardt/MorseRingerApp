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
import android.widget.Toast;

/**
 * Created by Mike on 10/4/2015.
 */
public class CallDetectService extends Service {
    private MorsePlayer m_MorsePlayer;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private float mXAccel; // acceleration apart from gravity
    private float mXAccelCurrent; // current acceleration including gravity
    private float mXAccelLast; // last acceleration including gravity
    private float mYAccel; // acceleration apart from gravity
    private float mYAccelCurrent; // current acceleration including gravity
    private float mYAccelLast; // last acceleration including gravity
    private float mZAccel; // acceleration apart from gravity
    private float mZAccelCurrent; // current acceleration including gravity
    private float mZAccelLast; // last acceleration including gravity
    private long shakeStart = 0;
    private int shakeCnt = 0;

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];

            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            mXAccelLast = mXAccelCurrent;
            mXAccelCurrent = x;
            delta = mXAccelCurrent - mXAccelLast;
            mXAccel = mXAccel * 0.9f + delta; // perform low-cut filter

            mYAccelLast = mYAccelCurrent;
            mYAccelCurrent = y;
            delta = mYAccelCurrent - mYAccelLast;
            mYAccel = mYAccel * 0.9f + delta; // perform low-cut filter

            mZAccelLast = mZAccelCurrent;
            mZAccelCurrent = z;
            delta = mZAccelCurrent - mZAccelLast;
            mZAccel = mZAccel * 0.9f + delta; // perform low-cut filter

            // Current 'shake detect' is three rapid accelerations (>4.5 magnitude) within 4seconds
            if ( mYAccel > 4.5 ) {
                long deltaTime = System.currentTimeMillis() - shakeStart;
                if (shakeCnt == 0 || deltaTime > 4000){
                    shakeCnt = 1;
                    shakeStart = System.currentTimeMillis();
                }
                else if (++shakeCnt >= 3){
                    Intent localIntent = new Intent(Constants.SMS_MSG);
                    localIntent.putExtra(Constants.SMS_PLAY_ACTION, "play");
                    // Broadcasts the Intent to receivers in this app.
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(localIntent);
                    shakeCnt = 0;
                    shakeStart = 0;
                }
            }
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
                        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
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

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        mXAccel = mYAccel = mZAccel = mAccel;
        mXAccelCurrent = mYAccelCurrent = mZAccelCurrent = mAccelCurrent;
        mXAccelLast = mYAccelLast = mZAccelLast = mAccelLast;

        try {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
