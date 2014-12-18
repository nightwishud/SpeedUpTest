package cn.edu.pku.plde.smp;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class VObjArr extends VArr {
	public VObjArr(){
		items = new ArrayList<DArrItem>();
	}
	public VObjArr(String s){
		items = new ArrayList<DArrItem>();
		tp = s;
	}
	@Attribute
	public String tp = "";
//	@Attribute
//	public boolean nil;
	@Attribute
	public int len;
	@ElementList
	public List<DArrItem> items;
}
