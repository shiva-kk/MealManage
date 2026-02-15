package com.mealManage.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.mealManage.mealmodel.school.ModuleInfo;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "TierInfo", uniqueConstraints={
	    @UniqueConstraint(columnNames = {"tierName"})})
/**This entity used for capture the Tier Info**/
public class TierInfo implements Serializable{

	private static final long serialVersionUID = 120640370652653795L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tierId", updatable = false, nullable = false)
	private Long tierId;
	private String tierName;
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
	        name = "tierInfo_moduleInfo", 
	        joinColumns = { @JoinColumn(name = "tierId") }, 
	        inverseJoinColumns = { @JoinColumn(name = "moduleId") }
	    )
	@OrderBy("moduleName ASC")
	private Set<ModuleInfo> moduleInfos = new HashSet<>();
	/**
	 * @return the tierId
	 */
	public Long getTierId() {
		return tierId;
	}
	/**
	 * @param tierId the tierId to set
	 */
	public void setTierId(Long tierId) {
		this.tierId = tierId;
	}
	/**
	 * @return the tierName
	 */
	public String getTierName() {
		return tierName;
	}
	/**
	 * @param tierName the tierName to set
	 */
	public void setTierName(String tierName) {
		this.tierName = tierName;
	}
	/**
	 * @return the moduleInfos
	 */
	public Set<ModuleInfo> getModuleInfos() {
		return moduleInfos;
	}
	/**
	 * @param moduleInfos the moduleInfos to set
	 */
	public void setModuleInfos(Set<ModuleInfo> moduleInfos) {
		this.moduleInfos = moduleInfos;
	}
	
}
