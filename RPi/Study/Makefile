all: study

study: study.o parson.o
	g++ -o study study.o parson.o -std=c++11 -lwiringPi

parson.o: parson.c parson.h
	g++ -c -o parson.o parson.c -std=c++11 -lwiringPi

clean:
	rm -f study *.o
