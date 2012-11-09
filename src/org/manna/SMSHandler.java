package org.manna;

import android.telephony.SmsManager;

public class SMSHandler implements AlertaListener {

	SmsManager smsManager;
	String tel;

	public SMSHandler(String tel) {
		this.tel = tel;
		smsManager = SmsManager.getDefault();
	}

	@Override
	public void alertar(AlertMessage msg) {
		smsManager.sendTextMessage(tel, null, msg.toString(), null, null);
	}

}
