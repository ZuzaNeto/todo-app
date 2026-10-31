(ns todo.backend.core
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [todo.backend.handler :as handler])
  (:gen-class))

(def app-routes
  (ring/router
   ["/api"
    ["/hello" {:get {:handler handler/hello-handler}}]

    ["/todos"
     {:get  {:handler handler/list-todos-handler}
      :post {:handler handler/create-todo-handler}}]]))

(def app
  (ring/ring-handler
   app-routes
   (ring/create-default-handler)
   {:middleware [[wrap-cors
                  ;; Em quais origens o backend confia:
                  :access-control-allow-origin [#"http://localhost:8000"]
                  ;; Quais métodos HTTP são permitidos:
                  :access-control-allow-methods [:get :post :put :delete]
                  ;; Quais headers o frontend pode enviar:
                  ;; (necessário para o POST com Content-Type: application/json)
                  :access-control-allow-headers ["Content-Type"]]

                 wrap-json-response
                 [wrap-json-body {:keywords? true}]
                 wrap-params
                 wrap-keyword-params]}))

(defn start-server [port]
  (println (str "Servidor iniciado na porta " port))
  (jetty/run-jetty #'app {:port port :join? false}))

(defn -main [& args]
  (let [port (Integer/parseInt (or (first args) "3000"))]
    (start-server port)))