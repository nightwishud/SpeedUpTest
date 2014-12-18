package cn.edu.pku.plde.callgraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import cn.edu.pku.plde.asm.LibClassNode;

public class LibAnalyzer implements Opcodes {
	
	public static Map<String, String> nonFinalStatMethod = new HashMap<String, String>();//<method-id, non-final-static-field-id>
	
	
	
	public static void findAllStaticMtd(){
//		Properties p = System.getProperties();
//		String jhome = p.getProperty("java.home");
		String jhome = "C:\\Program Files\\Java\\jdk1.6.0_38\\jre";
		jhome = jhome + "\\lib\\rt.jar";
		processLib(jhome);
		dumpNonFinal();
	}
	
	public static void dumpNonFinal() {
		System.out.println("\n------------------- DUMP NON-FINAL-STATIC ----------------------\n");
		Iterator it = nonFinalStatMethod.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			System.out.println(k + " -> " + v);
		}
		System.out.println("\n----------------------------------------------------------------\n");
	}

	private static void processLib(String jhome) {
		File f = new File(jhome);
		try {
			JarFile jar = new JarFile(f);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (e.getName().endsWith(".class")){
					//1. the first pass , to get the methodNode and build inherit tree
					InputStream is = jar.getInputStream(e);
					ClassReader cr_0 = new ClassReader(is);
					ClassNode cn_0 = new ClassNode();
					cr_0.accept(cn_0, 0); 
					
					HashSet<String> priStat = new HashSet<String>();
					List<FieldNode> fl = cn_0.fields;
					for(FieldNode fn : fl){
						if( ((fn.access & ACC_PUBLIC) == 0) && ((fn.access & ACC_STATIC) != 0)){// is not public but is static
							priStat.add(fn.name);
						}
					}
					//2. the second pass, collect the method info for the class
					if(e.getName().equals("java/lang/Integer.class")){
						
						for(String s : priStat){
							System.out.println(s + " ======================");
						}
						
						LibClassNode cn = new LibClassNode(cn_0.methods, priStat);
						is = jar.getInputStream(e);
						ClassReader cr = new ClassReader(is);
						cr.accept(cn, 0);
					}
				}
			}
			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		findAllStaticMtd();
	}
}
