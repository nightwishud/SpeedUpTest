package cn.edu.pku.plde.rec.etr;

import org.simpleframework.xml.Attribute;

import cn.edu.pku.plde.rec.val.Value;

public class Parameter extends Entry {
	//just for deserialization
	public Parameter(){}
	
	public Parameter(int id, Value value) {
		this.id = id;
		this.value = value;
	}
	@Attribute
	public int id;
}
