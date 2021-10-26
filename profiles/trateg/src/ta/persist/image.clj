(ns ta.persist.image
  (:require
   [tech.v3.resource :as resource]
   [clojure.java.io :as io])
  (:import java.io.File
           java.awt.image.BufferedImage
           javax.imageio.ImageIO))

(defn save-png [file-name ^BufferedImage buffered-image]
  (ImageIO/write buffered-image
                 "png"
                 ^java.io.File (io/file file-name)))


