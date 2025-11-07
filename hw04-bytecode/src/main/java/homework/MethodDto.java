package homework;

public class MethodDto {

    private final String methodName;

    private final String methodDescriptor;

    public MethodDto(String methodName, String methodDescriptor) {
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }
}
