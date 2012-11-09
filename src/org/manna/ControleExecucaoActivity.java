package org.manna;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ControleExecucaoActivity extends Activity {

	String TAG = this.getClass().getSimpleName();
	Intent accelerometerHandler;
	TextView status;
	Button btIniciar;
	Button btParar;
	private UpdateReceiver updateReceiver;
	boolean stopped = true;

	public static enum msgType {
		QUEDA, MONITORANDO
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		status = (TextView) findViewById(R.id.tvStatus);
		btIniciar = (Button) findViewById(R.id.button1);
		btIniciar.setOnClickListener(new BtIniciar());
		btParar = (Button) findViewById(R.id.button2);
		btParar.setOnClickListener(new BtParar());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!stopped) {
			unregisterReceiver(updateReceiver);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerUpdateReceiver();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != 1) {
			return;
		}
		try {
			accelerometerHandler = new Intent(ControleExecucaoActivity.this,
					AccelerometerHandler.class);
			startService(accelerometerHandler);
			btIniciar.setEnabled(false);
			btParar.setEnabled(true);
		} catch (Exception e) {
			Log.d(TAG, " problema iniciar serviço de acelerometro");
		}
	}

	public void registerUpdateReceiver() {
		if (updateReceiver == null)
			updateReceiver = new UpdateReceiver();
		IntentFilter intentFilter = new IntentFilter(msgType.QUEDA.name());
		registerReceiver(updateReceiver, intentFilter);
		intentFilter = new IntentFilter(msgType.MONITORANDO.name());
		registerReceiver(updateReceiver, intentFilter);
		stopped = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itemPrefs) {
			startActivity(new Intent(this, PrefsActivity.class));
		}
		return true;
	}

	class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(msgType.QUEDA.name())) {
				Intent queda = new Intent(ControleExecucaoActivity.this,
						QuedaActivity.class);
				startActivity(queda);
			} else {
				status.setText(intent.getAction());
			}
		}

	}

	class BtIniciar implements OnClickListener {

		@Override
		public void onClick(View v) {
			registerUpdateReceiver();
			startActivityForResult(new Intent(ControleExecucaoActivity.this,
					CalibrationActivity.class), 1);
		}

	}

	class BtParar implements OnClickListener {

		@Override
		public void onClick(View v) {
			stopped = true;
			unregisterReceiver(updateReceiver);
			stopService(accelerometerHandler);
			btIniciar.setEnabled(true);
			btParar.setEnabled(false);
			status.setText("PARADO");
		}

	}
}