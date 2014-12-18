package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

public class DoubleValue extends PrimitiveValue {
	//just for deserialization
	public DoubleValue(){}
	
	public DoubleValue(double value) {
		this.value = value;
	}
	@Element
	public double value;
}
