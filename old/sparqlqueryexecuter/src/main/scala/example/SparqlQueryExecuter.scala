package example

import java.io.FileOutputStream
import better.files._
import better.files.File._
import better.files.Dsl.SymbolicOperations
import org.apache.jena.query.ParameterizedSparqlString
import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.{RDFDataMgr, RDFFormat}
import org.apache.jena.sparql.engine.http.QueryEngineHTTP


object SparqlQueryExecuter {

  case class ArgsClass(limit : Long, outDir : File, prefix : String, endpoint : String, query : File)

  val usage = "Usage: SparqlQueryExecuter [--outdir <path/to/output/directory>] [--limit <page size>] " +
    "[--endpoint <URI>] [--prefix <outfileprefix>] <path/to/queryFile.rq>"

  def main(args : Array[String]) {

    parseCommandLineArguments(args.toList) match {

      case None => println(usage)
      case Some(a) => executeQuery(a)
    }
  }

  def executeQuery(args : ArgsClass) = {

    def nextTriples(offset : Long, pQuery : ParameterizedSparqlString) : Unit = {

      pQuery.setLiteral("offset", offset)

      val httpQuery = new QueryEngineHTTP(args.endpoint, pQuery.asQuery)

      val model : Model = httpQuery.execConstruct()

      httpQuery.close()

      if (!model.isEmpty) {

        RDFDataMgr.write(new FileOutputStream((args.outDir / args.prefix /
          s"${args.prefix}_${offset}.nt").toJava), model, RDFFormat.NTRIPLES_UTF8)

        nextTriples(offset + args.limit, pQuery)
      }
    }

    (args.outDir / args.prefix).createIfNotExists(true, true) //asDirectory = true, createParents = true

    val parameterString = s"${args.query.contentAsString}\nLIMIT ?limit\nOFFSET ?offset"

    val pQuery = new ParameterizedSparqlString(parameterString)
    pQuery.setLiteral("limit", args.limit)

    nextTriples(0, pQuery)

    val singleFiles = (args.outDir / args.prefix).globRegex(raw"""${args.prefix}_[0-9]+\.nt""".r)

    singleFiles.foreach { file => (args.outDir / s"${args.prefix}.nt") << file.contentAsString }
  }

  def parseCommandLineArguments(argList : List[String]) : Option[ArgsClass] = {

    val defaultArgs = ArgsClass(10000, home / "out", "p", "http://factforge.net/repositories/ff-news", null)

    def nextOption(args : ArgsClass, list: List[String]) : Option[ArgsClass] = {

      def checkQueryFile(query : String) : Option[File] = {

        val targetFile = File(query)

        if (targetFile.isRegularFile) Some(targetFile)
        else {
          println(s"The given queryFile ${query} is not a regular file.")
          None
        }
      }

      list match {

        case Nil => Some(args)

        case "--queryFile" :: value :: tail => checkQueryFile(value).fold { val tmp : Option[ArgsClass] = None; tmp }
          {file => nextOption(args.copy(query = file), tail)}

        case "--limit" :: value :: tail => nextOption(args.copy(limit = value.toInt.max(1)), tail)

        case "--endpoint" :: value :: tail => nextOption(args.copy(endpoint = value.toString), tail)

        case "--outDir" :: value :: tail => nextOption(args.copy(outDir = File(value)), tail)

        case "--prefix" :: value :: tail => nextOption(args.copy(prefix = value.toString), tail)

        case value :: opt2 :: _ if (opt2(0) == '-') => checkQueryFile(value).fold { val t : Option[ArgsClass] = None; t }
          {file => nextOption(args.copy(query = file), list.tail)}

        case value :: Nil => checkQueryFile(value).fold { val tmp : Option[ArgsClass] = None; tmp }
          {file => nextOption(args.copy(query = file), list.tail)}

        case option :: _ => { println(s"Unknown option: ${option}\n"); None }
      }
    }

    nextOption(defaultArgs, argList).fold { val tmp : Option[ArgsClass] = None; tmp }
    {args => if (args.query == null) None else Some(args)}
  }
}