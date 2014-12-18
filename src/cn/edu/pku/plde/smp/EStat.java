package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Attribute;
/**
 * static entry
 * */
public class EStat extends Etr{
	public EStat(){}
	public EStat(String cls, String fld, V v){
		this.cls = cls;
		this.fld = fld;
		this.v = v;
	}
	@Attribute
	public String cls;
	@Attribute
	public String fld;
}
