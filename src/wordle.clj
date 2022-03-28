(ns wordle
  (:require [clojure.string]))

(def WORD_LENGTH 5)
(def MAX_GUESSES 6)

(def words (->> (slurp "/usr/share/dict/words")
                (clojure.string/split-lines)
                (filter (partial re-matches #"^[a-z]+$"))
                (filter (comp (partial = WORD_LENGTH) count))))

(defn white [s]
  (str "\033[1m" s "\033[0m"))

(defn yellow [s]
  (str "\033[1;43m" s "\033[0m"))

(defn green [s]
  (str "\033[1;42m" s "\033[0m"))

(defn feedback [target guess]
  (let [check-letter (fn [t g]
                       (cond
                         (= t g) :here
                         (some #{g} target) :elsewhere
                         :else :nowhere))]
    (map check-letter target guess)))

(defn render-guess [guess feedback]
  (let [feedback->color {:here green :elsewhere yellow :nowhere white}]
    (apply str (map (fn [g f] ((feedback->color f) g)) guess feedback))))

(defn main [args]
  (let [target (.toUpperCase (rand-nth words))]
    (loop [num-guesses 0]
      (print "Guess a word: ")
      (flush)
      (let [guess (.toUpperCase (read-line))
            fb (feedback target guess)
            output (render-guess guess fb)]
        (println output)
        (cond
          (= guess target) (println "You win!")
          (< (inc num-guesses) MAX_GUESSES) (recur (inc num-guesses))
          :else (println "Sorry, you lost."))))))
