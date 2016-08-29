package why.supermanmusic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import why.supermanmusic.R;
import why.supermanmusic.adapter.AlbumListAdapter;
import why.supermanmusic.bean.FindSongs;
import why.supermanmusic.bean.Mp3Info;
import why.supermanmusic.utils.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsRecordsFragment extends Fragment implements AdapterView.OnItemClickListener {


    @InjectView(R.id.list_albumsrecords)
    ListView mListAlbumsrecords;
    private FindSongs mFindSongs;
    private List<Mp3Info> mMp3Infos;
    private AlbumListAdapter mAdapter;

    public AlbumsRecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFindSongs = new FindSongs();
        mMp3Infos = mFindSongs.getMp3Infos(getActivity().getContentResolver());

        mAdapter = new AlbumListAdapter(UIUtils.getContext(), mMp3Infos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_albums_records, container, false);
        ButterKnife.inject(this, view);

        initListView();
        initEvent();

        return view;
    }

    private void initEvent() {
        mListAlbumsrecords.setOnItemClickListener(this);
    }

    private void initListView() {
        //设置适配器
        mFindSongs.setAlbumAdpter(UIUtils.getContext(), mMp3Infos, mListAlbumsrecords);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(UIUtils.getContext(), mMp3Infos.get(position).getAlbum(), Toast.LENGTH_SHORT).show();
    }
}
