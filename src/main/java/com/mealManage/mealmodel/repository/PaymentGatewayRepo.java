package com.mealManage.mealmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.mealManage.mealmodel.transaction.PaymentGateway;

@Repository
/**This repository used for payment gateway info***/
public interface PaymentGatewayRepo extends JpaRepository<PaymentGateway, Long>{
	
	@RestResource(exported = false)
	/**This API disabled for delete operation of payment gateway**/
	public void delete(PaymentGateway paymentGateway);
	 
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for create payment gateway. It can be execute by super admin user only.**/
	public PaymentGateway save(PaymentGateway paymentGateway);

}
