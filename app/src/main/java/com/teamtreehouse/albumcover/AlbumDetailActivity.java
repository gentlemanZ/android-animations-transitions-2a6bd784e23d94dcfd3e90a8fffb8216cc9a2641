package com.teamtreehouse.albumcover;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.graphics.Palette;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.teamtreehouse.albumcover.transition.Fold;
import com.teamtreehouse.albumcover.transition.Scale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlbumDetailActivity extends Activity {

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";

    @Bind(R.id.album_art) ImageView albumArtView;
    @Bind(R.id.fab) ImageButton fab;
    @Bind(R.id.title_panel) ViewGroup titlePanel;
    @Bind(R.id.track_panel) ViewGroup trackPanel;
    @Bind(R.id.detail_container) ViewGroup detailContainer;

    private TransitionManager mTransitionManager;
    private  Scene mExpandedScene;
    private Scene mCollapsedScene;
    private  Scene mCurrentScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        populate();
        setupTransitions();
    }
    //for 3/27 adding animation on floating animation button.
//    private void animate(){
////        ObjectAnimator scalex = ObjectAnimator.ofFloat(fab,"scaleX",0,1);
////        ObjectAnimator scaley = ObjectAnimator.ofFloat(fab,"scaleY",0,1);
////        AnimatorSet scaleFab = new AnimatorSet();
////        scaleFab.playTogether(scalex,scaley);
//        Animator scaleFab = AnimatorInflater.loadAnimator(this,R.animator.scale);
//        scaleFab.setTarget(fab);
//
//        int titleStartValue = titlePanel.getTop();
//        int titleEndValue = titlePanel.getBottom();
//        ObjectAnimator animatorTitle = ObjectAnimator.ofInt(titlePanel,"bottom",titleStartValue,titleEndValue);
//        animatorTitle.setInterpolator(new AccelerateInterpolator());
//
//        int trackStartValue = trackPanel.getTop();
//        int trackEndValue = trackPanel.getBottom();
//        ObjectAnimator animatorTrack= ObjectAnimator.ofInt(trackPanel,"bottom",trackStartValue,trackEndValue);
//        animatorTrack.setInterpolator(new DecelerateInterpolator());
//        //need to make the view disappear when we open the view.
//        titlePanel.setBottom(titleStartValue);
//        trackPanel.setBottom(titleStartValue);
//        fab.setScaleX(0);
//        fab.setScaleY(0);
//
//        //animatorTitle.setDuration(1000);
//        //animatorTrack.setDuration(1000);
//        //animatorTitle.setStartDelay(1000);
//
//        AnimatorSet set = new AnimatorSet();
//        set.playSequentially(animatorTitle,animatorTrack, scaleFab);
//        set.start();
//    }

    private Transition createTransition(){
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Transition tFab = new Scale();
        tFab.setDuration(150);
        tFab.addTarget(fab);

        Transition tTitle = new Fold();
        tTitle.setDuration(150);
        tTitle.addTarget(titlePanel);

        Transition tTrack = new Fold();
        tTrack.setDuration(150);
        tTrack.addTarget(titlePanel);

        set.addTransition(tTrack);
        set.addTransition(tTitle);
        set.addTransition(tFab);
        return set;

    }
    @OnClick(R.id.album_art)
    public void onAlbumArtClick(View view) {
        Transition transition = createTransition();
        TransitionManager.beginDelayedTransition(detailContainer,transition);
        fab.setVisibility(View.INVISIBLE);
        titlePanel.setVisibility(View.INVISIBLE);
        trackPanel.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.track_panel)
    public void onTrackPanelClicked(View view){
        if(mCurrentScene == mExpandedScene){
            mCurrentScene = mCollapsedScene;
        }
        else {
            mCurrentScene = mExpandedScene;
        }
        mTransitionManager.transitionTo(mCurrentScene);
    }

    private void setupTransitions() {
//        Slide slide = new Slide(Gravity.BOTTOM);
//        slide.excludeTarget(android.R.id.statusBarBackground, true);
//        getWindow().setEnterTransition(slide);
//        getWindow().setSharedElementsUseOverlay(false);

        mTransitionManager = new TransitionManager();
        ViewGroup transitionRoot = detailContainer;

        // Expanded scene
        mExpandedScene = Scene.getSceneForLayout(transitionRoot, R.layout.activity_album_detail_expended, this);

        mExpandedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mExpandedScene;
            }
        });

        TransitionSet expandTransitionSet= new TransitionSet();
        expandTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        ChangeBounds changeBounds =new ChangeBounds();
        changeBounds.setDuration(200);
        expandTransitionSet.addTransition(changeBounds);
        Fade fadeLyrics = new Fade();
        fadeLyrics.setDuration(150);
        fadeLyrics.addTarget(R.id.lyrics);
        expandTransitionSet.addTransition(fadeLyrics);

        // Collapsed scene
        mCollapsedScene = Scene.getSceneForLayout(transitionRoot, R.layout.activity_album_detail, this);

        mCollapsedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mCollapsedScene;
            }
        });

        TransitionSet collapseTransitionSet= new TransitionSet();
        collapseTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Fade fadeOutLyrics = new Fade();
        fadeOutLyrics.setDuration(150);
        fadeOutLyrics.addTarget(R.id.lyrics);
        collapseTransitionSet.addTransition( fadeOutLyrics);

        ChangeBounds resetBounds =new ChangeBounds();
        resetBounds.setDuration(200);
        collapseTransitionSet.addTransition(resetBounds);

        mTransitionManager.setTransition(mExpandedScene,mCollapsedScene,collapseTransitionSet);
        mTransitionManager.setTransition(mCollapsedScene,mExpandedScene,expandTransitionSet);
        mCollapsedScene.enter();

//        postponeEnterTransition();
    }

    private void populate() {

        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
        albumArtView.setImageResource(albumArtResId);

        Bitmap albumBitmap = getReducedBitmap(albumArtResId);
        colorizeFromImage(albumBitmap);

//        startPostponedEnterTransition();
    }

    private Bitmap getReducedBitmap(int albumArtResId) {
        // reduce image size in memory to avoid memory errors
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 8;
        return BitmapFactory.decodeResource(getResources(), albumArtResId, options);
    }

    private void colorizeFromImage(Bitmap image) {
        Palette palette = Palette.from(image).generate();

        // set panel colors
        int defaultPanelColor = 0xFF808080;
        int defaultFabColor = 0xFFEEEEEE;
        titlePanel.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
        trackPanel.setBackgroundColor(palette.getLightMutedColor(defaultPanelColor));

        // set fab colors
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[]{
                palette.getVibrantColor(defaultFabColor),
                palette.getLightVibrantColor(defaultFabColor)
        };
//        fab.setBackgroundTintList(new ColorStateList(states, colors));
    }
}
