for i in 20 30 40 50 60 70 80 90 100 120 140 160 180 200
do
    echo "$i ------------------" >> b/bench.txt
#java -cp lib/djep-1.0.0.jar:lib/peersim-doclet.jar:lib/peersim-1.0.5.jar:lib/jep-2.3.0.jar:bin/ peersim.Simulator a/a$i >> b/bench.txt
java -cp lib/*:bin/ peersim.Simulator a/a$i >> b/bench.txt
done
