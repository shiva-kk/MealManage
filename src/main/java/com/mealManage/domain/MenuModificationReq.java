package com.mealManage.domain;

import java.util.List;
import java.util.Set;

import com.mealManage.mealmodel.meal.MealMenu;

/**This class used for add/remove/edit the menu item based on item id**/
public class MenuModificationReq {

	private String menuModificationDate;
	private Long deleteSchoolMealId;
	private Set<MealMenu> addMenus;
	private List<MenuUpdateDetails> menuEdit;
	private String menuDeletionReason;
	private String cancellationDate;
	
	/**
	 * @return the deleteSchoolMealId
	 */
	public Long getDeleteSchoolMealId() {
		return deleteSchoolMealId;
	}
	/**
	 * @param deleteSchoolMealId the deleteSchoolMealId to set
	 */
	public void setDeleteSchoolMealId(Long deleteSchoolMealId) {
		this.deleteSchoolMealId = deleteSchoolMealId;
	}
	/**
	 * @return the addMenus
	 */
	public Set<MealMenu> getAddMenus() {
		return addMenus;
	}
	/**
	 * @param addMenus the addMenus to set
	 */
	public void setAddMenus(Set<MealMenu> addMenus) {
		this.addMenus = addMenus;
	}
	
	/**
	 * @return the menuEdit
	 */
	public List<MenuUpdateDetails> getMenuEdit() {
		return menuEdit;
	}
	/**
	 * @param menuEdit the menuEdit to set
	 */
	public void setMenuEdit(List<MenuUpdateDetails> menuEdit) {
		this.menuEdit = menuEdit;
	}
	/**
	 * @return the menuDeletionReason
	 */
	public String getMenuDeletionReason() {
		return menuDeletionReason;
	}
	/**
	 * @param menuDeletionReason the menuDeletionReason to set
	 */
	public void setMenuDeletionReason(String menuDeletionReason) {
		this.menuDeletionReason = menuDeletionReason;
	}
	/**
	 * @return the cancellationDate
	 */
	public String getCancellationDate() {
		return cancellationDate;
	}
	/**
	 * @param cancellationDate the cancellationDate to set
	 */
	public void setCancellationDate(String cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public String getMenuModificationDate() {
		return menuModificationDate;
	}

	public void setMenuModificationDate(String menuModificationDate) {
		this.menuModificationDate = menuModificationDate;
	}
}
