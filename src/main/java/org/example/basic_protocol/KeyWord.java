package org.example.basic_protocol;

public interface KeyWord {
    public final static String EXIT_SIGN = "$$-exit-$$";
    public final static String EXIT_CONFIRM = "%%-EXIT_CONFIRM-$$";

    enum Method{ERROR, MESSAGE};
}
