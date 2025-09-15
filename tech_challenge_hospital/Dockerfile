# Variáveis de tempo de construção (com valor default - opcional)
ARG IMAGE_BUILD="eclipse-temurin:21.0.3_9-jdk-alpine"
ARG IMAGE_RUN="eclipse-temurin:21.0.3_9-jre-alpine"

# 1 - Estágio de construção/build
FROM ${IMAGE_BUILD} AS builder

# Copiar todos os arquivos da raíz do projeto (origem) para dentro da imagem (destino)
COPY . .

## Constrói o projeto com o Gradle
RUN ./gradlew clean build -x test --no-daemon


# 2 - Estágio de execução
FROM ${IMAGE_RUN} AS runner

# Variáveis de tempo de construção (com valor default - opcional)
ARG APP_NAME="microsserviços"
ARG APP_VERSION="v0.0.1"
ARG APP_DESCRIPTION="Microsserviços em Java (21Lts) e Spring Boot (3.5.4)."

ARG DEVELOPER="juniorsmartins"
ARG MAINTAINER="juniorsmartins"
ARG CONTATO="juniorsoaresmartins@gmail.com"

# Metadados
LABEL aplication=${APP_NAME} \
    version=${APP_VERSION} \
    description=${APP_DESCRIPTION} \
    authors=${DEVELOPER} \
    maintainer=${MAINTAINER} \
    contato=${CONTATO}

# Instala o curl
RUN apk add --no-cache curl

COPY --from=builder /build/libs/*.jar app.jar

# Comandos
ENTRYPOINT ["java","-jar","app.jar"]

