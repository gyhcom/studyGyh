package com.study.gyh.zone;

import com.study.gyh.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndProvince(String cityName, String provinceName);
}