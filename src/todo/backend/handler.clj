(ns todo.backend.handler
  "Este namespace define nossas 'funções de resposta' (Handlers)."
  (:require [todo.backend.db :as db]
            [clojure.string :as str]))

(defn- parse-id
  "Converte o parâmetro :id da URL para inteiro de forma segura."
  [request]
  (try
    (some-> (get-in request [:path-params :id]) Integer/parseInt)
    (catch NumberFormatException _
      nil)))

(defn hello-handler
  "Nosso primeiro handler. Ele apenas diz 'Olá, Mundo!'"
  [_request]
  {:status 200
   :body "Hello, World!"})

;; --- Handler para Listar Todos ---
(defn list-todos-handler
  "Handler para a rota GET /api/todos."
  [_request]
  {:status 200
   :body {:todos (db/get-all-todos)}})

;; --- Handler para Criar um Todo ---
(defn create-todo-handler
  "Handler para a rota POST /api/todos."
  [request]
  (let [todo-data (:body request)
        title (some-> (:title todo-data) str/trim)]
    (if (and title (not (str/blank? title)))
      (let [new-todo (db/create-todo (assoc todo-data :title title))]
        {:status 201
         :body new-todo})
      {:status 400
       :body {:error "O 'título' (title) é obrigatório"}})))

;; --- Handler para Alternar (toggle) um Todo ---
(defn toggle-todo-handler
  "Handler para a rota POST /api/todos/:id/toggle."
  [request]
  (if-let [id (parse-id request)]
    (if-let [updated-todo (db/toggle-todo! id)]
      {:status 200 :body updated-todo}
      {:status 404 :body {:error "Todo não encontrado"}})
    {:status 400 :body {:error "ID inválido"}}))

;; --- Handler para Deletar um Todo ---
(defn delete-todo-handler
  "Handler para a rota DELETE /api/todos/:id."
  [request]
  (if-let [id (parse-id request)]
    (if-let [deleted-todo (db/delete-todo! id)]
      {:status 200 :body deleted-todo}
      {:status 404 :body {:error "Todo não encontrado"}})
    {:status 400 :body {:error "ID inválido"}}))