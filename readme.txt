You just downloaded JDraw for JDK1.4, know everything about
Open Source Software, Source Forge and the GNU General
Public Licence which protects JDraw.

So now you should have some of the following files:

jdraw_jdk1.4.jar:
	This is a prebuilt jar file. To start JDraw just type:
	"java -jar jdraw_jdk1.4.jar" at your shell's prompt
	or - in Windows XP - double click the file in the
	Explorer.
	
jdraw_jdk1.4.src.zip:
	This is JDraw's source bundle. It contains all  
	Java classes, images and an HTML help file.
	To help you building your own JDraw archive, the
	following two files are included as well.

build.xml:
  This is a build file for Ant from Jacarta. Running ant in 
  this file's directory will build all necessary class files, 
  create a new jdraw.jar file from them and finally start
  JDraw.

manifest:
	This is a simple manifest file for your new jdraw jar
	archive that tells Java about JDraw's main class.
	

NOTE: Please avoid using ugly file and directory names 
with blanks and other evil characters in them. Running 
JDraw from its jar archive only works with Unix style 
file names.

	
We hope you enjoy working with JDraw. Let us know about
bugs and ideas for new features...


J-Domain, December 2003
EMail: jdomain@users.sourceforge.net
	
