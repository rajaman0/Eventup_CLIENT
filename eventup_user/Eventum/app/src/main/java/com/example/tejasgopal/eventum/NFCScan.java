package com.example.tejasgopal.eventum;

import android.content.Intent;
import android.media.Image;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;

import static android.nfc.NdefRecord.createMime;

public class NFCScan extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback,
                                                            NfcAdapter.OnNdefPushCompleteCallback {

    private TextView confirmation;
    private ImageView img;
    private String fireBaseKey;
    private SpinKitView spinKitView;
    private ImageView message_complete;
    private Button anothertap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcscan);

        img = (ImageView) findViewById(R.id.checkmark);
        img.setVisibility(View.INVISIBLE);

        message_complete = (ImageView) findViewById(R.id.message_sent);

        confirmation = (TextView) findViewById(R.id.confirmation);

        spinKitView = (SpinKitView) findViewById(R.id.spin_kit);
        spinKitView.setVisibility(View.VISIBLE);

        anothertap = (Button) findViewById(R.id.another_tap);
        anothertap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinKitView.setVisibility(View.VISIBLE);
                message_complete.setImageResource(R.drawable.failure);
                img.setVisibility(View.INVISIBLE);
                confirmation.setText("Wait for confirmation...");
            }
        });

        fireBaseKey = getIntent().getStringExtra("key");

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter != null && nfcAdapter.isEnabled()) {
            Log.d("NFCScan", "NFC available");
            Toast.makeText(this, "NFC is available!", Toast.LENGTH_SHORT).show();
        }
        else if(nfcAdapter != null && !nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC for use of Eventum!", Toast.LENGTH_SHORT).show();
            Intent enableNFC = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(enableNFC);
        }
        else if(nfcAdapter == null) {
            Toast.makeText(this, "Device does not support NFC.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(nfcAdapter != null) {
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/com.example.tejasgopal.eventum", fireBaseKey.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        */
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Message sent!",
                        Toast.LENGTH_SHORT).show();
                img.setImageResource(R.drawable.checkmark);
                img.setVisibility(View.VISIBLE);
                confirmation.setText("You were successfully added!");
                message_complete.setImageResource(R.drawable.checkmark);
                spinKitView.setVisibility(View.INVISIBLE);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            //processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        //textView = (TextView) findViewById(R.id.textView2);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        //textView.setText(new String(msg.getRecords()[0].getPayload()));
    }
}
