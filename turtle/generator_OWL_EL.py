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

grammar_file = "functional_owl_el.bnf"
#print("using grammar:", grammar_file)
with open(grammar_file, 'r') as file:
	grammar = file.read()

cost_vector = CostWeightVector(
    tree_closing_cost=0,
    constraint_cost=1,
    derivation_depth_penalty=1,
    low_k_coverage_penalty=100,
    low_global_k_path_coverage_penalty=0,
)

solver = ISLaSolver(
    grammar,
    cost_computer=GrammarBasedBlackboxCostComputer(
        CostSettings(cost_vector, k=3),
        gg.GrammarGraph.from_grammar(parse_bnf(grammar)),
    ),
)




# idea: generate 10 grammars, concacenate them + add prefix declaration
ontology = ""



for i in range(1):
    ontology += str(solver.solve())

#print("Write output to file ", output_file)
with open(output_file, 'w') as file:
     file.writelines(ontology)

