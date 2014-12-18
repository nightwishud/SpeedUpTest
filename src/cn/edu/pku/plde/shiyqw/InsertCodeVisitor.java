package cn.edu.pku.plde.shiyqw;

import java.io.*;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;

import cn.edu.pku.plde.info.*;
import cn.edu.pku.plde.utils.ClassAnalyser;

public class InsertCodeVisitor extends ClassVisitor implements Opcodes{
	private boolean isInterface;
	String className;
	public InsertCodeVisitor(ClassVisitor cv) {
		super(ASM4, cv);
	}
	@Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		isInterface = (access & ACC_INTERFACE) != 0;
		className = name;
	}
	@Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		if(!isInterface && mv != null && !name.equals("<init>") && !name.equals("main")) {	// TODO:: test ??
			mv = new InsertCodeMethodAdapter(access, name, desc, mv);
		}/*else if(name.equals("main")){
			mv = new MethodVisitor(Opcodes.ASM4, mv){

				@Override
				public void visitCode() {
					super.visitCode();
//				 	mv.visitLdcInsn("bin/cn/edu/pku/plde/test/Sort.class");
				 	mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/shiyqw/Record", "putPrimPara", "(ILjava/lang/String;Ljava/lang/Object;)V");
				}
				
			};
		}*/
		return mv;
	}
	class InsertCodeMethodAdapter extends LocalVariablesSorter {
		String methodName;
		String ownerName;
		String simpleName;
		String desc;
		boolean isStatic;
		MethodInfo mi;
		public InsertCodeMethodAdapter(int access, String name, String desc, MethodVisitor mv) {
			super(ASM4, desc, mv);
			methodName = className+"."+name+desc;
			ownerName = className.replace('/', '.');
			simpleName = name;
			isStatic = (access & ACC_STATIC) != 0;
			this.desc = desc;
			mi = ClassAnalyser.findMethodInfoByName(ownerName, simpleName, desc);
		}
		@Override 
		public void visitCode() {
			
			mv.visitCode();
			// Insert
			int miNumber = mi.getParaNum()+1;
			int recordNumber = miNumber+1;
			int ovNumber = miNumber+2;

			mv.visitLdcInsn(ownerName);
			mv.visitLdcInsn(simpleName);
			mv.visitLdcInsn(desc);
			mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/utils/ClassAnalyser", "findMethodInfoByName", 
					"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcn/edu/pku/plde/info/MethodInfo;");
			mv.visitVarInsn(ASTORE, miNumber);
			// new record
			mv.visitTypeInsn(NEW, "cn/edu/pku/plde/shiyqw/Record");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/shiyqw/Record", "<init>", "()V");
			mv.visitVarInsn(ASTORE, recordNumber);
			// new ov
			mv.visitTypeInsn(NEW, "cn/edu/pku/plde/shiyqw/ObjectValue");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/shiyqw/ObjectValue", "<init>", "()V");
			mv.visitVarInsn(ASTORE, ovNumber);
			
			// Add parameter Info
			for(ParameterInfo parameter : mi.getParameters()) {
				String type = parameter.getType();
				int id = parameter.getId();
				if(!isStatic) id = id+1;
				if(parameter.isTag()) {
					System.out.println(id);
					System.out.println(type);
					mv.visitVarInsn(ALOAD, recordNumber);
					mv.visitIntInsn(BIPUSH, id);
					mv.visitLdcInsn(type);
					if(type.equals("int")) {
						mv.visitVarInsn(ILOAD, id);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
					} else if(type.equals("char")) {
						mv.visitVarInsn(ILOAD, id);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
					} else if(type.equals("byte")) {
						mv.visitVarInsn(ILOAD, id);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
					}  else if(type.equals("double")) {
						mv.visitVarInsn(DLOAD, id);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
					}  else if(type.equals("long")) {
						mv.visitVarInsn(LLOAD, id);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
					}  else if(type.equals("float")) {
						mv.visitVarInsn(FLOAD, id);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
					}  else if(type.equals("short")) {
						mv.visitVarInsn(ILOAD, id);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
					}  else if(type.equals("boolean")) {
						mv.visitVarInsn(ILOAD, id);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
					} 
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/shiyqw/Record", "putPrimPara", "(ILjava/lang/String;Ljava/lang/Object;)V");
				} else {
					System.out.println(id);
					System.out.println(type);
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/shiyqw/ObjectValue");
					mv.visitInsn(DUP);
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/shiyqw/ObjectValue", "<init>", "()V");
					mv.visitVarInsn(ASTORE, ovNumber);
					
					mv.visitVarInsn(ALOAD, ovNumber);
					mv.visitVarInsn(ALOAD, miNumber);
					mv.visitLdcInsn(type);
					mv.visitVarInsn(ALOAD, id);
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/shiyqw/ObjectValue", "analCompPara", "(Lcn/edu/pku/plde/info/MethodInfo;Ljava/lang/String;Ljava/lang/Object;)V");
					
					mv.visitVarInsn(ALOAD, recordNumber);
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/shiyqw/Parameter");
					mv.visitInsn(DUP);
					mv.visitIntInsn(BIPUSH, id);
					mv.visitVarInsn(ALOAD, ovNumber);
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/shiyqw/Parameter", "<init>", "(ILcn/edu/pku/plde/shiyqw/Value;)V");
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/shiyqw/Record", "putEntry", "(Lcn/edu/pku/plde/shiyqw/Entry;)V");
				}
			}
			
			
			for(StaticInfo staticField : mi.getStatics()) {
				mv.visitVarInsn(ALOAD, recordNumber);
				mv.visitLdcInsn(staticField.getClassName());
				mv.visitLdcInsn(staticField.getFieldName());
				mv.visitLdcInsn(staticField.getType());
				String className = staticField.getClassName();
				String fieldName = staticField.getFieldName();
				String type = staticField.getType();
				if(type == "int") {
					mv.visitFieldInsn(GETSTATIC, className, fieldName, "I");
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				} else if(type == "char") {
					mv.visitFieldInsn(GETSTATIC, className, fieldName, "C");
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
				} else if(type == "byte") {
					mv.visitFieldInsn(GETSTATIC, className, fieldName, "B");
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
				}  else if(type == "double") {
					mv.visitFieldInsn(GETSTATIC, className, fieldName, "D");
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				}  else if(type == "long") {
					mv.visitFieldInsn(GETSTATIC, className, fieldName, "J");
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				}  else if(type == "float") {
					mv.visitFieldInsn(GETSTATIC, className, fieldName, "F");
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				}  else if(type == "short") {
					mv.visitFieldInsn(GETSTATIC, className, fieldName, "S");
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
				}  else if(type == "boolean") {
					mv.visitFieldInsn(GETSTATIC, className, fieldName, "Z");
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
				} 
				mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/shiyqw/Record", "putStatic", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
			}
			
			// record write
			String recordFile = mi.getMethodName()+".txt";
			recordFile = recordFile.replace('/', '.');
			mv.visitVarInsn(ALOAD, recordNumber);
			mv.visitLdcInsn(recordFile);
			mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/shiyqw/Record", "write", "(Ljava/lang/String;)V");

		}
		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			mv.visitMaxs(maxStack+3, maxLocals+6);
		}
	}

