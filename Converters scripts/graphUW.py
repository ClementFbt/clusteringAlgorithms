import sys

# Convert graph to an unweighted one
def removeWeight(graph, output):
    with open(graph) as x, open(output, 'w') as outfile:
        infile = [line.split() for line in x.readlines()]
        composedFile = ''
        for line in infile:
            del(line[2])
            outputLine = ''
            for elem in line:
                outputLine += elem + ' '
            composedFile += outputLine.rstrip() + '\n'
        outfile.write(composedFile.rstrip())
                
def main(argv):
    if len(argv) == 1:
        print('remove weight on the graph. Arguments : \n')
        print('[1] output\n')
        print('[2] graph \n')
        print('python graphUW.py input/graphUW.txt input/graphINT.txt \n')
    else:
        removeWeight(argv[2], argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
