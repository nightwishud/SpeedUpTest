package cn.edu.pku.plde.smp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;


public class XML2SmpRcd {
	
	public static List<SmpRcd> deSerializedRecords = new LinkedList<SmpRcd>();	// TODO:: LinkedList or ArrayList
	public static Map<SmpRcd, Integer> runtimesMap = new HashMap<SmpRcd, Integer>();
	
	public static void findDuplication(){
		for(int i = 0; i< deSerializedRecords.size(); i++){
			SmpRcd r1 = deSerializedRecords.get(i);
			runtimesMap.put(r1, 1);
			for(int j = i+1;j<deSerializedRecords.size();j++){
				SmpRcd r2 = deSerializedRecords.get(j);
				if(r1.equals(r2)){
					deSerializedRecords.remove(j);
					int t = runtimesMap.get(r1) + 1;
					runtimesMap.put(r1, t);
					j--;
				}
			}
		}
	}
	
	
	private static SmpRcd restore(String s){
		SmpRcd r = null;
		Serializer serializer = new Persister();   
		try {
			r = serializer.read(SmpRcd.class, s);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}
	
	public static void restoreXML(File f){
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			StringBuffer str = new StringBuffer();
			while((s = br.readLine()) != null){
				str.append(s);
				if(s.equals("</smpRcd>")){
					SmpRcd r = restore(str.toString());
					if(r.rt > 1000){ // only for 25000 items
						break;
					}
					deSerializedRecords.add(r);
					str.delete(0, str.length());
					continue;
				}			
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
		String path = "D:\\test\\org.apache.commons.math3.analysis.differentiation.DerivativeStructure_hypot.xml";
		File f = new File(path);
		restoreXML(f);
		System.out.println("TOTAL RECORD NUM : " + deSerializedRecords.size());
		findDuplication();
		Iterator it = runtimesMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			System.out.println(entry.getValue());
		}
	}
}
