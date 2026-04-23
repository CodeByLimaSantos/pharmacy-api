# 🏥 HealthSync API
## Sistema de Gestão Integrada para Farmácias e Distribuidoras

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![Status](https://img.shields.io/badge/Status-Production-success.svg)]()

---

## 🎯 Visão Geral

**HealthSync** é uma **API REST robusta e escalável** para gestão completa de farmácias e distribuidoras. Desenvolvida em **Spring Boot 4.0.5**, oferece solução integrada, documentada e pronta para produção.


### Principais Funcionalidades

| Módulo | Capacidades |
|--------|---|
| 💰 **Financeiro** | Contas a receber/pagar, fluxo de caixa, relatórios de vencimento |
| 📦 **Estoque** | Controle de lotes, data de validade, rastreamento de movimentações |
| 👥 **Clientes** | Cadastro com CPF validado, histórico de compras, análise |
| 🏭 **Fornecedores** | CNPJ validado, gestão de relacionamento e produtos |
| 🛒 **Vendas** | PDV integrado, múltiplos métodos de pagamento, cálculo automático |
| 🔐 **Segurança** | JWT + Spring Security, roles ADMIN e CAIXA, CORS configurável |
| 📊 **API** | OpenAPI 3.0, Swagger interativo, DTOs completos, validação robusta |

---

## 🚀 Stack Tecnológico

### Backend
```
Java 17                    Linguagem principal
Spring Boot 4.0.5          Framework Web
Spring Data JPA            ORM e persistência
Spring Security            Autenticação/Autorização
Spring Validation          Validação de dados
```

### Banco de Dados & Migrações
```
MySQL 8.0+                 Banco de dados relacional
Flyway                     Versionamento de schema
Hibernate                  Mapeamento objeto-relacional
```

### Segurança & Documentação
```
Auth0 JWT                  Tokens JWT
OpenAPI 3.0                Especificação de API
SpringDoc                  Swagger/OpenAPI integrado
```

### Ferramentas
```
Maven 3.9+                 Build e dependências
Lombok                     Redução de boilerplate
JUnit 5                    Testes unitários
```

---

## 📋 Começando

### Pré-requisitos

```bash
✅ Java 17 ou superior
✅ Maven 3.9 ou superior
✅ MySQL 8.0 ou superior
✅ Git
```

### Instalação Rápida

#### 1️⃣ Clone o Repositório
```bash
git clone https://github.com/limasantos/pharmacy-api.git
cd pharmacy-api
```

#### 2️⃣ Configure o Banco de Dados

**Crie o banco e usuário:**
```sql
CREATE DATABASE pharmacy_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'pharmacy_user'@'localhost' IDENTIFIED BY 'SenhaSegura123!';
GRANT ALL PRIVILEGES ON pharmacy_db.* TO 'pharmacy_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 3️⃣ Configure as Credenciais

Edite `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pharmacy_db
spring.datasource.username=pharmacy_user
spring.datasource.password=SenhaSegura123!
api.security.token.secret=your_secret_jwt_key_here
```

#### 4️⃣ Instale e Execute

```bash
# Instalar dependências
mvn clean install

# Executar aplicação
mvn spring-boot:run
```

**Pronto! Acesse:** `http://localhost:8080`

---

## 📚 Documentação da API

### 🔗 Swagger

Após iniciar, acesse:
```
http://localhost:8080/swagger-ui.html
```

**Recursos disponíveis:**
- ✅ Testar endpoints em tempo real
- ✅ Visualizar schemas completos
- ✅ Autenticação JWT integrada
- ✅ Documentação automática dos parâmetros

### 🔑 Endpoints Principais

#### Autenticação
```http
POST   /api/auth/register          # Registrar novo usuário
POST   /api/auth/login             # Login e obter JWT
POST   /api/auth/refresh           # Renovar token expirado
```

#### Clientes
```http
GET    /api/customers              # Listar todos (paginado)
GET    /api/customers/{id}         # Detalhes do cliente
POST   /api/customers              # Criar novo
PUT    /api/customers/{id}         # Atualizar
DELETE /api/customers/{id}         # Remover
```

#### Produtos
```http
GET    /api/products               # Listar com filtros
GET    /api/products/{id}          # Detalhes
POST   /api/products               # Criar (validação MS + CNPJ)
PUT    /api/products/{id}          # Atualizar
DELETE /api/products/{id}          # Remover
```

#### Estoque
```http
GET    /api/inventory/lots         # Lotes em estoque
POST   /api/inventory/lots         # Registrar novo lote
GET    /api/inventory/movements    # Histórico de movimentações
POST   /api/inventory/movements    # Registrar movimentação (ENTRY, SALE, RETURN, DISPOSAL)
```

#### Financeiro
```http
GET    /api/financials             # Lançamentos (filtro: tipo, status)
POST   /api/financials             # Criar lançamento
PUT    /api/financials/{id}        # Atualizar
GET    /api/financials/overdue     # Contas vencidas
```

#### Vendas
```http
GET    /api/sales                  # Listar com filtros
POST   /api/sales                  # Nova venda (recalcula total automático)
GET    /api/sales/{id}             # Detalhes + itens
PUT    /api/sales/{id}/payment     # Processar pagamento
```

#### Fornecedores
```http
GET    /api/suppliers              # Listar fornecedores
POST   /api/suppliers              # Cadastrar (validação CNPJ)
PUT    /api/suppliers/{id}         # Atualizar
DELETE /api/suppliers/{id}         # Remover
```

#### Testes
```http
GET    /api/test/health            # Verificar saúde da API
GET    /api/test/greet/{name}      # Saudação personalizada
POST   /api/test/echo              # Echo do payload
```

---

## 🔐 Autenticação & Segurança

### Fluxo de Autenticação

```
1. POST /api/auth/login
   {
     "username": "admin",
     "password": "senha123"
   }

2. Resposta com JWT Token
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "expiresIn": 86400,
     "type": "Bearer"
   }

3. Usar em requisições
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

4. Servidor valida token via Spring Security
```

### Roles e Permissões

| Role | Permissões |
|------|-----------|
| **ROLE_ADMIN** | Acesso total, gerenciar usuários, relatórios |
| **ROLE_CAIXA** | Vendas, clientes, estoque (leitura), consultas |

### Validações Integradas

✅ **CPF** - Validação de dígito verificador e unicidade

✅ **CNPJ** - Validação de formato e dígitos

✅ **Email** - Formato RFC 5322 e unicidade

✅ **Registro MS** - Exatamente 13 dígitos numéricos

✅ **Telefone** - 10-11 dígitos

✅ **Valores** - Precisão Decimal para operações financeiras

---

## 🗄️ Estrutura do Banco de Dados

### Diagrama ER Completo

Visualize em: [dbdiagram.io](https://dbdiagram.io)

### Entidades Principais

#### TB_users
```sql
├── id (UUID, PK)
├── username (VARCHAR UNIQUE, NOT NULL)
├── password (VARCHAR, NOT NULL - bcrypt)
├── role (ENUM: ROLE_ADMIN, ROLE_CAIXA)
└── email (VARCHAR UNIQUE, NOT NULL)
```

#### TB_products
```sql
├── id (BIGINT, PK)
├── name (VARCHAR, NOT NULL)
├── description (TEXT)
├── priceCost (DECIMAL, ≥ 0)
├── priceSale (DECIMAL, ≥ 0)
├── controlled (BOOLEAN)
├── tarja (VARCHAR)
├── registerMS (VARCHAR UNIQUE, 13 dígitos)
├── productCategoryType (ENUM)
└── supplier_id (BIGINT, FK → TB_suppliers)
```

#### TB_customers
```sql
├── id (BIGINT, PK)
├── name (VARCHAR, NOT NULL)
└── cpf (VARCHAR UNIQUE, validated)
```

#### TB_inventory_lot
```sql
├── id (BIGINT, PK)
├── product_id (BIGINT, FK)
├── lotNumber (VARCHAR)
├── entryDate (DATE, auto-generated)
├── expirationDate (DATE)
└── quantity (INTEGER)
```

#### TB_inventory_movements
```sql
├── id (BIGINT, PK)
├── inventory_lot_id (BIGINT, FK)
├── movementType (ENUM: ENTRY, SALE, RETURN, DISPOSAL)
├── quantity (INTEGER)
├── movementDate (DATETIME, auto-generated)
└── reason (VARCHAR, obrigatório para DISPOSAL/ADJUSTMENT)
```

#### TB_sales
```sql
├── id (BIGINT, PK)
├── customer_id (BIGINT, FK, nullable)
├── saleDate (DATETIME, auto-generated)
├── totalAmount (DECIMAL, recalculado automático)
├── paymentMethod (ENUM: DINHEIRO, CARTAO, PIX, CHEQUE)
└── items (OneToMany → TB_sale_items)
```

#### TB_financials
```sql
├── id (BIGINT, PK)
├── type (ENUM: CONTA_A_RECEBER, CONTA_A_PAGAR)
├── description (VARCHAR, NOT NULL)
├── amount (DECIMAL, Positive)
├── issueDate (DATETIME, auto-generated)
├── dueDate (DATE)
├── paymentDate (DATE, nullable)
├── status (ENUM: PENDING, PARTIALLY_PAID, PAID, OVERDUE, CANCELED)
├── paymentMethod (ENUM, nullable)
├── customer_id (BIGINT, FK, nullable)
├── supplier_id (BIGINT, FK, nullable)
├── notes (TEXT)
├── createdAt (DATETIME)
└── updatedAt (DATETIME)
```

---

## 📊 Padrão de Response

### ✅ Sucesso

```json
{
  "success": true,
  "message": "Operação realizada com sucesso",
  "code": "SUCCESS",
  "timestamp": "2026-04-23T10:30:00",
  "path": "/api/customers",
  "data": {
    "id": 1,
    "name": "João Silva",
    "cpf": "123.456.789-00"
  }
}
```

### ❌ Erro de Validação

```json
{
  "success": false,
  "message": "Erro ao validar entrada",
  "code": "VALIDATION_ERROR",
  "timestamp": "2026-04-23T10:30:00",
  "path": "/api/customers",
  "errors": [
    {
      "field": "cpf",
      "message": "CPF inválido",
      "rejectedValue": "000.000.000-00"
    },
    {
      "field": "email",
      "message": "Email deve ser válido",
      "rejectedValue": "invalid-email"
    }
  ]
}
```

### 📋 Listagem com Paginação

```json
{
  "success": true,
  "message": "Clientes listados",
  "code": "SUCCESS",
  "timestamp": "2026-04-23T10:30:00",
  "data": [
    { "id": 1, "name": "Cliente 1", "cpf": "123.456.789-00" },
    { "id": 2, "name": "Cliente 2", "cpf": "987.654.321-00" }
  ],
  "pagination": {
    "totalElements": 150,
    "totalPages": 15,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

---

## ⚙️ Configuração

### arquivo application.properties

```properties
# ==================== APPLICATION ====================
spring.application.name=pharmacy.api
server.port=8080

# ==================== DATABASE ====================
spring.datasource.url=jdbc:mysql://localhost:3306/pharmacy_db
spring.datasource.username=pharmacy_user
spring.datasource.password=SenhaSegura123!

# ==================== JPA ====================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ==================== FLYWAY ====================
spring.flyway.enabled=true

# ==================== LOGGING ====================
logging.level.com.limasantos.pharmacy=DEBUG
logging.level.org.springframework.web=DEBUG

# ==================== SWAGGER ====================
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

api.docs.title=HealthSync API
api.docs.version=1.0.0

# ==================== CORS ====================
api.cors.allowed-origins=http://localhost:3000,http://localhost:5173

# ==================== JWT ====================
api.security.token.secret=your_secret_key_here
api.security.token.expiration=86400000
```

### Variáveis de Ambiente (Produção)

```bash
export JWT_SECRET="sua_chave_segura_aqui"
export DB_URL="jdbc:mysql://db.prod.com:3306/pharmacy_db"
export DB_USERNAME="pharmacy_prod"
export DB_PASSWORD="senha_muito_segura"
```

---

## 🧪 Testes & Validação

### Executar Testes

```bash
# Todos os testes
mvn test

# Teste específico
mvn test -Dtest=CustomerServiceTest

# Com cobertura de código
mvn test jacoco:report
```

### Exemplo de Teste Unitário

```java
@SpringBootTest
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerWithValidData() {
        Customer customer = new Customer();
        customer.setName("João Silva");
        customer.setCpf("123.456.789-00");

        Customer saved = customerService.save(customer);

        assertNotNull(saved.getId());
        assertEquals("João Silva", saved.getName());
    }

    @Test
    void shouldThrowExceptionForDuplicateCpf() {
        // Arrange: Criar primeiro cliente
        Customer first = new Customer();
        first.setName("Cliente 1");
        first.setCpf("123.456.789-00");
        customerService.save(first);

        // Act & Assert: Tentar criar com CPF duplicado
        Customer duplicate = new Customer();
        duplicate.setName("Cliente 2");
        duplicate.setCpf("123.456.789-00");

        assertThrows(DataIntegrityViolationException.class,
            () -> customerService.save(duplicate));
    }
}
```

---

## 🚀 Deploy em Produção

### Build Release

```bash
mvn clean package -DskipTests -P prod
```

### Executar JAR

```bash
java -jar target/pharmacy.api-0.0.1-SNAPSHOT.jar
```

### Com Docker

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/pharmacy.api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
```

```bash
# Build
docker build -t pharmacy-api:1.0 .

# Run
docker run -d \
  -e JWT_SECRET="secret_key" \
  -e DB_URL="jdbc:mysql://db:3306/pharmacy_db" \
  -e DB_USERNAME="user" \
  -e DB_PASSWORD="password" \
  -p 8080:8080 \
  pharmacy-api:1.0
```

---

## 🔄 Migrations com Flyway

### Estrutura

```
src/main/resources/db/migration/
├── V1__Create_users_table.sql
├── V2__Create_customers_table.sql
├── V3__Create_products_table.sql
├── V4__Create_suppliers_table.sql
├── V5__Create_sales_table.sql
├── V6__Create_inventory_tables.sql
├── V7__Create_financials_table.sql
└── V8__Create_indexes.sql
```

### Naming Convention

**Padrão:** `V{numero}__{descricao}.sql`

Exemplos corretos:
- `V1__Initial_schema.sql`
- `V2__Add_audit_columns.sql`
- `V3__Create_foreign_keys.sql`

As migrations rodam **automaticamente** ao iniciar a aplicação.

---

## 🛠️ Solução de Problemas

### Erro: "Can't connect to MySQL server"

```bash
# Verificar se MySQL está rodando
mysql -u root -p

# Testar conexão
telnet localhost 3306
```

### Erro: "Access denied for user"

```bash
# Verificar credenciais no application.properties
# Resetar permissões no MySQL
GRANT ALL PRIVILEGES ON pharmacy_db.* TO 'pharmacy_user'@'localhost';
FLUSH PRIVILEGES;
```

### Erro: "Illegal base64 character"

```
Motivo: JWT_SECRET inválido
Solução: Usar string sem caracteres especiais ou base64 válido
```

### Porta 8080 já em uso

```bash
# Finder qual processo usa a porta
lsof -i :8080

# Mudar porta no application.properties
server.port=8081
```

---

## 📖 Estrutura de Projeto

```
pharmacy-api/
├── src/main/java/com/limasantos/pharmacy/api/
│   ├── customer/              # 👥 Módulo de Clientes
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── product/               # 📦 Módulo de Produtos
│   ├── supplier/              # 🏭 Módulo de Fornecedores
│   ├── inventory/             # 📊 Módulo de Estoque
│   │   ├── entity/
│   │   ├── enums/
│   │   └── service/
│   ├── sales/                 # 🛒 Módulo de Vendas
│   ├── financial/             # 💰 Módulo Financeiro
│   ├── user/                  # 🔐 Módulo de Usuários
│   ├── test/                  # 🧪 Endpoints de Teste
│   ├── shared/                # 🔄 Recursos Compartilhados
│   │   ├── dto/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── filter/
│   │   └── security/
│   └── config/                # ⚙️ Configurações
│       ├── security/
│       └── swagger/
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   └── db/migration/          # 🗄️ Scripts Flyway
├── src/test/java/             # 🧪 Testes Unitários
├── pom.xml
└── README.md
```

---

## 📝 Padrões de Código

### Entidade Base

```java
@Entity
@Table(name = "TB_exemplo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exemplo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    @Column(nullable = false)
    private String campo;

    @PrePersist
    protected void onCreate() {
        // Lógica de inicialização
    }

    @PreUpdate
    protected void onUpdate() {
        // Lógica de atualização
    }
}
```

### DTO com Validação

```java
@Data
@Builder
public class ExemploDTO {

    @NotBlank(message = "Campo é obrigatório")
    private String campo;

    @Positive(message = "Deve ser maior que zero")
    private BigDecimal valor;

    @Email(message = "Email inválido")
    private String email;
}
```

### Service com Transação

```java
@Service
@RequiredArgsConstructor
@Transactional
public class ExemploService {

    private final ExemploRepository repository;

    @Transactional(readOnly = true)
    public List<Exemplo> listar() {
        return repository.findAll();
    }

    public Exemplo criar(ExemploDTO dto) {
        Exemplo exemplo = new Exemplo();
        // mapear DTO para entidade
        return repository.save(exemplo);
    }
}
```

---



## 📄 Licença

MIT License © 2026 - Lucas Lima Santos

Este projeto não é um projeto OpenSource. Veja [LICENSE](LICENSE) para detalhes completos.

---
## 👨‍💻 Autor

**Guilherme Lima**

- 🔗 GitHub: [@limasantos](https://github.com/limasantos)
- 📧 Email: dev.healthsync@localhost
- 💼 LinkedIn: [linkedin.com/in/lucas-lima-santos](https://linkedin.com/in/lucas-lima-santos)

---


## Version

### ✅ v1.0.0 (Atual)
- CRUD completo de clientes, produtos, fornecedores
- Gestão de estoque com lotes e validade
- Vendas integradas
- Gestão financeira básica
- Autenticação JWT com roles


**Versão:** 1.0.0 | **Atualizado:** 23 de Abril de 2026 |