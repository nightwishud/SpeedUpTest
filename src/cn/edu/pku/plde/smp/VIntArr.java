package cn.edu.pku.plde.smp;

import org.simpleframework.xml.ElementArray;

public class VIntArr extends VArr{
	public VIntArr(){}
	public VIntArr(int[] v){
		this.v = v;
	}
	@ElementArray
	public int[] v;
}
