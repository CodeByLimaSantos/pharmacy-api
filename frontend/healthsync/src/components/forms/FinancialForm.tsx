import { useState, useEffect } from 'react';
import type { Financial, CreateFinancialDTO, FinancialType as FinancialTypeVal, PaymentStatus as PaymentStatusVal } from '../../types';
import { FinancialType, FinancialTypeLabels, PaymentStatus, PaymentStatusLabels } from '../../types';
import { Button, Input, Select, FormDialog } from '../ui';

interface FinancialFormProps {
  financial?: Financial | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateFinancialDTO) => Promise<void>;
}

const financialTypes: FinancialTypeVal[] = [
  FinancialType.RECEIVABLE,
  FinancialType.PAYABLE,
];

const paymentStatuses: PaymentStatusVal[] = [
  PaymentStatus.PENDING,
  PaymentStatus.PAID,
  PaymentStatus.CANCELLED,
];

const initialFormData: CreateFinancialDTO = {
  description: '',
  type: FinancialType.RECEIVABLE,
  amount: 0,
  dueDate: new Date().toISOString().split('T')[0],
  status: PaymentStatus.PENDING,
  notes: '',
};

export function FinancialForm({ financial, isOpen, onClose, onSubmit }: FinancialFormProps) {
  const [formData, setFormData] = useState<CreateFinancialDTO>(initialFormData);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const isEditing = !!financial;

  useEffect(() => {
    if (financial) {
      setFormData({
        description: financial.description,
        type: financial.type,
        amount: financial.amount,
        dueDate: financial.dueDate?.split('T')[0] || '',
        paymentDate: financial.paymentDate?.split('T')[0] || undefined,
        status: financial.status,
        notes: financial.notes || '',
      });
    } else {
      setFormData(initialFormData);
    }
    setErrors({});
  }, [financial, isOpen]);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.description.trim()) {
      newErrors.description = 'Descrição é obrigatória';
    }

    if (formData.amount <= 0) {
      newErrors.amount = 'Valor deve ser maior que zero';
    }

    if (!formData.dueDate) {
      newErrors.dueDate = 'Data de vencimento é obrigatória';
    }

    if (formData.status === PaymentStatus.PAID && !formData.paymentDate) {
      newErrors.paymentDate = 'Data de pagamento é obrigatória para status Pago';
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
      console.error('Erro ao salvar registro financeiro:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof CreateFinancialDTO, value: string | number) => {
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
      title={isEditing ? 'Editar Registro' : 'Novo Registro Financeiro'}
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Descrição *"
          value={formData.description}
          onChange={(e) => handleChange('description', e.target.value)}
          error={errors.description}
          placeholder="Descrição do movimento"
        />

        <div className="grid grid-cols-2 gap-4">
          <Select
            label="Tipo"
            value={formData.type}
            onChange={(value) => handleChange('type', value)}
            options={financialTypes.map(type => ({
              value: type,
              label: FinancialTypeLabels[type],
            }))}
          />

          <Select
            label="Status"
            value={formData.status}
            onChange={(value) => handleChange('status', value)}
            options={paymentStatuses.map(status => ({
              value: status,
              label: PaymentStatusLabels[status],
            }))}
          />
        </div>

        <Input
          label="Valor *"
          type="number"
          step="0.01"
          min="0"
          value={formData.amount}
          onChange={(e) => handleChange('amount', parseFloat(e.target.value) || 0)}
          error={errors.amount}
        />

        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Data de Vencimento *"
            type="date"
            value={formData.dueDate}
            onChange={(e) => handleChange('dueDate', e.target.value)}
            error={errors.dueDate}
          />

          <Input
            label="Data de Pagamento"
            type="date"
            value={formData.paymentDate || ''}
            onChange={(e) => handleChange('paymentDate', e.target.value)}
            error={errors.paymentDate}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-1">
            Observações
          </label>
          <textarea
            value={formData.notes}
            onChange={(e) => handleChange('notes', e.target.value)}
            placeholder="Notas adicionais"
            rows={3}
            className="w-full px-3 py-2 border border-input rounded-md bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 resize-none"
          />
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
