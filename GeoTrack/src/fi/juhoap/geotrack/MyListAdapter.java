package fi.juhoap.geotrack;

/*
 * class for my list adapter 
 */

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter {

	private Context context;
    private List<MyListRow> listRow;

    public MyListAdapter(Context context, List<MyListRow> listRow) {
        this.context = context;
        this.listRow = listRow;
    }

    public int getCount() {
        return listRow.size();
    }

    public Object getItem(int position) {
        return listRow.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MyListRow entry = listRow.get(position); // get entry for the desired list position
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        // set location, date and icon for the view
        TextView location = (TextView) convertView.findViewById(R.id.textView2);
        location.setText(entry.getLatitude() + " / " + entry.getLongitude());

        TextView date = (TextView) convertView.findViewById(R.id.textView3);
        date.setText(entry.getDate());

        ImageView icon = (ImageView) convertView.findViewById(R.id.imageView1);
        icon.setImageResource(R.drawable.ic_launcher);

        return convertView;
    }

}
