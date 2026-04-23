import { useState, useEffect, useCallback } from 'react';
import type { Product, CreateInventoryMovementDTO, MovementType as MovementTypeVal } from '../../types';
import { MovementType, MovementTypeLabels } from '../../types';
import { Button, Input, Select, FormDialog } from '../ui';
import { productService } from '../../services';

interface InventoryFormProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateInventoryMovementDTO) => Promise<void>;
  defaultProductId?: number;
}

const movementTypes: MovementTypeVal[] = [
  MovementType.ENTRY,
  MovementType.SALE_EXIT,
  MovementType.ADJUSTMENT_IN,
  MovementType.ADJUSTMENT_OUT,
  MovementType.DISPOSAL,
];

const initialFormData: CreateInventoryMovementDTO = {
  productId: 0,
  type: MovementType.ENTRY,
  quantity: 1,
  reason: '',
  lotNumber: '',
  expirationDate: '',
};

export function InventoryForm({ isOpen, onClose, onSubmit, defaultProductId }: InventoryFormProps) {
  const [formData, setFormData] = useState<CreateInventoryMovementDTO>(initialFormData);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const loadProducts = useCallback(async () => {
    setLoadingProducts(true);
    try {
      const data = await productService.getAll();
      setProducts(data.filter(p => p.active));
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
    } finally {
      setLoadingProducts(false);
    }
  }, []);

  useEffect(() => {
    if (isOpen) {
      loadProducts();
      setFormData({
        ...initialFormData,
        productId: defaultProductId || 0,
      });
      setErrors({});
    }
  }, [isOpen, loadProducts, defaultProductId]);

  const selectedProduct = products.find(p => p.id === formData.productId);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.productId) {
      newErrors.productId = 'Selecione um produto';
    }

    if (formData.quantity <= 0) {
      newErrors.quantity = 'Quantidade deve ser maior que zero';
    }

    if ((formData.type === MovementType.SALE_EXIT || formData.type === MovementType.ADJUSTMENT_OUT || formData.type === MovementType.DISPOSAL) && selectedProduct) {
      if (formData.quantity > selectedProduct.currentStock) {
        newErrors.quantity = `Estoque insuficiente. Disponível: ${selectedProduct.currentStock}`;
      }
    }

    if (!formData.reason?.trim()) {
      newErrors.reason = 'Motivo é obrigatório';
    }

    if (formData.type === MovementType.ENTRY && formData.lotNumber && !formData.expirationDate) {
      newErrors.expirationDate = 'Data de validade é obrigatória para lotes';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validate()) return;

    setLoading(true);
    try {
      await onSubmit(formData);
      onClose();
    } catch (error) {
      console.error('Erro ao registrar movimento:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof CreateInventoryMovementDTO, value: string | number) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field as string]) {
      setErrors(prev => {
        const next = { ...prev };
        delete next[field as string];
        return next;
      });
    }
  };

  return (
    <FormDialog
      isOpen={isOpen}
      onClose={onClose}
      title="Movimento de Estoque"
    >
      {loadingProducts ? (
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin h-8 w-8 border-4 border-primary border-t-transparent rounded-full" />
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select
            label="Produto *"
            value={formData.productId?.toString() || ''}
            onChange={(value) => handleChange('productId', parseInt(value) || 0)}
            options={[
              { value: '', label: 'Selecione um produto' },
              ...products.map(p => ({
                value: p.id.toString(),
                label: `${p.name} (Estoque: ${p.currentStock})`,
              }))
            ]}
            error={errors.productId}
          />

          <Select
            label="Tipo de Movimento"
            value={formData.type}
            onChange={(value) => handleChange('type', value)}
            options={movementTypes.map(type => ({
              value: type,
              label: MovementTypeLabels[type],
            }))}
          />

          <Input
            label="Quantidade *"
            type="number"
            min="1"
            value={formData.quantity}
            onChange={(e) => handleChange('quantity', parseInt(e.target.value) || 0)}
            error={errors.quantity}
          />

          <Input
            label="Motivo *"
            value={formData.reason}
            onChange={(e) => handleChange('reason', e.target.value)}
            error={errors.reason}
            placeholder="Ex: Compra de fornecedor, Venda, Ajuste de inventário"
          />

          {formData.type === MovementType.ENTRY && (
            <>
              <Input
                label="Número do Lote"
                value={formData.lotNumber}
                onChange={(e) => handleChange('lotNumber', e.target.value)}
                placeholder="Ex: LOT-2024-001"
              />

              <Input
                label="Data de Validade"
                type="date"
                value={formData.expirationDate}
                onChange={(e) => handleChange('expirationDate', e.target.value)}
                error={errors.expirationDate}
              />
            </>
          )}

          {selectedProduct && (
            <div className="p-3 bg-muted/50 rounded-lg text-sm">
              <p className="font-medium">{selectedProduct.name}</p>
              <p className="text-muted-foreground">
                Estoque atual: {selectedProduct.currentStock} | 
                Mínimo: {selectedProduct.minStock || 0}
              </p>
              <p className="text-muted-foreground mt-1">
                Após movimento: {' '}
                <span className={
                  formData.type === MovementType.ENTRY || formData.type === MovementType.ADJUSTMENT_IN
                    ? 'text-green-600'
                    : formData.type === MovementType.SALE_EXIT || formData.type === MovementType.ADJUSTMENT_OUT || formData.type === MovementType.DISPOSAL
                      ? 'text-red-600'
                      : ''
                }>
                  {formData.type === MovementType.ENTRY || formData.type === MovementType.ADJUSTMENT_IN
                    ? selectedProduct.currentStock + formData.quantity
                    : formData.type === MovementType.SALE_EXIT || formData.type === MovementType.ADJUSTMENT_OUT || formData.type === MovementType.DISPOSAL
                      ? selectedProduct.currentStock - formData.quantity
                      : formData.quantity
                  }
                </span>
              </p>
            </div>
          )}

          <div className="flex justify-end gap-3 pt-4 border-t">
            <Button type="button" variant="outline" onClick={onClose}>
              Cancelar
            </Button>
            <Button type="submit" loading={loading}>
              Registrar Movimento
            </Button>
          </div>
        </form>
      )}
    </FormDialog>
  );
}
