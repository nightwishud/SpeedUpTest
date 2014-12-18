package cn.edu.pku.plde.callgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import cn.edu.pku.plde.info.MethodInfo;
import cn.edu.pku.plde.info.StaticInfo;
import cn.edu.pku.plde.utils.ClassAnalyser;

public class CallGraphBuilder {
	
	public static void getCallGraph(final String cp, final String processDir, final String clsName, final String mtdName, final String mainCls){
		
		final List<String> argsList = new ArrayList<String>();
//		   argsList.addAll(Arrays.asList(new String[] { "-w", "-cp", appDir,"-process-dir", appDir,  //"all-reachable:true",
//					"-pp", "-allow-phantom-refs", "-output-format", "none"}));	//"-p", "cg.spark", "enabled:true"
		
		if(processDir.contains(";")){//TODO:: in UNIX change to ':'
			String[] paths = processDir.split(";");
			argsList.addAll(Arrays.asList(new String[] { "-w", "-cp", cp, "-main-class", mainCls, "-p", "cg", "verbose:true",
					"-pp", "-allow-phantom-refs", "-output-format", "none"}));
			for(String s : paths){
				argsList.add("-process-dir");
				argsList.add(s);
			}
		}else{
			argsList.addAll(Arrays.asList(new String[] { "-w", "-cp", cp,"-process-dir", processDir, "-main-class", mainCls,
					"-pp", "-allow-phantom-refs", "-p", "cg", "verbose:true", "-output-format", "none"}));
		}
		
		/*
		   PackManager.v().getPack("cg").add(new Transform("cg.myTrans", new SceneTransformer() {

				@Override
				protected void internalTransform(String phaseName, Map options) {
				
					SootClass a = Scene.v().forceResolve(clsName, SootClass.SIGNATURES);
					a.setApplicationClass();
					Scene.v().loadNecessaryClasses();
					List<SootMethod> entryMethods = new ArrayList<SootMethod>();
//					System.out.println(a.getMethods().toString());
//					SootMethod method = a.getMethodByName(mtdName);
					for(SootMethod m : a.getMethods()){
						entryMethods.add(m);
					}
//					entryMethods.add(method);
					Scene.v().setEntryPoints(entryMethods);
					
					
				}
			}));
			*/
		
		   PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

				@Override
				protected void internalTransform(String phaseName, Map options) {
					
//				       CHATransformer.v().transform();
//				       SootClass a = Scene.v().getSootClass(clsName);
//				       SootMethod method = a.getMethodByName(mtdName);
//				       CallGraph cg = Scene.v().getCallGraph();
//
//				       Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(method));
//				       while (targets.hasNext()) {
//				           SootMethod tgt = (SootMethod)targets.next();
//				           System.out.println(method + " may call " + tgt);
//				       }
				       
						HashMap<String, String> optionsMap = new HashMap<String, String>();
						optionsMap.put("enabled", "true");
						optionsMap.put("verbose", "true");
						optionsMap.put("propagator", "worklist");
						optionsMap.put("simple-edges-bidirectional", "false");
						optionsMap.put("on-fly-cg", "true");
						optionsMap.put("set-impl", "double");
						optionsMap.put("double-set-old", "hybrid");
						optionsMap.put("double-set-new", "hybrid");
						optionsMap.put("whole-program", "true");
						
						SparkTransformer.v().transform("", optionsMap);
						CallGraph cg = Scene.v().getCallGraph();
//						System.out.println("Call graph size: " + cg.size());
						SootClass a = Scene.v().getSootClass(clsName);
						SootMethod src = a.getMethodByName(mtdName);
						Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(src));
						while (targets.hasNext()) {
				           SootMethod tgt = (SootMethod)targets.next();
				           System.out.println(src + " may call " + tgt);
						}
				}
				   
			   }
		   ));
		   
		String[] args = argsList.toArray(new String[0]);
		soot.Main.main(args);
	}
	
	public static void completeStaticInfoWithCallGraph(final String cp, final String processDir, 
			final String mainCls){
		final List<String> argsList = new ArrayList<String>();
		if(processDir.contains(";")){ //TODO:: in UNIX change to ':' ?
			String[] paths = processDir.split(";");
			argsList.addAll(Arrays.asList(new String[] { "-w", "-cp", cp, "-main-class", mainCls, "-p", "cg", "verbose:true",
					"-pp", "-allow-phantom-refs", "-output-format", "none"}));
			for(String s : paths){
				argsList.add("-process-dir");
				argsList.add(s);
			}
		}else{
			argsList.addAll(Arrays.asList(new String[] { "-w", "-cp", cp,"-process-dir", processDir, "-main-class", mainCls,
					"-pp", "-allow-phantom-refs", "-p", "cg", "verbose:true", "-output-format", "none"}));
		}
		

		
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map options) {
		       
					HashMap<String, String> optionsMap = new HashMap<String, String>();
					optionsMap.put("enabled", "true");
					optionsMap.put("verbose", "true");
					optionsMap.put("propagator", "worklist");
					optionsMap.put("simple-edges-bidirectional", "false");
					optionsMap.put("on-fly-cg", "true");
					optionsMap.put("set-impl", "double");
					optionsMap.put("double-set-old", "hybrid");
					optionsMap.put("double-set-new", "hybrid");
					optionsMap.put("whole-program", "true");
					
					SparkTransformer.v().transform("", optionsMap);
					CallGraph cg = Scene.v().getCallGraph();
					
					for(SootMethod src : Scene.v().getMainClass().getMethods()){
						if(src.getName().startsWith("test")){
							Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(src));
							while (targets.hasNext()) {
						           SootMethod tgt = (SootMethod)targets.next();
						           System.out.println(src + " may call " + tgt);
						           
						           MethodInfo mi = getMtdInfo(tgt);
						           if(mi != null){// means tgt is one of the application method
//						        	   System.out.println( " ---> " + mi.getMethodName());
						        	   totalStat = 0;
						        	   completeOneLoop(cg, tgt, mi);
						        	   do{
						        		   totalMtd.clear();
						        		   int s1 = totalStat;
						        		   completeOneLoop(cg, tgt, mi);
						        		   if(totalStat > s1){
						        			   fixed = false;
						        		   }else{
						        			   fixed = true;
						        		   }
						        	   }while(!fixed);
						           }
							}
						}
					}
				}
		   	}
			));
	   
		String[] args = argsList.toArray(new String[0]);
		soot.Main.main(args);
	}
	
	private static int totalStat = 0;
	
	private static HashSet<String> totalMtd = new HashSet<String>();
	
	private static boolean fixed = false;
	
	
	private static void completeOneLoop(CallGraph cg, SootMethod src, MethodInfo srcMInfo){
		Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(src));
		while(targets.hasNext()){
			SootMethod tgt = (SootMethod)targets.next();
			MethodInfo mi = getMtdInfo(tgt);
			if(mi != null){// means tgt is one of the application method
//				System.out.println( src.getName() + " ---> " + tgt.getName());
				if(totalMtd.contains(mi.getMethodName())){
					continue;
				}else{
					totalMtd.add(mi.getMethodName());
					if(mi.getStatics().size() >=0 ){
						int s1 = srcMInfo.getStatics().size();
						srcMInfo.getStatics().removeAll(mi.getStatics());
						srcMInfo.getStatics().addAll(mi.getStatics());
						totalStat += (srcMInfo.getStatics().size() - s1);
					}
					completeOneLoop(cg, tgt, mi);
				}
			}
		}
	}
	
	private static MethodInfo getMtdInfo(SootMethod tgt){
		List<MethodInfo> list = ClassAnalyser.totalClassInfo.get(tgt.getDeclaringClass().getName());
		if(list != null){
	        for(MethodInfo mi : list){
	     	   if(mi.getMethodName().split(" ")[1].equals(tgt.getName())){
	     		   return mi;
	     	   }
	        }
        }
        return null;
	}
	
	public static void main(String[] args){
//		CallGraphBuilder.getCallGraph("D:\\SootCallGraphTest\\bin", "testers.CallGraphs", "doStuff");
//		CallGraphBuilder.getCallGraph("D:\\test\\", "Test1", "test2", "Test1");
		
//		CallGraphBuilder.getCallGraph("D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build;D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\testbuild",
//				"D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build;D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\testbuild",
//				"org.jgrapht.alg.BellmanFordShortestPath", 
//				"getPathEdgeList", 
//				"org.jgrapht.alg.BellmanFordShortestPathTest");
		
//		ClassAnalyser.analysisClassFromDir("D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build");
//		
//		CallGraphBuilder.completeStaticInfoWithCallGraph("D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build;D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\testbuild",
//				"D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build;D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\testbuild",
//				"org.jgrapht.alg.BellmanFordShortestPathTest");
//		System.out.println("\n\n\n");
//		ClassAnalyser.dumpTotalClassInfo();
		
//		String path9 = "D:\\MyProgram\\Test_Program\\commons-math3-3.3\\target\\classes";
//		String path10 = "D:\\MyProgram\\Test_Program\\commons-math3-3.3\\target\\test-classes";
		
		String path9 = "D:\\Code\\SVN\\SUPCGTest\\bin";

		ClassAnalyser.analysisClassFromDir(path9);
//		CallGraphBuilder.completeStaticInfoWithCallGraph((path9 + ";" + path10), (path9 + ";" + path10),
//				"org.apache.commons.math3.analysis.FunctionUtilsTest");
		
		CallGraphBuilder.completeStaticInfoWithCallGraph(path9, path9,
				"cn.edu.pku.plde.test.callgraph.SmpStaticTest");
		
		System.out.println("\n\n\n");
		ClassAnalyser.dumpTotalClassInfo();
	}
}
