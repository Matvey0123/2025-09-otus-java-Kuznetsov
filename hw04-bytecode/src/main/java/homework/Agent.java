package homework;

import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.objectweb.asm.*;

@SuppressWarnings("java:S1172")
public class Agent {

    private Agent() {}

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer) {
                var methodsWithAnnotation = getMethodsWithLogAnnotation(classfileBuffer);
                if (!methodsWithAnnotation.isEmpty()) {
                    return addProxyMethods(classfileBuffer, methodsWithAnnotation, className);
                }
                return classfileBuffer;
            }
        });
    }

    private static List<MethodDto> getMethodsWithLogAnnotation(byte[] originalClass) {
        var classReader = new ClassReader(originalClass);
        var annotationScanner = new AnnotationScanner();
        classReader.accept(annotationScanner, 0);
        return annotationScanner.getMethodsWithAnnotation();
    }

    private static byte[] addProxyMethods(byte[] originalClass, List<MethodDto> methods, String className) {
        var cr = new ClassReader(originalClass);
        var cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        var cv = new LogAnnotationProcessor(Opcodes.ASM9, cw, methods);
        cr.accept(cv, Opcodes.ASM9);

        for (MethodDto methodDto : methods) {
            var originalMethodName = methodDto.getMethodName();
            var proxiedMethodName = originalMethodName + "Proxied";
            var methodDescriptor = methodDto.getMethodDescriptor();
            var methodArguments = getMethodArguments(methodDescriptor);

            var mv = cw.visitMethod(Opcodes.ACC_PUBLIC, originalMethodName, methodDescriptor, null, null);
            addLogging(mv, originalMethodName, methodArguments);

            mv.visitVarInsn(Opcodes.ALOAD, 0);
            loadAllArgsForMethod(mv, methodArguments);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, proxiedMethodName, methodDescriptor, false);

            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        return cw.toByteArray();
    }

    private static Type[] getMethodArguments(String methodDescriptor) {
        Type methodType = Type.getMethodType(methodDescriptor);
        return methodType.getArgumentTypes();
    }

    private static String buildLogString(String methodName, int argumentsCount) {
        return "executed method: " + methodName + ", params: "
                + IntStream.range(0, argumentsCount).mapToObj(i -> "\u0001").collect(Collectors.joining(","));
    }

    private static String buildConcatMethodDescriptor(Type[] types) {
        StringBuilder result = new StringBuilder("(");
        for (Type type : types) {
            result.append(type);
        }
        result.append(")Ljava/lang/String;");
        return result.toString();
    }

    private static Handle getConcatMethodHandle() {
        return new Handle(
                H_INVOKESTATIC,
                Type.getInternalName(java.lang.invoke.StringConcatFactory.class),
                "makeConcatWithConstants",
                MethodType.methodType(
                                CallSite.class,
                                MethodHandles.Lookup.class,
                                String.class,
                                MethodType.class,
                                String.class,
                                Object[].class)
                        .toMethodDescriptorString(),
                false);
    }

    private static void loadAllArgsForMethod(MethodVisitor mv, Type[] methodArguments) {
        for (int i = 1; i <= methodArguments.length; i++) {
            var type = methodArguments[i - 1];
            mv.visitVarInsn(type.getOpcode(ILOAD), i);
        }
    }

    private static void addLogging(MethodVisitor mv, String originalMethodName, Type[] methodArguments) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        int methodArgumentsCount = methodArguments.length;
        var logString = buildLogString(originalMethodName, methodArgumentsCount);
        loadAllArgsForMethod(mv, methodArguments);
        var concatMethodDescriptor = buildConcatMethodDescriptor(methodArguments);
        mv.visitInvokeDynamicInsn(
                "makeConcatWithConstants", concatMethodDescriptor, getConcatMethodHandle(), logString);

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
