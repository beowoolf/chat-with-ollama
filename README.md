# Chat with Ollama

[![Maven](https://img.shields.io/badge/Maven-3.9.9-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Java](https://img.shields.io/badge/Java-25-007396?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.4-6DB33F?logo=spring&logoColor=white)](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
[![Version](https://img.shields.io/badge/version-0.0.1--SNAPSHOT-blue)](pom.xml)
[![License](https://img.shields.io/badge/license-not%20specified-lightgrey)](#license)

A Spring Boot service that sends a user message to a local [Ollama](https://ollama.com/) model and returns the model's text reply.

Maven coordinates: `pl.mojezapiski:ai:0.0.1-SNAPSHOT`.

## Table of contents

- [Project description](#project-description)
- [Tech stack](#tech-stack)
- [Getting started locally](#getting-started-locally)
- [Available scripts](#available-scripts)
- [Project scope](#project-scope)
- [Project status](#project-status)
- [License](#license)

## Project description

This project is a small HTTP API for chatting with open-source large language models served by Ollama. The application description in `pom.xml` is: *A project to build a chat using Ollama's Open Source LLM models.*

A client posts a message (and, optionally, a model name) to `POST /ollama`. The service builds a Spring AI prompt, calls `OllamaChatModel`, and returns the generated text as a plain string.

If the request omits `model`, the controller uses `llama2-uncensored`.

```json
{
  "message": "Explain dependency injection in one sentence.",
  "model": "llama2-uncensored"
}
```

The application can run as an executable Spring Boot process or be deployed as a WAR. `ServletInitializer` registers `AiApplication` for an external servlet container. Embedded Tomcat is declared with `provided` scope.

There is no web UI, persistence, authentication, or conversation history. Each request is a single prompt/response call.

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Java 25 |
| Build | Apache Maven 3.9.9 (Maven Wrapper) |
| Framework | Spring Boot 3.5.16 |
| AI integration | Spring AI 1.1.4 (`spring-ai-ollama-spring-boot-starter`) |
| HTTP API | Spring Web (`spring-boot-starter-web`) |
| Reactive stack | Spring WebFlux and Project Reactor (on the classpath; the public endpoint is synchronous) |
| Boilerplate | Lombok (`@RequiredArgsConstructor`, optional, excluded from the packaged artifact) |
| Servlet container | Apache Tomcat (`spring-boot-starter-tomcat`, `provided`) |
| Packaging | WAR (`ai-0.0.1-SNAPSHOT.war`) |
| Tests | JUnit via `spring-boot-starter-test`, plus `reactor-test` |

Runtime configuration in `src/main/resources/application.properties` sets only the application name:

```properties
spring.application.name=ai
```

Ollama connection settings are left to Spring AI defaults. The usual local base URL is `http://localhost:11434` (`spring.ai.ollama.base-url`).

Further reading:

- [Spring AI Ollama Chat](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
- [Ollama](https://ollama.com/)

## Getting started locally

### Prerequisites

- JDK 25
- [Ollama](https://ollama.com/download) installed and running
- The model you intend to call pulled locally. The built-in default is `llama2-uncensored`

Maven does not need to be installed globally. Use `mvnw` (macOS/Linux) or `mvnw.cmd` (Windows) from the repository root. The wrapper downloads Maven 3.9.9.

### 1. Start Ollama and pull a model

Start the Ollama daemon, then pull the default model (or any other model you will send in the request):

```bash
ollama pull llama2-uncensored
```

### 2. Run the application

From the repository root:

```bash
# macOS / Linux
./mvnw spring-boot:run
```

```bat
mvnw.cmd spring-boot:run
```

The API listens on port `8080`.

To point at an Ollama server that is not on the default host, add a property before starting:

```properties
spring.ai.ollama.base-url=http://localhost:11434
```

### 3. Send a chat request

```bash
curl -X POST http://localhost:8080/ollama \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"Say hello in one short sentence.\",\"model\":\"llama2-uncensored\"}"
```

`model` may be omitted. The response body is the model text, not a JSON envelope.

### 4. Package a WAR

```bash
./mvnw package
```

The artifact is `target/ai-0.0.1-SNAPSHOT.war`. Spring Boot's plugin produces an executable WAR, so it can also be started with:

```bash
java -jar target/ai-0.0.1-SNAPSHOT.war
```

The same WAR can be deployed to an external servlet container. Tomcat is not bundled as a compile dependency (`provided`).

## Available scripts

This repository uses Maven goals through the wrapper. There is no separate script runner.

| Command | Purpose |
| --- | --- |
| `./mvnw spring-boot:run` | Start the app with the embedded server |
| `./mvnw test` | Run tests (`AiApplicationTests.contextLoads`) |
| `./mvnw package` | Compile, test, and build `target/ai-0.0.1-SNAPSHOT.war` |
| `./mvnw clean` | Remove the `target` directory |
| `./mvnw clean package` | Clean build of the WAR |

On Windows, replace `./mvnw` with `mvnw.cmd`.

`contextLoads` checks that the Spring context starts. It does not call Ollama.

## Project scope

In scope today:

- Spring Boot entry point (`AiApplication`) and WAR bootstrap (`ServletInitializer`)
- `POST /ollama` accepting a JSON body mapped to `UserRequest` (`message`, `model`)
- Model selection per request, with `llama2-uncensored` when `model` is null
- A synchronous `OllamaChatModel.call(...)` and a plain-text response (`ChatResponse` output text)
- A context-load smoke test

Out of scope in the current code:

- Streaming responses (`Flux` / server-sent events), even though WebFlux is a dependency
- Multi-turn memory, sessions, or stored conversations
- A frontend or static chat page
- Authentication, authorization, and request validation beyond JSON binding
- Health, metrics, or model-management endpoints
- Automated pull of Ollama models at startup (`application.properties` does not set `spring.ai.ollama.init.pull-model-strategy`)

## Project status

**Early snapshot (`0.0.1-SNAPSHOT`).** The service exposes one chat endpoint and is suitable for local experiments against a running Ollama instance.

- No continuous-integration workflow is included, so there is no build-status badge.
- `pom.xml` leaves project URL, developers, SCM, and license empty.
- `src/main/resources/application.properties` does not override the Ollama base URL or default chat model. The default model name is hard-coded in `OllamaRestController`.
- Tests cover application context startup only.

## License

No project license is declared. `pom.xml` contains an empty `<license>` element, and the repository has no `LICENSE` file.

The Apache License 2.0 notice in `.mvn/wrapper/maven-wrapper.properties` applies to the Maven Wrapper, not to this application.
