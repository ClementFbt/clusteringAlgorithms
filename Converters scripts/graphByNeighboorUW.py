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
            data.append([int(line[0])])


#Each line is composed with each adjacent nodes without weight
#A line correspond to a node
def convertToNeighboorUW(file):
    with open(file) as x, open('input/graphNeighboorUW.txt', 'w') as outfile:
        infile = [line for line in x.readlines()]
        edges = 0
        nodeList = 0
        for d in data:
            for l in infile:
                line = l.split()
                if d[0] == int(line[0]):
                    d.append(int(line[1]))
                    nodeList += 1
                #Comment to have a directed graph
                elif d[0] == int(line[1]):
                    d.append(int(line[0]))
            #count number of edges
            edges += 1
            
        output = str(edges) + ' ' + str(nodeList) + ' 1\n'
        for line in data:
            outputLine = ''
            del(line[0])
            for elem in line:
                outputLine += str(elem) + ' '
            output += outputLine.rstrip() + '\n'
        outfile.write(output.rstrip())


def main(argv):
    csvToList(argv[2])
    convertToNeighboorUW(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
