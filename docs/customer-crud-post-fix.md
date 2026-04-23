# Fix do POST de Cliente (Frontend)

Data: 2026-04-20

## Contexto
No backend, o endpoint `POST /customer/create` funciona no Insomnia com payload `{ name, cpf }`, incluindo validacoes (`@NotBlank`, `@CPF`).
No frontend, o fluxo falhava por tratamento incompleto de erro e inconsistencias no parsing da resposta.

## Objetivo atendido
- Remover mascara do CPF antes do envio.
- Tratar erros de validacao vindos do backend e exibir para o usuario.
- Lidar corretamente com respostas com e sem envelope `ApiResponse<T>`.

## Arquivos alterados
- `frontend/healthsync/src/services/customerService.ts`
- `frontend/healthsync/src/components/forms/CustomerForm.tsx`
- `frontend/healthsync/src/pages/Clientes.tsx`
- `frontend/healthsync/src/types/index.ts`

## O que foi corrigido

### 1) Sanitizacao de CPF no service
No `customerService.create`, o CPF agora e sempre sanitizado com `replace(/\D/g, '')` antes do envio.
Tambem foi aplicado no `getByCpf` para manter consistencia.

### 2) Parsing robusto de resposta da API
Foi adicionado `unwrapApiData` no `customerService` para suportar:
- resposta ja desembrulhada pelo interceptor (`data` direto), e
- resposta no formato envelope (`ApiResponse<T>` com `data`).

### 3) Validacao no formulario + mensagens do backend
No `CustomerForm`:
- CPF passou a ser obrigatorio no client-side (`CPF *`).
- em erro de submit, o formulario tenta ler `fieldErrors` e `details` da resposta do backend.
- erros por campo (`name`, `cpf`, etc.) sao exibidos no proprio input.
- erro geral aparece no topo do formulario.

### 4) Toast de erro com mensagem real da API
Na pagina `Clientes`, o catch do submit agora extrai mensagens do backend (`fieldErrors`, `details`, `message`) para o toast, em vez de mostrar sempre mensagem generica.

### 5) Tipagem de erro/resposta
Em `types/index.ts`:
- `ApiError` foi ampliado com `details`, `httpStatus` e `fieldErrors`.
- adicionado `ApiResponse<T>` para representar o envelope padrao da API.

## Resultado esperado
- `POST /customer/create` envia CPF sem mascara.
- Erros de validacao (ex.: CPF invalido, nome em branco) aparecem corretamente na UI.
- Fluxo de sucesso e erro fica consistente mesmo com diferentes formatos de resposta.

## Validacao manual recomendada
1. Abrir tela de `Clientes` e clicar em `Novo Cliente`.
2. Tentar salvar com CPF invalido (ou vazio).
3. Confirmar exibicao de erro no campo CPF e/ou topo do formulario.
4. Salvar com CPF valido (`somente digitos` no payload final).
5. Confirmar toast de sucesso e item novo na lista.

## Comando de teste executado
```powershell
Set-Location "C:\Users\PICHAU\Desktop\pharmacy.api\frontend\healthsync"
npm run test:run
```

