;; # Using Python from Clojure -- DRAFT

;; author: Piotr Kaznowski

(ns noj-book.python-scratch
  (:require [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.. py.-] :as py]
            [tech.v3.datatype :as dtype]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;;

(defn render-py [code]
  (kind/hiccup
   [:div
    [:pre [:code {:class "sourceCode language-python bg-light "} code]]]))

(defmacro hide-code [form]
  `^{:kindly/hide-code true} ~form)


(defmacro render-python-code22 [code]
  `^{:kindly/hide-code true}
  (render-py ~code))

(render-python-code22 "print()")

;; (defmacro render-python-code3 [code]
;;   (with-meta
;;     `(kind/hiccup
;;       [:div
;;        [:pre [:code {:class "sourceCode language-python bg-light "} ~code]]])
;;     {:kindly/hide-code true}))


(kind/hidden (inc 9))
(inc 8)

^:kindly/hide-code
(kind/md
"```python
def foo():
    print('hello world')
```
"
 )

(render-python-code4
 "# Python
def example_function():
    y = 'This is Python code in Clay!'
    return y + 'foo'")


(require-python '[numpy :as np])
(require-python '[pandas :as pd])

(np/array [1 42 99])

(->
 (rdatasets/datasets-iris)
 :rownames
 np/array
 type

 )

(pd/DataFrame (rdatasets/datasets-iris))

(def foo "asdf")

;; ideas
;; NLP
;; image processing
;; pytorch

;; noteboks?
;; web apps? / visualization?


;; aqsdqf
(defn hello [name]
  (str "Hello, " name))

(defmacro hide [form]
  (list 'with-meta form {:kindly/hide-code true}))

(hide
 (kind/hiccup
  [:div
   [:pre [:code {:class "sourceCode language-python bg-light"} "print('yo')"]]]))

(defmacro rend-py-x [code]
  (with-meta
    `(kind/hiccup
       [:div
        [:pre [:code {:class "sourceCode language-python bg-light"} ~code]]])
    {:kindly/hide-code true}))

(rend-py-x "print('hello')")


(defmacro rend-py-y [code]
  `^{:kindly/hide-code true}
   (kind/hiccup
    [:div
     [:pre [:code {:class "sourceCode language-python bg-light"} ~code]]]))

(macroexpand-1
 (rend-py-y "print('asdf')"))

[:div [:pre [:code {:class "sourceCode language-python bg-light"} "print('asdf')"]]]



(defmacro render-python-code-hidden-99 [code]
  (let [form `(kind/hiccup
                [:div
                 [:pre [:code {:class "sourceCode language-python bg-light"} ~code]]])]
    (with-meta form {:kindly/hide-code true})))


(macroexpand-1
 (render-python-code-hidden-99 "print('foo')"))

(render-python-code-hidden-99 "print('bar')")


(defmacro hide-code [form]
  `^{:kindly/hide-code true}
  ~form)

(defn foo ^:kindly/hide-code []
 (kind/hiccup
  [:div
   [:pre [:code {:class "sourceCode language-python bg-light"} "print()"]]]))

(foo)
