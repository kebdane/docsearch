find technical/biomed/*.txt > find-results.txt
grep "base pair" find-results.txt > grep-results.txt
wc grep-results.txt