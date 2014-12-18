package cn.edu.pku.plde.test;

public class Teacher {
	public static String school = "PKU";
	public int salary;
	private int id;
	private String name;
	private Student monitor;
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
	public Student getMonitor() {
		return monitor;
	}
	public void setMonitor(Student monitor) {
		this.monitor = monitor;
	}
	
}
