#!/bin/bash

# usage
# ./exe.sh ../sparqlQueries/geonames ./sparqlqueryexecuter/ ./out/ "http://factforge.net/repositories/ff-news"


# check the existence of input arguments
if [ $# -eq 0 ]; then
  echo "Usage: $ ./exe.sh <queryDir> <projectDir> <outDir> <endpointUrl>"
  echo "queryDir: directory containing the query files"
  echo "projectDir: directory containing the SparqlQueryExecuter-Project"
  echo "outDir: output directory where the evaluation results will be stored"
  echo "endpointURL: URL of the SPARQL endpoint"

else
  queryDir=$1
  projectDir=$2
  outDir=$3
  endpoint=$4

  queryDirAbs=$(cd $queryDir && pwd)

  for q in $queryDirAbs/*.rq ; do
    echo $q
    prefix=$(basename $q .rq)
    echo $prefix
    (cd $projectDir && exec sbt "run --outDir ${outDir} --prefix ${prefix} --endpoint ${endpoint} $q")
  done
fi