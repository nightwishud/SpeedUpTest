package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VInt extends VPrim {
	public VInt(){}
	public VInt(int v){
		this.v = v;
	}
	@Element
	public int v;
}
