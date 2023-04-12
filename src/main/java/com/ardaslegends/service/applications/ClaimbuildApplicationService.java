package com.ardaslegends.service.applications;

import com.ardaslegends.domain.applications.ClaimbuildApplication;
import com.ardaslegends.repository.applications.ClaimbuildApplicationRepository;
import com.ardaslegends.service.AbstractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class ClaimbuildApplicationService extends AbstractService<ClaimbuildApplication, ClaimbuildApplicationRepository> {
}
