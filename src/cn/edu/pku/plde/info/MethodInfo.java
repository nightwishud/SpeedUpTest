package cn.edu.pku.plde.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodInfo {
	private String methodName;	//methodName = className + " " + methodName + " " + desc
	private int paraNum;
	private boolean isStatic;
	private List<ParameterInfo> parameters;
	private List<StaticInfo> statics;
	private Map<String, ClassInfo> relationsMap;//
	
	//constructor
	public MethodInfo(String methodName) {
		this.methodName = methodName;		
		this.parameters = new ArrayList<ParameterInfo>();
		this.statics = new ArrayList<StaticInfo>();
		this.relationsMap = new HashMap<String, ClassInfo>(); 
	}

	public int getParaNum() {
		return paraNum;
	}
	public void setParaNum(int paraNum) {
		this.paraNum = paraNum;
	}
	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public Map<String, ClassInfo> getRelationsMap() {
		return relationsMap;
	}

	public void setRelationsMap(Map<String, ClassInfo> relationsMap) {
		this.relationsMap = relationsMap;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<ParameterInfo> getParameters() {
		return parameters;
	}
	public void setParameters(List<ParameterInfo> parameters) {
		this.parameters = parameters;
	}
	public List<StaticInfo> getStatics() {
		return statics;
	}
	public void setStatics(List<StaticInfo> statics) {
		this.statics = statics;
	}

}
