#!/bin/sh

rm *trainerror.dat *trainloss.dat *validationlogrankloss.dat
#rm ../run*/*trainerror.dat ../run*/*trainloss.dat ../run*/*validationlogrankloss.dat

# Make all dat files
../../eda/make-graphs-trainerror.pl
../../eda/make-graphs-trainloss.pl
../../eda/make-graphs-validationlogrankloss.pl

ln -s ../*/*trainerror.dat .
ln -s ../*/*trainloss.dat .
ln -s ../*/*validationlogrankloss.dat .

# Sort all dat files
# First perl recipe adds gnuplot codes
# Second perl recipe strips final ', \'  to prevent gnuplot error
echo > graphs-trainerror.gp
echo "set terminal postscript color 12" >> graphs-trainerror.gp
echo "set output 'graphs-trainerror.ps'" >> graphs-trainerror.gp
echo "set logscale y" >> graphs-trainerror.gp
echo "plot [] [0.006:0.025] \\" >> graphs-trainerror.gp
~/dev/common-scripts/sort-curves.py *trainerror.dat | perl -ne "chop; print \"\\t'\$_' with l lw 3, \\\\\\n\"" | perl -e '$str = ""; while(<>){ $str .= $_; } $str =~ s/, \\$//s; print $str' >> graphs-trainerror.gp

echo > graphs-trainloss.gp
echo "set terminal postscript color 12" >> graphs-trainloss.gp
echo "set output 'graphs-trainloss.ps'" >> graphs-trainloss.gp
echo "set logscale y" >> graphs-trainloss.gp
echo "plot [] [] \\" >> graphs-trainloss.gp
~/dev/common-scripts/sort-curves.py *trainloss.dat | perl -ne "chop; print \"\\t'\$_' with l lw 3, \\\\\\n\""  | perl -e '$str = ""; while(<>){ $str .= $_; } $str =~ s/, \\$//s; print $str' >> graphs-trainloss.gp

echo > graphs-validationlogrankloss.gp
echo "set terminal postscript color 12" >> graphs-validationlogrankloss.gp
echo "set output 'graphs-validationlogrankloss.ps'" >> graphs-validationlogrankloss.gp
#echo "set logscale y" >> graphs-validationlogrankloss.gp
echo "plot [] [] \\" >> graphs-validationlogrankloss.gp
~/dev/common-scripts/sort-curves.py *validationlogrankloss.dat | perl -ne "chop; print \"\\t'\$_' with l lw 3, \\\\\\n\""  | perl -e '$str = ""; while(<>){ $str .= $_; } $str =~ s/, \\$//s; print $str' >> graphs-validationlogrankloss.gp

gnuplot graphs-trainerror.gp
gnuplot graphs-trainloss.gp
gnuplot graphs-validationlogrankloss.gp
ps2pdf graphs-trainerror.ps
ps2pdf graphs-trainloss.ps
ps2pdf graphs-validationlogrankloss.ps
cp *pdf ~/public_html/priv ; chmod a+r ~/public_html/priv/*pdf
#scp  *pdf turian@joyeux.iro.umontreal.ca:public_html/priv/
