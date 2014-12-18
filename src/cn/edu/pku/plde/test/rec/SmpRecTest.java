package cn.edu.pku.plde.test.rec;

import cn.edu.pku.plde.smp.DFld;
import cn.edu.pku.plde.smp.EThis;
import cn.edu.pku.plde.smp.SmpRcd;
import cn.edu.pku.plde.smp.VInt;
import cn.edu.pku.plde.smp.VNil;
import cn.edu.pku.plde.smp.VObj;
import cn.edu.pku.plde.smp.VString;

public class SmpRecTest extends SmpTestFather{
	static String s1;
	static int i1;
	static Object[] o;
	public int f1;
	String f2;
	public double f3;
	
//	public void fPrimArr(int[] a){
//		SmpRcd r = SmpRcd.getInstance("fPrimArr");
//		if(a != null){
//			r.etrs.add(new EPara(0, new VIntArr(a)));
//		}else{
//			r.etrs.add(new EPara(0, VNull.getInstance()));
//		}
//	}
//	public void fStr(String a){
//		SmpRcd r = SmpRcd.getInstance("fStr");
//		EPara ep;
//		if(a != null){
//			ep = new EPara(0, new VString(a));
//		}else{
//			ep = new EPara(0, VNull.getInstance());
//		}
//		r.etrs.add(ep);
//		r.write();
//	}
//	public void fObj(A o){
//		SmpRcd r = SmpRcd.getInstance("fObj");
//		EPara ep;
//		if(o != null){
//			ep = new EPara(0, new VObj("A", o)); // TODO
//		}else{
//			ep = new EPara(0, VNull.getInstance());
//		}
//		r.etrs.add(ep);
//		r.write();
//	}
//	public void fIntArr(int[] arr, int i, String[] s){
//		SmpRcd r = SmpRcd.getInstance("fIntArr");
//		r.putSimplePara(0, "[I", arr);
//		r.putPrimPara(1, "int", i);
//		r.putSimplePara(2, "ssss", s);
//		r.write();
//	}
	
	public void fThis(){
		SmpRcd r = SmpRcd.getInstance("fThis");
		r.putTotalThis(this);
//		vo.flds.add(new DFld("f1", "int", new VInt(f1)));
//		vo.flds.add(new DFld("f2", "java.lang.String", new VString(f2)));
//		vo.flds.add(new DFld("f3", "double", VNil.getInstance()));
//		vo.flds.add(new DFld("f4", "int", new VInt(f4)));

		r.write("D/test/fThis.xml");
		
	}
//	public void fStringAA(String[][] a){
//		SmpRcd r = SmpRcd.getInstance("fStringAA");
//		r.putArrPara(0, a);
//	}
	
//	public void fStat(){
//		SmpRcd r = SmpRcd.getInstance("fIntArr");
//		r.putPrimStatic("SmpTest", "i1", "int", i1);
//		r.putSimpleStatic("SmpRecTest", "s1", "java.lang.String", s1);
//		r.putArrStatic("SMPTEST", "o", o);
//	}
	/*
	public static void main(String[] args){
//		SmpTestFather rt = new SmpRecTest();
		SmpRecTest rt = new SmpRecTest();
//		rt.f1 = 123;
//		rt.f2 = "abcd";
		System.out.println(rt.f1);
		System.out.println(rt.f4);
//		rt.fThis();
		Set<Field> fields = new HashSet<Field>();
		for(Field f: rt.getClass().getDeclaredFields()){
			fields.add(f);
			System.out.println(f.toString());
		}
		System.out.println();
		for(Field f: rt.getClass().getFields()){
			fields.add(f);
			System.out.println(f);
		}
		System.out.println();
		for(Field f: fields){
			System.out.println(f);
		}
	}*/
	public void fInteger(Integer i){
		SmpRcd r = SmpRcd.getInstance("fInteger");
		r.putPrimPara(0, "java.lang.Integer", i);
	}
}
