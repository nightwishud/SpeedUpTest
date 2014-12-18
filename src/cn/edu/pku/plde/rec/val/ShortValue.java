package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

public class ShortValue extends PrimitiveValue {
	//just for deserialization
	public ShortValue(){}
	
	public ShortValue(short value) {
		this.value = value;
	}
	@Element
	public short value;
}
