package cn.edu.pku.plde.test.rec.inner;

import java.lang.reflect.Field;

public class InnerClassTest {
	int j;
	public int f1(){
		Inner i = new Inner();
		return i.f2();
	}
	
	public static void main(String[] args){
		InnerClassTest it = new InnerClassTest();
		for(Field f:InnerClassTest.class.getDeclaredFields()){
			System.out.println(f.getName());
		}
		it.f1();
		System.out.println();
		for(Field f:Inner.class.getDeclaredFields()){
			System.out.println(f.getName());
		}
	} 
	
	class Inner{
		int i;
		public int f2(){
			return InnerClassTest.this.j + this.i;
		}		
	}
}
