package com.example.messagingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class BroadcastReceiverClass extends BroadcastReceiver {
    String message = "";
    String details = "";
    Handler handler;
    SmsManager manager;
    Context appContext;
    @Override
    public void onReceive(Context context, Intent intent) {

        handler = new Handler();
        manager = SmsManager.getDefault();

        appContext = context.getApplicationContext();

        message = "";
        details = "";

        Log.d("GOOD", "started onReceive");
        Toast.makeText(appContext, "Received Message!", Toast.LENGTH_SHORT).show();

        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[])(bundle.get("pdus"));
        SmsMessage[] messages = new SmsMessage[pdus.length];

        for(int i=0; i< pdus.length; i++)
        {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i] , bundle.getString("format"));
            message+=messages[i].getMessageBody();
            details+=messages[i].getOriginatingAddress();
        }
        Log.d("GOOD", "onReceive: " + details);
        Log.d("GOOD", "message " + message);

        MainActivity.t1.setText("Incoming:\nFrom- "+details+"\nMessage- "+message);
        new AsyncThread().execute(message);
    }

    public class AsyncThread extends AsyncTask<String, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject json;
            URL url;
            try {
                String msg = strings[0];
                url = new URL("http://api.brainshop.ai/get?bid=174965&key=cPNaV7d5Ztad4cxD&uid=1&msg="+message);
                Log.d("GOOD", url.toString());
                URLConnection connect = url.openConnection();
                Log.d("FAIL", "input");
                InputStream stream = connect.getInputStream();
                Log.d("FAIL", "buffer");
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                String text = "";
                String line = "";
                while ((line = buffer.readLine()) != null) {
                    text += line;
                }
                buffer.close();
                Log.d("FAIL", "did not reached json");
                json = new JSONObject(text);
                Log.d("FAIL", "reached json");
                return json;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("FAIL", "null");
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                MainActivity.t2.setText("Outgoing:\nMessage- "+json.getString("cnt")+"\nIn 3 seconds");
                handler.postDelayed(delay(json.getString("cnt")), 3000);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable delay(String msg){
        return new Runnable() {
            @Override
            public void run() {
                Log.d("GOOD", "out: "+msg);
                manager.sendTextMessage("5556", null, msg, null, null);
                Toast.makeText(appContext, "Sent Message!", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
