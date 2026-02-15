package com.mealManage;

public class RequestCorrelation {
	public static final String CORRELATION_ID = "correlationId";

    private static final ThreadLocal<String> id = new ThreadLocal<String>();


    public static String getId() { return id.get(); }

    public static void setId(String correlationId) { id.set(correlationId); }

}
