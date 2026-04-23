import { useEffect, useState, useCallback, useMemo } from 'react';
import { Plus, Search, Eye, Receipt } from 'lucide-react';
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

import { SaleForm } from '../components/forms';
import { saleService } from '../services';
import { PaymentMethodLabels } from '../types';
import type { Sale, CreateSaleDTO } from '../types';
import { formatCurrency, formatDate } from '../utils';

export function Vendas() {
  const [sales, setSales] = useState<Sale[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);

  const loadSales = useCallback(async () => {
    try {
      setLoading(true);
      const data = await saleService.getAll();
      setSales(data);
    } catch (error) {
      console.error('Error loading sales:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadSales();
  }, [loadSales]);

  const filteredSales = sales.filter((sale) =>
    sale.id.toString().includes(search)
  );

  const stats = useMemo(() => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    
    const salesToday = sales.filter(sale => {
      const saleDate = new Date(sale.saleDate);
      saleDate.setHours(0, 0, 0, 0);
      return saleDate.getTime() === today.getTime();
    });
    
    const salesThisMonth = sales.filter(sale => {
      const saleDate = new Date(sale.saleDate);
      return saleDate >= startOfMonth;
    });
    
    const totalToday = salesToday.reduce((sum, s) => sum + s.totalAmount, 0);
    const totalMonth = salesThisMonth.reduce((sum, s) => sum + s.totalAmount, 0);
    const avgTicket = salesThisMonth.length > 0 ? totalMonth / salesThisMonth.length : 0;
    
    return {
      todayTotal: totalToday,
      todayCount: salesToday.length,
      monthTotal: totalMonth,
      monthCount: salesThisMonth.length,
      avgTicket,
    };
  }, [sales]);

  const handleCreate = () => {
    setIsFormOpen(true);
  };

  const handleSubmit = async (data: CreateSaleDTO) => {
    const created = await saleService.create(data);
    setSales([created, ...sales]);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
  };

  return (
    <div className="space-y-4 sm:space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">Vendas</h1>
          <p className="text-sm sm:text-base text-muted-foreground">Gerencie as vendas da farmácia</p>
        </div>
        <Button onClick={handleCreate} className="w-full sm:w-auto">
          <Plus className="mr-2 h-4 w-4" />
          Nova Venda
        </Button>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Vendas Hoje</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-xl sm:text-2xl font-bold">{formatCurrency(stats.todayTotal)}</div>
            <p className="text-xs text-muted-foreground">{stats.todayCount} vendas realizadas</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Vendas do Mês</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-xl sm:text-2xl font-bold">{formatCurrency(stats.monthTotal)}</div>
            <p className="text-xs text-muted-foreground">{stats.monthCount} vendas no mês</p>
          </CardContent>
        </Card>
        <Card className="sm:col-span-2 lg:col-span-1">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Ticket Médio</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-xl sm:text-2xl font-bold">{formatCurrency(stats.avgTicket)}</div>
            <p className="text-xs text-muted-foreground">Valor médio por venda</p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <CardTitle>Histórico de Vendas</CardTitle>
            <div className="relative w-full sm:w-auto">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Buscar por código..."
                className="pl-8 w-full sm:w-[250px]"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex justify-center py-8">
              <Loading text="Carregando vendas..." />
            </div>
          ) : filteredSales.length === 0 ? (
            <EmptyState
              type={search ? 'no-results' : 'no-data'}
              title={search ? 'Nenhuma venda encontrada' : 'Nenhuma venda registrada'}
              description={
                search
                  ? 'Tente ajustar sua busca'
                  : 'Comece registrando uma nova venda'
              }
              action={
                !search
                  ? {
                      label: 'Nova Venda',
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
                      <TableHead>Código</TableHead>
                      <TableHead className="hidden sm:table-cell">Cliente</TableHead>
                      <TableHead className="hidden md:table-cell">Data</TableHead>
                      <TableHead className="hidden lg:table-cell">Pagamento</TableHead>
                      <TableHead>Total</TableHead>
                      <TableHead className="w-[80px] sm:w-[100px]">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredSales.map((sale) => (
                      <TableRow key={sale.id}>
                        <TableCell className="font-medium">#{sale.id}</TableCell>
                        <TableCell className="hidden sm:table-cell">{sale.customerName || 'Consumidor final'}</TableCell>
                        <TableCell className="hidden md:table-cell">{formatDate(sale.saleDate)}</TableCell>
                        <TableCell className="hidden lg:table-cell">
                          <Badge variant="secondary">
                            {PaymentMethodLabels[sale.paymentMethod]}
                          </Badge>
                        </TableCell>
                        <TableCell className="font-medium">
                          {formatCurrency(sale.totalAmount)}
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center gap-1 sm:gap-2">
                            <Button variant="ghost" size="icon" title="Ver detalhes">
                              <Eye className="h-4 w-4" />
                            </Button>
                            <Button variant="ghost" size="icon" title="Imprimir cupom">
                              <Receipt className="h-4 w-4" />
                            </Button>
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

      <SaleForm
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        onSubmit={handleSubmit}
      />
    </div>
  );
}
