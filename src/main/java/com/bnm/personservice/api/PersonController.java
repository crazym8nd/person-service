package com.bnm.personservice.api;

import com.bnm.personservice.mapper.api.AddressApiMapper;
import com.bnm.personservice.mapper.api.CountryApiMapper;
import com.bnm.personservice.mapper.api.IndividualApiMapper;
import com.bnm.personservice.mapper.api.UserApiMapper;
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
    private final CountryApiMapper countryMapper;
    private final AddressService addressService;
    private final AddressApiMapper addressMapper;
    private final UserService userService;
    private final UserApiMapper userMapper;
    private final IndividualService individualService;
    private final IndividualApiMapper individualMapper;

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
        final var domain = countryMapper.toDomain(countryRequest);
        final var createdCountry = countryService.create(domain);
        return ResponseEntity.ok(countryMapper.toResponse(createdCountry));
    }

    @Override
    public ResponseEntity<CountryResponse> updateCountry(final Integer id,
                                                         final CountryRequest countryRequest) {
        final var domain = countryMapper.toDomain(countryRequest);
        final var updatedCountry = countryService.update(id.longValue(), domain);
        return ResponseEntity.ok(countryMapper.toResponse(updatedCountry));
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
        final var country = countryService.findById(addressRequest.getCountryId().longValue());
        final var domain = addressMapper.toDomain(addressRequest, country);
        final var createdAddress = addressService.create(domain);
        return ResponseEntity.ok(addressMapper.toResponse(createdAddress));
    }

    @Override
    public ResponseEntity<AddressResponse> getAddressById(final UUID id) {
        return ResponseEntity.ok(
                addressMapper.toResponse(addressService.findById(id))
        );
    }

    @Override
    public ResponseEntity<AddressResponse> updateAddress(final UUID id, final AddressRequest addressRequest) {
        final var country = countryService.findById(addressRequest.getCountryId().longValue());
        final var domain = addressMapper.toDomain(addressRequest, country);
        final var updatedAddress = addressService.update(id, domain);
        return ResponseEntity.ok(addressMapper.toResponse(updatedAddress));
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
        final var address = addressService.findById(userRequest.getAddressId());
        final var domain = userMapper.toDomain(userRequest, address);
        final var createdUser = userService.create(domain);
        return ResponseEntity.ok(userMapper.toResponse(createdUser));
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(final UUID id) {
        return ResponseEntity.ok(
                userMapper.toResponse(userService.findById(id))
        );
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(final UUID id, final UserRequest userRequest) {
        final var address = addressService.findById(userRequest.getAddressId());
        final var domain = userMapper.toDomain(userRequest, address);
        final var updatedUser = userService.update(id, domain);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @Override
    public ResponseEntity<Void> deleteUser(final UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserResponse> updateUserVerificationStatus(final UUID id,
                                                                     final UserVerificationRequest userVerificationRequest) {
        final var updatedUser = userService.updateVerificationStatus(id, userVerificationRequest.getStatus());
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
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
        final var domain = individualMapper.toDomain(individualRequest);
        final var createdIndividual = individualService.create(domain, individualRequest.getUserId());
        return ResponseEntity.ok(individualMapper.toResponse(createdIndividual));
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
        final var domain = individualMapper.toDomain(individualRequest);
        final var updatedIndividual = individualService.update(id, domain);
        return ResponseEntity.ok(individualMapper.toResponse(updatedIndividual));
    }

    @Override
    public ResponseEntity<Void> deleteIndividual(final UUID id) {
        individualService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 