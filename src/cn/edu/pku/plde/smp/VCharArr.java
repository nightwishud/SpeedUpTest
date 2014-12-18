package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VCharArr extends VArr {
	public VCharArr(){}
	public VCharArr(char[] v) {
		this.v = v;
	}

	@ElementArray
	public char[] v;
}
