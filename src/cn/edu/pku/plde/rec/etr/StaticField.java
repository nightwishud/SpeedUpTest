package cn.edu.pku.plde.rec.etr;

import org.simpleframework.xml.Attribute;

import cn.edu.pku.plde.rec.val.Value;

public class StaticField extends Entry{
	//just for deserialization
	public StaticField(){}
	
	public StaticField(String className, String fieldName, Value value) {
		this.value = value;
		this.className = className;
		this.fieldName = fieldName;
	}
	@Attribute
	public String className;
	@Attribute
	public String fieldName;
}
