package cn.edu.pku.plde.shiyqw;


import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;

import cn.edu.pku.plde.info.*;

@Root
public class Record {
	Record() {
		entries = new ArrayList<Entry>();
	}
	Record(String id){
		entries = new ArrayList<Entry>();
	}
	void putEntry(Entry entry){
		entries.add(entry);
	}

	@ElementList
	List<Entry> entries;
/*	static public void main(String [] args) throws Exception {
		Record r = new Record();
//		Serializer serializer = new Persister();
//		File file = new File("output.txt");
//		serializer.write(r, file);
//		serializer.write(r, file);
//		System.out.println("End");
//	    PrintWriter output = new PrintWriter(file);
//	    output.println("---");
	    r.write("output.txt");
	    Entry entry = new Parameter("x", new IntValue(1));
	    r.putEntry(entry);
	    ObjectValue object = new ObjectValue();
	    object.putFieldData(new FieldData("float", new CharValue('c')));
	    object.putFieldData(new FieldData("g", new IntValue(10)));
	    entry = new Parameter("y", object);
	    r.putEntry(entry);
	    r.write("output.txt");
//	    output.close();
	}*/
	
	void putPrimPara(int id, String type, Object obj) {
		if(type == "int") {
			int temp = (Integer)obj;
			putEntry(new Parameter(id, new IntValue(temp)));
		} else if(type == "char") {
			char temp = (Character)obj;
			putEntry(new Parameter(id, new CharValue(temp)));
		} else if(type == "byte") {
			byte temp = (Byte)obj;
			putEntry(new Parameter(id, new ByteValue(temp)));
		} else if(type == "double") {
			double temp = (Double)obj;
			putEntry(new Parameter(id, new DoubleValue(temp)));
		} else if(type == "float") {
			float temp = (Float)obj;
			putEntry(new Parameter(id, new FloatValue(temp)));
		} else if(type == "long") {
			long temp = (Long)obj;
			putEntry(new Parameter(id, new LongValue(temp)));
		} else if(type == "short") {
			short temp = (Short)obj;
			putEntry(new Parameter(id, new ShortValue(temp)));
		} else if(type == "boolean") {
			boolean temp = (Boolean)obj;
			putEntry(new Parameter(id, new BooleanValue(temp)));
		}
	}
	
	void putStatic(String className, String fieldName, String type, Object obj) {
		if(type == "int") {
			int temp = (Integer)obj;
			putEntry(new StaticField(className, fieldName, new IntValue(temp)));
		} else if(type == "char") {
			char temp = (Character)obj;
			putEntry(new StaticField(className, fieldName, new CharValue(temp)));
		} else if(type == "byte") {
			byte temp = (Byte)obj;
			putEntry(new StaticField(className, fieldName, new ByteValue(temp)));
		} else if(type == "double") {
			double temp = (Double)obj;
			putEntry(new StaticField(className, fieldName, new DoubleValue(temp)));
		} else if(type == "float") {
			float temp = (Float)obj;
			putEntry(new StaticField(className, fieldName, new FloatValue(temp)));
		} else if(type == "long") {
			long temp = (Long)obj;
			putEntry(new StaticField(className, fieldName, new LongValue(temp)));
		} else if(type == "short") {
			short temp = (Short)obj;
			putEntry(new StaticField(className, fieldName, new ShortValue(temp)));
		} else if(type == "boolean") {
			boolean temp = (Boolean)obj;
			putEntry(new StaticField(className, fieldName, new BooleanValue(temp)));
		}
	}
	
	void write(String fileName) {
		try {
			Serializer serializer = new Persister();
			FileWriter writer = new FileWriter(fileName, true);
			writer.write("\n");
	        serializer.write(this, writer);
	        writer.close();
		} catch (Exception ex) {
			throw new RuntimeException();
		}
		
	}
}

class Entry {
	@Element
	Value value;
}

class Parameter extends Entry {
	Parameter(int id, Value value) {
		this.value = value;
		this.id = id;
	}
	@Attribute
	int id;
}

class StaticField extends Entry {
	StaticField(String className, String fieldName, Value value) {
		this.value = value;
		this.className = className;
		this.fieldName = fieldName;
	}
	@Attribute
	String className;
	@Attribute
	String fieldName;
}

class Value{}
class PrimitiveValue extends Value {}
class ByteValue extends PrimitiveValue {
	ByteValue(byte value) {
		this.value = value;
	}
	@Element
	byte value;
}
class CharValue extends PrimitiveValue {
	CharValue(char value) {
		this.value = value;
	}
	@Element
	char value;
}

class ShortValue extends  PrimitiveValue {
	ShortValue(short value) {
		this.value = value;
	}
	@Element
	short value;
}
class IntValue extends PrimitiveValue {
	IntValue(int value) {
		this.value = value;
	}
	@Element
	int value;
}
class FloatValue extends PrimitiveValue {
	FloatValue(float value) {
		this.value = value;
	}
	@Element
	float value;
}
class LongValue extends PrimitiveValue {
	LongValue(long value) {
		this.value = value;
	}
	@Element
	long value;
}

class DoubleValue extends PrimitiveValue {
	DoubleValue(double value) {
		this.value = value;
	}
	@Element
	double value;
}

