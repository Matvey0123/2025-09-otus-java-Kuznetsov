package homework;

import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LogAnnotationProcessor extends ClassVisitor {

    private static final String LOG_ANNOTATION_DESCRIPTOR = "Lhomework/Log;";

    private final List<MethodDto> methods;

    public LogAnnotationProcessor(int api, ClassVisitor classVisitor, List<MethodDto> methods) {
        super(api, classVisitor);
        this.methods = methods;
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor, String signature, String[] exceptions) {
        if (isMethodPresent(name, descriptor, methods)) {
            int privateAccess = (access & ~Opcodes.ACC_PUBLIC & ~Opcodes.ACC_PROTECTED & ~Opcodes.ACC_PRIVATE)
                    | Opcodes.ACC_PRIVATE;
            var mv = super.visitMethod(privateAccess, name + "Proxied", descriptor, signature, exceptions);
            return new MethodVisitor(Opcodes.ASM9, mv) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (descriptor.equals(LOG_ANNOTATION_DESCRIPTOR)) {
                        return null;
                    }
                    return super.visitAnnotation(descriptor, visible);
                }
            };
        } else {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private boolean isMethodPresent(String name, String descriptor, List<MethodDto> methods) {
        return methods.stream()
                .anyMatch(m -> m.getMethodName().equals(name)
                        && m.getMethodDescriptor().equals(descriptor));
    }
}
