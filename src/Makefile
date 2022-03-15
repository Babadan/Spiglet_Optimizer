all: compile


compile: 
	java -jar ../lib/jtb132di.jar Spiglet.jj
	java -jar ../lib/javacc5.jar Spiglet-jtb.jj
	javac -d ../bin/ ./spiglet_optimizer/Info_table/*.java
	javac -d ../bin/ ./spiglet_optimizer/visitor/*.java
	javac -d ../bin/ -classpath ../lib/iris-0.60.jar:../lib/iris-parser-0.60.jar:./ Spiglet_Optimizer.java

run: 
	java -cp ../lib/iris-0.60.jar:../lib/iris-parser-0.60.jar:../bin/ Spiglet_Optimizer Factorial.sp


clean: 
	rm -rf ../bin/*
	rm -f Spiglet-jtb.jj
	rm -f JavaCharStream.java
	rm -f SpigletParser.java
	rm -f SpigletParserConstants.java
	rm -f SpigletParserTokenManager.java
	rm -f ParseException.java
	rm -f Token.java
	rm -f TokenMgrError.java
	rm -rf visitor
	rm -rf syntaxtree
