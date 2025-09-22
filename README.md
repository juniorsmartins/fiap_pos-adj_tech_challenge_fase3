# PROJETO: fase 3 - Tech Challenge ADJ

Equipe: Junior Martins (rm364241)

## Índice
1. Introdução;
2. Arquitetura do Sistema;
3. Descrição dos Endpoints;
4. Exemplos para testes;
5. Configuração do Projeto;
6. Qualidade do Código;
7. Repositório do Código;
8. Vídeos;
9. Notas;
10. Autoria.


## Introdução

#### Descrição do problema:

Em um ambiente hospitalar, é essencial contar com sistemas que garantam o 
agendamento eficaz de consultas, o gerenciamento do histórico de pacientes e 
o envio de lembretes automáticos para garantir a presença dos pacientes nas 
consultas. Este sistema deve ser acessível a diferentes tipos de usuários 
(médicos, enfermeiros e pacientes), com acesso controlado e funcionalidades 
específicas para cada perfil.

#### Objetivo do projeto:

O objetivo é desenvolver um backend simplificado e modular, com foco em
segurança e comunicação assíncrona, garantindo que o sistema seja escalável,
seguro e que utilize boas práticas de autenticação, autorização e comunicação
entre serviços.

#### Requisitos do Sistema

1. Segurança em Aplicações Java: <br>
   ● Autenticação com Spring Security: implementar autenticação básica para 
garantir que cada tipo de usuário tenha acesso controlado às funcionalidades. <br>
   ● Níveis de Acesso: <br>
   ○ Médicos: podem visualizar e editar o histórico de consultas. <br>
   ○ Enfermeiros: podem registrar consultas e acessar o histórico. <br>
   ○ Pacientes: podem visualizar apenas as suas consultas. <br> 

2. Consultas e Histórico do Paciente com GraphQL: <br>
   ● Implementação de GraphQL: permitir consultas flexíveis sobre o histórico 
médico, como listar todos os atendimentos de um paciente ou apenas as futuras. <br>
   ● Serviço de Agendamento: médicos e enfermeiros poderão registrar novas 
consultas e modificar consultas existentes. <br> 

3. Separação em mais de um serviço: <br>
   ● Serviço de Agendamento: responsável pela criação e edição das consultas. <br>
   ● Serviço de notificações: envia lembretes automáticos aos pacientes sobre 
consultas futuras. <br>
   ● Serviço de histórico (opcional): armazena o histórico de consultas e 
disponibiliza dados via GraphQL. <br>

4. Comunicação Assíncrona com RabbitMQ ou Kafka: <br>
   ● RabbitMQ ou Kafka: utilizar uma dessas ferramentas para gerenciar a 
comunicação assíncrona entre os serviços. <br> 
   ○ O Serviço de agendamento deve enviar uma mensagem ao serviço de 
notificações quando uma consulta for criada ou editada. <br>
   ○ O serviço de notificações processa essa mensagem e envia um lembrete 
ao paciente. <br> <br>


## Arquitetura do Sistema

#### Descrição da Arquitetura 

- Clean Architecture:

A Arquitetura Limpa (Clean Architecture), proposta por Robert C. Martin, é um 
conjunto de princípios para criar sistemas de software robustos, escaláveis e 
fáceis de manter. Ela organiza o código em camadas concêntricas, priorizando a 
separação de responsabilidades. No centro, estão as entidades, que representam 
as regras de negócio principais. Ao redor, camadas como casos de uso definem a 
lógica da aplicação, enquanto interfaces e frameworks ficam nas camadas 
externas. A comunicação entre camadas segue a dependência inversa, onde camadas 
internas não conhecem as externas. Isso promove flexibilidade, testabilidade e 
independência de tecnologias externas.

#### Diagrama da Arquitetura

![TechChallenge3](docs/DiagramaTechChallenge-v8.png)

Imagem de autoria do responsável pelo projeto. Desenvolvida por meio do software StarUML. 

## Descrição dos Endpoints da API

##### schema.graphqls #####

