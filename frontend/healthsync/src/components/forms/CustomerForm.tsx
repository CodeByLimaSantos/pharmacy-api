import { useState, useEffect } from 'react';
import type { AxiosError } from 'axios';
import type { Customer, CreateCustomerDTO } from '../../types';
import { Button, Input, FormDialog } from '../ui';



interface ValidationErrorPayload {
  details?: string;
  message?: string;
  fieldErrors?: Record<string, string>;
}



interface CustomerFormProps {
  customer?: Customer | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateCustomerDTO) => Promise<void>;
}



const initialFormData: CreateCustomerDTO = {
  name: '',
  cpf: '',
  email: '',
  phone: '',
  address: '',
  active: true,
};

export function CustomerForm({ customer, isOpen, onClose, onSubmit }: CustomerFormProps) {
  const [formData, setFormData] = useState<CreateCustomerDTO>(initialFormData);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const isEditing = !!customer;

  useEffect(() => {
    if (customer) {
      setFormData({
        name: customer.name,
        cpf: customer.cpf || '',
        email: customer.email || '',
        phone: customer.phone || '',
        address: customer.address || '',
        active: customer.active,
      });
    } else {
      setFormData(initialFormData);
    }
    setErrors({});
  }, [customer, isOpen]);

  const formatCPF = (value: string): string => {
    const numbers = value.replace(/\D/g, '').slice(0, 11);
    if (numbers.length <= 3) return numbers;
    if (numbers.length <= 6) return `${numbers.slice(0, 3)}.${numbers.slice(3)}`;
    if (numbers.length <= 9) return `${numbers.slice(0, 3)}.${numbers.slice(3, 6)}.${numbers.slice(6)}`;
    return `${numbers.slice(0, 3)}.${numbers.slice(3, 6)}.${numbers.slice(6, 9)}-${numbers.slice(9)}`;
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

    const cpfNumbers = formData.cpf?.replace(/\D/g, '') || '';
    if (!cpfNumbers) {
      newErrors.cpf = 'CPF é obrigatório';
    } else if (cpfNumbers.length !== 11) {
      newErrors.cpf = 'CPF deve ter 11 dígitos';
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
        cpf: formData.cpf?.replace(/\D/g, '') || '',
        phone: formData.phone?.replace(/\D/g, '') || '',
      });
      onClose();
    } catch (error) {
      console.error('Erro ao salvar cliente:', error);
      const axiosError = error as AxiosError<ValidationErrorPayload>;
      const fieldErrors = axiosError.response?.data?.fieldErrors;
      const details = axiosError.response?.data?.details || axiosError.response?.data?.message;

      if (fieldErrors && Object.keys(fieldErrors).length > 0) {
        setErrors((prev) => ({ ...prev, ...fieldErrors }));
      } else {
        setErrors((prev) => ({
          ...prev,
          form: details || 'Não foi possível salvar os dados do cliente.',
        }));
      }
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof CreateCustomerDTO, value: string | boolean) => {
    let processedValue = value;

    if (field === 'cpf' && typeof value === 'string') {
      processedValue = formatCPF(value);
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
      title={isEditing ? 'Editar Cliente' : 'Novo Cliente'}
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        {errors.form && (
          <div className="rounded-md border border-destructive/20 bg-destructive/10 p-3 text-sm text-destructive">
            {errors.form}
          </div>
        )}

        <Input
          label="Nome *"
          value={formData.name}
          onChange={(e) => handleChange('name', e.target.value)}
          error={errors.name}
          placeholder="Nome completo"
        />

        <Input
          label="CPF *"
          value={formData.cpf}
          onChange={(e) => handleChange('cpf', e.target.value)}
          error={errors.cpf}
          placeholder="000.000.000-00"
        />

        <Input
          label="Email"
          type="email"
          value={formData.email}
          onChange={(e) => handleChange('email', e.target.value)}
          error={errors.email}
          placeholder="email@exemplo.com"
        />

        <Input
          label="Telefone"
          value={formData.phone}
          onChange={(e) => handleChange('phone', e.target.value)}
          placeholder="(00) 00000-0000"
        />

        <Input
          label="Endereço"
          value={formData.address}
          onChange={(e) => handleChange('address', e.target.value)}
          placeholder="Endereço completo"
        />

        <div className="flex items-center gap-2">
          <input
            type="checkbox"
            id="customerActive"
            checked={formData.active}
            onChange={(e) => handleChange('active', e.target.checked)}
            className="h-4 w-4 rounded border-gray-300"
          />
          <label htmlFor="customerActive" className="text-sm text-muted-foreground">
            Cliente ativo
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
