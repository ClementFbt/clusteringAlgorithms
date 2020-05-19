#!/usr/bin/env python3
import sys
import ast
import json
import getpass
import os
import fileinput
import re

#create one file per clusterd graph
def convertSpecInput(file):
    out = []
    with open(file) as x, open('graphNeighboorUW.txt', 'w') as outfile:
        infile = [line for line in x.readlines()]
        outline = ''
        edges = 0
        nodeList = []
        for l in infile:
            line = l.split()                
            # concat edges neibourhood to specific node
            if outline.split() and int(line[0]) != int(outline.split()[0]):
                out.append(outline)
                outline = ' ' + line[0]
            elif not outline.split():
                outline += ' ' + line[0]
            outline += ' ' + line[1]
            #count number of nodes
            if int(line[0]) not in nodeList:
                nodeList.append(int(line[0]))
            if int(line[1]) not in nodeList:
                nodeList.append(int(line[1]))
            #count number of edges
            edges += 1
            
        output = str(len(nodeList)) + ' ' + str(edges) + '\n'
        for line in out:
            output += line + '\n'
        outfile.write(output.rstrip())


def main(argv):
    convertSpecInput(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
