$(eval VERSION := $(shell cat VERSION))
JAR = target/smg-$(VERSION).jar

help:
	@echo
	@echo "AVAILABLE RULES"
	@echo
	@echo "jar   - Generates the jar file."
	@echo "dist  - Creates a zip file for distribution."
	@echo "clean - Removes all files generated during a build."
	@echo "all   - Does a clean, jar, and dist."
	@echo "test  - Runs the gerated smg on the sample config file."
	@echo
	
jar:
	mvn package
	
clean:
	mvn clean

dist:
	./make-dist.sh
	
all:
	mvn clean package
	./make-dist.sh
	
test: 
	./test.sh
	
	

	