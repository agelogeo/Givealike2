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
import com.google.android.gms.ads.AdView;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private AdView mAdView;
    FloatingActionButton fab;
    ConstraintLayout imageConstraint;
    Button getHashtagsBtn,categoryButton;
    TextView hashtagsView,customUsername,customLikeView, pasteTextView;
    ImageView customPhotoWallpaper,customProfile;
    String link = "";
    String tag = "",clipboard = "" , hashtags[];


    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);





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
                /*if(rewardedVideoAd.isAdLoaded())
                    rewardedVideoAd.show();
                else
                    rewardedVideoAd.loadAd();
*//*
                if(interstitialAd.isAdLoaded())
                    interstitialAd.show();
                else
                    interstitialAd.loadAd();*/
            }
        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCategory();
            }
        });

        hashtagsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(getContext(),hashtagsView.getText().toString());
            }
        });

        /*interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                getHashtagsBtn.setEnabled(false);
                getHashtagsBtn.setText("Please wait...");
                interstitialAd.loadAd();
                hashtagsView.setVisibility(View.VISIBLE);
                Cursor c = myDatabase.rawQuery("SELECT * FROM hashtags WHERE category='"+tag+"'",null);
                if(c.getCount()>0){
                    String temp = "";
                    c.moveToFirst();
                    while(c.moveToNext()){
                        temp += c.getString(2);
                    }
                    hashtags = temp.split("#");
                    int randomNumber = randomWithRange(1,hashtags.length-20);
                    clipboard = "";
                    for(int i=randomNumber;i<randomNumber+20;i++){
                        clipboard+= "#"+hashtags[i]+" ";
                    }
                    hashtagsView.setText(clipboard);
                    setClipboard(getContext(),hashtagsView.getText().toString());
                }else{
                    Log.i("DATABASE","No Result...");
                }
                c.close();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                getHashtagsBtn.setText("Get Hashtags");
                getHashtagsBtn.setEnabled(true);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });*/


        return view;
    }

    public void OpenCategory(){
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
            //interstitialAd.loadAd();
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
        categoryButton.setText(getString(R.string.choose_category));
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
            String result = "",line;
            URL url;
            HttpURLConnection urlConnection;

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

                return BitmapFactory.decodeStream(inputStream);

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

                return BitmapFactory.decodeStream(inputStream);

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

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(getContext(),"Hashtags copied to Clipboard.",Toast.LENGTH_SHORT).show();

    }

}
