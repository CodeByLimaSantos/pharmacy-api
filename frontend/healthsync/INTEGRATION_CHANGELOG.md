# HealthSync — Integração Frontend ↔ Backend

## Changelog Completo de Alterações

**Data:** 2026-04-20
**Escopo:** Integração completa do frontend React com o backend Spring Boot via Swagger/OpenAPI

---

## RESUMO DAS ALTERAÇÕES

| Fase | Descrição | Status |
|------|-----------|--------|
| Base de Integração | URL base, services, interceptor JWT | ✅ Concluído |
| Integração Swagger | Todos os 7 controllers mapeados | ✅ Concluído |
| Autenticação | Login/Register, JWT, rotas protegidas | ✅ Concluído |
| Identidade Visual | Nome "HealthSync" aplicado | ✅ Concluído |

---

## FASE 1 — BASE DE INTEGRAÇÃO

### [1] Configuração da baseURL (`src/constants/index.ts`)

**Antes:** `http://localhost:8080/api`
**Depois:** `http://localhost:8080`

O backend Spring Boot não utiliza prefixo `/api`. A URL base foi corrigida para apontar diretamente à raiz do servidor.

Variável de ambiente `VITE_API_URL` pode ser usada para customizar.

### [2] Camada de Services baseada no Swagger (`src/services/`)

Todos os 7 services foram reescritos para:
- Usar os endpoints reais do Swagger/Controller
- Mapear field names entre frontend e backend DTOs
- Tratar responses com `Array.isArray()` guard para segurança

**Services alterados:**
- `productService.ts` — ProductController endpoints
- `customerService.ts` — CustomerController endpoints
- `supplierService.ts` — SupplierController endpoints
- `saleService.ts` — SaleController endpoints
- `financialService.ts` — FinancialController endpoints
- `inventoryService.ts` — InventoryController endpoints

**Service criado:**
- `authService.ts` — AuthController endpoints (login/register)

### [3] Interceptador JWT (`src/services/api.ts`)

- Token lido de `localStorage` com chave `healthsync-token`
- Header `Authorization: Bearer <token>` adicionado automaticamente
- Response interceptor: auto-unwrap do `ApiResponse<T>` wrapper do backend
- Error interceptor: redirect para `/login` em 401, limpeza de token

### [4] Autenticação `/auth/login` e `/auth/register`

**Novo arquivo:** `src/services/authService.ts`

```typescript
authService.login({ username, password }) → token (string)
authService.register({ username, password, email, role }) → void
authService.logout() → limpa token
authService.isAuthenticated() → boolean
```

**Detalhes:**
- Login envia POST para `/auth/login` com `{ username, password }`
- Response esperada: `{ token: "jwt..." }`
- Token persistido em `localStorage['healthsync-token']`
- Register envia POST para `/auth/register` com `{ username, password, email, role }`
- Roles válidos: `ROLE_ADMIN`, `ROLE_CAIXA`

### [5] Persistência do Token

- Token armazenado em `localStorage` com chave `healthsync-token`
- Zustand `useUserStore` sincroniza com `localStorage` (setToken faz `localStorage.setItem`)
- Logout limpa tanto Zustand quanto localStorage

### [6] Proteção de Rotas (`src/routes.tsx`)

- `PrivateRoute` — verifica `authService.isAuthenticated()`, redireciona para `/login`
- `PublicRoute` — redireciona usuário autenticado para `/` (evita login duplo)
- Todas as rotas do app protegidas exceto `/login`

---

## FASE 2 — INTEGRAÇÃO COM SWAGGER

### [7] ProductController → `productService.ts`

| Operação | Frontend | Backend Endpoint |
|----------|----------|-----------------|
| Listar todos | `getAll()` | `GET /product/search` |
| Buscar por ID | `getById(id)` | `GET /product/search/{id}` |
| Detalhes | `getDetail(id)` | `GET /product/searchDetails/{id}` |
| Por fornecedor | `getBySupplier(id)` | `GET /product/supplier/{supplierId}` |
| Controlados | `getControlled()` | `GET /product/controlled` |
| Criar | `create(data)` | `POST /product/create` |
| Atualizar | `update(id, data)` | `PUT /product/{id}` |
| Excluir | `delete(id)` | `DELETE /product/delete/{id}` |

**Mapeamento de campos:**
- Frontend `category` → Backend `productCategoryType`
- Frontend `unitPrice` → ignorado (não existe no backend CreateProductDTO)
- Backend response mapeado com defaults para `currentStock=999`, `active=true`, `barcode=''`

