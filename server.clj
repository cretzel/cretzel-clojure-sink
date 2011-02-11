(import 
 '(java.net ServerSocket InetSocketAddress)
 '(java.nio.channels ServerSocketChannel)
 '(java.io BufferedInputStream BufferedOutputStream InputStreamReader OutputStreamWriter BufferedReader))

(defn sendMessage 
  "sends a message to the client over its output stream"
  [out message]
  (.write out (str message "\n"))
  (.flush out))

(defn receiveMessage
  "receives a message from the client over its input stream"
  [in]
  (.readLine in))

(defn exitChat
  "client exits the chat, close all"
  [clientSocket in out]
  (.close out)
  (.close in)
  (.close clientSocket)
  (println "Client disconnected"))

(defn chat 
  "initiate the chat, start a request response loop"
  [clientSocket]
  (let [in (BufferedReader. (InputStreamReader. (BufferedInputStream. (.getInputStream clientSocket)))) 
       out (OutputStreamWriter. (BufferedOutputStream. (.getOutputStream clientSocket)))]
       (println "Starting chat")
       (loop []
	     (let [inMsg (receiveMessage in)]
		  (cond 
		   (= inMsg "exit") (exitChat clientSocket in out)
		   true (do
			 (sendMessage out (str "You said '" inMsg "'"))
			 (recur)
			 )
		   )
		  )
	     )
       )
  )

(defn listen 
  "listen on the server socket for connections,
   start chat when connection is established,
   listen for new connection"
  [serverSocket]
  (println "Server startet")
  (loop []
	(let [clientSocket (.accept serverSocket)]
	     (println "Client connected")
	     (.start (Thread. #(chat clientSocket)))
	     (recur)
	     )
	)
  )

(defn server 
  "create a server socket and listen for connections"
  [host port]
  (let [channel (ServerSocketChannel/open)
       serverSocket (.socket channel)
       socketAddress (InetSocketAddress. host port)]
       (.bind serverSocket socketAddress)
       (listen serverSocket)
       )
  )

(server "localhost" 8765)
