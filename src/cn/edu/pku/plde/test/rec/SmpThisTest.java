package cn.edu.pku.plde.test.rec;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.plde.smp.EThis;
import cn.edu.pku.plde.smp.SmpRcd;
import cn.edu.pku.plde.smp.VPObj;

public class SmpThisTest extends SmpThisTestFather{
	private int i = 2;
	private String s = "123456";
	private List<String> list = new ArrayList<String>();
	private TA[][] ta = new TA[2][2];
	private TA t = new TA();
//	private List<TA> l2 = new ArrayList<TA>();
	public SmpThisTest(){
		list.add("aaaaa");
		list.add("bbbb");
		ta[0][1] = new TA();
		ta[1][1] = new TA();
//		l2.add(new TA());
//		l2.add(new TA());
	}
	public int add(){
//		SmpRcd r = SmpRcd.getInstance("add");
//		r.putTotalThis(this);
//		VPObj v = new VPObj("cn.edu.pku.plde.test.rec.SmpThisTest");
//		
//		r.putPrimFld("j", "int", j, v);
//		r.putSimpleFld("s", "java.lang.String", s, v);
//		r.putObjFld("list", "List", list, v);
//		r.putArrFld("ta", "[[TA", ta, v);
//		
//		r.etrs.add(new EThis(v));
//		
//		r.write("D:/test/this.xml");
		return j + list.size() + ta.length + s.length() + t.f1;
	}
	
	public static void main(String[] args){
		SmpThisTest t = new SmpThisTest();
		System.out.println(t.add());
	}
}
class TA{
	int f1 = 0;
	int f2 = 1;
}
