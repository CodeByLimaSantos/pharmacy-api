import { useState, useEffect } from 'react';
import type { Supplier, CreateSupplierDTO } from '../../types';
import { Button, Input, FormDialog } from '../ui';

interface SupplierFormProps {
  supplier?: Supplier | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateSupplierDTO) => Promise<void>;
}

const initialFormData: CreateSupplierDTO = {
  name: '',
  cnpj: '',
  email: '',
  phone: '',
  address: '',
  contactName: '',
  active: true,
};

export function SupplierForm({ supplier, isOpen, onClose, onSubmit }: SupplierFormProps) {
  const [formData, setFormData] = useState<CreateSupplierDTO>(initialFormData);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const isEditing = !!supplier;

  useEffect(() => {
    if (supplier) {
      setFormData({
        name: supplier.name,
        cnpj: supplier.cnpj || '',
        email: supplier.email || '',
        phone: supplier.phone || '',
        address: supplier.address || '',
        contactName: supplier.contactName || '',
        active: supplier.active,
      });
    } else {
      setFormData(initialFormData);
    }
    setErrors({});
  }, [supplier, isOpen]);

  const formatCNPJ = (value: string): string => {
    const numbers = value.replace(/\D/g, '').slice(0, 14);
    if (numbers.length <= 2) return numbers;
    if (numbers.length <= 5) return `${numbers.slice(0, 2)}.${numbers.slice(2)}`;
    if (numbers.length <= 8) return `${numbers.slice(0, 2)}.${numbers.slice(2, 5)}.${numbers.slice(5)}`;
    if (numbers.length <= 12) return `${numbers.slice(0, 2)}.${numbers.slice(2, 5)}.${numbers.slice(5, 8)}/${numbers.slice(8)}`;
    return `${numbers.slice(0, 2)}.${numbers.slice(2, 5)}.${numbers.slice(5, 8)}/${numbers.slice(8, 12)}-${numbers.slice(12)}`;
  };

  const formatPhone = (value: string): string => {
    const numbers = value.replace(/\D/g, '').slice(0, 11);
    if (numbers.length <= 2) return numbers;
    if (numbers.length <= 7) return `(${numbers.slice(0, 2)}) ${numbers.slice(2)}`;
    return `(${numbers.slice(0, 2)}) ${numbers.slice(2, 7)}-${numbers.slice(7)}`;
  };

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Nome é obrigatório';
    }

    const cnpjNumbers = formData.cnpj?.replace(/\D/g, '') || '';
    if (cnpjNumbers && cnpjNumbers.length !== 14) {
      newErrors.cnpj = 'CNPJ deve ter 14 dígitos';
    }

    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Email inválido';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validate()) return;

    setLoading(true);
    try {
      await onSubmit({
        ...formData,
        cnpj: formData.cnpj?.replace(/\D/g, '') || '',
        phone: formData.phone?.replace(/\D/g, '') || '',
      });
      onClose();
    } catch (error) {
      console.error('Erro ao salvar fornecedor:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof CreateSupplierDTO, value: string | boolean) => {
    let processedValue = value;
    
    if (field === 'cnpj' && typeof value === 'string') {
      processedValue = formatCNPJ(value);
    } else if (field === 'phone' && typeof value === 'string') {
      processedValue = formatPhone(value);
    }
    
    setFormData(prev => ({ ...prev, [field]: processedValue }));
    if (errors[field]) {
      setErrors(prev => {
        const next = { ...prev };
        delete next[field];
        return next;
      });
    }
  };

  return (
    <FormDialog
      isOpen={isOpen}
      onClose={onClose}
      title={isEditing ? 'Editar Fornecedor' : 'Novo Fornecedor'}
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Razão Social *"
          value={formData.name}
          onChange={(e) => handleChange('name', e.target.value)}
          error={errors.name}
          placeholder="Nome da empresa"
        />

        <Input
          label="CNPJ"
          value={formData.cnpj}
          onChange={(e) => handleChange('cnpj', e.target.value)}
          error={errors.cnpj}
          placeholder="00.000.000/0000-00"
        />

        <Input
          label="Nome do Contato"
          value={formData.contactName}
          onChange={(e) => handleChange('contactName', e.target.value)}
          placeholder="Pessoa de contato"
        />

        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Email"
            type="email"
            value={formData.email}
            onChange={(e) => handleChange('email', e.target.value)}
            error={errors.email}
            placeholder="email@empresa.com"
          />

          <Input
            label="Telefone"
            value={formData.phone}
            onChange={(e) => handleChange('phone', e.target.value)}
            placeholder="(00) 00000-0000"
          />
        </div>

        <Input
          label="Endereço"
          value={formData.address}
          onChange={(e) => handleChange('address', e.target.value)}
          placeholder="Endereço completo"
        />

        <div className="flex items-center gap-2">
          <input
            type="checkbox"
            id="supplierActive"
            checked={formData.active}
            onChange={(e) => handleChange('active', e.target.checked)}
            className="h-4 w-4 rounded border-gray-300"
          />
          <label htmlFor="supplierActive" className="text-sm text-muted-foreground">
            Fornecedor ativo
          </label>
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t">
          <Button type="button" variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" loading={loading}>
            {isEditing ? 'Salvar' : 'Criar'}
          </Button>
        </div>
      </form>
    </FormDialog>
  );
}
