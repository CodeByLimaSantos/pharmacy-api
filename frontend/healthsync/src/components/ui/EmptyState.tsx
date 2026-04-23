import * as React from 'react';
import { cn } from '../../utils';
import { FileX, Search, AlertCircle, Package } from 'lucide-react';
import { Button } from './Button';

type EmptyStateType = 'no-data' | 'no-results' | 'error' | 'empty';

interface EmptyStateProps {
  type?: EmptyStateType;
  title?: string;
  description?: string;
  action?: {
    label: string;
    onClick: () => void;
  };
  className?: string;
  icon?: React.ReactNode;
}

const defaultContent: Record<EmptyStateType, { title: string; description: string; icon: React.ReactNode }> = {
  'no-data': {
    title: 'Nenhum dado encontrado',
    description: 'Ainda não há registros cadastrados.',
    icon: <Package className="h-12 w-12 text-muted-foreground" />,
  },
  'no-results': {
    title: 'Nenhum resultado',
    description: 'Tente ajustar seus filtros ou termos de busca.',
    icon: <Search className="h-12 w-12 text-muted-foreground" />,
  },
  error: {
    title: 'Erro ao carregar',
    description: 'Não foi possível carregar os dados. Tente novamente.',
    icon: <AlertCircle className="h-12 w-12 text-destructive" />,
  },
  empty: {
    title: 'Vazio',
    description: 'Não há itens para exibir.',
    icon: <FileX className="h-12 w-12 text-muted-foreground" />,
  },
};

export function EmptyState({
  type = 'no-data',
  title,
  description,
  action,
  className,
  icon,
}: EmptyStateProps) {
  const content = defaultContent[type];

  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center gap-4 py-12 text-center',
        className
      )}
    >
      {icon || content.icon}
      <div className="space-y-2">
        <h3 className="text-lg font-semibold text-foreground">{title || content.title}</h3>
        <p className="text-sm text-muted-foreground max-w-sm">
          {description || content.description}
        </p>
      </div>
      {action && (
        <Button onClick={action.onClick} variant="outline">
          {action.label}
        </Button>
      )}
    </div>
  );
}
