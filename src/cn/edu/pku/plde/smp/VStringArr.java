package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VStringArr extends VArr {
	public VStringArr(){}
	public VStringArr(String[] v) {
		this.v = v;
	}

	@ElementArray
	public String[] v;
}
