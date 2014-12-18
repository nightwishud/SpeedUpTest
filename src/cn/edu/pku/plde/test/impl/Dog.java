package cn.edu.pku.plde.test.impl;

public class Dog implements Animal, Pat{
	static int a;
	@Override
	public void eat() {
		a++;
//		System.out.println("DOG EATING");
	}

	@Override
	public void play() {
//		System.out.println("DOG PALYING");
	}
	
}
