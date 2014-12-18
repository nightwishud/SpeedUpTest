package cn.edu.pku.plde.test.rec;

import java.util.*;
import org.objectweb.asm.*;

public class FobjDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "cn/edu/pku/plde/rec/RecTest",
				null, "java/lang/Object", null);

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "fobj",
					"(Lcn/edu/pku/plde/rec/A;)V", null, null);
			mv.visitCode();
			mv.visitLdcInsn("fobj");
			mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/Record",
					"getInstance",
					"(Ljava/lang/String;)Lcn/edu/pku/plde/rec/Record;");
			mv.visitVarInsn(ASTORE, 2);
			mv.visitVarInsn(ALOAD, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(IFNONNULL, l0);
			mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/NullValue",
					"getInstance", "()Lcn/edu/pku/plde/rec/NullValue;");
			mv.visitVarInsn(ASTORE, 3);
			Label l1 = new Label();
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_APPEND, 1,
					new Object[] { "cn/edu/pku/plde/rec/Record" }, 0, null);
			mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/ObjectValue");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("A obj TYPE");
			mv.visitMethodInsn(INVOKESPECIAL,
					"cn/edu/pku/plde/rec/ObjectValue", "<init>",
					"(Ljava/lang/String;)V");
			mv.visitVarInsn(ASTORE, 3);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESTATIC, "cn/edu/pku/plde/rec/Record",
					"analzCompondPara",
					"(Ljava/lang/Object;Lcn/edu/pku/plde/rec/ObjectValue;)V");
			mv.visitLabel(l1);
			mv.visitFrame(Opcodes.F_APPEND, 1,
					new Object[] { "cn/edu/pku/plde/rec/ObjectValue" }, 0, null);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(GETFIELD, "cn/edu/pku/plde/rec/Record",
					"entries", "Ljava/util/List;");
			mv.visitTypeInsn(NEW, "cn/edu/pku/plde/rec/Parameter");
			mv.visitInsn(DUP);
			mv.visitIntInsn(BIPUSH, 6);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESPECIAL, "cn/edu/pku/plde/rec/Parameter",
					"<init>", "(ILcn/edu/pku/plde/rec/Value;)V");
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add",
					"(Ljava/lang/Object;)Z");
			mv.visitInsn(POP);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitLdcInsn("D:/test/RefTest.xml");
			mv.visitMethodInsn(INVOKEVIRTUAL, "cn/edu/pku/plde/rec/Record",
					"write", "(Ljava/lang/String;)V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(5, 4);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}