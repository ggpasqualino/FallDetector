package org.manna;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class QuedaActivity extends Activity {

	String TAG = this.getClass().getSimpleName();
	Button btTdBem;
	boolean tocarAlarme;
	Timer alertaTimer;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queda);

		btTdBem = (Button) findViewById(R.id.buttonTdBem);
		btTdBem.setOnClickListener(new ClickTdBem());

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		alertaTimer = new Timer(true);
		TimerTask alertaTask = new AlertaHandler();
		alertaTimer.scheduleAtFixedRate(alertaTask, 60 * 1000, 30 * 1000);

		tocarAlarme = true;
		new Alarme().execute((Void) null);
	}

	class ClickTdBem implements OnClickListener {

		@Override
		public void onClick(View v) {
			tocarAlarme = false;
			alertaTimer.purge();
			alertaTimer.cancel();
			finish();
		}

	}

	class Alarme extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Ringtone alarm;
			Uri alarmUri;
			alarmUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_ALARM);
			if (alarmUri == null) {
				alarmUri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
			if (alarmUri == null) {
				alarmUri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
			alarm = RingtoneManager.getRingtone(QuedaActivity.this, alarmUri);
			while (tocarAlarme) {
				alarm.play();
			}
			return null;
		}

	}

	class AlertaHandler extends TimerTask {

		ArrayList<AlertaListener> alertas = new ArrayList<AlertaListener>();
		LocationManager locationManager;

		public AlertaHandler() {
			AlertMessage.pessoa = prefs.getString("usuario", "");
			AlertMessage.message = prefs.getString("msg", "");
			if (prefs.getBoolean("isAlertaWebEnable", false)) {
				alertas.add(new WebHandler(prefs.getString("urlWebService", "")));
			}
			if (prefs.getBoolean("isAlertaSmsEnable", false)) {
				alertas.add(new SMSHandler(prefs.getString("phoneSms", "")));
			}
			if (prefs.getBoolean("isAlertaRedeEnable", false)) {
				alertas.add(new NetworkHandler(prefs.getString("host", ""),
						Integer.parseInt(prefs.getString("porta", "0"))));
			}

			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}

		@Override
		public void run() {
			Location location = null;
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			AlertMessage message = location != null ? new AlertMessage(
					location.getLatitude(), location.getLongitude())
					: new AlertMessage(null, null);
			for (AlertaListener alerta : alertas) {
				alerta.alertar(message);
				Log.d(TAG, "Alerta!!");
			}
		}
	}
}
