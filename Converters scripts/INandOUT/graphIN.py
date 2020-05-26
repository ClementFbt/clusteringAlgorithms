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
            data.append([line[1]])


#create one file per clusterd graph
def convertSpecInput(file):
    with open(file) as x, open('nodesIN.txt', 'w') as outfile:
        infile = [line for line in x.readlines()]
        for d in data:
            for l in infile:
                line = l.split()
                if d[0] == line[1]:
                    d.append(line[0])

            
        output = ''
        for line in data:
            outputLine = line[0] + ':'
            del(line[0])
            for elem in line:
                outputLine += elem + ' '
            output += outputLine.rstrip() + '\n'
        outfile.write(output.rstrip())


def main(argv):
    csvToList(argv[2])
    convertSpecInput(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
