package com.bnm.personservice.service;

import com.bnm.personservice.domain.Address;
import com.bnm.personservice.mapper.persistence.AddressPersistenceMapper;
import com.bnm.personservice.mapper.persistence.CountryPersistenceMapper;
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
    private final AddressPersistenceMapper addressMapper;
    private final CountryPersistenceMapper countryPersistenceMapper;

    @Transactional(readOnly = true)
    public List<Address> findAll() {
        return addressRepository.findAllWithCountry().stream()
                .map(addressMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Address findById(final UUID id) {
        return addressRepository.findByIdWithCountry(id)
                .map(addressMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
    }

    @Transactional
    public void delete(final UUID id) {
        addressRepository.deleteById(id);
    }

    @Transactional
    public Address create(final Address address) {
        final var entity = addressMapper.toEntity(address);
        final var savedEntity = addressRepository.save(entity);
        return addressMapper.toDomain(savedEntity);
    }

    @Transactional
    public Address update(final UUID id, final Address address) {
        final var existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
        existingAddress.setAddress(address.getAddress());
        existingAddress.setZipCode(address.getZipCode());
        existingAddress.setCity(address.getCity());
        existingAddress.setState(address.getState());
        existingAddress.setCountry(address.getCountry() != null ?
                countryPersistenceMapper.toEntity(address.getCountry()) : null);
        final var savedEntity = addressRepository.save(existingAddress);
        return addressMapper.toDomain(savedEntity);
    }
} 