package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

import cn.edu.pku.plde.rec.val.PrimitiveValue;

public class ByteValue extends PrimitiveValue {
	//just for deserialization
	public ByteValue(){}
	
	public ByteValue(byte value) {
		this.value = value;
	}
	@Element
	public byte value;
}
