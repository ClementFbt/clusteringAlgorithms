#!/usr/bin/env python3
import sys
import ast
import json
import getpass
import os
import fileinput
import re

data = []
def csvToList(csv):
    with open(csv) as f:
        infile = [line for line in f.readlines()]
        for l in infile:
            line = l.split()
            line[0] = int(line[0])
            data.append([line[0],line[1]])

# create one file per clusterd graph
def convertToInt(file, data):
    with open(file) as x, open('input/graphNeighboorToName.txt', 'w') as outfile:
        infile = [line for line in x.readlines()]
        output = ''
        for l in infile:
            if '#' not in l:
                line = l.split()
                outputLine = ''
                for elem in line:
                    for row in data:
                        if row[0] == int(elem):
                            outputLine += row[1] + ' '
                output += outputLine.rstrip() + '\n'
        print(output)
        outfile.write(output.rstrip())


def main(argv):
    csvToList(argv[2])
    convertToInt(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