```
input UserRequestDto {
    username: String!
    password: String!
}

input PacienteRequestDto {
    nome: String!
    email: String!
    user: UserRequestDto!
}

input MedicoRequestDto {
    nome: String!
    user: UserRequestDto!
}

input EnfermeiroRequestDto {
    nome: String!
    user: UserRequestDto!
}

input ConsultaRequestDto {
    dataHora: String!
    medicoId: ID!
    pacienteId: ID!
}

input FiltroConsulta {
    id: ID
    dataHora: String
    status: String
    medicoId: ID
    pacienteId: ID
}

input HistoricoMedicoRequestDto {
    diagnostico: String!
    prescricao: String!
    exames: String
    consultaId: ID!
}

input FiltroHistoricoMedico {
    id: ID
    diagnostico: String
    prescricao: String
    exames: String
    consultaId: ID
}


type RoleResponseDto {
    id: ID
    name: String
}

type UserResponseDto {
    id: ID
    username: String
    password: String
    enabled: Boolean
    role: RoleResponseDto
}

type PacienteResponseDto {
    id: ID
    nome: String
    email: String
    user: UserResponseDto
}

type MedicoResponseDto {
    id: ID
    nome: String
    user: UserResponseDto
}

type EnfermeiroResponseDto {
    id: ID
    nome: String
    user: UserResponseDto
}

type ConsultaResponseDto {
    id: ID
    dataHora: String
    status: String
    medico: MedicoResponseDto
    paciente: PacienteResponseDto
}

type HistoricoMedicoResponseDto {
    id: ID
    diagnostico: String
    prescricao: String
    exames: String
    consulta: ConsultaResponseDto
}

type Query {
    consultarPacientePorId(id: ID!): PacienteResponseDto!

    consultarMedicoPorId(id: ID!): MedicoResponseDto!

    consultarEnfermeiroPorId(id: ID!): EnfermeiroResponseDto!

    consultarConsultaPorId(id: ID!): ConsultaResponseDto!
    listarHistoricoDeConsultasPorIdPaciente(id: ID!): [ConsultaResponseDto!]!
    pesquisarConsulta(filtro: FiltroConsulta!): [ConsultaResponseDto!]!

    consultarHistoricoMedicoPorIdConsulta(id: ID!): HistoricoMedicoResponseDto!
    listarHistoricoMedicoPorIdPaciente(id: ID!): [HistoricoMedicoResponseDto!]!
    pesquisarHistoricoMedico(filtro: FiltroHistoricoMedico!): [HistoricoMedicoResponseDto!]!
}

type Mutation {
    criarPaciente(request: PacienteRequestDto!): PacienteResponseDto
    apagarPaciente(id: ID!): Boolean
    atualizarPaciente(id: ID!, request: PacienteRequestDto!): PacienteResponseDto

    criarMedico(request: MedicoRequestDto!): MedicoResponseDto
    apagarMedico(id: ID!): Boolean
    atualizarMedico(id: ID!, request: MedicoRequestDto!): MedicoResponseDto

    criarEnfermeiro(request: EnfermeiroRequestDto!): EnfermeiroResponseDto
    apagarEnfermeiro(id: ID!): Boolean
    atualizarEnfermeiro(id: ID!, request: EnfermeiroRequestDto!): EnfermeiroResponseDto

    criarConsulta(request: ConsultaRequestDto!): ConsultaResponseDto
    cancelarConsulta(id: ID!): Boolean
    atualizarConsulta(id: ID!, request: ConsultaRequestDto!): ConsultaResponseDto
    concluirConsulta(id: ID!): Boolean

    criarHistoricoMedico(request: HistoricoMedicoRequestDto!): HistoricoMedicoResponseDto
    atualizarHistoricoMedico(request: HistoricoMedicoRequestDto!): HistoricoMedicoResponseDto
}
```

##### Exemplos #####

```
mutation MyMutation {
  criarEnfermeiro(
    request: {nome: "Robert Martin", user: {username: "111", password: "111"}}
  ) {
    id
    nome
    user {
      password
      username
      id
      enabled
      role {
        id
        name
      }
    }
  }
}
```

```
mutation MyMutation {
  criarMedico(
    request: {nome: "Martin Fowler", user: {username: "222", password: "222"}}
  ) {
    id
    nome
    user {
      enabled
      password
      id
      username
      role {
        id
        name
      }
    }
  }
}
```

```
mutation MyMutation {
  criarPaciente(
    request: {nome: "Jeff Sutterland", email: "seu_email@email,.com", user: {username: "333", password: "333"}}
  ) {
    email
    id
    nome
    user {
      enabled
      id
      password
      username
      role {
        id
        name
      }
    }
  }
}
```

```
mutation MyMutation {
  atualizarEnfermeiro(
    id: "1"
    request: {nome: "Robert Cecil Martin", user: {username: "999", password: "999"}}
  ) {
    id
    nome
    user {
      enabled
      id
      password
      username
      role {
        id
        name
      }
    }
  }
}
```

```
mutation MyMutation {
  atualizarMedico(
    id: "1"
    request: {nome: "Martin Refactor Fowler", user: {username: "888", password: "888"}}
  ) {
    id
    nome
    user {
      enabled
      id
      password
      username
      role {
        id
        name
      }
    }
  }
}
```

```
mutation MyMutation {
  atualizarPaciente(
    id: "1"
    request: {nome: "Jeff Scrum Suttherland", email: "seu_email_aqui@gmail.com", user: {username: "777", password: "777"}}
  ) {
    email
    id
    nome
    user {
      enabled
      id
      password
      username
      role {
        id
        name
      }
    }
  }
}
```

