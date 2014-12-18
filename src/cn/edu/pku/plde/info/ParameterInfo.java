package cn.edu.pku.plde.info;

public class ParameterInfo {
	private int id;		//begins from zero
	private int site;	//the site of in the local var table
	private String type;
	private String typeDesc;
	private boolean tag; // 0 means compound, 1 means primitive
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSite() {
		return site;
	}
	public void setSite(int site) {
		this.site = site;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isTag() {
		return tag;
	}
	public void setTag(boolean tag) {
		this.tag = tag;
	}
	public String getTypeDesc() {
		return typeDesc;
	}
	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
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
