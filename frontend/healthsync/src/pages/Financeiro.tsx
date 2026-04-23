import { useEffect, useState, useCallback } from 'react';
import {
  Plus,
  Search,
  TrendingUp,
  TrendingDown,
  Clock,
  AlertTriangle,
  Check,
  Edit,
} from 'lucide-react';
import {
  Button,
  Input,
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  Table,
  TableHeader,
  TableBody,
  TableHead,
  TableRow,
  TableCell,
  Badge,
  Loading,
  EmptyState,
} from '../components/ui';
import { FinancialForm } from '../components/forms';
import { financialService } from '../services';
import {
  FinancialTypeLabels,
  PaymentStatusLabels,
  PaymentStatus,
  FinancialType,
} from '../types';
import type { Financial, FinancialSummary, CreateFinancialDTO } from '../types';
import { formatCurrency, formatDate } from '../utils';

export function Financeiro() {
  const [financials, setFinancials] = useState<Financial[]>([]);
  const [summary, setSummary] = useState<FinancialSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<'all' | 'pending' | 'overdue'>('all');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingFinancial, setEditingFinancial] = useState<Financial | null>(null);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      const [financialsData, summaryData] = await Promise.all([
        filter === 'pending'
          ? financialService.getPending()
          : filter === 'overdue'
          ? financialService.getOverdue()
          : financialService.getAll(),
        financialService.getSummary(),
      ]);
      setFinancials(financialsData);
      setSummary(summaryData);
    } catch (error) {
      console.error('Error loading financial data:', error);
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const filteredFinancials = financials.filter((financial) =>
    financial.description?.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = () => {
    setEditingFinancial(null);
    setIsFormOpen(true);
  };

  const handleEdit = (financial: Financial) => {
    setEditingFinancial(financial);
    setIsFormOpen(true);
  };

  const handleSubmit = async (data: CreateFinancialDTO) => {
    if (editingFinancial) {
      const updated = await financialService.update(editingFinancial.id, data);
      setFinancials(financials.map((f) => (f.id === updated.id ? updated : f)));
    } else {
      const created = await financialService.create(data);
      setFinancials([...financials, created]);
    }
    // Refresh summary
    const summaryData = await financialService.getSummary();
    setSummary(summaryData);
  };

  const handleMarkAsPaid = async (financial: Financial) => {
    const updateData: CreateFinancialDTO = {
      description: financial.description,
      type: financial.type,
      amount: financial.amount,
      dueDate: financial.dueDate,
      status: PaymentStatus.PAID,
      paymentDate: new Date().toISOString().split('T')[0],
      notes: financial.notes || undefined,
    };
    const updated = await financialService.update(financial.id, updateData);
    setFinancials(financials.map((f) => (f.id === updated.id ? updated : f)));
    const summaryData = await financialService.getSummary();
    setSummary(summaryData);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setEditingFinancial(null);
  };

  const getStatusBadge = (status: Financial['status']) => {
    const variants: Record<string, 'default' | 'success' | 'destructive' | 'warning'> = {
      PAID: 'success',
      PENDING: 'warning',
      CANCELLED: 'default',
      OVERDUE: 'destructive',
      PARTIALLY_PAID: 'warning',
    };
    return (
      <Badge variant={variants[status] || 'default'}>
        {PaymentStatusLabels[status]}
      </Badge>
    );
  };

  const getTypeBadge = (type: Financial['type']) => {
    return (
      <Badge variant={type === FinancialType.RECEIVABLE ? 'success' : 'destructive'}>
        {FinancialTypeLabels[type]}
      </Badge>
    );
  };

  return (
    <div className="space-y-4 sm:space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">Financeiro</h1>
          <p className="text-sm sm:text-base text-muted-foreground">Gerencie as finanças da farmácia</p>
        </div>
        <Button onClick={handleCreate} className="w-full sm:w-auto">
          <Plus className="mr-2 h-4 w-4" />
          Nova Conta
        </Button>
      </div>

      <div className="grid gap-4 grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-xs sm:text-sm font-medium">A Receber</CardTitle>
            <TrendingUp className="h-4 w-4 text-green-500" />
          </CardHeader>
          <CardContent>
            <div className="text-lg sm:text-2xl font-bold text-green-600">
              {formatCurrency(summary?.receivable || 0)}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-xs sm:text-sm font-medium">A Pagar</CardTitle>
            <TrendingDown className="h-4 w-4 text-red-500" />
          </CardHeader>
          <CardContent>
            <div className="text-lg sm:text-2xl font-bold text-red-600">
              {formatCurrency(summary?.payable || 0)}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-xs sm:text-sm font-medium">Pendentes</CardTitle>
            <Clock className="h-4 w-4 text-yellow-500" />
          </CardHeader>
          <CardContent>
            <div className="text-lg sm:text-2xl font-bold text-yellow-600">
              {summary?.pending || 0}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-xs sm:text-sm font-medium">Vencidas</CardTitle>
            <AlertTriangle className="h-4 w-4 text-red-500" />
          </CardHeader>
          <CardContent>
            <div className="text-lg sm:text-2xl font-bold text-red-600">
              {summary?.overdue || 0}
            </div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <CardTitle>Contas</CardTitle>
            <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
              <div className="flex rounded-md border overflow-hidden">
                <Button
                  variant={filter === 'all' ? 'secondary' : 'ghost'}
                  size="sm"
                  onClick={() => setFilter('all')}
                  className="rounded-r-none flex-1 sm:flex-none"
                >
                  Todas
                </Button>
                <Button
                  variant={filter === 'pending' ? 'secondary' : 'ghost'}
                  size="sm"
                  onClick={() => setFilter('pending')}
                  className="rounded-none border-x flex-1 sm:flex-none"
                >
                  Pendentes
                </Button>
                <Button
                  variant={filter === 'overdue' ? 'secondary' : 'ghost'}
                  size="sm"
                  onClick={() => setFilter('overdue')}
                  className="rounded-l-none flex-1 sm:flex-none"
                >
                  Vencidas
                </Button>
              </div>
              <div className="relative w-full sm:w-auto">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Buscar..."
                  className="pl-8 w-full sm:w-[200px]"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex justify-center py-8">
              <Loading text="Carregando dados financeiros..." />
            </div>
          ) : filteredFinancials.length === 0 ? (
            <EmptyState
              type={search ? 'no-results' : 'no-data'}
              title={
                search
                  ? 'Nenhuma conta encontrada'
                  : 'Nenhuma conta cadastrada'
              }
              description={
                search
                  ? 'Tente ajustar sua busca'
                  : 'Comece adicionando uma nova conta'
              }
              action={
                !search
                  ? {
                      label: 'Nova Conta',
                      onClick: handleCreate,
                    }
                  : undefined
              }
            />
          ) : (
            <div className="overflow-x-auto -mx-4 sm:mx-0">
              <div className="inline-block min-w-full align-middle">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Descrição</TableHead>
                      <TableHead className="hidden sm:table-cell">Tipo</TableHead>
                      <TableHead className="hidden md:table-cell">Vencimento</TableHead>
                      <TableHead>Valor</TableHead>
                      <TableHead className="hidden lg:table-cell">Status</TableHead>
                      <TableHead className="w-[100px] sm:w-[140px]">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredFinancials.map((financial) => (
                      <TableRow key={financial.id}>
                        <TableCell className="font-medium">
                          {financial.description}
                        </TableCell>
                        <TableCell className="hidden sm:table-cell">{getTypeBadge(financial.type)}</TableCell>
                        <TableCell className="hidden md:table-cell">{formatDate(financial.dueDate)}</TableCell>
                        <TableCell className="font-medium">
                          {formatCurrency(financial.amount)}
                        </TableCell>
                        <TableCell className="hidden lg:table-cell">{getStatusBadge(financial.status)}</TableCell>
                        <TableCell>
                          <div className="flex items-center gap-1">
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleEdit(financial)}
                              title="Editar"
                            >
                              <Edit className="h-4 w-4" />
                            </Button>
                            {financial.status === PaymentStatus.PENDING && (
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => handleMarkAsPaid(financial)}
                                title="Marcar como pago"
                                className="hidden sm:flex"
                              >
                                <Check className="mr-1 h-4 w-4" />
                                Pagar
                              </Button>
                            )}
                            {financial.status === PaymentStatus.PENDING && (
                              <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => handleMarkAsPaid(financial)}
                                title="Marcar como pago"
                                className="sm:hidden"
                              >
                                <Check className="h-4 w-4" />
                              </Button>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <FinancialForm
        financial={editingFinancial}
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        onSubmit={handleSubmit}
      />
    </div>
  );
}
