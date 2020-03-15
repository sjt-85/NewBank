package newbank.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NBUnit {

  public static void runTest(Class<?>[] classes) {
    Arrays.stream(classes).forEach(NBUnit::runTestClass);
  }

  private static List<Method> getPublicMethods(Class<?> aClass) {
    return Arrays.stream(aClass.getDeclaredMethods())
        .filter(method -> Modifier.isPublic(method.getModifiers()))
        .collect(Collectors.toList());
  }

  private static void runTestClass(Class<?> aClass) {
    System.out.println("running..." + aClass.getName());
    getPublicMethods(aClass)
        .forEach(
            method -> {
              try {
                Object testFixture = aClass.getDeclaredConstructor().newInstance();
                method.invoke(testFixture);
                System.out.println("pass: " + method.getName());
              } catch (InvocationTargetException e) {
                System.out.println("fail: " + method.getName());
                if (e.getTargetException() != null) e.getTargetException().printStackTrace();
                else e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
  }

  public static void Assert(boolean b) {
    Assert(b, "");
  }

  public static void Assert(boolean b, String msg) {
    if (!b) throw new AssertionError(msg);
  }

  public static void AssertEqual(Object expected, Object actual) {
    Assert(
        Objects.equals(expected, actual),
        String.format(
            "extected:%s actual:%s",
            expected == null ? "null" : expected.toString(),
            actual == null ? "null" : actual.toString()));
  }
}
