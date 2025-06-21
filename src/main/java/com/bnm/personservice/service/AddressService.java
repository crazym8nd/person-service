package com.bnm.personservice.service;

import com.bnm.personservice.entity.AddressEntity;
import com.bnm.personservice.entity.CountryEntity;
import com.bnm.personservice.mapper.AddressMapper;
import com.bnm.personservice.model.AddressRequest;
import com.bnm.personservice.model.AddressResponse;
import com.bnm.personservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final CountryService countryService;
    private final AddressMapper addressMapper;

    @Transactional(readOnly = true)
    public List<AddressEntity> findAll() {
        return addressRepository.findAllWithCountry();
    }

    @Transactional(readOnly = true)
    public AddressEntity findById(final UUID id) {
        return addressRepository.findByIdWithCountry(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
    }

    @Transactional
    public AddressResponse createAddress(final AddressRequest addressRequest) {
        final CountryEntity country = countryService.findById(
                addressRequest.getCountryId().longValue());
        final AddressEntity address = addressMapper.toEntity(addressRequest, country);
        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(final UUID id) {
        addressRepository.deleteById(id);
    }

    @Transactional
    public AddressEntity update(final UUID id, final AddressEntity address) {
        final AddressEntity existingAddress = findById(id);
        existingAddress.setAddress(address.getAddress());
        existingAddress.setZipCode(address.getZipCode());
        existingAddress.setCity(address.getCity());
        existingAddress.setState(address.getState());
        existingAddress.setCountry(address.getCountry());
        return addressRepository.save(existingAddress);
    }
} 