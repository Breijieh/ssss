//abstract view of dao just needed methods
package com.example.service;

import com.example.dao.CarDAO;
import com.example.model.Car;
import com.example.model.CarServiceRecord;
import com.example.util.ExportUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CarService {
    private CarDAO carDAO;
    private ExportUtil exportUtil;

    public CarService() {
        this.carDAO = new CarDAO();
        this.exportUtil = new ExportUtil();
    }

    public Map<String, String> getColumnNamesAndTypes(String tableName) throws SQLException {
        return carDAO.getColumnNamesAndTypes(tableName);
    }

    public List<Car> getAllCars() throws SQLException {
        return carDAO.getAllCars();
    }

    public boolean updateCar(Car car) throws SQLException {
        return carDAO.updateCar(car);
    }

    public boolean insertCar(Car car) throws SQLException {
        return carDAO.insertCar(car);
    }

    public boolean deleteCar(int carID) throws SQLException {
        return carDAO.deleteCar(carID);
    }

    // from util
    public void exportCarsToCSV(List<Car> cars, File destination) throws IOException {
        exportUtil.exportCarsToCSV(cars, destination);
    }

    public List<CarServiceRecord> getServicesForCar(int carID) throws SQLException {
        return carDAO.getServicesForCar(carID);
    }

    public Car getCarById(int carID) throws SQLException {
        return carDAO.getCarById(carID);
    }
}
