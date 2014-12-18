package cn.edu.pku.plde.test.rec;

import cn.edu.pku.plde.smp.SmpRcd;

public class SmpStaticTest {
	public static void main(String[] args){
		SmpStaticTest t= new SmpStaticTest();
		t.begin();
//		System.out.println(t.test());
	}
	public void begin(){
		this.test();
	}
	public int test(){
		SmpRcd r = SmpRcd.getInstance("test");
		r.putOuterStatic("cn.edu.pku.plde.test.rec.StaticTestA", "a", "int", 0);
//		r.putOuterStatic("cn/edu/pku/plde/test/rec/StaticTestA", "a", "int", 0);//wrong
		r.write("D://test");
		StaticTestA sta = new StaticTestA();
		return sta.get();
	}
}

class StaticTestA{
	private static int a = 5;
	int get(){
		return a + 5;
	}
}