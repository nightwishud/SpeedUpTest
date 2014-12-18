package cn.edu.pku.plde.test;

import cn.edu.pku.plde.rec.Record;
import cn.edu.pku.plde.rec.etr.Parameter;
import cn.edu.pku.plde.rec.val.ArrayValue;
import cn.edu.pku.plde.rec.val.NullValue;
import cn.edu.pku.plde.rec.val.ObjectValue;
import cn.edu.pku.plde.smp.EPara;
import cn.edu.pku.plde.smp.SmpRcd;
import cn.edu.pku.plde.smp.VIntArr;
import cn.edu.pku.plde.smp.VNull;

public class Sort {

	// 划分数组
	public static int partion(int[] array, int p, int r) {
//		SmpRcd rcd = SmpRcd.getInstance("partion");
//		rcd.putSimplePara(0, "[I", array);
//		rcd.putPrimPara(1, "int", p);
//		rcd.putPrimPara(2, "int", r);
//		rcd.write("D:/test/partion.xml");
		
/*		Record rcd = Record.getInstance("a");
		ObjectValue ov;
		if(array == null){
			ov = new NullValue();
		}
		else{
			ov = new ArrayValue("A[] array TYPE");
			Record.analzArray(array, (ArrayValue) ov);
		}
		rcd.entries.add(new Parameter(0, ov));
		rcd.write("D:/test/sort2.xml");
		
		rcd.putPrimPara(1, "int", p);
		rcd.putPrimPara(2, "int", r);
		*/
		int x = array[r];
		int i = p - 1;// 注意这点，把i设成负值，然后作为移动的标志
		int j;
		for (j = p; j < r; j++) {
			if (array[j] <= x) {
				i++;
				int temp = array[j];
				array[j] = array[i];
				array[i] = temp;
			}
		}
		int temp = array[j];
		array[j] = array[i + 1];
		array[i + 1] = temp;
		return i + 1;// 返回的应该是交换后的哨兵的位置
	}

	// 递归解决每个划分后的小数组
	public static void quickSort(int[] array, int p, int r) {
		if (p < r) {
			int q = partion(array, p, r);
			quickSort(array, p, q - 1);
			quickSort(array, q + 1, r);
		}
	}

	public static void main(String[] args) {
		int[] array = { 4, 16,3, 1, 5, 14, 6, 17, 2, 7, 9, 8, 18, 10, 15, 13, 12, 11 };
		Sort.quickSort(array, 0, array.length - 1);
		for (int i : array) {
			System.out.print(i + " ");
		} 
	}
}
