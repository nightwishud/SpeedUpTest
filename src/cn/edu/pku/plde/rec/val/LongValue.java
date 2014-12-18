package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

public class LongValue extends PrimitiveValue {
	//just for deserialization
	public LongValue(){}
	
	public LongValue(long value) {
		this.value = value;
	}
	@Element
	public long value;
}
