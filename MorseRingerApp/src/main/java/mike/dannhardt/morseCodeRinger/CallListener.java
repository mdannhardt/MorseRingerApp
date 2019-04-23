package mike.dannhardt.morseCodeRinger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

public class CallListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
                int state = 0;
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                }

                Intent localIntent = new Intent(Constants.PHONE_STATE_CHG)
                        .putExtra(Constants.EXTRA_PHONE_STATE, state)
                        .putExtra(Constants.EXTRA_FROM_NUMBER, number);
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
            }
        }
    }
}