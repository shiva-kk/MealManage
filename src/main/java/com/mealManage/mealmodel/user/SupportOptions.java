package com.mealManage.mealmodel.user;

import java.util.Arrays;
import java.util.List;

public enum SupportOptions {
	
	Parent("Registration related inquiry", "Payment related inquiry", "Order related inquiry","Student profile related inquiry"),
	School("General product inquiry", "Quote request", "Product demo request", "Product Feedback");
	
	private final List<String> values;

	SupportOptions(String ...values) {
        this.values = Arrays.asList(values);
    }
    public List<String> getValues() {
        return values;
    }

}
