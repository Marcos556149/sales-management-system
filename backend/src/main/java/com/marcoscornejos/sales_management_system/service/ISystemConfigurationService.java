package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.SystemConfigurationRequestDTO;
import com.marcoscornejos.sales_management_system.dto.SystemConfigurationResponseDTO;

public interface ISystemConfigurationService{

    SystemConfigurationResponseDTO getConfiguration();

    SystemConfigurationResponseDTO updateConfiguration(SystemConfigurationRequestDTO request);
}
