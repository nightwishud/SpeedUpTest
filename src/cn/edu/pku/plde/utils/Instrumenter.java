package cn.edu.pku.plde.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import cn.edu.pku.plde.asm.BigTimeVisitor;
import cn.edu.pku.plde.asm.ClsMethodInfoRecordVisitor;
import cn.edu.pku.plde.asm.ClsMethodInfoSmpRcdVisitor;
import cn.edu.pku.plde.asm.TestTimeOutputVisitor;
import cn.edu.pku.plde.asm.TimeVisitor;
import cn.edu.pku.plde.callgraph.CallGraphBuilder;
import cn.edu.pku.plde.test.rec.GenericTest;
import cn.edu.pku.plde.test.rec.SmpThisTest;

public class Instrumenter {
	public static void instrument(String path, Task t){
		File f = new File(path);
		if (!f.exists()) {
			System.err.println("Unable to read path " + path);
			System.exit(-1);
		}
		if (f.isDirectory())
			processDirectory(f, t);
		else if (path.endsWith(".jar"))
			processJar(f);
		else if (path.endsWith(".class"))
			processClass(f, t);
		else if (path.endsWith(".zip")) {
			processZip(f);
		} else {
			System.err.println("Unknown type for path " + path);
			System.exit(-1);
		}
	}

	private static void processZip(File f) {
		// TODO Auto-generated method stub
	}

	private static void processClass(File f, Task t) {
		String name = f.getPath();
		try {
			InputStream is = new FileInputStream(f);
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
			if(t.equals(Task.RECORDER)){	// TODO:: instead "equals" with "==" 
				ClsMethodInfoRecordVisitor cmrv = new ClsMethodInfoRecordVisitor(cw);
				cr.accept(cmrv, ClassReader.EXPAND_FRAMES); // TODO :: 0 or ClassReader.EXPAND_FRAMES ?
			}else if(t.equals(Task.SMPRCD)){
				ClsMethodInfoSmpRcdVisitor cmsrv = new ClsMethodInfoSmpRcdVisitor(cw);
				cr.accept(cmsrv, ClassReader.EXPAND_FRAMES);
			}else if(t.equals(Task.TIMER)){
				TimeVisitor tv = new TimeVisitor(cw);
				cr.accept(tv, ClassReader.EXPAND_FRAMES);
			}else if(t.equals(Task.BIGTIMER)){
				BigTimeVisitor btv = new BigTimeVisitor(cw);
				cr.accept(btv, ClassReader.EXPAND_FRAMES);
			}else if(t.equals(Task.OUTPUTTIME) || t.equals(Task.OUTPUTBIGTIME)){
				TestTimeOutputVisitor ttv = new TestTimeOutputVisitor(cw, t);
				cr.accept(ttv, 0);
			}
			byte[] b2 = cw.toByteArray();
		    if(f.exists()){
		    	f.delete();
		    }
		    FileOutputStream output = null;
		    output = new FileOutputStream(f);
		    output.write(b2,0,b2.length); 
		    is.close();
		    output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void processJar(File f) {
		
	}

	private static void processDirectory(File f, Task t) {
		for (File fi : f.listFiles()) {
//			System.out.println("FILE NAME: " + fi.getName());
			if (fi.isDirectory()){
				processDirectory(fi, t);
			}else if (fi.getName().endsWith(".class")){
				processClass(fi, t);
			}else if(fi.getName().endsWith(".jar")){
				// TODO add jar and zip
				processJar(fi);
			}else if(fi.getName().endsWith(".zip")){
				processZip(fi);
			}
		}
	}
	
	public static void main(String[] args){
		String path = "/home/nightwish/workspace/SpeedUpTest/bin/cn/edu/pku/plde/rec/RecTest.class";
		String path0 = "/home/nightwish/workspace/test_program/jgrapht-0.8.3/build";
		String path1 = "/home/nightwish/workspace/test_program/jgrapht-0.8.3/testbuild";
		String path2 = "/home/nightwish/workspace/test_program/jgrapht-0.8.3/testbuild/org/jgrapht/alg/KShortestPathKValuesTest.class";
		String path3 = "/home/nightwish/workspace/SpeedUpTest/bin/cn/edu/pku/plde/test/RefSelfTest.class";
		String path4 = "/home/nightwish/workspace/test_program/apache-river-2.2.2/test/classes";
		String path5 = "D:/Code/SVN/SpeedUpTest/bin/cn/edu/pku/plde/test/Sort.class";
		String path6 = "D:/Code/SVN/SpeedUpTest/bin/cn/edu/pku/plde/test/rec/Test.class";
		String path7 = "D:/MyProgram/Test_Program/jgrapht-0.8.3/build";
		String path8 = "D:/MyProgram/Test_Program/jscience-4.3/bin";
		
		String path9 = "D:\\MyProgram\\Test_Program\\commons-math3-3.3\\target\\classes";
		String path10 = "D:\\MyProgram\\Test_Program\\commons-math3-3.3\\target\\test-classes";

//		ClassAnalyser.analysisClassFromDir("D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build");
//		CallGraphBuilder.completeStaticInfoWithCallGraph("D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build;D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\testbuild",
//				"D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build;D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\testbuild",
//				"org.jgrapht.alg.BellmanFordShortestPathTest");
//		System.out.println("\n\n\n");
//		ClassAnalyser.dumpTotalClassInfo();
//		instrument("D:\\MyProgram\\Test_Program\\jgrapht-0.8.3\\build", Task.SMPRCD);
		
		ClassAnalyser.analysisClassFromDir(path9);
		CallGraphBuilder.completeStaticInfoWithCallGraph((path9 + ";" + path10), (path9 + ";" + path10),
				"org.apache.commons.math3.analysis.FunctionUtilsTest");
		System.out.println("\n\n\n");
		ClassAnalyser.dumpTotalClassInfo();
		instrument(path9, Task.SMPRCD);
		
//		ClassAnalyser.analysisClassFromDir(path6);
//
//		instrument(path6, Task.SMPRCD);
		
	}
}

