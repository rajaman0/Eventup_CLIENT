package xyz.chiragtoprani.eventup_recruit;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ListViewActivity extends AppCompatActivity {

    ListView lv;
    Context context;

    public ArrayList<User> ar;
    public CustomAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        Firebase.setAndroidContext(this);
        String url = "https://eventum-eac60.firebaseio.com/";
        Firebase ref = new Firebase(url);

        ar = new ArrayList<User>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("dataset changed");
                System.out.println("got value");
                snapshot= snapshot.child("log");

                ar.clear();

                for(DataSnapshot d: snapshot.getChildren()){
                    User r = d.getValue(User.class);
                    ar.add(r);
                }
                System.out.println(ar.size());

                ca.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        context=this;

        lv=(ListView) findViewById(R.id.listView);

        ca = new CustomAdapter(this,0,ar);
        lv.setAdapter(ca);

    }





}