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
  "Gets url as unique parameter."
  [url]
  (java.net.URL. url)
  )

(defn fetch-clj
  "An alternative fetch function to pass a User Agent string to the webserver. Url and user agent as parameteres, insecure true or not as optional"
  [url user-agent & insecure-tf]
  (client/get url {:insecure insecure-tf :headers {"User-Agent" user-agent}})
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
  "Fetch data from Aguascalientes. Span confirmados is used twice.
  descartados = negativos"
  []
  (let [source (html/html-resource(fetch "https://www.aguascalientes.gob.mx/coronavirus/"))
        resultados {:clave-entidad "1"
                    :entidad "Aguascalientes"
                    :negativos (first(:content(first  (html/select source [:#C-descartados]))))
                    :sospechosos (first(:content(first  (html/select source [:#C-sospechosos]))))
                    :confirmados (first(:content(first  (html/select source [:#C-confirmados]))))
                    :fallecidos  (first(:content(second (html/select source [:#C-confirmados]))))
                    :timestamp   (timestamp)
                    }        
        ]
    resultados)
  )


(defn baja-california
  "Fetch page from url and takes only first 925 lines of text as html-resource."
  []
  (let [source (html/select
                (html/html-resource
                 (java.io.StringReader.
                  (apply str
                         (take 925
                               (clojure.string/split-lines
                                (:body
                                 (fetch-clj "http://www.bajacalifornia.gob.mx/coronavirus" {})
                                 )
                                )
                               )
                         )
                  )
                 )[:.divSemaforo :h2])
        
        resultados {:clave-entidad "2"
                    :estado "Baja California"
                    :negativos   (first(:content (nth source 0)))
                    :sospechosos (first(:content (nth source 1)))
                    :confirmados (first(:content (nth source 2)))
                    :fallecidos  (first(:content (nth source 3)))
                    }
        ]
    resultados)
)

(defn baja-california-sur
  "BCS, not bad. Activos=confirmados. No descartados"
  []
  (let [source (html/html-resource(fetch "https://coronavirus.bcs.gob.mx"))
        resultados {:clave-entidad "3"
                    :entidad "Baja California Sur"
                    :negativos   "ND"
                    :sospechosos (first(:content(nth (html/select source [:.elementor-counter-number]) 0)))
                    :confirmados (first(:content(nth (html/select source [:.elementor-counter-number]) 1)))
                    :recuperados (first(:content(nth (html/select source [:.elementor-counter-number]) 3)))
                    :fallecidos  (first(:content(nth (html/select source [:.elementor-counter-number]) 2)))
                    :timestamp (timestamp)
                    }
        ]
    resultados)
  )

(defn colima    
    "Uses positivos as confirmados."
  []
    (let [source  (html/html-resource(java.io.StringReader.(:body(client/get "http://www.col.gob.mx/coronavirus#" {:headers {"User-Agent" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:75.0) Gecko/20100101 Firefox/75.0"}}))))
          resultados {:clave-entidad "6"
                      :entidad "Colima"
                      :negativos   (clojure.string/trim (first(:content (nth (html/select source [:.pb-1]) 0))))
                      :sospechosos (clojure.string/trim (first(:content (nth (html/select source [:.pb-1]) 1))))
                      :confirmados (clojure.string/trim (first(:content (nth (html/select source [:.pb-1]) 2))))
                      :activos     (clojure.string/trim (first (:content (first(html/select source [:.activos])))))
                      :recuperados (clojure.string/trim (first (:content (first(html/select source [:.recuperados])))))
                      :fallecidos  (clojure.string/trim (first (:content (first(html/select source [:.defunciones])))))
                      :timestamp   (timestamp)
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
                    :pendientes  (first(:content (nth source 5)))
                    :fallecidos  (first(:content (nth source 6)))
                    :timestamp (timestamp)
                      }]
     resultados)

  )



(defn ciudad-de-mexico
  "There is no case. They publish based on Federal data, one day later."
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
  "Now they are reporting via webpage."
  []
  (let [source (html/select(html/html-resource(fetch "http://covid.durango.gob.mx/"))[:.count.wow :span])
        resultados {:clave-entidad   "10"
                    :entidad         "Durango"
                    :negativos       (first(:content(nth source 0)))
                    :sospechosos     (first(:content(nth source 2)))
                    :confirmados     (first(:content(nth source 4)))
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


(defn hidalgo
  "Hidalgo has a complete json to retrieve via a post"
  []
  (let [data (first(:results(:success(json/parse-string(:body(post-clj "http://148.223.224.76:9058/get/registers/end" {:headers {}})) true))))
        resultados{:clave-entidad  "13"
                   :estado         "Hidalgo"
                   :estudiados     (:casos_estudiados     data)
                   :negativos      (:casos_negativos      data)
                   :positivos      (:casos_positivos      data)
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
  (let [source (html/select(html/html-resource(fetch "https://edomex.gob.mx/covid-19"))[:.celdacentrada ])
        resultados {:clave-entidad "15"
                    :estado "Estado de México"
                    :confirmados (first (:content (nth source 501)))
                    :fallecidos  (first (:content (nth source 502)))
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
  (let [source (html/select(html/html-resource(java.io.StringReader.(:body (fetch-clj "https://coronavirus.oaxaca.gob.mx/" " " true))))[:p.has-text-centered])
        resultados {:clave-entidad "20"
                    :entidad "Oaxaca"
                    :notificados (second(:content (nth source 0)))
                    :negativos   (second(:content (nth source 1)))
                    :sospechosos (second(:content (nth source 2)))
                    :confirmados (second(:content (nth source 3)))
                    :recuperados (second(:content (nth source 4)))
                    :fallecidos  (second(:content (nth source 5)))
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
  (let [source (html/select(html/html-resource(java.io.StringReader.(:body(fetch-clj "https://www.sonora.gob.mx/coronavirus/inicio.html" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:75.0) Gecko/20100101 Firefox/75.0"))))[:.sppb-animated-number])
        resultados {:clave-entidad "26"
                    :estado "Sonora"
                    :confirmados        (:data-digit(:attrs (nth source 0)))
                    :activos            (:data-digit(:attrs (nth source 1)))
                    :recuperados        (:data-digit(:attrs (nth source 2)))
                    :fallecidos         (:data-digit(:attrs (nth source 3)))
                    :pruebas-realizadas (:data-digit(:attrs (nth source 4)))
                    :descartados        (:data-digit(:attrs (nth source 5)))}
        ]
    resultados)
  )


(defn tamaulipas
  ""
  []
  (let [source (html/select(html/html-resource(fetch "http://coronavirus.tamaulipas.gob.mx/"))[:.num-casos])
        resultados{:clave-entidad "28"
                   :estado      "Tamaulipas"
                   :positivos    (first(:content(nth source 0)))
                   :negativos    (first(:content(nth source 1)))
                   :sospechosos  (first(:content(nth source 2)))
                   :recuperados  (first(:content(nth source 3)))
                   :fallecidos   (first(:content(nth source 4)))
                   }
        ]
    resultados)

  )

(defn veracruz
  "I will comeback to this. Renders negativos in server but other data in client JS."
  []
  (let [source (html/html-resource(fetch "http://coronavirus.veracruz.gob.mx/mapa/"))
        resultados{:clave-entidad "30"
                   :estado      "Veracruz"
                   :positivos    "ND"
                   :negativos    (clojure.string/trim(first(:content(nth(:content(first(html/select source [:.info-negativos])))1))))
                   :sospechosos  "ND"
                   :recuperados  "ND"
                   :fallecidos   "ND"
                   }
        ]
    resultados)

  )

(defn yucatan
  "A PNG with the info"
  []
  (let [ resultados {:clave-entidad "31"
                      :entidad "Yucatan"
                      :sospechosos "ND"
                      :confirmados "ND"
                      :recuperados "ND"
                      :fallecidos  "ND"
                      :timestamp (timestamp)
                      }]
     resultados)

  )

(defn zacatecas
  "A PNG with the info."
  []
  (let [ resultados {:clave-entidad "32"
                      :entidad "Zacatecas"
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
  (spit "data/reporte-estados.edn" (conj {:date (timestamp) :data {:1 (aguascalientes)  :2 (baja-california-sur) }}) :append true)
  )
