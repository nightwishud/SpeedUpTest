package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VLongArr extends VArr {
	public VLongArr(){}
	public VLongArr(long[] v) {
		this.v = v;
	}

	@ElementArray
	public long[] v;
}
