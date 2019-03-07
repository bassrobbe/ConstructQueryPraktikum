# SparlQueryExecuter
A light-weight tool to fuse complementary facts to DBpedia identifiers


Usage: 

Under the directory created from downloading/cloning, use the following format to run it  

``$ sbt "run [--limit <page size>] [--outdir <path/to/output/directory>] [--prefix <outfileprefix>] --endpoint <URI> <path/to/queryFile.rq>``
 
 where the arguments are:
 	
 + ``endpoint`` 	URI to the SPARQL endpoint  
 + ``limit``     Required page size; defaults to 10000, actual page size should be checked against the SPARQL endpoint.
 + ``outdir``    Directory where output files are written, defaults to _out_ under the user home directory
 + ``prefix``	Prefix for the output files. Each file will additionally have a number which corresponds to the offset of records from the SPARQL endpoint; defaults to _p_	.