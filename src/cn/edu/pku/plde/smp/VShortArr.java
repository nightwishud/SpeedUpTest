package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VShortArr extends VArr {
	public VShortArr(){}
	public VShortArr(short[] v) {
		this.v = v;
	}

	@ElementArray
	public short[] v;
}
