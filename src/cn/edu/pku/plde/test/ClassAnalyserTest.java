package cn.edu.pku.plde.test;

import cn.edu.pku.plde.utils.ClassAnalyser;

public class ClassAnalyserTest {
	public double dsum(double a, double b, double c, double d){
		a = a + b;
		return a + b + c + d;
	}
	public static double dsum2(double a, double b, double c, double d){
		return a + b + c + d;
	}
	public static double dsum3(long a, int b, double c, float d){
		c = a +b +c +d;
		return a+b+c+d;
	}
	public double dsum4(String[] s0, long a, int b, double c, float d, String s1){
//		return s0.length + a+b+c+d + s1.length();
		return a + b;
	}
	public void f(){
		int a = 5;
	}
	public static void main(String[] args){
		String path = "bin/cn/edu/pku/plde/test/ClassAnalyserTest.class";
//		ClassAnalyser.analysisClassFromDir(path);
	}
}
