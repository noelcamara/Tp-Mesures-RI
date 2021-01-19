set terminal wxt
set encoding utf8
set xrange [0:90]
set yrange [0:317080]
set xlabel "jours"
set ylabel "Infectés"

plot "Cas1Aleatoire" t "Graphe aléatoire" with linesp lt 1 pt 1,\
 "cas1Barabasi-albert" t "Barabai-Albert" with linesp lt 2 pt 2
