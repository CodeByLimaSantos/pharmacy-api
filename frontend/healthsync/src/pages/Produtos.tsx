import { useEffect, useState, useCallback, useRef } from 'react';
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
import { ProductForm } from '../components/forms';
import { productService } from '../services';
import { ProductCategoryTypeLabels } from '../types';
import type { Product, CreateProductDTO } from '../types';
import { formatCurrency } from '../utils';

export function Produtos() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  // Track which product IDs have a pending delete to disable their button
  const [deletingIds, setDeletingIds] = useState<Set<number>>(new Set());
  const toast = useToast();

  // Stable ref so loadProducts never changes identity and avoids
  // unnecessary effect re-runs while still always calling the latest toast.
  const toastRef = useRef(toast);
  toastRef.current = toast;

  const loadProducts = useCallback(async () => {
    try {
      setLoading(true);
      const data = await productService.getAll();
      setProducts(data);
    } catch (error) {
      console.error('Error loading products:', error);
      toastRef.current.error('Erro ao carregar produtos', 'Não foi possível carregar a lista de produtos');
    } finally {
      setLoading(false);
    }
  }, []); // empty deps — stable for the lifetime of the component

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  const filteredProducts = products.filter(
    (product) =>
      product.name.toLowerCase().includes(search.toLowerCase()) ||
      product.barcode?.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = () => {
    setEditingProduct(null);
    setIsFormOpen(true);
  };

  const handleEdit = (product: Product) => {
    setEditingProduct(product);
    setIsFormOpen(true);
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Tem certeza que deseja excluir este produto?')) return;

    setDeletingIds((prev) => new Set(prev).add(id));
    try {
      await productService.delete(id);
      setProducts((prev) => prev.filter((p) => p.id !== id));
      toast.success('Produto excluído', 'O produto foi removido com sucesso');
    } catch (error) {
      console.error('Error deleting product:', error);
      toast.error('Erro ao excluir', 'Não foi possível excluir o produto');
    } finally {
      setDeletingIds((prev) => {
        const next = new Set(prev);
        next.delete(id);
        return next;
      });
    }
  };

  // Does NOT rethrow — the form receives success/failure via the returned boolean
  const handleSubmit = async (data: CreateProductDTO): Promise<boolean> => {
    try {
      if (editingProduct) {
        const updated = await productService.update(editingProduct.id, data);
        setProducts((prev) => prev.map((p) => (p.id === updated.id ? updated : p)));
        toast.success('Produto atualizado', 'Os dados foram salvos com sucesso');
      } else {
        const created = await productService.create(data);
        setProducts((prev) => [...prev, created]);
        toast.success('Produto cadastrado', 'Novo produto adicionado com sucesso');
      }
      handleCloseForm();
      return true;
    } catch (error) {
      console.error('Error saving product:', error);
      toast.error('Erro ao salvar', 'Não foi possível salvar os dados do produto');
      return false;
    }
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setEditingProduct(null);
  };

  return (
    <div className="space-y-4 sm:space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">Produtos</h1>
          <p className="text-sm sm:text-base text-muted-foreground">Gerencie o catálogo de produtos</p>
        </div>
        <Button onClick={handleCreate} className="w-full sm:w-auto">
          <Plus className="mr-2 h-4 w-4" />
          Novo Produto
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <CardTitle>Lista de Produtos</CardTitle>
            <div className="relative w-full sm:w-auto">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Buscar produtos..."
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
              <Loading text="Carregando produtos..." />
            </div>
          ) : filteredProducts.length === 0 ? (
            <EmptyState
              type={search ? 'no-results' : 'no-data'}
              title={search ? 'Nenhum produto encontrado' : 'Nenhum produto cadastrado'}
              description={
                search
                  ? 'Tente ajustar sua busca'
                  : 'Comece adicionando um novo produto ao catálogo'
              }
              action={
                !search
                  ? {
                      label: 'Adicionar Produto',
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
                      <TableHead className="hidden sm:table-cell">Categoria</TableHead>
                      <TableHead>Preço</TableHead>
                      <TableHead>Estoque</TableHead>
                      <TableHead className="hidden md:table-cell">Status</TableHead>
                      <TableHead className="w-[80px] sm:w-[100px]">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredProducts.map((product) => (
                      <TableRow key={product.id}>
                        <TableCell className="font-medium">{product.name}</TableCell>
                        <TableCell className="hidden sm:table-cell">
                          <Badge variant="secondary">
                            {ProductCategoryTypeLabels[product.category ?? 'MEDICAMENTOS']}
                          </Badge>
                        </TableCell>
                        <TableCell>{formatCurrency(product.priceSale ?? 0)}</TableCell>
                        <TableCell>
                          <Badge
                            variant={
                              (product.currentStock ?? 0) <= (product.minStock ?? 0)
                                ? 'destructive'
                                : 'default'
                            }
                          >
                            {product.currentStock ?? 0}
                          </Badge>
                        </TableCell>
                        <TableCell className="hidden md:table-cell">
                          {product.active ? (
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
                              disabled={deletingIds.has(product.id)}
                              onClick={() => handleEdit(product)}
                            >
                              <Edit className="h-4 w-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="icon"
                              disabled={deletingIds.has(product.id)}
                              onClick={() => handleDelete(product.id)}
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

      <ProductForm
        product={editingProduct}
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        onSubmit={handleSubmit}
      />
    </div>
  );
}