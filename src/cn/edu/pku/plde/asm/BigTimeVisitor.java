package cn.edu.pku.plde.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import cn.edu.pku.plde.rec.BigTimeRecord;

public class BigTimeVisitor extends ClassVisitor implements Opcodes {
	private boolean isInterface;
	private String className;
	
	public BigTimeVisitor(ClassVisitor cv) {
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
			mv = new BigTimerMethodAdapter(access, name, desc, mv);
		}
		return mv;
	}

	class BigTimerMethodAdapter extends LocalVariablesSorter {
		private String ownerName;
		private String simpleName;
		private String desc;
		private int recordSite;
		private int t1Site;
		private int t2Site;
		
		public BigTimerMethodAdapter(int access, String name, String desc,
				MethodVisitor mv) {
			super(ASM4, access, desc, mv);
			// methodName = className+"."+name+desc;
			ownerName = className.replace('/', '.');
			simpleName = name;
			this.desc = desc;
		}
		
		@Override
		public void visitCode() {
			super.visitCode();
			mv.visitLdcInsn(ownerName + " " + simpleName + " " + desc);
			mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/BigTimeRecord", "getInstance", "(Ljava/lang/String;)Lcn/edu/pku/plde/rec/BigTimeRecord;");
			recordSite = newLocal(Type.getType(BigTimeRecord.class));
			mv.visitVarInsn(ASTORE, recordSite);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J");
			t1Site = newLocal(Type.LONG_TYPE);
			mv.visitVarInsn(LSTORE, t1Site);
		}
		@Override
		public void visitInsn(int opcode) {
			if(opcode >= IRETURN && opcode <= RETURN){	// TODO:: || opcode == ATHROW ??
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J");
				t2Site = newLocal(Type.LONG_TYPE);
				mv.visitVarInsn(LSTORE, t2Site);
				mv.visitVarInsn(ALOAD, recordSite);
				mv.visitVarInsn(ALOAD, recordSite);
				mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/rec/BigTimeRecord", "useTime", "Ljava/math/BigInteger;");
				mv.visitVarInsn(LLOAD, t2Site);
				mv.visitVarInsn(LLOAD, t1Site);
				mv.visitInsn(LSUB);
				mv.visitMethodInsn(INVOKESTATIC, "java/math/BigInteger", "valueOf", "(J)Ljava/math/BigInteger;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "add", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
				mv.visitFieldInsn(PUTFIELD, "cn/edu/pku/plde/rec/BigTimeRecord", "useTime", "Ljava/math/BigInteger;");
			}
			super.visitInsn(opcode);
		}
		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			super.visitMaxs(maxStack, maxLocals);
//			System.out.println("INSTRUMENT BIG-TIMER SUCCESS : " + ownerName + "   " + simpleName);
		}
	}
}
