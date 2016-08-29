package why.supermanmusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import why.supermanmusic.R;
import why.supermanmusic.View.CircleImageView;
import why.supermanmusic.bean.Mp3Info;
import why.supermanmusic.utils.MediaUtil;

/**
 * 创建者     Ted
 * 创建时间   2016/6/11 22:48
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AlbumListAdapter extends BaseAdapter {

    private Context context;        //上下文对象引用
    private List<Mp3Info> mp3Infos;   //存放Mp3Info引用的集合

    public AlbumListAdapter(Context context, List<Mp3Info> mp3Infos) {
        this.context = context;
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        if (mp3Infos != null) {
            return mp3Infos.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.album_list_item, null);
            holder.album_name = (TextView) convertView.findViewById(R.id.album_name);
            holder.album_num = (TextView) convertView.findViewById(R.id.album_num);
            holder.album_image = (CircleImageView) convertView.findViewById(R.id.album_pic);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置不同条目的背景颜色
        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.listview_item1_selector);
        } else {
            convertView.setBackgroundResource(R.drawable.listview_item2_selector);
        }

        //自己计算同一专辑有多少首歌曲
        HashMap<String, Integer> map = new HashMap<>();

        for (Mp3Info info : mp3Infos) {
            String album = info.getAlbum();
            Integer count = map.get(album);

            if (count == null) {
                map.put(album, 1);
            } else {
                map.put(album, count + 1);
            }
        }

        //输出结果
        /*for(String album:map.keySet()){
//            System.out.println(mp3Info+"出现了 " +map.get(mp3Info) +"次");

            holder.album_name.setText(album);

            holder.album_num.setText(map.get(album) + "首歌曲");

        }*/
        Mp3Info mp3Info = mp3Infos.get(position);


        holder.album_name.setText(mp3Info.getAlbum());

        holder.album_num.setText(map.get(mp3Info.getAlbum()) + "首歌曲");


        //设置专辑图片
        Bitmap bitmap = MediaUtil.getArtwork(context, mp3Info.getId(), mp3Info.getAlbum_id(), true, true);
        if (bitmap == null) {
            holder.album_image.setImageResource(R.drawable.default_album);
        } else {
            holder.album_image.setImageBitmap(bitmap);
        }

        return convertView;
    }

    class ViewHolder {
        public TextView album_name;
        public TextView album_num;
        public CircleImageView album_image;
    }

}
