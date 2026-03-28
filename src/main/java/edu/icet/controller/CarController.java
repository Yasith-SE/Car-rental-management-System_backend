package edu.icet.controller;

import edu.icet.model.dto.Car;
import edu.icet.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/uploadcars")
    public ResponseEntity<Car> addCar(@ModelAttribute Car carDto) throws IOException {

        // 1. Handle the Image File
        if (carDto.getImageFile() != null && !carDto.getImageFile().isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + carDto.getImageFile().getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(carDto.getImageFile().getInputStream(), filePath);

            // Set the URL that React will use to show the image
            carDto.setImage("http://localhost:8080/uploads/" + fileName);
        }

        Car savedCar = carService.addCar(carDto);
        return new ResponseEntity<>(savedCar, HttpStatus.CREATED);
    }

    @GetMapping("/availablecars")
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully");
    }
}