package com.mealManage.mealmodel.meal;

import java.math.BigInteger;
import java.util.Date;

/**This POJO class used for menu item info**/
public class MenuItemInfo {
	
	private Long menuId;
	private String desc;
	private Double price;
	private Double reducedPrice;
	private Date start; //date should be like yyyy-MM-dd
	private String title;
	private MealType type;
	
	public MenuItemInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public MenuItemInfo(Object[] obj) {
		super();
		this.menuId = obj[0]!=null?((BigInteger)obj[0]).longValue():null;
		this.desc = obj[1]!=null?(String)obj[1]:null;
		/*this.price = obj[2]!=null?(Double)obj[2]:null;
		this.reducedPrice = obj[3]!=null?(Double)obj[3]:null;
		this.start = obj[4]!=null?(Timestamp)obj[4]:null;*/
		this.title = obj[2]!=null?(String)obj[2]:null;
		this.type = obj[3]!=null?MealType.valueOf((String)obj[3]):null;	
	}

	/**
	 * @return the menuId
	 */
	public Long getMenuId() {
		return menuId;
	}
	/**
	 * @param menuId the menuId to set
	 */
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
	/**
	 * @return the reducedPrice
	 */
	public Double getReducedPrice() {
		return reducedPrice;
	}
	/**
	 * @param reducedPrice the reducedPrice to set
	 */
	public void setReducedPrice(Double reducedPrice) {
		this.reducedPrice = reducedPrice;
	}
	
	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the type
	 */
	public MealType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(MealType type) {
		this.type = type;
	}
	
}
