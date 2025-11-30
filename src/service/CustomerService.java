package service;

import model.Customer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomerService {
    private static CustomerService instance;
    private final Map<String, Customer> customers = new HashMap<>();

    private CustomerService() {}

    public static CustomerService getInstance() {
        if (instance == null) instance = new CustomerService();
        return instance;
    }

    // register new customer; false if already exists
    public boolean registerCustomer(String email, String firstName, String lastName) {
        String key = email.toLowerCase();
        if (customers.containsKey(key)) return false;
        Customer customer = new Customer(firstName, lastName, key);
        customers.put(key, customer);
        return true;
    }

    public Customer fetchCustomer(String email) {
        if (email == null) return null;
        return customers.get(email.toLowerCase());
    }

    public Collection<Customer> listAllCustomers() {
        return customers.values();
    }
}
