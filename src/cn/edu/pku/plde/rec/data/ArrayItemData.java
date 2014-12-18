package cn.edu.pku.plde.rec.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import cn.edu.pku.plde.rec.val.Value;

public class ArrayItemData extends Data {
	
	//just for deserialization
	public ArrayItemData(){}
	
	public ArrayItemData(String t, Value val){
		type = t;
		value = val;
	}
	@Attribute
	public String type;
	@Element
	public Value value;
}
