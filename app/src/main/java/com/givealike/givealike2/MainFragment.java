package com.givealike.givealike2;

import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FloatingActionButton fab;
    ConstraintLayout imageConstraint;
    Button getHashtagsBtn,categoryButton;
    TextView hashtagsView,customUsername,customLikeView, pasteTextView;
    ImageView customPhotoWallpaper,customProfile;
    String link = "https://www.instagram.com/p/BvAkJnoDwmF/";
    String tag = "",clipboard = "" , hashtags[];

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        fab = view.findViewById(R.id.pasteButton);
        imageConstraint = view.findViewById(R.id.imageConstraintLayout);
        categoryButton = view.findViewById(R.id.categoryButton);
        getHashtagsBtn = view.findViewById(R.id.getHashtagsButton);
        hashtagsView = view.findViewById(R.id.hashtagsTextView);
        customPhotoWallpaper = view.findViewById(R.id.custom_photoWallpaper);
        customProfile = view.findViewById(R.id.custom_profilePicView);
        customUsername = view.findViewById(R.id.custom_usernameTextView);
        customLikeView = view.findViewById(R.id.customLikeView);
        pasteTextView = view.findViewById(R.id.pasteTextView);

        initializeUI();

        final SQLiteDatabase myDatabase = getActivity().openOrCreateDatabase("Hashtags",getActivity().MODE_PRIVATE,null);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = readFromClipboard();
                DownloadTask task = new DownloadTask();
                try {
                    task.execute(link);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        getHashtagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashtagsView.setVisibility(View.VISIBLE);
                Cursor c = myDatabase.rawQuery("SELECT * FROM hashtags WHERE category='"+tag+"'",null);
                if(c.getCount()>0){
                    String temp = "";
                    c.moveToFirst();
                    while(c.moveToNext()){
                        temp += c.getString(2);
                    }
                    hashtags = temp.split("#");
                    int randomNumber = randomWithRange(0,hashtags.length-20);
                    clipboard = "";
                    for(int i=randomNumber;i<randomNumber+20;i++){
                        clipboard+= "#"+hashtags[i+1]+" ";
                    }
                    hashtagsView.setText(clipboard);
                }else{
                    Log.i("DATABASE","No Result...");
                }
                c.close();
            }
        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCategory(view);
            }
        });

        return view;
    }

    public void OpenCategory(View v){
        Intent i = new Intent(getContext(),CategoryActivity.class);
        startActivityForResult(i,1);
    }

    int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == getActivity().RESULT_OK){
            Toast.makeText(getContext(),data.getStringExtra("Category"),Toast.LENGTH_SHORT).show();
            getHashtagsBtn.setEnabled(true);
            categoryButton.setText(data.getStringExtra("Category"));
            tag = data.getStringExtra("Category");
        }

    }

    public void updateViewAfterPaste(){
        imageConstraint.setVisibility(View.VISIBLE);
        categoryButton.setVisibility(View.VISIBLE);
        getHashtagsBtn.setVisibility(View.VISIBLE);
        hashtagsView.setVisibility(View.GONE);
        pasteTextView.setVisibility(View.GONE);
        categoryButton.setText("Choose Category");
        getHashtagsBtn.setEnabled(false);
    }

    public void initializeUI(){
        imageConstraint.setVisibility(View.GONE);
        categoryButton.setVisibility(View.GONE);
        getHashtagsBtn.setVisibility(View.GONE);
        hashtagsView.setVisibility(View.GONE);
        pasteTextView.setVisibility(View.VISIBLE);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String readFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        Toast.makeText(getContext(),"Please copy a valid link.", Toast.LENGTH_SHORT).show();
        return "";
    }

    public class DownloadTask extends AsyncTask<String,Void ,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "",line = null;
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                while ((line = reader.readLine()) != null)
                    result += line;

                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            analyzePost(s);
        }
    }

    public void analyzePost(String s){
        try{
            //Log.i("analyze",s);
            String link ;
            Pattern pattern = Pattern.compile("window._sharedData = (.*?)[}];");
            Matcher matcher = pattern.matcher(s);

            matcher.find();
            String jObject = matcher.group(1)+"}";
            JSONObject jsonObject = new JSONObject(jObject);
            JSONObject entry_data = jsonObject.getJSONObject("entry_data");
            JSONArray PostPage = entry_data.getJSONArray("PostPage");
            JSONObject first_graphql_shortcode_media = PostPage.getJSONObject(0).getJSONObject("graphql").getJSONObject("shortcode_media");
            JSONObject owner = first_graphql_shortcode_media.getJSONObject("owner");

            Log.i("USERNAME",owner.getString("username"));
            Log.i("PROFILE_PIC_URL",owner.getString("profile_pic_url"));
            customUsername.setText("@"+owner.getString("username"));
            customLikeView.setText(first_graphql_shortcode_media.getJSONObject("edge_media_preview_like").getString("count"));
            ProfileImageDownloader task = new ProfileImageDownloader();
            task.execute(owner.getString("profile_pic_url"));

            if(first_graphql_shortcode_media.has("edge_sidecar_to_children")){
                JSONArray children_edges = first_graphql_shortcode_media.getJSONObject("edge_sidecar_to_children").getJSONArray("edges");
                Log.i("WITH_CHILDREN_COUNT",Integer.toString(children_edges.length()));

                for(int i=0; i<1; i++){
                    JSONObject node = children_edges.getJSONObject(i).getJSONObject("node");

                    if(node.has("video_url")){
                        //link = node.getString("video_url");
                        link = node.getJSONArray("display_resources").getJSONObject(2).getString("src");
                        Log.i("CHILDREN_W_VIDEO_"+(i+1),node.getString("video_url"));
                    }else{
                        link = node.getJSONArray("display_resources").getJSONObject(2).getString("src");
                        Log.i("CHILDREN_W_PHOTO_"+(i+1),node.getJSONArray("display_resources").getJSONObject(2).getString("src"));
                    }
                    Picasso.get().load(link).into(customPhotoWallpaper);

                    /*ImageDownloader imageTask = new ImageDownloader();
                    imageTask.execute(link);*/
                }
            }else{
                if(first_graphql_shortcode_media.has("video_url")){
                    Log.i("NO_CHILDREN_W_VIDEO",first_graphql_shortcode_media.getString("video_url"));
                    link = first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(2).getString("src");
                }else{
                    Log.i("NO_CHILDREN_W_PHOTO",first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(2).getString("src"));
                    link = first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(2).getString("src");
                }
                Picasso.get().load(link).into(customPhotoWallpaper);

                /*ImageDownloader imageTask = new ImageDownloader();
                imageTask.execute(link);*/
            }
            updateViewAfterPaste();

        }catch (Exception e){
            Toast.makeText(getContext(),"Error with your link.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public class ImageDownloader extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            customPhotoWallpaper.setImageBitmap(bitmap);
            updateViewAfterPaste();
            //imageGrid.setAdapter(new ImageAdapter(getActivity().getApplicationContext(), bitmapList));

        }
    }

    public class ProfileImageDownloader extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            customProfile.setImageBitmap(bitmap);
            //imageGrid.setAdapter(new ImageAdapter(getActivity().getApplicationContext(), bitmapList));

        }
    }

}
