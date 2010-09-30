.SUFFIXES: .java .class

target_jar = rel/hanzitrainer.jar

packages = hanzitrainer hanzitrainer/md5 hanzitrainer/settings hanzitrainer/internals
classes = $(foreach dir, $(packages), $(wildcard src/$(dir)/*.java))
objects = $(patsubst src/%,obj/%,$(patsubst %.java,%.class,$(classes)))

.PHONY : all
all : dirs $(target_jar) libs

.PHONY : libs
libs : 
	cp -Rf lib/ rel/lib

$(target_jar) : src/MANIFEST.MF $(objects)
	cp -Rf resources/* obj/
	jar cfm $@ src/MANIFEST.MF -C obj . 

$(objects) : obj/%.class : src/%.java 
	@echo javac: $<
	@javac $< -d obj/ -classpath obj/:lib/:lib/appframework-1.0.3.jar -sourcepath src/


.PHONY : dirs
dirs : 
	mkdir -p rel/ 
	mkdir -p obj/

.PHONY : docs
docs :
	mkdir -p docs/
	javadoc -d docs -public -sourcepath src -subpackages hanzitrainer

.PHONY : clean
clean : 
	rm -Rf rel/
	rm -Rf obj/
	rm -Rf docs/

.PHONY : print
print :
	@echo "Source files :\n"
	@echo $(classes)
	@echo "\nClasses :\n"
	@echo $(objects)
