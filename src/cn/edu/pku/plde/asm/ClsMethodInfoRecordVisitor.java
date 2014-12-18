package cn.edu.pku.plde.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import cn.edu.pku.plde.info.MethodInfo;
import cn.edu.pku.plde.info.ParameterInfo;
import cn.edu.pku.plde.info.StaticInfo;
import cn.edu.pku.plde.rec.Record;
import cn.edu.pku.plde.rec.val.ObjectValue;
import cn.edu.pku.plde.utils.ClassAnalyser;

public class ClsMethodInfoRecordVisitor extends ClassVisitor implements Opcodes {

	private boolean isInterface;
	private String className;

	public ClsMethodInfoRecordVisitor(ClassVisitor cv) {
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
				&& !name.equals("main") && !name.contains("$") ) { // TODO:: <clinit> or main needed ??
			mv = new InfoRecordMethodAdapter(access, name, desc, mv);
		}
		return mv;
	}

	class InfoRecordMethodAdapter extends LocalVariablesSorter {

		private boolean isStatic;
		private String ownerName;
		private String simpleName;
		private String desc;
		private MethodInfo mi;
		
		public InfoRecordMethodAdapter(int access, String name, String desc,
				MethodVisitor mv) {
			super(ASM4, access, desc, mv);
			// methodName = className+"."+name+desc;
			ownerName = className.replace('/', '.');
			simpleName = name;
			isStatic = (access & ACC_STATIC) != 0;
			this.desc = desc;
			mi = ClassAnalyser.findMethodInfoByName(ownerName, simpleName, desc);
		}

		@Override
		public void visitCode() {
			super.visitCode();
			if( mi.getParameters().size() == 0 && mi.getStatics().size() == 0)	{ // TODO:: add this pointer
				return;
			}
/*			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitLdcInsn("----------------------------------------------");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");*/

			int recordSite = newLocal(Type.getType(Record.class));
			int ovSite = newLocal(Type.getType(ObjectValue.class));
			//1. get Record of this method
			mv.visitLdcInsn(ownerName + " " + simpleName + " " + desc);
			mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/Record", "getInstance", 
					"(Ljava/lang/String;)Lcn/edu/pku/plde/rec/Record;");
			mv.visitVarInsn(ASTORE, recordSite);
			//2. for each parameter
			for(ParameterInfo parameter : mi.getParameters()) {
				String type = parameter.getType();
				int id = parameter.getId();
				int site = parameter.getSite();
				//(1). process primitive param 
				if(parameter.isTag()){
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitIntInsn(BIPUSH, id);	// TODO :: opt
					mv.visitLdcInsn(type);
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
					} 
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/rec/Record", "putPrimPara", "(ILjava/lang/String;Ljava/lang/Object;)V");
				}else if(parameter.isArray()){// for array object TODO :: check isArray() is OK
					mv.visitVarInsn(ALOAD, site);
					Label l0 = new Label();
					mv.visitJumpInsn(IFNONNULL, l0);
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/NullValue","getInstance", "()Lcn/edu/pku/plde/rec/val/NullValue;");
					mv.visitVarInsn(ASTORE, ovSite);
					Label l1 = new Label();
					mv.visitJumpInsn(GOTO, l1);
					mv.visitLabel(l0);
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/val/ArrayValue");
					mv.visitInsn(DUP);
					mv.visitLdcInsn(parameter.getType());
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/rec/val/ArrayValue",	"<init>", "(Ljava/lang/String;)V");
					mv.visitVarInsn(ASTORE, ovSite);
					mv.visitVarInsn(ALOAD, site);
					mv.visitVarInsn(ALOAD, ovSite);
					mv.visitTypeInsn(CHECKCAST, "cn/edu/pku/plde/rec/val/ArrayValue");
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/Record","analzArray",	"(Ljava/lang/Object;Lcn/edu/pku/plde/rec/val/ArrayValue;)V");
					mv.visitLabel(l1);
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/rec/Record","entries", "Ljava/util/List;");
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/etr/Parameter");
					mv.visitInsn(DUP);
					mv.visitIntInsn(BIPUSH, id);
					mv.visitVarInsn(ALOAD, ovSite);
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/rec/etr/Parameter","<init>", "(ILcn/edu/pku/plde/rec/val/Value;)V");
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add","(Ljava/lang/Object;)Z");
					mv.visitInsn(POP);
				}else{// for normal compound type object
					mv.visitVarInsn(ALOAD, site);
					Label l0 = new Label();
					mv.visitJumpInsn(IFNONNULL, l0);					
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/val/NullValue","getInstance", "()Lcn/edu/pku/plde/rec/val/NullValue;");
					mv.visitVarInsn(ASTORE, ovSite);	// TODO:: modify the site while coping with multi-object-param
					Label l1 = new Label();
					mv.visitJumpInsn(GOTO, l1);
					mv.visitLabel(l0);
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/val/ObjectValue");
					mv.visitInsn(DUP);
					mv.visitLdcInsn(parameter.getType());
					mv.visitMethodInsn(INVOKESPECIAL,"cn/edu/pku/plde/rec/val/ObjectValue", "<init>","(Ljava/lang/String;)V");
					mv.visitVarInsn(ASTORE, ovSite);
					mv.visitVarInsn(ALOAD, site);
					mv.visitVarInsn(ALOAD, ovSite);
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/Record","analzCompondPara","(Ljava/lang/Object;Lcn/edu/pku/plde/rec/val/ObjectValue;)V");
					mv.visitLabel(l1);
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/rec/Record","entries", "Ljava/util/List;");
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/etr/Parameter");
					mv.visitInsn(DUP);
					mv.visitIntInsn(BIPUSH, id);
					mv.visitVarInsn(ALOAD, ovSite);
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/rec/etr/Parameter","<init>", "(ILcn/edu/pku/plde/rec/val/Value;)V");
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add","(Ljava/lang/Object;)Z");
					mv.visitInsn(POP);		
				}
			}
			// 3. for each static field
			for(StaticInfo staticInfo : mi.getStatics()){
				String className = staticInfo.getClassName().replace( "." , "/" );
				String className2 = staticInfo.getClassName();
				String fieldName = staticInfo.getFieldName();
				String type = staticInfo.getType(); // TODO:: check StaticField.type and Parameter.type have the same meaning
				String desc = staticInfo.getTypeDesc();
				if(staticInfo.isTag()){					
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
					} 
					mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/rec/Record", "putPrimStatic", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
				}else if(staticInfo.isArray()){
					mv.visitFieldInsn(GETSTATIC, className, fieldName, desc);
					Label l0 = new Label();
					mv.visitJumpInsn(IFNONNULL, l0);
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/val/NullValue", "getInstance", "()Lcn/edu/pku/plde/rec/val/NullValue;");
					mv.visitVarInsn(ASTORE, ovSite);
					Label l1 = new Label();
					mv.visitJumpInsn(GOTO, l1);
					mv.visitLabel(l0);
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/val/ArrayValue");
					mv.visitInsn(DUP);
					mv.visitLdcInsn(type);
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/rec/val/ArrayValue", "<init>", "(Ljava/lang/String;)V");
					mv.visitVarInsn(ASTORE, ovSite);
					mv.visitFieldInsn(GETSTATIC,  className, fieldName, desc );
					mv.visitVarInsn(ALOAD, ovSite);
					mv.visitTypeInsn(CHECKCAST, "cn/edu/pku/plde/rec/val/ArrayValue");
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/Record", "analzArray", "(Ljava/lang/Object;Lcn/edu/pku/plde/rec/val/ArrayValue;)V");
					mv.visitLabel(l1);
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/rec/Record", "entries", "Ljava/util/List;");
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/etr/StaticField");
					mv.visitInsn(DUP);
					mv.visitLdcInsn(className2);
					mv.visitLdcInsn(fieldName);
					mv.visitVarInsn(ALOAD, ovSite);
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/rec/etr/StaticField", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lcn/edu/pku/plde/rec/val/Value;)V");
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
					mv.visitInsn(POP);
				}else{
					mv.visitFieldInsn(GETSTATIC, className, fieldName, desc);
					Label l0 = new Label();
					mv.visitJumpInsn(IFNONNULL, l0);
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/val/NullValue", "getInstance", "()Lcn/edu/pku/plde/rec/val/NullValue;");
					mv.visitVarInsn(ASTORE, ovSite);
					Label l1 = new Label();
					mv.visitJumpInsn(GOTO, l1);
					mv.visitLabel(l0);
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/val/ObjectValue");
					mv.visitInsn(DUP);
					mv.visitLdcInsn(type); 	
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/rec/val/ObjectValue", "<init>", "(Ljava/lang/String;)V");
					mv.visitVarInsn(ASTORE, ovSite);
					mv.visitFieldInsn(GETSTATIC, className, fieldName, desc);
					mv.visitVarInsn(ALOAD, ovSite);
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/Record", "analzCompondPara", "(Ljava/lang/Object;Lcn/edu/pku/plde/rec/val/ObjectValue;)V");
					mv.visitLabel(l1);
					mv.visitVarInsn(ALOAD, recordSite);
					mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/rec/Record", "entries", "Ljava/util/List;");
					mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/etr/StaticField");
					mv.visitInsn(DUP);
					mv.visitLdcInsn(className2);
					mv.visitLdcInsn(fieldName);
					mv.visitVarInsn(ALOAD, ovSite);
					mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/rec/etr/StaticField", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lcn/edu/pku/plde/rec/val/Value;)V");
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
					mv.visitInsn(POP);
				}
			}
			// 4 for this pointer
			if(!isStatic){
				
			}
			
			mv.visitVarInsn(ALOAD, recordSite);
			mv.visitLdcInsn("/home/nightwish/workspace/test_program/output/" +ownerName +"_" +  simpleName + ".xml");	// TODO:: change to your dir
			mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/rec/Record",	"write", "(Ljava/lang/String;)V");
		}

		private int getRecordSite() {
			int i = 1;
			if(isStatic){
				i = 0;
			}
			Type[] types = Type.getArgumentTypes(desc);
			for(Type t: types){
				if(t.getClassName().equals("double") || t.getClassName().equals("long")){
					i++;
				}
				i++;
			}
			return i;
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			super.visitMaxs(maxStack, maxLocals);
//			System.out.println("INSTRUMENT RECORD SUCCESS : " + ownerName + "   " + simpleName);
		}
	}
}