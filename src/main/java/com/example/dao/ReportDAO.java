package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Data Access Object for generating reports.
 */
public class ReportDAO extends AbstractDAO<Object> {

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
        String sql;
        if (model == null || model.isEmpty()) {
            sql = "SELECT ServiceDescription AS service_type, COUNT(*) AS frequency FROM services GROUP BY ServiceDescription";
        } else {
            sql = "SELECT s.ServiceDescription AS service_type, COUNT(*) AS frequency " +
                    "FROM services s " +
                    "JOIN cars c ON s.CarID = c.CarID " +
                    "WHERE c.Model = ? " +
                    "GROUP BY s.ServiceDescription";
        }

        Map<String, Integer> frequencyMap = new HashMap<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            if (model != null && !model.isEmpty()) {
                statement.setString(1, model);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String serviceType = rs.getString("service_type");
                    int frequency = rs.getInt("frequency");
                    frequencyMap.put(serviceType, frequency);
                }
            }
        }

        return frequencyMap;
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
        if (!aggregation.equalsIgnoreCase("MONTH") && !aggregation.equalsIgnoreCase("QUARTER")) {
            throw new IllegalArgumentException("Aggregation must be either 'MONTH' or 'QUARTER'");
        }

        String sql;
        if (aggregation.equalsIgnoreCase("MONTH")) {
            sql = "SELECT ServiceDescription AS service_type, MONTH(ServiceDate) AS month, YEAR(ServiceDate) AS year, SUM(Cost) AS total_revenue "
                    +
                    "FROM services " +
                    "WHERE YEAR(ServiceDate) = ? " +
                    "GROUP BY ServiceDescription, YEAR(ServiceDate), MONTH(ServiceDate) " +
                    "ORDER BY ServiceDescription, YEAR(ServiceDate), MONTH(ServiceDate)";
        } else { // QUARTER
            sql = "SELECT ServiceDescription AS service_type, QUARTER(ServiceDate) AS quarter, YEAR(ServiceDate) AS year, SUM(Cost) AS total_revenue "
                    +
                    "FROM services " +
                    "WHERE YEAR(ServiceDate) = ? " +
                    "GROUP BY ServiceDescription, YEAR(ServiceDate), QUARTER(ServiceDate) " +
                    "ORDER BY ServiceDescription, YEAR(ServiceDate), QUARTER(ServiceDate)";
        }

        Map<String, Map<String, Double>> revenueMap = new HashMap<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, year);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String serviceType = rs.getString("service_type");
                    String period;
                    if (aggregation.equalsIgnoreCase("MONTH")) {
                        int month = rs.getInt("month");
                        period = getMonthName(month);
                    } else { // QUARTER
                        int quarter = rs.getInt("quarter");
                        period = "Q" + quarter;
                    }
                    double revenue = rs.getDouble("total_revenue");

                    revenueMap.computeIfAbsent(serviceType, k -> new HashMap<>()).put(period, revenue);
                }
            }
        }

        return revenueMap;
    }

    /**
     * Retrieves all distinct car models from the database.
     *
     * @return A list of car models.
     * @throws SQLException If a database access error occurs.
     */
    public List<String> getAllCarModels() throws SQLException {
        String sql = "SELECT DISTINCT Model FROM cars ORDER BY Model ASC";
        List<String> models = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                models.add(rs.getString("Model"));
            }
        }

        return models;
    }

    /**
     * Retrieves distinct years from the services data.
     *
     * @return A list of years.
     * @throws SQLException If a database access error occurs.
     */
    public List<Integer> getDistinctServiceYears() throws SQLException {
        String sql = "SELECT DISTINCT YEAR(ServiceDate) AS year FROM services ORDER BY year DESC";
        List<Integer> years = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        }

        return years;
    }

    /**
     * Helper method to convert month number to month name.
     *
     * @param month Number of the month (1-12).
     * @return Name of the month.
     */
    private String getMonthName(int month) {
        return java.time.Month.of(month).name();
    }

    @Override
    protected Object mapRowToObject(ResultSet resultSet) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mapRowToObject'");
    }
}
