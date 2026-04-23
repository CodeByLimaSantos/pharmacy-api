import { useState, useEffect, useCallback } from 'react';
import { Search, Plus, Minus, Trash2, ShoppingCart } from 'lucide-react';
import type { Customer, Product, CreateSaleDTO, CreateSaleItemDTO, PaymentMethod as PaymentMethodType } from '../../types';
import { PaymentMethod, PaymentMethodLabels } from '../../types';
import { Button, Select, FormDialog, Card, Badge } from '../ui';
import { customerService, productService } from '../../services';

interface CartItem extends CreateSaleItemDTO {
  product: Product;
}

interface SaleFormProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateSaleDTO) => Promise<void>;
}

const paymentMethods: PaymentMethodType[] = [
  PaymentMethod.CASH,
  PaymentMethod.CREDIT_CARD,
  PaymentMethod.DEBIT_CARD,
  PaymentMethod.PIX,
];

export function SaleForm({ isOpen, onClose, onSubmit }: SaleFormProps) {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedCustomer, setSelectedCustomer] = useState<number | null>(null);
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethodType>(PaymentMethod.CASH);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);

  const loadData = useCallback(async () => {
    setLoadingData(true);
    try {
      const [customersRes, productsRes] = await Promise.all([
        customerService.getAll(),
        productService.getAll(),
      ]);
      setCustomers(customersRes.filter(c => c.active));
      setProducts(productsRes.filter(p => p.active && p.currentStock > 0));
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    } finally {
      setLoadingData(false);
    }
  }, []);

  useEffect(() => {
    if (isOpen) {
      loadData();
      setCart([]);
      setSelectedCustomer(null);
      setPaymentMethod(PaymentMethod.CASH);
      setSearchTerm('');
    }
  }, [isOpen, loadData]);

  const filteredProducts = products.filter(product =>
    product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.barcode?.includes(searchTerm)
  );

  const addToCart = (product: Product) => {
    const existingItem = cart.find(item => item.productId === product.id);
    
    if (existingItem) {
      if (existingItem.quantity >= product.currentStock) {
        alert('Estoque insuficiente');
        return;
      }
      setCart(cart.map(item =>
        item.productId === product.id
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      setCart([...cart, {
        productId: product.id,
        product,
        quantity: 1,
        unitPrice: product.priceSale,
        discount: 0,
      }]);
    }
  };

  const updateQuantity = (productId: number, delta: number) => {
    setCart(cart.map(item => {
      if (item.productId !== productId) return item;
      
      const newQuantity = item.quantity + delta;
      if (newQuantity <= 0) return item;
      if (newQuantity > item.product.currentStock) {
        alert('Estoque insuficiente');
        return item;
      }
      
      return { ...item, quantity: newQuantity };
    }));
  };

  const removeFromCart = (productId: number) => {
    setCart(cart.filter(item => item.productId !== productId));
  };

  const updateDiscount = (productId: number, discount: number) => {
    setCart(cart.map(item =>
      item.productId === productId
        ? { ...item, discount: Math.max(0, Math.min(discount, (item.unitPrice || 0) * item.quantity)) }
        : item
    ));
  };

  const subtotal = cart.reduce((sum, item) => sum + ((item.unitPrice || 0) * item.quantity), 0);
  const totalDiscount = cart.reduce((sum, item) => sum + (item.discount || 0), 0);
  const total = subtotal - totalDiscount;

  const handleSubmit = async () => {
    if (cart.length === 0) {
      alert('Adicione pelo menos um produto');
      return;
    }

    setLoading(true);
    try {
      const saleData: CreateSaleDTO = {
        customerId: selectedCustomer || undefined,
        paymentMethod,
        items: cart.map(({ productId, quantity, unitPrice, discount }) => ({
          productId,
          quantity,
          unitPrice,
          discount,
        })),
      };
      await onSubmit(saleData);
      onClose();
    } catch (error) {
      console.error('Erro ao registrar venda:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);

  return (
    <FormDialog
      isOpen={isOpen}
      onClose={onClose}
      title="Nova Venda"
      className="max-w-4xl"
    >
      {loadingData ? (
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin h-8 w-8 border-4 border-primary border-t-transparent rounded-full" />
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-6">
          {/* Produtos */}
          <div className="space-y-4">
            <h3 className="font-medium">Produtos</h3>
            
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <input
                type="text"
                placeholder="Buscar produto..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20"
              />
            </div>

            <div className="max-h-80 overflow-y-auto space-y-2">
              {filteredProducts.map(product => (
                <Card
                  key={product.id}
                  className="p-3 cursor-pointer hover:bg-muted/50 transition-colors"
                  onClick={() => addToCart(product)}
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-medium text-sm">{product.name}</p>
                      <p className="text-xs text-muted-foreground">
                        Estoque: {product.currentStock}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="font-medium text-sm text-primary">
                        {formatCurrency(product.priceSale)}
                      </p>
                      <Plus className="h-4 w-4 text-muted-foreground ml-auto" />
                    </div>
                  </div>
                </Card>
              ))}
              {filteredProducts.length === 0 && (
                <p className="text-center text-muted-foreground py-4">
                  Nenhum produto encontrado
                </p>
              )}
            </div>
          </div>

          {/* Carrinho */}
          <div className="space-y-4">
            <div className="flex items-center gap-2">
              <ShoppingCart className="h-5 w-5" />
              <h3 className="font-medium">Carrinho</h3>
              {cart.length > 0 && (
                <Badge variant="default">{cart.length}</Badge>
              )}
            </div>

            <Select
              label="Cliente (opcional)"
              value={selectedCustomer?.toString() || ''}
              onChange={(value) => setSelectedCustomer(value ? parseInt(value) : null)}
              options={[
                { value: '', label: 'Consumidor final' },
                ...customers.map(c => ({ value: c.id.toString(), label: c.name }))
              ]}
            />

            <Select
              label="Forma de Pagamento"
              value={paymentMethod}
              onChange={(value) => setPaymentMethod(value as PaymentMethodType)}
              options={paymentMethods.map(pm => ({
                value: pm,
                label: PaymentMethodLabels[pm],
              }))}
            />

            <div className="max-h-48 overflow-y-auto space-y-2">
              {cart.map(item => (
                <Card key={item.productId} className="p-3">
                  <div className="flex justify-between items-start mb-2">
                    <p className="font-medium text-sm flex-1">{item.product.name}</p>
                    <button
                      type="button"
                      onClick={() => removeFromCart(item.productId)}
                      className="text-destructive hover:text-destructive/80"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                  
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <button
                        type="button"
                        onClick={() => updateQuantity(item.productId, -1)}
                        className="p-1 rounded border hover:bg-muted"
                      >
                        <Minus className="h-3 w-3" />
                      </button>
                      <span className="w-8 text-center">{item.quantity}</span>
                      <button
                        type="button"
                        onClick={() => updateQuantity(item.productId, 1)}
                        className="p-1 rounded border hover:bg-muted"
                      >
                        <Plus className="h-3 w-3" />
                      </button>
                    </div>
                    
                    <div className="text-right">
                      <p className="text-sm">
                        {formatCurrency((item.unitPrice || 0) * item.quantity)}
                      </p>
                      <input
                        type="number"
                        min="0"
                        step="0.01"
                        placeholder="Desconto"
                        value={item.discount || ''}
                        onChange={(e) => updateDiscount(item.productId, parseFloat(e.target.value) || 0)}
                        className="w-20 text-xs px-2 py-1 border rounded mt-1"
                      />
                    </div>
                  </div>
                </Card>
              ))}
              
              {cart.length === 0 && (
                <p className="text-center text-muted-foreground py-8">
                  Carrinho vazio
                </p>
              )}
            </div>

            {/* Totais */}
            <Card className="p-4 bg-muted/50">
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Subtotal</span>
                  <span>{formatCurrency(subtotal)}</span>
                </div>
                {totalDiscount > 0 && (
                  <div className="flex justify-between text-destructive">
                    <span>Desconto</span>
                    <span>-{formatCurrency(totalDiscount)}</span>
                  </div>
                )}
                <div className="flex justify-between text-lg font-bold pt-2 border-t">
                  <span>Total</span>
                  <span className="text-primary">{formatCurrency(total)}</span>
                </div>
              </div>
            </Card>

            <div className="flex gap-3">
              <Button
                type="button"
                variant="outline"
                onClick={onClose}
                className="flex-1"
              >
                Cancelar
              </Button>
              <Button
                type="button"
                onClick={handleSubmit}
                loading={loading}
                disabled={cart.length === 0}
                className="flex-1"
              >
                Finalizar Venda
              </Button>
            </div>
          </div>
        </div>
      )}
    </FormDialog>
  );
}
