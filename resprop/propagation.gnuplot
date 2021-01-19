set terminal wxt
set encoding utf8
set xrange [0:90]
set yrange [0:317080]
set xlabel "jours"
set ylabel "Infectés"

plot "Cas1" t "Sans empêchement" with linesp lt 1 pt 1,\
 "Cas2" t "Immunisation aléatoire" with linesp lt 2 pt 2,\
  "Cas3" t "Immunisation sélective" with linesp lt 3 pt 3