package cn.edu.pku.plde.test.rec;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class ArrayUnenforcedSet<E> extends ArrayList<E> implements Set<E>{
	
	
	class SetForEquality extends AbstractSet<E>{

		@Override
		public Iterator<E> iterator() {
			return ArrayUnenforcedSet.this.iterator();
		}

		@Override
		public int size() {
			 return ArrayUnenforcedSet.this.size();
		}
		
	}
}
