package com.tfl.billing.Components;

import com.tfl.external.Customer;

import java.util.List;
import java.util.UUID;

/**
 * Created by marcin on 24.11.17.
 */
public interface ICustomerDatabase {
    List<Customer> getCustomers();
    boolean isRegisteredId(UUID customer);
}
