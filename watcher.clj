(def my-agent (agent 0))

(def my-watcher (agent []))

(defn my-watcher-action [messages reference]
	(let [message (str "Another message has arrived for " reference)
		  msg-count (count messages)]
		(assoc messages msg-count message))
)

(add-watcher my-agent :send-off my-watcher my-watcher-action)

(send my-agent + 2)
(send my-agent + 2)
(send my-agent + 2)

(await my-watcher)
(dorun 
	(for [msg @my-watcher] (println msg))
)

(shutdown-agents)