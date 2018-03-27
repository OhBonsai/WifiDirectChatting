package com.example.android.wifidirect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;


public class ClientSocketHandler extends Thread{
	   private static final String TAG = "ClientSocketHandler";
	    private Handler handler;
	    private ChatManager chat;
	    private String mAddress;
	    private int port;

	    public ClientSocketHandler(Handler handler, String mAddress, int port) {
	        this.handler = handler;
	        Log.d(TAG, "000: " + handler);
	        this.mAddress = mAddress;
	        this.port = port;
	    }

	    @Override
	    public void run() {
	    	Log.d(TAG,"客户端开始执行");
	        Socket socket = new Socket();
	        try {
	        	
	            socket.bind(null);
	            socket.connect(new InetSocketAddress(mAddress,port), 5000);
	            Log.d(TAG, "Launching the I/O handler");
	            chat = new ChatManager(socket, handler,false);
	            new Thread(chat).start();
	            Log.d(TAG, "客户端chatManager线程启动");
	        } catch (IOException e) {
	            e.printStackTrace();
	            try {
	                socket.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	            return;
	        }
	    }

	    public ChatManager getChat() {
	        return chat;
	    }

	}
