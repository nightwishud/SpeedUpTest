package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VByteArr extends VArr {
	public VByteArr(){}
	public VByteArr(byte[] v) {
		this.v = v;
	}

	@ElementArray
	public byte[] v;
}
