package org.manna;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AlertMessage {
	static String pessoa;
	static String message;
	Double latitude;
	Double longitude;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	public AlertMessage(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static String getPessoa() {
		return pessoa;
	}

	public static String getMessage() {
		return message;
	}

	public String getDataEnvio() {
		return sdf.format(new Date());
	}

	public String getLocal() {
		if (latitude != null && longitude != null) {
			return String.format("%.4f,%.4f", latitude, longitude);
		}
		return "";
	}

	@Override
	public String toString() {
		return String.format("%s, %s, em %s, às %s", pessoa, message,
				getLocal(), getDataEnvio());
	}

}
