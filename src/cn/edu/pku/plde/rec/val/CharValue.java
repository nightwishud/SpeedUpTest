package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

public class CharValue extends PrimitiveValue {
	//just for deserialization
	public CharValue(){}
	
	public CharValue(char value) {
		this.value = value;
	}
	@Element
	public char value;
}