//	public static void main(String [] args) throws Exception {
//		
//		ClassAnalyser.analysisClassFromDir("bin/cn/edu/pku/plde/shiyqw/A.class");
//		
//		ClassReader cr = new ClassReader(A.class.getName());
//		ClassWriter cw = new ClassWriter(cr, 0);
//		InsertCodeVisitor icv = new InsertCodeVisitor(cw);
//		cr.accept(icv, 0);
//		byte[] b2 = cw.toByteArray();
//	    File fileOut = new File("bin/cn/edu/pku/plde/shiyqw/A.class");
//	    if(fileOut.exists()){        
//	    	fileOut.delete();
//	    }
//	    FileOutputStream output = null;
//	    output = new FileOutputStream(fileOut);
//	    output.write(b2,0,b2.length); 
//	    output.close();
//		System.out.println("Start");
//		A a = new A();
//		a.test();
//	}
	
	public static void parseClass(String className, String path) throws Exception {
		ClassReader cr = new ClassReader(className);
		ClassWriter cw = new ClassWriter(cr, 0);
		InsertCodeVisitor icv = new InsertCodeVisitor(cw);
		cr.accept(icv, 0);
		byte[] b2 = cw.toByteArray();
	    File fileOut = new File(path);
	    if(fileOut.exists()){
	    	fileOut.delete();
	    }
	    FileOutputStream output = null;
	    output = new FileOutputStream(fileOut);
	    output.write(b2,0,b2.length); 
	    output.close();
	}
}
