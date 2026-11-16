(ns todo.frontend.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdom]
            [clojure.string :as str]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]))

;; --- O "Cérebro" Reativo ---
(defonce app-state (r/atom {:input-text ""
                            :todos []
                            :filter :all
                            :loading false
                            :error nil}))

;; --- A URL base da nossa API (o backend, porta 3000) ---
(def api-url "http://localhost:3000/api")

;; Função auxiliar: faz o fetch e converte a resposta JSON
;; em dados Clojure (mapas com keywords).
(defn fetch-json [url options]
  (-> (js/fetch url (clj->js options))
      (.then (fn [response]
               (when-not (.-ok response)
                 (throw (js/Error. (str "HTTP error: " (.-status response)))))
               (.json response)))
      ;; Converte o objeto JS em dados Clojure com chaves keyword
      (.then #(js->clj % :keywordize-keys true))))

;; Busca todos os "todos" da API
(defn get-todos []
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (let [response (<p! (fetch-json (str api-url "/todos") {:method "GET"}))]
        (swap! app-state assoc :todos (:todos response) :loading false))
      (catch js/Error e
        (swap! app-state assoc :error (.-message e) :loading false)))))

;; --- Cria um "todo" via API (POST) ---
(defn create-todo [todo-data]
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (<p! (fetch-json (str api-url "/todos")
                       {:method "POST"
                        :headers {"Content-Type" "application/json"}
                        ;; Converte o mapa Clojure em uma string JSON
                        :body (js/JSON.stringify (clj->js todo-data))}))

      ;; Se o POST funcionou, recarregamos a lista
      (get-todos)
      (catch js/Error e
        (swap! app-state assoc :error (.-message e) :loading false)))))

(defn toggle-todo
  "Chama a API para alternar o status de um todo."
  [id]
  (go
    (try
      (<p! (fetch-json (str api-url "/todos/" id "/toggle")
                       {:method "POST"}))
      ;; Se funcionou, recarrega a lista inteira
      (get-todos)
      (catch js/Error e
        (swap! app-state assoc :error (.-message e) :loading false)))))

(defn delete-todo
  "Chama a API para remover um todo."
  [id]
  (go
    (try
      (<p! (fetch-json (str api-url "/todos/" id)
                       {:method "DELETE"}))
      ;; Se funcionou, recarrega a lista
      (get-todos)
      (catch js/Error e
        (swap! app-state assoc :error (.-message e) :loading false)))))

(defn submit-input! []
  (let [title (str/trim (or (:input-text @app-state) ""))]
    (if (str/blank? title)
      (swap! app-state assoc :error "Digite um título para a tarefa antes de adicionar.")
      (do
        (create-todo {:title title})
        (swap! app-state assoc :input-text "" :error nil)))))

(defn todo-form []
  [:div.todo-input
   [:input
    {:type "text"
     :placeholder "O que precisa ser feito?"
     :value (:input-text @app-state)
     :on-change #(swap! app-state assoc :input-text (-> % .-target .-value))
     :on-key-down #(when (= (.-key %) "Enter")
                     (submit-input!))}]

   [:button
    {:on-click submit-input!}
    "Adicionar"]])

(defn status-banners []
  [:div.status-area
   (when (:loading @app-state)
     [:div.loading-banner "Sincronizando com o servidor..."])
   (when-let [err (:error @app-state)]
     [:div.error-banner
      [:span (str "Erro: " err)]
      [:button.close-err
       {:on-click #(swap! app-state assoc :error nil)}
       "×"]])])

(defn todo-filters []
  (let [current-filter (:filter @app-state)]
    [:div.todo-filters
     [:button.filter-btn
      {:class (when (= current-filter :all) "active")
       :on-click #(swap! app-state assoc :filter :all)}
      "Todas"]
     [:button.filter-btn
      {:class (when (= current-filter :active) "active")
       :on-click #(swap! app-state assoc :filter :active)}
      "Ativas"]
     [:button.filter-btn
      {:class (when (= current-filter :completed) "active")
       :on-click #(swap! app-state assoc :filter :completed)}
      "Concluídas"]]))

(defn filtered-todos []
  (let [todos (:todos @app-state)
        current-filter (:filter @app-state)]
    (case current-filter
      :active (filter #(zero? (:todos/completed %)) todos)
      :completed (filter #(= 1 (:todos/completed %)) todos)
      todos)))

(defn todo-summary []
  (let [todos (:todos @app-state)
        total (count todos)
        done (count (filter #(= 1 (:todos/completed %)) todos))
        pending (- total done)]
    (when (pos? total)
      [:div.todo-summary
       [:span (str "Total: " total)]
       [:span (str "Pendentes: " pending)]
       [:span (str "Concluídas: " done)]])))

(defn todo-list []
  (let [todos (filtered-todos)]
    (if (empty? todos)
      [:p.empty-msg "Nenhuma tarefa encontrada para este filtro."]
      [:ul.todo-list
       (for [todo todos]
         ^{:key (:todos/id todo)}
         [:li.todo-item {:class (when (= 1 (:todos/completed todo)) "completed")}

          [:input.todo-checkbox
           {:type "checkbox"
            :checked (not= 0 (:todos/completed todo))
            :on-change #(toggle-todo (:todos/id todo))}]

          [:span.todo-title (:todos/title todo)]

          [:button.delete-btn
           {:on-click #(when (js/confirm "Remover esta tarefa?")
                         (delete-todo (:todos/id todo)))}
           "X"]])])))

(defn app []
  [:div.todo-app
   [:h1 "Todo App"]
   [:p "Conectado à API. Os dados sobrevivem ao F5!"]

   [todo-form]
   [todo-filters]
   [status-banners]
   [todo-list]
   [todo-summary]])

(defn ^:export init []
  (println "Frontend inicializado...")
  (let [root (rdom/create-root (js/document.getElementById "app"))]
    (.render root (r/as-element [app])))

  ;; Ao carregar a página, busca os todos da API
  (get-todos))