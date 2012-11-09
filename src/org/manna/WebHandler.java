package org.manna;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class WebHandler implements AlertaListener {

	String TAG = this.getClass().getSimpleName();
	HttpClient client;
	String address;

	public WebHandler(String address) {
		this.address = address;
		client = new DefaultHttpClient();
	}

	@Override
	public void alertar(AlertMessage msg) {
		new WebPost().execute(msg);
	}

	class WebPost extends AsyncTask<AlertMessage, Integer, String> {

		@Override
		protected String doInBackground(AlertMessage... params) {
			Log.d(TAG, "enviando");
			HttpPost post = new HttpPost(address);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("msg", AlertMessage.getMessage()));
			pairs.add(new BasicNameValuePair("dataEnvio", params[0]
					.getDataEnvio()));
			pairs.add(new BasicNameValuePair("local", params[0].getLocal()));
			pairs.add(new BasicNameValuePair("pessoa", AlertMessage.getPessoa()));
			try {
				post.setEntity(new UrlEncodedFormEntity(pairs));
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Encoding nao suportado");
			} catch (Exception e) {
				Log.e(TAG, "erro no envio");
			}
			try {
				HttpResponse response = client.execute(post);
				Log.d(TAG, response.toString());
			} catch (ClientProtocolException e) {
				Log.e(TAG, "Excecao protocolo");
			} catch (IOException e) {
				Log.e(TAG, "Erro de IO");
			} catch (Exception e) {
				Log.e(TAG, "erro no envio");
			}
			return null;
		}

	}

}
