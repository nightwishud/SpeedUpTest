package cn.edu.pku.plde.rec.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import cn.edu.pku.plde.rec.val.Value;

public class FieldData extends Data {
	//just for deserialization
	public FieldData(){}
	
	public FieldData(String fieldName, String fieldType, Value value) {
		this.name = fieldName;
		this.type = fieldType;
		this.value = value;
	}
	@Attribute
	public String name;
	@Attribute
	public String type;
	@Element
	public Value value;
}
