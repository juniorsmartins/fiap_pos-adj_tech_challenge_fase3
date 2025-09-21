# PROJETO: fase 3 - Tech Challenge ADJ

Equipe: Junior Martins (rm364241)


## Índice
1. Introdução;
2. Arquitetura do Sistema;
3. Descrição dos Endpoints da API;
4. Configuração do Projeto;
5. Qualidade do Código;
6. Collections para Teste;
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
utation MyMutation {
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
- Passo 4: entre no diretório docker;
- Passo 5: rode o comando no diretório docker: docker compose up --build -d


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

- Tratamento de Erros:
Prática: Use exceções em vez de códigos de erro e forneça mensagens claras. Estruture o tratamento de erros de forma centralizada.

- Evite Duplicação de Código:
Prática: Refatore trechos duplicados em métodos ou classes reutilizáveis. Use padrões como Template Method ou Strategy
quando apropriado.

- Testes Unitários:
Prática: Escreva testes unitários claros e independentes para cada funcionalidade. Use nomes de teste que descrevam o
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

- Prefira Imutabilidade:
Prática: Sempre que possível, use objetos imutáveis e evite alterações de estado após a criação. No Java, utilize final
e coleções imutáveis.

- Siga Convenções de Nomenclatura:
Prática: Adote convenções de nomenclatura consistentes com a linguagem e o framework, como camelCase para métodos Java
e nomes descritivos para endpoints REST.

- Refatore Incrementalmente:
Prática: Aplique melhorias contínuas ao código, refatorando pequenos trechos sempre que identificar oportunidades,
sem esperar por grandes revisões.


##### SOLID

1. Princípio da Responsabilidade Única (SRP):

O Princípio da Responsabilidade Única (Single Responsibility Principle) estabelece que uma classe deve ter apenas
um motivo para mudar, ou seja, deve ser responsável por uma única parte da funcionalidade do sistema, e essa
responsabilidade deve ser totalmente encapsulada pela classe. Isso reduz o acoplamento, facilita a manutenção e
melhora a legibilidade do código, já que mudanças em uma responsabilidade específica afetam apenas uma classe.

A aplicação segue uma arquitetura com responsabilidades bem definidas:

Controllers (AbstractCreateController, ClienteCreateController, ProprietarioCreateController): A classe abstrata
AbstractCreateController lida com a operação Criar de forma genérica, enquanto as subclasses definem endpoints 
específicos. Isso é uma boa separação, pois o controlador genérico centraliza a lógica comum, e as subclasses 
configuram o contexto específico. Cada controlador tem a responsabilidade clara de gerenciar requisições HTTP.

UseCases (AbstractCreateUseCase, ClienteCreateUseCase, ProprietarioCreateUseCase): Contêm a lógica de negócio para 
a operação de criação. A classe abstrata AbstractCreateUseCase lida com a operação de forma genérica,enquanto as 
subclasses definem contextos específicos. Dessa forma, a subclasse ClienteCreateUseCase é responsável por criar 
Cliente e a subclasse ProprietarioCreateUseCase é responsável por criar Proprietario. Ou seja, cada
uma possui um único motivo para mudar. Elas mudarão apenas se mudarem as regras por seu tipo específico de usuário.

Adaptadores (UsuarioCreateAdapter, UsuarioDeleteAdapter, UsuarioFindByIdAdapter): Lidam com a persistência de dados,
interagindo com o repositório JPA. Cada adaptador lida com uma única operação de acesso a dados (salvar, deletar, buscar),
alinhando-se ao SRP.

Interfaces como CreateInputPort, UpdateInputPort e DeleteByIdInputPort definem contratos claros para operações 
específicas, garantindo que cada interface tenha uma única responsabilidade.

As classes ClienteEntity e ProprietarioEntity são responsáveis apenas por representar os dados persistidos, sem conter
lógica de negócio, o que está alinhado com o SRP.


2. Princípio Aberto/Fechado (OCP):

O Princípio Aberto/Fechado (Open/Closed Principle) estabelece que as entidades de software (classes, módulos, funções e
etc.) devem estar abertas para extensão, mas fechadas para modificação. Novas funcionalidades são adicionadas por meio
de extensões (como subclasses ou implementações), sem alterar o código existente. Isso significa que o comportamento de
uma classe pode ser estendido para atender a novos requisitos sem alterar seu código-fonte existente. O OCP promove a
flexibilidade e a reutilização do código, reduzindo o risco de introduzir erros ao modificar classes já testadas.

Classes Abstratas (AbstractCreateController, AbstractCreateUseCase): O uso de generics (T, E) permite
adicionar novos tipos de usuários (como Admin ou Funcionario) criando subclasses (AdminCreateController e
AdminCreateUseCase ou FuncionarioCreateController e FuncionarioCreateUseCase) sem modificar as classes base.

Exemplo: O método create em AbstractCreateController é reutilizado por ClienteCreateController e ProprietarioCreateController 
sem alterações, permitindo que novos tipos de usuários sejam adicionados por meio de novas subclasses.

Interfaces Genéricas (CreateInputPort, UpdateInputPort e etc.): Interfaces como CreateInputPort<T>, UpdateInputPort<T>, 
DeleteByIdInputPort<T>, InputMapper<I, U, T>, e OutputMapper<T, O, E> são genéricas, permitindo que diferentes tipos de 
usuários sejam manipulados sem alterar o contrato definido pelas interfaces. Novos tipos de usuários podem implementar essas 
interfaces sem alterar o código existente.

Exemplo: A interface CreateInputPort<T> define o método create(T domain), que é implementado por ClienteCreateUseCase e
ProprietarioCreateUseCase. Isso permite adicionar suporte a novos tipos de usuários (por exemplo, Administrador) criando novas
implementações da interface, sem modificar o código existente.

Uso de Herança na Camada de Persistência:

A classe abstrata UsuarioEntity define atributos e comportamentos comuns (como nome, email, login, senha, e endereco),
enquanto ClienteEntity e ProprietarioEntity estendem essa classe para adicionar campos específicos (numeroCartaoFidelidade
para ClienteEntity e descricao para ProprietarioEntity). Isso permite a extensão para novos tipos de entidades sem alterar
UsuarioEntity.

Exemplo: A adição de uma nova entidade, como AdministradorEntity, pode ser feita criando uma nova classe que estende
UsuarioEntity, sem modificar o código existente.


3. Princípio da Substituição de Liskov (LSP):

O Princípio de Substituição de Liskov (Liskov Substitution Principle) estabelece que objetos de uma classe derivada devem poder
substituir objetos da classe base sem alterar o comportamento correto do programa. Em outras palavras, uma subclasse deve ser
substituível por sua superclasse sem quebrar as expectativas do código que utiliza a superclasse. Isso implica que as subclasses
devem respeitar os contratos definidos pela superclasse, incluindo pré-condições, pós-condições e invariantes.

As classes ClienteCreateController e ProprietarioCreateController estendem AbstractCreateController, que define métodos genéricos para
create. Essas subclasses utilizam a implementação genérica da superclasse, passando tipos específicos via parâmetros genéricos.

Conformidade: As subclasses respeitam o contrato da superclasse, pois utilizam os métodos herdados sem alterar seu comportamento.
Por exemplo, o método create em ClienteCreateController funciona da mesma forma que em AbstractCreateController, apenas com tipos
específicos mantendo as pré-condições (entrada válida) e pós-condições (retorno de um ResponseEntity com status 201).

As classes ClienteCreateUseCase e ProprietarioCreateUseCase estendem AbstractCreateService e implementam interfaces específicas
(CreateInputPort, UpdateInputPort, DeleteByIdInputPort). A superclasse define a lógica genérica para operações de negócio, enquanto 
as subclasses apenas delegam para os métodos herdados.

Conformidade: As subclasses não alteram o comportamento dos métodos herdados, garantindo que qualquer código que utilize AbstractCreateUseCase 
possa substituir por ClienteCreateUseCase ou ProprietarioCreateUseCase sem quebrar o sistema.

As classes ClienteEntity e ProprietarioEntity estendem UsuarioEntity, que define atributos comuns (usuarioId, nome, email, login,
senha, endereco). Cada subclasse adiciona atributos específicos (numeroCartaoFidelidade para ClienteEntity e descricao para
ProprietarioEntity), mas não altera o comportamento ou os contratos da superclasse.

Conformidade: Como UsuarioEntity é uma classe de modelo de dados sem métodos complexos, as subclasses mantêm a compatibilidade com a
superclasse. Por exemplo, o repositório UsuarioRepository pode manipular instâncias de ClienteEntity ou ProprietarioEntity sem problemas,
já que ambas respeitam a estrutura definida por UsuarioEntity.

Benefício: Qualquer operação de persistência que utilize o repositório de UsuarioEntity (como buscas) funciona corretamente com 
ClienteEntity ou ProprietarioEntity.

Interfaces como CreateInputPort<T>, UpdateInputPort<T>, e DeleteByIdInputPort<T> são implementadas com tipos específicos. Essas implementações 
respeitam os contratos das interfaces, garantindo que métodos como create(T domain) ou deleteById(UUID id) funcionem como esperado.

Conformidade: As implementações específicas não introduzem comportamentos inesperados, permitindo que qualquer código que dependa dessas
interfaces seja utilizada de forma intercambiável.


4. Princípio da Segregação de Interfaces (ISP):

O Princípio de Segregação de Interfaces (Interface Segregation Principle) estabelece que os clientes não devem ser forçados a depender
de interfaces que não utilizam. Em outras palavras, uma classe não deve ser obrigada a implementar métodos que não são relevantes para
sua funcionalidade. Interfaces devem ser específicas e coesas, contendo apenas os métodos necessários para um contexto específico,
reduzindo o acoplamento e facilitando a manutenção. Muitas interfaces específicas são melhores que uma interface geral.

A aplicação define interfaces específicas para diferentes operações de negócio, como CreateInputPort<T>, UpdateInputPort<T>, DeleteByIdInputPort<T> 
e FindByIdOutputPort<E>. Cada interface contém apenas um método relacionado à sua responsabilidade (por exemplo, create para criação, 
update para atualização, deleteById para exclusão).

Conformidade: Essas interfaces são coesas e focadas em uma única operação, garantindo que as classes que as implementam não sejam forçadas a 
implementar métodos desnecessários.

Exemplo: A interface CreateInputPort<T> define apenas o método T create(T domain), e ClienteCreateUseCase implementa apenas esse método para 
criação, sem precisar de métodos irrelevantes como exclusão ou busca.

Os adaptadores de repositório (classes com o sufixo Adapter) implementam interfaces específicas (CreateOutputPort<E>, DeleteOutputPort<E>, 
FindByIdOutputPort<E>), cada uma com um único método correspondente à sua função (salvar, excluir, buscar por ID).

Conformidade: Cada adaptador implementa apenas a interface necessária para sua operação, evitando a inclusão de métodos desnecessários.

Benefício: Isso reduz o acoplamento e garante que os adaptadores sejam usados apenas para as operações que suportam.

As interfaces genéricas (CreateInputPort<T>, InputMapper<I, U, T>, etc.) permitem que diferentes tipos de entidades (Cliente e Proprietario) 
sejam manipulados sem forçar a implementação de métodos irrelevantes. 

Conformidade: A flexibilidade dos generics garante que as interfaces sejam aplicadas apenas aos tipos relevantes, mantendo a coesão.


5. Princípio da Inversão de Dependência (DIP):

O Princípio de Inversão de Dependência (Dependency Inversion Principle) estabelece que: Módulos de alto nível não devem depender de
módulos de baixo nível; ambos devem depender de abstrações. Abstrações não devem depender de detalhes; detalhes devem depender de
abstrações.

Isso significa que classes de alto nível (como controllers e usecases) devem interagir com dependências por meio de interfaces ou
classes abstratas, em vez de classes concretas. Além disso, as implementações concretas devem depender de interfaces, promovendo baixo
acoplamento, maior flexibilidade e facilidade de substituição de componentes.

A aplicação utiliza interfaces como CreateInputPort<T>, UpdateInputPort<T>, DeleteByIdInputPort<T> e FindByIdOutputPort<E> para 
definir contratos. Essas interfaces são injetadas em classes de alto nível, como AbstractCreateController, que dependem dessas 
abstrações em vez de implementações concretas.

Conformidade: AbstractCreateController depende de interfaces como CreateInputPort<T> e OutputMapper<T, O, E>, enquanto as
implementações concretas são injetadas via injeção de dependências. Isso respeita o DIP, pois o controller (módulo de alto nível) 
não depende diretamente de classes concretas.

Exemplo: No método create de AbstractCreateController, a lógica utiliza createInputPort.create e outputPresenter.toDtoResponse, sem
conhecer as implementações específicas.

Conformidade: Em AbstractCreateUseCase, dependências como EntityMapper<T, E>, CreateOutputPort<E> e FindByIdOutputPort<E> são 
injetadas como interfaces, permitindo que implementações concretas sejam fornecidas pelo contêiner do Spring sem acoplamento direto.

Benefício: Isso facilita a substituição de implementações (por exemplo, trocar um adaptador de banco de dados por um mock em testes)
sem alterar o código das classes consumidoras.

As interfaces InputMapper<I, U, T>, OutputMapper<T, O, E> e EntityMapper<T, E> são usadas para definir contratos de mapeamento e
classes como ClienteMapper e ProprietarioMapper implementam essas abstrações.

Conformidade: Classes de alto nível, como AbstractCreateController e AbstractCreateUseCase, dependem dessas interfaces, enquanto as
implementações concretas são injetadas. Isso segue o DIP, pois os detalhes dependem das abstrações.


##### TDD

Prática ágil onde testes são escritos antes do código, seguindo o ciclo Red-Green-Refactor.

Cucumber: Ferramenta de Behavior-Driven Development (BDD) capaz de escrever especificações em linguagem natural (Gherkin) mapeadas para
testes automatizados.

- Via Gradle, foi criado um módulo, chamado acceptanceTest, para organizar os testes de aceitação;
- Nesse módulo, foram escritos cenários em arquivos .feature para descrever o comportamento dos endpoints;
- E passos Gherkin foram implementados em step definitions Java, interagindo com a API via RestAssured.

Motivos para Considerar Boa Prática

Colaboração: Gherkin permite que stakeholders não técnicos validem requisitos, promovendo uma linguagem ubíqua.
Documentação Viva: Cenários .feature documentam o comportamento da API, úteis para onboarding e auditorias.
Foco no Comportamento: Testes validam resultados visíveis (ex.: status 201), não detalhes internos.
Integração com TDD: Testes de aceitação guiam o desenvolvimento iterativo.
Manutenibilidade: Step definitions reutilizáveis e testes alinhados com a Arquitetura Hexagonal.


##### Design Patterns

Strategy

O Strategy é um padrão de projeto comportamental que permite que você defina uma família de algoritmos, coloque-os em classes separadas,
e faça os objetos deles intercambiáveis.

O padrão Strategy sugere que você pegue uma classe que faz algo específico em diversas maneiras diferentes e extraia todos esses algoritmos 
para classes separadas chamadas estratégias.

A classe original, chamada contexto, deve ter um campo para armazenar uma referência para um dessas estratégias. O contexto delega o 
trabalho para um objeto estratégia ao invés de executá-lo por conta própria.

O contexto não é responsável por selecionar um algoritmo apropriado para o trabalho. Ao invés disso, o cliente passa a estratégia desejada 
para o contexto. Na verdade, o contexto não sabe muito sobre as estratégias. Ele trabalha com todas elas através de uma interface genérica, 
que somente expõe um único método para acionar o algoritmo encapsulado dentro da estratégia selecionada.

Desta forma o contexto se torna independente das estratégias concretas, então você pode adicionar novos algoritmos ou modificar os existentes 
sem modificar o código do contexto ou outras estratégias.

O projeto possui duas classes, chamadas AbstractCreateUseCase e AbstractUpdateUseCase, que implementam o Design Pattern Strategy. Essas duas 
classes são o contexto, cadastrar e atualizar, e ambos usam a interface UsuarioRulesStrategy, que é composta por várias classes com estratégias 
de regras específicas, como, por exemplo: não permitir cadastros e atualizações com nomes repetidos, emails repetidos e logins repetidos. São 
regras que garantes que tais propriedades sejam únicas. 


## Collections para Teste

#### Link para a Collection do Postman

O arquivo de coleções de teste do Postman está no diretório "postman": 
https://github.com/juniorsmartins/fiap_technical_challenge_adj/tree/master/postman

[Link para baixar coleção do Postman - Clique aqui](postman/TechChallenge-ADJ-v8.postman_collection.json)  

#### Descrição dos Testes Manuais

Você precisa subir a aplicação com o comando docker compose up --build -d (mais informações na sessão "Instruções para execução local") e
depois baixar a coleção do Postman, importá-la no Postman e dar send nas requisições e ver a resposta. Todas as requisições estão prontas
para execução. A aplicação possui um arquivo, chamado import.sql, responsável por gerar pequena massa de dados para os testes em questão.


## Repositório do Código 

[Link para o repositório do código](https://github.com/juniorsmartins/fiap_technical_challenge_adj)

https://github.com/juniorsmartins/fiap_technical_challenge_adj 


## Vídeos

O vídeo de apresentação e complementares estão no diretório "video" - na raíz do projeto. Entre no diretório para ter acesso.

[Ou pode tentar acessar pelo link do diretório](https://github.com/juniorsmartins/fiap_technical_challenge_adj/tree/master/video)


## Notas

1. Tipos de Usuários:

O projeto utiliza a Herança como solução para os tipos de usuários. Há uma entidade pai, chamada Usuário, e suas filhas, Cliente e 
Proprietario. Há estrutura de CRUD para Cliente e há outra para Proprietario. Há uso de polimorfismo para simplificar e não duplicar 
código. Bem como o uso de generics, conforme o SOLID (Princípio do Aberto-fechado), para facilitar a implementação de novos tipos de 
usuários sem precisar modificar nada.

2. Esquema de Exceptions:

O projeto usa Herança no esquema de exceções para simplificar o tratamento personalizado e global. Bem como usa internacionalização de 
mensagens em dois idiomas, Português e Inglês. Podendo, se necessário, facilmente adicionar outros idiomas nas mensagens de retorno de 
exceções.

3. Esquema de Testes:

O projeto possui um submódulo, chamado acceptanceTest, para os testes de integração/aceitação. Esses testes foram construídos via BDD/TDD 
com Cucumber. Bem como também há testes unitários construídos com JUnit e Mockito e H2, com cobertura de testes acima de 80%. 

![TechChallenge3](docs/CoberturaDeTestes-v8.png)


## Autoria

[Junior Martins](https://www.linkedin.com/in/juniorsmartins/)

https://www.linkedin.com/in/juniorsmartins/
