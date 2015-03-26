# ShellRaptor


A command-line SQL Utility; Supports any JDBC compliant database.
 
ShellRaptor: A command-line SQL Utility; Supports any JDBC compliant database.
Release 12.07.05 http://shellraptor.sourceforge.net

One new change in this release: now includes the ability to load both .jar and .zip
library extensions.

There is a new feature included in this release that allows for the loading, creation, 
execution and saving of PreparedStatements.

These work with by using the following commands (also available with help;)
load-ps	load-ps <file> Load an XML Prepared Statement File
init-ps	init-ps <name> <prepared statement> Create the prepared statement
load-ps	list-ps list the alias names of all available prepared statements that are available
exec-ps exec-ps <alias> <args1>, <args2>, <args...n> Execute the prepared Statement and use the given values
save-ps	save-ps <output file> Saves the prepared statements in memory -in xml format-

***** Default Mode ***** 
edit conf/stdout.xml Set the parameters how you want them (explanation of files
is listed farther down)
java -jar shellraptor.jar --stdout
to config an use differant files, use 
java -jar shellraptor.jar --stdout=./conf/myShell.xml


**************** HELP COMMANDS ****************
	help			Available commands
	open <alias> <password>	Open a new connection based on 
                                     dbconfig entry. Password is optional
	list-alias			Show the list of available aliases in the 
                                     dbconfig file
	list-tables		List database tables
	list-dbinfo		List database information
	list-index <table>		Explain the index plan for the supplied 
                                     table
	-silent			Silent mode, no result sets returned
	-verbose			Verbose mode, result sets returned
	desc <table>		Describe the table schema
	select ...			basic sql commands
	drop <table>		Drop the given table
	create <table...>		Create the given table
	@<file>			Open and execute the listed file
	#<file> <sql>		save sql results to...
	#<file.out> @<file.in>	[reversable] Read in and write to..
	set maxrowcount <#>	Set the max @ of return results 
                                     (must be supported by 
                                     your supplied driver)
	exit			Exit the Application

	
ShellRaptor (2005): Command Line SQL Utility
Base Arguements
	 -s	<saveOutputTo_File>

	 -mx	<maximumRowCountResults>
	 -test	(-only- test connectivity)
	 -h	(this help screen)
	 --stdout	Use a config file (default is conf/xml) to execute command 
                                     from the command line to stdout
	 --stdout=<input.File>	Provide a config file to 
                                     executeConnection Arguements
1) no arguements uses default config file conf\dbconf.xml
2) External Config File
	-c	<config file>
3) Command Line Values
	-d	<driver>
	-u	<connection-url>

	-l	<username>
	-p	<password
 *** COMMENTS ***
 It is possible to use a config and override the values
 of that file by supplying driver, url..etc values.
 For example;
 java -jar ShellRaptor.jar -c myconf.xml -d newest.Version.ofSomeDriver.jar

 
Dev Notes: 
The new stdout command set presently only supports select.  I would like to 
add the delete, update and create functionality at some point but that will
have to wait for now. My work load is beyond imagination. 
alias			Name of the database alias in the 
                                     conf/dbconf.xml file
password 		Supply a password if one is not provided in the 
                                     conf/dbconf.xml file
outputfile  		Path and file to save output to. Setting this tag 
                                     as 'stdout' will
			pipe the output directly to the command line
delimiter			Delimiter used to separate the results. -NO- 
                                     default is provided
with-column-names	[true/false] Display column headers. -NO- 
                                     default is provided
silent			[true/false] Display connection, logging information. Used when the
			application is being called by external resources 
                                     and output needs
			to be piped to another file
 
StdOut Example File
<raptor>
        <stdout>
          <alias>default</alias>

          <password>shellraptor</password>
          <outputfile>x:/tmp/dc_test_out.csv</outputfile>
<!-- to dump to the command line only, set your outputfile = stdout
          <outputfile>stdout</outputfile>
-->

          <delimeter>^</delimeter>
          <sql>select * from ATBAT</sql>
<!-- pipe out columns name true/false -->
          <with-column-names>true</with-column-names>

<!-- Show -ONLY- results...not details true/false -->
          <silent>true</silent>
        </stdout>
</raptor>


