(set-env!
 :source-paths    #{"src/cljs" "src/clj"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs              "1.7.228-2"      :scope "test"]
                 [adzerk/boot-cljs-repl         "0.3.3"          :scope "test"]
                 [adzerk/boot-reload            "0.4.13"         :scope "test"]
                 [binaryage/devtools            "0.9.0"          :scope "test"]
                 [binaryage/dirac               "1.1.3"          :scope "test"]
                 [com.cemerick/piggieback       "0.2.1"          :scope "test" :exclusions [org.clojure/clojure]]
                 [org.clojure/tools.nrepl       "0.2.12"         :scope "test" :exclusions [org.clojure/clojure]]
                 [org.martinklepsch/boot-garden "1.3.2-0"        :scope "test"]
                 [pandeiro/boot-http            "0.7.6"          :scope "test" :exclusions [org.clojure/clojure]]
                 [powerlaces/boot-cljs-devtools "0.2.0"          :scope "test"]
                 [weasel                        "0.7.0"          :scope "test" :exclusions [org.clojure/clojure]]
                 [com.amazonaws/aws-java-sdk    "1.11.98"        :scope "test" :exclusions [joda-time]]
                 [clj-http                      "2.3.0"          :scope "test"]
                 [tailrecursion/boot-bucket     "0.2.1-SNAPSHOT" :scope "test" :exclusions [com.amazonaws/aws-java-sdk-s3]]
                 [cljsjs/auth0-lock             "10.8.1-0"]
                 [cljsjs/bootstrap              "3.3.6-1"]
                 [cljsjs/jquery                 "2.2.4-0"]
                 [org.clojure/clojurescript     "1.9.293"]
                 [re-frame                      "0.9.2"]
                 [re-frisk                      "0.3.2"]
                 [reagent                       "0.6.0"]
                 [secretary                     "1.2.3"]])

(require
 '[adzerk.boot-cljs              :refer [cljs]]
 '[adzerk.boot-cljs-repl         :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload            :refer [reload]]
 '[boot.util                     :refer [info]]
 '[tailrecursion.boot-bucket     :refer [spew]]
 '[pandeiro.boot-http            :refer [serve]]
 '[clj-http.client               :refer [delete]]
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

(deftask invalidate-cache []
  (with-post-wrap fileset
    (info "Invalidating Cloudflare cache...")
    (let [result (delete "https://api.cloudflare.com/client/v4/zones/a1e5ee6bcaa84c7af10b5b6cd1ff62bf/purge_cache"
                         {:headers      {"X-Auth-Email" "cloudflare@waygate.org"
                                         "X-AUTH-Key"   (System/getenv "CF_API_KEY")}
                          :content-type :json
                          :body         "{\"purge_everything\": true}"})]
      (when (not= 200 (:status result))
        (throw (ex-info "Error invalidating cache" result)))
      (info " done\n"))))

(deftask write-to-s3 []
  (spew :bucket "engaged.rip"
        :access-key (System/getenv "AWS_ACCESS_KEY_ID")
        :secret-key (System/getenv "AWS_SECRET_ACCESS_KEY")))

(deftask deploy []
  (comp (sift :include #{#"/app.out/"} :invert true)
     (write-to-s3)
     (invalidate-cache)))

(deftask development []
  (task-options! cljs {:optimizations :none}
                 reload {:on-jsload 'engaged.app/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
     (run)))
