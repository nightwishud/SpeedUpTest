package cn.edu.pku.plde.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import cn.edu.pku.plde.callgraph.LibAnalyzer;


public class LibClassNode extends ClassNode implements Opcodes {
	
	private List<MethodNode> methodNodeList;
	private boolean isInterface;
	private boolean isJDK = false;
	private HashSet<String> priStat;
	
	public LibClassNode(List<MethodNode> mnl, HashSet<String> priStat){
		super();
		this.methodNodeList = mnl;
		isInterface = (access & ACC_INTERFACE) != 0;
		this.priStat = priStat;
	}
	
	
	
	
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		String s = name.replace('/', '.');
		if(s.startsWith("java.") || s.startsWith("javax.")){
			isJDK = true;
		}
	}

	@Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc,
            final String signature, final String[] exceptions) {
        String className = this.name.replaceAll("/", ".");
        String mtdID = className + " " + name + " " + desc;
    	for(MethodNode node : this.methodNodeList){
    		if(node.name.equals(name)){
    			break;
    		}
    	}
        MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
        if( isInterface || name.equals("<clinit>") || !isJDK || name.contains("$")){//TODO:: contains($) ?
    		return v;
    	}
        LibInfoMethodAdapter adapter = new LibInfoMethodAdapter(Opcodes.ASM4, v, mtdID);
        return adapter;
    }
	
	
	class LibInfoMethodAdapter extends MethodVisitor {
		private String mtdID;
		public LibInfoMethodAdapter(int api, MethodVisitor v, String mtdID) {
			super(api, v);
			this.mtdID = mtdID;
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			owner = owner.replaceAll("/", ".");
			if(opcode == Opcodes.GETSTATIC && !owner.contains("$") && !name.contains("$")){ // TODO:: $
//				System.out.println("GETSTATIC!	OWNER: " + owner +"    NAME: " + name + "    DESC: " + desc);
				String type = Type.getType(desc).getClassName();
				Class cls;
				try {
					cls = Class.forName(owner);
					Field f;
					if(priStat.contains(name)){
						f = cls.getDeclaredField(name);	
					}else{
						f = cls.getField(name);
					}
					if(f.isAccessible()){
						if(!Modifier.isFinal(f.getModifiers())){ // not final static field
							String statID = owner + " " + name;
							LibAnalyzer.nonFinalStatMethod.put(mtdID, statID);
						}
					}else{
						f.setAccessible(true);
						if(!Modifier.isFinal(f.getModifiers())){ // not final static field
							String statID = owner + " " + name;
							LibAnalyzer.nonFinalStatMethod.put(mtdID, statID);
						}
						f.setAccessible(false);
					}
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					System.err.println("## METHOD : " + mtdID + "\n## FIELD : " + name + " IN " + owner + " NOT FOUND ##");
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				
				
			}
			super.visitFieldInsn(opcode, owner, name, desc);
		}
		
	}
}
