package homework;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AnnotationScanner extends ClassVisitor {

    private static final String LOG_ANNOTATION_DESCRIPTOR = "Lhomework/Log;";

    private final List<MethodDto> methodsWithAnnotation = new ArrayList<>();

    public AnnotationScanner() {
        super(Opcodes.ASM9);
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor, String signature, String[] exceptions) {
        var methodDescriptor = descriptor;
        return new MethodVisitor(Opcodes.ASM9) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (descriptor.equals(LOG_ANNOTATION_DESCRIPTOR)) {
                    methodsWithAnnotation.add(new MethodDto(name, methodDescriptor));
                }
                return super.visitAnnotation(descriptor, visible);
            }
        };
    }

    public List<MethodDto> getMethodsWithAnnotation() {
        return methodsWithAnnotation;
    }
}
