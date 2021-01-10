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
            {:transparent true
             :frame       false
             :type        "splash"
             :title       title

             :webPreferences
             {:enableRemoteModule false
              :nodeIntegration    false}}))

  (.loadURL ^js @main-window url)
  (println "main-window reset" opts)
  (.on ^js @main-window "closed" #(reset! main-window nil)))

(defn main [& args]
  (println "Starting clover" args)
  (let [args  (->> args
                   rest ;; drop clover.js
                   (remove (fn [s]
                             (or
                               (.startsWith s "--")
                               (= s "clover.js")))))
        url   (some-> args first)
        title (nth args 2 "no-title")]
    (.on app "window-all-closed" #(.quit app))
    (.on app "ready"
         (fn []
           (let [pause 1000]
             (println "app ready")
             (js/setTimeout
               #(init-browser {:title (str "clover/" title) :url url})
               pause))))))

(comment
  (->> ["--hello" "world"]
       (filter #(.startsWith % "--")))

  (let [v "hello"]
    (.startsWith v "he")
    )

  (println "hello")
  (init-browser
    {:title "Mah title"
     :url   ""})
  )
