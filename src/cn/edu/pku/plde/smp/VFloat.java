package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VFloat extends VPrim{
	public VFloat(){}
	public VFloat(float v){
		this.v = v;
	}
	@Element
	public float v;
}
