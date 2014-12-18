package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VDouble extends VPrim {
	public VDouble(){}
	public VDouble(double v){
		this.v = v;
	}
	@Element
	public double v;
}
