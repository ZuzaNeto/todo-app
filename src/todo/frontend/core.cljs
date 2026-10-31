(ns todo.frontend.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdom]))

;; --- 1. O "Cérebro" (o r/atom) ---
;;
;; (defonce) garante que o valor (o 0) NÃO seja resetado
;; toda vez que o "hot-reload" do shadow-cljs recompilar.
(defonce app-state (r/atom 0))

;; --- 2. O Componente ---
(defn counter-app []
  [:div
   [:h1 "Entendendo o 'r/atom'"]

   ;; (Leitura): "lemos" o valor dentro da caixa usando '@'
   [:p "O valor atual do contador é: " @app-state]

   [:button
    ;; (Escrita): no clique, usamos 'swap!' para "trocar"
    ;; o valor na caixa, aplicando a função 'inc'.
    {:on-click #(swap! app-state inc)}
    "Clique para Incrementar"]

   [:button
    {:on-click #(reset! app-state 0)}
    "Resetar"]])

;; --- 3. A Inicialização ---
(defn ^:export init []
  (println "Frontend 'Contador' inicializado...")
  (let [root (rdom/create-root (js/document.getElementById "app"))]
    (.render root (r/as-element [counter-app]))))