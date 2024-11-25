# generates turtle documents according to the specification
import sys
from grammar_graph import gg

from isla.language import parse_bnf
from isla.solver import (
    ISLaSolver,
    CostWeightVector,
    GrammarBasedBlackboxCostComputer,
    CostSettings,
)

if (len(sys.argv) != 2):
    print("ERROR: Please provide a file to safe the output to.")
    sys.exit()

output_file = sys.argv[1]


# grammar
grammar_file = "functional_owl_el.bnf"
#print("using grammar:", grammar_file)
with open(grammar_file, 'r') as file:
	grammar = file.read()
	
# constraints
constraint_file = "functional_owl_el_new.isla"
with open(constraint_file, 'r') as file:
	    constraint = file.read()
	

cost_vector = CostWeightVector(
    tree_closing_cost=1,
    constraint_cost=1,
    derivation_depth_penalty=0,
    low_k_coverage_penalty=2,
    low_global_k_path_coverage_penalty=0,
)

solver = ISLaSolver(
    grammar,
    constraint,
    cost_computer=GrammarBasedBlackboxCostComputer(
        CostSettings(cost_vector, k=3),
        gg.GrammarGraph.from_grammar(parse_bnf(grammar)),
    )
)

print("used script: generator_OWL_EL.py")
print("used grammar: " + grammar_file)
print("used constraints: " + constraint_file)



ontology = ""

for i in range(1):
    ontology += str(solver.solve())

#print("Write output to file ", output_file)
with open(output_file, 'w') as file:
     file.writelines(ontology)

