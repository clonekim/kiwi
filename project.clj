(defproject kiwi "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging"1.1.0"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [ring/ring-core "1.8.2"]
                 [com.h2database/h2 "1.4.200"]
                 [nrepl "0.8.3"]
                 [integrant "0.8.0"]
                 [compojure "1.6.2"]
                 [org.commonmark/commonmark "0.17.0"]
                 ;;                 [com.github.jknack/handlebars "4.2.0"]
                 [hbs/hbs "1.0.3"]
                 [http-kit/http-kit "2.5.1"]
                 [ch.qos.logback/logback-classic "1.2.3"]]
  :repl-options {:init-ns kiwi.core}
  :main kiwi.core
  :profiles
  {:uberjar
   {:omit-source true
    :uberjar-exclusions [#"META-INF/(leiningen|maven)"]
    :aot :all}
   })
