package org.manna;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class NetworkHandler implements AlertaListener {

	String TAG = this.getClass().getSimpleName();
	Socket socket = null;
	DataInputStream input = null;
	DataOutputStream output = null;

	public NetworkHandler(String hostAddress, int door) {
		try {
			socket = new Socket(hostAddress, door);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			Log.d(TAG, "Host indisponivel");
		} catch (IOException e) {
			Log.d(TAG, "IO problem");
		}
	}

	@Override
	public void alertar(AlertMessage msg) {
		new EnvioAcelerometro().execute(msg);
	}

	class EnvioAcelerometro extends AsyncTask<AlertMessage, Integer, String> {

		@Override
		protected String doInBackground(AlertMessage... params) {
			try {
				output.writeUTF(params[0].toString());
			} catch (Exception e) {
				Log.e(TAG, "erro no envio");
			}
			return "Valores enviados";
		}
	}

}
