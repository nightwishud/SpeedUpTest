package cn.edu.pku.plde.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Student {
	public static String school = "USTC";
	public static Teacher chiarman = new Teacher();
	private int id;
	private String name;
	private int[] scores;
	public ArrayList<Integer> list;
	public static int staticint;
//	public Map<Integer, String> map;
//	public Iterator<Integer> it;

	public int[] getScores() {
		return scores;
	}	
	public void setScores(int[] scores) {
		this.scores = scores;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void test(){
//		Teacher t = new Teacher();
//		t.setId(this.id);
//		t.setId(staticint);
//		int i = t.salary;
		int i = chiarman.getId();
	}
}
