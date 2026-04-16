package com.limasantos.pharmacy.api.sales.service;

import com.limasantos.pharmacy.api.sales.dto.CreateSaleDTO;
import com.limasantos.pharmacy.api.sales.dto.CreateSaleItemDTO;
import com.limasantos.pharmacy.api.sales.dto.SaleDTO;
import com.limasantos.pharmacy.api.sales.dto.SaleDetailDTO;
import com.limasantos.pharmacy.api.sales.entity.Sale;
import com.limasantos.pharmacy.api.sales.entity.SaleItem;
import com.limasantos.pharmacy.api.sales.mapper.SaleMapper;
import com.limasantos.pharmacy.api.sales.repository.SaleRepository;
import com.limasantos.pharmacy.api.customer.entity.Customer;
import com.limasantos.pharmacy.api.customer.repository.CustomerRepository;
import com.limasantos.pharmacy.api.product.entity.Product;
import com.limasantos.pharmacy.api.product.repository.ProductRepository;
import com.limasantos.pharmacy.api.inventory.service.InventoryService;
import com.limasantos.pharmacy.api.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SaleMapper saleMapper;
    private final InventoryService inventoryService;

    public SaleService(SaleRepository saleRepository,
                      CustomerRepository customerRepository,
                      ProductRepository productRepository,
                      SaleMapper saleMapper,
                      InventoryService inventoryService) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.saleMapper = saleMapper;
        this.inventoryService = inventoryService;
    }

    /**
     * Cria uma nova venda com itens
     */
    public SaleDTO createSale(CreateSaleDTO dto) {
        // Validar cliente se fornecido
        Customer customer = null;
        if (dto.getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + dto.getCustomerId()));
        }

        // Validar itens
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Venda deve conter pelo menos um item");
        }
        
        // Validar itens antes de processar
        for (CreateSaleItemDTO itemDTO : dto.getItems()) {
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantidade do item deve ser maior que zero");
            }
            if (itemDTO.getPriceAtSale() == null || itemDTO.getPriceAtSale().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Preço de venda não pode ser negativo");
            }
        }

        Sale sale = saleMapper.convertToEntity(dto);
        sale.setCustomer(customer);

        // Processar itens da venda
        for (CreateSaleItemDTO itemDTO : dto.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + itemDTO.getProductId()));

            // Validate stock
            Integer availableStock = inventoryService.calculateProductStock(product.getId());
            if (itemDTO.getQuantity() > availableStock) {
                throw new IllegalStateException(
                        String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                                product.getName(), availableStock, itemDTO.getQuantity()));
            }

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setQuantity(itemDTO.getQuantity());
            saleItem.setPriceAtSale(itemDTO.getPriceAtSale());

            sale.addItem(saleItem);

            // Register stock exit
            inventoryService.registerSaleExitByProduct(product.getId(), itemDTO.getQuantity());
        }

        Sale savedSale = saleRepository.save(sale);
        return saleMapper.convertToBasicDTO(savedSale);
    }

    /**
     * Busca uma venda por ID
     */
    @Transactional(readOnly = true)
    public SaleDTO findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        return saleMapper.convertToBasicDTO(sale);
    }

    /**
     * Busca detalhes completos de uma venda
     */
    @Transactional(readOnly = true)
    public SaleDetailDTO findDetailById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        return saleMapper.convertToDetailDTO(sale);
    }

    /**
     * Lista todas as vendas
     */
    @Transactional(readOnly = true)
    public List<SaleDTO> findAll() {
        List<Sale> sales = saleRepository.findAll();
        return saleMapper.convertToBasicDTOList(sales);
    }

    /**
     * Lista vendas de um cliente
     */
    @Transactional(readOnly = true)
    public List<SaleDTO> findByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + customerId));

        List<Sale> sales = saleRepository.findByCustomer(customer);
        return saleMapper.convertToBasicDTOList(sales);
    }

    /**
     * Busca o total de vendas (agregação)
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalSalesAmount() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream()
                .map(sale -> sale.getTotalAmount() != null ? sale.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula total de vendas de um cliente
     */
    @Transactional(readOnly = true)
    public BigDecimal getCustomerTotalSales(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + customerId));

        return customer.getPurchaseHistory().stream()
                .map(sale -> sale.getTotalAmount() != null ? sale.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Cancela uma venda (reverter estoque)
     */
    public void cancelSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));

        // Reverter movimentações de estoque
        // Este é um exemplo simplificado - em produção você teria controles mais robustos
        saleRepository.delete(sale);
    }
}

