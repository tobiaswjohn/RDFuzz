
import sys
from grammar_graph import gg

from isla.language import parse_bnf
from isla.solver import (
    ISLaSolver,
    CostWeightVector,
    GrammarBasedBlackboxCostComputer,
    CostSettings,
)

no_contraints = False
if (len(sys.argv) == 2):
    no_contraints = True
elif (len(sys.argv) != 3):
    print("ERROR: Please provide a grammar file and optionally a constraint file as arguments.")
    sys.exit()
	
# read grammar
grammar_file = sys.argv[1]
print("using grammar:", grammar_file)
with open(grammar_file, 'r') as file:
	grammar = file.read()

# read contraints (if provided)
if (not(no_contraints)):
    constraint_file = sys.argv[2]   
    print("using constraints:", constraint_file)
    with open(constraint_file, 'r') as file:
	    constraint = file.read()





cost_vector = CostWeightVector(
    tree_closing_cost=-100,
    constraint_cost=0,
    derivation_depth_penalty=0,
    low_k_coverage_penalty=20,
    low_global_k_path_coverage_penalty=0,
)

if (no_contraints):
    solver = ISLaSolver(
        grammar,
        cost_computer=GrammarBasedBlackboxCostComputer(
            CostSettings(cost_vector, k=3),
            gg.GrammarGraph.from_grammar(parse_bnf(grammar)),
        ),
    )
else:
    solver = ISLaSolver(
        grammar,
        constraint,
        cost_computer=GrammarBasedBlackboxCostComputer(
            CostSettings(cost_vector, k=3),
            gg.GrammarGraph.from_grammar(parse_bnf(grammar)),
        ),
    )
for i in range(10):
    print(solver.solve())
