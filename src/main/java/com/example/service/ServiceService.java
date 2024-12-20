package com.example.service;

import com.example.dao.ServiceDAO;
import com.example.model.Service;
import com.example.util.ExportUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ServiceService {
    private ServiceDAO serviceDAO;
    private ExportUtil exportUtil;

    public ServiceService() {
        this.serviceDAO = new ServiceDAO();
        this.exportUtil = new ExportUtil();
    }

    public Map<String, String> getColumnNamesAndTypes(String tableName) throws SQLException {
        return serviceDAO.getColumnNamesAndTypes(tableName);
    }

    public List<Service> getAllServices() throws SQLException {
        return serviceDAO.getAllServices();
    }

    public boolean updateService(Service service) throws SQLException {
        return serviceDAO.updateService(service);
    }

    public boolean insertService(Service service) throws SQLException {
        return serviceDAO.insertService(service);
    }

    public boolean deleteService(int serviceID) throws SQLException {
        return serviceDAO.deleteService(serviceID);
    }

    public void exportServicesToCSV(List<Service> services, File destination) throws IOException {
        exportUtil.exportServicesToCSV(services, destination);
    }

    // Additional methods to retrieve related data if needed
}
