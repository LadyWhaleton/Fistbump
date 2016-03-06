package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MediaGalleryTab.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MediaGalleryTab#newInstance} factory method to
 * create an instance of this fragment.
 * http://developer.android.com/guide/topics/media/mediaplayer.html
 */
public class MediaGalleryTab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    GridView galleryGridView;
    TextView emptyGridView;

    private String folderName = Environment.getExternalStorageDirectory().toString() + "/Fistbump";
    public static List<Media> mediaList;

    // TODO: Temporary hardcoded images. Need to use directory.
    private int defaultSongCover = R.drawable.default_song_cover;
    private int defaultFileIcon = R.drawable.file_icon_generic_file;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MediaGalleryTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MediaGalleryTab.
     */
    // TODO: Rename and change types and number of parameters
    public static MediaGalleryTab newInstance(String param1, String param2) {
        MediaGalleryTab fragment = new MediaGalleryTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        setListClickListener();

    }

    private void setListClickListener()
    {
        galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = mediaList.get(position).getUriFromFileName();
                intent.setDataAndType(uri, mediaList.get(position).getMimeType());
                startActivity(intent);

                /**
                 if (mediaList.get(position).getMediaType().equals("image"))
                 {
                 Intent intent = new Intent();
                 intent.setAction(Intent.ACTION_VIEW);
                 Uri uri = mediaList.get(position).getUriFromFileName();
                 intent.setDataAndType(uri, "image/*");
                 startActivity(intent);
                 }

                 else if ( mediaList.get(position).getMediaType().equals("video")) {
                 Intent intent = new Intent();
                 intent.setAction(Intent.ACTION_VIEW);
                 Uri uri = mediaList.get(position).getUriFromFileName();
                 intent.setDataAndType(uri, "video/*");
                 startActivity(intent);
                 }
                 else if ( mediaList.get(position).getMediaType().equals("audio"))
                 {
                 Intent intent = new Intent();
                 intent.setAction(Intent.ACTION_VIEW);
                 Uri uri = mediaList.get(position).getUriFromFileName();
                 intent.setDataAndType(uri, "audio/*");
                    startActivity(intent);
                }

                else
                    Toast.makeText(getContext(), mediaList.get(position).getMediaType(), Toast.LENGTH_SHORT).show();
                    */
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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

        /** Toast.makeText(getContext(), String.valueOf(FistbumpMediaFiles.length), Toast.LENGTH_SHORT).show();

        for (File f : FistbumpMediaFiles)
            Toast.makeText(getContext(), f.toString(), Toast.LENGTH_SHORT).show();
         */

        // For each file, create a Media object
        for (File f: FistbumpMediaFiles)
        {
            // TODO: remove this hardcoded values
            DateFormat df = new SimpleDateFormat("HH:mm MM/dd");
            String timeCreated = df.format(new Date());
            mediaList.add( new Media (f.getName(), f.toString(), timeCreated, "Wailord", "1337") );
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

            // set the timestamp
            TextView timestamp = (TextView) view.findViewById(R.id.gallery_item_timestamp);
            timestamp.setText(mediaList.get(position).getTimestamp());

            return view;
        }

    }

}
