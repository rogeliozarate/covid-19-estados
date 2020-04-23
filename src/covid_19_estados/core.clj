(ns covid-19-estados.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]

            )
  )

;(defn -main
;  "I don't do a whole lot ... yet."
;  [& args]
;  (println "Hello, World!"))

(defn fetch
  "Fetchs the webpage"
  [url]
  (java.net.URL. url)
  )

(defn timestamp
  []
  (f/unparse (f/formatter :date-time) (t/minus (l/local-now)(t/hours 5)))
)


(defn aguascalientes
  "Aguascalientes have a well formed webpage with the data inside spans with id."
  []
  (let [source (html/html-resource(fetch "https://www.aguascalientes.gob.mx/coronavirus/"))
        resultados {:clave-entidad "1"
                    :entidad "Aguascalientes"
                    :descartados (first(:content(first(html/select source [:#C-descartados]))))
                    :sospechosos (first(:content(first(html/select source [:#C-sospechosos]))))
                    :confirmados (first(:content(first(html/select source [:#C-confirmados]))))
                    :fallecidos  (first(:content(last(html/select  source [:#C-descartados]))))
                    :timestamp   (timestamp)
                    }        
        ]
    resultados)
  )


(defn baja-california
  "I will comeback here. Something is funny with the webpage"
  []
  nil
)
