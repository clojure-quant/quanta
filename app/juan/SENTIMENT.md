


Sentiment effect - study needed.
This needs to be studied more.
Collect session pivot at begin of session. Assume it has an effect over the next session.
Out of all session data, filter significant imbalance. 1 week: 15 sentiment samples (5 days over 3 sessions). 20% extreme levels means we get 3 unbalanced samples. We need 100 unbalanced samples so we can calculate any statistic. So we need 30 weeks samples. (Half a year).
Statistic: average move in session. The average should be in the other direction than the sentiment positioning.
For 10 assets we might be ok with just 20 samples, or 6 weeks.

Implementation
Asia eu us session opening calendar.
Algo: download sentiment data and append to db. Supervision: on error - telegram message. Start algo on 3 calendars. [:jp :day] [:eu :day] [:us :day]
Environment will provide session in balance via get-series.
Notbook to calculate the study.
Time: 1-2 days.
