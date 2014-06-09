package com.synature.videoplayer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.synature.util.MediaManager;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoPlayer implements OnCompletionListener, OnPreparedListener, 
		OnVideoSizeChangedListener, SurfaceHolder.Callback{
	
	private static final String TAG = "MyMediaPlayer";
	
	private MediaManager mMediaManager;
	
	private ArrayList<HashMap<String, String>> mPlayLst;
	
	private int mVideoWidth;
	private int mVideoHeight;
	private boolean mIsPause = false;
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;
	private int mCurrMediaIndex = -1;
	
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private MediaPlayer mMediaPlayer;
	private MediaPlayerStateListener mListener;
	
	public VideoPlayer(Context c, SurfaceView surfaceView, String mediaDir, 
			MediaPlayerStateListener state){
		mSurfaceView = surfaceView; // for getWidth() and getHeight()
		mSurfaceHolder = surfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mMediaManager = new MediaManager(c, mediaDir);
		mMediaPlayer = new MediaPlayer();
		mListener = state;
	}

    public void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setSoundVolumn(float left, float right){
    	mMediaPlayer.setVolume(left, right);
    }
    
    private void startVideoPlayback() {
		Log.v(TAG, "startVideoPlayback");
		
//		float boxWidth = mSurfaceView.getWidth();
//		float boxHeight = mSurfaceView.getHeight();
//		float videoWidth = mVideoWidth;
//		float videoHeight = mVideoHeight;
//		
//		float widthRatio = boxWidth / videoWidth;
//		float heightRatio = boxHeight / videoHeight;
//		float aspectRatio = videoWidth / videoHeight;
//
//		if (widthRatio > heightRatio)
//			mVideoWidth = (int) (boxHeight * aspectRatio);
//		else
//			mVideoHeight = (int) (boxWidth / aspectRatio);

		mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
		mMediaPlayer.start();
    }

	private void doCleanUp() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		mIsVideoReadyToBePlayed = false;
		mIsVideoSizeKnown = false;
	}
	 
	private void nextTrack(){
		try {
			if(mCurrMediaIndex < (mPlayLst.size() - 1)){
				mCurrMediaIndex ++;
				playMedia();
			}else{
				mCurrMediaIndex = 0;
				readMedia();
				playMedia();
			}
		} catch (Exception e) {
			mListener.onError(e);
			e.printStackTrace();
		}
	}
	
	public void next(){
		nextTrack();
	}
	
	public void back(){
		if(mCurrMediaIndex-- < 0);
			mCurrMediaIndex = 0;
		playMedia();
	}
	
	public void resume(){
		mMediaPlayer.start();
		mIsPause = false;
	}
	
	public void pause(){
		mMediaPlayer.pause();
		mIsPause = true;
	}
	
	private void playMedia(){
		doCleanUp();
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(mPlayLst.get(mCurrMediaIndex).get(MediaManager.FILE_PATH));
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.prepare();
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnVideoSizeChangedListener(this);
			mListener.onPlayedFileName(mPlayLst.get(mCurrMediaIndex).get(MediaManager.FILE_TITLE));
		} catch (IllegalArgumentException e) {
			mListener.onError(e);
			e.printStackTrace();
		} catch (SecurityException e) {
			mListener.onError(e);
			e.printStackTrace();
		} catch (IllegalStateException e) {
			mListener.onError(e);
			e.printStackTrace();
		} catch (IOException e) {
			mListener.onError(e);
			e.printStackTrace();
		}
	}
	
	private void readMedia(){
		mPlayLst = mMediaManager.getVideoPlayList();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void startPlayMedia(){
		readMedia();
		try {
			if(mPlayLst.size() > 0){
				mCurrMediaIndex = 0;
				playMedia();
			}
		} catch (Exception e) {
			mListener.onError(e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		startPlayMedia();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<HashMap<String, String>> getPlayLst() {
		return mPlayLst;
	}

	public static interface MediaPlayerStateListener{
		public void onPlayedFileName(String fileName);
		public void onError(Exception e);
	}

	public boolean isPause(){
		return mIsPause;
	}
	
	@Override
	public void onVideoSizeChanged(android.media.MediaPlayer mp, int width,
			int height) {
		 Log.v(TAG, "onVideoSizeChanged called");
	        if (width == 0 || height == 0) {
	            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
	            return;
	        }
	        mVideoWidth = width;
	        mVideoHeight = height;
	        mIsVideoSizeKnown = true;
	        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
	            startVideoPlayback();
	        }
	}

	@Override
	public void onPrepared(android.media.MediaPlayer mp) {
		 Log.d(TAG, "onPrepared called");
	        mIsVideoReadyToBePlayed = true;
	        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
	        	startVideoPlayback();
	        }
	}

	@Override
	public void onCompletion(android.media.MediaPlayer mp) {
		nextTrack();
	}
}