### [8] CustomerController → `customerService.ts`

| Operação | Frontend | Backend Endpoint |
|----------|----------|-----------------|
| Listar todos | `getAll()` | `GET /customer/search` |
| Buscar por ID | `getById(id)` | `GET /customer/search/{id}` |
| Detalhes | `getDetail(id)` | `GET /customer/searchDetails/{id}` |
| Por CPF | `getByCpf(cpf)` | `GET /customer/cpf/{cpf}` |
| Criar | `create(data)` | `POST /customer/create` |
| Atualizar | `update(id, data)` | `PUT /customer/update/{id}` |
| Excluir | `delete(id)` | `DELETE /customer/delete/{id}` |

**Mapeamento:**
- Backend CreateCustomerDTO aceita apenas `{ name, cpf }` — campos extras do frontend filtrados
- Backend UpdateCustomerDTO aceita apenas `{ name }` — somente nome enviado no update
- Campos `email`, `phone`, `address`, `active` são defaults no frontend

### [9] FinancialController → `financialService.ts`

| Operação | Frontend | Backend Endpoint |
|----------|----------|-----------------|
| Listar todos | `getAll()` | `GET /financial` |
| Buscar por ID | `getById(id)` | `GET /financial/search/{id}` |
| Detalhes | `getDetail(id)` | `GET /financial/detail/{id}` |
| Pendentes | `getPending()` | `GET /financial/pending` |
| Vencidos | `getOverdue()` | `GET /financial/overdue` |
| A receber | `getReceivable()` | `GET /financial/receivable` |
| A pagar | `getPayable()` | `GET /financial/payable` |
| Resumo | `getSummary()` | `GET /financial/summary` |
| Criar | `create(data)` | `POST /financial/create` |
| Atualizar | `update(id, data)` | `PUT /financial/update/{id}` |
| Marcar pago | `markAsPaid(id, method)` | `POST /financial/{id}/pay?paymentMethod=...` |
| Excluir | `delete(id)` | `DELETE /financial/delete/{id}` |

**Mapeamento de enums:**
- Frontend `FinancialType.RECEIVABLE` = `'CONTA_A_RECEBER'` (antes: `'RECEIVABLE'`)
- Frontend `FinancialType.PAYABLE` = `'CONTA_A_PAGAR'` (antes: `'PAYABLE'`)
- Frontend `PaymentStatus.CANCELLED` = `'CANCELED'` (antes: `'CANCELLED'`)

### [10] InventoryController → `inventoryService.ts`

| Operação | Frontend | Backend Endpoint |
|----------|----------|-----------------|
| Listar lotes | `getAllLots()` | `GET /inventory/all` |
| Lote por ID | `getLotById(id)` | `GET /inventory/lots/{id}` |
| Estoque produto | `getProductStock(id)` | `GET /inventory/product/{id}/stock` |
| Melhor lote (FEFO) | `getBestLotForSale(id, qty)` | `GET /inventory/lots/best-sale` |
| Lotes vencidos | `getExpiredLots()` | `GET /inventory/lots/expired` |
| Lotes vencendo | `getExpiringLots(days)` | `GET /inventory/lots/expiring/{days}` |
| Auditoria | `getMovementsAudit()` | `GET /inventory/movements/audit` |
| Histórico lote | `getLotHistory(lotId)` | `GET /inventory/lots/{lotId}/history` |
| Criar lote | `createLot(data)` | `POST /inventory/create` |
| Entrada | `registerEntry(lotId, qty)` | `POST /inventory/lots/{lotId}/entry` |
| Saída venda | `registerSaleExit(lotId, qty)` | `POST /inventory/lots/{lotId}/sale-exit` |
| Ajuste entrada | `registerAdjustmentIn(...)` | `POST /inventory/lots/{lotId}/adjustment-in` |
| Ajuste saída | `registerAdjustmentOut(...)` | `POST /inventory/lots/{lotId}/adjustment-out` |
| Descarte | `registerDisposal(...)` | `POST /inventory/lots/{lotId}/disposal` |
| Processar vencidos | `processExpiredLots()` | `POST /inventory/process-expired` |

### [11] SupplierController → `supplierService.ts`

