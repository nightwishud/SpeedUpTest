package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VShort extends VPrim{
	public VShort(){}
	public VShort(short v){
		this.v = v;
	}
	@Element
	public short v;
}
