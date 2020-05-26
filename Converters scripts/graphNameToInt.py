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
            data.append(line)

# convert Named graph to INT graph
def convertToInt(file, data):
    with open(file) as x, open('input/graphINT.txt', 'w') as outfile:
        infile = [line for line in x.readlines()]
        composedFile = ''
        for l in infile:
            line = l.split()
            line0, line1 = line[0], line[1]
            for row in data:
                if row[1] == line0 or row[1] == line1:
                    if row[1] == line0:
                        line0 = line[0].replace(row[1], str(row[0]))
                    if row[1] == line1:
                        line1 = line[1].replace(row[1], str(row[0]))
                    if line0.isdigit() == True and line1.isdigit() == True:
                        composedFile += line0 + ' ' + line1 + ' ' + str(int(float(line[2])*10)) + '\n'
        
        outfile.write(composedFile.rstrip())

def main(argv):
    csvToList(argv[2])
    convertToInt(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
