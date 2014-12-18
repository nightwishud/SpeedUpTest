package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class DArrItem {
	public DArrItem(){}
	public DArrItem(int i, V v){
		this.i = i;
		this.v = v;
	}
	@Attribute
	public int i;
	@Element
	public V v;
}
