;; # Using Python from Clojure -- DRAFT

;; author: Piotr Kaznowski

(ns noj_book.python
  {:kindly/options {:kinds-that-hide-code #{:kind/code :kind/hiccup}}}
  (:require [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.. py.-] :as py]
            [scicloj.kindly.v4.kind :as kind]))

;; One of the strengths of the Clojure ecosystem is its ability to interoperate
;; with other languages and ecosystems. Python, with its vast set of scientific,
;; data, and machine learning libraries, is a natural partner. In this chapter,
;; we’ll walk through how to use Python modules from Clojure using the
;; `libpython-clj2` library.


^:kindly/hide-code
(defn render-py
  "A helper function to render Python code nicely, by setting `kindly/options`
  to hide `kind/hiccup` at the namespece level we hide all calls to this function as well"
  [code]
  (kind/hiccup
   [:div
    [:pre [:code {:class "sourceCode language-python bg-light"} code]]]))


(do
  (require-python '[numpy :as np])
  (require-python '[pandas :as pd])
  (require-python '[builtins :as python])
  (require-python '[operator]))


;; ## some caveats
;; but you can't call `count' on it
(py/call-attr
 (np/ones [2 3])
 "__len__")
;; or simply

(python/len (np/ones [2 3]))

;; ## some Python obj operations (I don't think they matter here)
(def xs (python/list))
(py. xs append 1)



;; ## how to show clojure code without executing it by clay
(kind/code "(count (np/ones [2 3]))")
(kind/code "def foo()\n    return 'foo'")
(kind/code "# Python
def foo()
    return 'foo'")

(comment
  (count (np/ones [2 3]))

  )


;; ## how callers work + syntactic sugar
;; a sample from NumPy tutorial:

(kind/code ">>> import numpy as np
>>> a = np.arange(15).reshape(3, 5)
>>> a
array([[ 0,  1,  2,  3,  4],
       [ 5,  6,  7,  8,  9],
       [10, 11, 12, 13, 14]])
>>> a.shape
(3, 5)
>>> a.ndim
2
>>> a.dtype.name
'int64'
>>> a.itemsize
8
>>> a.size
15
>>> type(a)
<class 'numpy.ndarray'>
>>> b = np.array([6, 7, 8])
b
>>> array([6, 7, 8])
>>> type(b)
<class 'numpy.ndarray'>")


(def a (np/arange 15))

;; ## can't use `=' here, also: shows how we can use different calls
(operator/eq
   (-> a (py. "reshape" 3 5) (py.- :shape))
   (-> a (py/call-attr "reshape" 3 5) (py/get-attr "shape")))


;; ## clj type is not Python type
(type a)
(python/type a)

(-> a (py.- :dtype) (py.- :name))

;; ## another tutorial: some weirdness of clay

(kind/code ">>> np.zeros((3, 4))
array([[0., 0., 0., 0.],
       [0., 0., 0., 0.],
       [0., 0., 0., 0.]])
>>> np.ones((2, 3, 4), dtype=np.int16)
array([[[1, 1, 1, 1],
        [1, 1, 1, 1],
        [1, 1, 1, 1]],

       [[1, 1, 1, 1],
        [1, 1, 1, 1],
        [1, 1, 1, 1]]], dtype=int16)
>>> np.empty((2, 3))
array([[3.73603959e-262, 6.02658058e-154, 6.55490914e-260],  # may vary
       [5.30498948e-313, 3.14673309e-307, 1.00000000e+000]])")


;; it was sometimes caching values? => sometimes `e` is shown as vec of `[1. 1. 1.]`
;; can we format the output?
(let [zz (np/zeros [3 4])
      oo (np/ones [2 3 4] :dtype np/int16)
      ee (np/empty [2 3])]
  [zz oo ee]
  )


(kind/code
 "[[6.72070549e-310 6.72070549e-310 6.46572228e+170]
  [4.92606469e+204 1.69201034e+190 8.89755519e+247]]")

(def empt (np/empty [2 3]))
empt

;; ## third tut: using objects and interacting

(kind/code ">>> from numpy import pi
>>> np.linspace(0, 2, 9)                   # 9 numbers from 0 to 2
array([0.  , 0.25, 0.5 , 0.75, 1.  , 1.25, 1.5 , 1.75, 2.  ])
>>> x = np.linspace(0, 2 * pi, 100)        # useful to evaluate function at lots of points
>>> f = np.sin(x)")


;; this doesn't work -- why???
(kind/code "(py/from-import numpy pi)")

(def x (np/linspace 0 (* 2 np/pi) 9))
(np/sin x)

;; ## more: array ops

(kind/code ">>> B = np.arange(3)
B
>>> np.exp(B)
array([1.        , 2.71828183, 7.3890561 ])
>>> np.sqrt(B)
array([0.        , 1.        , 1.41421356])
>>> C = np.array([2., -1., 4.])
>>> np.add(B, C)
array([2., 0., 6.])")

(let [B (np/arange 3)
      C (np/array [2. -1. 4.])]
  (np/add B C))


;; ## slicing etc
(kind/code ">>> a = np.arange(10)**3
>>> a
array([  0,   1,   8,  27,  64, 125, 216, 343, 512, 729])
>>> a[2]
8
>>> a[2:5]
array([ 8, 27, 64])
# equivalent to a[0:6:2] = 1000;
# from start to position 6, exclusive, set every 2nd element to 1000
>>> a[:6:2] = 1000
>>> a
array([1000,    1, 1000,   27, 1000,  125,  216,  343,  512,  729])
>>> a[::-1]  # reversed a
array([ 729,  512,  343,  216,  125, 1000,   27, 1000,    1, 1000])")

(def a (operator/pow (np/arange 10) 3))

(py/get-item a 2)
(py/get-item a (python/slice 2 5))
(py/set-item! a (python/slice 0 6 2) 1000)
(py/get-item a (python/slice nil nil -1))

;; ## can we use clj funcs in np???

(kind/code ">>> def f(x, y):
    return 10 * x + y
>>> b = np.fromfunction(f, (5, 4), dtype=int)
>>> b
array([[ 0,  1,  2,  3],
       [10, 11, 12, 13],
       [20, 21, 22, 23],
       [30, 31, 32, 33],
       [40, 41, 42, 43]])
>>> b[2, 3]
23
>>> b[0:5, 1]  # each row in the second column of b
array([ 1, 11, 21, 31, 41])
>>> b[:, 1]    # equivalent to the previous example
array([ 1, 11, 21, 31, 41])
>>> b[1:3, :]  # each column in the second and third row of b
array ([[10, 11, 12, 13],
        [20, 21, 22, 23]])")


;; In order to run `np.fromfunction` with a func object we need to put a function into local Python namespace:
(def py-fn
  (->
   (py/run-simple-string "
def f(x, y):
    \"it's a docstring\"
    return 10*x + y ")
   (get-in [:globals "f"])))

(python/type py-fn)
(py.- py-fn :__doc__)

(def b (np/fromfunction py-fn [5 4] :dtype np/int16))

;; now our array matches the one from the tutorial:

b

;; However! "Multidimensional arrays can have one index per axis. These indices are given in a tuple separated by commas:"
;; `slice` will not work here, obviously

(py/get-item b (python/slice 2 3))

;; we have to use:

(py/get-item b [2 3])

;; which is actually exactly what we would do in clojure:

(let [two-d-arr [[0 1 2]]]
  (get-in two-d-arr [0 1]))


;; `b[:, 1]`
(py/get-item b [(python/slice nil nil) 1])

;; equivalent to `b[..., 1]`:

(py/get-item b [python/Ellipsis 3])

;; `b[1:3, :]`
(py/get-item b [(python/slice 1 3) (python/slice nil nil)])

;; `b[-1] == b[-1, :]`
(py/get-item b -1)

;; Python:

(kind/code
 ">>> for row in b:
...    print(row)
...
[0 1 2 3]
[10 11 12 13]
[20 21 22 23]
[30 31 32 33]
[40 41 42 43]")

(for [r b] r)

(kind/code
 ">>> for element in b.flat:
...    print(element)
...
0
1
2
3
10
11
12
13
20
21
22
23
30
31
32
33
40
41
42
43")


(for [e (py.- b :flat)] e)

;; ### Hiding code now works??

(render-py "def clojure_is_hidden():
    return 'works!'")

;; ### `clj-kondo` doesn't see namespaces required by `require-python`


;; # simple tutorial

(*
 (py/->jvm 2)
 (py/->jvm 1))

(type
 (python/bool 0))

(operator/__and__ 0 9)
