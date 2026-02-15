package com.mealManage.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mealManage.mealmodel.school.SchoolGrades;

public class GradeFormatBuild {
	
	/**This method used for return the grades name as String by comma separated in sequence order**/
	public String getGradesFromSet(Set<SchoolGrades> gradeNames){
		StringBuilder sb = new StringBuilder();
		SchoolGrades[] schoolGrades = gradeNames.toArray(new SchoolGrades[0]);
		Arrays.sort(schoolGrades);
		for(SchoolGrades schGrd : schoolGrades){
			sb.append(schGrd.toString()).append(",");
		}
		String finalGradesName = "";
		if(sb.length() > 0)
			finalGradesName = sb.toString().substring(0, sb.length()-1);
		
		return finalGradesName;
	}
	
	/**Build the required grade format**/
	public String buildGradeName(String gradeName, Map<String, String> gradeMap){
		String[] gradesStr = gradeName.split(",");
		Set<SchoolGrades> schoolGradesList = new HashSet<>();
		for(String grade : gradesStr){
			schoolGradesList.add(SchoolGrades.valueOf(grade));
		}
		SchoolGrades[] grades1 = schoolGradesList.toArray(new SchoolGrades[0]);
		Arrays.sort(grades1);
		List<String> grades2 = new LinkedList<String>();
		for(SchoolGrades sg : grades1){
			grades2.add(sg.toString());
		}
		String[] grades = grades2.stream().toArray(String[]::new);
		
		StringBuilder sb = new StringBuilder();
		String finalGrades = "";	
		Boolean status = false;
		int j = 0;
		for(int i=0; i < grades.length; i++){
			if(i == 0){
				j = SchoolGrades.valueOf(grades[i]).ordinal();
			}
			if(!grades[i].equalsIgnoreCase((SchoolGrades.values()[j]).toString())){
				status = true;
			}
			sb.append(gradeMap.get(grades[i])).append(",");
			j++;
		}
		
		if(sb.length() > 0)
			finalGrades = sb.toString().substring(0, sb.length()-1);
		
		if(!status && grades.length > 1){
			grades = finalGrades.split(",");
			finalGrades = grades[0]+"-"+grades[grades.length-1];
		}
			
		return finalGrades;
 	}
		
	/**Return the grade in key value as Map**/
	/*public Map<String, String> gradeKeyValue(){
		Map<String, String> gradeMap = new HashMap<>();
		gradeMap.put("pk", "PK");
		gradeMap.put("kg", "KG");
		gradeMap.put("k", "KG");
		//gradeMap.put("k", "k");
		gradeMap.put("one", "1");
		gradeMap.put("two", "2");
		gradeMap.put("three", "3");
		gradeMap.put("four", "4");
		gradeMap.put("five", "5");
		gradeMap.put("six", "6");
		gradeMap.put("seven", "7");
		gradeMap.put("eight", "8");
		gradeMap.put("nine", "9");
		gradeMap.put("ten", "10");
		gradeMap.put("eleven", "11");
		gradeMap.put("twelve", "12");
		gradeMap.put("thirteen", "13");
		gradeMap.put("staff", "Staff");
		gradeMap.put("year_1", "year_1");
		gradeMap.put("year_2", "year_2");
		gradeMap.put("year_3", "year_3");
		gradeMap.put("year_4", "year_4");
		gradeMap.put("year_5", "year_5");
		gradeMap.put("year_6", "year_6");
		gradeMap.put("year_7", "year_7");
		gradeMap.put("year_8", "year_8");
		gradeMap.put("year_9", "year_9");
		gradeMap.put("year_10", "year_10");
		gradeMap.put("year_11", "year_11");	
		return gradeMap;
	}*/

	/**This method used for convert list of String to Set of SchoolGrade enum**/
	public Set<SchoolGrades> convertToSchoolGradeSet(List<String> grades){
		Set<SchoolGrades> schoolGrades = new HashSet<SchoolGrades>();
		for(String str : grades){
			schoolGrades.add(SchoolGrades.valueOf(str));
		}
		return schoolGrades;
	}

}
