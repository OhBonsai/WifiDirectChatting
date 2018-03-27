package com.example.android.wifidirect;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;



import android.os.Handler;
import android.util.Log;

public class ChatManager implements Runnable{


    private Socket socket = null;
    private Handler handler;
    private boolean isOwner;

    public ChatManager(Socket socket, Handler handler, boolean isOwner) {
        this.socket = socket;
        this.handler = handler;
        this.isOwner = isOwner;
        Log.d(TAG, "ʵ����chatmanager�ɹ�");

    }


    private ObjectOutputStream ooStream;
    private ObjectInputStream oiStream;
    private static final String TAG = "ChatHandler";
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 try {
			    Log.d(TAG, "����run����");
	            
			    if(isOwner){
			    	  oiStream = new ObjectInputStream(socket.getInputStream());
				      Log.d(TAG, "��������������");   	
			    	 ooStream = new ObjectOutputStream(socket.getOutputStream());
			         Log.d(TAG, "�������������");
			           
			    }else{
			    	ooStream = new ObjectOutputStream(socket.getOutputStream());
		            Log.d(TAG, "�������������");
		            oiStream = new ObjectInputStream(socket.getInputStream());
		            Log.d(TAG, "��������������");
			    	
			    }
	            
	            ChatMsgEntity entity = new ChatMsgEntity();
	            Log.d(TAG, "������Ϣʵ��");
	            handler.obtainMessage(ChatActivity.MY_HANDLE, this)
	                    .sendToTarget();
	            Log.d(TAG, "�Ѿ���ʼ��my_handler�����activity");
	            

	            Log.d(TAG, "000��" +handler);
	            while (true) {
	                try {
	                    // Read from the InputStream
	                    entity = (ChatMsgEntity) oiStream.readObject();
	                    Log.d(TAG, "Rec:" + String.valueOf(entity.getText()));
	                    handler.obtainMessage(ChatActivity.MESSAGE_READ,
	                            entity).sendToTarget();
	                } catch (IOException e) {
	                    Log.e(TAG, "disconnected", e);
	                } catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                socket.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	}
	
	public void write(ChatMsgEntity entity) {
		// TODO Auto-generated method stub
		try{
			ooStream.writeObject(entity);
			ooStream.flush();
		}catch(IOException e){
			Log.e(TAG, "Exception during write", e);
		}
	}

}
