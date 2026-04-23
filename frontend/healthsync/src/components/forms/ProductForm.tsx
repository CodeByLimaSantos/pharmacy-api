import { useState, useEffect } from 'react';
import type { AxiosError } from 'axios';
import type { Product, CreateProductDTO, ProductCategoryType } from '../../types';
import { ProductCategoryTypeLabels, TarjaOptions } from '../../types';
import { Button, Input, Select, FormDialog } from '../ui';

interface ValidationErrorPayload {
  details?: string;
  message?: string;
  fieldErrors?: Record<string, string>;
}

interface ProductFormProps {
  product?: Product | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateProductDTO) => Promise<boolean>;
}

const initialFormData: CreateProductDTO = {
  name: '',
  barcode: '',
  description: '',
  priceCost: 0,
  priceSale: 0,
  controlled: false,
  tarja: 'Sem Tarja',
  registerMS: '',
  category: 'MEDICAMENTOS',
  supplierId: 0,
  minStock: 0,
  active: true,
};

export function ProductForm({ product, isOpen, onClose, onSubmit }: ProductFormProps) {
  const [formData, setFormData] = useState<CreateProductDTO>(initialFormData);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const isEditing = !!product;

  useEffect(() => {
    if (product) {
      setFormData({
        name: product.name,
        barcode: product.barcode ?? '',
        description: product.description ?? '',
        priceCost: product.priceCost ?? 0,
        priceSale: product.priceSale ?? 0,
        controlled: product.controlled ?? false,
        tarja: product.tarja ?? 'Sem Tarja',
        registerMS: product.registerMS ?? '',
        category: (product.category as ProductCategoryType) ?? 'MEDICAMENTOS',
        supplierId: product.supplierId ?? 0,
        minStock: product.minStock ?? 0,
        active: product.active ?? true,
      });
    } else {
      setFormData(initialFormData);
    }
    setErrors({});
  }, [product, isOpen]);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Nome é obrigatório';
    }

    if (!formData.priceSale || formData.priceSale <= 0) {
      newErrors.priceSale = 'Preço de venda é obrigatório e deve ser maior que zero';
    }

    if ((formData.priceCost ?? 0) < 0) {
      newErrors.priceCost = 'Preço de custo não pode ser negativo';
    }

    if (formData.minStock !== undefined && formData.minStock < 0) {
      newErrors.minStock = 'Estoque mínimo não pode ser negativo';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent): Promise<boolean> => {
    e.preventDefault();

    if (!validate()) {
      return false;
    }

    setLoading(true);
    try {
      const success = await onSubmit({
        ...formData,
        barcode: formData.barcode?.replace(/\D/g, '') ?? '',
      });

      if (success) {
        onClose();
      }

      return success;
    } catch (error) {
      console.error('Erro ao salvar produto:', error);
      const axiosError = error as AxiosError<ValidationErrorPayload>;
      const fieldErrors = axiosError.response?.data?.fieldErrors;
      const details = axiosError.response?.data?.details || axiosError.response?.data?.message;

      if (fieldErrors && Object.keys(fieldErrors).length > 0) {
        setErrors((prev) => ({ ...prev, ...fieldErrors }));
      } else {
        setErrors((prev) => ({
          ...prev,
          form: details || 'Não foi possível salvar os dados do produto.',
        }));
      }
      return false;
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof CreateProductDTO, value: string | number | boolean) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => {
        const next = { ...prev };
        delete next[field];
        return next;
      });
    }
  };

  const handleNumberChange = (field: keyof CreateProductDTO, value: string) => {
    const numberValue = parseFloat(value.replace(',', '.')) || 0;
    handleChange(field, numberValue);
  };

  return (
    <FormDialog
      isOpen={isOpen}
      onClose={onClose}
      title={isEditing ? 'Editar Produto' : 'Novo Produto'}
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        {errors.form && (
          <div className="rounded-md border border-destructive/20 bg-destructive/10 p-3 text-sm text-destructive">
            {errors.form}
          </div>
        )}

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Input
            label="Nome *"
            value={formData.name}
            onChange={(e) => handleChange('name', e.target.value)}
            error={errors.name}
            placeholder="Nome do produto"
            className="sm:col-span-2"
          />

          <Input
            label="Descrição *"
            value={formData.description}
            onChange={(e) => handleChange('description', e.target.value)}
            error={errors.description}
            placeholder="Descrição do produto"
            className="sm:col-span-2"
             />

             
          <Input
            label="Código de Barras"
            value={formData.barcode}
            onChange={(e) => handleChange('barcode', e.target.value)}
            error={errors.barcode}
            placeholder="0000000000000"
          />


          <Select
            label="Categoria"
            value={formData.category ?? ''}
            onChange={(value) => handleChange('category', value as ProductCategoryType)}
            options={Object.entries(ProductCategoryTypeLabels).map(([value, label]) => ({
              value,
              label,
            }))}
          />
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Input
            label="Preço de Custo"
            type="number"
            value={formData.priceCost?.toString() ?? '0'}
            onChange={(e) => handleNumberChange('priceCost', e.target.value)}
            error={errors.priceCost}
            placeholder="0,00"
            step="0.01"
            min="0"
          />

          <Input
            label="Preço de Venda *"
            type="number"
            value={formData.priceSale?.toString() ?? '0'}
            onChange={(e) => handleNumberChange('priceSale', e.target.value)}
            error={errors.priceSale}
            placeholder="0,00"
            step="0.01"
            min="0.01"
          />
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Input
            label="Estoque Mínimo"
            type="number"
            value={formData.minStock?.toString() ?? '0'}
            onChange={(e) => handleNumberChange('minStock', e.target.value)}
            error={errors.minStock}
            placeholder="0"
            min="0"
          />

          <Input
            label="Registro MS"
            value={formData.registerMS ?? ''}
            onChange={(e) => handleChange('registerMS', e.target.value)}
            placeholder="Número do registro no Ministério da Saúde"
          />
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Select
            label="Tarja"
            value={formData.tarja ?? 'Sem Tarja'}
            onChange={(value) => handleChange('tarja', value)}
            options={TarjaOptions.map((tarja) => ({ value: tarja, label: tarja }))}
          />

          <Input
            label="ID Fornecedor"
            type="number"
            value={formData.supplierId?.toString() ?? '0'}
            onChange={(e) => handleChange('supplierId', parseInt(e.target.value) || 0)}
            placeholder="0"
            min="0"
          />
        </div>

        <div className="flex items-center gap-4">
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={formData.controlled}
              onChange={(e) => handleChange('controlled', e.target.checked)}
              className="h-4 w-4 rounded border-gray-300"
            />
            <span className="text-sm text-muted-foreground">Produto Controlado</span>
          </label>

          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={formData.active}
              onChange={(e) => handleChange('active', e.target.checked)}
              className="h-4 w-4 rounded border-gray-300"
            />
            <span className="text-sm text-muted-foreground">Produto Ativo</span>
          </label>
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t">
          <Button type="button" variant="outline" onClick={onClose} disabled={loading}>
            Cancelar
          </Button>
          <Button type="submit" loading={loading} disabled={loading}>
            {isEditing ? 'Salvar' : 'Criar'}
          </Button>
        </div>
      </form>
    </FormDialog>
  );
}