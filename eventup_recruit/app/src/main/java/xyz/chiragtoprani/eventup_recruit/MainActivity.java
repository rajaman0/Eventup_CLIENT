package xyz.chiragtoprani.eventup_recruit;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;

import com.cjj.sva.JJSearchView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity implements OnNdefPushCompleteCallback{

    private static final int MESSAGE_SENT = 1;
    JJSearchView mJJSearchView;
    NfcAdapter mNfcAdapter;
    TextView mInfoText;

    DataSnapshot snap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            mInfoText = (TextView) findViewById(R.id.textView);
            mInfoText.setText("NFC is not available on this device.");
        }

            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

    }

//    public NdefMessage createNdefMessage(NfcEvent event) {
//        Time time = new Time();
//        time.setToNow();
//        String text = ("Beam me up!\n\n" +
//                "Beam Time: " + time.format("%H:%M:%S"));
//        NdefMessage msg = new NdefMessage(
//                new NdefRecord[] { createMimeRecord(
//                        "application/com.example.android.beam", text.getBytes())
//                        /**
//                         * The Android Application Record (AAR) is commented out. When a device
//                         * receives a push with an AAR in it, the application specified in the AAR
//                         * is guaranteed to run. The AAR overrides the tag dispatch system.
//                         * You can add it back in to guarantee that this
//                         * activity starts when receiving a beamed message. For now, this code
//                         * uses the tag dispatch system.
//                        */
//                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
//                });
//        return msg;
//    }
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }


    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        System.out.println("push complete");
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();


        System.out.println("Key sending onResume");
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            System.out.println("Test");
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        System.out.println("getting intent");
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        System.out.println(snap);
        processUID(new String(msg.getRecords()[0].getPayload()));
    }

    public void processUID(String userId){
        queryDB(userId);
    }

    public void queryDB(final String userId){
        String url = "https://eventum-eac60.firebaseio.com/users/" + userId;
        Firebase ref = new Firebase(url);
        System.out.println("getting value");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("got value");
                setSnap(snapshot, userId);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void setSnap(DataSnapshot sn, String userId){
        snap = sn;
        System.out.println("suh");

        System.out.println(userId);
        System.out.println(snap);
        DataSnapshot data = snap;
        System.out.println(data);
        String firstName = (String) data.child("firstName").getValue();
        String lastName = (String) data.child("lastName").getValue();
        String email = (String) data.child("email").getValue();
        String resumeLink = (String) data.child("ResumeLink").getValue();


        TextView tv = (TextView) findViewById(R.id.added);
        tv.setText("" + firstName + " was added.");

        View v = findViewById(R.id.notif);
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        anim.setRepeatCount(0);
        v.startAnimation(anim);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void start(View v) {
        mJJSearchView.startAnim();
    }

    public void reset(View v) {
        mJJSearchView.resetAnim();
    }
}
