package com.limasantos.pharmacy.api.supplier.service;

import com.limasantos.pharmacy.api.supplier.dto.CreateSupplierDTO;
import com.limasantos.pharmacy.api.supplier.dto.SupplierDTO;
import com.limasantos.pharmacy.api.supplier.dto.SupplierDetailDTO;
import com.limasantos.pharmacy.api.supplier.entity.Supplier;
import com.limasantos.pharmacy.api.supplier.mapper.SupplierMapper;
import com.limasantos.pharmacy.api.supplier.repository.SupplierRepository;
import com.limasantos.pharmacy.api.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    
    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }




    // CREATE
    public SupplierDTO createSupplier(CreateSupplierDTO dto) {

        // Validar CNPJ duplicado
        if (supplierRepository.existsByCnpj(dto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado no sistema");
        }
        
        Supplier supplier = supplierMapper.toEntity(dto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDTO(savedSupplier);
    }





    //SEARCH BY ID
    @Transactional(readOnly = true)
    public SupplierDTO findById(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));

        return supplierMapper.toDTO(supplier);


    }
    
   //SEARCH DETAIL BY ID
    @Transactional(readOnly = true)
    public SupplierDetailDTO findDetailById(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));



        return supplierMapper.toDetailDTO(supplier);
    }
    


    //SHOW ALL
    @Transactional(readOnly = true)
    public List<SupplierDTO> findAll() {

        List<Supplier> suppliers = supplierRepository.findAll();

        return supplierMapper.toDTOList(suppliers);



    }
    




    //UPDATE
    public SupplierDTO update(Long id, CreateSupplierDTO dto) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));
        
        // Validar CNPJ não muda (CNPJ é imutável em dados cadastrais)
        // Se implementar atualização de CNPJ, adicionar validação aqui
        
        supplier.setName(dto.getName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        
        Supplier updatedSupplier = supplierRepository.save(supplier);

        return supplierMapper.toDTO(updatedSupplier);



    }
    


    //DELETE
    public void delete(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));
        
        // Validar se tem produtos
        if (!supplier.getProducts().isEmpty()) {
            throw new IllegalStateException("Não é possível deletar fornecedor com produtos cadastrados");
        }
        
        supplierRepository.delete(supplier);
    }
    

   //SHOW SUPPLIER BY CNPJ
    @Transactional(readOnly = true)
    public SupplierDTO findByCnpj(String cnpj) {

        Supplier supplier = supplierRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com CNPJ: " + cnpj));


        return supplierMapper.toDTO(supplier);


    }
}

