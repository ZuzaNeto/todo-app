(ns todo.frontend.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdom]))

;; --- 1. Nossos Componentes (Estáticos) ---

;; O Formulário (não faz nada ainda)
(defn todo-form []
  [:div.todo-input
   [:input {:type "text" :placeholder "O que precisa ser feito?"}]
   [:button "Adicionar (Desligado)"]])

;; A Lista (recebe uma lista de "todos" como argumento)
(defn todo-list [todos] ;; "todos" é um argumento!
  [:ul.todo-list
   (for [todo todos]
     ^{:key (:id todo)}
     [:li.todo-item
      (:title todo)])])

;; O App Principal (que monta tudo)
(defn app []
  [:div.todo-app
   [:h1 "Todo App (Estático)"]
   [:p "Isto é apenas o visual. Nada funciona ainda."]

   ;; 1. Renderiza o formulário
   [todo-form]

   ;; 2. Renderiza a lista, passando "dados falsos"
   [todo-list [{:id 1 :title "Meu primeiro item (falso)"}
               {:id 2 :title "Meu segundo item (falso)"}]]])

;; --- 2. A Inicialização ---
(defn ^:export init []
  (println "Frontend 'Todo Estático' inicializado...")
  (let [root (rdom/create-root (js/document.getElementById "app"))]
    (.render root (r/as-element [app]))))