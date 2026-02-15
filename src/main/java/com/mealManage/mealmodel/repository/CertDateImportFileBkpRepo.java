package com.mealManage.mealmodel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.mealManage.menu.entities.CertDateImportFileBkp;

public interface CertDateImportFileBkpRepo extends JpaRepository<CertDateImportFileBkp, Long> {
	
	public List<CertDateImportFileBkp> findByMealSchoolIdAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);

}
