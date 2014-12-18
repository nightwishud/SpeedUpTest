package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

public class IntValue extends PrimitiveValue {
	//just for deserialization
	public IntValue(){}
	
	public IntValue(int value) {
		this.value = value;
	}
	@Element
	public int value;
}
