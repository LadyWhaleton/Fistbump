package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MediaGalleryTab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    GridView galleryGridView;
    TextView emptyGridView;
    SwipeRefreshLayout swipeLayout;

    private String folderName = Environment.getExternalStorageDirectory().toString() + "/Fistbump";
    public static List<Media> mediaList;

    // TODO: Temporary hardcoded images. Need to use directory.
    private int defaultSongCover = R.drawable.default_song_cover;
    private int defaultFileIcon = R.drawable.file_icon_generic_file;

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    public MediaGalleryTab() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MediaGalleryTab newInstance(String param1, String param2) {
        MediaGalleryTab fragment = new MediaGalleryTab();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_gallery_tab, container, false);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // check if the file directory has been created for Fistbump. If not, make one.
        File fistbumpFolder = new File(folderName);

        if(!fistbumpFolder.exists()) {
            fistbumpFolder.mkdirs();
            //Toast.makeText(getContext(), fistbumpFolder + " created!", Toast.LENGTH_SHORT).show();
        }
        else {
            //Toast.makeText(getContext(), fistbumpFolder + " already exists!", Toast.LENGTH_SHORT).show();
        }

        mediaList = new ArrayList<>();
        galleryGridView = (GridView) getView().findViewById(R.id.galleryGridView);
        emptyGridView = (TextView) getView().findViewById(R.id.emptyGalleryTextView);
        galleryGridView.setEmptyView(emptyGridView);
        LoadMedia();
        setSwipeRefresh();

        // TODO: replace load media with LiveUpdate. Move LoadMedia to tabbedMain
        setListClickListener();

    }

    private void setSwipeRefresh(){
        swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                        LoadMedia();
                        if(swipeLayout.isRefreshing())
                            swipeLayout.setRefreshing(false);
                        
                    }
                }, 1000);
            }
        });

    }

    private void setListClickListener()
    {
        galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!mediaList.get(position).getMediaType().equals("?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = mediaList.get(position).getUriFromFileName();
                    intent.setDataAndType(uri, mediaList.get(position).getMimeType());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "You cannot view this file.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
                    */
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /** The Load Media function should load shared files from storage. Load Media should be able to
      * detect different kinds of files (photos, videos, songs) and display thumbnails. Files
      * should be populated by most recent first. For example:
      * [1] [2] [3]
      * [4] [5] [6]
      * [7] [8] [9]
      *  etc...
      */
    private void LoadMedia()
    {
        // TODO: Implement LoadMedia
        // Get MediaList log file


        File FistbumpDir = new File(folderName);
        File[] FistbumpMediaFiles = FistbumpDir.listFiles();

         Toast.makeText(getContext(), String.valueOf(FistbumpMediaFiles.length), Toast.LENGTH_SHORT).show();

//        for (File f : FistbumpMediaFiles)
//            Toast.makeText(getContext(), f.toString(), Toast.LENGTH_SHORT).show();
//         */

        // For each file, create a Media object
        for (File f: FistbumpMediaFiles)
        {
            // TODO: remove this hardcoded values
            long timeVal = new Date().getTime();
            mediaList.add( new Media (f.getName(), f.toString(), timeVal, "Wailord", "1337") );
        }

        // Update the Media Gallery
        LiveUpdateMedia();


    }

    /** The LiveUpdateMedia function should update the Media Gallery whenever a new file has been
     * received.
     */
    private void LiveUpdateMedia()
    {
        // TODO: Implement LiveUpdateMedia
        galleryGridView.setAdapter(new MediaGalleryAdapter(getActivity()));
        // galleryGridView.setAdapter(new ImageAdapter());
    }

    // TODO: Implement the Adapter

    public class MediaGalleryAdapter extends ArrayAdapter<Media>
    {

        public MediaGalleryAdapter(Context context)
        {
            super(context,R.layout.gridview_media_info, mediaList);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            // if there's no instance of this view
            if (view == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.gridview_media_info, parent, false);
            }

            // obtain a thumbnail for the media
            ImageView gallery_item = (ImageView) view.findViewById(R.id.gallery_item);
            Bitmap thumbnail = mediaList.get(position).getThumbnail(getContext().getContentResolver());

            // set the thumbnail according to it's file type (image, video, audio, etc)
            if (thumbnail != null)
                gallery_item.setImageBitmap(thumbnail);

            else
            {
                if ( mediaList.get(position).getMediaType().equals("audio") )
                    gallery_item.setImageDrawable(getResources().getDrawable(defaultSongCover));
                else
                    gallery_item.setImageDrawable(getResources().getDrawable(defaultFileIcon));
            }

            // set the profile pic of the sender
            ImageView ownerProfilePic = (ImageView) view.findViewById(R.id.media_profile_pic);
            ownerProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.profile_gray));

            // set the timestamp
            TextView timestamp = (TextView) view.findViewById(R.id.gallery_item_timestamp);
            timestamp.setText(mediaList.get(position).getFormattedDate());

            TextView fileExt = (TextView) view.findViewById((R.id.gallery_item_fileExt));
            fileExt.setText(mediaList.get(position).getFileExt());

            return view;
        }

    }


}
