package cn.edu.pku.plde.test;

import java.math.BigInteger;

import cn.edu.pku.plde.rec.BigTimeRecord;
import cn.edu.pku.plde.rec.TimeRecord;

public class Timer {
	public void test(){
		TimeRecord r = TimeRecord.getInstance("test");
		r.useTime -= System.nanoTime();
		
		
		int a = 1;
		while(a<500){
			a++;
		}
		
		r.useTime += System.nanoTime();
	}
	
	public void t(){
		BigTimeRecord r = BigTimeRecord.getInstance("t");
		long t1 = System.nanoTime();

		int a = 1;
		
		long t2 = System.nanoTime();
		r.useTime = r.useTime.add(BigInteger.valueOf(t2 - t1));
//		System.out.println(r.useTime);
	}
	
	public static void main(String[] args){
		Timer t = new Timer();
		t.t();
		System.out.println(BigTimeRecord.getInstance("t").useTime);
		t.test();System.out.println(TimeRecord.getInstance("test").useTime);
	}
}
