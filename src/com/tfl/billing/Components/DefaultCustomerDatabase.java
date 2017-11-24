package com.tfl.billing.Components;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

/**
 * Created by marcin on 24.11.17.
 */
public class DefaultCustomerDatabase implements ICustomerDatabase {
    private final CustomerDatabase database;

    public DefaultCustomerDatabase() {
        this.database = CustomerDatabase.getInstance();
    }

    @Override
    public List<Customer> getCustomers() {
        return database.getCustomers();
    }

    @Override
    public boolean isRegisteredId(UUID customer) {
        return database.isRegisteredId(customer);
    }
}
