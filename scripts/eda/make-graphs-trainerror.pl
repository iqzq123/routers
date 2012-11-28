#!/usr/bin/perl -w
#
#  Make a .dat file for each .out file.
#

$gnuplot = "plot";
$first = 1;
#foreach $f (split(/[\r\n]+/, `ls [0-9]*out`)) {
foreach $f (split(/[\r\n]+/, `ls ../run*/log.* | grep -v 'dat\$'`)) {
    next if $f =~ m/\.dat$/;
    ($badf = $f) =~ s/\/[^\/]*$/\/BAD/;
    next if -e $badf;
    ($fnew = $f) =~ s/$/-trainerror.dat/;
    die $! if $fnew eq $f;
    print STDERR "$f => $fnew\n";
    $cmd = "cat $f | grep --text 'pre-update train err' | perl -ne 's/=/ /g; print' | cut -d ' ' -f 2,10 | grep -v '[a-zA-Z]' | grep '0000 ' > $fnew";
    print STDERR "$cmd\n";
    system($cmd);
    $gnuplot .= "," unless $first;
    $first = 0;
    $gnuplot .= " \\\n\t'$fnew' with lp"
}
print "$gnuplot\n";
