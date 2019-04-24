package mike.dannhardt.morseCodeRinger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View.OnClickListener;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class SetupActivity extends AppCompatActivity {
        public String m_Preamble = Constants.DFT_PREABMPLE;
        public Integer m_Tone = Constants.DFT_TONE;
        public Integer m_Wpm = Constants.DFT_WPM;
        public Boolean m_AnnounceRun;

        public final static String EXTRA_MESSAGE = "mike.dannhardt.morseCodeRinger.MESSAGE";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        m_Preamble = settings.getString(Constants.PREF_PREAMBLE, Constants.DFT_PREABMPLE);
        m_Wpm = settings.getInt(Constants.PREF_WPR, Constants.DFT_WPM);
        m_Tone = settings.getInt(Constants.PREF_TONE, Constants.DFT_TONE);
        m_AnnounceRun = settings.getBoolean(Constants.EXTRA_ANNC_START, true);

        // Intent to reset Play/Stop Last SMS Message button text
        IntentFilter filter = new IntentFilter(Constants.SMS_MSG);
        LocalBroadcastManager.getInstance(this.getBaseContext()).registerReceiver(playLastSmsMessageCmp, filter);

        EditText editText;
        editText = (EditText) findViewById(R.id.edit_preamble);
        editText.setText(m_Preamble);
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                m_Preamble = s.toString();

                Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTRA_SET_PREAMBLE, m_Preamble);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            }
        });

        editText = (EditText) findViewById(R.id.edit_speed);
        editText.setText(m_Wpm.toString());
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    m_Wpm = Integer.parseInt(s.toString());
                } catch(NumberFormatException nfe) {
                    return;
                }
                Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                        .putExtra(Constants.EXTRA_SET_SPEED, m_Wpm);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            }
        });

        editText = (EditText) findViewById(R.id.edit_tone);
        editText.setText(m_Tone.toString());
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int tone;
                try {
                    tone = Integer.parseInt(s.toString());
                } catch(NumberFormatException nfe) {
                    return;
                }
                if (tone >= 100 && tone <= 2000) {
                    m_Tone = tone;
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Valid tones are 100 to 2000 hz.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                        .putExtra(Constants.EXTRA_SET_TONE, m_Tone);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            }
        });

        CheckBox cb = (CheckBox) findViewById(R.id.announceRunning);
        cb.setChecked(m_AnnounceRun);
        cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_AnnounceRun = ((CheckBox) v).isChecked();
                Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                        .putExtra(Constants.EXTRA_ANNC_START, m_AnnounceRun);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            }
        });

        Intent i = new Intent("mike.dannhardt.morseCodeRinger.CallDetectService");
        Context context = getApplicationContext();
        i.setClass(context, CallDetectService.class);
        context.startService(i);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hello Dana!! * Good Luck Dancing Today *", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_my, menu);
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Play or Stop Last SMS message button */
    public void playLastSmsMessage(View view) {
        Button btnPlaySms = (Button)findViewById(R.id.btn_playSms);
        String btnLabel = btnPlaySms.getText().toString();

        Intent localIntent = new Intent(Constants.SMS_MSG);

        // Select the intent and toggle the button
        if (btnLabel.equals(getResources().getString(R.string.button_play_last))) {
            localIntent.putExtra(Constants.SMS_PLAY_ACTION, "play");
            btnPlaySms.setText(getResources().getString(R.string.button_stop_last));
        }
        else {
            localIntent.putExtra(Constants.SMS_PLAY_ACTION, "stop");
            btnPlaySms.setText(getResources().getString(R.string.button_play_last));
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    /** Called when Last SMS message finished */
    private BroadcastReceiver playLastSmsMessageCmp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra(Constants.SMS_PLAY_ACTION);
            if (action.equals("stop")){
                Button btnPlaySms = (Button)findViewById(R.id.btn_playSms);
                btnPlaySms.setText(getResources().getString(R.string.button_play_last));
            }
        }
    };


    /** Called when the user clicks the Play button */
    public void playTestMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String phoneNumber = editText.getText().toString();

        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                // Puts the status into the Intent
                .putExtra(Constants.EXTRA_TEST_STRING, phoneNumber);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void saveSetting(View view) {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(Constants.PREF_PREAMBLE, m_Preamble);
        editor.putInt(Constants.PREF_WPR, m_Wpm);
        editor.putInt(Constants.PREF_TONE, m_Tone);
        editor.putBoolean(Constants.PREF_ANNC, m_AnnounceRun);

        // Commit the edits!
        editor.commit();
    }
}
