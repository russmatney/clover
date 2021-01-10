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
             :backgroundColor "#00000000"
             :titleBarStyle   "hidden"
             :frame           false
             :thickFrame      false
             :type            "splash"
             :title           (str "clover/" (or title "no-title"))

             :webPreferences
             {:enableRemoteModule         false
              :nodeIntegrationInSubFrames false
              :nodeIntegration            false
              ;; :enableRemoteModule         true
              ;; :nodeIntegrationInSubFrames true
              ;; :nodeIntegration            true
              }}))
  ;; Path is relative to the compiled js file (main.js in our case)
  (.loadURL ^js @main-window url)

  (println "main-window reset" opts)
  (.on ^js @main-window "closed" #(reset! main-window nil)))

(defn main [& args]
  (println "Starting clover frame with params:" args)
  (let [url   (some-> args first)
        title (nth args 2 nil)]

    (.on app "window-all-closed" #(.quit app))
    (.on app "ready"
         (fn []
           (let [pause 1000]
             (println "app ready")
             (js/setTimeout
               #(init-browser {:title title :url url})
               pause))))))
