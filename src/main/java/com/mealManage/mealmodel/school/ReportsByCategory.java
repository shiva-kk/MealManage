package com.mealManage.mealmodel.school;

import java.util.Arrays;
import java.util.List;

public enum ReportsByCategory {
	
	standard("School Report", "Order Report", "Negative Balance Students", "Balance Payment Type","Ordered Vs Not Ordered",
			"Available Balance Amount", "Lunch Eligibility","Allergy Report","Regularly ordered lunch students but not having order in specific month",
			"Deposits Report","Purchase Report","Account History","Low Balance Report","Deposit Summary Report"),
	extended("Free/Reduced Price Lunch Eligibility Survey Report", "Students Lunch Eligibility Program Actual Report"),
	caterer("Monthly Caterer Report"),
	audit("Summarized reimbursement claim report","Daily edit check worksheet report");
	
	private final List<String> values;

	ReportsByCategory(String ...values) {
        this.values = Arrays.asList(values);
    }

    public List<String> getValues() {
        return values;
    }

}
