package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VBool extends VPrim {
	public VBool(){}
	public VBool(boolean v){
		this.v = v;
	}
	@Element
	public boolean v;
}
