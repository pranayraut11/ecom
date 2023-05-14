package com.ecom.user.service.implementation;

import com.ecom.shared.common.exception.EcomException;
import com.ecom.user.entity.UserDetails;
import com.ecom.user.model.Address;
import com.ecom.user.repository.UserRepository;
import com.ecom.user.service.specification.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    private UserRepository userRepository;

    public AddressServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void create(@NotNull Address address) {
        UserDetails userDetails = getUser();
        if (StringUtils.isBlank(address.getId())) {
            address.setId(UUID.randomUUID().toString());
        }
        if (CollectionUtils.isEmpty(userDetails.getAddresses())) {
            userDetails.setAddresses(Collections.singleton(address));
        }else{
            userDetails.getAddresses().add(address);
        }
        userRepository.save(userDetails);
        log.info("Address saved/updated successfully for user {} ... ",userDetails.getUserId());
    }

    private UserDetails getUser(){
        String userId = com.ecom.shared.common.dto.UserDetails.getUserId();
        log.info("Saving address for user {} ... ",userId);
        return userRepository.findByUserId(userId).orElseThrow(() -> new EcomException(HttpStatus.NOT_FOUND, "404"));
    }

    @Override
    public Set<Address> get() {
       return getUser().getAddresses();
    }

    @Override
    public Address get(String id) {
        return getUser().getAddresses().stream().filter(address -> address.getId().equals(id)).findFirst().orElseThrow(() -> new EcomException(HttpStatus.NOT_FOUND, "404"));
    }

    @Override
    public void delete(String id) {
       getUser().getAddresses().remove(get(id));
    }
}
