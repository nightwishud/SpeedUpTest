package cn.edu.pku.plde.smp;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class VObj extends V {
	public VObj(){
		flds =  new ArrayList<DFld>();
	}
	public VObj(String s, Object obj){
		tp = s;
		flds =  new ArrayList<DFld>();
		SmpRcd.analzCompondObj(obj, this);
	}
	@Attribute
	public String tp = "";
//	@Attribute
//	public boolean nil;
	@ElementList
	public List<DFld> flds;
}
