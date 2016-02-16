all: *.java
	mkdir web_server
	javac *.java
	mv *.class web_server 
	jar -cvmf manifest normal_web_server.jar web_server/*.class
	chmod 777 normal_web_server.jar
	chmod 777 normal_web_server
