package com.example.android.wifidirect;

import java.io.Serializable;
import java.net.Socket;

public class TransferSocket implements Serializable{

	
	private Socket socket;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
