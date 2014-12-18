package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VChar extends VPrim{
	public VChar(){}
	public VChar(char v){
		this.v = v;
	}
	@Element
	public char v;
}
