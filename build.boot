(set-env!
 :source-paths    #{"src/cljs" "src/clj"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs              "1.7.228-2"  :scope "test"]
                 [adzerk/boot-cljs-repl         "0.3.3"      :scope "test"]
                 [adzerk/boot-reload            "0.4.13"     :scope "test"]
                 [binaryage/devtools            "0.9.0"      :scope "test"]
                 [binaryage/dirac               "1.1.3"      :scope "test"]
                 [com.cemerick/piggieback       "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl       "0.2.12"     :scope "test"]
                 [org.martinklepsch/boot-garden "1.3.2-0"    :scope "test"]
                 [pandeiro/boot-http            "0.7.6"      :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.2.0"      :scope "test"]
                 [weasel                        "0.7.0"      :scope "test"]
                 [cljsjs/auth0-lock             "10.8.1-0"]
                 [cljsjs/bootstrap              "3.3.6-1"]
                 [cljsjs/jquery                 "2.2.4-0"]
                 [org.clojure/clojurescript     "1.9.293"]
                 [re-frame                      "0.9.2"]
                 [re-frisk                      "0.3.2"]
                 [reagent                       "0.6.0"]
                 [secretary                     "1.2.3"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[org.martinklepsch.boot-garden :refer [garden]]
 '[powerlaces.boot-cljs-devtools :refer [cljs-devtools dirac]])

(deftask build []
  (comp (speak)
     (cljs)
     (garden :styles-var 'engaged.styles/screen
             :output-to "css/garden.css")))

(deftask run []
  (comp (serve)
     (watch)
     (cljs-repl)
     (cljs-devtools)
     (dirac)
     (reload)
     (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 garden {:pretty-print false})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none}
                 reload {:on-jsload 'engaged.app/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
     (run)))
