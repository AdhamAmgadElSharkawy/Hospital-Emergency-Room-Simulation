public class Logger {
  public static synchronized void print(String param) {
    System.out.println(param);
  }
  public static synchronized void header(String param) {
    System.out.print(param);
  }
}
