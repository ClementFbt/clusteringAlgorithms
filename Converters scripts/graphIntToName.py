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
            line[1] = line[1].rstrip()
            data.append(line)

# convert output of a graph with Int values to Graph with labels
def convertToInt(file, data):
    with open(file) as x, open('input/graphIntToName.txt', 'w') as outfile:
        infile = [line for line in x.readlines()]
        composedFile = ''
        for l in infile:
            if '#' not in l:
                line = l.split()
                line0 = int(line[0])
                for row in data:
                    if row[0] == line0:
                        composedFile += row[1] + ' ' + line[1] + '\n'
        outfile.write(composedFile.rstrip())


def main(argv):
    csvToList(argv[2])
    convertToInt(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
