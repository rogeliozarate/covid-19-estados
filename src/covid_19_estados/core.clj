(ns covid-19-estados.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]
            [cheshire.core :refer :all :as json]

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

(defn post-clj
  "To request via server POST"
  [url parameters]
  
  (client/post url parameters)
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
  "Seems that Durango feels ok reporting via Tweeter. "
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
  "Everything is easy with a json. I wonder why they are not offering this in a open data repo, in time series, with
  a methodological note."
  []
  (let [source (:casos (json/parse-string(slurp "https://coronavirus.guanajuato.gob.mx/infectados.json") true))
        resultados {:clave-entidad "11"
                    :entidad     "Guanajuato"
                    :descartados (:descartados    source)
                    :sospechosos (:investigacion  source)
                    :confirmados (:confirmados    source)
                    :comunitaria (:comunitaria    source)
                    :recuperados (:recuperados    source)
                    :fallecidos  (:fallecidos     source)
                    :timestamp   (timestamp)
                      }]
     resultados)
  )

(defn guerrero
  "No info."
  []
  (let [ resultados {:clave-entidad "12"
                      :entidad "Guerrero"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn hidalgo
  "Hidalgo has a complete json to retrieve via a post"
  []
  (let [data (first(:results(:success(json/parse-string(:body(post-clj "http://148.223.224.76:9058/get/registers/end" {:headers {}})) true))))
        resultados{:clave-entidad  "13"
                   :estado         "Hidalgo"
                   :estudiados     (:casos_estudiados     data)
                   :negativos      (:casos_negativos      data)
                   :sospechosos    (:casos_sospechosos    data)
                   :fallecidos     (:casos_defunciones    data)
                   :hospitalizados (:casos_hospitalizados data)
                   :ambulatorios   (:casos_ambulatorios   data)
                   :mujeres        (:casos_mujeres        data)
                   :hombres        (:casos_hombres        data)
                   :recuperados    (:casos_recuperados    data)}
                   ]
    resultados)
  )


(defn jalisco
  "This state reports federal plus UG plus particular data. I will take the total."
  []


  (let [source (html/html-resource(fetch "https://coronavirus.jalisco.gob.mx"))
        resultados {:clave-entidad "14"
                    :estado "Jalisco"
                    :confirmados (first(:content(first(html/select source[:.bg-rojo-t.c-rojo :h5 :b]))))
                    :sospechosos (first(:content(first(html/select source[:.bg-naranja-t.c-naranja :h5 :b]))))
                    :descartados (first(:content(first(html/select source[:.bg-azul-t.c-azul :h5 :b]))))
                    :fallecidos  (first(:content(first(html/select source[:.bg-gris-t.c-gris :h5 :b]))))
                             }]
    resultados)
  
  )


(defn edomex
  "Not a surprise. EdoMex reports with a PNG http://148.215.3.96:8283/imgcovid/Datos-actualizados.png
  There is a table aggregated with only two variables"
  []
  (let [source (html/select(html/html-resource(fetch "https://edomex.gob.mx/covid-19"))[:tr :td ])
        resultados {:clave-entidad "15"
                    :estado "Estado de México"
                    :confirmados (first (:content (nth source 376)))
                    :fallecidos  (first (:content (nth source 377)))
                             }]
    resultados)
  )


(defn michoacan
  "A funny increasing counter in the page."
  []
  (let [source (html/html-resource(fetch "https://michoacancoronavirus.com/"))
        resultados {:clave-entidad "16"
                    :estado "Michoacán"
                    :confirmados (:data-to (:attrs(nth (html/select source [:.numeros]) 0)))
                    :sospechosos (:data-to (:attrs(nth (html/select source [:.numeros]) 1)))
                    :negativos   (:data-to (:attrs(nth (html/select source [:.numeros]) 2)))
                    :fallecidos  (:data-to (:attrs(nth (html/select source [:.numeros]) 3)))
                    :recuperados (:data-to (:attrs(nth (html/select source [:.numeros]) 4)))
                    }]
   resultados )
  )


(defn morelos
  "A PDF sent via Whatsapp to the web programmer."
  []
  (let [ resultados {:clave-entidad "17"
                      :entidad "Morelos"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn nayarit
  "All data in a row."
  []
  (let [source (html/select (html/html-resource(fetch "https://covid19.nayarit.gob.mx/package/indexFone.php"))[:.col-sm-3 :h2])
        resultados {:clave-entidad "18"
                    :entidad "Nayarit"
                    :confirmados   (clojure.string/trim(first(:content(nth source 0))))
                    :activos       (clojure.string/trim(first(:content(nth source 1))))
                    :recuperados   (clojure.string/trim(first(:content(nth source 2))))
                    :fallecidos    (clojure.string/trim(first(:content(nth source 3))))
                    }

        ]
    resultados)
  )

(defn nuevo-leon
  "Another PDF."
  []
  (let [ resultados {:clave-entidad "19"
                      :entidad "Nuevo León"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn oaxaca
  "Another PDF."
  []
  (let [ resultados {:clave-entidad "20"
                      :entidad "Oaxaca"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn puebla
  "A PNG with the info. They don't work on weekends."
  []
  (let [ resultados {:clave-entidad "21"
                      :entidad "Puebla"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn queretaro
  "They have nice graphics. On image files."
  []
  (let [ resultados {:clave-entidad "22"
                      :entidad "Queretaro"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn quintana-roo
  ""
  []
  (let [source (html/html-resource (fetch "https://salud.qroo.gob.mx/portal/coronavirus/coronavirus.php"))
        resultados {:clave-entidad "23"
                    :entidad "Quintana Roo"
                    :negativos     (clojure.string/trim(first(:content (nth (html/select source [:.numero.border.border2]) 0))))
                    :sospechosos   (clojure.string/trim(first(:content (nth (html/select source [:.border.numero]) 1))))
                    :positivos     (clojure.string/trim(first(:content (nth (html/select source [:.border.numero]) 2))))
                    :recuperados   (clojure.string/trim(first(:content (nth (html/select source [:.border.numero]) 3))))
                    :fallecidos    (first (:content (nth (html/select source [:table :tr :td]) 4)))
                    :timestamp (timestamp)
                      }]
     resultados)

  )

(defn sonora
  ""
  []
  (let [source (html/select (html/html-resource (fetch "https://www.sonora.gob.mx/coronavirus/inicio.html"))[:.sppb-animated-number])
        resultados {:clave-entidad "26"
                    :estado "Sonora"
                    :confirmados        (first(:data-digit(nth source 0)))
                    :activos            (first(:data-digit(nth source 1)))
                    :recuperados        (first(:data-digit(nth source 2)))
                    :fallecidos         (first(:data-digit(nth source 3)))
                    :pruebas-realizadas (first(:data-digit(nth source 4)))
                    :descartados        (first(:data-digit(nth source 5)))}
        ]
    resultados)
  )

(defn write-current-data
  "Write to a file EDN"
  []
  (spit "data/reporte-estados.edn" (conj {:date (timestamp) :data {:1 (aguascalientes)  :2 (baja-california-sur)}}) :append true)
  )
