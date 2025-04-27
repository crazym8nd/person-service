package com.bnm.personservice.api;

import com.bnm.personservice.mapper.AddressMapper;
import com.bnm.personservice.mapper.CountryMapper;
import com.bnm.personservice.mapper.IndividualMapper;
import com.bnm.personservice.mapper.UserMapper;
import com.bnm.personservice.model.Address;
import com.bnm.personservice.model.AddressCreate;
import com.bnm.personservice.model.Country;
import com.bnm.personservice.model.CountryCreate;
import com.bnm.personservice.model.Individual;
import com.bnm.personservice.model.IndividualCreate;
import com.bnm.personservice.model.User;
import com.bnm.personservice.model.UserCreate;
import com.bnm.personservice.model.UserVerificationUpdate;
import com.bnm.personservice.service.AddressService;
import com.bnm.personservice.service.CountryService;
import com.bnm.personservice.service.IndividualService;
import com.bnm.personservice.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PersonApiImpl implements DefaultApi {

  private final CountryService countryService;
  private final CountryMapper countryMapper;
  private final AddressService addressService;
  private final AddressMapper addressMapper;
  private final UserService userService;
  private final UserMapper userMapper;
  private final IndividualService individualService;
  private final IndividualMapper individualMapper;

  @Override
  public ResponseEntity<List<Country>> getCountries() {
    return ResponseEntity.ok(
        countryService.findAll().stream()
            .map(countryMapper::toDto)
            .toList()
    );
  }

  @Override
  public ResponseEntity<Country> getCountryById(final Integer id) {
    return ResponseEntity.ok(
        countryMapper.toDto(countryService.findById(id.longValue()))
    );
  }

  @Override
  public ResponseEntity<Country> createCountry(final CountryCreate countryCreate) {
    return ResponseEntity.ok(
        countryMapper.toDto(
            countryService.create(countryMapper.toEntity(countryCreate))
        )
    );
  }

  @Override
  public ResponseEntity<Country> updateCountry(final Integer id, final CountryCreate countryCreate) {
    return ResponseEntity.ok(
        countryMapper.toDto(
            countryService.update(id.longValue(), countryMapper.toEntity(countryCreate))
        )
    );
  }

  @Override
  public ResponseEntity<Void> deleteCountry(final Integer id) {
    countryService.delete(id.longValue());
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<Address>> getAddresses() {
    return ResponseEntity.ok(
        addressService.findAll().stream()
            .map(addressMapper::toDto)
            .toList()
    );
  }

  @Override
  public ResponseEntity<Address> createAddress(final AddressCreate addressCreate) {
    return ResponseEntity.ok(addressService.createAddress(addressCreate));
  }

  @Override
  public ResponseEntity<Address> getAddressById(final UUID id) {
    return ResponseEntity.ok(
        addressMapper.toDto(addressService.findById(id))
    );
  }

  @Override
  public ResponseEntity<Address> updateAddress(final UUID id, final AddressCreate addressCreate) {
    final com.bnm.personservice.entity.Country country = countryService.findById(
        addressCreate.getCountryId().longValue());
    return ResponseEntity.ok(
        addressMapper.toDto(
            addressService.update(id, addressMapper.toEntity(addressCreate,country))
        )
    );
  }

  @Override
  public ResponseEntity<Void> deleteAddress(final UUID id) {
    addressService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok(
        userService.findAll().stream()
            .map(userMapper::toDto)
            .toList()
    );
  }

  @Override
  public ResponseEntity<User> createUser(final UserCreate userCreate) {
    final com.bnm.personservice.entity.Address address = addressService.findById(
        userCreate.getAddressId());
    return ResponseEntity.ok(
        userMapper.toDto(
            userService.create(userMapper.toEntity(userCreate, address))
        )
    );
  }

  @Override
  public ResponseEntity<User> getUserById(final UUID id) {
    return ResponseEntity.ok(
        userMapper.toDto(userService.findById(id))
    );
  }

  @Override
  public ResponseEntity<User> updateUser(final UUID id, final UserCreate userCreate) {
    final com.bnm.personservice.entity.Address address = addressService.findById(userCreate.getAddressId());
    return ResponseEntity.ok(
        userMapper.toDto(
            userService.update(id, userMapper.toEntity(userCreate, address))
        )
    );
  }

  @Override
  public ResponseEntity<Void> deleteUser(final UUID id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<User> updateUserVerificationStatus(final UUID id,
      final UserVerificationUpdate userVerificationUpdate) {
    return ResponseEntity.ok(
        userMapper.toDto(
            userService.updateVerificationStatus(id, userVerificationUpdate.getStatus())
        )
    );
  }

  @Override
  public ResponseEntity<List<Individual>> getIndividuals() {
    return ResponseEntity.ok(
        individualService.findAll().stream()
            .map(individualMapper::toDto)
            .toList()
    );
  }

  @Override
  public ResponseEntity<Individual> createIndividual(final IndividualCreate individualCreate) {
    final com.bnm.personservice.entity.User user = userService.findById(individualCreate.getUserId());
    return ResponseEntity.ok(
        individualMapper.toDto(
            individualService.create(individualMapper.toEntity(individualCreate, user))
        )
    );
  }

  @Override
  public ResponseEntity<Individual> getIndividualById(final UUID id) {
    return ResponseEntity.ok(
        individualMapper.toDto(individualService.findById(id))
    );
  }

  @Override
  public ResponseEntity<Individual> updateIndividual(final UUID id, final IndividualCreate individualCreate) {
    final com.bnm.personservice.entity.User user = userService.findById(individualCreate.getUserId());
    return ResponseEntity.ok(
        individualMapper.toDto(
            individualService.update(id, individualMapper.toEntity(individualCreate, user))
        )
    );
  }

  @Override
  public ResponseEntity<Void> deleteIndividual(final UUID id) {
    individualService.delete(id);
    return ResponseEntity.noContent().build();
  }
} 