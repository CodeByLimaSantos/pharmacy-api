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
import { SupplierForm } from '../components/forms';
import { supplierService } from '../services';
import type { Supplier, CreateSupplierDTO } from '../types';
import { formatCNPJ, formatPhone } from '../utils';

export function Fornecedores() {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState<Supplier | null>(null);
  const toast = useToast();

  const loadSuppliers = useCallback(async () => {
    try {
      setLoading(true);
      const data = await supplierService.getAll();
      setSuppliers(data);
    } catch (error) {
      console.error('Error loading suppliers:', error);
      toast.error('Erro ao carregar fornecedores', 'Não foi possível carregar a lista de fornecedores');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadSuppliers();
  }, [loadSuppliers]);

  const filteredSuppliers = suppliers.filter(
    (supplier) =>
      supplier.name.toLowerCase().includes(search.toLowerCase()) ||
      supplier.cnpj?.includes(search)
  );

  const handleCreate = () => {
    setEditingSupplier(null);
    setIsFormOpen(true);
  };

  const handleEdit = (supplier: Supplier) => {
    setEditingSupplier(supplier);
    setIsFormOpen(true);
  };

  const handleDelete = async (id: number) => {
    if (confirm('Tem certeza que deseja excluir este fornecedor?')) {
      try {
        await supplierService.delete(id);
        setSuppliers(suppliers.filter((s) => s.id !== id));
        toast.success('Fornecedor excluído', 'O fornecedor foi removido com sucesso');
      } catch (error) {
        console.error('Error deleting supplier:', error);
        toast.error('Erro ao excluir', 'Não foi possível excluir o fornecedor');
      }
    }
  };

  const handleSubmit = async (data: CreateSupplierDTO) => {
    try {
      if (editingSupplier) {
        const updated = await supplierService.update(editingSupplier.id, data);
        setSuppliers(suppliers.map((s) => (s.id === updated.id ? updated : s)));
        toast.success('Fornecedor atualizado', 'Os dados foram salvos com sucesso');
      } else {
        const created = await supplierService.create(data);
        setSuppliers([...suppliers, created]);
        toast.success('Fornecedor cadastrado', 'Novo fornecedor adicionado com sucesso');
      }
    } catch (error) {
      console.error('Error saving supplier:', error);
      toast.error('Erro ao salvar', 'Não foi possível salvar os dados do fornecedor');
      throw error; // Re-throw to keep form open
    }
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setEditingSupplier(null);
  };

  return (
    <div className="space-y-4 sm:space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">Fornecedores</h1>
          <p className="text-sm sm:text-base text-muted-foreground">Gerencie os fornecedores da farmácia</p>
        </div>
        <Button onClick={handleCreate} className="w-full sm:w-auto">
          <Plus className="mr-2 h-4 w-4" />
          Novo Fornecedor
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <CardTitle>Lista de Fornecedores</CardTitle>
            <div className="relative w-full sm:w-auto">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Buscar fornecedores..."
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
              <Loading text="Carregando fornecedores..." />
            </div>
          ) : filteredSuppliers.length === 0 ? (
            <EmptyState
              type={search ? 'no-results' : 'no-data'}
              title={search ? 'Nenhum fornecedor encontrado' : 'Nenhum fornecedor cadastrado'}
              description={
                search
                  ? 'Tente ajustar sua busca'
                  : 'Comece adicionando um novo fornecedor'
              }
              action={
                !search
                  ? {
                      label: 'Adicionar Fornecedor',
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
                      <TableHead className="hidden sm:table-cell">CNPJ</TableHead>
                      <TableHead className="hidden md:table-cell">Contato</TableHead>
                      <TableHead>Telefone</TableHead>
                      <TableHead className="hidden lg:table-cell">Status</TableHead>
                      <TableHead className="w-[80px] sm:w-[100px]">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredSuppliers.map((supplier) => (
                      <TableRow key={supplier.id}>
                        <TableCell className="font-medium">{supplier.name}</TableCell>
                        <TableCell className="hidden sm:table-cell">{formatCNPJ(supplier.cnpj)}</TableCell>
                        <TableCell className="hidden md:table-cell">{supplier.contactName || '-'}</TableCell>
                        <TableCell>{supplier.phone ? formatPhone(supplier.phone) : '-'}</TableCell>
                        <TableCell className="hidden lg:table-cell">
                          {supplier.active ? (
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
                              onClick={() => handleEdit(supplier)}
                            >
                              <Edit className="h-4 w-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleDelete(supplier.id)}
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

      <SupplierForm
        supplier={editingSupplier}
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        onSubmit={handleSubmit}
      />
    </div>
  );
}
