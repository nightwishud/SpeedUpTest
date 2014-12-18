package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Element;

public class VLong extends VPrim {
	public VLong(){}
	public VLong(long v){
		this.v = v;
	}
	@Element
	public long v;
}
