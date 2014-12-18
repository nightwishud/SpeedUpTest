package cn.edu.pku.plde.rec.val;

import org.simpleframework.xml.Element;

import cn.edu.pku.plde.rec.val.NullValue;

public class NullValue extends ObjectValue {
	public NullValue() {// TODO::check
		super("null");
		isNull = true;
	}
	//singleton
	private static NullValue inst = null;
	public static NullValue getInstance(){
		if(inst == null){
			inst = new NullValue();
		}
		return inst;
	}
	@Element
	public static final String value = "null";
}
