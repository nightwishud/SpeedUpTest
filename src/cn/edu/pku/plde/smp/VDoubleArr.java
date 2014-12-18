package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VDoubleArr extends VArr {
	
	public VDoubleArr(){}
	public VDoubleArr(double[] v) {
		this.v = v;
	}

	@ElementArray
	public double[] v;
}
