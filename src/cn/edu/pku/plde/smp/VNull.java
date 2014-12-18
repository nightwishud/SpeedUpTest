package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Attribute;

public class VNull extends V {
	public VNull(){}
	public static VNull getInstance(){
		if(v == null){
			v = new VNull();
		}
		return v;
	}
	private static VNull v;
	@Attribute
	public static final String value = "null";
	
}
