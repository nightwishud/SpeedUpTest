package cn.edu.pku.plde.inher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class InheritanceTreeBuilder {

	public static HashMap<String, InheritTreeNode> inheritTree = new HashMap<String, InheritTreeNode>();
	public static HashMap<String, ClassNode> classNodes = new HashMap<String, ClassNode>();
	public static Set<Class> classSet = new HashSet<Class>();
	
	public static void loaderAllClasses(File f){
		if (!f.exists()) {
			System.err.println("Unable to read path " + f.getAbsolutePath());
			System.exit(-1);
		}
		URL url;
		try {
			url = f.toURI().toURL();
//			System.out.println(url);
			URLClassLoader loader = new URLClassLoader(new URL[]{url});
			Iterator it = classNodes.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry) it.next();
				String name = (String) entry.getKey();
				ClassNode cn = (ClassNode) entry.getValue();
//				System.out.println(name);
				Class c = loader.loadClass(name);
				System.out.println("LOAD OUTSIDE CLASS: " + name);
				classSet.add(c);
				
				while(c.getSuperclass() != null){
					c = c.getSuperclass();
//					System.out.println(c.getName());
					classSet.add(c);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}

	private static void processClass(File f) {
		try {
			ClassReader cr = new ClassReader(new FileInputStream(f));
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			String className = cn.name.replaceAll("/", ".");
			classNodes.put(className, cn);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void processJar(File f) {
		try {
			JarFile jar = new JarFile(f);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (e.getName().endsWith(".class")){
					ClassReader cr = new ClassReader(jar.getInputStream(e));
					ClassNode cn = new ClassNode();
					cr.accept(cn, 0);
					String className = cn.name.replaceAll("/", ".");
					classNodes.put(className, cn);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void processDirectory(File f) {
		for (File fi : f.listFiles()) {
//			System.out.println("FILE NAME: " + fi.getName());
			if (fi.isDirectory()){
				processDirectory(fi);
			}else if (fi.getName().endsWith(".class")){
				processClass(fi);
			}else if(fi.getName().endsWith(".jar")){
				// TODO add jar and zip
				processJar(fi);
			}else if(fi.getName().endsWith(".zip")){
				processZip(fi);
			}
		}
	}
	
	private static void processZip(File f) {
		
	}
	
//	public static void main(String[] args) {
//		String path = "D:\\test";
//		String path = "D:\\Code\\SVN\\SpeedUpTest\\bin\\cn\\edu\\pku\\plde\\test";
//		String path = "C:\\Program Files\\Java\\jre7\\lib\\rt.jar";
//		String path = "D:\\MyDocuments\\PKU\\Task\\asm-4.1.jar";
//		String path = "D:\\Code\\SVN\\SpeedUpTest\\bin\\cn\\edu\\pku\\plde\\test\\impl";
//		buildInherTree(path);
//		dumpInherTree();
//	}

	public static void buildInherTree(String path) {
		File f = new File(path);
		if (!f.exists()) {
			System.err.println("Unable to read path " + path);
			System.exit(-1);
		}
		if (f.isDirectory())
			processDirectory(f);
		else if (path.endsWith(".jar"))
			processJar(f);
		else if (path.endsWith(".class"))
			processClass(f);
		else if (path.endsWith(".zip")) {
			processZip(f);
		} else {
			System.err.println("Unknown type for path " + path);
			System.exit(-1);
		}
		if(classNodes.size() == 0){
			System.err.println("NO CLASS");
			return;
		}
//		System.out.println("CLASSNODE SIZE : " + classNodes.size());
		loaderAllClasses(f);
		buildeInherTree();
	}

	public static void dumpInherTree() {
		System.out.println("\n################## InherTree Size : " + inheritTree.size() + " ##################");
		System.out.println();
		Iterator it = inheritTree.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			String name = (String) entry.getKey();
			InheritTreeNode node = (InheritTreeNode) entry.getValue();
			if(node.getNode().isInterface()){
				System.out.print("------------------- INTERFACE :  ");
			}else{
				System.out.print("------------------- CLASS :  ");
			}
			System.out.print(name + "  --------------------------\n");
			for(Class c : node.getChildren()){
				System.out.println("\t" + c.getName());
			}
			System.out.println("---------------------------------------------------");
			System.out.println();
		}
		System.out.println("######################################################\n");
		System.out.println();
	}

	private static void buildeInherTree() {
		for(Class c: classSet){
			InheritTreeNode n =	inheritTree.get(c.getName());
			if(n == null){
				n = new InheritTreeNode(c);
				inheritTree.put(c.getName(), n);
			}
			// get it's direct father
			Class father = c.getSuperclass();
			// record father to child relation
			recordRealtion(c, father);
			// cope with implemented interfaces
			Class[] impls = c.getInterfaces();
			buildInterfaceRealation(c, impls);
		}
	}

	private static void recordRealtion(Class child, Class father){
		while(father != null){
			InheritTreeNode fn = inheritTree.get(father.getName());
			if(fn == null){
				fn = new InheritTreeNode(father);
				inheritTree.put(father.getName(), fn);
			}
			fn.getChildren().add(child);
			Class[] impls = father.getInterfaces();
			buildInterfaceRealation(child, impls);
			father = father.getSuperclass();
		}
	}
	
	private static void buildInterfaceRealation(Class child, Class[] impls){
		if(impls.length == 0){
			return;
		}
		for(Class ic : impls){
			InheritTreeNode in = inheritTree.get(ic.getName());
			if(in == null){
				in = new InheritTreeNode(ic);
				inheritTree.put(ic.getName(), in);
			}
			in.getChildren().add(child);
			buildInterfaceRealation(child, ic.getInterfaces());
		}
	}
}
