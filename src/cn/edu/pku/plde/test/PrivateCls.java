package cn.edu.pku.plde.test;

import java.lang.reflect.Modifier;

public class PrivateCls {
	public static void main(String[] args){
		int m = PI.class.getModifiers();
		System.out.println(Modifier.isPrivate(m));
		System.out.println(Modifier.isProtected(m));
		System.out.println(Modifier.isNative(m));
		System.out.println(Inner.class.isMemberClass());
//		System.out.println(new class N(){	}));
	}
	
	class Inner{
		
	}
	
	private class PI{
		
	}
}
