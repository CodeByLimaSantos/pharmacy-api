# FarmaERP - Sistema de Gestão para Farmácias

Frontend React para o sistema de gestão de farmácias FarmaERP, desenvolvido com React 19, TypeScript, Vite e Tailwind CSS.

## Tecnologias

- **React 19** - Framework de UI
- **TypeScript 6** - Tipagem estática
- **Vite 8** - Build tool e dev server
- **Tailwind CSS 4** - Estilização
- **Zustand** - Gerenciamento de estado
- **React Router DOM 6** - Roteamento
- **Axios** - Cliente HTTP
- **Radix UI** - Componentes acessíveis
- **Lucide React** - Ícones
- **Vitest** - Testes unitários

## Estrutura do Projeto

```
src/
├── components/
│   ├── forms/           # Formulários (ProductForm, CustomerForm, etc.)
│   ├── layout/          # Layout (Header, Sidebar, Layout)
│   └── ui/              # Componentes UI reutilizáveis (Button, Card, Table, etc.)
├── hooks/               # Custom hooks (useMediaQuery, useDebounce, etc.)
├── pages/               # Páginas da aplicação
├── services/            # Serviços de API (productService, saleService, etc.)
├── store/               # Zustand stores (sidebar, theme, toast, etc.)
├── test/                # Testes unitários
├── types/               # TypeScript types e interfaces
└── utils/               # Funções utilitárias (formatCurrency, formatDate, etc.)
```

## Funcionalidades

- **Dashboard** - Visão geral com métricas principais
- **Produtos** - Cadastro e gestão de produtos
- **Vendas** - Registro e histórico de vendas
- **Clientes** - Cadastro de clientes
- **Fornecedores** - Gestão de fornecedores
- **Financeiro** - Contas a pagar/receber
- **Relatórios** - Geração de relatórios

## Instalação

```bash
# Instalar dependências
npm install

# Iniciar servidor de desenvolvimento
npm run dev

# Build para produção
npm run build

# Executar testes
npm run test

# Executar testes (single run)
npm run test:run

# Executar testes com cobertura
npm run test:coverage
```

## Scripts Disponíveis

| Script | Descrição |
|--------|-----------|
| `npm run dev` | Inicia servidor de desenvolvimento |
| `npm run build` | Gera build de produção |
| `npm run preview` | Preview do build de produção |
| `npm run lint` | Executa ESLint |
| `npm run test` | Executa testes em modo watch |
| `npm run test:run` | Executa testes uma vez |
| `npm run test:coverage` | Executa testes com cobertura |

## Configuração da API

O frontend conecta-se ao backend Spring Boot na porta 8080. Configure a URL base da API em:

```typescript
// src/services/api.ts
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});
```

## Estado Global (Zustand)

O projeto utiliza Zustand para gerenciamento de estado:

- `useSidebarStore` - Estado da sidebar (aberta/fechada)
- `useThemeStore` - Tema da aplicação
- `useToastStore` - Notificações toast
- `useAppStore` - Estado global (loading, error)

## Responsividade

O layout é totalmente responsivo:
- **Mobile** (< 768px): Sidebar drawer, tabelas com scroll horizontal
- **Tablet** (768px - 1024px): Layout adaptado
- **Desktop** (> 1024px): Layout completo com sidebar fixa

## Testes

Os testes estão em `src/test/` e cobrem:
- Funções utilitárias (formatters, cn)
- Stores Zustand
- Componentes UI (Button)

```bash
# Executar todos os testes
npm run test:run

# Com cobertura
npm run test:coverage
```

## Padrões de Código

- **TypeScript strict mode**
- **ESLint** para linting
- **Componentes funcionais** com hooks
- **Barrel exports** em cada módulo (index.ts)
- **Separação de concerns** (UI, lógica, tipos)

## Licença

Este projeto é proprietário e de uso interno.
