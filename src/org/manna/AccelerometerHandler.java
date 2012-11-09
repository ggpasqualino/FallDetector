package org.manna;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class AccelerometerHandler extends Service implements
		SensorEventListener {

	String TAG = this.getClass().getSimpleName();
	SensorManager sensorManager;
	Sensor accelerometerSensor;
	static AccelerometerValues ACCELEROMETER_VALUES;

	Timer analiseTimer;

	@Override
	public void onCreate() {
		super.onCreate();
		HandlerThread thread = new HandlerThread("AccelerometerHandler",
				android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		AccelerometerValues.DELTAX = Float.parseFloat(prefs.getString("deltaX",
				"0"));
		AccelerometerValues.DELTAY = Float.parseFloat(prefs.getString("deltaY",
				"0"));
		AccelerometerValues.DELTAZ = Float.parseFloat(prefs.getString("deltaZ",
				"0"));
		ACCELEROMETER_VALUES = new AccelerometerValues();

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometerSensor,
				SensorManager.SENSOR_DELAY_GAME);

		analiseTimer = new Timer(true);
		TimerTask analiseTask = new AnaliseQueda();
		analiseTimer.scheduleAtFixedRate(analiseTask, 50, 500);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(this);
		analiseTimer.purge();
		analiseTimer.cancel();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
			return;
		} else {
			inserirValor(event);
		}

	}

	private void inserirValor(SensorEvent event) {
		notificar(ControleExecucaoActivity.msgType.MONITORANDO);
		Float[] values = new Float[3];
		values[0] = new Float(event.values[0]);
		values[1] = new Float(event.values[1]);
		values[2] = new Float(event.values[2]);
		ACCELEROMETER_VALUES.addValue(values);
	}

	public void notificar(ControleExecucaoActivity.msgType msg) {
		sendBroadcast(new Intent(msg.name()));
	}

	class AnaliseQueda extends TimerTask {

		double limiarAceleracao = 2.5;

		@Override
		public void run() {
			while (ACCELEROMETER_VALUES.size() > 0) {
				double accel = ACCELEROMETER_VALUES.calcularAceleracao(0);
				ACCELEROMETER_VALUES.removeValue(0);
				Log.i("Aceleracao", "" + accel);
				if (accel < limiarAceleracao) {
					notificar(ControleExecucaoActivity.msgType.QUEDA);
					Log.i("Queda", "Queda");
					ACCELEROMETER_VALUES.clear();
				}
			}
		}

	}
}
