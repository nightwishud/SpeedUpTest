package cn.edu.pku.plde.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

public class ObjCMP { // TODO:: check recursive type reference
	public static boolean isObjEqual(Object o1, Object o2) throws Exception {
		if(o1==o2){	
			return true;
		}
		if((o1==null && o2!=null) || (o1!=null && o2==null)){
			return false;
		}
		Class c1 = o1.getClass();
		Class c2 = o2.getClass();
//		System.out.println("O1 TYPE: " + c1.getName() + " 	O2 TYPE: " + c2.getName());		
		if (!c1.getName().equals(c2.getName())) {
//			System.out.println("OJBCMP : Obj1 and Obj2 are not same type !");
			return false; 
		}

		if (canDirectlyCmp(c1) && canDirectlyCmp(c2)) {
			return o1.equals(o2);
		}
		if(! foreachField(c1, o1, o2)){
			return false;
		}
		//
		Class c0 = c1.getSuperclass();
		while( c0 != null){
			if( ! foreachField(c0, o1, o2)){// TODO:: remove private fields in super
				return false;
			}
			c0 = c0.getSuperclass();
		}
		return true;
	}

	public static boolean canDirectlyCmp(Class c) {
		if (c.getName().equals("java.lang.String")
				|| c.getName().equals("java.lang.Integer")
				|| c.getName().equals("java.lang.Float")
				|| c.getName().equals("java.lang.Double")
				|| c.getName().equals("java.lang.Boolean")
				|| c.getName().equals("java.lang.Byte")
				|| c.getName().equals("java.lang.Character")
				|| c.getName().equals("java.lang.Long")
				|| c.getName().equals("java.lang.Short")
				|| c.getName().equals("java.lang.Class")) { 
			return true;
		}
		return false;
	}

	private static Object getFieldValue(Object obj, Field f) {
		try {
			Object o = null;
			if(!f.isAccessible()){	
				f.setAccessible(true);
				o = f.get(obj);
				f.setAccessible(false);
			}else{
				o = f.get(obj);
			}
			return o;
		} catch (Exception e) {
			System.err.println("ERR IN getFieldValueByName()");
			return null;
		}
	}
	private static boolean mapIsEqual(Object map1, Object map2) throws Exception{
		Method sizeMethod = map1.getClass().getDeclaredMethod("size");
		Integer s1 = (Integer) sizeMethod.invoke(map1);
		Integer s2 = (Integer) sizeMethod.invoke(map2);
		if(!s1.equals(s2)){
			return false;
		}
		return false;
	}
	private static boolean collectionIsEqual(Object list1, Object list2) throws Exception{
		Method toArrayMethod = list1.getClass().getDeclaredMethod("toArray");
		Object[] oa1 = (Object[]) toArrayMethod.invoke(list1);
		Object[] oa2 = (Object[]) toArrayMethod.invoke(list2);
		int len1 = Array.getLength(oa1);
		int len2 = Array.getLength(oa2);
		System.out.println("Array len 1: "+ len1);
		System.out.println("Array len 2: "+ len2);
		if(len1 != len2){
			return false;
		}
		for(int j=0;j<len1;j++){
			Object element1 = Array.get(oa1, j);
			Object element2 = Array.get(oa2, j);
			if(!isObjEqual(element1, element2)){
				return false;
			}
		}
		return true;
	}
	private static boolean arrayIsEqual(Object array1, Object array2) throws Exception{
		int len1 = Array.getLength(array1);
		int len2 = Array.getLength(array2);
		if(len1 != len2){
			return false;
		}
		for(int j=0;j<len1;j++){
			Object element1 = Array.get(array1, j);
			Object element2 = Array.get(array2, j);
			if(!isObjEqual(element1, element2)){
				return false;
			}
		}
		return true;
	}
	
	private static boolean foreachField(Class c, Object o1, Object o2) throws Exception{
		Field[] fields1 = c.getDeclaredFields();
		boolean result = true;
		for (int i = 0; i < fields1.length; i++) {
//			System.out.println("FIELD TYPE : " + fields1[i].getType().getName() + "  FIELD NAME : " + fields1[i].getName());		
			if(Modifier.isStatic(fields1[i].getModifiers())){
				continue;
			}
			Object fo1 = getFieldValue(o1, fields1[i]);
			Object fo2 = getFieldValue(o2, fields1[i]);
			if((fo1==null)&&(fo2==null)){
				continue;
			}
			if((fo1!=null && fo2==null) || (fo1==null && fo2!=null)){
//				System.out.println("OJBCMP : One is null, the other is not !");
				return false;
			}
			if(fo1.getClass().isArray()){
//				System.out.println("FIELD TYPE : " + fields1[i].getType().getName() + "  FIELD NAME : " + fields1[i].getName());		
				if(arrayIsEqual(fo1, fo2)){
					continue;
				}else{
					return false;
				}
			}
			result = result && isObjEqual(fo1, fo2);
			if(result == false){
//				System.out.println("OJBCMP : " + "\"" + c.getName() + "\"  \""+ fields1[i].getType().getName() +" -> " + fields1[i].getName() + "\" is not equal");
				return false;
			}
		}
		return result;
	}
	
}
