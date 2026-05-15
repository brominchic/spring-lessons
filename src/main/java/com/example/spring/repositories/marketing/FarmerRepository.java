package com.example.spring.repositories.marketing;

import com.example.spring.model.dto.marketing.FarmerDto;
import com.example.spring.model.entity.marketing.FarmerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FarmerRepository extends CrudRepository<FarmerEntity, Long> {

    @Query("""
            select new com.example.spring.model.dto.marketing.FarmerDto(
                farmer.id,
                farmer.shopName,
                farmer.region
            )
            from FarmerEntity farmer
            order by farmer.shopName
            """)
    List<FarmerDto> findAllDto();
}
