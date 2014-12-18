package cn.edu.pku.plde.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import cn.edu.pku.plde.rec.TimeRecord;

public class TimeVisitor extends ClassVisitor implements Opcodes {
	private boolean isInterface;
	private String className;
	
	public TimeVisitor(ClassVisitor cv) {
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
		if (!isInterface && mv != null && !name.equals("<init>") &&  ! name.equals("<clinit>")	&& !name.equals("main")  ) { // TODO:: <clinit> or main needed ??
			mv = new TimerMethodAdapter(access, name, desc, mv);
		}
		return mv;
	}
	
	class TimerMethodAdapter extends LocalVariablesSorter {
		private boolean isStatic;
		private String ownerName;
		private String simpleName;
		private String desc;
		private int recordSite;
		
		public TimerMethodAdapter(int access, String name, String desc,
				MethodVisitor mv) {
			super(ASM4, access, desc, mv);
			// methodName = className+"."+name+desc;
			ownerName = className.replace('/', '.');
			simpleName = name;
			isStatic = (access & ACC_STATIC) != 0;
			this.desc = desc;
		}

		@Override
		public void visitCode() {
			super.visitCode();
			mv.visitLdcInsn(ownerName + " " + simpleName + " " + desc);
			mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/TimeRecord", "getInstance"
			, "(Ljava/lang/String;)Lcn/edu/pku/plde/rec/TimeRecord;");
			recordSite = newLocal(Type.getType(TimeRecord.class));
			mv.visitVarInsn(ASTORE, recordSite);
			mv.visitVarInsn(ALOAD, recordSite);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/rec/TimeRecord", "useTime", "J");
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J");
			mv.visitInsn(LSUB);
			mv.visitFieldInsn(PUTFIELD, "cn/edu/pku/plde/rec/TimeRecord", "useTime", "J");
		}

		
		
		@Override
		public void visitInsn(int opcode) {
			if(opcode >= IRETURN && opcode <= RETURN){	// TODO:: || opcode == ATHROW ??
				mv.visitVarInsn(ALOAD, recordSite);
				mv.visitInsn(DUP);
				mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/rec/TimeRecord", "useTime", "J");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J");
				mv.visitInsn(LADD);
				mv.visitFieldInsn(PUTFIELD, "cn/edu/pku/plde/rec/TimeRecord", "useTime", "J");
			}
			super.visitInsn(opcode);
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
//			System.out.println("INSTRUMENT TIMER SUCCESS : " + ownerName + "   " + simpleName);
		}
	}
}
