# Exemplo de projeto fazendo migration de vários bancos de dados com Liquibase

### Tecnologias

* Java 17
* Spring Boot
* PostgreSQL

### Como testar

#### 1 - Criar um banco para armazenar os dados dos outros bancos

* Nome do banco (main) onde a sua url ficará da seguinte forma: jdbc:postgresql://localhost:5432/main
* Usuário (root)
* Senha (root)

#### 2 - Criar tabela para dados dos outros bancos no banco (main)

* Nome da tabela (databases)
* Colunas (id, name, url)

#### 3 - Criar quantos banco quiser para testar a migração

* Os próximos bancos podem ser criados com os seguintes nomes: data_01, data_02, data_03, etc.
* Criar a tabela (users) nos bancos e inserir alguns dados
* Colunas (id, name)
* Inserir dados referente aos bancos criados na tabela (databases) do banco (main) com o nome do banco e sua url

#### 4 - Testar a migation nos bancos

* Iniciar a aplicação
* Se tudo ocorrer bem, será adicionado a coluna (cpf) nas tabelas (users)
