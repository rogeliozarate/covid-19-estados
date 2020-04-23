(ns covid-19-estados.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
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

(defn aguascalientes
  "Aguascalientes have a well formed webpage with the data inside spans with id."
  []
  (let [source (html/html-resource(fetch "https://www.aguascalientes.gob.mx/coronavirus/"))
        resultados {:clave-entidad "1"
                    :entidad "aguscalientes"
                    :descartados (first(:content(first(html/select source [:#C-descartados]))))
                    :sospechosos (first(:content(first(html/select source [:#C-sospechosos]))))
                    :confirmados (first(:content(first(html/select source [:#C-confirmados]))))
                    :fallecidos  (first(:content(last(html/select  source [:#C-descartados]))))
                    }        
        ]
    resultados)
  )
