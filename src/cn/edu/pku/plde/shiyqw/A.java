package cn.edu.pku.plde.shiyqw;

import cn.edu.pku.plde.info.MethodInfo;
import cn.edu.pku.plde.utils.ClassAnalyser;



//用来测试两个接口的程序
public class A {
	int x;
	char y;
	void f(C c, int u, int v) {
		MethodInfo mi = ClassAnalyser.findMethodInfoByName("2", "1", "1");	
		Record record = new Record("AAA");
		if(c==null){
			
		}else{
			int s = 1234;
		}
		
//		ObjectValue ov = new ObjectValue();
//		ov = new ObjectValue();
//		ov.analCompPara(mi, "cn/edu/pku/plde/shiyqw/C", c);
//		record.putEntry(new Parameter(1, ov));
//		record.putPrimPara(12, "I", u);
//		record.putStatic("v2/C", "x", "I", C.x);
///*		for(ParameterInfo parameter : mi.parameters) {
//			
//			if(parameter.tag) {
//				//primitive
//				record.putPrimPara(parameter.id, parameter.type, u);
//				continue;
//			}
//			ObjectValue ov = new ObjectValue();
//			ov.analCompPara(mi, parameter.type, c);
//			record.putEntry(new Parameter(parameter.id, ov));
//		}*/
//		record.write("cn.edu.pku.plde.shiyqw.A.f.txt");
		// 以上代码是我自己添加的，等插桩完成后以及Method接口完成后可自动插桩
		int j = 123;
		x = this.x + u+c.b.x + j;
	}
//	public void test() {
//		A a = new A();
//		C c = new C();
//		a.f(c, 1, 2);
////		c.b.x += 1;
////		a.f(c, 3, 4);
////		Object obj = true;
////		boolean i = (Boolean)(obj);
////		System.out.println(i);
//	}
}

class B {
	public B() {
		this.x = 1;
		this.y = 1;
	}
	int x = 1;
	int y;
}

class C {
	public C() {
		b = new B();
	}
	B b;
	static int x = 2;
}

