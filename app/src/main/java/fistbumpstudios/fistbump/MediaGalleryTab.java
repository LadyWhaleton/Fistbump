package fistbumpstudios.fistbump;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MediaGalleryTab.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MediaGalleryTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MediaGalleryTab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    GridView galleryGridView;
    TextView emptyGridView;
    public static List<Media> mediaList;

    // TODO: Temporary hardcoded images. Need to use directory.
    private int[] images = {
            R.drawable.fancy_wailord_by_pokemaster360,
            R.drawable.fancy_wailord_by_pokemaster360
    };

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

        mediaList = new ArrayList<>();
        galleryGridView = (GridView) getView().findViewById(R.id.galleryGridView);
        emptyGridView = (TextView) getView().findViewById(R.id.emptyGalleryTextView);
        galleryGridView.setEmptyView(emptyGridView);
        LoadMedia();

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
        // Load Media from storage here


        // Update the Media Gallery
        LiveUpdateMedia();


    }

    /** The LiveUpdateMedia function should update the Media Gallery whenever a new file has been
     * received.
     */
    private void LiveUpdateMedia()
    {
        // TODO: Implement LiveUpdateMedia
        // galleryGridView.setAdapter(new MediaGalleryAdapter(getActivity()));
        galleryGridView.setAdapter(new ImageAdapter());
    }

    // TODO: Implement the Adapter
    /*
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

            ImageView gallery_item = (ImageView) view.findViewById(R.id.gallery_item);
            gallery_item.setImageDrawable(getResources().getDrawable(images[position]));


            // here we set the thumbnail of the image according to the path
            // gallery_item.setImageURI( Uri.parse(mediaList.get(position).toString()) );



            return view;
        }

    }
    */

    // TODO: REMOVE
    public class ImageAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return images[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View view, ViewGroup parent) {
            // if there's no instance of this view
            if (view == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.gridview_media_info, parent, false);
            }

            ImageView gallery_item = (ImageView) view.findViewById(R.id.gallery_item);
            gallery_item.setImageDrawable(getResources().getDrawable(images[position]));

            return view;
        }
    }


}