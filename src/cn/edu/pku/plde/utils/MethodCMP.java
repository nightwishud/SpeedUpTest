package cn.edu.pku.plde.utils;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;

public class MethodCMP {
	/*
	 * is m1 equals m2 ? according to m2's name and descriptor
	 * */
	public static boolean isMethodEq(final Method m1, final String m2Name, final String m2Desc){ 
		final Type[] args = Type.getArgumentTypes(m2Desc);
		return !m2Name.equals(m1.getName()) || !sameType(args, m1.getParameterTypes());
	}
	
	private static boolean sameType(Type[] types, Class[] classes){
        if (types.length != classes.length) {
            return false;
        }
 
        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(classes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
	}
}
