# Todo List — Clojure/ClojureScript

**Tutorial original:** [Tutorial Clojure/ClojureScript: Construindo uma Aplicação Persistente e Reativa](https://lambdageo-edu.github.io/lambdageo-tutorials/clojure/)

## Descrição

Aplicação full-stack de lista de tarefas (Todo List), construída de forma incremental para estudar a arquitetura de aplicações funcionais modernas:

- **Backend:** Clojure, com [Ring](https://github.com/ring-clojure/ring) (servidor Jetty) e [Reitit](https://github.com/metosin/reitit) (roteamento), expondo uma API REST com CRUD completo.
- **Banco de dados:** SQLite, acessado via [next.jdbc](https://github.com/seancorfield/next-jdbc), com persistência real em disco (`prod.db`).
- **Frontend:** ClojureScript com [Reagent](https://github.com/reagent-project/reagent) (React) e [Shadow-CLJS](https://github.com/thheller/shadow-cljs), consumindo a API via `fetch`.

## Pré-requisitos

| Ferramenta                                                        | Versão mínima |
| ----------------------------------------------------------------- | ------------- |
| Java (JDK)                                                        | 11+           |
| [Clojure CLI](https://clojure.org/guides/install_clojure) (`clj`) | 1.11+         |
| Node.js (`node` / `npm`)                                          | 18+           |

## 👥 Créditos

**Alunos:**

- **José Nunes de Sousa Neto** — Matrícula: **20250071231**

**Disciplina:** EECP0047 - TÓPICOS EM ENGENHARIA DA COMPUTAÇÃO II - INTRODUÇÃO A PROGRAMAÇÃO FUNCIONAL 

**Professor:** Prof. Dr. Sergio Souza Costa  

**Instituição:** UFMA — Universidade Federal do Maranhão  

**Semestre:** 2026.2

---

<div align="center">

**Este repositório possui fins acadêmicos.**

</div>

---
