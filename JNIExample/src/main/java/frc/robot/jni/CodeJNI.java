package frc.robot.jni;

public class CodeJNI {
    // Load native library <name>.dll (Windows) or lib<name>.so (Unixes) at runtime
    // This library contains a native method called myFunc()
    static {
        System.loadLibrary("JniLibrary");
    }

    public static native int myFunc();
}