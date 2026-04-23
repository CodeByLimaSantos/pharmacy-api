# Fix Create Customer 2.0

## Objetivo
Corrigir o fluxo completo de criacao/edicao de cliente para que os campos adicionais (`email`, `phone`, `address`) sejam realmente persistidos e retornados ao frontend, com tratamento de erros de validacao consistente.

## Causa raiz identificada
1. O backend recebia `email`, `phone` e `address`, mas os responses usados no controller (`CustomerResponse` e `CustomerListResponse`) retornavam apenas `id`, `name` e `cpf`.
2. O frontend interpreta os dados vindos dos endpoints como fonte da tabela/formulario. Como os campos adicionais nao vinham no payload, parecia que o cadastro estava incompleto.
3. O banco antigo era criado com `tb_customers` apenas com `id`, `cpf` e `name` (migration `V9`), sem migration formal para os novos campos.
4. A busca por CPF no service nao normalizava o valor antes da consulta.

## Alteracoes aplicadas

### Backend

#### 1) `src/main/java/com/limasantos/pharmacy/api/dto/response/domain/customer/CustomerResponse.java`
- Removidos campos `createdAt` e `updatedAt` (estavam sempre `null` no fluxo atual).
- Adicionados campos:
  - `email`
  - `phone`
  - `address`
- Atualizado `fromDto(CustomerDTO dto)` para mapear os novos campos.

#### 2) `src/main/java/com/limasantos/pharmacy/api/dto/response/domain/customer/CustomerListResponse.java`
- Adicionados campos:
  - `email`
  - `phone`
  - `address`
- Atualizado `fromDto(CustomerDTO dto)` para retornar todos os dados necessarios para a tabela de clientes no frontend.

#### 3) `src/main/java/com/limasantos/pharmacy/api/customer/service/CustomerService.java`
- Ajustado `findByCpf(String cpf)` para normalizar o CPF (`replaceAll("\\D", "")`) antes de consultar o repositorio.
- Mantido o fluxo de create/update ja normalizando CPF e salvando os campos de contato.

#### 4) `src/main/resources/migration/V11__add_customer_contact_fields.sql`
- Nova migration para adicionar colunas opcionais em `tb_customers`:
  - `email VARCHAR(255)`
  - `phone VARCHAR(20)`
  - `address VARCHAR(255)`
- Migration escrita de forma idempotente usando `INFORMATION_SCHEMA` + SQL dinamico (evita falha em base que ja possua as colunas).

### Frontend

Nenhuma alteracao adicional foi necessaria nesta etapa porque:
- `CustomerMapper` ja mapeia `email`, `phone`, `address`.
- `CustomerForm` ja remove mascara de CPF/telefone e trata `fieldErrors`/`details`.
- `customerService` ja envia payload completo no create e interpreta retorno.

## Validacao executada

### Frontend
Comandos executados:

```powershell
Set-Location "C:\Users\PICHAU\Desktop\pharmacy.api\frontend\healthsync"
npm run build
npm run test:run
```

Resultado:
- Build OK (`tsc -b` + `vite build`)
- Testes OK (`34 passed`)

### Backend
Tentativa de compilacao:

```powershell
Set-Location "C:\Users\PICHAU\Desktop\pharmacy.api"
.\mvnw.cmd -q -DskipTests compile
```

Resultado:
- Nao foi possivel compilar via wrapper porque o projeto nao possui `.mvn/wrapper/maven-wrapper.properties` no ambiente atual.

## Como testar manualmente (E2E)
1. Suba backend e frontend.
2. Faça login no frontend para obter JWT.
3. Em `Clientes`, crie um cliente com:
   - Nome
   - CPF mascarado
   - Email
   - Telefone
   - Endereco
4. Verifique que o novo cliente aparece na tabela com telefone/email preenchidos.
5. Recarregue a pagina e confirme que os dados continuam presentes.
6. Edite o cliente e altere telefone/endereco; salve e valide o retorno.
7. Teste validacao:
   - CPF invalido
   - Nome vazio
   - Email invalido
   Deve exibir mensagem de erro no formulario/toast.

## Impacto esperado
- Fluxo de create customer passa a retornar os dados completos.
- UI nao perde mais os campos de contato apos salvar/recarregar.
- Base antiga fica compatibilizada com os novos campos de customer.

