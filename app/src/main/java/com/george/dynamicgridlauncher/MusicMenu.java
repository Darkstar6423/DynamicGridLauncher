package com.george.dynamicgridlauncher;

import static android.view.View.NO_ID;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MusicMenu  {

    private class Song {
        private long id;
        private String title;
        private String album;
        private String path;

        private Bitmap image;

        @NonNull
        @Override
        public String toString()
        {
            return (id+":"+title+":"+album);
        }

    }

    int currentSong = 0;

    List<View> AlbumList = new ArrayList<View>();
    List<Song> songs = new ArrayList<Song>();

    String selection = MediaStore.Audio.Media.IS_MUSIC + " > 0";
    Cursor cursor;

    public LinearLayout scrollLayout;
    public NestedScrollView scrollView;

    public ConstraintLayout parent;
    Context context;
    public Button playButton;
    public Button prevButton;
    public Button nextButton;
    public ImageView songArt;

    private MainActivity main;

    public AudioManager manager;

    public IntentFilter intentFilter;

    public BroadcastReceiver mReceiver;

    Intent switchIntent = new Intent("com.george.dynamicgridlauncher.ACTION_PLAY");


    MediaPlayer mediaPlayer;

    View.OnFocusChangeListener playButtonFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus)
            {
                scrollView.setScrollY(0);
            }

        }
    };

    View.OnClickListener playButtonClickListener = new View.OnClickListener() {


        @Override
        public void onClick(View v) {
            if(mediaPlayer != null)
            {
                Resources res = main.getResources();
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    Drawable drawable = ResourcesCompat.getDrawable(res, android.R.drawable.ic_media_play, null);
                    playButton.setForeground(drawable);
                }
                else
                {
                    Drawable drawable = ResourcesCompat.getDrawable(res, android.R.drawable.ic_media_pause, null);
                    playButton.setForeground(drawable);
                    mediaPlayer.start();
                }
            }
        }
    };

    View.OnClickListener prevMediaButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(currentSong-1 > 0) {
                playSong(songs.get(currentSong - 1), currentSong - 1);
            }
        }
    };

    View.OnClickListener nextMediaButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(currentSong+1 < songs.size()-1) {
                playSong(songs.get(currentSong + 1), currentSong + 1);
            }
        }
    };




    public MusicMenu(ConstraintLayout p, Context c, MainActivity main)
    {
        parent = p;
        this.main = main;
        scrollView = p.findViewById(R.id.NestedScrollView);
        scrollLayout = p.findViewById(R.id.verticalScrollLayout);
        playButton = p.findViewById(R.id.playButton);
        prevButton = p.findViewById(R.id.prevMediaButton);
        nextButton = p.findViewById(R.id.nextMediaButton);
        songArt = p.findViewById(R.id.songImageBackground);
        context = c;

        SetFocuseDirections();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        ContentResolver cr = main.getContentResolver();
        cursor = cr.query(uri, null, selection, null, sortOrder);

        if(cursor != null && cursor.moveToFirst())
        {
            int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            String currentAlbum = "none";
            Bitmap image = null;
            do {
                Song song = new Song();
                song.id = cursor.getLong(songId);
                song.title = cursor.getString(songTitle);
                song.album = cursor.getString(songAlbum);
                song.path = cursor.getString(songData);



                Log.d("songs", song.toString());
                songs.add(song);
                if(!Objects.equals(currentAlbum, song.album))
                {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(song.path);
                    byte[] data = mmr.getEmbeddedPicture();
                   if(data != null){
                    song.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                    image = song.image;}
                }
                else
                {
                    if(image != null) {
                        song.image = image;
                    }
                }

                currentAlbum = song.album;
            } while(cursor.moveToNext());
        }
        View div1 = scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
        View div2 = scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
        main.scrollViewMain.horizontalScrollView.dividers.add(div1);
        main.scrollViewMain.horizontalScrollView.dividers.add(div2);
        populateSongView();

        playButton.setOnFocusChangeListener(playButtonFocusListener);
        playButton.setOnClickListener(playButtonClickListener);
        prevButton.setOnClickListener(prevMediaButtonListener);
        nextButton.setOnClickListener(nextMediaButtonListener);

        if(AlbumList.size() > 0) {
            scrollManager.addVerticalScrollto(div1, scrollView, AlbumList.get(0).findViewById(R.id.collapseButton), 1000);
            scrollManager.addVerticalScrollto(div2, scrollView, playButton, 0);
            scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
            scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
            scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
            scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
            scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
            scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
            scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
            scrollManager.addVertDivider(scrollLayout, main.scrollViewMain);
        }

    }




    private void SetFocuseDirections()
    {

        playButton.setNextFocusDownId(R.id.VertDivider);
        playButton.setNextFocusLeftId(R.id.prevMediaButton);
        playButton.setNextFocusRightId(R.id.nextMediaButton);
        playButton.setNextFocusUpId(R.id.playButton);

        prevButton.setNextFocusDownId(R.id.VertDivider);
        prevButton.setNextFocusRightId(R.id.playButton);
        prevButton.setNextFocusUpId(R.id.prevMediaButton);

        nextButton.setNextFocusDownId(R.id.VertDivider);
        nextButton.setNextFocusRightId(R.id.nextMediaButton);
        nextButton.setNextFocusUpId(R.id.nextMediaButton);
        nextButton.setNextFocusLeftId(R.id.playButton);




    }


    public void playSong(Song song, int index)
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
        }
        songArt.setImageBitmap(song.image);
        mediaPlayer = MediaPlayer.create(main, Uri.parse(song.path));
        mediaPlayer.start();

        Resources res = main.getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, android.R.drawable.ic_media_pause, null);
        playButton.setForeground(drawable);
        currentSong = index;

    }


    public void populateSongView()
    {
        String currentAlbum = "none";
        LinearLayout layout = null;
        CollapsingLinearLayout itemList = null;
        for(int i=0; i<songs.size();i++) {

            if(!currentAlbum.equals(songs.get(i).album)) {
                currentAlbum = songs.get(i).album;
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflater.inflate(R.layout.album_item, scrollLayout, false);
                scrollLayout.addView(layout);
                AlbumList.add(layout);
                Button alBut = layout.findViewById(R.id.collapseButton);
                alBut.setText(songs.get(i).album);
                itemList = (CollapsingLinearLayout) layout.findViewById(R.id.items);


                final CollapsingLinearLayout finalTemp = itemList;
                final LinearLayout finalLayout = layout;

                alBut.setOnClickListener(new View.OnClickListener() {
                    Button cb = alBut;
                    @Override
                    public void onClick(View v) {
                        for(View view : AlbumList) {
                            CollapsingLinearLayout layout = view.findViewById(R.id.items);
                            if(layout != finalTemp)
                                layout.collapse();

                        }

                        finalTemp.toggleCollapse();


                        if(finalLayout == AlbumList.get(AlbumList.size()-1)) {
                            if (finalTemp.isCollapsed()) {
                                cb.setNextFocusDownId(R.id.collapseButton);
                            }
                            else{
                                cb.setNextFocusDownId(NO_ID);
                            }
                        }
                    }
                });

                ImageView image = layout.findViewById(R.id.image);
                image.setImageBitmap(songs.get(i).image);

            }
            if(layout != null && itemList != null) {
                LayoutInflater inflater = (LayoutInflater) itemList.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ConstraintLayout item = (ConstraintLayout) inflater.inflate(R.layout.song_item, itemList, false);
                itemList.addView(item);
                Button button = item.findViewById(R.id.funcButton);
                button.setText(songs.get(i).title);
                final Song song = songs.get(i);
                int finalI = i;
                final CollapsingLinearLayout list =  AlbumList.get(AlbumList.size()-1).findViewById(R.id.items);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playSong(song, finalI);
                        list.collapse();
                        ObjectAnimator animator=ObjectAnimator.ofInt(scrollView, "scrollY", 0 );
                        animator.setStartDelay(0);
                        animator.setDuration(400);
                        animator.start();
                        playButton.requestFocus();

                    }
                });

                if((i+1) < songs.size()-1 && !Objects.equals(songs.get(i + 1).album, currentAlbum))
                {
                    itemList.collapse();
                }

                if(i == songs.size()-1)
                {
                    itemList.collapse();
                    button.setNextFocusDownId(R.id.funcButton);

                }
            }


        }
    }




}
