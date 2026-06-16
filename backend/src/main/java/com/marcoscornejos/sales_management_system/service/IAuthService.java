package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
    public LoginResponseDTO login(LoginRequestDTO request, HttpServletRequest httpRequest);
}