| Operação | Frontend | Backend Endpoint |
|----------|----------|-----------------|
| Listar todos | `getAll()` | `GET /suppliers/all` |
| Buscar por ID | `getById(id)` | `GET /suppliers/search/{id}` |
| Detalhes | `getDetail(id)` | `GET /suppliers/searchDetails/{id}` |
| Por CNPJ | `getByCnpj(cnpj)` | `GET /suppliers/cnpj/{cnpj}` |
| Criar | `create(data)` | `POST /suppliers/create` |
| Atualizar | `update(id, data)` | `PUT /suppliers/update/{id}` |
| Excluir | `delete(id)` | `DELETE /suppliers/remove/{id}` |

**Mapeamento:**
- Backend CreateSupplierDTO aceita: `{ name, cnpj, email, phone }`
- Backend UpdateSupplierDTO aceita: `{ name, email, phone }`
- Campos extras (`address`, `contactName`, `active`) são defaults no frontend

### [12] SaleController → `saleService.ts`

| Operação | Frontend | Backend Endpoint |
|----------|----------|-----------------|
| Listar todas | `getAll()` | `GET /sales/all` |
| Buscar por ID | `getById(id)` | `GET /sales/{id}` |
| Detalhes | `getDetail(id)` | `GET /sales/searchDetails/{id}` |
| Por cliente | `getByCustomer(id)` | `GET /sales/customer/{customerId}` |
| Total vendas | `getTotalAmount()` | `GET /sales/total/amount` |
| Total cliente | `getCustomerTotal(id)` | `GET /sales/customer/{id}/total` |
| Criar | `create(data)` | `POST /sales/create` |
| Cancelar | `cancel(id)` | `DELETE /sales/{id}` |

**Mapeamento:**
- Frontend `CreateSaleItemDTO.unitPrice` → Backend `CreateSaleItemDTO.priceAtSale`
- Backend SaleDTO.items[] mapeado para `itemsCount` no frontend

---

## FASE 3 — AUTENTICAÇÃO E SEGURANÇA

### [13] JWT em todas requisições

- Interceptor em `api.ts` adiciona header `Authorization: Bearer <token>` automaticamente
- Token lido de `localStorage['healthsync-token']`
- Todas as chamadas via axios instance compartilhada passam pelo interceptor

### [14] Tratamento de erros 403 Forbidden

- Interceptor de erro trata status 403 com log
- Em caso de 401 (token expirado/inválido): token removido + redirect para `/login`

### [15] Roles (ADMIN / CAIXA)

- Backend SecurityConfig:
  - `POST /product, /inventory, /sales, /customer, /financial, /supplier` → `hasRole("ADMIN")`
  - Demais endpoints autenticados → qualquer role
- Frontend Register page permite seleção de role: `ROLE_ADMIN` ou `ROLE_CAIXA`
- Role armazenado no Zustand `useUserStore`

### [16] Backend SecurityFilter

- Endpoints públicos configurados: `/auth/login`, `/auth/register`, Swagger UI
- JWT validado via HMAC256 com secret configurável
- Token expira em 2 horas

---

## FASE 4 — IDENTIDADE VISUAL

### [17-20] Nome "HealthSync" aplicado

| Arquivo | Alteração |
|---------|-----------|
| `index.html` | `<title>HealthSync</title>` (já estava) |
| `src/components/layout/Sidebar.tsx` | Logo: `FarmaERP` → `HealthSync` |
| `src/pages/Dashboard.tsx` | Subtítulo: `FarmaERP` → `HealthSync` |
| `src/constants/index.ts` | Storage keys: `farmaerp-*` → `healthsync-*` |
| `src/pages/Login.tsx` | Header: "HealthSync" com logo |
| `package.json` | Nome: `healthsync` (já estava) |

**Layout e estilos mantidos exatamente iguais.**

---

## ARQUIVOS MODIFICADOS

### Modificados (existentes):
1. `src/constants/index.ts` — URLs, endpoints, storage keys
2. `src/types/index.ts` — FinancialType e PaymentStatus enum values
3. `src/services/api.ts` — Interceptor JWT, auto-unwrap ApiResponse, 401 redirect
4. `src/services/productService.ts` — Endpoints + DTO mapping
5. `src/services/customerService.ts` — Endpoints + DTO mapping
6. `src/services/supplierService.ts` — Endpoints + DTO mapping
7. `src/services/saleService.ts` — Endpoints + DTO mapping
8. `src/services/financialService.ts` — Endpoints + DTO mapping
9. `src/services/inventoryService.ts` — Endpoints corrigidos
10. `src/services/index.ts` — Export authService
11. `src/routes.tsx` — PrivateRoute/PublicRoute guards, rota /login
12. `src/store/index.ts` — UserStore sync com localStorage, storage key
13. `src/pages/index.ts` — Export Login
14. `src/pages/Dashboard.tsx` — Branding HealthSync
15. `src/components/layout/Sidebar.tsx` — Branding HealthSync + logout button

