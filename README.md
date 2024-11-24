# 📊 AnglePro API

API desenvolvida em Kotlin com Ktor para geração e envio de relatórios em PDF para aplicativo de avaliação fisioterapêutica.

## 🚀 Tecnologias Utilizadas

![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-FF6F00?style=for-the-badge&logo=kotlin&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS_EC2-232F3E?style=for-the-badge&logo=amazon&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

## 📋 Descrição

API responsável por gerar relatórios em PDF dos pacientes a partir dos dados armazenados no Firebase Firestore. A API é integrada com serviço de e-mail para envio automático dos relatórios gerados.

## 🛠️ Funcionalidades

- ✅ Autenticação com Firebase
- ✅ Geração de PDF com dados do paciente
- ✅ Envio de e-mail com relatório em anexo
- ✅ Integração com Firebase Firestore
- ✅ CORS configurado para segurança
- ✅ Containerização com Docker
- ✅ Deploy automatizado na AWS

## 🔒 Segurança

- Autenticação via Firebase Authentication
- Bearer Token validation
- CORS configurado para requisições seguras
- Credenciais seguras via variáveis de ambiente

## 📡 Endpoints

```http
GET /api/patients
GET /api/patients/{id}
POST /api/patients
PUT /api/patients/{id}
DELETE /api/patients/{id}
POST /api/patients/{id}/report
POST /api/patients/{id}/measurements
```

#### Parâmetros
- `userId`: ID do usuário no Firebase
- `patientId`: ID do paciente
- `email`: E-mail para envio do relatório

#### Headers necessários
```
Authorization: Bearer {firebase_token}
```

#### Respostas
- `200`: PDF gerado e enviado com sucesso
- `401`: Não autorizado
- `404`: Paciente não encontrado
- `500`: Erro interno do servidor

🏗️ Arquitetura
A API está organizada em uma arquitetura de camadas, seguindo o padrão MVC (Model-View-Controller):

- application: Essa camada contém a configuração principal da aplicação, os plugins (HTTP, Routing, Security, Serialização) e os repositórios. Essa camada segue o padrão arquitetural Repository, responsável por isolar a lógica de acesso a dados.
- config: Essa camada contém as configurações de e-mail e Firebase.
- controller: Essa camada contém os controladores da API, responsáveis por receber e processar as requisições HTTP, seguindo o padrão arquitetural Controller.
- mapper: Essa camada contém classes responsáveis por mapear as entidades de domínio (Model) para DTOs (Data Transfer Objects) e vice-versa, seguindo o padrão de Mapeador.
- model: Essa camada contém as classes de domínio (Measurement, Patient) e os DTOs, seguindo o padrão arquitetural Model.
- repository: Essa camada contém as interfaces e implementações dos repositórios, responsáveis por interagir com o banco de dados, seguindo o padrão arquitetural Repository.
- service: Essa camada contém as interfaces e implementações dos serviços de negócio, como o serviço de e-mail e o serviço de pacientes, seguindo o padrão arquitetural Service.
- util: Essa camada contém classes de extensão e utilitários.

Essa organização em camadas, seguindo os padrões arquiteturais MVC e Repository, permite a separação de responsabilidades, a escalabilidade e a manutenibilidade do código.

## 🚀 Deploy

O deploy é realizado automaticamente no Google Cloud Run através de container Docker.

### Dockerfile
```dockerfile
FROM gradle:8-jdk17 as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle dependencies --no-daemon
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/ktor-app-fat.jar /app/ktor-app.jar
COPY firebase-credentials.json /app/firebase-credentials.json
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]
```

## ⚙️ Configuração Local

1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/AnglePro-API.git
```

2. Configure as credenciais do Firebase
```bash
cp firebase-credentials.example.json firebase-credentials.json
# Adicione suas credenciais no arquivo
```

3. Build com Docker
```bash
docker build -t anglepro-api .
```

4. Execute o container
```bash
docker run -p 8080:8080 anglepro-api
```

## 📦 Dependências Principais

- Ktor: Framework web em Kotlin
- Firebase Admin SDK: Integração com Firebase
- iText: Geração de PDFs
- Apache Commons Email: Envio de e-mails
- Kotlin Serialization: Serialização JSON

## 🔍 Monitoramento

- Logs disponíveis no AWS CloudWatch
- Métricas de performance via AWS CloudWatch
- Rastreamento de erros e exceções

## 🤝 Integração

Esta API é parte do ecossistema AnglePro, integrada com:
- Aplicativo Android AnglePro
- Firebase Authentication
- Firebase Firestore
- AWS EC2

## 👨‍💻 Autor

[Guilherme Carneiro](https://github.com/guicarneiro11)
=======
# AngleProAPI
API criada utilizando Kotlin para envio dos relatórios via email
