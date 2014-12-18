package cn.edu.pku.plde.rec;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class TimeRecord {
	public static Map<String, TimeRecord> timeRecords = new HashMap<String, TimeRecord>();
	
	@Attribute
	public String key;
	@Attribute
	public int runtimes = 0;
	@Attribute
	public long useTime = 0L;
	
	public static int totalRuntime = 0;
	public static long totalUseTime = 0L;
	
	public TimeRecord(String key) {
		this.key = key;
	}


	public static TimeRecord getInstance(String key){
		TimeRecord r = timeRecords.get(key);
		if(r == null){
			r = new TimeRecord(key);
			timeRecords.put(key, r);
		}
		r.runtimes++;
		return r;
	}
	
	public static void writeAll(){
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		System.out.println("\n\n########### "+stackElements[1] + " ##############\n");
		Iterator it = timeRecords.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			String name = (String) entry.getKey();
			TimeRecord tr = (TimeRecord) entry.getValue();
			String[] names = name.split(" ");
			if (tr.useTime <= Long.MAX_VALUE) {
				System.out.println(names[0] + "_" + names[1] + "  \tRUNTIMES:"
						+ tr.runtimes + " \tTIME:" + tr.useTime + " \tAVG:" + (tr.useTime / tr.runtimes));
			}else{
				System.out.println(names[0] + "_" + names[1] + "  \tRUNTIMES: "
						+ tr.runtimes + " \tTIME: " + tr.useTime + " \tAVG: "
						+ (tr.useTime / tr.runtimes));
			}
			totalRuntime += tr.runtimes;
			totalUseTime += tr.useTime;
		}
		System.out.println("\nTOTAL RUNTIME: " + totalRuntime);
		System.out.println("TOTAL TIME: " + totalUseTime);
		System.out.println("TOTAL AVG: " + (totalUseTime/totalRuntime));
		System.out.println("\n################################################################################################\n");
	}
}
