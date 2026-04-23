🧩 1. DESCRIÇÃO DO PROBLEMA

Durante a tentativa de cadastro de clientes no sistema, a aplicação retornava a mensagem genérica:

Erro ao cadastrar. Verifique os dados e tente novamente.

Enquanto isso, o backend respondia com:

500 INTERNAL_SERVER_ERROR

Ou seja, o sistema basicamente entrou em modo “deu ruim, se vira”.

🔍 2. ANÁLISE TÉCNICA

A partir dos logs do backend:

ConstraintViolationException:
CPF inválido

Validação aplicada:

org.hibernate.validator.constraints.br.CPF
✔ Conclusão da análise

O erro foi causado por:

alterei o customerdto
envio de um CPF inválido matematicamente
validação automática do Hibernate Validator
tratamento incorreto da exceção (retornando 500 em vez de 400)


💥 3. CAUSA RAIZ

Uso de dados inválidos no campo cpf:

"cpf": "12345678901"

Apesar de conter 11 dígitos, o valor não atende às regras de validação de CPF.

🛠️ 4. AÇÃO CORRETIVA
✔ Correção aplicada

Substituição do CPF inválido por um CPF válido para testes:

{
"name": "Guilherme",
"cpf": "52998224725"
}
🔧 5. MELHORIAS IDENTIFICADAS
🔹 Backend
Alterar tratamento de exceções de validação:
De: 500 INTERNAL_SERVER_ERROR
Para: 400 BAD_REQUEST
Retornar erros estruturados:
{
"fieldErrors": {
"cpf": "CPF inválido"
}
}
🔹 Frontend
Melhorar validação de CPF antes do envio (evitar ida desnecessária ao backend)
Exibir mensagens específicas por campo
🧪 6. RESULTADO APÓS CORREÇÃO
Cadastro realizado com sucesso ao utilizar CPF válido
Sistema funcionando conforme esperado
Erro identificado como dados inválidos, não falha de código
🎯 7. CONCLUSÃO

O problema não estava relacionado a:

lógica do frontend
integração com API
estrutura do payload

Mas sim à validação de dados no backend, especificamente no campo CPF.

🧠 OBSERVAÇÃO FINAL

Esse tipo de erro é clássico:

“tudo parece quebrado, mas na verdade é só dado inválido”