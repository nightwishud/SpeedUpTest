package cn.edu.pku.plde.test.rec;

import java.lang.reflect.Field;

import org.objectweb.asm.Type;

public class Test {
	int i;
	String s;
	Byte b;
	Test t;
	AAA aaa;
	static int[] intArr = new int[5];;
	double[] doubleArr;
	String[] sArr;
	char[] cArr;
	long[] lArr;
	short[] shortArr;
	byte[] bArr;
	boolean[] boolArr;
	float[] fArr;
	String[][] s2;
	public static void main(String[] args){
//		Field[] flds = Test.class.getDeclaredFields();
//		for(Field f: flds){
//			System.out.println(f.getType().getName());
//		}
//		System.out.println(Type.getDescriptor(int.class));
//		System.out.println(Type.getType("java.lang.String"));
//		t(intArr);
		test();
	}
	static void t(Object obj){
		System.out.println(obj.getClass().getName());
	}
	
	public static String test(){
		return SmpEnumTest.ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR.toString();
	}
	
	class AAA{
		
	}
}