```
mutation MyMutation {
  apagarEnfermeiro(id: "1")
}
```

```
mutation MyMutation {
  apagarMedico(id: "1")
}
```

```
mutation MyMutation {
  apagarPaciente(id: "1")
}
```

```
mutation MyMutation {
  criarConsulta(
    request: {dataHora: "2025-01-01T10:05:55", medicoId: "1", pacienteId: "1"}
  ) {
    dataHora
    id
    status
    medico {
      nome
      id
    }
    paciente {
      nome
      email
      id
    }
  }
}
```

```
mutation MyMutation {
  criarHistoricoMedico(
    request: {diagnostico: "Diagnóstico teste", prescricao: "Prescrição Teste", consultaId: "1", exames: "Exame teste"}
  ) {
    diagnostico
    exames
    id
    prescricao
    consulta {
      dataHora
      id
      status
      medico {
        nome
      }
      paciente {
        nome
      }
    }
  }
}
```

```
mutation MyMutation {
  atualizarConsulta(
    id: "1"
    request: {dataHora: "2025-02-20T14:30:22", medicoId: "1", pacienteId: "1"}
  ) {
    dataHora
    id
    status
    medico {
      nome
    }
    paciente {
      nome
      email
    }
  }
}
```

```
mutation MyMutation {
  atualizarHistoricoMedico(
    request: {diagnostico: "Diagnóstico Atualizado", prescricao: "Prescrição Atualizada", consultaId: "1", exames: "Exames Atualizado"}
  ) {
    diagnostico
    exames
    id
    prescricao
    consulta {
      status
      dataHora
      medico {
        nome
      }
      paciente {
        nome
        email
      }
    }
  }
}
```

```
mutation MyMutation {
  concluirConsulta(id: "1")
}
```

```
mutation MyMutation {
  cancelarConsulta(id: "2")
}
```

```
query MyQuery {
  consultarEnfermeiroPorId(id: "1") {
    id
    nome
  }
}
```

```
query MyQuery {
  consultarMedicoPorId(id: "1") {
    id
    nome
  }
}
```

```
query MyQuery {
  consultarPacientePorId(id: "1") {
    email
    id
    nome
  }
}
```

```
query MyQuery {
  consultarConsultaPorId(id: "2") {
    dataHora
    id
    status
    medico {
      nome
    }
    paciente {
      nome
      email
    }
  }
}
```

```
query MyQuery {
  consultarHistoricoMedicoPorIdConsulta(id: "1") {
    diagnostico
    exames
    id
    prescricao
    consulta {
      id
      medico {
        nome
      }
      paciente {
        nome
        email
      }
      status
    }
  }
}
```

```
query MyQuery {
  listarHistoricoDeConsultasPorIdPaciente(id: "1") {
    dataHora
    id
    status
    medico {
      nome
    }
    paciente {
      nome
      email
    }
  }
}
```

```
query MyQuery {
  listarHistoricoMedicoPorIdPaciente(id: "1") {
    diagnostico
    exames
    id
    prescricao
    consulta {
      dataHora
      id
      status
      medico {
        nome
      }
      paciente {
        email
        nome
      }
    }
  }
}
```

```
query MyQuery {
  pesquisarConsulta(filtro: {pacienteId: "1", status: "AGENDADO"}) {
    dataHora
    id
    status
    medico {
      nome
    }
    paciente {
      nome
      email
    }
  }
}
```

```
query MyQuery {
  pesquisarHistoricoMedico(filtro: {consultaId: "1"}) {
    diagnostico
    exames
    id
    prescricao
    consulta {
      id
      medico {
        nome
      }
      paciente {
        nome
        email
      }
    }
  }
}
```

Mais informações podem ser adquiridas via Graphql (rode o docker compose): http://localhost:9050/graphiql


## Configuração do Projeto

#### Configuração do Docker Compose

