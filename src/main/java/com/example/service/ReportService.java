package com.example.service;

import com.example.dao.ReportDAO;
import com.example.dao.CarDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service layer for handling report-related operations.
 */
public class ReportService {
    private ReportDAO reportDAO;
    private CarDAO carDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
        this.carDAO = new CarDAO();
    }

    /**
     * Retrieves the frequency of each service type across all cars or a specific
     * model.
     *
     * @param model The car model to filter services. If null or empty, fetches for
     *              all models.
     * @return A map with service types as keys and their frequencies as values.
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Integer> getServiceFrequency(String model) throws SQLException {
        return reportDAO.getServiceFrequency(model);
    }

    /**
     * Retrieves the revenue generated from each service type, aggregated monthly or
     * quarterly for a specific year.
     *
     * @param year        The year to filter revenue data.
     * @param aggregation "MONTH" or "QUARTER" to specify aggregation level.
     * @return A map where the key is the service type and the value is another map
     *         of time period to revenue.
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Map<String, Double>> getRevenueByServiceType(int year, String aggregation) throws SQLException {
        return reportDAO.getRevenueByServiceType(year, aggregation);
    }

    /**
     * Retrieves all distinct car models from the database.
     *
     * @return A list of car models.
     * @throws SQLException If a database access error occurs.
     */
    public List<String> getAllCarModels() throws SQLException {
        return carDAO.getAllCarModels();
    }

    /**
     * Retrieves distinct years from the services data.
     *
     * @return A list of years.
     * @throws SQLException If a database access error occurs.
     */
    public List<Integer> getDistinctServiceYears() throws SQLException {
        return reportDAO.getDistinctServiceYears();
    }

    /**
     * Closes resources if necessary.
     */
    public void close() {
        // Implement resource cleanup if needed
    }
}
