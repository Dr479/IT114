package server;

public class Debug {
	private static boolean isDebug = true;

	public static void setDebug(boolean debug) {
		Debug.isDebug = debug;
	}

	
	
	public static void log(String message) {
		if (!isDebug) {
			return;
		}
		System.out.println(message);
	}
}