package com.example.android.wifidirect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class InterPhoneActivity extends Activity{

	
	private final String TAG = "interphoneactivity";
	
	private InterPhoneService mPhoneService = InterPhoneService.getInterPhoneService();
	
	//��FragmentDetail���ﴫ������sokcet
	private Socket socket = null;
	
	//��FragmentDetail���ﴫ������isOwner
	private boolean isOwner = false;
	
	//¼����ʼ
	private AudioRecord mRecorder = null;
	private boolean mInRecording = false;
	private int mRecordBufferSize = 0;
	private static final int mSampleRate = 44100;
	private static final int mRecordChannel = AudioFormat.CHANNEL_IN_MONO;
	private static final int mTrackChannel = AudioFormat.CHANNEL_OUT_MONO;
	private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
	
	//���ų�ʼ
	private AudioTrack mPlayer = null;
	
	
	private final Handler mMsgHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			//���ﱾ������mPhoneService.MSG_CONTENT_READED
			//��֪��Ϊʲô����,����ֱ��дֵ��
			case 0:
				byte[] readBuf = (byte[]) msg.obj;
				int readBytes = msg.arg1;
				if (null != mPlayer)
				{
					mPlayer.write(readBuf, 0, readBytes);
				}
				break;
			case 1:
				break;
			case 2:
				//���ǽ�transfer socket������
				TransferSocket transferSocket = (TransferSocket)msg.obj;
				Log.d(TAG, "�õ�socket��");
				socket = transferSocket.getSocket();
				if(null != socket){
					Log.d(TAG, "socket����Ч��");
					 
					  mPhoneService.setSocket(socket);
					    mPhoneService.setMsgHandler(mMsgHandler);
				        mPhoneService.setIsOwner(isOwner);
				        mPhoneService.beginTransferData();
		           
		            Log.d(TAG,"mPhoneService �Ѿ�����socket");
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);

		};
	};
	
	
	
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
        this.setContentView(R.layout.voice_activity);
        
    
        
        int trackBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mTrackChannel, mAudioFormat);
        Log.d(TAG, "1");
        mPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mTrackChannel, mAudioFormat, trackBufferSize, AudioTrack.MODE_STREAM);
        Log.d(TAG, "2");
		mRecordBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mRecordChannel, mAudioFormat);
		Log.d(TAG, "3");
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRate, mRecordChannel, mAudioFormat, mRecordBufferSize);
		Log.d(TAG, "4");
        ImageButton recordBtn = (ImageButton)this.findViewById(R.id.btnSpeaking);
        Log.d(TAG, "5");
        recordBtn.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				switch (action)
				{
				case MotionEvent.ACTION_DOWN:
					onBtnRecord(true);
					break;
				case MotionEvent.ACTION_UP:
					onBtnRecord(false);
					break;
				}
				return false;
			}
			
		});      
        
        
        Intent intent = getIntent();
        isOwner = intent.getExtras().getBoolean("isOwner");
        if(isOwner){
        	new OwnerSocketThread(mMsgHandler).start();
        	Log.d(TAG, "�������߳�socket��������");
        }else{
        	new ClientSocketThread(mMsgHandler).start();
        	Log.d(TAG,"�ͻ����߳�socket��������");
        }
        

    
      

        
        
        
     
        	
        	
        
        
	};
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (null != mPlayer)
		{
			mPlayer.pause();
		}
	}
	
	@Override
	protected void onStop() {
		
		mPhoneService.stopTransferData();
		mPlayer.stop();
		mPlayer.release();
		mRecorder.release();
		Log.d(TAG, "�����߳�ֹͣ��");
		super.onStop();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (null != mPlayer)
		{
			mPlayer.play();
		}
	}
	
	private void onBtnRecord(boolean start)
	{
		if (start)
		{
			mInRecording = true;
			AudioRecordThread recordThread = new AudioRecordThread();
			recordThread.start();
		}
		else
		{
			mInRecording = false;
		}
	}
	
	class AudioRecordThread extends Thread{
		@Override
		public void run() {
			Log.d(TAG, "�ҿ�ʼ��¼������1");
			try
			{
				Log.d(TAG, "�ҿ�ʼ��¼������2");
				byte[] buffer = new byte[mRecordBufferSize];
				Log.d(TAG, "�ҿ�ʼ��¼������3");
				
				mRecorder.startRecording();
				Log.d(TAG, "�ҿ�ʼ��¼������4");
				while (mInRecording)
				{
					int readSize = mRecorder.read(buffer, 0, mRecordBufferSize);
					byte[] sendBuffer = new byte[readSize];
					System.arraycopy(buffer, 0, sendBuffer, 0, readSize);
					Log.d(TAG, "��Ŀ�ʼ��¼��");
					mPhoneService.sendDataToRemote(sendBuffer, 0, readSize);
					Log.d(TAG,"��������");
				}
				mRecorder.stop();			
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}		
	}
	
	class OwnerSocketThread extends Thread{
		private Handler handler;
		 
		
		
		public OwnerSocketThread(Handler handler) {
			// TODO Auto-generated constructor stub
			this.handler  = handler;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				ServerSocket serverSocket = new ServerSocket(1024);
				Socket socket = null;
				TransferSocket transferSocket = new TransferSocket();
				while((socket = serverSocket.accept())!=null){
					
					transferSocket.setSocket(socket);
					
					handler.obtainMessage(2,transferSocket).sendToTarget();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			super.run();
		}
	}
	
	
	class ClientSocketThread extends Thread{
		private Handler handler;
		 
		
		
		public ClientSocketThread(Handler handler) {
			// TODO Auto-generated constructor stub
			this.handler  = handler;
		}
		
		@SuppressWarnings("null")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Log.d(TAG, "�ͻ������ӳɹ�1");
				
				 Socket socket = new Socket();
				 socket.bind(null);
				 Log.d(TAG, "�ͻ������ӳɹ�2");
		         socket.connect(new InetSocketAddress("192.168.49.1",1024), 1000);
		         Log.d(TAG, "�ͻ������ӳɹ�3");
				TransferSocket transferSocket = new TransferSocket();
				if(null != socket){
					transferSocket.setSocket(socket);
					handler.obtainMessage(2,transferSocket).sendToTarget();
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			super.run();
		}
	}

	
	}
