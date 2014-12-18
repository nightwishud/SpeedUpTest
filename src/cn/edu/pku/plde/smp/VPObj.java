package cn.edu.pku.plde.smp;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class VPObj extends V{
	public VPObj(){
		flds = new ArrayList<DFld>();
	}
	public VPObj(String tp){
		this.tp = tp;
		flds = new ArrayList<DFld>();
	}
	@Attribute
	public String tp = "";
	@ElementList
	public List<DFld> flds;
}
