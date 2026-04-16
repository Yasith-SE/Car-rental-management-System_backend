package edu.icet.service.impl;

import edu.icet.model.dto.Car;
import edu.icet.model.entity.CarEntity;
import edu.icet.model.entity.User;
import edu.icet.repository.CarRepository;
import edu.icet.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new LinkedHashMap<>();

    static {
        CATEGORY_KEYWORDS.put("sports", List.of(
                "sports", "sport", "coupe", "roadster", "convertible",
                "gtr", "gt-r", "nismo", "350z", "370z", "supra", "mustang", "camaro"
        ));
        CATEGORY_KEYWORDS.put("sedan", List.of(
                "sedan", "saloon", "civic", "corolla", "accord", "camry", "elantra", "sonata"
        ));
        CATEGORY_KEYWORDS.put("suv", List.of(
                "suv", "crossover", "4x4", "fortuner", "prado", "rav4", "cr-v", "sportage", "tucson"
        ));
        CATEGORY_KEYWORDS.put("hatchback", List.of(
                "hatchback", "hatch", "golf", "fit", "jazz", "swift", "yaris", "polo"
        ));
        CATEGORY_KEYWORDS.put("pickup", List.of(
                "pickup", "truck", "hilux", "ranger", "navara", "amarok", "d-max", "dmax"
        ));
        CATEGORY_KEYWORDS.put("luxury", List.of(
                "luxury", "executive", "premium", "maybach", "s-class", "a8", "7 series"
        ));
    }

    private final CarRepository carRepository;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Override
    public Car addCar(Car carDto, User actor) {
        if (carDto.getImageFile() == null || carDto.getImageFile().isEmpty()) {
            throw new RuntimeException("Main vehicle image is required.");
        }

        if (carDto.getModelFile() == null || carDto.getModelFile().isEmpty()) {
            throw new RuntimeException("3D showroom model is required.");
        }

        String heroImageUrl = storeFile(carDto.getImageFile(), "cars/images");
        List<String> galleryUrls = new ArrayList<>();
        galleryUrls.add(heroImageUrl);

        if (carDto.getGalleryFiles() != null) {
            for (MultipartFile galleryFile : carDto.getGalleryFiles()) {
                if (galleryFile != null && !galleryFile.isEmpty()) {
                    galleryUrls.add(storeFile(galleryFile, "cars/images"));
                }
            }
        }

        CarEntity entity = new CarEntity();
        entity.setName(trim(carDto.getName()));
        entity.setYear(trim(carDto.getYear()));
        entity.setPrice(carDto.getPrice() == null ? 0D : carDto.getPrice());
        entity.setCategory(resolveCategory(carDto));
        entity.setImage(heroImageUrl);
        entity.setGalleryImages(String.join(",", galleryUrls));
        entity.setModelUrl(storeFile(carDto.getModelFile(), "cars/models"));
        entity.setShowroomSummary(trim(carDto.getShowroomSummary()));
        entity.setSupportPromptTemplate(trim(carDto.getSupportPromptTemplate()));
        entity.setPartHighlights(trim(carDto.getPartHighlights()));
        entity.setHorsepower(carDto.getHorsepower());
        entity.setTopSpeed(carDto.getTopSpeed());
        entity.setZeroToSixty(carDto.getZeroToSixty());
        entity.setQuarterMile(carDto.getQuarterMile());
        entity.setBrakePower(carDto.getBrakePower());
        entity.setBrakeResponse(carDto.getBrakeResponse());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(actor != null ? actor.getName() : "Admin Upload");

        return mapToDto(carRepository.save(entity));
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll()
                .stream()
                .sorted((left, right) -> {
                    LocalDateTime leftCreated = left.getCreatedAt() == null ? LocalDateTime.MIN : left.getCreatedAt();
                    LocalDateTime rightCreated = right.getCreatedAt() == null ? LocalDateTime.MIN : right.getCreatedAt();
                    return rightCreated.compareTo(leftCreated);
                })
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Car getCarById(Long id) {
        CarEntity entity = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found."));
        return mapToDto(entity);
    }

    @Override
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new RuntimeException("Car not found.");
        }

        carRepository.deleteById(id);
    }

    private String resolveCategory(Car carDto) {
        if (!isBlank(carDto.getCategory()) && !"uncategorized".equalsIgnoreCase(carDto.getCategory())) {
            return normalize(carDto.getCategory());
        }

        String reference = normalize(
                trim(carDto.getName()) + " " +
                        (carDto.getImageFile() != null ? trim(carDto.getImageFile().getOriginalFilename()) : "")
        );

        return CATEGORY_KEYWORDS.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(reference::contains))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("uncategorized");
    }

    private String storeFile(MultipartFile file, String subDirectory) {
        try {
            Path uploadDirectory = Paths.get("src/main/resources/static/uploads").resolve(subDirectory);
            Files.createDirectories(uploadDirectory);

            String originalFileName = file.getOriginalFilename() == null ? "upload-file" : file.getOriginalFilename();
            String cleanFileName = sanitizeFileName(originalFileName);
            String fileName = System.currentTimeMillis() + "_" + cleanFileName;
            Path filePath = uploadDirectory.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return trim(appBaseUrl).replaceAll("/+$", "") + "/uploads/" + subDirectory.replace("\\", "/") + "/" + fileName;
        } catch (IOException exception) {
            throw new RuntimeException("Failed to store uploaded file.");
        }
    }

    private Car mapToDto(CarEntity entity) {
        Car dto = new Car();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setYear(entity.getYear());
        dto.setPrice(entity.getPrice());
        dto.setCategory(entity.getCategory());
        dto.setImage(entity.getImage());
        dto.setGalleryImages(entity.getGalleryImages());
        dto.setModelUrl(entity.getModelUrl());
        dto.setShowroomSummary(trim(entity.getShowroomSummary()));
        dto.setSupportPromptTemplate(trim(entity.getSupportPromptTemplate()));
        dto.setPartHighlights(trim(entity.getPartHighlights()));
        dto.setHorsepower(entity.getHorsepower());
        dto.setTopSpeed(entity.getTopSpeed());
        dto.setZeroToSixty(entity.getZeroToSixty());
        dto.setQuarterMile(entity.getQuarterMile());
        dto.setBrakePower(entity.getBrakePower());
        dto.setBrakeResponse(entity.getBrakeResponse());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(trim(entity.getCreatedBy()));
        return dto;
    }

    private String sanitizeFileName(String fileName) {
        return trim(fileName).replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }

    private String normalize(String value) {
        return trim(value).toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return trim(value).isEmpty();
    }
}
