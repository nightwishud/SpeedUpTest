package cn.edu.pku.plde.inher;

import java.util.HashSet;
import java.util.Set;


public class InheritTreeNode {
	private Class node;	
	private Set<Class> children;	//it's children
	
	public InheritTreeNode(Class c){
		this.node = c;
		children = new HashSet<Class>();
	}
	
	public Class getNode() {
		return node;
	}
	public void setNode(Class node) {
		this.node = node;
	}
	public Set<Class> getChildren() {
		return children;
	}
	public void setChildren(Set<Class> children) {
		this.children = children;
	}
}
