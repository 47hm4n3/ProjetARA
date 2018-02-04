JFLAGS = -cp
JAVA = java
JC = javac
JVM = java
VI = vi
RM = rm -rf

BIN = bin/
LIB = lib/
LIBS = $(LIB)/djep-1.0.0.jar:$(LIB)/peersim-doclet.jar:$(LIB)/peersim-1.0.5.jar:$(LIB)/jep-2.3.0.jar
RES = resources/
BEN = benchs/

SIM = peersim.Simulator

default:
	@echo "Choisissez une execution : exo1, exo2q1, exo2q4, exo2q5, exo2q6, exo2q7, exo2q8_3, exo2q8_4"
	@echo "make votre_choix"

exo1:
	$(RM) $(BEN)/exo1.txt
	$(JAVA) $(JFLAGS) $(LIBS):$(BIN) $(SIM) $(RES)/exo1.cfg >> $(BEN)/exo1.txt
	$(VI) $(BEN)/exo1.txt

exo2q1:
	$(RM) $(BEN)/exo2q1.txt
	$(JAVA) $(JFLAGS) $(LIBS):$(BIN) $(SIM) $(RES)/exo2q1.cfg >> $(BEN)/exo2q1.txt
	$(VI) $(BEN)/exo2q1.txt

exo2q4:
	$(RM) $(BEN)/exo2q4.txt
	$(JAVA) $(JFLAGS) $(LIBS):$(BIN) $(SIM) $(RES)/exo2q4.cfg >> $(BEN)/exo2q4.txt
	$(VI) $(BEN)/exo2q4.txt

exo2q5:
	$(RM) $(BEN)/exo2q5.txt
	#for i in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0 ; do
		#sed -i -e '/protocol.emitdecor.proba / s/  .*/  $i/' $(RES)/exo2q5.cfg ;
		#sed -i "s/^\(protocol\.emitdecor\.proba\s* \s*\).*\$/\1$i/" exo2q5.cfg ;  
	$(JAVA) $(JFLAGS) $(LIBS):$(BIN) $(SIM) $(RES)/exo2q5.cfg >> $(BEN)/exo2q5.txt ; 
	#done
	$(VI) $(BEN)/exo2q5.txt

exo2q6:
	$(RM) $(BEN)/exo2q6.txt
	$(JAVA) $(JFLAGS) $(LIBS):$(BIN) $(SIM) $(RES)/exo2q6.cfg >> $(BEN)/exo2q6.txt
	$(VI) $(BEN)/exo2q6.txt

exo2q7:
	$(RM) $(BEN)/exo2q7.txt
	$(JAVA) $(JFLAGS) $(LIBS):$(BIN) $(SIM) $(RES)/exo2q7.cfg >> $(BEN)/exo2q7.txt
	$(VI) $(BEN)/exo2q7.txt

exo2q8_3:
	$(RM) $(BEN)/exo2q8_3.txt
	$(JAVA) $(JFLAGS) $(LIBS):$(BIN) $(SIM) $(RES)/exo2q8_3.cfg >> $(BEN)/exo2q8_3.txt
	$(VI) $(BEN)/exo2q8_3.txt

exo2q8_4:	
	$(RM) $(BEN)/exo2q8_4.txt
	$(JAVA) $(JFLAGS) $(LIBS):$(BIN) $(SIM) $(RES)/exo2q8_4.cfg >> $(BEN)/exo2q8_4.txt
	$(VI) $(BEN)/exo2q8_4.txt

clean:
	$(RM) $(BEN)/*

