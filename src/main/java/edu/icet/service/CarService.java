package edu.icet.service;

import edu.icet.model.dto.Car;

import java.util.List;

public interface CarService {
    Car addCar(Car carDto);
    List<Car> getAllCars();
    Car getCarById(Long id);
    void deleteCar(Long id);

}
