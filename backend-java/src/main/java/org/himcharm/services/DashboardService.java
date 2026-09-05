package org.himcharm.services;

import org.himcharm.dtos.DashboardResponseDTO;
import org.himcharm.dtos.CustomerDashboardResponseDTO;

import java.time.LocalDate;

public interface DashboardService {

    DashboardResponseDTO getDashboard(LocalDate fromDate, LocalDate toDate, Long storeId);

    CustomerDashboardResponseDTO getCustomerDashboardGraph();
}
