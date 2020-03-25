package newbank.test;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class NBUnit {

  /** Annotation */
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD})
  public @interface Test {}

  /** Assertions */
  public static void Assert(boolean b) {
    Assert(b, "");
  }

  public static void Assert(boolean b, String msg) {
    if (!b) throw new AssertionError(msg);
  }

  public static void AssertEqual(Object expected, Object actual) {
    Function<Object, String> toString =
        (Object o) ->
            o == null ? "null" : (o instanceof String ? "\"" + o.toString() + "\"" : o.toString());

    Assert(
        Objects.equals(expected, actual),
        String.format("expected:%s actual:%s", toString.apply(expected), toString.apply(actual)));
  }

  public static void AssertIsNotNull(Object o) {
    Assert(o != null, "null is not expected.");
  }

  /** test helpers */
  public static String runServerCommand(String userName, String password, String command) {

    String inputString =
        String.format(
            "%s\n%s\n%s%s", userName, password, command, command.length() == 0 ? "" : "\n");

    var outputStream = new ByteArrayOutputStream();

    var target =
        new newbank.server.NewBankClientHandler.ClientThreadTarget(
            new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(inputString.getBytes()))),
            new PrintWriter(outputStream));

    try {
      target.run();
      target.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      target.close();
    }

    return outputStream.toString();
  }

  /** Test runner */
  public static void run() {
    try {
      discoverTestMethods().forEach(NBUnit::invokeTestMethod);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void invokeTestMethod(Method method) {
    try {
      Object testFixture = method.getDeclaringClass().getDeclaredConstructor().newInstance();
      method.setAccessible(true); // a private method can be called by setting true.
      method.invoke(testFixture);
      printSuccess("pass: " + method.getName());
    } catch (InvocationTargetException e) {
      printError("fail: " + method.getName());
      printInvocationException(e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printInvocationException(InvocationTargetException e) {
    if (e.getTargetException() == null) e.printStackTrace();
    else if (!(e.getTargetException() instanceof AssertionError))
      e.getTargetException().printStackTrace();
    else printAssertionError((AssertionError) e.getTargetException());
  }

  private static void printAssertionError(AssertionError error) {
    printError(String.format("\tassertion failed: %s", error.getMessage()));

    var assertionCaller =
        Arrays.stream(error.getStackTrace())
            .filter(element -> !element.getMethodName().contains("Assert"))
            .findFirst()
            .get();

    printError(
        String.format(
            "\tat %s.%s(%s:%s)",
            assertionCaller.getClassName(),
            assertionCaller.getMethodName(),
            assertionCaller.getFileName(),
            assertionCaller.getLineNumber()));
  }

  private static Stream<Method> discoverTestMethods() throws IOException {
    var clientClasses = PackageHelper.getClasses("newbank.client");
    var serverClasses = PackageHelper.getClasses("newbank.server");
    var testClasses = PackageHelper.getClasses("newbank.test");

    var allClasses = Stream.concat(Stream.concat(clientClasses, serverClasses), testClasses);

    return allClasses.flatMap(NBUnit::searchTestMethods);
  }

  private static Stream<Method> searchTestMethods(Class<?> aClass) {
    return Arrays.stream(aClass.getDeclaredMethods())
        .filter(method -> method.getAnnotation(Test.class) != null);
  }

  private static class PackageHelper {

    public static Stream<Class<?>> getClasses(String packageName) throws IOException {
      return getDirectories(packageName)
          .flatMap(directory -> enumerateClasses(directory, packageName));
    }

    private static Stream<File> getDirectories(String packageName) throws IOException {

      String path = packageName.replace('.', '/');

      return PackageHelper.enumerationToList(
              Thread.currentThread().getContextClassLoader().getResources(path))
          .map(o -> new File(o.getFile()));
    }

    private static Stream<Class<?>> enumerateClasses(File directory, String currentPath) {
      return Arrays.stream(Objects.requireNonNull(directory.listFiles()))
          .flatMap(
              file -> {
                String nextPath = currentPath + "." + file.getName();

                return file.isDirectory()
                    ? enumerateClasses(file, nextPath)
                    : nextPath.endsWith(".class")
                        ? Stream.of(forNameSafe(nextPath.substring(0, nextPath.length() - 6)))
                        : null;
              })
          .filter(Objects::nonNull);
    }

    private static <T> Stream<T> enumerationToList(Enumeration<T> resources) {
      ArrayList<T> items = new ArrayList<>();

      while (resources.hasMoreElements()) {
        var o = resources.nextElement();
        items.add(o);
      }

      return items.stream();
    }

    private static Class<?> forNameSafe(String className) {
      try {
        return Class.forName(className);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      return null;
    }
  }

  private static void printError(String str) {
    print(str, ANSI_RED);
  }

  private static void printSuccess(String str) {
    print(str, ANSI_GREEN);
  }

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";

  static void print(String str, String color) {
    System.out.println(color + str + ANSI_RESET);
  }
}
