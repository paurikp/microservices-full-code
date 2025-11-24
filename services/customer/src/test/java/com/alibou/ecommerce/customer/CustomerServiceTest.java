package com.alibou.ecommerce.customer;

import com.alibou.ecommerce.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerService service;

    @Test
    void createCustomer_savesAndReturnsResponse() {
        Address addr = new Address("Main St", "10", "12345");
        CustomerRequest req = new CustomerRequest(null, "John", "Doe", "john@example.com", addr);

        Customer toSave = Customer.builder()
                .id(null)
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .address(addr)
                .build();

        Customer saved = Customer.builder()
                .id("1")
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .address(addr)
                .build();

        CustomerResponse expected = new CustomerResponse("1", "John", "Doe", "john@example.com", addr);

        when(mapper.toCustomer(req)).thenReturn(toSave);
        when(repository.save(toSave)).thenReturn(saved);
        when(mapper.fromCustomer(saved)).thenReturn(expected);

        CustomerResponse result = service.createCustomer(req);

        assertNotNull(result);
        assertEquals("1", result.id());
        assertEquals("John", result.firstname());
        verify(repository).save(toSave);
        verify(mapper).fromCustomer(saved);
    }

    @Test
    void findById_returnsResponse_whenFound() {
        Address addr = new Address("Main St", "10", "12345");
        Customer customer = Customer.builder().id("1").firstname("Jane").lastname("Doe").email("jane@example.com").address(addr).build();
        CustomerResponse resp = new CustomerResponse("1", "Jane", "Doe", "jane@example.com", addr);

        when(repository.findById("1")).thenReturn(Optional.of(customer));
        when(mapper.fromCustomer(customer)).thenReturn(resp);

        CustomerResponse result = service.findById("1");

        assertEquals("1", result.id());
        assertEquals("jane@example.com", result.email());
        verify(repository).findById("1");
    }

    @Test
    void findById_throws_whenNotFound() {
        when(repository.findById("not-exist")).thenReturn(Optional.empty());

        CustomerNotFoundException ex = assertThrows(CustomerNotFoundException.class, () -> service.findById("not-exist"));
        assertTrue(ex.getMessage().contains("No customer found"));
    }

    @Test
    void updateCustomer_mergesAndSaves() {
        Address addr = new Address("Main St", "10", "12345");
        Customer existing = Customer.builder().id("1").firstname("Old").lastname("Name").email("old@example.com").address(addr).build();

        CustomerRequest req = new CustomerRequest("1", "NewFirst", "", "new@example.com", null);

        CustomerResponse resp = new CustomerResponse("1", "NewFirst", "Name", "new@example.com", addr);

        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(mapper.fromCustomer(existing)).thenReturn(resp);

        CustomerResponse result = service.updateCustomer(req);

        // verify fields merged
        assertEquals("NewFirst", existing.getFirstname());
        assertEquals("Name", existing.getLastname()); // blank lastname in request should not overwrite
        assertEquals("new@example.com", existing.getEmail());

        verify(repository).save(existing);
        verify(mapper).fromCustomer(existing);
        assertEquals("1", result.id());
    }

    @Test
    void updateCustomer_throws_whenNotFound() {
        CustomerRequest req = new CustomerRequest("missing", "A", "B", "a@b.com", null);
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.updateCustomer(req));
    }

    @Test
    void findAllCustomers_mapsAll() {
        Address addr = new Address("Main St", "10", "12345");
        Customer c1 = Customer.builder().id("1").firstname("A").lastname("B").email("a@b.com").address(addr).build();
        Customer c2 = Customer.builder().id("2").firstname("C").lastname("D").email("c@d.com").address(addr).build();

        when(repository.findAll()).thenReturn(List.of(c1, c2));
        when(mapper.fromCustomer(c1)).thenReturn(new CustomerResponse("1", "A", "B", "a@b.com", addr));
        when(mapper.fromCustomer(c2)).thenReturn(new CustomerResponse("2", "C", "D", "c@d.com", addr));

        List<CustomerResponse> all = service.findAllCustomers();

        assertEquals(2, all.size());
        verify(repository).findAll();
    }

    @Test
    void existsById_returnsTrueWhenPresent() {
        Customer c = Customer.builder().id("42").firstname("X").lastname("Y").email("x@y.com").build();
        when(repository.findById("42")).thenReturn(Optional.of(c));

        assertTrue(service.existsById("42"));
    }

    @Test
    void deleteCustomer_delegatesToRepository() {
        doNothing().when(repository).deleteById("9");

        service.deleteCustomer("9");

        verify(repository).deleteById("9");
    }
}
