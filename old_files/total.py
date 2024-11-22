from rdflib import Graph
import os.path
import sys
import os



rdfile = sys.argv[1]
outfile = sys.argv[2]
sparfile = sys.argv[3]

print("running isla ...")

# generate new input
os.system(f"isla -O solve usc.bnf usc.isla > {rdfile}")

print("isla finished ...")

if not os.path.exists(rdfile):
    print("Writing input RDF into "+rdfile+" failed")
    exit


# run the pipeline
os.system(f"python3 /home/edkam/src/ocp2kg/src/evol_kg.py -c {rdfile} -m /home/edkam/src/ocp2kg/mappings/AUT_full_mapping.rml.ttl -o /home/edkam/src/ocp2kg/epo-ontology/ePO_3.1.ttl -n {outfile}")


if not os.path.exists(outfile):
    print("Writing input RDF into "+outfile+" failed")
    exit
g = Graph()
g.parse(outfile,format='ttl')
print(f"File {outfile} with {len(g)} triples loaded")

# run the query
if not os.path.exists(sparfile):
    print("File "+sparfile+"dots not exist!")
    exit

with open(sparfile, 'r') as file:
    q = file.read()

print(f"File {sparfile} with query loaded")

res = bool(g.query(q))

print(f"Result: {res}")
