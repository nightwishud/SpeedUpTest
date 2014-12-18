package cn.edu.pku.plde.info;

public class FieldInfo {
	private String owner;
	private String name;
	private String type;
	private String desc;
	private boolean tag;	// 0 means compound, 1 means primitive
	
	public FieldInfo(){}
	
	public FieldInfo(String owner, String name){
		this.owner = owner;
		this.name = name;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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
