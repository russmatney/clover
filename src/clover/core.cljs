(ns clover.core
  (:require ["electron" :refer [app BrowserWindow]]))

(def main-window (atom nil))

(defn browser-window [prefs]
  (-> prefs
      clj->js
      (BrowserWindow.)))

(defn init-browser [{:keys [title url] :as opts}]
  (println "init-browser" opts)
  (reset! main-window
          (browser-window
            {:transparent     true
             :backgroundColor "#00ffffff"
             :titleBarStyle   "hidden"
             :frame           false
             :thickFrame      false
             :type            "splash"
             :title           (str "clover/" (or title "no-title"))

             :webPreferences
             {:enableRemoteModule         true
              :nodeIntegrationInSubFrames true
              :nodeIntegration            true}}))
  ;; Path is relative to the compiled js file (main.js in our case)
  (.loadURL ^js @main-window url)

  (println "main-window reset" opts)
  (.on ^js @main-window "closed" #(reset! main-window nil)))

(defn main [title url & args]
  (println "Starting clover frame with params" title url args)

  (.on app "window-all-closed" #(.quit app))
  (.on app "ready"
       (fn []
         (let [pause 3000 #_ 0]
           (println "app ready")
           (js/setTimeout
             #(init-browser {:title title :url url})
             pause)))))
