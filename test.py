from isla.repair_solver import RepairSolver

bnffile = open("rdf.bnf", "r")
bnf = bnffile.read()
islafile = open("rdf.isla", "r")
isla = islafile.read()

solver = RepairSolver(bnf, isla)
for _ in range(10):
    print(solver.solve())