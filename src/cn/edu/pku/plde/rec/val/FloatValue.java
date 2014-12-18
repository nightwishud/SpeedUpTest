package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

import cn.edu.pku.plde.rec.val.PrimitiveValue;

public class FloatValue extends PrimitiveValue {
	//just for deserialization
	public FloatValue(){}
	
	public FloatValue(float value) {
		this.value = value;
	}
	@Element
	public float value;
}
