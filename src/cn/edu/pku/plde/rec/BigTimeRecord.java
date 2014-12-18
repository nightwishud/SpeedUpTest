package cn.edu.pku.plde.rec;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.simpleframework.xml.Attribute;

public class BigTimeRecord {
	public static Map<String, BigTimeRecord> timeRecords = new HashMap<String, BigTimeRecord>();

	@Attribute
	public String key;
	@Attribute
	public long runtimes = 0L;
	@Attribute
	public BigInteger useTime = BigInteger.ZERO;

	public static BigInteger totalRuntimes = BigInteger.ZERO;
	public static BigInteger totalUseTime = BigInteger.ZERO;

	public BigTimeRecord(String key) {
		this.key = key;
	}

	public static BigTimeRecord getInstance(String key) {
		BigTimeRecord r = timeRecords.get(key);
		if (r == null) {
			r = new BigTimeRecord(key);
			timeRecords.put(key, r);
		}
		r.runtimes++;
		return r;
	}

	public static void writeAll() throws FileNotFoundException {
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		String path = "/home/nightwish/workspace/test_program/result/time.txt";
		PrintStream out = new PrintStream(new FileOutputStream(path, true));
		System.setOut(out);
		System.out.println("\n\n########### " + stackElements[1] + " ##############\n");
		Iterator it = timeRecords.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String name = (String) entry.getKey();
			BigTimeRecord btr = (BigTimeRecord) entry.getValue();
//			String[] names = name.split(" ");
//			System.out.println(names[0] + "_" + names[1] + "  \tRUNTIMES:" + btr.runtimes + " \tTIME:" + btr.useTime + " \tAVG:"
//						+ btr.useTime.divide(BigInteger.valueOf(btr.runtimes)) );
			totalRuntimes = totalRuntimes.add(BigInteger.valueOf(btr.runtimes));
			totalUseTime = totalUseTime.add(btr.useTime);
		}
		it = timeRecords.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String name = (String) entry.getKey();
			BigTimeRecord btr = (BigTimeRecord) entry.getValue();
			String[] names = name.split(" ");
			BigDecimal proportion = (new BigDecimal(btr.useTime)).divide(new BigDecimal(totalUseTime), 4, BigDecimal.ROUND_HALF_EVEN);
//			double proportion = btr.useTime.doubleValue()/totalRuntimes.doubleValue();
//			DecimalFormat df = new DecimalFormat("#.##");
			System.out.println(names[0] + "_" + names[1] + "  \tRUNTIMES:" + btr.runtimes + " \tTIME:" + btr.useTime + " \tAVG:"
						+ btr.useTime.divide(BigInteger.valueOf(btr.runtimes)) + "\tPER:" + proportion);
		}
		System.out.println("\nTOTAL RUNTIME: " + totalRuntimes);
		System.out.println("TOTAL TIME: " + totalUseTime);
		System.out.println("TOTAL AVG: " + totalUseTime.divide(totalRuntimes));
		System.out.println("\n################################################################################################\n");
	}
}
