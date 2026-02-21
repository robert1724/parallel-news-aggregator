JAVAC = javac
JAVA  = java
CP = .:lib/*

SRCS = $(wildcard *.java)
CLASS = Tema1

build:
	$(JAVAC) -cp $(CP) -Xlint:unchecked $(SRCS)

run:
	$(JAVA) -cp $(CP) $(CLASS) $(ARGS)

clean:
	rm -f *.class