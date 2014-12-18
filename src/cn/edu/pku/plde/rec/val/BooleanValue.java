package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

public class BooleanValue extends PrimitiveValue {
	//just for deserialization
	public BooleanValue(){}
	
	public BooleanValue(boolean value) {
		this.value = value;
	}
	@Element
	public boolean value;
}
