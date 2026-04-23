# Ajustes para teste de CORS + JWT + fluxo de autenticacao (HealthSync)

Data: 2026-04-20

## Objetivo
Normalizar requisicoes autenticadas do frontend (`http://localhost:5173`) para o backend Spring Boot, removendo falha de preflight (`OPTIONS`) que aparecia no browser como `AxiosError: Network Error`.

## Arquivo alterado
- `src/main/java/com/limasantos/pharmacy/api/infra/security/SecurityConfigurations.java`
- `frontend/healthsync/src/services/authService.ts`
- `frontend/healthsync/src/services/api.ts`
- `frontend/healthsync/src/store/index.ts`
- `frontend/healthsync/src/App.tsx`
- `frontend/healthsync/src/pages/Login.tsx`

## Alteracoes aplicadas
1. **Habilitado CORS na cadeia de seguranca do Spring**
   - Adicionado `.cors(withDefaults())` no `SecurityFilterChain`.
   - Motivo: garantir que os headers CORS sejam processados na camada de Security (incluindo preflight).

2. **Liberado preflight global (`OPTIONS`) sem autenticacao**
   - Adicionado matcher: `requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()`.
   - Motivo: navegadores enviam preflight antes de `POST` com `Authorization`; se esse preflight exigir auth, a requisicao principal e bloqueada pelo browser.

3. **Alinhados matchers de POST com endpoints reais dos controllers**
   - De:
     - `/product`, `/inventory`, `/sales`, `/customer`, `/financial`, `/supplier`
   - Para:
     - `/product/create`, `/inventory/create`, `/sales/create`, `/customer/create`, `/financial/create`, `/suppliers/create`
   - Motivo: remover divergencia entre regra de autorizacao e rotas de criacao usadas pela API.

4. **Import adicionado para habilitar `withDefaults()`**
   - `import static org.springframework.security.config.Customizer.withDefaults;`

## Impacto esperado
- Preflight `OPTIONS` retorna com CORS valido e sem exigir JWT.
- Requisicoes `POST` autenticadas (ex.: criar produto) deixam de ser canceladas pelo browser por CORS.
- Regras de role para criacao passam a incidir nos endpoints corretos.

## Ajustes de autenticacao no frontend
1. **Bootstrap de sessao no startup do app**
   - `frontend/healthsync/src/App.tsx` chama `authService.bootstrapSession()` no `useEffect` inicial.
   - Motivo: limpar token invalido/expirado antes das primeiras rotas privadas.

2. **Validacao local de exp do JWT**
   - `frontend/healthsync/src/services/authService.ts` agora decodifica payload JWT e valida `exp`.
   - Metodos atualizados: `getToken()`, `isAuthenticated()`, `bootstrapSession()`.
   - Motivo: impedir acesso inicial ao dashboard apenas por token stale no `localStorage`.

3. **Sincronizacao de login/logout com Zustand**
   - `login` e `logout` passaram a usar `useUserStore` para manter estado e storage alinhados.
   - Motivo: evitar estado divergente (token em storage, mas store nao atualizado).

4. **Interceptor Axios usando token validado**
   - `frontend/healthsync/src/services/api.ts` usa `authService.getToken()` no request interceptor.
   - Em `401`, chama `authService.logout()` e redireciona para `/login`.
   - Motivo: remover token invalido automaticamente e normalizar retry de sessao.

5. **Flag de autenticacao coerente ao setar token**
d   - `frontend/healthsync/src/store/index.ts`: `setToken` agora atualiza `isAuthenticated: !!token`.
   - Motivo: consistencia do estado global de auth.

6. **Login sem dupla escrita de token**
   - `frontend/healthsync/src/pages/Login.tsx`: removido `setToken(...)` apos `authService.login(...)`.
   - Motivo: `authService.login` ja persiste token no store/storage; manter so um ponto de escrita evita confusao em debug.

## O que NAO foi alterado
- `frontend/healthsync/src/services/productService.ts`
  - Endpoint de criacao (`/product/create`) ja estava consistente com `ProductController`.
- `src/main/java/com/limasantos/pharmacy/api/infra/config/CorsConfig.java`
  - Mantido, pois ja contempla `http://localhost:5173`, `OPTIONS` e headers amplos.

## Validacao executada
- Frontend: `npm run test:run` (34 testes, todos aprovados).
- Backend: nao foi possivel rodar testes no ambiente atual (wrapper Maven incompleto e `mvn` ausente).

## Checklist rapido para validacao manual
1. Fazer login no frontend.
2. Tentar criar produto em `POST /product/create`.
3. No DevTools (Network), validar:
   - `OPTIONS /product/create` com status 200/204.
   - Resposta com `Access-Control-Allow-Origin` e `Access-Control-Allow-Headers` (incluindo `Authorization`).
4. Confirmar que o `POST` subsequente e enviado e respondido normalmente.

## Comando util para subir backend local
```powershell
Set-Location "C:\Users\PICHAU\Desktop\pharmacy.api"
.\mvnw.cmd spring-boot:run
```

