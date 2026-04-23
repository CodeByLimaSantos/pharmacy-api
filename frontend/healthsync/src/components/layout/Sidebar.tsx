import { Link, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Package,
  ShoppingCart,
  Users,
  Truck,
  DollarSign,
  BarChart3,
  Settings,
  X,
  LogOut,
} from 'lucide-react';
import { cn } from '../../utils';
import { Button } from '../ui/Button';
import { useSidebarStore, useUserStore } from '../../store';
import { authService } from '../../services';
import { useMediaQuery } from '../../hooks';

interface NavItem {
  title: string;
  href: string;
  icon: React.ReactNode;
}

const navItems: NavItem[] = [
  {
    title: 'Dashboard',
    href: '/',
    icon: <LayoutDashboard className="h-5 w-5" />,
  },
  {
    title: 'Produtos',
    href: '/produtos',
    icon: <Package className="h-5 w-5" />,
  },
  {
    title: 'Vendas',
    href: '/vendas',
    icon: <ShoppingCart className="h-5 w-5" />,
  },
  {
    title: 'Clientes',
    href: '/clientes',
    icon: <Users className="h-5 w-5" />,
  },
  {
    title: 'Fornecedores',
    href: '/fornecedores',
    icon: <Truck className="h-5 w-5" />,
  },
  {
    title: 'Financeiro',
    href: '/financeiro',
    icon: <DollarSign className="h-5 w-5" />,
  },
  {
    title: 'Relatórios',
    href: '/relatorios',
    icon: <BarChart3 className="h-5 w-5" />,
  },
];

export function Sidebar() {
  const location = useLocation();
  const { isOpen, setOpen, isCollapsed } = useSidebarStore();
  const { logout } = useUserStore();
  const isMobile = useMediaQuery('(max-width: 768px)');

  const handleLinkClick = () => {
    if (isMobile) {
      setOpen(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    logout();
    window.location.href = '/login';
  };

  return (
    <>
      {/* Mobile overlay */}
      {isMobile && isOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/50"
          onClick={() => setOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          'fixed left-0 top-0 z-50 flex h-full flex-col border-r bg-background transition-all duration-300 md:sticky md:z-auto',
          isOpen ? 'w-64' : '-translate-x-full md:translate-x-0',
          isCollapsed && !isMobile && 'md:w-16',
          !isCollapsed && 'md:w-64'
        )}
      >
        {/* Logo */}
        <div className="flex h-16 items-center justify-between border-b px-4">
          <Link
            to="/"
            className={cn(
              'flex items-center gap-2 font-bold text-xl text-primary',
              isCollapsed && !isMobile && 'md:hidden'
            )}
            onClick={handleLinkClick}
          >
            <Package className="h-6 w-6" />
            <span>HealthSync</span>
          </Link>
          {isCollapsed && !isMobile && (
            <Link to="/" className="hidden md:flex">
              <Package className="h-6 w-6 text-primary" />
            </Link>
          )}
          {isMobile && (
            <Button variant="ghost" size="icon" onClick={() => setOpen(false)}>
              <X className="h-5 w-5" />
            </Button>
          )}
        </div>

        {/* Navigation */}
        <nav className="flex-1 space-y-1 p-2">
          {navItems.map((item) => {
            const isActive = location.pathname === item.href;

            return (
              <Link
                key={item.href}
                to={item.href}
                onClick={handleLinkClick}
                className={cn(
                  'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-primary text-primary-foreground'
                    : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground',
                  isCollapsed && !isMobile && 'md:justify-center md:px-2'
                )}
                title={isCollapsed && !isMobile ? item.title : undefined}
              >
                {item.icon}
                <span className={cn(isCollapsed && !isMobile && 'md:hidden')}>
                  {item.title}
                </span>
              </Link>
            );
          })}
        </nav>

        {/* Settings & Logout */}
        <div className="border-t p-2 space-y-1">
          <Link
            to="/configuracoes"
            onClick={handleLinkClick}
            className={cn(
              'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground',
              isCollapsed && !isMobile && 'md:justify-center md:px-2'
            )}
            title={isCollapsed && !isMobile ? 'Configurações' : undefined}
          >
            <Settings className="h-5 w-5" />
            <span className={cn(isCollapsed && !isMobile && 'md:hidden')}>
              Configurações
            </span>
          </Link>
          <button
            onClick={handleLogout}
            className={cn(
              'flex w-full items-center gap-3 rounded-md px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-destructive/10 hover:text-destructive',
              isCollapsed && !isMobile && 'md:justify-center md:px-2'
            )}
            title={isCollapsed && !isMobile ? 'Sair' : undefined}
          >
            <LogOut className="h-5 w-5" />
            <span className={cn(isCollapsed && !isMobile && 'md:hidden')}>
              Sair
            </span>
          </button>
        </div>
      </aside>
    </>
  );
}
