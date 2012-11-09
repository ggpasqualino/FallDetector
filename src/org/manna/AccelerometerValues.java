package org.manna;

import java.util.ArrayList;

import android.util.Log;

public class AccelerometerValues {
	private ArrayList<Float> AxisXValues;
	private ArrayList<Float> AxisYValues;
	private ArrayList<Float> AxisZValues;
	static float DELTAX;
	static float DELTAY;
	static float DELTAZ;
	static float MEDIAX;
	static float MEDIAY;
	static float MEDIAZ;

	public AccelerometerValues() {
		AxisXValues = new ArrayList<Float>();
		AxisYValues = new ArrayList<Float>();
		AxisZValues = new ArrayList<Float>();
	}

	void addValue(Float[] values) {
		if (changedFromLast(values)) {
			AxisXValues.add(values[0]);
			AxisYValues.add(values[1]);
			AxisZValues.add(values[2]);
		}
	}

	boolean changedFromLast(Float[] values) {
		if (AxisXValues.isEmpty()) {
			return true;
		}
		int lastIndex = AxisXValues.size() - 1;
		float delta = Math.abs(AxisXValues.get(lastIndex) - values[0]);
		if (delta > DELTAX) {
			return true;
		}
		delta = Math.abs(AxisYValues.get(lastIndex) - values[1]);
		if (delta > DELTAY) {
			return true;
		}
		delta = Math.abs(AxisZValues.get(lastIndex) - values[2]);
		if (delta > DELTAZ) {
			return true;
		}
		return false;
	}

	public static void calcularDeltas(float[][] values) {
		DELTAX = calculaMaxDelta(values[0]);
		DELTAY = calculaMaxDelta(values[1]);
		DELTAZ = calculaMaxDelta(values[2]);
		Log.i("Desvio", "" + DELTAX + " " + DELTAY + " " + DELTAZ);
	}

	private static float calculaMaxDelta(float[] values) {
		int indiceMax = 0;
		int indiceMin = 0;
		for (int i = 1; i < values.length; i++) {
			indiceMax = values[i] > values[indiceMax] ? i : indiceMax;
			indiceMin = values[i] < values[indiceMin] ? i : indiceMin;
		}
		return Math.abs(values[indiceMax] - values[indiceMin]);
	}

	public static void calcularMedias(float[][] values) {
		MEDIAX = calcularMedia(values[0]);
		MEDIAY = calcularMedia(values[1]);
		MEDIAZ = calcularMedia(values[2]);
	}

	private static float calcularMedia(float[] values) {
		float media = 0;
		for (float f : values) {
			media = media + f;
		}
		media = media / (float) values.length;
		return media;
	}

	public static void calcularDesvios(float[][] values) {
		DELTAX = calcularDesvioPadrao(values[0], MEDIAX);
		DELTAY = calcularDesvioPadrao(values[1], MEDIAY);
		DELTAZ = calcularDesvioPadrao(values[2], MEDIAZ);
		Log.i("Desvio", " " + DELTAX + " " + DELTAY + " " + DELTAZ);
	}

	private static float calcularDesvioPadrao(float[] values, float media) {
		float variancia = 0;
		for (float f : values) {
			variancia = (float) (variancia + Math.pow((float) (f - media), 2));
		}
		variancia = variancia / (float) (values.length - 1);
		return (float) Math.sqrt((double) variancia);
	}

	public int size() {
		return AxisXValues.size();
	}

	public Float[] getValue(int index) {
		Float[] value = new Float[3];
		value[0] = AxisXValues.get(index);
		value[1] = AxisYValues.get(index);
		value[2] = AxisZValues.get(index);
		return value;
	}

	public void removeValue(int index) {
		if (index <= this.size() && index >= 0) {
			AxisXValues.remove(index);
			AxisYValues.remove(index);
			AxisZValues.remove(index);
		}
	}

	public ArrayList<Float> getAxisXValues() {
		return AxisXValues;
	}

	public ArrayList<Float> getAxisYValues() {
		return AxisYValues;
	}

	public ArrayList<Float> getAxisZValues() {
		return AxisZValues;
	}

	public double calcularAceleracao(int indice) {
		double acel = Math.pow(AxisXValues.get(indice), 2);
		acel = acel + Math.pow(AxisYValues.get(indice), 2);
		acel = acel + Math.pow(AxisZValues.get(indice), 2);
		acel = Math.sqrt(acel);
		Log.i("Aceleracao", "" + acel);
		return acel;
	}

	public void clear() {
		AxisXValues.clear();
		AxisYValues.clear();
		AxisZValues.clear();
	}

}
