package cn.edu.pku.plde.rec.val;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import cn.edu.pku.plde.rec.data.Data;


public class ObjectValue extends Value {
	@Attribute
	public String type;
	@Attribute
	public boolean isNull;
	@ElementList
	public List<Data> fieldsValue;
	
	public ObjectValue(String tpye){
		this.type = tpye;
		fieldsValue = new ArrayList<Data>();		//TODO::opt
	}
	
	//just for deserialization
	public ObjectValue(){ 		// TODO::check
//		this.type = "null";
//		fieldsValue = new ArrayList<Data>();		
	}
}
