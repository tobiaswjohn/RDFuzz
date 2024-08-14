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

grammar_file = "rdf_generator.bnf"
#print("using grammar:", grammar_file)
with open(grammar_file, 'r') as file:
	grammar = file.read()

cost_vector = CostWeightVector(
    tree_closing_cost=5,
    constraint_cost=0,
    derivation_depth_penalty=1,
    low_k_coverage_penalty=-3,
    low_global_k_path_coverage_penalty=7,
)

solver = ISLaSolver(
    grammar,
    cost_computer=GrammarBasedBlackboxCostComputer(
        CostSettings(cost_vector, k=3),
        gg.GrammarGraph.from_grammar(parse_bnf(grammar)),
    ),
)




# idea: generate 5 grammars, concacenate them + add prefix declaration
turtle = ""

# add "base" declaration
turtle += "@base <k> .\n"

# add "prefix" declarations
turtle += "@prefix p2: <n> .\n@prefix p1: <m> .\n"


for i in range(5):
    turtle += str(solver.solve())

#print("Write output to file ", output_file)
with open(output_file, 'w') as file:
     file.writelines(turtle)