class BooleanValue extends PrimitiveValue {
	BooleanValue(boolean value) {
		this.value = value;
	}
	@Element
	boolean value;
}

class ObjectValue extends Value {
	ObjectValue() {
		this.fields = new ArrayList<FieldData>();
		this.typeName = new None<String>();
	}
	
	void putFieldData(FieldData fieldData) {
		fields.add(fieldData);
	}
	
	void analCompPara(MethodInfo mi, String paraType, Object obj){
		ClassInfo ci = mi.getRelationsMap().get(paraType);
		for(FieldInfo field : ci.getFields()) {
			if(field.isTag()) {
				Object data;
				try {
					Field objField = obj.getClass().getDeclaredField(field.getName());  
					objField.setAccessible(true);   
					data = objField.get(obj);
				} catch (Exception ex) {
					throw new RuntimeException();
				}
				if(field.getType() == "int") {
					int temp = (Integer)data;
					putFieldData(new FieldData(field.getName(), new IntValue(temp)));
				} else if(field.getType() == "char") {
					char temp = (Character)data;
					putFieldData(new FieldData(field.getName(), new CharValue(temp)));
				} else if(field.getType() == "byte") {
					byte temp = (Byte)data;
					putFieldData(new FieldData(field.getName(), new ByteValue(temp)));
				} else if(field.getType() == "double") {
					double temp = (Double)data;
					putFieldData(new FieldData(field.getName(), new DoubleValue(temp)));
				} else if(field.getType() == "float") {
					float temp = (Float)data;
					putFieldData(new FieldData(field.getName(), new FloatValue(temp)));
				} else if(field.getType() == "long") {
					long temp = (Long)data;
					putFieldData(new FieldData(field.getName(), new LongValue(temp)));
				} else if(field.getType() == "short") {
					short temp = (Short)data;
					putFieldData(new FieldData(field.getName(), new ShortValue(temp)));
				} else if(field.getType() == "boolean") {
					boolean temp = (Boolean)data;
					putFieldData(new FieldData(field.getName(), new BooleanValue(temp)));
				}
				/*...*/
				continue;
			}
			ObjectValue ov = new ObjectValue();
			try {
				Field objField = obj.getClass().getDeclaredField(field.getName());  
				objField.setAccessible(true);   
				ov.analCompParaField(mi, field, objField.get(obj)/*refl*/);
				putFieldData(new FieldData(field.getName(), ov));
			} catch (Exception ex) {
				throw new RuntimeException();
			}
		}
	}
	
	void analCompParaField(MethodInfo mi, FieldInfo fi, Object obj){
		ClassInfo ci = mi.getRelationsMap().get(fi.getType());
		for(FieldInfo field : ci.getFields()) {
			if(field.isTag()) {
				Object data;
				try {
					Field objField = obj.getClass().getDeclaredField(field.getName());  
					objField.setAccessible(true);   
					data = objField.get(obj);
				} catch (Exception ex) {
					throw new RuntimeException();
				}
				if(field.getType() == "int") {
					int temp = (Integer)data;
					putFieldData(new FieldData(field.getName(), new IntValue(temp)));
				} else if(field.getType() == "char") {
					char temp = (Character)data;
					putFieldData(new FieldData(field.getName(), new CharValue(temp)));
				} else if(field.getType() == "byte") {
					byte temp = (Byte)data;
					putFieldData(new FieldData(field.getName(), new ByteValue(temp)));
				} else if(field.getType() == "double") {
					double temp = (Double)data;
					putFieldData(new FieldData(field.getName(), new DoubleValue(temp)));
				} else if(field.getType() == "float") {
					float temp = (Float)data;
					putFieldData(new FieldData(field.getName(), new FloatValue(temp)));
				} else if(field.getType() == "long") {
					long temp = (Long)data;
					putFieldData(new FieldData(field.getName(), new LongValue(temp)));
				} else if(field.getType() == "short") {
					short temp = (Short)data;
					putFieldData(new FieldData(field.getName(), new ShortValue(temp)));
				} else if(field.getType() == "boolean") {
					boolean temp = (Boolean)data;
					putFieldData(new FieldData(field.getName(), new BooleanValue(temp)));
				}
				/*...*/
				continue;
			}
			ObjectValue ov = new ObjectValue();
			try {
				Field objField = obj.getClass().getDeclaredField(field.getName());  
				objField.setAccessible(true);   
				ov.analCompParaField(mi, field, objField.get(obj)/*refl*/);
				putFieldData(new FieldData(field.getName(), ov));
			} catch (Exception ex) {
				throw new RuntimeException();
			}
		}
	}
	
	@Element
	Option<String> typeName;
	@ElementList
	List<FieldData> fields;
}

class FieldData {
	FieldData(String fieldName, Value value) {
		this.fieldName = fieldName;
		this.value = value;
	}
	@Attribute
	String fieldName;
	@Element
	Value value;
}

abstract class Option<T> {}
class Some<T> extends Option <T> {
	Some(T value) {
		this.value = value;
	}
	@Element
	T value;
}
class None<T> extends Option <T> {
	None() {
		this.message = "none";
	}
	@Element
	String message;
}

