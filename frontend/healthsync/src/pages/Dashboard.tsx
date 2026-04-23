import { useEffect, useState } from 'react';
import {
  Package,
  ShoppingCart,
  Users,
  TrendingUp,
  TrendingDown,
  AlertTriangle,
} from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui';
import { financialService, productService, inventoryService } from '../services';
import { formatCurrency } from '../utils';

interface DashboardStats {
  totalProducts: number;
  totalCustomers: number;
  pendingPayments: number;
  expiringProducts: number;
  receivable: number;
  payable: number;
}

export function Dashboard() {
  const [stats, setStats] = useState<DashboardStats>({
    totalProducts: 0,
    totalCustomers: 0,
    pendingPayments: 0,
    expiringProducts: 0,
    receivable: 0,
    payable: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadStats() {
      try {
        const [products, summary, expiringLots] = await Promise.all([
          productService.getAll(),
          financialService.getSummary(),
          inventoryService.getExpiringLots(30),
        ]);

        setStats({
          totalProducts: products.length,
          totalCustomers: 0, // TODO: implement customer count
          pendingPayments: summary?.pending || 0,
          expiringProducts: expiringLots.length,
          receivable: summary?.receivable || 0,
          payable: summary?.payable || 0,
        });
      } catch (error) {
        console.error('Error loading dashboard stats:', error);
      } finally {
        setLoading(false);
      }
    }

    loadStats();
  }, []);

  const cards = [
    {
      title: 'Total de Produtos',
      value: stats.totalProducts,
      icon: <Package className="h-4 w-4 text-muted-foreground" />,
      description: 'Produtos cadastrados',
    },
    {
      title: 'Vendas do Mês',
      value: formatCurrency(0),
      icon: <ShoppingCart className="h-4 w-4 text-muted-foreground" />,
      description: '+12% em relação ao mês anterior',
      trend: 'up' as const,
    },
    {
      title: 'Clientes Ativos',
      value: stats.totalCustomers,
      icon: <Users className="h-4 w-4 text-muted-foreground" />,
      description: 'Clientes cadastrados',
    },
    {
      title: 'A Receber',
      value: formatCurrency(stats.receivable),
      icon: <TrendingUp className="h-4 w-4 text-green-500" />,
      description: 'Total a receber',
      variant: 'success' as const,
    },
    {
      title: 'A Pagar',
      value: formatCurrency(stats.payable),
      icon: <TrendingDown className="h-4 w-4 text-red-500" />,
      description: 'Total a pagar',
      variant: 'danger' as const,
    },
    {
      title: 'Produtos Vencendo',
      value: stats.expiringProducts,
      icon: <AlertTriangle className="h-4 w-4 text-yellow-500" />,
      description: 'Nos próximos 30 dias',
      variant: 'warning' as const,
    },
  ];

  return (
    <div className="space-y-4 sm:space-y-6">
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-sm sm:text-base text-muted-foreground">
          Bem-vindo ao HealthSync - Sistema de Gestão Integrada
        </p>
      </div>

      <div className="grid gap-4 grid-cols-2 lg:grid-cols-3">
        {cards.map((card) => (
          <Card key={card.title}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-xs sm:text-sm font-medium">{card.title}</CardTitle>
              {card.icon}
            </CardHeader>
            <CardContent>
              <div className="text-lg sm:text-2xl font-bold">
                {loading ? '...' : card.value}
              </div>
              <p className="text-xs text-muted-foreground flex items-center gap-1">
                {card.trend === 'up' && <TrendingUp className="h-3 w-3 text-green-500" />}
                {card.description}
              </p>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Vendas Recentes</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-muted-foreground">
              Nenhuma venda recente para exibir.
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Produtos com Estoque Baixo</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-muted-foreground">
              Nenhum produto com estoque baixo.
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
