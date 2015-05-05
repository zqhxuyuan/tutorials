package com.java7developer.chapter5;

// NOTE: This class requires the ASM library - version 4 RC1 or above.

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;

import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class DynamicIndyMaker {
  private volatile int ID_GENERATOR = 0;

  public void makeClassWithInvokeDynamic(String nameOfIndyCallsite,
      MethodType desc, Class<?> bsmClass, String bsmName, MethodType bsmType,
      Object... bsmArgs) {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    String clazzName = "Gen" + (ID_GENERATOR++);
    cw.visit(V1_7, ACC_PUBLIC | ACC_SUPER, clazzName, null, "java/lang/Object",
        null);

    MethodVisitor init = cw
        .visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    init.visitCode();
    init.visitVarInsn(ALOAD, 0);
    init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
    init.visitInsn(RETURN);
    init.visitMaxs(-1, -1);
    init.visitEnd();

    String descriptor = desc.toMethodDescriptorString();

    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "invokedynamic",
        descriptor, null, null);
    // MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "invokedynamic",
    // descriptor, null, null);
    int slot = 0;
    for (Type parameterType : Type.getArgumentTypes(descriptor)) {
      mv.visitVarInsn(parameterType.getOpcode(ILOAD), slot);
      slot += parameterType.getSize();
    }

    // 1st 2 args are name and descriptor of this indy callsite
    mv.visitInvokeDynamicInsn(nameOfIndyCallsite, descriptor,
        new Handle(H_INVOKESTATIC, bsmClass.getName().replace('.', '/'),
            bsmName, bsmType.toMethodDescriptorString()), bsmArgs);

    Type returnType = Type.getReturnType(descriptor);
    mv.visitInsn(returnType.getOpcode(IRETURN));

    mv.visitMaxs(-1, -1);
    mv.visitEnd();

    cw.visitEnd();

    byte[] bytes = cw.toByteArray();
    // Class<?> clazz = defineClass(null, bytes, 0, bytes.length);

    /*
     * try (FileOutputStream fos = new FileOutputStream(clazzName + ".class")) {
     * fos.write(bytes); }
     */

    try {
      FileOutputStream fos = new FileOutputStream(clazzName + ".class");
      fos.write(bytes);
    } catch (IOException iox) {
      iox.printStackTrace();
    }

    /*
     * try { return MethodHandles.lookup().findStatic(clazz, "invokedynamic",
     * desc); // return MethodHandles.lookup().findVirtual(clazz,
     * "invokedynamic", desc); } catch (NoAccessException e) { throw
     * (AssertionError)new AssertionError().initCause(e); }
     */
  }

  // Bootstrap method, has to be static and return a callsite
  public static CallSite bsm(Lookup lookup, String name, MethodType methodType,
      Object arg) {
    System.out.println("construct the BigDecimal constant " + arg);

    // return a trivial constant callsite, with a constant method handle as its
    // target
    return new ConstantCallSite(MethodHandles.constant(BigDecimal.class,
        new BigDecimal(arg.toString())));
  }

  public static void main(String[] args) throws Throwable {
    DynamicIndyMaker indyMaker = new DynamicIndyMaker();
    // indyMaker.makeClassWithInvokeDynamic(nameOfCallSite, desc, bsmClass,
    // bsmName, bsmType, bsmArgs)

    indyMaker.makeClassWithInvokeDynamic("_", MethodType
        .methodType(BigDecimal.class), DynamicIndyMaker.class, "bsm",
        MethodType.methodType(CallSite.class, Lookup.class, String.class,
            MethodType.class, Object.class), "1234567890.1234567890");

  }

}
