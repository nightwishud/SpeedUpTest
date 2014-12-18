package cn.edu.pku.plde.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class MethodParaInstrumenter {
	public static void paraRecordInstrument(String path){
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
	}

	private static void processZip(File f) {
		// TODO Auto-generated method stub
		
	}

	private static void processClass(File f) {
		try {
			ClassReader cr = new ClassReader(new FileInputStream(f));
			ClassWriter cw = new ClassWriter(cr, 0);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void processJar(File f) {
		// TODO Auto-generated method stub
		
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
	
}
