package xyz.chiragtoprani.eventup_recruit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;

import java.util.List;

/**
 * Created by Chirag on 10/1/2016.
 */
public class CustomAdapter extends ArrayAdapter<User> {
    DataSnapshot result;
    private static LayoutInflater inflater=null;
    public CustomAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CustomAdapter(Context context, int resource, List<User> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        System.out.println("redo view");

        if (rowView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            rowView = vi.inflate(R.layout.listviewitem, null);
        }

        String name = getItem(position).vals.get(0) +  getItem(position).vals.get(1);
        String m = getItem(position).vals.get(4);

        ((TextView) rowView.findViewById(R.id.nameA)).setText(name);
        ((TextView) rowView.findViewById(R.id.majorA)).setText(m);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToDetails(position);

            }
        });
        return rowView;
    }

    public void moveToDetails(int index){
        Bundle b = new Bundle();
        b.putStringArrayList("items", getItem(index).vals);
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtras(b);
        getContext().startActivity(intent);
    }
}

