package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VFloatArr extends VArr {
	public VFloatArr(){}
	public VFloatArr(float[] v) {
		this.v = v;
	}

	@ElementArray
	public float[] v;
}
