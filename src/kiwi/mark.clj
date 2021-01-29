(ns kiwi.mark
  (:import [org.commonmark.parser Parser]
           [org.commonmark.renderer.html HtmlRenderer]))

(defonce -parser
  (-> (Parser/builder)
      .build))

(defonce -renderer
  (-> (HtmlRenderer/builder)
      .build))

(defn parse [body]
  (let [node (.parse -parser body)]
    (.render -renderer node)))
