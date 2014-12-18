package cn.edu.pku.plde.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class FieldTest {
	public static void main(String[] args){
		Set<Field> fields = new HashSet<Field>();
		for(Field f: Grandson.class.getDeclaredFields()){
			fields.add(f);
//			System.out.println(f.toString());
		}
		
		Class<?> superCls = Grandson.class.getSuperclass();
		while(superCls != null){
			for(Field f : superCls.getDeclaredFields()){
				int mod = f.getModifiers();
//				System.out.println(f.toString());
//				System.out.println(mod);
//				if(Modifier.isProtected(mod) || Modifier.isPublic(mod)  ){
				if(!Modifier.isPrivate(mod)){
					fields.add(f);
				}
			}
			superCls = superCls.getSuperclass();
		}
//		System.out.println();
//		for(Field f: Child.class.getFields()){
//			fields.add(f);
//			System.out.println(f);
//		}
		System.out.println();
		for(Field f: fields){
			System.out.println(f);
		}
		Grandson g = new Grandson();
		System.out.println(g.f6);
	}
	
}

class Father{
	private int f1;
	public int f2;
	protected int f3;
}
class Child extends Father{
	private int f4;
	public int f5;
	int f6;
//	private int f3;
}

class Grandson extends Child{
	public int get(){
		return f3;
	}
}