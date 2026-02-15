package com.mealManage.mealmodel.school;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "DataSyncFieldMapping", indexes = { 
	    @Index(columnList = "mealSchoolId")})
/**This entity having all the data sync file's field mapping details**/
public class DataSyncSchoolFieldMapping extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = 5684883929287186311L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	@NotNull
	private Long mealSchoolId;
	@Enumerated(EnumType.STRING)
	@NotNull
	private DataSyncFieldConstants standardField;
	@NotNull
	private String fileMappingField;
	/**
	 * @return the recId
	 */
	public Long getRecId() {
		return recId;
	}
	/**
	 * @param recId the recId to set
	 */
	public void setRecId(Long recId) {
		this.recId = recId;
	}
	
	/**
	 * @return the mealSchoolId
	 */
	public Long getMealSchoolId() {
		return mealSchoolId;
	}
	/**
	 * @param mealSchoolId the mealSchoolId to set
	 */
	public void setMealSchoolId(Long mealSchoolId) {
		this.mealSchoolId = mealSchoolId;
	}
	/**
	 * @return the standardField
	 */
	public DataSyncFieldConstants getStandardField() {
		return standardField;
	}
	/**
	 * @param standardField the standardField to set
	 */
	public void setStandardField(DataSyncFieldConstants standardField) {
		this.standardField = standardField;
	}
	/**
	 * @return the fileMappingField
	 */
	public String getFileMappingField() {
		return fileMappingField;
	}
	/**
	 * @param fileMappingField the fileMappingField to set
	 */
	public void setFileMappingField(String fileMappingField) {
		this.fileMappingField = fileMappingField;
	}

}
