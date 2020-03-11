Done by: Aaron Lim
Student ID: 5985171
I run on netbean, and transferred it to ubuntu

Ubuntu's command prompt build script:
javac Rainbow.txt

Ubuntu's command prompt run script:
java Rainbow fileName.txt

Reduction method:
1) Convert from hexdecimal to big integer
2) Check if the word is already in used
3a) If not used, it will proceed to return the int and change the .setUsed to true in main
3b) If used, double hash the value and check if it is used again, continue until it found a word that is not used
4) Do until all words are completed