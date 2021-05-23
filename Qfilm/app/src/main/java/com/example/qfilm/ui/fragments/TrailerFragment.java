package com.example.qfilm.ui.fragments;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qfilm.R;

import com.example.qfilm.data.models.entities.Video;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController;


public class TrailerFragment extends DialogFragment {

    private static final String TAG = "TrailerFragment";

    private NavigationInterface navigationInterface;

    private Video video;

    public TrailerFragment(Video video) {

        this.video = video;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        navigationInterface = (NavigationInterface) requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trailer, container, false);

        YouTubePlayerView youTubePlayerView = view.findViewById(R.id.youtube_player_view);

        startTrailer(youTubePlayerView);

        // click listener for navigate back or close dialog

        view.findViewById(R.id.btn_close_trailer).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(getResources().getBoolean(R.bool.is_tablet)){

                    dismiss();

                }else {

                    navigationInterface.onBackPressed();
                }

            }
        });

        return view;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        // video is allowed to be viewed in landscape mode
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        if(getResources().getBoolean(R.bool.is_tablet)) {

            Dialog dialog = getDialog();

            if (dialog != null) {

                UiUtil.setDialogFragmentDimensions(dialog);

                dialog.getWindow().setWindowAnimations(R.style.Window_DialogAnimationFade);

            }
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        // changes to landscape mode, percentage of parent increases to 100 % for video

        if(getResources().getBoolean(R.bool.is_tablet)) {

            Dialog dialog = getDialog();

            if (dialog != null) {

                UiUtil.setDialogFragmentDimensions(dialog);

                dialog.getWindow().setWindowAnimations(R.style.Window_DialogAnimationFade);

            }

        }
    }


    private void startTrailer(YouTubePlayerView youTubePlayerView) {

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.enterFullScreen();

        PlayerUiController playerUiController = youTubePlayerView.getPlayerUiController();

        playerUiController.showVideoTitle(false);

        String finalKey = video.getKey();

        youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {

            if(finalKey != null) {
                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer,
                        getLifecycle(),
                        finalKey,
                        0
                );
            }
        });

    }



    @Override
    public void onPause() {

        super.onPause();

        // rest of application can only be viewed in portrait mode for now
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


}