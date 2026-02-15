package com.mealManage.mealmodel.user;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "ModuleDetails", uniqueConstraints={
	    @UniqueConstraint(columnNames = {"module", "subModule", "pageName"})})
/**This entity used for capture the Module details**/
public class ModuleDetails implements Serializable{

	private static final long serialVersionUID = 120640370652653795L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "moduleId", updatable = false, nullable = false)
	private Long moduleId;
	@NotNull
	private String module;
	@NotNull
	private String subModule;
	private String pageName;
	private String moduleIcon;
	private String access;
	@Transient
	private Map<String, Boolean> accessMap;
	/**
	 * @return the moduleId
	 */
	public Long getModuleId() {
		return moduleId;
	}
	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	/**
	 * @return the module
	 */
	public String getModule() {
		return module;
	}
	/**
	 * @param module the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}
	/**
	 * @return the subModule
	 */
	public String getSubModule() {
		return subModule;
	}
	/**
	 * @param subModule the subModule to set
	 */
	public void setSubModule(String subModule) {
		this.subModule = subModule;
	}
	
	/**
	 * @return the pageName
	 */
	public String getPageName() {
		return pageName;
	}
	/**
	 * @param pageName the pageName to set
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	/**
	 * @return the moduleIcon
	 */
	public String getModuleIcon() {
		return moduleIcon;
	}
	/**
	 * @param moduleIcon the moduleIcon to set
	 */
	public void setModuleIcon(String moduleIcon) {
		this.moduleIcon = moduleIcon;
	}
	/**
	 * @return the access
	 */
	public String getAccess() {
		return access;
	}
	/**
	 * @param access the access to set
	 */
	public void setAccess(String access) {
		this.access = access;
	}
	/**
	 * @return the accessMap
	 */
	public Map<String, Boolean> getAccessMap() {
		return accessMap;
	}
	/**
	 * @param accessMap the accessMap to set
	 */
	public void setAccessMap(Map<String, Boolean> accessMap) {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			String arrayToJson = objectMapper.writeValueAsString(accessMap);
			this.access=arrayToJson;
		} catch (JsonProcessingException e) {}
		this.accessMap = accessMap;
	}
	
}
