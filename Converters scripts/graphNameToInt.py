import sys

data = []

def createListId(classList):
    with open(classList) as f:
        infile = [line.split() for line in f.readlines()]
        for line in infile:
            line[0] = int(line[0])
            data.append(line)

# convert Named graph to INT graph
def convertNameToInt(graphNamed, output):
    with open(graphNamed) as x, open(output, 'w') as outfile:
        infile = [line.split() for line in x.readlines()]
        composedFile = ''
        for line in infile:
            line0, line1 = line[0], line[1]
            for row in data:
                if (row[1] == line0) or (row[1] == line1):
                    if row[1] == line0:
                        line0 = line[0].replace(row[1], str(row[0]))
                    if row[1] == line1:
                        line1 = line[1].replace(row[1], str(row[0]))
                    if (line0.isdigit() == True) and (line1.isdigit() == True):
                        composedFile += line0 + ' ' + line1
                        if len(line) == 3:
                            composedFile += ' ' + str(int(float(line[2])*10)) + '\n'
                        else:
                            composedFile += '\n'
        outfile.write(composedFile.rstrip())

def main(argv):
    if len(argv) == 1:
        print('remove weight on the graph. Arguments : \n')
        print('[1] output\n')
        print('[2] graph with their name \n')
        print('[3] list of class with their id \n')
        print('python graphUW.py input/graphINT.txt input/graphNamed.txt input/classList.txt \n')
    else:
        createListId(argv[3])
        convertNameToInt(argv[2], argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
