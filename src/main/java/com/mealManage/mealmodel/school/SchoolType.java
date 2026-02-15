package com.mealManage.mealmodel.school;

import java.util.Arrays;
import java.util.List;

public enum SchoolType {
	
	/*k2("k", "one", "two", "three"),
	k6("four", "five", "six", "seven", "eight"),
	k10("nine", "ten", "eleven", "twelve");*/
	//US School types & grades
	PreKG_six("pk","k", "one", "two", "three","four", "five", "six"),
	k_six("k", "one", "two", "three","four", "five", "six"),
	AZ_elementary_school("k", "one", "two", "three","four", "five", "six"),
	AZ_middle_school("seven", "eight"),
	AZ_high_school("nine", "ten", "eleven", "twelve"),
	seven_twelve("seven", "eight", "nine", "ten", "eleven", "twelve"),
	 //UK School Types & Grades
	Primary_School("one", "two", "three","four", "five", "six"),
	Secondary_School("seven", "eight", "nine", "ten", "eleven"),
	staff("staff"),
	Vegetarian("Vegetarian"),
	Int_High_school("k", "one", "two", "three","four", "five", "six","seven", "eight", "nine", "ten", "eleven", "twelve"),
	US_Elementary_school("k", "one", "two", "three","four", "five"),
	US_Middle_school("six","seven", "eight"),
	US_High_school("nine", "ten", "eleven", "twelve"),
	US_Primary_School("Infant1", "Infant2", "Toddler", "Primary1", "Primary2", "Primary3"),
	PreKG_School("pk"),
	Spain_Elementary_School("pk","k", "one", "two", "three","four", "five", "six"),
	Spain_High_School("seven", "eight", "nine", "ten", "eleven", "twelve","thirteen"),
	Nursery("Nursery");
	
	private final List<String> values;

	SchoolType(String ...values) {
        this.values = Arrays.asList(values);
    }

    public List<String> getValues() {
        return values;
    }
}
