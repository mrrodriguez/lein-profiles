{:user {:plugins [ ;;[lein-localrepo "0.4.1" :exclusions [org.clojure/clojure]]
                  [lein-pprint "1.1.2" :exclusions [org.clojure/clojure]]
                  ;;[lein-exec "0.3.6"]
                  [org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                  ;;[org.clojure/tools.nrepl "0.2.12"]
                  [cider/cider-nrepl "0.13.0"]]
        :jvm-opts ["-Djava.rmi.server.hostname=localhost"
                   ;;"-XX:+UnlockDiagnosticVMOptions"
                   ;;"-XX:+PrintInlining"
                   ;;"-XX:+PrintFlagsFinal"
                   ;;"-XX:+LogCompilation"
                   ;;"-XX:CompileThreshold=1000"
                   ;;"-XX:+TraceClassLoading"
                   ;;"-XX:+PrintCompilation"
                   ;;"-XX:MaxInlineSize=325"
                   ;;"-XX:FreqInlineSize=1000"
                   ;;"-Dclojure.compiler.direct-linking=true"
                   ]
        
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
                            (require (ns-name *ns*) :reload))
                          ;; TODO clean up and merge with above
                          (defn rq
                            ([] (rq (ns-name *ns*)))
                            ([ns-nom] (require ns-nom :reload)))))

                     (setup-repl-env)]}}
