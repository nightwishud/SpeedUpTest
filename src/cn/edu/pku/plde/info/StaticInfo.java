package cn.edu.pku.plde.info;

public class StaticInfo {
	private String className;
	private String fieldName;
	private String type;
	private String typeDesc;
	private boolean tag;
	
	// TODO::
	@Override
	public boolean equals(Object obj){
		if(obj instanceof StaticInfo){
			StaticInfo o = (StaticInfo) obj;
			return this.className.equals(o.getClassName()) && this.fieldName.equals(o.getFieldName());
		}
		return false;		
	}
	
	@Override
	public int hashCode() {
		return className.hashCode() + fieldName.hashCode();
	}

	@Override
	public String toString() {
		return super.toString() +"  " + className + "->"+fieldName;
	}



	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeDesc() {
		return typeDesc;
	}
	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}	
	public boolean isTag() {
		return tag;
	}
	public void setTag(boolean tag) {
		this.tag = tag;
	}
	
	public boolean isArray(){
		if(type.endsWith("[]")){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isSimple(){
		if(type.equals("java.lang.String")||type.equals("java.lang.String[]")||type.equals("int[]")
				||type.equals("double[]")||type.equals("boolean[]")||type.equals("byte[]")
				||type.equals("long[]")||type.equals("char[]")||type.equals("short[]")
				||type.equals("float[]")){
			return true;
		}
		return false;
	}
}
