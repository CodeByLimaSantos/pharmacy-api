import { useEffect, useState, useCallback } from 'react';
import { Plus, Search, Edit, Trash2 } from 'lucide-react';
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
  useToast,
} from '../components/ui';
import { CustomerForm } from '../components/forms';
import { customerService } from '../services';
import type { Customer, CreateCustomerDTO } from '../types';
import { formatCPF, formatPhone } from '../utils';

const extractApiErrorMessage = (error: unknown): string => {
  const payload = (error as { response?: { data?: {
    details?: string;
    message?: string;
    fieldErrors?: Record<string, string>;
  } } })?.response?.data;

  const firstFieldError = payload?.fieldErrors
    ? Object.values(payload.fieldErrors)[0]
    : undefined;

  return firstFieldError || payload?.details || payload?.message || 'Não foi possível salvar os dados do cliente';
};

export function Clientes() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState<Customer | null>(null);
  const toast = useToast();

  const loadCustomers = useCallback(async () => {
    try {
      setLoading(true);
      const data = await customerService.getAll();
      setCustomers(data);
    } catch (error) {
      console.error('Error loading customers:', error);
      toast.error('Erro ao carregar clientes', 'Não foi possível carregar a lista de clientes');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadCustomers();
  }, [loadCustomers]);

  const filteredCustomers = customers.filter(
    (customer) =>
      customer.name.toLowerCase().includes(search.toLowerCase()) ||
      customer.cpf?.includes(search)
  );

  const handleCreate = () => {
    setEditingCustomer(null);
    setIsFormOpen(true);
  };

  const handleEdit = (customer: Customer) => {
    setEditingCustomer(customer);
    setIsFormOpen(true);
  };

  const handleDelete = async (id: number) => {
    if (confirm('Tem certeza que deseja excluir este cliente?')) {
      try {
        await customerService.delete(id);
        setCustomers(customers.filter((c) => c.id !== id));
        toast.success('Cliente excluído', 'O cliente foi removido com sucesso');
      } catch (error) {
        console.error('Error deleting customer:', error);
        toast.error('Erro ao excluir', 'Não foi possível excluir o cliente');
      }
    }
  };

  const handleSubmit = async (data: CreateCustomerDTO) => {
    try {
      if (editingCustomer) {
        const updated = await customerService.update(editingCustomer.id, data);
        setCustomers(customers.map((c) => (c.id === updated.id ? updated : c)));
        toast.success('Cliente atualizado', 'Os dados foram salvos com sucesso');
      } else {
        const created = await customerService.create(data);
        setCustomers([...customers, created]);
        toast.success('Cliente cadastrado', 'Novo cliente adicionado com sucesso');
      }
    } catch (error) {
      console.error('Error saving customer:', error);
      toast.error('Erro ao salvar', extractApiErrorMessage(error));
      throw error;
    }
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setEditingCustomer(null);
  };

  return (
    <div className="space-y-4 sm:space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">Clientes</h1>
          <p className="text-sm sm:text-base text-muted-foreground">Gerencie os clientes da farmácia</p>
        </div>
        <Button onClick={handleCreate} className="w-full sm:w-auto">
          <Plus className="mr-2 h-4 w-4" />
          Novo Cliente
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <CardTitle>Lista de Clientes</CardTitle>
            <div className="relative w-full sm:w-auto">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Buscar clientes..."
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
              <Loading text="Carregando clientes..." />
            </div>
          ) : filteredCustomers.length === 0 ? (
            <EmptyState
              type={search ? 'no-results' : 'no-data'}
              title={search ? 'Nenhum cliente encontrado' : 'Nenhum cliente cadastrado'}
              description={
                search
                  ? 'Tente ajustar sua busca'
                  : 'Comece adicionando um novo cliente'
              }
              action={
                !search
                  ? {
                      label: 'Adicionar Cliente',
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
                      <TableHead>Nome</TableHead>
                      <TableHead className="hidden sm:table-cell">CPF</TableHead>
                      <TableHead>Telefone</TableHead>
                      <TableHead className="hidden md:table-cell">Email</TableHead>
                      <TableHead className="hidden lg:table-cell">Status</TableHead>
                      <TableHead className="w-[80px] sm:w-[100px]">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredCustomers.map((customer) => (
                      <TableRow key={customer.id}>
                        <TableCell className="font-medium">{customer.name}</TableCell>
                        <TableCell className="hidden sm:table-cell">{formatCPF(customer.cpf)}</TableCell>
                        <TableCell>{customer.phone ? formatPhone(customer.phone) : '-'}</TableCell>
                        <TableCell className="hidden md:table-cell">{customer.email || '-'}</TableCell>
                        <TableCell className="hidden lg:table-cell">
                          {customer.active ? (
                            <Badge variant="default">Ativo</Badge>
                          ) : (
                            <Badge variant="outline">Inativo</Badge>
                          )}
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center gap-1 sm:gap-2">
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleEdit(customer)}
                            >
                              <Edit className="h-4 w-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleDelete(customer.id)}
                            >
                              <Trash2 className="h-4 w-4 text-destructive" />
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

      <CustomerForm
        customer={editingCustomer}
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        onSubmit={handleSubmit}
      />
    </div>
  );
}
