package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VBoolArr extends VArr {
	public VBoolArr(){}
	public VBoolArr(boolean[] v) {
		this.v = v;
	}
	@ElementArray
	public boolean[] v;
}
