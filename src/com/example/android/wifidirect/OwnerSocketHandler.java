package com.example.android.wifidirect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.util.Log;



public class OwnerSocketHandler extends Thread {

    ServerSocket socket = null;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private static final String TAG = "GroupOwnerSocketHandler";

    public OwnerSocketHandler(Handler handler) throws IOException {
        try {
            socket = new ServerSocket(1234);
            this.handler = handler;
            Log.d(TAG, "000: " + handler);
            Log.d(TAG, "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }

    }

    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void run() {
        while(true){
    	try {
			    // A blocking operation. Initiate a ChatManager instance when
				// there is a new connection
					pool.execute(new ChatManager(socket.accept(), handler,true));
				    Log.d(TAG, "有的话 就是socket脸上了，要不然就循环等待了");
				    Log.d(TAG, "服务器chatManager线程启动");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
    }

}
