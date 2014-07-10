(ns midimatrix.core
  (:require
    [overtone.studio.midi :refer :all]
    [quil.core :as q]))

(defonce midi-out (midi-find-connected-receivers "TR-8"))

;(defonce midi-out (midi-find-connected-receivers "IAC Driver Bus 1"))

(defn setup []
  (q/smooth)
  (q/frame-rate 30)
  (q/background 255))

(defn gen-cells []
  (vec (take 32 (repeat 0))))

(def grid
  (atom [
         {:step 0 :seq [1 0 0 0 1 0 0 0 1 0 0 0 1 0 0 0 1 0 0 0 1 0 0 0 1 0 0 0 1 0 1 0] :length 15 :name "BD" :midi-note 36}
         {:step 0 :seq (gen-cells) :length 15 :name "SD" :midi-note 38}
         {:step 0 :seq (gen-cells) :length 15 :name "LT" :midi-note 43}
         {:step 0 :seq (gen-cells) :length 15 :name "MT" :midi-note 47}
         {:step 0 :seq (gen-cells) :length 15 :name "HT" :midi-note 40}
         {:step 0 :seq (gen-cells) :length 15 :name "RS" :midi-note 37}
         {:step 0 :seq (gen-cells) :length 15 :name "HC" :midi-note 39}
         {:step 0 :seq (gen-cells) :length 15 :name "CH" :midi-note 42}
         {:step 0 :seq (gen-cells) :length 15 :name "OH" :midi-note 46}
         {:step 0 :seq (gen-cells) :length 15 :name "CC" :midi-note 49}
         {:step 0 :seq (gen-cells) :length 15 :name "RC" :midi-note 51}
        ]))

(def last-clicked (atom [100 100]))

(defn advance-step [grid]
  (vec (for [row grid]
    (if (< (:step row) (:length row))
      (update-in row [:step] inc)
      (assoc-in row [:step] 0)))))

(defn button [x y status]
  (q/stroke-weight 0)

  (cond
    (= status :step) (q/fill (q/color 255 0 0))
    (= status :step-on) (q/fill (q/color 205 80 255))
    (= status :disabled) (q/fill 50)
    (= status :disabled-on) (q/fill 90)
    (= status :on) (q/fill 170)
    (= status :off) (q/fill (q/color 255 255 0)))

  (q/rect (+ 55 (* y 30)) (+ (* x 30) 20) 20 20))

(defn draw-matrix [grid]
  (doseq [x (range (count grid)) y (range (count (-> grid (nth x) :seq)))]
    (let [row (-> grid (nth x))
          cell (-> row :seq (nth y))
          status (cond
                   (and (> y (:length row)) (> cell 0)) :disabled-on
                   (> y (:length row)) :disabled
                   (and (= y (:step row)) (> cell 0)) :step-on
                   (= y (:step row)) :step
                   (> cell 0) :off
                   :else :on
                 )]
      (button x y status))))

(defn trigger [grid]
  (doseq [x (range (count grid))]
    (let [row (-> grid (nth x))
          cell (-> row :seq (nth (:step row)))]
         (if (> cell 0)
           (midi-note (first midi-out) (:midi-note row) 100 100 9)))))

(defn draw-labels []
  (doseq [x (range (count @grid))]
    (q/fill 100)
    (q/text (-> @grid (nth x) :name) 20 (+ 35 (* x 30)))))

(defn draw []
  (q/background 32)
  (draw-labels)

  (when
    (= (mod (q/frame-count) 4) 0)
      (swap! grid advance-step)
      (trigger @grid))

  (draw-matrix @grid))

(defn toggle-button []
  (let [x (int (- (Math/round (float (/ (q/mouse-y) 30))) 1))
        y (int (- (Math/round (float (/ (q/mouse-x) 30))) 2))
        cell (get-in (vec @grid) [x :seq y])
        toggled-value (if (> cell 0) 0 100)]
    (reset! last-clicked [x y])
    (if (q/key-pressed?)
      (reset! grid (assoc-in @grid [x :length] y))
      (reset! grid (assoc-in @grid [x :seq y] toggled-value)))))

(defn drag-mouse []
  (let [x (int (- (Math/round (float (/ (q/mouse-y) 30))) 1))
        y (int (- (Math/round (float (/ (q/mouse-x) 30))) 2))]
    (when (not (= @last-clicked [x y]))
      (toggle-button))))

(q/defsketch example
  :title ":midi-seq"
  :setup setup
  :draw draw
  :mouse-pressed toggle-button
  :mouse-dragged drag-mouse
  :size [1020 355])
