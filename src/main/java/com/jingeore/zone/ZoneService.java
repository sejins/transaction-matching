package com.jingeore.zone;

import com.jingeore.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    // 디비에 지역정보 데이터를 항상 가지고 있어야한다.
    // 이는 처음 서비스를 시작할때만 디비에 반영을 한다.
    @PostConstruct
    public void initZoneData() throws IOException {
        if(zoneRepository.count() == 0){
            Resource resource = new ClassPathResource("zones_kr.csv");
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] strArr = line.split(",");
                        return Zone.builder().city(strArr[0]).localNameOfCity(strArr[1]).province(strArr[2]).build();
                    }).collect(Collectors.toList());
            zoneRepository.saveAll(zoneList);
        }
    }
}
