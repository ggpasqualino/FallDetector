package org.manna;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CalibrationActivity extends Activity implements
		SensorEventListener {

	String TAG = CalibrationActivity.class.getSimpleName();
	TextView tvContagemCalibragem;
	ProgressBar pbCalibragem;

	static boolean calibrado;
	static int numAmostrasParaCalibrar;
	static float[] amostrasMax;
	static float[] amostrasMin;
	static int indiceCalibrar;

	SensorManager sensorManager;
	Sensor accelerometerSensor;

	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calibration);
		tvContagemCalibragem = (TextView) findViewById(R.id.tvContagemCalibragem);
		pbCalibragem = (ProgressBar) findViewById(R.id.pbCalibragem);
		pbCalibragem.setProgress(0);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		calibrado = prefs.getBoolean("calibrado", false);
		numAmostrasParaCalibrar = Integer.parseInt(prefs.getString(
				"numAmostras", "50"));
		if (calibrado) {
			finish();
		} else {
			indiceCalibrar = 0;
			amostrasMax = new float[3];
			amostrasMin = new float[3];
			Log.d(TAG, "iniciar contagem");
			new Contagem().execute();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
			return;
		}
		if (indiceCalibrar < numAmostrasParaCalibrar) {
			inserirValorCalibragem(event);
		} else {
			sensorManager.unregisterListener(this);
			String[] deltasName = { "deltaX", "deltaY", "deltaZ" };
			Editor editor = prefs.edit();
			for (int i = 0; i < amostrasMax.length; i++) {
				editor.putString(deltasName[i],
						Float.toString(amostrasMax[i] - amostrasMin[i]));
			}
			editor.putBoolean("calibrado", true);
			editor.commit();
			finish();
		}
	}

	private void inserirValorCalibragem(SensorEvent event) {
		if (indiceCalibrar == 0) {
			for (int i = 0; i < amostrasMax.length; i++) {
				amostrasMax[i] = amostrasMin[i] = event.values[i];
			}
		} else {
			novoAmostrasMax(event);
			novoAmostrasMin(event);
		}
		indiceCalibrar++;
		pbCalibragem
				.setProgress((int) (((float) indiceCalibrar / numAmostrasParaCalibrar) * 100));
	}

	private void novoAmostrasMin(SensorEvent event) {
		for (int i = 0; i < amostrasMin.length; i++) {
			if (event.values[i] < amostrasMin[i]) {
				amostrasMin[i] = event.values[i];
			}
		}
	}

	private void novoAmostrasMax(SensorEvent event) {
		for (int i = 0; i < amostrasMax.length; i++) {
			if (event.values[i] > amostrasMax[i]) {
				amostrasMax[i] = event.values[i];
			}
		}
	}

	class Contagem extends AsyncTask<Void, Void, Void> {

		int secs = 3;
		int deltaSec = 1000;

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "contagem");
			while (secs > 0) {
				long start = SystemClock.elapsedRealtime();
				while ((SystemClock.elapsedRealtime() - start) < deltaSec)
					;
				secs--;
				publishProgress();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			tvContagemCalibragem.setText("em " + secs);
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			tvContagemCalibragem.setText("");
			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			accelerometerSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(CalibrationActivity.this,
					accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		}

	}

}
