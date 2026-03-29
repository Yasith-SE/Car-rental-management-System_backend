package edu.icet.service.impl;

import edu.icet.model.dto.Car; // This is your DTO
import edu.icet.model.entity.CarEntity; // This is your Database Entity
import edu.icet.repository.CarRepository;
import edu.icet.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    public Car addCar(Car carDto) {
        // 1. Convert DTO (Car) to Entity (CarEntity)
        CarEntity entity = new CarEntity();
        entity.setName(carDto.getName());
        entity.setYear(carDto.getYear());
        entity.setPrice(carDto.getPrice());
        entity.setImage(carDto.getImage());

        // 2. Save Entity to Database
        CarEntity savedEntity = carRepository.save(entity);

        // 3. Convert back to DTO to send to frontend
        return mapToDto(savedEntity);
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Car getCarById(Long id) {
        CarEntity entity = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        return mapToDto(entity);
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    // Helper method for mapping Entity (CarEntity) to DTO (Car)
    private Car mapToDto(CarEntity entity) {
        Car dto = new Car();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setYear(entity.getYear());
        dto.setPrice(entity.getPrice());
        dto.setImage(entity.getImage());
        return dto;
    }
}