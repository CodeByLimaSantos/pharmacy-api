package com.limasantos.pharmacy.api.financial.service;

import com.limasantos.pharmacy.api.financial.dto.CreateFinancialDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialDetailDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialSummaryDTO;
import com.limasantos.pharmacy.api.financial.entity.Financial;
import com.limasantos.pharmacy.api.financial.entity.Financial.FinancialType;
import com.limasantos.pharmacy.api.financial.entity.Financial.PaymentStatus;
import com.limasantos.pharmacy.api.financial.mapper.FinancialMapper;
import com.limasantos.pharmacy.api.financial.repository.FinancialRepository;
import com.limasantos.pharmacy.api.customer.entity.Customer;
import com.limasantos.pharmacy.api.customer.repository.CustomerRepository;
import com.limasantos.pharmacy.api.supplier.entity.Supplier;
import com.limasantos.pharmacy.api.supplier.repository.SupplierRepository;
import com.limasantos.pharmacy.api.shared.enums.PaymentMethod;
import com.limasantos.pharmacy.api.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FinancialService {

    private final FinancialRepository financialRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final FinancialMapper financialMapper;

    public FinancialService(FinancialRepository financialRepository,
                            CustomerRepository customerRepository,
                            SupplierRepository supplierRepository,
                            FinancialMapper financialMapper) {
        this.financialRepository = financialRepository;
        this.customerRepository = customerRepository;
        this.supplierRepository = supplierRepository;
        this.financialMapper = financialMapper;
    }




    // CREATE
    public FinancialDTO createFinancial(CreateFinancialDTO dto) {

        if (dto.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de vencimento não pode ser anterior à data atual");
        }

        Financial financial = financialMapper.toEntity(dto);

        if (dto.getType() == FinancialType.CONTA_A_RECEBER) {

            if (dto.getCustomerId() == null) {
                throw new IllegalArgumentException("Cliente é obrigatório para conta a receber");
            }

            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + dto.getCustomerId()));

            financial.setCustomer(customer);

        } else if (dto.getType() == FinancialType.CONTA_A_PAGAR) {

            if (dto.getSupplierId() == null) {
                throw new IllegalArgumentException("Fornecedor é obrigatório para conta a pagar");
            }

            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + dto.getSupplierId()));

            financial.setSupplier(supplier);
        }

        Financial savedFinancial = financialRepository.save(financial);

        return financialMapper.toDTO(savedFinancial);
    }




    // READ — buscar por ID
    @Transactional(readOnly = true)
    public FinancialDTO findById(Long id) {

        Financial financial = financialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado com ID: " + id));

        return financialMapper.toDTO(financial);
    }




    // READ — detalhes por ID
    @Transactional(readOnly = true)
    public FinancialDetailDTO findDetailById(Long id) {

        Financial financial = financialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado com ID: " + id));

        return financialMapper.convertToDetailDTO(financial);
    }




    // READ — listar todos
    @Transactional(readOnly = true)
    public List<FinancialDTO> findAll() {

        List<Financial> financials = financialRepository.findAll();

        return financialMapper.toDTOList(financials);
    }


    // READ — listar pendentes
    @Transactional(readOnly = true)
    public List<FinancialDTO> findPending() {

        List<Financial> financials = financialRepository.findByStatus(PaymentStatus.PENDING);

        return financialMapper.toDTOList(financials);
    }


    // READ — listar vencidos
    @Transactional(readOnly = true)
    public List<FinancialDTO> findOverdue() {

        List<Financial> financials = financialRepository.findByStatus(PaymentStatus.OVERDUE);

        return financialMapper.toDTOList(financials);
    }


    // READ — listar contas a receber
    @Transactional(readOnly = true)
    public List<FinancialDTO> findReceivable() {

        List<Financial> financials = financialRepository.findByType(FinancialType.CONTA_A_RECEBER);

        return financialMapper.toDTOList(financials);
    }


    // READ — listar contas a pagar
    @Transactional(readOnly = true)
    public List<FinancialDTO> findPayable() {

        List<Financial> financials = financialRepository.findByType(FinancialType.CONTA_A_PAGAR);

        return financialMapper.toDTOList(financials);
    }


    // UPDATE
    public FinancialDTO update(Long id, CreateFinancialDTO dto) {

        Financial financial = financialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado com ID: " + id));

        financial.setDescription(dto.getDescription());
        financial.setAmount(dto.getAmount());
        financial.setDueDate(dto.getDueDate());
        financial.setNotes(dto.getNotes());

        Financial updatedFinancial = financialRepository.save(financial);

        return financialMapper.toDTO(updatedFinancial);
    }


    // MARK AS PAID
    public FinancialDTO markAsPaid(Long id, PaymentMethod paymentMethod) {

        Financial financial = financialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado com ID: " + id));

        if (financial.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Lançamento já foi pago");
        }

        financial.markAsPaid(paymentMethod);

        Financial updatedFinancial = financialRepository.save(financial);

        return financialMapper.toDTO(updatedFinancial);
    }


    // DELETE
    public void delete(Long id) {

        Financial financial = financialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado com ID: " + id));

        if (financial.getStatus() == PaymentStatus.PAID ||
                financial.getStatus() == PaymentStatus.PARTIALLY_PAID) {
            throw new IllegalStateException("Não é possível deletar lançamento com pagamento registrado");
        }

        financialRepository.delete(financial);
    }


    // SUMMARY
    @Transactional(readOnly = true)
    public FinancialSummaryDTO generateSummary() {

        FinancialSummaryDTO summary = new FinancialSummaryDTO();


        //============================================================================================================================================



        // Contas a receber
        List<Financial> receivable = financialRepository.findByType(FinancialType.CONTA_A_RECEBER);


        BigDecimal totalReceivable = receivable.stream()
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal received = receivable.stream()
                .filter(f -> f.getStatus() == PaymentStatus.PAID)
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal pendingReceivable = receivable.stream()
                .filter(f -> f.getStatus() == PaymentStatus.PENDING || f.getStatus() == PaymentStatus.PARTIALLY_PAID)
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal overdueReceivable = receivable.stream()
                .filter(Financial::isOverdue)
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);




      //==============================================================================================================================================


        // Contas a pagar
        List<Financial> payable = financialRepository.findByType(FinancialType.CONTA_A_PAGAR);


        BigDecimal totalPayable = payable.stream()
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paid = payable.stream()
                .filter(f -> f.getStatus() == PaymentStatus.PAID)
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingPayable = payable.stream()
                .filter(f -> f.getStatus() == PaymentStatus.PENDING || f.getStatus() == PaymentStatus.PARTIALLY_PAID)
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overduePayable = payable.stream()
                .filter(Financial::isOverdue)
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        summary.setTotalReceivable(totalReceivable);
        summary.setTotalPayable(totalPayable);
        summary.setTotalReceived(received);
        summary.setTotalPaid(paid);
        summary.setPendingReceivable(pendingReceivable);
        summary.setPendingPayable(pendingPayable);
        summary.setOverdueReceivable(overdueReceivable);
        summary.setOverduePayable(overduePayable);



        summary.setCountPendingReceivable(
                receivable.stream().filter(f -> f.getStatus() == PaymentStatus.PENDING).toList().size()
        );
        summary.setCountPendingPayable(
                payable.stream().filter(f -> f.getStatus() == PaymentStatus.PENDING).toList().size()
        );
        summary.setCountOverdueReceivable(
                receivable.stream().filter(Financial::isOverdue).toList().size()
        );
        summary.setCountOverduePayable(
                payable.stream().filter(Financial::isOverdue).toList().size()
        );

        if (totalReceivable.compareTo(BigDecimal.ZERO) > 0) {
            summary.setReceivedRate((received.doubleValue() / totalReceivable.doubleValue()) * 100);
        }

        if (totalPayable.compareTo(BigDecimal.ZERO) > 0) {
            summary.setPaidRate((paid.doubleValue() / totalPayable.doubleValue()) * 100);
        }

        return summary;
    }




}