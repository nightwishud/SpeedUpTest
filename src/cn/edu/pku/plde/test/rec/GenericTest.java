package cn.edu.pku.plde.test.rec;

import cn.edu.pku.plde.smp.SmpRcd;

public class GenericTest<K> {
	public K s;
	public String testGeneric(K x){
		SmpRcd r = SmpRcd.getInstance("testGeneric");
		r.putGenericPara(0, x);
		r.write("D:/test/gen.xml");
		return x.toString();
	}
	public static void main(String[] args){
		GenericTest<String[][]> g = new GenericTest<String[][]>();
		String[][] arr = {{"aaa", "bbb"}, {"ccc", "ddd"}};
		g.testGeneric(arr);
	}
}
