package com.ardaslegends.repository.applications.claimbuildapp;

import com.ardaslegends.domain.applications.ClaimbuildApplication;

public interface ClaimbuildApplicationRepositoryCustom {
    ClaimbuildApplication queryByName(String claimbuildName);
}
