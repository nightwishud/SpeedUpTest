package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VByte extends VPrim {
	public VByte(){}
	public VByte(byte v){
		this.v = v;
	}
	@Element
	public byte v;
}
