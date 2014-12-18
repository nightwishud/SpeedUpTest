package cn.edu.pku.plde.info;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import cn.edu.pku.plde.test.Student;
import cn.edu.pku.plde.utils.MethodCMP;

public class MethodInfoCollector {
	private String className;
	public Map<String, MethodInfo> methodInfoMap; // the methods info of a class
	public static Map<String, ClassInfo> classPool = new HashMap<String, ClassInfo>();
	
	public MethodInfoCollector(String name){
		this.className = name;
		this.methodInfoMap = new HashMap<String, MethodInfo>();
	}
	
	public static MethodInfo getMethodInfo(final Method m){
		String className = m.getDeclaringClass().getName();
		String methodName = m.getName();
		String methodDesc = Type.getMethodDescriptor(m);
//		System.out.println(className + "    " + m.getName() + "    " + methodDesc);

		final MethodInfo mInfo = new MethodInfo(className + " " + methodName + " " + methodDesc);
		ClassReader cr = null;
		try {
			cr = new ClassReader(className);
			cr.accept(new ClassVisitor(Opcodes.ASM4){
				
	            public MethodVisitor visitMethod(final int access, final String name, final String desc,
	                    final String signature, final String[] exceptions) {
	            	if(MethodCMP.isMethodEq(m, name, desc)){
	                    return super.visitMethod(access, name, desc, signature,
	                            exceptions);
	                }
//	                MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
//	                MInfoMethodAdapter adapter = new MInfoMethodAdapter(Opcodes.ASM4,v, m, mInfo);
//	                
//	                
//	                return adapter;
                    return super.visitMethod(access, name, desc, signature,
                            exceptions);
	            }

				@Override
				public void visitEnd() {
					System.out.println(mInfo.getParameters().size() + "------------");
					super.visitEnd();
				}
	            
	            
	            
			}, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mInfo;
	}
	
	public static void main(String[] args) throws SecurityException, ClassNotFoundException{
		MethodInfoCollector mic = new MethodInfoCollector(Student.class.getName());
		Method[] methods;
		
			methods = Class.forName(Student.class.getName()).getDeclaredMethods();
			for(Method m:methods){
				if(m.getName().equals("test"))
					getMethodInfo(m);
			}
		

	}
}
