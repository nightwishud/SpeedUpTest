package cn.edu.pku.plde.smp;

public class VNil extends V {
	public VNil(){}
	public static VNil getInstance(){
		if(v == null){
			v = new VNil();
		}
		return v;
	}
	
	private static VNil v = null;
}
