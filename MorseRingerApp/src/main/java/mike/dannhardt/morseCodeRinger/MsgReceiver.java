package mike.dannhardt.morseCodeRinger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

public class MsgReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            SmsMessage[] messages = new SmsMessage[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            }
            String msgFrom, msgBody;
            msgFrom = "";
            msgBody = "";
            for(int i=0; i< messages.length; i++){
                msgFrom = messages[i].getOriginatingAddress();
                msgBody = messages[i].getMessageBody();
            }
            if (messages.length>0) {
                Intent localIntent = new Intent(Constants.SMS_RCVD)
                        .putExtra(Constants.EXTRA_FROM_NUMBER, msgFrom)
                        .putExtra(Constants.EXTRA_SMS_DATA, msgBody);
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
            }
        } else if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")){
            Intent localIntent = new Intent(Constants.SMS_RCVD)
                    .putExtra(Constants.EXTRA_FROM_NUMBER, "1234")
                    .putExtra(Constants.EXTRA_SMS_DATA, "wap");
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } else if (intent.getAction().equals("android.provider.Telephony.MMS_RECEIVED")){
            Intent localIntent = new Intent(Constants.SMS_RCVD)
                    .putExtra(Constants.EXTRA_FROM_NUMBER, "11")
                    .putExtra(Constants.EXTRA_SMS_DATA, "mms");
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } else {
            Intent localIntent = new Intent(Constants.SMS_RCVD)
                    .putExtra(Constants.EXTRA_FROM_NUMBER, "1")
                    .putExtra(Constants.EXTRA_SMS_DATA, "??");
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        }
    }
}