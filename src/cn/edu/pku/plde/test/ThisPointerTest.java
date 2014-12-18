package cn.edu.pku.plde.test;

public class ThisPointerTest {
	A a;
	public void f1(){
		a = new SubA();
		int t = ((SubA) a).k;
	}
}
class A{
	B b;
	int j;
}
class B{
	int i;
}
class SubA extends A{
	int k;
}
