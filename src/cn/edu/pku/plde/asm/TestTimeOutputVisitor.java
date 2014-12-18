package cn.edu.pku.plde.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cn.edu.pku.plde.utils.Task;

public class TestTimeOutputVisitor extends ClassVisitor implements Opcodes{

	private boolean isInterface;
	private String className;
	private Task task;
	public TestTimeOutputVisitor(ClassVisitor cv, Task t) {
		super(ASM4, cv);
		task = t;
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
		if (!isInterface && mv != null && name.startsWith("test") && (name.length() > 4) ) { //  just instrument test case method
			mv = new TestCaseMethodAdapter(mv);
		}
		return mv;
	}
	
	
	class TestCaseMethodAdapter extends MethodVisitor{

		
		public TestCaseMethodAdapter(MethodVisitor mv) {
			super(ASM4, mv);
		}
		
		@Override
		public void visitInsn(int opcode) {
			if(opcode >= IRETURN && opcode <= RETURN){	// TODO:: || opcode == ATHROW ??
				if(task.equals(Task.OUTPUTTIME)){
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/TimeRecord", "writeAll", "()V");
				}else{
					mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/BigTimeRecord", "writeAll", "()V");
				}
			}
			super.visitInsn(opcode);
		}
		
	}
}
