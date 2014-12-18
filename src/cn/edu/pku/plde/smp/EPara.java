package cn.edu.pku.plde.smp;

import org.simpleframework.xml.Attribute;
/**
 * parameter entry
 * */
public class EPara extends Etr {
	public EPara(){}
	public EPara(int i, V val){
		id = i;
		v = val;
	}
	@Attribute
	public int id;
}
