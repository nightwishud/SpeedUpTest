package cn.edu.pku.plde.test.rec;

import java.io.File;
import java.io.FileOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import cn.edu.pku.plde.asm.ClsMethodInfoRecordVisitor;
import cn.edu.pku.plde.rec.Record;
import cn.edu.pku.plde.rec.etr.Parameter;
import cn.edu.pku.plde.rec.etr.StaticField;
import cn.edu.pku.plde.rec.val.ArrayValue;
import cn.edu.pku.plde.rec.val.IntValue;
import cn.edu.pku.plde.rec.val.NullValue;
import cn.edu.pku.plde.rec.val.ObjectValue;
import cn.edu.pku.plde.utils.ClassAnalyser;


public class RecTest {
/*	public static void main(String[] args){
		RecTest rt = new RecTest();
		B b = new B();
		B b1 = new B();
		B[] bs = {b, b1};
		A a = new A(2, bs);
		A a1 = new A(3, null);
		A[] arr = {a, a1, null};
////		rt.f(a1);
//		rt.f(arr);
		ClassAnalyser.analysisClassFromDir("D:/Code/SVN/SpeedUpTest/bin/cn/edu/pku/plde/rec/RecTest.class");
	 	Type t = Type.getType("[Lcn.edu.pku.plde.rec.A;");
	 	System.out.println(arr.getClass().getName() + "  ~~~~~~~~~~~~~~~~~~~~~~~~");
	 	System.out.println(t.getSort());
//		rt.testString("abcdefghigk");
//		int[] arr = {1,2,3,4,5,6,7};
//		rt.testPrimArr(arr);
//		List<String> list = new ArrayList();
//		list.add("aaaa");
//		list.add("bbbb");
//		list.add("cccc");
//		rt.testList(list);
	}*/
	
	public static void main(String[] args) throws Exception{
//		ClassAnalyser.analysisClassFromDir("bin/cn/edu/pku/plde/rec/RecTest.class");
//		ClassReader cr = new ClassReader(RecTest.class.getName());
//		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
//		ClsMethodInfoRecordVisitor cmrv = new ClsMethodInfoRecordVisitor(cw);
//		cr.accept(cmrv, 0); // TODO :: EXPAND_FRAMES ?
//		byte[] b2 = cw.toByteArray();
//	    File fileOut = new File("bin/cn/edu/pku/plde/rec/RecTest.class");
//	    if(fileOut.exists()){         
//	    	fileOut.delete();
//	    }
//	    FileOutputStream output = null;
//	    output = new FileOutputStream(fileOut);
//	    output.write(b2,0,b2.length); 
//	    output.close();
//	    int[] array = { 3, 1, 2};
//		Sort.quickSort(array, 0, array.length - 1);
//		for (int i : array) {
//			System.out.print(i + " ");
//		} 
		
		RecTest rt = new RecTest();
		rt.testStatic();
	}
	
	
	
	public void farr(A[] a){
		Record r = Record.getInstance("farr");
		ObjectValue ov;
		if(a == null){
			ov = NullValue.getInstance();
		}
		else{
			ov = new ArrayValue("A[] array TYPE");
			Record.analzArray(a, (ArrayValue) ov);
		}
		r.entries.add(new Parameter(0, ov));
		r.write("D:/test/RefTest.xml");
	}
	
	public void fobj(A a){
		Record r = Record.getInstance("fobj");
		ObjectValue ov;
		if(a == null){
			ov = NullValue.getInstance();
		}else{
			ov = new ObjectValue("A obj TYPE");
			Record.analzCompondPara(a, ov);
		}
		r.entries.add(new Parameter(0, ov));
		r.write("/home/nightwish/workspace/test_program/output/RefTest.xml");
	}
	
	public void fobj2(A a, B b, int i){
		Record r = Record.getInstance("fobj");
		ObjectValue ov;
		
		if(a == null){
			ov = NullValue.getInstance();
		}else{
			ov = new ObjectValue("A obj TYPE");
			Record.analzCompondPara(a, ov);
		}
		r.entries.add(new Parameter(0, ov));
		
		if(b == null){
			ov = NullValue.getInstance();
		}else{
			ov = new ObjectValue("B obj TYPE");
			Record.analzCompondPara(b, ov);
		}
		r.entries.add(new Parameter(1, ov));
		
		r.putPrimPara(2, "int", i);
		
		r.write("D:/test/RefTest.xml");
	}
	public void faa(A[][] a){
		Record r = Record.getInstance("faa");
		ObjectValue ov;
		if(a == null){
			ov = new NullValue();
		}
		else{
			ov = new ArrayValue("A[] array TYPE");
			Record.analzArray(a, (ArrayValue) ov);
		}
		r.entries.add(new Parameter(0, ov));
		r.write("D:/test/RefTest.xml");
	}
	public void fpa(int[] a){
		Record r = Record.getInstance("a");
		ObjectValue ov;
		if(a == null){
			ov = new NullValue();
		}
		else{
			ov = new ArrayValue("A[] array TYPE");
			Record.analzArray(a, (ArrayValue) ov);
		}
		r.entries.add(new Parameter(0, ov));
		r.write("D:/test/RefTest.xml");
	}
	public void testString(String s){
		Record r = Record.getInstance("testString");
		ObjectValue ov;
		ov = new ObjectValue(s.getClass().getName());
		Record.analzCompondPara(s, ov);
		r.entries.add(new Parameter(0, ov));
		r.write("D:/test/RefTest.xml");
	}
	public void testPrimArr(int[] arr){
		Record r = Record.getInstance("testPrimArr");
		ObjectValue ov = new ArrayValue(arr.getClass().getName());
		Record.analzArray(arr, (ArrayValue) ov);
		r.entries.add(new Parameter(0, ov));
		r.write("D:/test/RefTest.xml");
	}
	
	public void testStatic(){
//		Record r = Record.getInstance("cn.edu.pku.plde.rec.RecTest testStatic DESC");
//		r.entries.add(new StaticField("className", "fieldName", new IntValue(A.i)));
//		r.putPrimStatic("A", "i", "int", A.i);
//		r.putPrimStatic("B", "p", "int", B.p);
		
		int i = A.i;
		int j = A.b.p;
	}
	/*public void testStatic2(){
		Record r = Record.getInstance("cn.edu.pku.plde.rec.RecTest testStatic DESC");
		ObjectValue ov;
		if(B.s == null){
			ov = NullValue.getInstance();
		}else{
			ov = new ObjectValue("java.lang.String");
			Record.analzCompondPara(B.s, ov);
		}
		r.entries.add(new StaticField("cn.edu.pku.plde.rec.B", "s",  ov));
		String s1 = B.s;
	}
	public void testStatic3(){
		Record r = Record.getInstance("cn.edu.pku.plde.rec.RecTest testStatic3 DESC");
		ObjectValue ov;
		if(A.arr == null){
			ov = NullValue.getInstance();
		}else{
			ov = new ArrayValue("A[] array TYPE");
			Record.analzArray(A.arr, (ArrayValue) ov);
		}
		r.entries.add(new StaticField("cn.edu.pku.plde.rec.A", "arr",  ov));
	}*/
}

class A{
	static int i;
	static B b;
	static String[] arr;
}
class B{
	static int p;
	static String s;
}