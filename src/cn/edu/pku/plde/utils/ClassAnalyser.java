package cn.edu.pku.plde.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import soot.Scene;
import soot.SootClass;
import cn.edu.pku.plde.asm.AnalyserClassNode;
import cn.edu.pku.plde.callgraph.CallGraphBuilder;
import cn.edu.pku.plde.info.ClassInfo;
import cn.edu.pku.plde.info.FieldInfo;
import cn.edu.pku.plde.info.MethodInfo;
import cn.edu.pku.plde.info.ParameterInfo;
import cn.edu.pku.plde.info.StaticInfo;


public class ClassAnalyser {
	public static HashMap<String, List<MethodInfo>> totalClassInfo = new HashMap<String, List<MethodInfo>>(); // <Key: className, Value: methods info 

	private static int totalMethodNum = 0;
	private static int totalParaNum = 0;
	private static int totalPrimParaNum = 0;
	private static int totalStaticNum = 0;
	private static int totalPrimStaticNum = 0;
	private static int primitiveMethodNum = 0;
	private static int noParaMethodNum = 0;
	private static int noStaticMethodNum = 0;
	private static int noInputMethodNum = 0;
	
	public static void analysisClassFromDir(String path){
		
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
	
		dumpTotalClassInfo();
		dumpStatistics();

	}

	private static void dumpStatistics() {
		System.out.println("\n\n########################  Statistics  #################################");
		System.out.println("totalMethodNum : " + totalMethodNum + "  primitiveMethodNum : " + primitiveMethodNum + "  noInputMethodNum : " + noInputMethodNum);
		System.out.println("totalParaNum : "+ totalParaNum + "  totalPrimParaNum : " + totalPrimParaNum);
		System.out.println("totalStaticNum : " + totalStaticNum + "  totalPrimStaticNum : " + totalPrimStaticNum);
		System.out.println("noParaMethodNum : " + noParaMethodNum + "  noStaticMethodNum : " + noStaticMethodNum);
		System.out.println("#########################################################\n\n");
	}

	private static void processZip(File f) {
		// TODO Auto-generated method stub
		
	}

