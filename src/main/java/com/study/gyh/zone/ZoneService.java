/* (C)2024 */
package com.study.gyh.zone;

import com.study.gyh.domain.Zone;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zone.csv");
            List<Zone> zoneList =
                    Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                            // stream을 이용
                            // 중간연산 map
                            .map(
                                    line -> {
                                        String[] split = line.split(",");
                                        return Zone.builder()
                                                .city(split[0])
                                                .localNameOfCity((split[1]))
                                                .province(split[2])
                                                .build();
                                    })
                            .distinct()
                            .collect(Collectors.toList());
            // 최종연산 리스트로
            zoneRepository.saveAll(zoneList);
        }
    }
}