```
volumes:
  db_hospital:
    name: db_hospital

networks:
  communication: 
    name: communication
    driver: bridge

services:

  kafka:
    image: bitnami/kafka:latest
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      - KAFKA_ENABLE_KRAFT=yes 
      - KAFKA_CFG_PROCESS_ROLES=broker,controller 
      - KAFKA_CFG_NODE_ID=1 
      - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@kafka:9093 
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 
      - KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT 
      - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER 
      - ALLOW_PLAINTEXT_LISTENER=yes 
    restart: unless-stopped
    networks:
      - communication

  notificacoes:
    container_name: notificacoes
    image: juniorsmartins/notificacoes:v0.0.1
    build:
      context: ../notificacoes
      dockerfile: Dockerfile
      args:
        APP_NAME: "notificacoes"
        APP_VERSION: "v0.0.1"
        APP_DESCRIPTION: "Serviço de Notificações."
    ports:
      - "9060:9060"
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
    restart: on-failure
    networks:
      - communication
    depends_on:
      kafka:
        condition: service_started

  challenge_hospital:
    container_name: challenge_hospital
    image: juniorsmartins/challenge_hospital:v0.0.1
    build:
      context: ../tech_challenge_hospital
      dockerfile: Dockerfile
      args:
        APP_NAME: "challenge_hospital"
        APP_VERSION: "v0.0.1"
        APP_DESCRIPTION: "Serviço de Crud de hospital."
    ports:
      - "9050:9050"
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 512M
    environment:
      - DB_HOST=challenge_database_hospital
      - DB_NAME=database_hospital
      - DB_PORT=5432
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
    restart: on-failure
    networks:
      - communication
    depends_on:
      challenge_database_hospital:
        condition: service_started
      kafka:
        condition: service_started

  challenge_database_hospital:
    container_name: challenge_database_hospital
    image: postgres:16.0
    ports:
      - "5501:5432"
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
    restart: on-failure
    environment:
      - POSTGRES_DB=database_hospital
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - db_hospital:/var/lib/postgresql/data
    networks:
      - communication
```

#### Instruções para execução local

- Passo 1: clone o projeto;
- Passo 2: abra o projeto na IDEA;
- Passo 3: abra o terminal da IDEA;
- Passo 4: entre no diretório docker (cd docker);
- Passo 5: rode o comando no diretório docker: docker compose up --build 

Espere alguns segundos e acesse o link http://localhost:9050/graphiql para efetuar as requisições. Você 
precisará logar, use o username "admin" e o password "123". Mais explicações sobre isso na sessão "Notas". <br>

## Qualidade do Código

#### Boas Práticas Utilizadas

##### Clean Code

- Nomes Significativos:
Prática: Use nomes de variáveis, métodos e classes que revelem claramente sua intenção e propósito. Evite abreviações ambíguas
ou nomes genéricos como data ou temp.

- Funções Pequenas e Focadas:
Prática: Escreva métodos curtos que façam apenas uma coisa e a façam bem. Evite métodos longos com múltiplas responsabilidades.

- Formatação Consistente:
Prática: Siga um padrão de formatação consistente, como indentação de 2 ou 4 espaços e organização lógica de classes (atributos,
construtores, métodos).

- Evite Duplicação de Código:
Prática: Refatore trechos duplicados em métodos ou classes reutilizáveis. Use padrões como Template Method ou Strategy
quando apropriado.

- Testes de Integração:
Prática: Escreva testes claros e independentes para cada funcionalidade. Use nomes de teste que descrevam o
comportamento esperado.

- Uso de Objetos e Estruturas de Dados:
Prática: Escolha entre objetos (que encapsulam comportamento) e estruturas de dados (que expõem dados) com base na
necessidade. Evite híbridos confusos.

- Simplicidade:
Prática: Priorize soluções simples e evite complexidade desnecessária. Refatore continuamente para remover código obsoleto
ou redundante.

- Limite o Escopo de Variáveis:
Prática: Declare variáveis o mais próximo possível de onde são usadas e minimize seu escopo. Evite variáveis globais ou
com vida longa desnecessária.

- Siga Convenções de Nomenclatura:
Prática: Adote convenções de nomenclatura consistentes com a linguagem e o framework, como camelCase para métodos Java
e nomes descritivos para endpoints REST.

- Refatore Incrementalmente:
Prática: Aplique melhorias contínuas ao código, refatorando pequenos trechos sempre que identificar oportunidades,
sem esperar por grandes revisões.

##### TDD

Prática ágil onde testes são escritos antes do código, seguindo o ciclo Red-Green-Refactor. <br> 


## Repositório do Código 

[Link para o repositório do código](https://github.com/juniorsmartins/fiap_pos-adj_tech_challenge_fase3)

https://github.com/juniorsmartins/fiap_pos-adj_tech_challenge_fase3


## Vídeos

O vídeo de apresentação e complementares estão no diretório "videos" - na raíz do projeto. 
Entre no diretório para ter acesso.

[Ou pode tentar acessar pelo link do diretório](https://github.com/juniorsmartins/fiap_pos-adj_tech_challenge_fase3/tree/master/videos)


## Notas

1. Esquema de Security:


2. Esquema de Exceptions:

Não foi construído esquema de tratamento de exceptions, pois não faz parte dos requisitos dessa fase 
do Tech Challenge. Logo, quando houver requisição com erro, será mostrado retorno de erro padrão. 

3. Use seu email: 

Quando for criar um paciente para testar a aplicação, use seu email para receber as notificações. Foi 
usado o serviço de email do Java Mail Sender.


## Autoria

[Junior Martins](https://www.linkedin.com/in/juniorsmartins/)

https://www.linkedin.com/in/juniorsmartins/
