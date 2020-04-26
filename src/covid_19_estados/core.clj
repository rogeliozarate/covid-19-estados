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

(defn fetch-clj
  "An alternative fetch function to pass a User Agent string to the webserver."
  [url user-agent]
  (client/get url {:headers {"User-Agent" user-agent}})
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

(defn baja-california-sur
  "BCS, not bad. Activos=confirmados. No descartados"
  []
  (let [source (html/html-resource(fetch "https://www.aguascalientes.gob.mx/coronavirus/"))
        resultados {:clave-entidad "3"
                    :entidad "Baja California Sur"
                    :sospechosos (first(:content(nth (html/select(html/html-resource(fetch "https://coronavirus.bcs.gob.mx"))[:.elementor-counter-number]) 0)))
                    :confirmados (first(:content(nth (html/select(html/html-resource(fetch "https://coronavirus.bcs.gob.mx"))[:.elementor-counter-number]) 1)))
                    :recuperados (first(:content(nth (html/select(html/html-resource(fetch "https://coronavirus.bcs.gob.mx"))[:.elementor-counter-number]) 3)))
                    :fallecidos  (first(:content(nth (html/select(html/html-resource(fetch "https://coronavirus.bcs.gob.mx"))[:.elementor-counter-number]) 2)))
                    :timestamp (timestamp)
                    }
        ]
    resultados)
  )

(defn campeche
  "No hay datos visibles"
  []
  (let [ resultados {:clave-entidad "4"
                    :entidad "Campeche"
                    :sospechosos "ND"
                    :confirmados "ND"
                    :recuperados "ND"
                    :fallecidos  "ND"
                    :timestamp (timestamp)
                     }])
  )

(defn coahuila
  "Google data studio "
  []
   (let [ resultados {:clave-entidad "5"
                    :entidad "Coahuila"
                    :sospechosos "ND"
                    :confirmados "ND"
                    :recuperados "ND"
                    :fallecidos  "ND"
                    :timestamp (timestamp)
                      }]
     resultados)

  )

  (defn colima    
    "This is Colima. Some tweaking required"
    ;;(nth (html/select(html/html-resource(java.io.StringReader. (:body(client/get "http://www.col.gob.mx/coronavirus#" {:headers {"User-Agent" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:75.0) Gecko/20100101 Firefox/75.0"}}))))[:.pb-1]) 2)
  []
    (let [source  (html/select(html/html-resource(java.io.StringReader.(:body(client/get "http://www.col.gob.mx/coronavirus#" {:headers {"User-Agent" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:75.0) Gecko/20100101 Firefox/75.0"}}))))[:.pb-1])
          resultados {:clave-entidad "6"
                      :entidad "Colima"
                      :negativos   (clojure.string/trim (first(:content (nth source 0))))
                      :sospechosos (clojure.string/trim (first(:content (nth source 1))))
                      :confirmados (clojure.string/trim (first(:content (nth source 2))))
                      :recuperados (clojure.string/trim (first(:content (nth source 3))))
                      :fallecidos  (clojure.string/trim (first(:content (nth source 4))))
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn chiapas
  ""
  []
  (let [source (html/select (html/html-resource (fetch "http://coronavirus.saludchiapas.gob.mx/"))[:.card-title])
        resultados {:clave-entidad "7"
                    :entidad "Chiapas"
                    :confirmados (first(:content (nth source 0)))
                    :sospechosos (first(:content (nth source 1)))
                    :negativos   (first(:content (nth source 2)))
                    :recuperados (first(:content (nth source 3)))
                    :procesados  (first(:content (nth source 4)))
                    :fallecidos  (first(:content (nth source 5)))
                    :timestamp (timestamp)
                      }]
     resultados)

  )

(defn chihuahua
  "link to a report with a image in it. Shame on you Gov. Corral"
  []
   (let [ resultados {:clave-entidad "8"
                      :entidad "Chihuahua"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)
  )


(defn ciudad-de-mexico
  ""
  []
  (let [ resultados {:clave-entidad "9"
                      :entidad "Ciudad de México"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn durango
  ""
  []
  (let [ resultados {:clave-entidad "10"
                      :entidad "Durango"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )


(defn guanajuato
  ""
  []
  (let [ resultados {:clave-entidad "11"
                      :entidad "Guanajuato"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )


(defn write-current-data
  "Write to a file EDN"
  []
  (spit "data/reporte-estados.edn" (conj {:date (timestamp) :data {:1 (aguascalientes)  :2 (baja-california-sur)}}) :append true)
  )
