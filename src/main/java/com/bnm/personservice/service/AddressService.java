package com.bnm.personservice.service;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.entity.Country;
import com.bnm.personservice.mapper.AddressMapper;
import com.bnm.personservice.model.AddressCreate;
import com.bnm.personservice.repository.AddressRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

  private final AddressRepository addressRepository;
  private final CountryService countryService;
  private final AddressMapper addressMapper;

  @Transactional(readOnly = true)
  public List<Address> findAll() {
    return addressRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Address findById(final UUID id) {
    return addressRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
  }

  @Transactional
  public Address create(final Address address) {
    return addressRepository.save(address);
  }

  @Transactional
  public com.bnm.personservice.model.Address createAddress(final AddressCreate addressCreate) {
    final Country country = countryService.findById(
        addressCreate.getCountryId().longValue());
    final Address address = addressMapper.toEntity(addressCreate, country);
    return addressMapper.toDto(addressRepository.save(address));
  }

  @Transactional
  public Address updateAddress(final UUID id, final AddressCreate addressCreate) {
    final Address existingAddress = findById(id);
    existingAddress.setAddress(addressCreate.getAddress());
    existingAddress.setZipCode(addressCreate.getZipCode());
    existingAddress.setCity(addressCreate.getCity());
    existingAddress.setState(addressCreate.getState());
    return addressRepository.save(existingAddress);
  }

  @Transactional
  public void deleteAddress(final UUID id) {
    addressRepository.deleteById(id);
  }
} 