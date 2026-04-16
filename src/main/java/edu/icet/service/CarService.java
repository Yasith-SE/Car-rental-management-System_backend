package edu.icet.service;

import edu.icet.model.dto.Car;
import edu.icet.model.entity.User;

import java.util.List;

public interface CarService {
    Car addCar(Car carDto, User actor);
    List<Car> getAllCars();
    Car getCarById(Long id);
    void deleteCar(Long id);

}
