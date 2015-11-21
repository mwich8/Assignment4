(ns drawit.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; :draw-mode can be line("L"), circle("C") or rectangle("R")
(def app-db (reagent/atom {:draw-mode "L" :clicked-once? false :start-position-x 0 :start-position-y 0}))

(println (str (:draw-mode @app-db) " " (:clicked-once? @app-db) " x:" (:start-position-x @app-db) " y:" (:start-position-y @app-db)))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Draw it!"}))

(defn draw-line
  [x2 y2]
  (let [x1 (get-in @app-db [:start-position-x])
        y1 (get-in @app-db [:start-position-y])]
    ;; [:line {:x1 300 :y1 100 :x2 500 :y2 300 :fill "green"}]
    (prn "x1: " x1 "- y1: " y1 "- x2: " x2 "- y2: " y2)
    ))

(defn draw-circle
  []
  (prn "I'll draw a circle later on"))

(defn draw-rect
  []
  (prn "I'll draw a rect later on"))

(defn draw-it []
  (let [value (reagent/atom {:draw-mode "L" :clicked-once? false :start-position-x 0 :start-position-y 0})]
  [:center
   [:h1 (:text @app-state)]
   [:button
     {:on-click
      (fn change-to-line [e]
          (reset! app-db {:draw-mode "L"}))}
     "Line"]
   [:button
     {:on-click
      (fn change-to-circle [e]
        (reset! app-db {:draw-mode "C"}))}
     "Circle"]
   [:button
     {:on-click
      (fn change-to-rect [e]
        (reset! app-db {:draw-mode "R"}))}
     "Rectangle"]
   [:svg
    {:width 500
     :height 500
     :stroke "black"
     :style {:display :block :border "black solid 1px"}
     :on-click
     (fn test
       [e]
       (prn "TEST"))}
    (list
     [:line {:x1 300 :y1 100 :x2 500 :y2 300 :fill "green"}]
    [:rect
     {:width 100
      :height 100
      :fill "black"
      :x 0
      :y 0}]

    [:rect
     {:width 99
      :height 99
      :fill "white"
      :x 0.5
      :y 0.5
      :on-click
      (fn draw
        [e]
        (if (= (:clicked-once? @app-db) false)
          (do
            ;; Set the start position of the drawing

            ;; (reset! app-db {:x :start-position} (.-clientX e))
            ;; reset! value {:x :start-position} (.-clientX e))
            ;; (prn (:x (:start-position @app-db)))
            (swap! app-db update-in [:start-position-x] #(.-clientX e))
            (swap! app-db update-in [:start-position-y] #(.-clientY e))
            ;; (reset! app-db (:x (:start-position 2)))
            ;; reset! value {:x :start-position} (.-clientX e))
            ;; (prn (:start-position-x @app-db) " " (:start-position-y @app-db))
            (swap! app-db update-in [:clicked-once?] not)
            )
          (do
            ;; draws the figure if a start position was set before
            (case (get-in @app-db [:draw-mode])
              "L" (draw-line (.-clientX e) (.-clientY e))
              "C" (prn "CCCCCCCC")
              "R" (prn "RRRRRRRR"))
            ;; (prn "You were here before, why did you came back?")
            (swap! app-db update-in [:clicked-once?] not)
            )))}])]]))

(reagent/render-component [draw-it]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
