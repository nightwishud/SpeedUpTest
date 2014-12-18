package cn.edu.pku.plde.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import cn.edu.pku.plde.info.ClassInfo;
import cn.edu.pku.plde.info.FieldInfo;
import cn.edu.pku.plde.info.MethodInfo;
import cn.edu.pku.plde.info.ParameterInfo;
import cn.edu.pku.plde.info.StaticInfo;
import cn.edu.pku.plde.smp.EThis;
import cn.edu.pku.plde.smp.SmpRcd;
import cn.edu.pku.plde.smp.VObj;
import cn.edu.pku.plde.smp.VPObj;
import cn.edu.pku.plde.utils.ClassAnalyser;

public class ClsMethodInfoSmpRcdVisitor extends ClassVisitor implements Opcodes{
	
	private boolean isInterface;
	private String className;
	
	public ClsMethodInfoSmpRcdVisitor(ClassVisitor cv) {
		super(ASM4, cv);
	}
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		isInterface = (access & ACC_INTERFACE) != 0;
		className = name;
	}
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
				exceptions);
		if (!isInterface && mv != null && !name.equals("<init>") &&  ! name.equals("<clinit>")	
				&& !name.equals("main") && !name.contains("$") ){//&& !name.equals("hashCode") ) { // TODO:: <clinit> or main needed ??
			mv = new InfoSmpRcdMethodAdapter(access, name, desc, mv);
		}
		return mv;
	}
	
	class InfoSmpRcdMethodAdapter extends LocalVariablesSorter{
		private boolean isStatic;
		private String ownerName;
		private String simpleName;
		private String desc;
		private MethodInfo mi;
		
		public InfoSmpRcdMethodAdapter(int access, String name, String desc,
				MethodVisitor mv) {
			super(ASM4, access, desc, mv);
			ownerName = className.replace('/', '.');
			simpleName = name;
			isStatic = (access & ACC_STATIC) != 0;
			this.desc = desc;
			mi = ClassAnalyser.findMethodInfoByName(ownerName, simpleName, desc);
		}
		
		@Override
		public void visitCode() {
			super.visitCode();
			if( mi.getParameters().size() == 0 && mi.getStatics().size() == 0 ){//&& mi.getRelationsMap().size() == 0)	{ 
				if(isStatic || mi.getRelationsMap().get(ownerName) == null){
					return;
				}
			}
			//1. get SimpleRecord of this method
			int recordSite = newLocal(Type.getType(SmpRcd.class));
			mv.visitLdcInsn(ownerName + " " + simpleName + " " + desc);
			mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/smp/SmpRcd", "getInstance", "(Ljava/lang/String;)Lcn/edu/pku/plde/smp/SmpRcd;");
			mv.visitVarInsn(ASTORE, recordSite);
			
			//2. for each para
			for(ParameterInfo parameter : mi.getParameters()) {
				String type = parameter.getType();
				int id = parameter.getId();
				int site = parameter.getSite();
				if(parameter.isTag()){//(1). process primitive param 
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitIntInsn(BIPUSH, id);
					mv.visitLdcInsn(type);	// TODO:: desc or typename
					if(type.equals("int")) {
						mv.visitVarInsn(ILOAD, site);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
					} else if(type.equals("char")) {
						mv.visitVarInsn(ILOAD, site);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
					} else if(type.equals("byte")) {
						mv.visitVarInsn(ILOAD, site);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
					}  else if(type.equals("double")) {
						mv.visitVarInsn(DLOAD, site);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
					}  else if(type.equals("long")) {
						mv.visitVarInsn(LLOAD, site);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
					}  else if(type.equals("float")) {
						mv.visitVarInsn(FLOAD, site);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
					}  else if(type.equals("short")) {
						mv.visitVarInsn(ILOAD, site);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
					}  else if(type.equals("boolean")) {
						mv.visitVarInsn(ILOAD, site);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
					} else{
						mv.visitVarInsn(ALOAD, site);
					}
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putPrimPara",
							 "(ILjava/lang/String;Ljava/lang/Object;)V");
				}else if(parameter.isSimple()){//(2). process simple param
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitIntInsn(BIPUSH, id);
					if(parameter.isArray()){
						mv.visitLdcInsn(parameter.getTypeDesc());
					}else{
						mv.visitLdcInsn(type);
					}
					mv.visitVarInsn(ALOAD, site);
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putSimplePara",
							 "(ILjava/lang/String;Ljava/lang/Object;)V");
				}else if(parameter.isArray()){//(3). process other array param
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitIntInsn(BIPUSH, id);
					mv.visitVarInsn(ALOAD, site);
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putArrPara",
							"(ILjava/lang/Object;)V");
				}else if(type.equals("java.lang.Object")){//(4). pricess generic para (java.lang.Object type)
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitIntInsn(BIPUSH, id);
					mv.visitVarInsn(ALOAD, site);
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putGenericPara",
							"(ILjava/lang/Object;)V");
				}else{//(4). process other object param
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitIntInsn(BIPUSH, id);
					mv.visitVarInsn(ALOAD, site);
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putObjPara",
							"(ILjava/lang/Object;)V");
				}
			}
			// 3. for each static field
			for(StaticInfo staticInfo : mi.getStatics()){
				String className = staticInfo.getClassName().replace( "." , "/" );
				String className2 = staticInfo.getClassName();
				String fieldName = staticInfo.getFieldName();
				String type = staticInfo.getType(); // TODO:: check StaticField.type and Parameter.type have the same meaning
				String statDesc = staticInfo.getTypeDesc();
				if(staticInfo.getClassName().equals(ownerName)){// access static of own class
					if(staticInfo.isTag()){//(1). prim					
						mv.visitVarInsn(ALOAD, recordSite);
						mv.visitLdcInsn(className2);
						mv.visitLdcInsn(fieldName);
						mv.visitLdcInsn(type);
						if(type.equals("int")) {
							mv.visitFieldInsn(GETSTATIC, className, fieldName, "I");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
						} else if(type.equals("char")) {
							mv.visitFieldInsn(GETSTATIC, className, fieldName, "C");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
						} else if(type.equals("byte")) {
							mv.visitFieldInsn(GETSTATIC, className, fieldName, "B");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
						}  else if(type.equals("double")) {
							mv.visitFieldInsn(GETSTATIC, className, fieldName, "D");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
						}  else if(type.equals( "long")) {
							mv.visitFieldInsn(GETSTATIC, className, fieldName, "J");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
						}  else if(type.equals("float")) {
							mv.visitFieldInsn(GETSTATIC, className, fieldName, "F");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
						}  else if(type.equals("short")) {
							mv.visitFieldInsn(GETSTATIC, className, fieldName, "S");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
						}  else if(type.equals("boolean")) {
							mv.visitFieldInsn(GETSTATIC, className, fieldName, "Z");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
						} else{
							mv.visitFieldInsn(GETSTATIC, className, fieldName, statDesc);
						}
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putPrimStatic",
								 "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
					}else if(staticInfo.isSimple()){//(2). simple
						mv.visitVarInsn(ALOAD, recordSite);
						mv.visitLdcInsn(className2);
						mv.visitLdcInsn(fieldName);
						mv.visitLdcInsn(type);
						mv.visitFieldInsn(GETSTATIC, className, fieldName, statDesc);
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putSimpleStatic",
								"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
					}else if(staticInfo.isArray()){//(3). array
						mv.visitVarInsn(ALOAD, recordSite);
						mv.visitLdcInsn(className2);
						mv.visitLdcInsn(fieldName);
						mv.visitFieldInsn(GETSTATIC, className, fieldName, statDesc);
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putArrStatic",
								"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
					}else{//(4). object
						mv.visitVarInsn(ALOAD, recordSite);
						mv.visitLdcInsn(className2);
						mv.visitLdcInsn(fieldName);
						mv.visitFieldInsn(GETSTATIC, className, fieldName, statDesc);
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putObjStatic",
								"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
					}
				}else{// access outer static, using reflection
					int task;
					if(staticInfo.isTag()){
						task = 0;
					}else if(staticInfo.isSimple()){
						task = 1;
					}else if(staticInfo.isArray()){
						task = 2;
					}else{
						task = 3;
					}
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitLdcInsn(className2);
					mv.visitLdcInsn(fieldName);
					mv.visitLdcInsn(type);
					mv.visitIntInsn(BIPUSH, task);
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putOuterStatic",
							"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
				}
			}
			
			//4. this point
			ClassInfo ci = mi.getRelationsMap().get(ownerName);
			if(ci != null && !isStatic && ci.getFields().size() > 0){
				
				mv.visitVarInsn(ALOAD, recordSite);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putTotalThis", "(Ljava/lang/Object;)V");
				
//				if(ci.isInnerClass()){//record outer this for inner class object
//					boolean useOuterThis = false;
//					FieldInfo f = null;
//					for(FieldInfo fi : ci.getFields()){
//						if(fi.getName().equals("this$0")){
//							useOuterThis = true;
//							f = fi;
//							break;
//						}
//					}
//					if(useOuterThis && ci.getFields().size() == 1){//means only use outer this
//						mv.visitVarInsn(ALOAD, recordSite);
//						mv.visitVarInsn(ALOAD, 0);
//						mv.visitFieldInsn(GETFIELD, className, "this$0", f.getDesc());//Type.getDescriptor(Class.forName(className.split("$")[0]))
//						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putTotalThis", "(Ljava/lang/Object;)V");
//						
//					}else{
//						mv.visitVarInsn(ALOAD, recordSite);
//						mv.visitVarInsn(ALOAD, 0);
//						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putTotalThis", "(Ljava/lang/Object;)V");
//					}
//				}else{//record normal total this
//					mv.visitVarInsn(ALOAD, recordSite);
//					mv.visitVarInsn(ALOAD, 0);
//					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putTotalThis", "(Ljava/lang/Object;)V");
//				}
				
				
				
				
	/*
				//new VPObj				
				mv.visitTypeInsn(NEW, "cn/edu/pku/plde/smp/VPObj");
				mv.visitInsn(DUP);
				mv.visitLdcInsn(ownerName);
				mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/smp/VPObj", "<init>", "(Ljava/lang/String;)V");
				int vpobjSite = newLocal(Type.getType(VPObj.class));
				mv.visitVarInsn(ASTORE, vpobjSite);
				for(FieldInfo fi: ci.getFields()){
					if(fi.getName().equals("this$0")){//TODO::
						continue;
					}
					String type = fi.getType();
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitLdcInsn(fi.getName());
					mv.visitLdcInsn(fi.getType());
					mv.visitVarInsn(ALOAD, 0);
					if(fi.isTag()){						
						if(type.equals("int")){
							mv.visitFieldInsn(GETFIELD, className, fi.getName(), "I");//TODO check className is spilt by '.' or '/'
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
						}else if(type.equals("char")){
							mv.visitFieldInsn(GETFIELD, className, fi.getName(), "C");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
						}else if(type.equals("byte")){
							mv.visitFieldInsn(GETFIELD, className, fi.getName(), "B");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
						}else if(type.equals("double")){
							mv.visitFieldInsn(GETFIELD, className, fi.getName(), "D");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
						}else if(type.equals( "long")){
							mv.visitFieldInsn(GETFIELD, className, fi.getName(), "J");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
						}else if(type.equals("float")){
							mv.visitFieldInsn(GETFIELD, className, fi.getName(), "F");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
						}else if(type.equals("short")){
							mv.visitFieldInsn(GETFIELD, className, fi.getName(), "S");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
						}else if(type.equals("boolean")){
							mv.visitFieldInsn(GETFIELD, className, fi.getName(), "Z");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
						} 
						mv.visitVarInsn(ALOAD, vpobjSite);
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putPrimFld", 
								"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Lcn/edu/pku/plde/smp/VPObj;)V");
					}else if(fi.isSimple()){
						mv.visitFieldInsn(GETFIELD, className, fi.getName(), fi.getDesc());
						mv.visitVarInsn(ALOAD, vpobjSite);
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putSimpleFld",
								"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Lcn/edu/pku/plde/smp/VPObj;)V");
					}else if(fi.isArray()){
						mv.visitFieldInsn(GETFIELD, className, fi.getName(), fi.getDesc());
						mv.visitVarInsn(ALOAD, vpobjSite);
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putArrFld", 
								"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Lcn/edu/pku/plde/smp/VPObj;)V");
					}else if(type.equals("java.lang.Object")){
						mv.visitFieldInsn(GETFIELD, className, fi.getName(), fi.getDesc());
						mv.visitVarInsn(ALOAD, vpobjSite);
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putGenericFld", 
								"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Lcn/edu/pku/plde/smp/VPObj;)V");
					}else{
						mv.visitFieldInsn(GETFIELD, className, fi.getName(), fi.getDesc());
						mv.visitVarInsn(ALOAD, vpobjSite);
						mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd", "putObjFld", 
								"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Lcn/edu/pku/plde/smp/VPObj;)V");
					}
				}
				mv.visitVarInsn(ALOAD, recordSite);
				mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/smp/SmpRcd", "etrs", "Ljava/util/List;");
				mv.visitTypeInsn(NEW, "cn/edu/pku/plde/smp/EThis");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, vpobjSite);
				mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/smp/EThis", "<init>", "(Lcn/edu/pku/plde/smp/V;)V");
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
				mv.visitInsn(POP);	
				*/
			}		
			
			//5. write to xml
			mv.visitVarInsn(ALOAD, recordSite);
//			mv.visitLdcInsn("/home/nightwish/workspace/test_program/output/" +ownerName +"_" +  simpleName + ".xml");	// TODO:: change to your dir
			mv.visitLdcInsn("D:/test/" +ownerName +"_" +  simpleName + ".xml");
			mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/smp/SmpRcd",	"write", "(Ljava/lang/String;)V");
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			super.visitMaxs(maxStack, maxLocals);
		}
	}
	
}
