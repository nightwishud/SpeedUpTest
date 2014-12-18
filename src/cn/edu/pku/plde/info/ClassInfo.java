package cn.edu.pku.plde.info;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
	private String name;
	private List<FieldInfo> fields;
	private boolean useType;
	
	//constructor
	public ClassInfo(String name){
		this.name = name;
		this.fields = new ArrayList<FieldInfo>();
		this.useType = false;
	}
	
	public String getName() {
		return name;
	}
	
	public List<FieldInfo> getFields() {
		return fields;
	}
	public void setFields(List<FieldInfo> fields) {
		this.fields = fields;
	}
	public boolean isUseType() {
		return useType;
	}
	public void setUseType(boolean useType) {
		this.useType = useType;
	}
	
	public boolean isInnerClass(){
		return name.contains("$");
	}
}
