package cn.edu.pku.plde.rec;

import java.io.FileWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
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

import cn.edu.pku.plde.rec.data.ArrayItemData;
import cn.edu.pku.plde.rec.data.FieldData;
import cn.edu.pku.plde.rec.etr.Entry;
import cn.edu.pku.plde.rec.etr.Parameter;
import cn.edu.pku.plde.rec.etr.StaticField;
import cn.edu.pku.plde.rec.val.ArrayValue;
import cn.edu.pku.plde.rec.val.BooleanValue;
import cn.edu.pku.plde.rec.val.ByteValue;
import cn.edu.pku.plde.rec.val.CharValue;
import cn.edu.pku.plde.rec.val.DoubleValue;
import cn.edu.pku.plde.rec.val.FloatValue;
import cn.edu.pku.plde.rec.val.IntValue;
import cn.edu.pku.plde.rec.val.LongValue;
import cn.edu.pku.plde.rec.val.NullValue;
import cn.edu.pku.plde.rec.val.ObjectValue;
import cn.edu.pku.plde.rec.val.ShortValue;
import cn.edu.pku.plde.utils.ObjCMP;



public class Record {
	public static Map<String,Record> records = new HashMap<String, Record>();
	
	private static Set<Object> analyzedObjs = new HashSet<Object>();
		
	@Attribute
	public String key;
	@Attribute
	public int runtimes;
	
	@ElementList
	public List<Entry> entries;
	
	public Record(){}
	
	public Record(String key){
		entries = new ArrayList<Entry>();
		this.key = key;
	}
	
	public static Record getInstance(String key){
//		System.out.println("KEY : " + key);
		Record r = records.get(key);
//		System.out.println("SIZE : " + analyzedObjs.size());
		if(r == null){
			r = new Record(key);
			records.put(key, r);
		}
		return r;
	}
	
	public void clear(){
		analyzedObjs.clear();
		entries.clear();
	}
	/**
	 * record a compond type object "obj" into an ObjectValue DS "ov"
	 * @param obj
	 * @param ov
	 */

	public static void analzCompondPara(Object obj, ObjectValue ov){
//		System.out.println("analzCompondPara");
		if(analyzedObjs.contains(obj)){
			return;
		}else{
			analyzedObjs.add(obj);
		}
//		System.out.println(analyzedObjs.size());
		ov.type = obj.getClass().getName();
		Field[] fields = obj.getClass().getDeclaredFields(); //getFields()
		for(Field f : fields){
			Field objField;
			Object data = null;
			try {
				objField = obj.getClass().getDeclaredField(f.getName());
				objField.setAccessible(true);   
				data = objField.get(obj);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}  
			//(1) the field is null
			if(data == null){
				ov.fieldsValue.add(new FieldData(f.getName(), f.getType().getName(), NullValue.getInstance()));
				continue;
			}
			// (2) the field is not null
			String fName = f.getName();
			String fType = f.getType().getName();
			if(fType.equals("int")){
				int temp = (Integer)data;
				ov.fieldsValue.add(new FieldData(fName, fType, new IntValue(temp)));
			}else if(fType.equals("char")){
				char temp = (Character)data;
				ov.fieldsValue.add(new FieldData(fName, fType, new CharValue(temp)));
			}else if(fType.equals("byte")){
				byte temp = (Byte)data;
				ov.fieldsValue.add(new FieldData(fName, fType, new ByteValue(temp)));
			}else if(fType.equals("double")){
				double temp = (Double)data;
				ov.fieldsValue.add(new FieldData(fName, fType, new DoubleValue(temp)));
			}else if(fType.equals("float")){
				float temp = (Float)data;
				ov.fieldsValue.add(new FieldData(fName, fType, new FloatValue(temp)));
			}else if(fType.equals("long")){
				long temp = (Long)data;
				ov.fieldsValue.add(new FieldData(fName, fType, new LongValue(temp)));
			}else if(fType.equals("short")){
				short temp = (Short)data;
				ov.fieldsValue.add(new FieldData(fName, fType, new ShortValue(temp)));
			}else if(fType.equals("boolean")){
				boolean temp = (Boolean)data;
				ov.fieldsValue.add(new FieldData(fName, fType, new BooleanValue(temp)));
			}else if(f.getType().isArray()){// TODO:: check
//				System.err.println("ARRAY");			
				ArrayValue arrVal = new ArrayValue(fType);
				analzArray(data, arrVal);
				ov.fieldsValue.add(new FieldData(fName, fType, arrVal));
			}else{
				ObjectValue subObjVal = new ObjectValue(fType);
				analzCompondPara(data, subObjVal);
				ov.fieldsValue.add(new FieldData(fName, fType, subObjVal));
			}			
		}
	}
	/**
	 * recording for array
	 * @param obj
	 * @param arrVal
	 */
	public static void analzArray(Object obj, ArrayValue arrVal){// TODO::check
//		System.out.println("analzArray");
		if(analyzedObjs.contains(obj)){
			return;
		}else{
			analyzedObjs.add(obj);
		}
		arrVal.type = obj.getClass().getName();
		int len = Array.getLength(obj);
		arrVal.length = len;
//		String arrType = obj.getClass().getName();
		for(int i=0; i<len; i++){
			Object data = Array.get(obj, i);
			if(analyzedObjs.contains(data)){
				continue;
			}else{
				analyzedObjs.add(data);
			}
			if(data == null){
				arrVal.arrayItems.add(new ArrayItemData("null", NullValue.getInstance()));
				continue;
			}
			String itemType = data.getClass().getName();
//			System.out.println("ITEM TYPE : " + itemType);
			if(itemType.equals("int") || itemType.equals("java.lang.Integer")){
				int temp = (Integer)data;
				arrVal.arrayItems.add(new ArrayItemData(itemType, new IntValue(temp)));
			}else if(itemType.equals("char") || itemType.equals("java.lang.Character")){
				char temp = (Character)data;
				arrVal.arrayItems.add(new ArrayItemData(itemType, new CharValue(temp)));
			}else if(itemType.equals("byte") || itemType.equals("java.lang.Byte")){
				byte temp = (Byte)data;
				arrVal.arrayItems.add(new ArrayItemData(itemType, new ByteValue(temp)));
			}else if(itemType.equals("double") || itemType.equals("java.lang.Double")){
				double temp = (Double)data;
				arrVal.arrayItems.add(new ArrayItemData(itemType, new DoubleValue(temp)));
			}else if(itemType.equals("float") || itemType.equals("java.lang.Float")){
				float temp = (Float)data;
				arrVal.arrayItems.add(new ArrayItemData(itemType, new FloatValue(temp)));
			}else if(itemType.equals("long") || itemType.equals("java.lang.Long")){
				long temp = (Long)data;
				arrVal.arrayItems.add(new ArrayItemData(itemType, new LongValue(temp)));
			}else if(itemType.equals("short") || itemType.equals("java.lang.Short")){
				short temp = (Short)data;
				arrVal.arrayItems.add(new ArrayItemData(itemType, new ShortValue(temp)));
			}else if(itemType.equals("boolean") || itemType.equals("java.lang.Boolean")){
				boolean temp = (Boolean)data;
				arrVal.arrayItems.add(new ArrayItemData(itemType, new BooleanValue(temp)));
			}else if(data.getClass().isArray()){
//				System.err.println("ARRAY");			
				ArrayValue subArrVal = new ArrayValue(itemType);
				analzArray(data, subArrVal);
				arrVal.arrayItems.add(new ArrayItemData(itemType, subArrVal));
			}else{
				ObjectValue subObjVal = new ObjectValue(itemType);
				analzCompondPara(data, subObjVal);
				arrVal.arrayItems.add(new ArrayItemData(itemType, subObjVal));
			}				
		}
	}
	
