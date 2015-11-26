(ns drawit.core
  (:require [reagent.core :as reagent :refer [atom]]))

;; (enable-console-print!)

;; :draw-mode can be line(:line), circle(:circle) or rectangle(:rect)
(def app-db (reagent/atom {:draw-mode :line :clicked-once? false :start-position-x 0 :start-position-y 0 :draw-list '()}))

;; Dummy methods #worstCodeQualityEver
(defn line-mode
  []
  :line)

(defn circle-mode
  []
  :circle)

(defn rect-mode
  []
  :rect)

;; Methods for adding a figure to the draw-list
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
  (let [value (reagent/atom {:draw-mode :line :clicked-once? false :start-position-x 0 :start-position-y 0})]
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
            ;; Set the start position of the drawing and negates the clicked-once? property
            (swap! app-db update-in [:start-position-x] #(- (.-clientX e) 10))
            (swap! app-db update-in [:start-position-y] #(- (.-clientY e) 10))
            (swap! app-db update-in [:clicked-once?] not)
            )
          (do
            ;; Draws the figure if a start position was set before; also negates clicked-once? property
            ;; Need to substract 10 because SVG has an offset of approx 10
            (let [x-pos (- (.-clientX e) 10)
                  y-pos (- (.-clientY e) 10)]
            (case (get-in @app-db [:draw-mode])
              :line (draw-line x-pos y-pos)
              :circle (draw-circle x-pos y-pos)
              :rect (draw-rect x-pos y-pos))
            (swap! app-db update-in [:clicked-once?] not)
            ))))}
    ;; Actually the part where all elements get drawn
    (list
    (get-in @app-db [:draw-list])
    )]
   ;; Buttons for changing draw-mode and undo-button
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
        ;; Just do anything if one figure is drawn, otherwise makes no sense
        (when (not-empty (get-in @app-db [:draw-list]))
          ;; If the draw-mode has the same value as the last drawn figure
          ;; the figure can be deleted, otherwise the draw-mode has to be
          ;; set to the last drawn figure
          (if (= (key (first (get-in @app-db [:draw-list]))) (get-in @app-db [:draw-mode]))
            (swap! app-db update-in [:draw-list] rest)
            (swap! app-db update-in [:draw-mode] #(key (first (get-in @app-db [:draw-list]))))
          )))}
     "Undo"]]))

(reagent/render-component [draw-it]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
)
