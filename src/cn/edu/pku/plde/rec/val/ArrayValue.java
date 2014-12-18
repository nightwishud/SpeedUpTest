package cn.edu.pku.plde.rec.val;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import cn.edu.pku.plde.rec.data.Data;

public class ArrayValue extends ObjectValue {
	//just for deserialization
	public ArrayValue(){}
	
	public ArrayValue(String type, int len) {
		super(type);
		length = len;
		arrayItems = new ArrayList<Data>(); 
	}
	public ArrayValue(String type) {
		super(type);
		arrayItems = new ArrayList<Data>(); 
	}
	@Attribute
	public int length;
	@ElementList
	public List<Data> arrayItems;
}