	private static void processClass(File f) {
		try {
			//1. the first pass , to get the methodNode and build inherit tree
			ClassReader cr_0 = new ClassReader(new FileInputStream(f));
			ClassNode cn_0 = new ClassNode();
			cr_0.accept(cn_0, 0); 
			
			//2. the second pass, collect the method info for the class		
			AnalyserClassNode cn_1 = new AnalyserClassNode(cn_0.methods);
			ClassReader cr_1 = new ClassReader(new FileInputStream(f));
			cr_1.accept(cn_1, 0);
			String className = cn_1.name.replaceAll("/", ".");
			if(totalClassInfo.containsKey(className)){
				return;
			}else{
				List<MethodInfo> list = cn_1.getmInfoList();
				totalClassInfo.put(className, list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void processJar(File f) { // TODO:: not complete
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
					
					AnalyserClassNode cn = new AnalyserClassNode(cn_0.methods);
					
					//2. the second pass, collect the method info for the class
					is = jar.getInputStream(e);
					ClassReader cr = new ClassReader(is);
					cr.accept(cn, 0);
					String className = cn.name.replaceAll("/", ".");
					if(totalClassInfo.containsKey(className)){
						return;
					}else{
						List<MethodInfo> list = cn.getmInfoList();
						totalClassInfo.put(className, list);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public static void dumpTotalClassInfo(){
		System.out.println("\n\n#################### TOTAL-CLSINFO-SIZE: " + totalClassInfo.size() + "  ########################\n");
		Iterator it = totalClassInfo.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			String name = (String) entry.getKey();
			List<MethodInfo> list = (List<MethodInfo>) entry.getValue();
			System.out.println("================================  " + name + "  =====================================");
			for(MethodInfo mi : list){
				dumpMethodInfo(mi);
				totalMethodNum++;
			}
			System.out.println("================================================================================================\n");
		}
		
		System.out.println("#######################################################################\n");
	}
	
	private static void dumpMethodInfo(MethodInfo mInfo) {
		boolean primMethod = true;
		boolean noInput = true;
		System.out.println("\n------------------------  " + mInfo.getMethodName() +"  STATIC : " + mInfo.isStatic() +"  --------------------------------");
		if(mInfo.getParameters().size() > 0){
			System.out.println("PARAMETER NUM :" + mInfo.getParaNum());
			System.out.println("PARAMETER INFO :");
			for(ParameterInfo pi : mInfo.getParameters()){
				System.out.println("\t PARA-ID: " + pi.getId() + "\t PARA-SITE: "+ pi.getSite() + "\t PARA-TYPE: " + pi.getType() 
						 + "\t PARA-DESC: " + pi.getTypeDesc() + "\t PARA-PRIM: " + pi.isTag());
				totalParaNum++;
				primMethod = primMethod && pi.isTag();
				if(pi.isTag()){
					totalPrimParaNum++;
				}
			}
			noInput = false;
		}else{
			noParaMethodNum++;
			noInput = true;
		}
		if(mInfo.getStatics().size() > 0){
			System.out.println();
			System.out.println("STATIC INFO :");
			for(StaticInfo si : mInfo.getStatics()){
				System.out.println("\t CLS-NAME: " + si.getClassName() + "\t FLD-NAME: " + si.getFieldName() + 
						"\t TYPE: " + si.getType() +  "\t DESC: "+ si.getTypeDesc() + "\t PRIMITIVE: " + si.isTag());
				totalStaticNum++;
				primMethod = primMethod && si.isTag();
				if(si.isTag()){
					totalPrimStaticNum++;
				}
			}
			noInput = false;
		}else{
			noStaticMethodNum++;
			noInput = noInput && true;
		}
		if(primMethod){
			primitiveMethodNum++;
		}
		if(noInput){
			noInputMethodNum++;
		}
		if(mInfo.getRelationsMap().size()>0){
			System.out.println();
			System.out.println("FIELD INFO :");
			Iterator it = mInfo.getRelationsMap().entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry) it.next();
				String owner = (String) entry.getKey();
				List<FieldInfo> fl = ((ClassInfo) entry.getValue()).getFields();
				for(FieldInfo fi: fl){
					System.out.println("\tFIELD-BELONGS-TO: " + owner + "\tFIELD-NAME: " + fi.getName() + 
							"\tFIELD-TYPE: " + fi.getType() + "\tFIELD-DESC: " + fi.getDesc());
				}
			}
		}
		System.out.println("----------------------------------------------------------------------------------------------\n");
	}
	
	public static MethodInfo findMethodInfoByName(String owner, String name, String desc){
//		System.out.println(owner);
		List<MethodInfo> list = totalClassInfo.get(owner);
//		System.out.println("findMethodInfoByName   LIST : " + list + " \t OWNER : " + owner + " \t NAME: " + name);
		for(MethodInfo info: list){
			String[] names = info.getMethodName().split(" ");
			if(names[1].equals(name) && names[2].equals(desc)){
//				System.out.println("findMethodInfoByName success");
				return info;
			}
		}
		System.err.println("findMethodInfoByName fail : " + owner + " " + name + " " + desc);
		return null;
	}
	
//	public static void completeStaticInfoWithCallGraph() {
//		boolean fixed = false;
//		do{
//			Iterator it = totalClassInfo.entrySet().iterator();
//			while(it.hasNext()){
//				Map.Entry entry = (Map.Entry) it.next();
//				String className = (String) entry.getKey();
//				SootClass c = Scene.v().getSootClass(className);
//				if(c == null){
//					System.err.println("MISS SOOTCLASS " + className);
//				}
//				
//				List<MethodInfo> list = (List<MethodInfo>) entry.getValue();
//				System.out.println("================================  " + className + "  =====================================");
//				for(MethodInfo mi : list){
//					String methodName = mi.getMethodName().split(" ")[1];
//					
//				}
//				System.out.println("================================================================================================\n");
//			}
//		}while(fixed);
//	}
	
	public static void main(String[] args){
		String path = "D:\\Code\\SVN\\SpeedUpTest\\bin\\cn\\edu\\pku\\plde\\test\\rec\\Test.class";
//		String path = "D:\\MyDocuments\\PKU\\Task\\asm-4.1.jar";
//		String path = "D:\\Code\\SVN\\SUPCGTest\\bin\\cn\\edu\\pku\\plde\\test\\callgraph\\";
//		String path = "D:/MyProgram/Test_Program/jgrapht-0.8.3/build/";
//		String path = "D:/MyProgram/Test_Program/jscience-4.3/bin";
//		String path = "/home/nightwish/workspace/test_program/jgrapht-0.8.3/build";
//		String path = "D:\\MyProgram\\Test_Program\\commons-math3-3.3\\target\\classes";
		analysisClassFromDir(path);
//		completeStaticInfoWithCallGraph();
	}

}
