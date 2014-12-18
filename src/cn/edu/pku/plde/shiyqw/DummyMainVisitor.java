package cn.edu.pku.plde.shiyqw;

import java.io.*;
import java.util.HashSet;
import java.util.Hashtable;

import org.objectweb.asm.*;

import cn.edu.pku.plde.info.*;
import cn.edu.pku.plde.utils.ClassAnalyser;

public class DummyMainVisitor extends ClassVisitor implements Opcodes{
	private boolean hasMain = false;
	private HashSet<Method> testMethods = new HashSet<Method>();
	private String className;
	public DummyMainVisitor(ClassVisitor cv) {
		super(ASM4, cv);
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.className = name;
		cv.visit(version, access, name, signature, superName, interfaces);
	}

	
	@Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
	
		if(name.startsWith("test")) {	
			if((access & ACC_STATIC) != 0) {
				testMethods.add(new Method(true, name, desc));
			} else {
				testMethods.add(new Method(false, name, desc));
			}
		}
		return mv;
	}
	@Override
	public void visitEnd() {
		if(!hasMain) {
			// visit main
			MethodVisitor mv = cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
	//		// mv.xxx
			mv.visitTypeInsn(NEW, className);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "()V");
			mv.visitVarInsn(ASTORE, 1);
			for(Method method : testMethods) {
				if(method.isStatic()) {
					mv.visitMethodInsn(INVOKESTATIC, className, method.getName(), method.getDesc());
					System.out.println("Static: " + className + " " + method.getName() + " " + method.getDesc());
				} else {
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, className, method.getName(), method.getDesc());
					System.out.println("VIRTUAL: " + className + " " + method.getName() + " " + method.getDesc());
				}
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(testMethods.size(), 2);
			mv.visitEnd();
		}
	//	cv.visitEnd();
		System.out.println("here");
		cv.visitEnd();
	}

	public static void main(String [] args) throws Exception {
		String className = TestClass.class.getName();
	    String path = "bin/cn/edu/pku/plde/shiyqw/TestClass.class";
	    parseClass(className, path);
	}
	
	public static void parseClass(String className, String path) throws Exception {
		ClassReader cr = new ClassReader(className);
		ClassWriter cw = new ClassWriter(cr, 0);
		DummyMainVisitor dmv = new DummyMainVisitor(cw);
		cr.accept(dmv, 0);
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

class Method {
	private String desc;
	private String name;
	private boolean isStatic;
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public Method(boolean isStatic, String name, String desc) {
		this.isStatic = isStatic;
		this.desc = desc;
		this.name = name;
	}
}

