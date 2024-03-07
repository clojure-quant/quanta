(ns ta.interact.options
  (:require 
   [ta.interact.template :as db])
  )



(defn apply-options
  "returns the template-spec (with the applied options)"
  [template-spec options])


{:name :finance/sales-tax
 :opts-fn (fn []
            {:current {:year 2023
                       :quarter 1}
             :options [{:path :year
                        :name "Year"
                        :spec (range 2018 2025)}
                       {:path :quarter
                        :name "quarter"
                        :spec [1 2 3 4]}]})}





{:name :finance/receiveable-clients
 :opts-fn (fn []
            {:current {:category :overdue}
             :options [{:path :category
                        :name "Category"
                        :spec [:overdue :bad-debt :not-due :all]}]})}

{:name :client/client-invoices
 :opts-fn (fn []
            {:current {:distributor-id 77}
             :options [{:path :distributor-id
                        :name "Client"
                        :spec (into [] (get-distributors))}]})}

{:name :fulfillment/aftership
 :opts-fn (fn []
            {:current {:days 30}
             :options [{:path :days
                        :name "Days"
                        :spec [30 60 90 180 365 (* 365 5)]}]})}

{:name :fulfillment/shiphero-orders
 :opts-fn (fn []
            {:current {:status "*"
                       :days 30}
             :options [{:path :status
                        :name "Status"
                        :spec ["*" "cancelled" "fulfilled"]}
                       {:path :days
                        :name "Days"
                        :spec [30 60 90 180 365 (* 365 5) (* 365 10)]}]})}

{:name :marketing/mailgun-email-campaign
 :opts-fn (fn []
            (let [{:keys [templates lists]} (mailgun/crbclean-email-options)]
              {:current {:template "christmas1"
                         :list "test@marketing.crbclean.com"}
               :options [{:path :template
                          :name "Template"
                          :spec templates}
                         {:path :list
                          :name "MailingList"
                          :spec lists}]}))}