package cn.edu.pku.plde.smp;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import cn.edu.pku.plde.utils.ObjCMP;


/**
 * SimpleRecord, the classes in package cn.edu.pku.plde.rec.smp have the same function with the out ones.
 * This package intended to cut the output in xml file.
 * */
public class SmpRcd {
	public static Map<String,SmpRcd> records = new HashMap<String, SmpRcd>();
//	private static Set<Object> analyzedObjs = new HashSet<Object>();
	private static Set<Integer> analyzedObjsKey = new HashSet<Integer>();
	@Attribute
	public String k; //key
	@Attribute
	public int rt; //runtimes
	@ElementList
	public List<Etr> etrs;//entries
	
	public SmpRcd(){}
	
	public SmpRcd(String key){
		etrs = new ArrayList<Etr>();
		this.k = key;
	}
	
	public static SmpRcd getInstance(String key){
		SmpRcd r = records.get(key);
		if(r == null){
			r = new SmpRcd(key);
			records.put(key, r);
		}
		r.rt++;	//runtimes++
		return r;
	}
	
	public void clear(){
//		analyzedObjs.clear();
		analyzedObjsKey.clear();
		etrs.clear();
		ti = 0;
	}
	public static int ti; // TODO:: just for test
	public static void analzCompondObj(Object obj, VObj vo){
//		if(analyzedObjs.contains(obj)){
//			return;
//		}else{
//			analyzedObjs.add(obj);
//		}
		int hashCode = obj.hashCode();
		if(analyzedObjsKey.contains(hashCode)){
			return;
		}else{
			analyzedObjsKey.add(hashCode);
		}
//		System.out.println((ti++) + " --------------------- " + obj.getClass().getName() );
	
		Class<?> cls = obj.getClass();
		vo.tp = cls.getName();
		HashSet<Field> fields = new HashSet<Field>();
		for(Field f: cls.getDeclaredFields()){
			fields.add(f);
		}
		Class<?> superCls = obj.getClass().getSuperclass();
		while(superCls != null){
			for(Field f : superCls.getDeclaredFields()){
				int mod = f.getModifiers();
				if(!Modifier.isPrivate(mod)){
					fields.add(f);
				}
			}
			superCls = superCls.getSuperclass();
		}
//		for(Field f: cls.getFields()){
//			fields.add(f);
//		}
		for(Field f : fields){
			Object data = null;
/*			
			if(f.getClass().isEnum()){// TODO:: for ENUM
				VEI ve = new VEI();
				if(f.isAccessible()){
					try {
						data = f.get(obj);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}else{
					f.setAccessible(true);
					try {
						data = f.get(obj);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					f.setAccessible(false);
				}
				ve.tp = f.getClass().getName();
				ve.fld = data.toString();
				vo.flds.add(new DFld(ve.tp, ve.fld, ve));
				continue;
			}
	*/		
			try {
				if(f.isAccessible()){
					data = f.get(obj);
				}else{
					f.setAccessible(true);
					data = f.get(obj);
					f.setAccessible(false);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			String fName = f.getName();
			String fType = f.getType().getName();
			
//			System.out.println(" ~~~~~ " + fName + " " + fType);
			
			if(data == null){
				vo.flds.add(new DFld(fName, fType, VNull.getInstance()));
				continue;
			}
			if(fType.equals("int") || fType.equals("java.lang.Integer")){// sort as frequency
				int temp = (Integer)data;
				vo.flds.add(new DFld(fName, fType, new VInt(temp)));
			}else if(fType.equals("java.lang.String")){	
				String temp = (String)data;
				vo.flds.add(new DFld(fName, fType, new VString(temp)));
			}else if(fType.equals("char") || fType.equals("java.lang.Character")){
				char temp = (Character)data;
				vo.flds.add(new DFld(fName, fType, new VChar(temp)));
			}else if(fType.equals("double") || fType.equals("java.lang.Double")){
				double temp = (Double)data;
				vo.flds.add(new DFld(fName, fType, new VDouble(temp)));
			}else if(fType.equals("boolean") || fType.equals("java.lang.Boolean")){
				boolean temp = (Boolean)data;
				vo.flds.add(new DFld(fName, fType, new VBool(temp)));
			}else if(fType.equals("long") || fType.equals("java.lang.Long")){
				long temp = (Long)data;
				vo.flds.add(new DFld(fName, fType, new VLong(temp)));
			}else if(fType.equals("float") || fType.equals("java.lang.Float")){
				float temp = (Float)data;
				vo.flds.add(new DFld(fName, fType, new VFloat(temp)));
			}else if(fType.equals("short") || fType.equals("java.lang.Short")){
				short temp = (Short)data;
				vo.flds.add(new DFld(fName, fType, new VShort(temp)));
			}else if(fType.equals("byte") || fType.equals("java.lang.Byte")){
				byte temp = (Byte)data;
				vo.flds.add(new DFld(fName, fType, new VByte(temp)));
			}else if(f.getType().isArray()){// TODO:: add Integer[], Double[] ...
				if(fType.equals("[I")){//int[]
					vo.flds.add(new DFld(fName, fType, new VIntArr((int[])data)));
				}else if(fType.equals("[D")){//double[]
					vo.flds.add(new DFld(fName, fType, new VDoubleArr((double[])data)));
				}else if(fType.equals("[Ljava.lang.String;")){//String[]
					vo.flds.add(new DFld(fName, fType, new VStringArr((String[])data)));
				}else if(fType.equals("[C")){//char[]
					vo.flds.add(new DFld(fName, fType, new VCharArr((char[])data)));
				}else if(fType.equals("[J")){//long[]
					vo.flds.add(new DFld(fName, fType, new VLongArr((long[])data)));
				}else if(fType.equals("[Z")){//boolean[]
					vo.flds.add(new DFld(fName, fType, new VBoolArr((boolean[])data)));
				}else if(fType.equals("[F")){//float[]
					vo.flds.add(new DFld(fName, fType, new VFloatArr((float[])data)));
				}else if(fType.equals("[B")){//byte[]
					vo.flds.add(new DFld(fName, fType, new VByteArr((byte[])data)));
				}else if(fType.equals("[S")){//short[]
					vo.flds.add(new DFld(fName, fType, new VShortArr((short[])data)));
				}else{
					VObjArr arrv = new VObjArr();
					analzCompoundArray(data, arrv);
					vo.flds.add(new DFld(fName, fType, arrv));
				}
			}else{
				VObj subObj = new VObj();
				analzCompondObj(data, subObj);
				vo.flds.add(new DFld(fName, fType, subObj));
			}
		}
		
	}
	
	public static void analzCompoundArray(Object obj, VObjArr arrv){
//		if(analyzedObjs.contains(obj)){
//			return;
//		}else{
//			analyzedObjs.add(obj);
//		}
		int hashCode = obj.hashCode();
		if(analyzedObjsKey.contains(hashCode)){
			return;
		}else{
			analyzedObjsKey.add(hashCode);
		}		
		String type = obj.getClass().getName();
		arrv.tp = type;
		int len = Array.getLength(obj);
		arrv.len = len;
		for(int i=0; i<len; i++){
			Object data = Array.get(obj, i);
//			if(analyzedObjs.contains(data)){
//				continue;
//			}else{
//				analyzedObjs.add(data);
//			}
			if(data == null){
				arrv.items.add(new DArrItem(i, VNull.getInstance()));
				continue;
			}
			String itemType = data.getClass().getName();
			if(itemType.equals("int") || itemType.equals("java.lang.Integer")){
				int temp = (Integer)data;
				arrv.items.add(new DArrItem(i, new VInt(temp)));
			}else if(itemType.equals("java.lang.String")){	
				String temp = (String)data;
				arrv.items.add(new DArrItem(i, new VString(temp)));
			}else if(itemType.equals("char") || itemType.equals("java.lang.Character")){
				char temp = (Character)data;
				arrv.items.add(new DArrItem(i, new VChar(temp)));
			}else if(itemType.equals("byte") || itemType.equals("java.lang.Byte")){
				byte temp = (Byte)data;
				arrv.items.add(new DArrItem(i, new VByte(temp)));
			}else if(itemType.equals("double") || itemType.equals("java.lang.Double")){
				double temp = (Double)data;
				arrv.items.add(new DArrItem(i, new VDouble(temp)));
			}else if(itemType.equals("float") || itemType.equals("java.lang.Float")){
				float temp = (Float)data;
				arrv.items.add(new DArrItem(i, new VFloat(temp)));
			}else if(itemType.equals("long") || itemType.equals("java.lang.Long")){
				long temp = (Long)data;
				arrv.items.add(new DArrItem(i, new VLong(temp)));
			}else if(itemType.equals("short") || itemType.equals("java.lang.Short")){
				short temp = (Short)data;
				arrv.items.add(new DArrItem(i, new VShort(temp)));
			}else if(itemType.equals("boolean") || itemType.equals("java.lang.Boolean")){
				boolean temp = (Boolean)data;
				arrv.items.add(new DArrItem(i, new VBool(temp)));
			}else if(data.getClass().isArray()){// TODO:: Integer[], Float[], Double[]...
				if(itemType.equals("[I")){//int[]
					arrv.items.add(new DArrItem(i, new VIntArr((int[])data)));
				}else if(itemType.equals("[D")){//double[]
					arrv.items.add(new DArrItem(i, new VDoubleArr((double[])data)));
				}else if(itemType.equals("[Ljava.lang.String;")){//String[]
					arrv.items.add(new DArrItem(i, new VStringArr((String[])data)));
				}else if(itemType.equals("[C")){//char[]
					arrv.items.add(new DArrItem(i, new VCharArr((char[])data)));
				}else if(itemType.equals("[J")){//long[]
					arrv.items.add(new DArrItem(i, new VLongArr((long[])data)));
				}else if(itemType.equals("[Z")){//boolean[]
					arrv.items.add(new DArrItem(i, new VBoolArr((boolean[])data)));
				}else if(itemType.equals("[F")){//float[]
					arrv.items.add(new DArrItem(i, new VFloatArr((float[])data)));
				}else if(itemType.equals("[B")){//byte[]
					arrv.items.add(new DArrItem(i, new VByteArr((byte[])data)));
				}else if(itemType.equals("[S")){//short[]
					arrv.items.add(new DArrItem(i, new VShortArr((short[])data)));
				}else{
					VObjArr subArrv = new VObjArr();
					analzCompoundArray(data, subArrv);
					arrv.items.add(new DArrItem(i, subArrv));
				}
			}else{
				VObj subObj = new VObj();
				analzCompondObj(data, subObj);
				arrv.items.add(new DArrItem(i, subObj));
			}
		}
		
		
	}
	
	public void putPrimPara(int id, String type, Object obj) {
		if(type.equals("int") || type.equals("java.lang.Integer")) {
			int temp = (Integer)obj;
			etrs.add(new EPara(id, new VInt(temp)));
		} else if(type.equals("char") || type.equals("java.lang.Character")) {
			char temp = (Character)obj;
			etrs.add(new EPara(id, new VChar(temp)));
		} else if(type.equals("byte") || type.equals("java.lang.Byte")) {
			byte temp = (Byte)obj;
			etrs.add(new EPara(id, new VByte(temp)));
		} else if(type.equals("double") || type.equals("java.lang.Double")) {
			double temp = (Double)obj;
			etrs.add(new EPara(id, new VDouble(temp)));
		} else if(type.equals("float") || type.equals("java.lang.Float")) {
			float temp = (Float)obj;
			etrs.add(new EPara(id, new VFloat(temp)));
		} else if(type.equals("long") || type.equals("java.lang.Long")) {
			long temp = (Long)obj;
			etrs.add(new EPara(id, new VLong(temp)));
		} else if(type.equals("short") || type.equals("java.lang.Short")) {
			short temp = (Short)obj;
			etrs.add(new EPara(id, new VShort(temp)));
		} else if(type.equals("boolean") || type.equals("java.lang.Boolean")) {
			boolean temp = (Boolean)obj;
			etrs.add(new EPara(id, new VBool(temp)));
		} else{
			System.err.println("putPrimPara ERR ! Type : " + type);
		}
	}
	
	public void putSimplePara(int id, String type, Object obj) {
		if(obj == null){
			etrs.add(new EPara(id, VNull.getInstance()));
		}else if(type.equals("java.lang.String")){
			String temp = (String)obj;
			etrs.add(new EPara(id, new VString(temp)));
		}else if(type.equals("[I")){
			int[] temp = (int[])obj;
			etrs.add(new EPara(id, new VIntArr(temp)));
		}else if(type.equals("[D")){
			double[] temp = (double[])obj;
			etrs.add(new EPara(id, new VDoubleArr(temp)));
		}else if(type.equals("[Ljava.lang.String;")){
			String[] temp = (String[])obj;
			etrs.add(new EPara(id, new VStringArr(temp)));
		}else if(type.equals("[C")){
			char[] temp = (char[])obj;
			etrs.add(new EPara(id, new VCharArr(temp)));
		}else if(type.equals("[J")){
			long[] temp = (long[]) obj;
			etrs.add(new EPara(id, new VLongArr(temp)));
		}else if(type.equals("[Z")){
			boolean[] temp = (boolean[]) obj;
			etrs.add(new EPara(id, new VBoolArr(temp)));
		}else if(type.equals("[F")){
			float[] temp = (float[]) obj;
			etrs.add(new EPara(id, new VFloatArr(temp)));
		}else if(type.equals("[B")){
			byte[] temp = (byte[]) obj;
			etrs.add(new EPara(id, new VByteArr(temp)));
		}else if(type.equals("[S")){
			short[] temp = (short[]) obj;
			etrs.add(new EPara(id, new VShortArr(temp)));
		}else{
			System.err.println("putSimplePara ERR ! Type : " + type);
		}
	}
	
	public void putObjPara(int id, Object obj){// TODO:: add gerneric
		if(obj == null){
			etrs.add(new EPara(id, VNull.getInstance()));
		}else{
			if(obj.getClass().isEnum()){
				VEI ve = new VEI();
				ve.tp = obj.getClass().getName();
				ve.fld = obj.toString();
//				analyzedObjs.clear();
				analyzedObjsKey.clear();
				etrs.add(new EPara(id, ve));
			}else{
				VObj vo = new VObj();
				analzCompondObj(obj, vo);
//				analyzedObjs.clear();
				analyzedObjsKey.clear();
				etrs.add(new EPara(id, vo));
			}
		}
	}
	
	public void putArrPara(int id, Object obj){
		if(obj == null){
			etrs.add(new EPara(id, VNull.getInstance()));
		}else{
			VObjArr arrv = new VObjArr();
			analzCompoundArray(obj, arrv);		
//			analyzedObjs.clear();
			analyzedObjsKey.clear();
			etrs.add(new EPara(id, arrv));
		}
	}
	
	public void putGenericPara(int id, Object obj){
		if(obj == null){
			etrs.add(new EPara(id, VNull.getInstance()));
		}else if(obj.getClass().isPrimitive()){
			String type = obj.getClass().getName();
			putPrimPara(id, type, obj);
		}else if(obj.getClass().equals(String.class)){
			putSimplePara(id, "java.lang.String", obj);
		}else if(obj.getClass().isArray()){
			String type = obj.getClass().getName();
			if(type.equals("[I") || type.equals("[D") || type.equals("[Ljava.lang.String;") || type.equals("[C") || 
			   type.equals("[J") || type.equals("[Z") || type.equals("[F") || type.equals("[B") || type.equals("[S")){
				putSimplePara(id, type, obj);
			}else{
				putArrPara(id, obj);
			}
		}else{
			putObjPara(id, obj);
		}
	}
	
	public void putOuterStatic(String clsName, String fldName, String type,int task){
		try {
			Class<?> cls = Class.forName(clsName);
			Field f = null;
			boolean notFound = true;
			while(cls.getSuperclass() != null && notFound){// TODO::optimise
				for(Field f2 : cls.getDeclaredFields()){
					if(f2.getName().equals(fldName)){
						f = f2;
						clsName = cls.getName();
						notFound = false;
						break;
					}
				}
				cls = cls.getSuperclass();
			}
			Object data = null;
			if(f.isAccessible()){
				data = f.get(null);
			}else{
				f.setAccessible(true);
				data = f.get(null);
				f.setAccessible(false);
			}
			switch(task){
				case 0:
					putPrimStatic(clsName, fldName, type, data);
					break;
				case 1:
					putSimpleStatic(clsName, fldName, type, data);
					break;
				case 2:
					putArrStatic(clsName, fldName, data);
					break;	
				case 3:
					putObjStatic(clsName, fldName, data);
					break;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public void putPrimStatic(String clsName, String fieldName, String type, Object obj){
		if(type.equals("int") || type.equals("java.lang.Integer")) {
			int temp = (Integer)obj;
			etrs.add(new EStat(clsName, fieldName, new VInt(temp)));
		} else if(type.equals("char") || type.equals("java.lang.Character")){
			char temp = (Character)obj;
			etrs.add(new EStat(clsName, fieldName, new VChar(temp)));
		} else if(type.equals("byte") || type.equals("java.lang.Byte")) {
			byte temp = (Byte)obj;
			etrs.add(new EStat(clsName, fieldName, new VByte(temp)));
		} else if(type.equals("double") || type.equals("java.lang.Double")) {
			double temp = (Double)obj;
			etrs.add(new EStat(clsName, fieldName, new VDouble(temp)));
		} else if(type.equals("float") || type.equals("java.lang.Float")) {
			float temp = (Float)obj;
			etrs.add(new EStat(clsName, fieldName, new VFloat(temp)));
		} else if(type.equals("long") || type.equals("java.lang.Long")) {
			long temp = (Long)obj;
			etrs.add(new EStat(clsName, fieldName, new VLong(temp)));
		} else if(type.equals("short") || type.equals("java.lang.Short")) {
			short temp = (Short)obj;
			etrs.add(new EStat(clsName, fieldName, new VShort(temp)));
		} else if(type.equals("boolean") || type.equals("java.lang.Boolean")) {
			boolean temp = (Boolean)obj;
			etrs.add(new EStat(clsName, fieldName, new VBool(temp)));
		} else{
			System.err.println("putPrimStatic ERR ! Type : " + type);
		}
	}
	
	public void putSimpleStatic(String clsName, String fieldName, String type, Object obj){
		if(obj == null){
			etrs.add(new EStat(clsName, fieldName, VNull.getInstance()));
		}else if(type.equals("java.lang.String")){
			String temp = (String)obj;
			etrs.add(new EStat(clsName, fieldName, new VString(temp)));
		}else if(type.equals("int[]")){
			int[] temp = (int[])obj;
			etrs.add(new EStat(clsName, fieldName, new VIntArr(temp)));
		}else if(type.equals("double[]")){
			double[] temp = (double[])obj;
			etrs.add(new EStat(clsName, fieldName, new VDoubleArr(temp)));
		}else if(type.equals("java.lang.String[]")){// TODO::check
			String[] temp = (String[])obj;
			etrs.add(new EStat(clsName, fieldName, new VStringArr(temp)));
		}else if(type.equals("char[]")){
			char[] temp = (char[])obj;
			etrs.add(new EStat(clsName, fieldName, new VCharArr(temp)));
		}else if(type.equals("long[]")){
			long[] temp = (long[]) obj;
			etrs.add(new EStat(clsName, fieldName, new VLongArr(temp)));
		}else if(type.equals("boolean[]")){
			boolean[] temp = (boolean[]) obj;
			etrs.add(new EStat(clsName, fieldName, new VBoolArr(temp)));
		}else if(type.equals("float[]")){
			float[] temp = (float[]) obj;
			etrs.add(new EStat(clsName, fieldName, new VFloatArr(temp)));
		}else if(type.equals("byte[]")){
			byte[] temp = (byte[]) obj;
			etrs.add(new EStat(clsName, fieldName, new VByteArr(temp)));
		}else if(type.equals("short[]")){
			short[] temp = (short[]) obj;
			etrs.add(new EStat(clsName, fieldName, new VShortArr(temp)));
		} else{
			System.err.println("putSimpleStatic ERR ! Type : " + type);
		}
	}
	
	public void putObjStatic(String clsName, String fieldName, Object obj){
		if(obj == null){
			etrs.add(new EStat(clsName, fieldName, VNull.getInstance()));
		}else{
			if(obj.getClass().isEnum()){
				VEI ve = new VEI();
				ve.tp = obj.getClass().getName();
				ve.fld = obj.toString();
//				analyzedObjs.clear();
				analyzedObjsKey.clear();
				etrs.add(new EStat(clsName, fieldName, ve));
			}else{
				VObj vo = new VObj();
				analzCompondObj(obj, vo);
	//			analyzedObjs.clear();
				analyzedObjsKey.clear();
				etrs.add(new EStat(clsName, fieldName, vo));
			}
		}
	}
	
	public void putArrStatic(String clsName, String fieldName, Object obj){
		if(obj == null){
			etrs.add(new EStat(clsName, fieldName, VNull.getInstance()));
		}else{
			VObjArr arrv = new VObjArr();
			analzCompoundArray(obj, arrv);		
//			analyzedObjs.clear();
			analyzedObjsKey.clear();
			etrs.add(new EStat(clsName, fieldName, arrv));
		}
	}
	
	
	
	public void putTotalThis(Object ths){
//		if(ths.getClass().isEnum()){ // TODO :: add enum handler?
		EThis ethis = new EThis();
		VObj vo = new VObj();
		analzCompondObj(ths, vo); 
//		analyzedObjs.clear();
		analyzedObjsKey.clear();
		ethis.v = vo;
		etrs.add(ethis);
	}
	
	public void putPrimFld(String name, String type, Object obj, VPObj v){
		if(type.equals("int")) {
			int temp = (Integer)obj;
			v.flds.add(new DFld(name, type, new VInt(temp)));
		} else if(type.equals("char")) {
			char temp = (Character)obj;
			v.flds.add(new DFld(name, type, new VChar(temp)));
		} else if(type.equals("byte")) {
			byte temp = (Byte)obj;
			v.flds.add(new DFld(name, type, new VByte(temp)));
		} else if(type.equals("double")) {
			double temp = (Double)obj;
			v.flds.add(new DFld(name, type, new VDouble(temp)));
		} else if(type.equals("float")) {
			float temp = (Float)obj;
			v.flds.add(new DFld(name, type, new VFloat(temp)));
		} else if(type.equals("long")) {
			long temp = (Long)obj;
			v.flds.add(new DFld(name, type, new VLong(temp)));
		} else if(type.equals("short")) {
			short temp = (Short)obj;
			v.flds.add(new DFld(name, type, new VShort(temp)));
		} else if(type.equals("boolean")) {
			boolean temp = (Boolean)obj;
			v.flds.add(new DFld(name, type, new VBool(temp)));
		} else{
			System.err.println("putPrimFld ERR ! Type : " + type);
		}
	}
	
	public void putSimpleFld(String name, String type, Object obj, VPObj v){
		if(obj == null){
			v.flds.add(new DFld(name, type, VNull.getInstance()));
		}else if(type.equals("java.lang.String")){
			String temp = (String)obj;
			v.flds.add(new DFld(name, type, new VString(temp)));
		}else if(type.equals("[I")){//TODO:: int[] or [I
			int[] temp = (int[])obj;
			v.flds.add(new DFld(name, type, new VIntArr(temp)));
		}else if(type.equals("[D")){
			double[] temp = (double[])obj;
			v.flds.add(new DFld(name, type, new VDoubleArr(temp)));
		}else if(type.equals("[Ljava.lang.String;")){
			String[] temp = (String[])obj;
			v.flds.add(new DFld(name, type, new VStringArr(temp)));
		}else if(type.equals("[C")){
			char[] temp = (char[])obj;
			v.flds.add(new DFld(name, type, new VCharArr(temp)));
		}else if(type.equals("[J")){
			long[] temp = (long[]) obj;
			v.flds.add(new DFld(name, type, new VLongArr(temp)));
		}else if(type.equals("[Z")){
			boolean[] temp = (boolean[]) obj;
			v.flds.add(new DFld(name, type, new VBoolArr(temp)));
		}else if(type.equals("[F")){
			float[] temp = (float[]) obj;
			v.flds.add(new DFld(name, type, new VFloatArr(temp)));
		}else if(type.equals("[B")){
			byte[] temp = (byte[]) obj;
			v.flds.add(new DFld(name, type, new VByteArr(temp)));
		}else if(type.equals("[S")){
			short[] temp = (short[]) obj;
			v.flds.add(new DFld(name, type, new VShortArr(temp)));
		} else{
			System.err.println("putSimpleFld ERR ! Type : " + type);
		}
	}
	
	public void putObjFld(String name, String type, Object obj, VPObj v){
		if(obj == null){
			v.flds.add((new DFld(name, type, VNull.getInstance())));
		}else{
//			System.out.println( "putObjFLd --> Name : " + name + " TYPE : " + obj.getClass().getName());
			VObj fvo = new VObj();
			analzCompondObj(obj, fvo);
//			analyzedObjs.clear();
			analyzedObjsKey.clear();
			v.flds.add(new DFld(name, type, fvo));
			
		}
	}
	
	public void putArrFld(String name, String type, Object obj, VPObj v){
		if(obj == null){
			v.flds.add((new DFld(name, type, VNull.getInstance())));
		}else{
			VObjArr fvo = new VObjArr();
			analzCompoundArray(obj, fvo);
//			analyzedObjs.clear();
			analyzedObjsKey.clear();
			v.flds.add(new DFld(name, type, fvo));
		}
	}
	
	public void putGenericFld(String name, String type, Object obj, VPObj v){
		if(obj == null){
			v.flds.add((new DFld(name, type, VNull.getInstance())));
		}else if(obj.getClass().isPrimitive()){
			putPrimFld(name, type, obj, v);
		}else if(obj.getClass().equals(String.class)){
			putSimpleFld(name, "java.lang.String", obj, v);
		}else if(obj.getClass().isArray()){
			String truetype = obj.getClass().getName();
			if(truetype.equals("[I") || truetype.equals("[D") || truetype.equals("[Ljava.lang.String;") || truetype.equals("[C") || 
					truetype.equals("[J") || truetype.equals("[Z") || truetype.equals("[F") || truetype.equals("[B") || truetype.equals("[S")){
				putSimpleFld(name, truetype, obj, v);
			}else{
				putArrFld(name, truetype, obj, v);
			}
		}else{
			String truetype = obj.getClass().getName();
			putObjFld(name, truetype, obj, v);//TODO:: type or truetype
		}
		
	}
	
	private static void printCallStack(){
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		try {
			FileWriter writer = new FileWriter("D://test//out.txt", true);
			for(int i = 1; i < stackElements.length; i++){
	//			System.out.println("--- " + (i-2) +  " : " + stackElements[i] + " ---"); 
				writer.write((i-1) +  " : " + stackElements[i] + "\n");
			}
			writer.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void write(String path){
//		String[] s = k.split(" ");
//		String path = "D:\\test\\" + s[0] + "_" + s[1] + ".xml";
		try {
			Serializer serializer = new Persister();
			FileWriter writer = new FileWriter(path, true);
			serializer.write(this, writer);
			writer.write("\n");
	        writer.close();
	        this.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object obj) { // cmp records except their runtime
		if(this == obj){
			return true;
		}
		if(obj instanceof SmpRcd){
			SmpRcd r = (SmpRcd) obj;
			if( ! this.k.equals(r.k)){
				return false;
			}
			try {
				return ObjCMP.isObjEqual(this.etrs, r.etrs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
