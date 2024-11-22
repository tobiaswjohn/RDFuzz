from rdflib import Graph
import os.path
import sys


rdfile = sys.argv[1]
if not os.path.exists(rdfile):
    print("File "+rdfile+"dots not exist!")
    exit

g = Graph()
g.parse(rdfile,format='ttl')

print(f"File {rdfile} with {len(g)} statements loaded")


sparfile = sys.argv[2]
if not os.path.exists(sparfile):
    print("File "+sparfile+"dots not exist!")
    exit

with open(sparfile, 'r') as file:
    q = file.read()

print(f"File {rdfile} with query loaded")

res = bool(g.query(q))

print(f"Result: {res}")
