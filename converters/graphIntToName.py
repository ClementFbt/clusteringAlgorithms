#!/usr/bin/env python3
import sys
import ast
import json
import getpass
import os
import fileinput
import re

def csvToList(csv):
    data = []
    with open(csv) as f:
        infile = [line for line in f.readlines()]
        for l in infile:
            line = l.split(';')
            line[0] = int(line[0])
            line[1] = line[1].rstrip()
            data.append(line)
        return data

# create one file per clusterd graph
def convertToInt(file, data):
    with open(file) as x, open('graphInfomap.txt', 'w') as outfile:
        infile = [line for line in x.readlines()]
        composedFile = '#nameNode | clusterId\n'
        for l in infile:
            if '#' not in l:
                line = l.split()
                line0 = int(line[0])
                for row in data:
                    if row[0] == line0:
                        composedFile += row[1] + ' ' + line[1] + '\n'
        outfile.write(composedFile.rstrip())


def main(argv):
    data = csvToList(argv[2])
    convertToInt(argv[1],data)


if __name__ == "__main__":
    sys.exit(main(sys.argv))
