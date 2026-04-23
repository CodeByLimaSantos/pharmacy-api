import {
  FileText,
  BarChart3,
  TrendingUp,
  Package,
  Users,
  DollarSign,
  Calendar,
  Download,
} from 'lucide-react';
import { Button, Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/ui';

interface ReportCard {
  title: string;
  description: string;
  icon: React.ReactNode;
  category: string;
}

const reports: ReportCard[] = [
  {
    title: 'Vendas por Período',
    description: 'Relatório detalhado de vendas com filtro por data',
    icon: <TrendingUp className="h-6 w-6" />,
    category: 'Vendas',
  },
  {
    title: 'Produtos Mais Vendidos',
    description: 'Lista dos produtos com maior volume de vendas',
    icon: <Package className="h-6 w-6" />,
    category: 'Vendas',
  },
  {
    title: 'Estoque Atual',
    description: 'Visão geral do estoque com quantidades e valores',
    icon: <Package className="h-6 w-6" />,
    category: 'Estoque',
  },
  {
    title: 'Produtos Vencendo',
    description: 'Lista de produtos próximos do vencimento',
    icon: <Calendar className="h-6 w-6" />,
    category: 'Estoque',
  },
  {
    title: 'Clientes Ativos',
    description: 'Relatório de clientes com histórico de compras',
    icon: <Users className="h-6 w-6" />,
    category: 'Clientes',
  },
  {
    title: 'Fluxo de Caixa',
    description: 'Entradas e saídas financeiras do período',
    icon: <DollarSign className="h-6 w-6" />,
    category: 'Financeiro',
  },
  {
    title: 'Contas a Pagar/Receber',
    description: 'Projeção de pagamentos e recebimentos',
    icon: <FileText className="h-6 w-6" />,
    category: 'Financeiro',
  },
  {
    title: 'Análise de Desempenho',
    description: 'Indicadores de performance da farmácia',
    icon: <BarChart3 className="h-6 w-6" />,
    category: 'Gerencial',
  },
];

const categories = [...new Set(reports.map((r) => r.category))];

export function Relatorios() {
  const handleGenerateReport = (reportTitle: string) => {
    console.log('Generating report:', reportTitle);
    // TODO: Implement report generation
  };

  return (
    <div className="space-y-4 sm:space-y-6">
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">Relatórios</h1>
        <p className="text-sm sm:text-base text-muted-foreground">
          Gere relatórios detalhados para análise e tomada de decisão
        </p>
      </div>

      {categories.map((category) => (
        <div key={category} className="space-y-4">
          <h2 className="text-lg sm:text-xl font-semibold">{category}</h2>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {reports
              .filter((report) => report.category === category)
              .map((report) => (
                <Card key={report.title} className="hover:shadow-md transition-shadow">
                  <CardHeader>
                    <div className="flex items-center gap-3">
                      <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary shrink-0">
                        {report.icon}
                      </div>
                      <div>
                        <CardTitle className="text-base sm:text-lg">{report.title}</CardTitle>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <CardDescription className="mb-4 text-sm">
                      {report.description}
                    </CardDescription>
                    <Button
                      variant="outline"
                      className="w-full"
                      onClick={() => handleGenerateReport(report.title)}
                    >
                      <Download className="mr-2 h-4 w-4" />
                      Gerar Relatório
                    </Button>
                  </CardContent>
                </Card>
              ))}
          </div>
        </div>
      ))}
    </div>
  );
}
