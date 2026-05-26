package com.cruise.service;

import com.cruise.dto.cruise.CreateCruiseRequest;
import com.cruise.dto.cruise.CruiseResponse;
import com.cruise.entity.Cruise;
import com.cruise.entity.CruiseType;
import com.cruise.entity.Ship;
import com.cruise.exception.ApiException;
import com.cruise.repository.CruiseRepository;
import com.cruise.repository.CruiseTypeRepository;
import com.cruise.repository.ShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CruiseService {

    private final CruiseRepository cruiseRepository;
    private final CruiseTypeRepository cruiseTypeRepository;
    private final ShipRepository shipRepository;

    // Все доступные круизы (публичный эндпоинт)
    public List<CruiseResponse> getAllAvailable() {
        return cruiseRepository.findAllAvailable()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Все круизы включая неактивные (для ADMIN/OWNER)
    public List<CruiseResponse> getAll() {
        return cruiseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Получить круиз по id
    public CruiseResponse getById(Integer id) {
        Cruise cruise = cruiseRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Круиз с id=" + id + " не найден"));
        return toResponse(cruise);
    }

    // Создать рейс (ADMIN/OWNER)
    @Transactional
    public CruiseResponse create(CreateCruiseRequest request) {
        if (request.getDepartureDate().isAfter(request.getReturnDate())) {
            throw ApiException.badRequest("Дата отправления не может быть позже даты возвращения");
        }

        CruiseType type = cruiseTypeRepository.findById(request.getCruiseTypeId())
                .orElseThrow(() -> ApiException.notFound("Тип круиза не найден"));

        Ship ship = shipRepository.findById(request.getShipId())
                .orElseThrow(() -> ApiException.notFound("Корабль не найден"));

        if (request.getAvailableSeats() > ship.getCapacity()) {
            throw ApiException.badRequest("Мест не может быть больше вместимости корабля ("
                    + ship.getCapacity() + ")");
        }

        Cruise cruise = new Cruise();
        cruise.setCruiseType(type);
        cruise.setShip(ship);
        cruise.setDepartureDate(request.getDepartureDate());
        cruise.setReturnDate(request.getReturnDate());
        cruise.setAvailableSeats(request.getAvailableSeats());
        cruise.setIsActive(true);

        return toResponse(cruiseRepository.save(cruise));
    }

    // Обновить рейс (ADMIN/OWNER)
    @Transactional
    public CruiseResponse update(Integer id, CreateCruiseRequest request) {
        Cruise cruise = cruiseRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Круиз с id=" + id + " не найден"));

        CruiseType type = cruiseTypeRepository.findById(request.getCruiseTypeId())
                .orElseThrow(() -> ApiException.notFound("Тип круиза не найден"));

        Ship ship = shipRepository.findById(request.getShipId())
                .orElseThrow(() -> ApiException.notFound("Корабль не найден"));

        cruise.setCruiseType(type);
        cruise.setShip(ship);
        cruise.setDepartureDate(request.getDepartureDate());
        cruise.setReturnDate(request.getReturnDate());
        cruise.setAvailableSeats(request.getAvailableSeats());

        return toResponse(cruiseRepository.save(cruise));
    }

    // Деактивировать рейс (OWNER)
    @Transactional
    public void deactivate(Integer id) {
        Cruise cruise = cruiseRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Круиз с id=" + id + " не найден"));
        cruise.setIsActive(false);
        cruiseRepository.save(cruise);
    }

    // Все типы круизов
    public List<CruiseType> getAllTypes() {
        return cruiseTypeRepository.findAll();
    }

    // Маппинг entity → DTO
    public CruiseResponse toResponse(Cruise cruise) {
        CruiseResponse dto = new CruiseResponse();
        dto.setId(cruise.getId());
        dto.setCruiseType(cruise.getCruiseType().getName());
        dto.setDescription(cruise.getCruiseType().getDescription());
        dto.setDurationDays(cruise.getCruiseType().getDurationDays());
        dto.setShipName(cruise.getShip().getName());
        dto.setDepartureDate(cruise.getDepartureDate());
        dto.setReturnDate(cruise.getReturnDate());
        dto.setAvailableSeats(cruise.getAvailableSeats());
        dto.setBasePrice(cruise.getCruiseType().getBasePrice());
        dto.setImageUrl(cruise.getCruiseType().getImageUrl());
        dto.setIsActive(cruise.getIsActive());
        return dto;
    }
}