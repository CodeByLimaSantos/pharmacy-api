# Changelog

## [1.0.0] - 2024

### Adicionado

#### Estrutura Base
- Projeto React 19 + TypeScript 6 + Vite 8
- Configuração Tailwind CSS 4
- Estrutura de pastas organizada (components, hooks, pages, services, store, types, utils)
- Sistema de barrel exports para imports limpos

#### Componentes UI
- **Button** - Botão com variantes (default, destructive, outline, secondary, ghost, link)
- **Card** - Card com header, content, footer, title, description
- **Table** - Tabela responsiva com header, body, row, cell
- **Input** - Campo de entrada de texto
- **Select** - Select dropdown com Radix UI
- **Badge** - Badge para status e categorias
- **Dialog/FormDialog** - Modal de diálogo para formulários
- **Loading** - Indicador de carregamento
- **EmptyState** - Estado vazio para listas

#### Layout
- **Header** - Cabeçalho com toggle de sidebar e dropdown de usuário
- **Sidebar** - Menu lateral com navegação e suporte mobile (drawer)
- **Layout** - Componente wrapper com sidebar e área de conteúdo

#### Páginas
- **Dashboard** - Visão geral com cards de métricas
- **Produtos** - CRUD completo de produtos
- **Vendas** - Registro de vendas com itens
- **Clientes** - Cadastro de clientes
- **Fornecedores** - Gestão de fornecedores
- **Financeiro** - Contas a pagar/receber
- **Relatórios** - Cards de relatórios disponíveis

#### Formulários
- **ProductForm** - Cadastro/edição de produtos
- **CustomerForm** - Cadastro/edição de clientes
- **SupplierForm** - Cadastro/edição de fornecedores
- **SaleForm** - Registro de vendas
- **FinancialForm** - Cadastro de contas financeiras

#### Services (API)
- `productService` - CRUD de produtos + estoque baixo
- `saleService` - CRUD de vendas
- `customerService` - CRUD de clientes
- `supplierService` - CRUD de fornecedores
- `financialService` - CRUD de contas + pendentes/vencidas + resumo
- `inventoryService` - Movimentações e lotes vencendo

#### Estado Global (Zustand)
- `useSidebarStore` - Controle de sidebar (persisted)
- `useThemeStore` - Tema da aplicação (persisted)
- `useToastStore` - Sistema de notificações toast
- `useAppStore` - Loading/error global
- Stores de cache para produtos, clientes, fornecedores, dashboard

#### Hooks Customizados
- `useMediaQuery` - Detecção de breakpoints
- `useDebounce` - Debounce de valores
- `useLocalStorage` - Persistência em localStorage
- `useOnClickOutside` - Detecção de cliques externos
- `usePrevious` - Valor anterior de state
- `useToggle` - Toggle booleano

#### Utilitários
- `cn` - Merge de classes Tailwind
- `formatCurrency` - Formatação de moeda (R$)
- `formatDate` - Formatação de data (dd/mm/yyyy)
- `formatCPF` - Formatação de CPF
- `formatCNPJ` - Formatação de CNPJ
- `formatPhone` - Formatação de telefone

#### Responsividade
- Layout responsivo para mobile, tablet e desktop
- Sidebar drawer no mobile
- Tabelas com scroll horizontal
- Cards em grid responsivo
- Formulários adaptados para telas menores

#### Testes
- Configuração Vitest com React Testing Library
- Testes de utilitários (formatters, cn)
- Testes de stores Zustand
- Testes de componentes UI

#### Feedback ao Usuário
- Sistema de toast notifications (success, error, warning, info)
- Loading states em todas as operações
- Empty states com ações
- Confirmação antes de excluir

### Tipos
- Interfaces completas para todas as entidades
- DTOs para criação/atualização
- Enums com labels em português
- Types para props de componentes
