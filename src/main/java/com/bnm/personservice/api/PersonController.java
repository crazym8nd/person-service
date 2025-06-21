package com.bnm.personservice.api;

import com.bnm.personservice.entity.AddressEntity;
import com.bnm.personservice.entity.CountryEntity;
import com.bnm.personservice.entity.UserEntity;
import com.bnm.personservice.mapper.AddressMapper;
import com.bnm.personservice.mapper.CountryMapper;
import com.bnm.personservice.mapper.IndividualMapper;
import com.bnm.personservice.mapper.UserMapper;
import com.bnm.personservice.model.*;
import com.bnm.personservice.service.AddressService;
import com.bnm.personservice.service.CountryService;
import com.bnm.personservice.service.IndividualService;
import com.bnm.personservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PersonController implements PersonApi {

    private final CountryService countryService;
    private final CountryMapper countryMapper;
    private final AddressService addressService;
    private final AddressMapper addressMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final IndividualService individualService;
    private final IndividualMapper individualMapper;

    @Override
    public ResponseEntity<List<CountryResponse>> getCountries() {
        return ResponseEntity.ok(
                countryService.findAll().stream()
                        .map(countryMapper::toResponse)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<CountryResponse> getCountryById(final Integer id) {
        return ResponseEntity.ok(
                countryMapper.toResponse(countryService.findById(id.longValue()))
        );
    }

    @Override
    public ResponseEntity<CountryResponse> createCountry(final CountryRequest countryRequest) {
        return ResponseEntity.ok(
                countryMapper.toResponse(
                        countryService.create(countryMapper.toEntity(countryRequest))
                )
        );
    }

    @Override
    public ResponseEntity<CountryResponse> updateCountry(final Integer id,
                                                         final CountryRequest countryRequest) {
        return ResponseEntity.ok(
                countryMapper.toResponse(
                        countryService.update(id.longValue(), countryMapper.toEntity(countryRequest))
                )
        );
    }

    @Override
    public ResponseEntity<Void> deleteCountry(final Integer id) {
        countryService.delete(id.longValue());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        return ResponseEntity.ok(
                addressService.findAll().stream()
                        .map(addressMapper::toResponse)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<AddressResponse> createAddress(final AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.createAddress(addressRequest));
    }

    @Override
    public ResponseEntity<AddressResponse> getAddressById(final UUID id) {
        return ResponseEntity.ok(
                addressMapper.toResponse(addressService.findById(id))
        );
    }

    @Override
    public ResponseEntity<AddressResponse> updateAddress(final UUID id, final AddressRequest addressRequest) {
        final CountryEntity country = countryService.findById(
                addressRequest.getCountryId().longValue());
        return ResponseEntity.ok(
                addressMapper.toResponse(
                        addressService.update(id, addressMapper.toEntity(addressRequest, country))
                )
        );
    }

    @Override
    public ResponseEntity<Void> deleteAddress(final UUID id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(
                userService.findAll().stream()
                        .map(userMapper::toResponse)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<UserResponse> createUser(final UserRequest userRequest) {
        final AddressEntity address = addressService.findById(
                userRequest.getAddressId());
        return ResponseEntity.ok(
                userMapper.toResponse(
                        userService.create(userMapper.toEntity(userRequest, address))
                )
        );
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(final UUID id) {
        return ResponseEntity.ok(
                userMapper.toResponse(userService.findById(id))
        );
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(final UUID id, final UserRequest userRequest) {
        final AddressEntity address = addressService.findById(
                userRequest.getAddressId());
        return ResponseEntity.ok(
                userMapper.toResponse(
                        userService.update(id, userMapper.toEntity(userRequest, address))
                )
        );
    }

    @Override
    public ResponseEntity<Void> deleteUser(final UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserResponse> updateUserVerificationStatus(final UUID id,
                                                                     final UserVerificationRequest userVerificationRequest) {
        return ResponseEntity.ok(
                userMapper.toResponse(
                        userService.updateVerificationStatus(id, userVerificationRequest.getStatus())
                )
        );
    }

    @Override
    public ResponseEntity<List<IndividualResponse>> getIndividuals() {
        return ResponseEntity.ok(
                individualService.findAll().stream()
                        .map(individualMapper::toResponse)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<IndividualResponse> createIndividual(final IndividualRequest individualRequest) {
        final UserEntity user = userService.findById(
                individualRequest.getUserId());
        return ResponseEntity.ok(
                individualMapper.toResponse(
                        individualService.create(individualMapper.toEntity(individualRequest, user))
                )
        );
    }

    @Override
    public ResponseEntity<IndividualResponse> getIndividualById(final UUID id) {
        return ResponseEntity.ok(
                individualMapper.toResponse(individualService.findById(id))
        );
    }

    @Override
    public ResponseEntity<IndividualResponse> updateIndividual(final UUID id,
                                                               final IndividualRequest individualRequest) {
        final UserEntity user = userService.findById(
                individualRequest.getUserId());
        return ResponseEntity.ok(
                individualMapper.toResponse(
                        individualService.update(id, individualMapper.toEntity(individualRequest, user))
                )
        );
    }

    @Override
    public ResponseEntity<Void> deleteIndividual(final UUID id) {
        individualService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 