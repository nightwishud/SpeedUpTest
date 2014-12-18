package cn.edu.pku.plde.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import cn.edu.pku.plde.info.ClassInfo;
import cn.edu.pku.plde.info.FieldInfo;
import cn.edu.pku.plde.info.MethodInfo;
import cn.edu.pku.plde.info.ParameterInfo;
import cn.edu.pku.plde.info.StaticInfo;

public class AnalyserClassNode extends ClassNode {

	private List<MethodNode> methodNodeList;
	private List<MethodInfo> mInfoList = new ArrayList<MethodInfo>();
	
	public AnalyserClassNode(List<MethodNode> mnl){
		super();
		this.methodNodeList = mnl;
	}
	
	
	
//	@Override
//	public void visit(int version, int access, String name, String signature,
//			String superName, String[] interfaces) {
//		System.out.println("VISIT: " + name);
//		super.visit(version, access, name, signature, superName, interfaces);
//	}



//	@Override
//	public FieldVisitor visitField(int access, String name, String desc,
//			String signature, Object value) {
//		System.out.println("VISIT-FIELD: " + name + " " + desc);
//		return super.visitField(access, name, desc, signature, value);
//	}

	@Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc,
            final String signature, final String[] exceptions) {
//    	if(name.equals("<init>") || name.equals("<clinit>")){
//            return super.visitMethod(access, name, desc, signature,
//                    exceptions);
//        }
    	String className = this.name.replaceAll("/", ".");
    	String mInfoID = className + " " + name + " " + desc;
    	MethodInfo mInfo = new MethodInfo(mInfoID);
    	MethodNode mn = null;
    	for(MethodNode node : this.methodNodeList){
    		if(node.name.equals(name) && node.desc.equals(desc)){
    			mn = node;
    			break;
    		}
    	}
        MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
        MInfoMethodAdapter adapter = new MInfoMethodAdapter(Opcodes.ASM4, v, mn, mInfo);  
        mInfoList.add(mInfo);
        return adapter;
    }

	public List<MethodInfo> getmInfoList() {
		return mInfoList;
	}

	public void setmInfoList(List<MethodInfo> mInfoList) {
		this.mInfoList = mInfoList;
	}
	
	
	class MInfoMethodAdapter extends MethodVisitor {

//		private Method m;
		private MethodNode mn;
		private boolean isStatic;
		private int index;
		private int[] paraSites;
		private MethodInfo mInfo;

		
		public MInfoMethodAdapter(int api, MethodVisitor mv, MethodNode mn, MethodInfo info) {
			super(api, mv);
			this.mn = mn;
			this.mInfo = info;
			this.isStatic = (mn.access & Opcodes.ACC_STATIC) !=0;	// modified bug : " ==1 " -> " != 0 "
			this.mInfo.setStatic(isStatic);
			int len = Type.getArgumentTypes(mn.desc).length;
			this.mInfo.setParaNum(len);
			paraSites = new int[len];
			getIndexAndParaSites();
//			System.out.println( info.getMethodName() + "  STATIC : " + isStatic + "   INDEX : " + index);
//			for(int i: paraSites){
//				System.out.println("SITE: " + i);
//			}
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			super.visitVarInsn(opcode, var);
			if (isLocalValLoad(opcode)) { // loading local variable
				if (var <= index) {
					int order = getOrder(var);
					if (order < 0) {// "this pointer"
						return;
					}
					if ( !paraCollected(order) ) {
						ParameterInfo paraInfo = new ParameterInfo();
//						 System.out.println("VAR : " + var + " OREDER : " + order);
						String desc = Type.getArgumentTypes(mn.desc)[order].getDescriptor();
						String type = Type.getArgumentTypes(mn.desc)[order].getClassName();
						paraInfo.setId(order);
						paraInfo.setSite(var);
						paraInfo.setType(type);
						paraInfo.setTypeDesc(desc);
						boolean tag = isPrimitiveType(type);
						paraInfo.setTag(tag);
//						 System.out.println( "the Para: " + paraInfo.getId() + "	DESC:" + desc + "	ParaType: " + type + "	IsPimitive: " + tag);
						mInfo.getParameters().add(paraInfo);
					}
				}
			}
		}

		private int getOrder(int var) {
			for(int j = 0; j < paraSites.length;j++){
				if(paraSites[j] == var){
					return j;
				}
			}
			return -1;
		}

		/*
		 * for static fields access, and fields within the class
		 * */
		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			owner = owner.replaceAll("/", ".");
			
			if(opcode == Opcodes.GETSTATIC && ! staticCollected(owner, name)){//for static fields
//				System.out.println("GETSTATIC!	OWNER: " + owner +"    NAME: " + name + "    DESC: " + desc);
				StaticInfo staticInfo = new StaticInfo();
				staticInfo.setClassName(owner);
				staticInfo.setFieldName(name);
				String type = Type.getType(desc).getClassName();
				staticInfo.setType(type);
				staticInfo.setTypeDesc(desc);
				staticInfo.setTag(isPrimitiveType(type));
//				System.out.println("STATIC FIELD : " + staticInfo.getType() + " " + staticInfo.isTag());
				mInfo.getStatics().add(staticInfo);
			}
			
			if(opcode == Opcodes.GETFIELD){//for normal fields
//				System.out.println("GETFIELD!	OWNER: " + owner +"    NAME: " + name + "    DESC: " + desc);
				ClassInfo classInfo = mInfo.getRelationsMap().get(owner);
				FieldInfo fieldInfo = null;
				if(classInfo == null){
					classInfo = new ClassInfo(owner);
					fieldInfo = new FieldInfo(owner, name);
					String type = Type.getType(desc).getClassName();
					fieldInfo.setType(type);
					fieldInfo.setTag(isPrimitiveType(type));
					fieldInfo.setDesc(desc);
					classInfo.getFields().add(fieldInfo);
					mInfo.getRelationsMap().put(owner, classInfo);
				}else{// the class has been already in the relation map
					int i;
					for(i = 0; i < classInfo.getFields().size(); i++){// find the field in the list
						fieldInfo = classInfo.getFields().get(i);
						if(fieldInfo.getName().equals(name)){
							break;
						}
					}
					if(i >= classInfo.getFields().size()){// the field has not been accessed
						fieldInfo = new FieldInfo(owner, name);
						String type = Type.getType(desc).getClassName();
						fieldInfo.setType(type);
						fieldInfo.setTag(isPrimitiveType(type));
						fieldInfo.setDesc(desc);
						classInfo.getFields().add(fieldInfo);
					}
				}
//				System.out.println("NORMAL FIELD : " + fieldInfo.getType() + " " + fieldInfo.isTag());
				
			}
			super.visitFieldInsn(opcode, owner, name, desc);
		}

		
		private boolean paraCollected(int id) {
			for(ParameterInfo pi: mInfo.getParameters()){
				if(pi.getId() == id){
					return true;
				}
			}
			return false;
		}
		
		private boolean staticCollected(String owner, String name) {
			for(StaticInfo si: mInfo.getStatics()){
				if(si.getClassName().equals(owner) && si.getFieldName().equals(name)){
					return true;
				}
			}
			return false;
		}

		private boolean isLocalValLoad(int opcode) {
			return opcode== Opcodes.ILOAD || opcode== Opcodes.ALOAD || opcode == Opcodes.LLOAD || opcode == Opcodes.DLOAD
					|| opcode == Opcodes.FLOAD;
		}

		private boolean isLocalValStore(int opcode) {
			return opcode== Opcodes.ISTORE || opcode== Opcodes.ASTORE || opcode == Opcodes.LSTORE || opcode == Opcodes.DSTORE
					|| opcode == Opcodes.FSTORE;
		}

		private boolean isPrimitiveType(String type){
			return type.equals("int") || type.equals("boolean") || type.equals("long") || type.equals("short") 
					|| type.equals("byte") ||type.equals("double") || type.equals("float") || type.equals("char") 
					|| type.equals("void") // TODO:: need void ?
					|| type.equals("java.lang.Integer") || type.equals("java.lang.Character")
					|| type.equals("java.lang.Byte") || type.equals("java.lang.Double") || type.equals("java.lang.Float")
					|| type.equals("java.lang.Long") || type.equals("java.lang.Short") || type.equals("java.lang.Boolean"); 	
		} 
		private void getIndexAndParaSites(){
			int i = 1;
			if(this.isStatic){
				i = 0;
			}
			Type[] types = Type.getArgumentTypes(mn.desc);
			for(int j = 0; j <types.length; j++){
				paraSites[j] = i;
				if(types[j].getClassName().equals("double") || types[j].getClassName().equals("long")){
					i++;
				}
				i++;
			}
			i--;
			index = i;
		}
	}
}
