package cn.edu.pku.plde.rec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class XML2Record {
	
//	public static List<Record> deSerializedRecords = new ArrayList<Record>();
	public static List<Record> deSerializedRecords = new LinkedList<Record>();	// TODO:: LinkedList or ArrayList
	public static Map<Record, Integer> runtimesMap = new HashMap<Record, Integer>();
	public static long analyzedRecNum;
	
	private static Record restore(String s){
		Record r = null;
		Serializer serializer = new Persister();   
		try {
			r = serializer.read(Record.class, s);	
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
				if(s.equals("</record>")){
					Record r = restore(str.toString());
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
	
	public static void dynamicRestoreXML(File f){
		if(f.length() <= 629145600L){	// file size is under 600M
			restoreXML(f);
		}else{
			try {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String s = null;
				StringBuffer str = new StringBuffer();
				while((s = br.readLine()) != null){
					analyzedRecNum++;
					str.append(s);
					if(s.equals("</record>")){
						Record r = restore(str.toString());
						if(deSerializedRecords.size() < 100000){// 2^20 1048576
							deSerializedRecords.add(r);
						}else{
							
						}
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
	}
	
	public static void findDuplication(){
		for(int i = 0; i< deSerializedRecords.size(); i++){
			Record r1 = deSerializedRecords.get(i);
			runtimesMap.put(r1, 1);
			for(int j = i+1;j<deSerializedRecords.size();j++){
				Record r2 = deSerializedRecords.get(j);
				if(r1.equals(r2)){
					deSerializedRecords.remove(j);
					int t = runtimesMap.get(r1) + 1;
					runtimesMap.put(r1, t);
					j--;
				}
			}
		}
	}
	
	public static void main(String[] args){
//		File f = new File("/home/nightwish/workspace/test_program/output/org.jgrapht.alg.AbstractPathElementList_get.xml");
//		File f = new File("/home/nightwish/workspace/test_program/output/org.jgrapht.alg.KShortestPathsIterator_tryToAddNewPaths.xml");	
		String path = "/home/nightwish/workspace/test_program/output/" + "org.jgrapht.alg.RankingPathElementList_addPathElements.xml";
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
