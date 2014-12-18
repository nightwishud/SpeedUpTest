package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import cn.edu.pku.plde.rec.val.Value;

public class DFld {
	public DFld(){}
	public DFld(String n, String t, V v) {
		this.nm = n;
		this.tp = t;
		this.v = v;
	}
	@Attribute
	public String nm;	//name
	@Attribute
	public String tp;	//type
	@Element
	public V v;
}
