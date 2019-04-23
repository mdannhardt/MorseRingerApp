package mike.dannhardt.morseCodeRinger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by Mike on 10/4/2015.
 */
public class ServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ServiceStarter", "ServiceStarter : onReceive();");

        Intent i = new Intent("mike.dannhardt.myfristapp.CallDetectService");
        i.setClass(context, CallDetectService.class);
        context.startService(i);
    }}
