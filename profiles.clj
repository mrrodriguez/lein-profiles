{:user {:plugins [[lein-localrepo "0.4.1" :exclusions [org.clojure/clojure]]
                  [lein-pprint "1.1.1" :exclusions [org.clojure/clojure]]
                  [cider/cider-nrepl "0.9.1" :exclusions [org.clojure/tools.nrepl
                                                          org.clojure/clojure]]]
        :jvm-opts ["-Djava.rmi.server.hostname=localhost"]
        :pedantic? false
        :checkout-deps-shares ^:replace [:source-paths]
        :global-vars {*warn-on-reflection* true}

        :injections [(defmacro setup-repl-env []
                       '(do
                          ;; Setup clojure.pprint shorthand
                          (require '[clojure.pprint])
                          (when-not (resolve 'pprint)
                            (refer 'clojure.pprint :only '[pprint]))
                          (when-not (some #{'pp} (keys (ns-aliases *ns*)))
                            (alias 'pp 'clojure.pprint))

                          (require '[clojure.repl])
                          (when-not (resolve 'doc)
                            (refer 'clojure.repl :only '[doc]))
                          (when-not (resolve 'source)
                            (refer 'clojure.repl :only '[source]))
                          (when-not (resolve 'pst)
                            (refer 'clojure.repl :only '[pst]))
                          
                          (try (require '[schema.core])
                               (when-not (some #{'sc} (keys (ns-aliases *ns*)))
                                 (alias 'sc 'schema.core))
                               (catch Exception e
                                 ;; Nothing we can do, the dependency is missing.
                                 ))
                          (defmacro switch-ns [ns-sym & _]
                            (let [ns-sym (if (seq? ns-sym) (second ns-sym) ns-sym)]
                              `(do
                                 (require '~ns-sym)
                                 (in-ns '~ns-sym)
                                 (~'user/setup-repl-env))))
                          (defn clear-ns-vars [ns]
                            (doseq [[k v] (ns-publics (the-ns ns))]
                              (ns-unmap ns k)))
                          (defn reload-all []
                            (require (ns-name *ns*) :reload-all))
                          (defn reload-ns []
                            (require (ns-name *ns*) :reload))))

                     (setup-repl-env)]}}
