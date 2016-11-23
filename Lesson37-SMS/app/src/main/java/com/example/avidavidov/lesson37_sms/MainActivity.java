package com.example.avidavidov.lesson37_sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

public class MainActivity extends Activity {

    public static final String SMS_SENT = "SMS_SENT";
    PendingIntent sendPI;
    BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.putExtra("adrress", "1234 435; 23455");
//        intent.putExtra("sms_body", "Black friday");
//        intent.setType("vdn.androis-dir/mms-sms");
//        startActivity(intent);

//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage("0546868414", null, "How are you",null,null);


        sendPI = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
    }

    public void btnSend(View view) {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case RESULT_OK:

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:

                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:

                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:

                        break;
                }
            }
        };
        registerReceiver(smsReceiver,new IntentFilter(SMS_SENT));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("0546868414", null, "How are you", null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsReceiver);
    }
}

