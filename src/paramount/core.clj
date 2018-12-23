(ns paramount.core
  "Provides a logging function which accepts an expression.
  Writes the log to *out* (configurable), and returns the result of the
  expression."
  (:require [clojure.string :as st]
            [clojure.pprint :refer [pprint pprint-indent]]))

(def default-config
  "Provides some sane defaults for paramount log.
  Most features are disabled, and configurable bindings
  use their respective defaults."
  {:print-length nil
   :print-level nil
   :seperator "\n"
   :out *out*
   :split? true
   :result? false
   :source? false
   :when? false
   :time? false})

(def config
  (atom {}))

(defn set-config!
  [opts]
  (reset! config opts))

(defn merge-config!
  [opts]
  (swap! config merge opts))

(defn reset-config!
  []
  (set-config {}))

(defn show-config
  ([]
  (swap! config identity))
  ([k]
   (get @config k)))

(defn readable-time
  ([nanoseconds]
   (readable-time :ms))
  ([nanoseconds unit]
   (double (/ nanoseconds
              (case unit
                :h 600000000000
                :m 60000000000
                :s  1000000000
                :ms 1000000
                :ns 1
                1000000)))))

(defmacro with-log
  ([msg expr]
   `(with-log ~msg {} ~expr))
  ([msg opts expr]
   `(let [{time?# :time?
           when?# :when?
           split?# :split?
           source?# :source?
           result?# :result?
           print-length# :print-length
           print-level# :print-level
           separator# :separator
           out# :out} (merge default-config @config ~opts)
          start-time# (when time?# (System/nanoTime))
          result# ~expr
          elapsed# (when time?# (- (System/nanoTime) start-time#))
          elapsed-time# (when time?#
                          (readable-time elapsed# time?#))]
      (print
        (st/join (or separator# " - ")
                 (remove nil? [(when split?# "----------------------------")
                               ~msg
                               (when when?# (str (java.util.Date.)))
                               (when source?# (str "Evaluated expression: " '~expr))
                               (when time?# (str "Elapsed time: " elapsed-time#))
                               (when result?# (str "Expression result: "
                                                   (binding [*print-length* (or print-length# nil)
                                                             *print-level* (or print-level# nil)]
                                                     (with-out-str (pprint result#)))))])))
      result#)))

(defmacro with-log-str
  "a wrapper around with-log to return the print as a str instead of
  the result of expr. Like clojure.core/pr-str"
  [& args]
  `(with-out-str (with-log ~@args)))