### Criados (novos):
1. `src/services/authService.ts` — Login, register, logout, isAuthenticated
2. `src/pages/Login.tsx` — Página de login/registro com JWT
3. `INTEGRATION_CHANGELOG.md` — Este documento

---

## MAPEAMENTO DE ENDPOINTS (SWAGGER → FRONTEND)

### Backend Controller Base Paths:
| Controller | Base Path |
|------------|-----------|
| AuthController | `/auth` |
| ProductController | `/product` |
| CustomerController | `/customer` |
| SupplierController | `/suppliers` |
| SaleController | `/sales` |
| FinancialController | `/financial` |
| InventoryController | `/inventory` |

---

## COMO TESTAR

### 1. Login completo
```bash
# Registrar usuário
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456","email":"admin@test.com","role":"ROLE_ADMIN"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
# Resposta: {"token":"eyJ..."}
```

### 2. Chamada autenticada
```bash
TOKEN="eyJ..."  # Token do login acima

# Listar produtos
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/product/search

# Criar cliente
curl -X POST http://localhost:8080/customer/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"João Silva","cpf":"12345678901"}'
```

### 3. Testar no navegador
1. Iniciar backend: `mvn spring-boot:run`
2. Iniciar frontend: `cd frontend/.../healthsync && npm run dev`
3. Acessar `http://localhost:5173`
4. Será redirecionado para `/login`
5. Cadastrar ou fazer login
6. Navegar pelas páginas — dados reais da API

### 4. Token inválido/expirado
```bash
# Usar token inválido
curl -H "Authorization: Bearer invalido" http://localhost:8080/product/search
# Resposta: 401 Unauthorized
# Frontend: redirect automático para /login
```

### 5. CRUD completo (exemplo: Produtos)
```bash
TOKEN="eyJ..."

# Criar
curl -X POST http://localhost:8080/product/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Dipirona","description":"Analgésico","priceCost":5.00,"priceSale":12.00,"controlled":false,"tarja":"Sem Tarja","registerMS":"1234567890123","productCategoryType":"MEDICAMENTOS","supplierId":1}'

# Listar
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/product/search

# Atualizar
curl -X PUT http://localhost:8080/product/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Dipirona 500mg","description":"Analgésico 500mg","priceCost":5.50,"priceSale":13.00,"controlled":false,"tarja":"Sem Tarja","registerMS":"1234567890123","productCategoryType":"MEDICAMENTOS","supplierId":1}'

# Excluir
curl -X DELETE -H "Authorization: Bearer $TOKEN" http://localhost:8080/product/delete/1
```

---

## POSSÍVEIS ERROS E SOLUÇÕES

| Erro | Causa | Solução |
|------|-------|---------|
| 401 Unauthorized | Token expirado ou inválido | Fazer login novamente (redirect automático) |
| 403 Forbidden | Role insuficiente (CAIXA tentando criar) | Usar conta ADMIN para operações de criação |
| CORS error | Frontend em porta diferente | Backend já configura CORS para localhost:3000 e :5173 |
| Network Error | Backend não iniciado | Iniciar com `mvn spring-boot:run` |
| Campo undefined | Mismatch de DTO | Verificar mapping no service correspondente |
| `data` null | ApiResponse unwrap falhou | Verificar se endpoint retorna ApiResponse wrapper |

---

## TECNOLOGIAS UTILIZADAS

**Frontend:**
- React 19 + TypeScript
- Axios (HTTP client com interceptors)
- React Router v7 (navegação + route guards)
- Zustand (state management persistido)
- Tailwind CSS (estilos — não alterado)

**Backend:**
- Spring Boot 3
- Spring Security + JWT (com.auth0.jwt)
- Flyway (migrations)
- MySQL
- SpringDoc OpenAPI (Swagger)

---

## RESULTADO FINAL

- ✅ Frontend totalmente conectado ao backend
- ✅ Login com JWT funcionando (login + register)
- ✅ CRUD completo integrado para todos os módulos
- ✅ Rotas protegidas com redirect automático
- ✅ Sistema com identidade "HealthSync"
- ✅ Interceptor JWT em todas as requisições
- ✅ Auto-unwrap de ApiResponse do backend
- ✅ Mapeamento de DTOs entre frontend e backend
- ✅ Tratamento de erros HTTP (401, 403, 404, 500)
- ✅ Logout com limpeza de token
- ✅ Sem alterações visuais/design
