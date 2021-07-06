(ns clover.core
  (:require ["electron" :refer [app BrowserWindow]]))

(def main-window (atom nil))

(defn browser-window [prefs]
  (-> prefs
      clj->js
      (BrowserWindow.)))

(defn init-browser [{:keys [title url]}]
  (reset! main-window
          (browser-window
            {:transparent    true
             :frame          false
             :type           "splash"
             :title          title
             :webPreferences {:enableRemoteModule false
                              :nodeIntegration    true}}))
  (.loadURL ^js @main-window url)
  (.on ^js @main-window "closed" #(reset! main-window nil)))

(defn main [& args]
  (let [args  (->> args
                   rest ;; drop clover.js
                   (remove (fn [s]
                             (or
                               (.startsWith s "--")
                               (= s "clover.js")))))
        url   (some-> args first)
        title (or (second args) "no-title")]
    (.on app "window-all-closed" #(.quit app))
    (.on app "ready"
         (fn []
           (let [pause 1000]
             (js/setTimeout
               #(init-browser {:title (str "clover/" title) :url url})
               pause))))))

(comment
  (init-browser {:title "Mah title" :url ""}))
