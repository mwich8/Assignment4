(ns drawit.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; :draw-mode can be line("L"), circle("C") or rectangle("R")
(def app-db (reagent/atom {:draw-mode "L" :clicked-once? false :start-position-x 0 :start-position-y 0 :draw-list '()}))

(println (str (:draw-mode @app-db) " " (:clicked-once? @app-db) " x:" (:start-position-x @app-db) " y:" (:start-position-y @app-db)))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Draw it!"}))

(def draw-list2 '([:line
                  {:x1 300
                   :y1 300
                   :x2 500
                   :y2 200}]
                 [:line
                  {:x1 300
                   :y1 300
                   :x2 500
                   :y2 350}]
                 [:rect
                  {:width 20
                   :height 20
                   :fill "black"
                   :x 50
                   :y 50}]
                  [:circle
                   {:cx 350
                    :cy 350
                    :r 50
                    :fill "red"}]))

;; Dummy methods #worstCodeQualityEver
(defn line-mode
  []
  "L"
  )

(defn circle-mode
  []
  "C")

(defn rect-mode
  []
  "R")

(defn draw-line
  [x2 y2]
  (let [line [:line
              {:x1 (get-in @app-db [:start-position-x])
               :y1 (get-in @app-db [:start-position-y])
               :x2 x2
               :y2 y2}]]
    (swap! app-db update-in [:draw-list] conj line)
    ))

(defn draw-circle
  [x2 y2]
  (let [x1 (get-in @app-db [:start-position-x])
        y1 (get-in @app-db [:start-position-y])
        width (- x2 (get-in @app-db [:start-position-x]))
        height (- y2 (get-in @app-db [:start-position-y]))
        hypotenuseLength (Math/sqrt (+ (Math/pow width 2) (Math/pow height 2)))
        circle [:circle
                {:cx x1
                 :cy y1
                 :r hypotenuseLength
                 :fill "red"}]]
    (swap! app-db update-in [:draw-list] conj circle)
    ))

(defn draw-rect
  [x2 y2]
  (let [x1 (get-in @app-db [:start-position-x])
        y1 (get-in @app-db [:start-position-y])
        width (- x2 (get-in @app-db [:start-position-x]))
        height (- y2 (get-in @app-db [:start-position-y]))
        x (min x1 x2)
        y (min y1 y2)
        rect [:rect
              {:width (Math/abs width)
               :height (Math/abs height)
               :fill "green"
               :x x
               :y y}]]
    (swap! app-db update-in [:draw-list] conj rect)
    ))

(defn draw-it []
  (let [value (reagent/atom {:draw-mode "L" :clicked-once? false :start-position-x 0 :start-position-y 0})]
  [:div
   [:svg
    {:width 500
     :height 500
     :stroke "black"
     :style {:display :block :border "black solid 1px"}
     :on-click
     (fn draw
       [e]
        (if (= (:clicked-once? @app-db) false)
          (do
            ;; Set the start position of the drawing

            ;; (reset! app-db {:x :start-position} (.-clientX e))
            ;; reset! value {:x :start-position} (.-clientX e))
            ;; (prn (:x (:start-position @app-db)))
            (swap! app-db update-in [:start-position-x] #(- (.-clientX e) 10))
            (prn "sY: "(.-screenY e) " - cY: " (.-clientY e))
            (prn "sX: "(.-screenX e) " - cX: " (.-clientX e))
            (swap! app-db update-in [:start-position-y] #(- (.-clientY e) 10))
            ;; (reset! app-db (:x (:start-position 2)))
            ;; reset! value {:x :start-position} (.-clientX e))
            ;; (prn (:start-position-x @app-db) " " (:start-position-y @app-db))
            (swap! app-db update-in [:clicked-once?] not)
            )
          (do
            ;; draws the figure if a start position was set before
            (let [x-pos (- (.-clientX e) 10)
                  y-pos (- (.-clientY e) 10)]
            (case (get-in @app-db [:draw-mode])
              "L" (draw-line x-pos y-pos)
              "C" (draw-circle x-pos y-pos)
              "R" (draw-rect x-pos y-pos))
            ;; (prn "You were here before, why did you came back?")
            (swap! app-db update-in [:clicked-once?] not)
            ))))}
    (list
    (get-in @app-db [:draw-list])
    )]
   ;; Buttons for changing draw-mode
   [:button
     {:on-click
      (fn change-to-line [e]
        (do
          (when (= (get-in @app-db [:clicked-once?]) true)
            (swap! app-db update-in [:clicked-once?] not))
        (swap! app-db update-in [:draw-mode] #(line-mode))))}
     "Line"]
   [:button
     {:on-click
      (fn change-to-circle [e]
        (do
          (when (= (get-in @app-db [:clicked-once?]) true)
            (swap! app-db update-in [:clicked-once?] not))
        (swap! app-db update-in [:draw-mode] #(circle-mode))))}
     "Circle"]
   [:button
     {:on-click
      (fn change-to-rect [e]
        (do
          (when (= (get-in @app-db [:clicked-once?]) true)
            (swap! app-db update-in [:clicked-once?] not))
        (swap! app-db update-in [:draw-mode] #(rect-mode))))}
     "Rectangle"]
   [:button
     {:on-click
      (fn undo [e]
        (when (not-empty (get-in @app-db [:draw-list]))
          (swap! app-db update-in [:draw-list] rest)
          ))}
     "Undo"]]))

(reagent/render-component [draw-it]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
