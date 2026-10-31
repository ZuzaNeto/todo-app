(ns todo.frontend.core
  ;; Requeremos o "coração" do Reagent (para 'r/atom' e 'r/as-element')
  (:require [reagent.core :as r]
            ;; E o DOM do Reagent (para 'create-root', API do React 18+)
            [reagent.dom.client :as rdom]))

;; --- 1. O Componente ---
;; Um componente em Reagent é apenas uma função
;; que retorna "Hiccup" (HTML escrito como vetores CLJS).
;;
;; [:h1 "Olá"] -> <h1>Olá</h1>
(defn hello-world []
  [:div
   [:h1 "Olá, Alunos!"]
   [:p "Nossa aplicação ClojureScript está funcionando."]])

;; --- 2. A Inicialização (React 18+) ---
;; Esta é a função que o shadow-cljs.edn chama.
;; Ela "monta" nosso componente [hello-world]
;; no <div id="app"> do nosso index.html.
(defn ^:export init []
  (println "Frontend inicializado...")
  (let [root (rdom/create-root (js/document.getElementById "app"))]
    (.render root (r/as-element [hello-world]))))