
from os.path import basename, splitext, exists
from os import makedirs
from SPARQLWrapper import SPARQLWrapper

def nextTriples(offset, q, prefix):

    # build final query string
    query = f'{q}\nLIMIT {limit}\nOFFSET {offset}'

    # execute query
    endpoint.setQuery(query)
    results = endpoint.query().convert().decode()
    
    if results: # if query returned any result
        outFile = f'{outDir}/{prefix}/{prefix}_{offset//limit}.nt'
        parts.append(outFile) # remember partial file during concatenation
        with open(outFile, 'w') as f:
            f.write(results)
        nextTriples(offset + limit, q, prefix) # next query with increased offset


# INPUT DATA

limit = 10000
initialOffset = 0
outDir = "./out"
endpointUrl = "http://factforge.net/repositories/ff-news"
queryFiles = [
    # "./sparqlQueries/geonames/gn-alternateName.rq",
    "./sparqlQueries/geonames/gn-officialName.rq",
    # "./sparqlQueries/geonames/gn-countryCode.rq",
    # "./sparqlQueries/geonames/gn-population.rq",
    # "./sparqlQueries/geonames/gn-posLat.rq",
    # "./sparqlQueries/geonames/gn-posLong.rq",
    # "./sparqlQueries/geonames/gn-postalCode.rq",
]

endpoint = SPARQLWrapper(endpointUrl)
endpoint.setReturnFormat('n3')

for qFile in queryFiles:

    parts = [] # initialize list of partial files
    basename = splitext(basename(qFile))[0]

    if not exists(f'{outDir}/{basename}'):
        makedirs(f'{outDir}/{basename}')

    # query execution
    with open(qFile, 'r') as query:
        nextTriples(initialOffset, query.read(), basename)

    # concatenation
    outFile = f'{outDir}/{basename}.nt'
    with open(outFile, 'w') as f:
        for pFile in parts:
            f.write(pFile.read())