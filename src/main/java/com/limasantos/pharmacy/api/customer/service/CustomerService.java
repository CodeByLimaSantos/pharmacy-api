package com.limasantos.pharmacy.api.customer.service;

import com.limasantos.pharmacy.api.customer.dto.CreateCustomerDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerDetailDTO;
import com.limasantos.pharmacy.api.customer.entity.Customer;
import com.limasantos.pharmacy.api.customer.mapper.CustomerMapper;
import com.limasantos.pharmacy.api.customer.repository.CustomerRepository;
import com.limasantos.pharmacy.api.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }


    // CREATE
    public CustomerDTO createCustomer(CreateCustomerDTO dto) {

        String normalizedCpf = normalizeCpf(dto.getCpf());
        dto.setCpf(normalizedCpf);

        if (customerRepository.existsByCpf(normalizedCpf)) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }

        Customer customer = customerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer); // ✅ usa savedCustomer com id gerado

        return customerMapper.toDTO(savedCustomer);
    }


    // READ — buscar por ID
    @Transactional(readOnly = true)
    public CustomerDTO findById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        return customerMapper.toDTO(customer);
    }


    // READ — detalhes por ID
    @Transactional(readOnly = true)
    public CustomerDetailDTO findDetailById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        return customerMapper.toDetailDTO(customer);
    }


    // READ — listar todos
    @Transactional(readOnly = true)
    public List<CustomerDTO> findAll() {

        List<Customer> customers = customerRepository.findAll();

        return customerMapper.toDTOList(customers);
    }


    // READ — buscar por CPF
    @Transactional(readOnly = true)
    public CustomerDTO findByCpf(String cpf) {

        String normalizedCpf = normalizeCpf(cpf);

        Customer customer = customerRepository.findByCpf(normalizedCpf)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com CPF: " + normalizedCpf));

        return customerMapper.toDTO(customer);
    }


    // UPDATE
    public CustomerDTO update(Long id, CreateCustomerDTO dto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        String normalizedCpf = normalizeCpf(dto.getCpf());
        dto.setCpf(normalizedCpf);

        if (!customer.getCpf().equals(normalizedCpf) &&
                customerRepository.existsByCpf(normalizedCpf)) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }

        customer.setName(dto.getName());
        customer.setCpf(normalizedCpf);
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);

        return customerMapper.toDTO(updatedCustomer);
    }


    // DELETE
    public void delete(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        if (!customer.getPurchaseHistory().isEmpty()) {
            throw new IllegalStateException("Não é possível deletar cliente com histórico de compras");
        }

        customerRepository.delete(customer);
    }

    private String normalizeCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }

}