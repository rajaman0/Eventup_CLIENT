package xyz.chiragtoprani.eventup_recruit;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;

/**
 * Created by Chirag on 10/1/2016.
 */
public class DetailsActivity extends AppCompatActivity {

    String resUrl;
    String email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_detail);

        ArrayList<String> ar = getIntent().getExtras().getStringArrayList("items");

        ((TextView) findViewById(R.id.email)).setText(ar.get(2));
        ((TextView) findViewById(R.id.major)).setText(ar.get(4));
        ((TextView) findViewById(R.id.school)).setText(ar.get(5));
        ((TextView) findViewById(R.id.nameDetails)).setText(ar.get(0) + " " + ar.get(1));
        resUrl = ar.get(3);
        email = ar.get(2);
        findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResume();

            }
        });

        findViewById(R.id.emailView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();

            }
        });


    }

    public void showResume(){
//        Intent i = new Intent(this, MyPdfViewActivity.class);
//        Bundle j = new Bundle();
//        j.putString("resume", resUrl);
//        i.putExtras(j);
//        startActivity(i);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resUrl));
        startActivity(browserIntent);
    }

    public void sendEmail(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SD Hacks 2016");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Important Update");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
