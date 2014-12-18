package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VString extends V {
	public VString(){}
	public VString(String v){
		this.v = v;
	}
	@Element
	public String v;
}
