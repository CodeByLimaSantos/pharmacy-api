import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { Layout } from './components/layout';
import {
  Login,
  Dashboard,
  Produtos,
  Vendas,
  Clientes,
  Fornecedores,
  Financeiro,
  Relatorios,
} from './pages';
import { authService } from './services';

// Private route guard — redirects to /login if no JWT token
function PrivateRoute({ children }: { children: React.ReactNode }) {
  if (!authService.isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
}

// Public route guard — redirects to / if already authenticated
function PublicRoute({ children }: { children: React.ReactNode }) {
  if (authService.isAuthenticated()) {
    return <Navigate to="/" replace />;
  }
  return <>{children}</>;
}

const router = createBrowserRouter([
  {
    path: '/login',
    element: (
      <PublicRoute>
        <Login />
      </PublicRoute>
    ),
  },
  {
    path: '/',
    element: (
      <PrivateRoute>
        <Layout />
      </PrivateRoute>
    ),
    children: [
      {
        index: true,
        element: <Dashboard />,
      },
      {
        path: 'produtos',
        element: <Produtos />,
      },
      {
        path: 'vendas',
        element: <Vendas />,
      },
      {
        path: 'clientes',
        element: <Clientes />,
      },
      {
        path: 'fornecedores',
        element: <Fornecedores />,
      },
      {
        path: 'financeiro',
        element: <Financeiro />,
      },
      {
        path: 'relatorios',
        element: <Relatorios />,
      },
      {
        path: 'configuracoes',
        element: <div className="text-center py-12 text-muted-foreground">Configurações - Em breve</div>,
      },
    ],
  },
]);

export function AppRouter() {
  return <RouterProvider router={router} />;
}
