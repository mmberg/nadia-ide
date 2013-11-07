package net.msoetebier.nadia;

public class ExceptionHandler extends Exception {
	private static final long serialVersionUID = -4680323751007868272L;

	public ExceptionHandler(String message) {
		super(message);
		System.out.println(message);
	}
	
	public ExceptionHandler(String message, Throwable throwable) {
		super(message, throwable);
	}
}