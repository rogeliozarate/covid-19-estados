(defproject covid-19-estados "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [nrepl "0.7.0"]
                 [clj-http "3.10.1"]
                 [enlive "1.1.6"]
                 [clj-time "0.15.2"]]
   :main ^:skip-aot covid-19-estados.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
