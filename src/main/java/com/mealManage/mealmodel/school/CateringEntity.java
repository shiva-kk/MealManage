package com.mealManage.mealmodel.school;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

@MappedSuperclass
/**This entity define the name of service provide to whom**/
public abstract class CateringEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "SchoolIdGenerator")
	@TableGenerator(table = "SCHOOL_SEQUENCES", name = "SchoolIdGenerator")
	private Long schoolId;
	
	private String name;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the schoolId
	 */
	public Long getSchoolId() {
		return schoolId;
	}

	/**
	 * @param schoolId the schoolId to set
	 */
	public void setSchoolId(Long schoolId) {
		this.schoolId = schoolId;
	}

}
