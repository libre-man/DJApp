package nl.sdaas.app.sdaas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChannelAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private Session session;

    public ChannelAdapter(Context context, Session session) {
        this.context = context;
        this.session = session;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.session.getAmountOfChannels();
    }

    @Override
    public Object getItem(int position) {
        return this.session.getChannel(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null)
            view = this.inflater.inflate(R.layout.channel_item, parent, false);

        Channel c = (Channel) this.getItem(position);

        if (c != null) {
            TextView tv = (TextView) view.findViewById(R.id.channelTextView);

            if (tv != null) {
                tv.setText(c.getChannelUrl());
                tv.setBackgroundColor(c.getColor());
            }
        }

        return view;
    }
}
