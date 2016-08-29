package why.supermanmusic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import why.supermanmusic.R;
import why.supermanmusic.bean.Mp3Info;

public class AudioPopAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Mp3Info> audioItems;

    public AudioPopAdapter(Context context, ArrayList<Mp3Info> audioItems) {
        this.context = context;
        this.audioItems = audioItems;
    }

    @Override
    public int getCount() {
        if(audioItems==null){
            return 0;
        }
        return audioItems.size();
    }

    @Override
    public Object getItem(int position) {
        return audioItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.audio_player_pop_list_item,null);
            convertView.setTag(holder);
            holder.audio_pop_list_name = (TextView) convertView.findViewById(R.id.audio_pop_item_name);
            holder.audio_pop_list_artist = (TextView) convertView.findViewById(R.id.audio_pop_item_artist);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.audio_pop_list_name.setText(audioItems.get(position).getTitle());
        holder.audio_pop_list_artist.setText(audioItems.get(position).getArtist());
        return convertView;
    }
    class ViewHolder{
        TextView audio_pop_list_name,audio_pop_list_artist;
    }
}
