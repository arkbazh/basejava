package org.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.example.model.Resume;

public class MainReflection {
    static void main(String[] args) {
        Resume resume = new Resume();

        extractToString(resume);
    }

    private static void extractToString(Resume resume) {
        try {
            Method toStrigMethod = resume.getClass().getMethod("toString");
            String str = (String) toStrigMethod.invoke(resume);
            System.out.println(str);
        } catch (ReflectiveOperationException err) {
            Throwable cause = (err instanceof InvocationTargetException exc)
                    ? exc.getCause()
                    : err;
            System.out.println("Failed " + cause);
        }
    }
}