	public void putPrimPara(int id, String type, Object obj) {
		if(type.equals("int")) {
			int temp = (Integer)obj;
			entries.add(new Parameter(id, new IntValue(temp)));
		} else if(type.equals("char")) {
			char temp = (Character)obj;
			entries.add(new Parameter(id, new CharValue(temp)));
		} else if(type.equals("byte")) {
			byte temp = (Byte)obj;
			entries.add(new Parameter(id, new ByteValue(temp)));
		} else if(type.equals("double")) {
			double temp = (Double)obj;
			entries.add(new Parameter(id, new DoubleValue(temp)));
		} else if(type.equals("float")) {
			float temp = (Float)obj;
			entries.add(new Parameter(id, new FloatValue(temp)));
		} else if(type.equals("long")) {
			long temp = (Long)obj;
			entries.add(new Parameter(id, new LongValue(temp)));
		} else if(type.equals("short")) {
			short temp = (Short)obj;
			entries.add(new Parameter(id, new ShortValue(temp)));
		} else if(type.equals("boolean")) {
			boolean temp = (Boolean)obj;
			entries.add(new Parameter(id, new BooleanValue(temp)));
		}
	}
	
	public void putPrimStatic(String clsName, String fieldName, String type, Object obj){
		if(type.equals("int")) {
			int temp = (Integer)obj;
			entries.add(new StaticField(clsName, fieldName, new IntValue(temp)));
		} else if(type.equals("char")) {
			char temp = (Character)obj;
			entries.add(new StaticField(clsName, fieldName, new CharValue(temp)));
		} else if(type.equals("byte")) {
			byte temp = (Byte)obj;
			entries.add(new StaticField(clsName, fieldName, new ByteValue(temp)));
		} else if(type.equals("double")) {
			double temp = (Double)obj;
			entries.add(new StaticField(clsName, fieldName, new DoubleValue(temp)));
		} else if(type.equals("float")) {
			float temp = (Float)obj;
			entries.add(new StaticField(clsName, fieldName, new FloatValue(temp)));
		} else if(type.equals("long")) {
			long temp = (Long)obj;
			entries.add(new StaticField(clsName, fieldName, new LongValue(temp)));
		} else if(type.equals("short")) {
			short temp = (Short)obj;
			entries.add(new StaticField(clsName, fieldName, new ShortValue(temp)));
		} else if(type.equals("boolean")) {
			boolean temp = (Boolean)obj;
			entries.add(new StaticField(clsName, fieldName, new BooleanValue(temp)));
		}
	}
	
	public void write(String fileName) {
		runtimes++; // inc run time
		try {
//			System.out.println("WRITING : " + fileName + "   RECORD : "+ this.key + "  RUNTIME : " + this.runtimes);
//			System.out.println("SIZE : " + analyzedObjs.size());
			Serializer serializer = new Persister();
			FileWriter writer = new FileWriter(fileName, true);
			writer.write("\n");
	        serializer.write(this, writer);
	        writer.close();
	        this.clear();
		} catch (Exception ex) {
			throw new RuntimeException();
		}
	}

	@Override
	public boolean equals(Object obj) { // cmp records except their runtime
		if(this == obj){
			return true;
		}
		if(obj instanceof Record){
			Record r = (Record) obj;
			if( ! this.key.equals(r.key)){
				return false;
			}
			try {
				return ObjCMP.isObjEqual(this.entries, r.entries);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
}
