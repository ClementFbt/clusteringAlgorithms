#!/usr/bin/env python3
import sys
import json
import getpass
import re
from random import randrange

data = {"nodes": [], "links": []}

def csvToList(csv):
    with open(csv) as f:
        infile = [line for line in f.readlines()]
        for l in infile:
            line = l.split()
            data["nodes"].append({"id": line[0], "name": line[1], "type": randrange(2)})

# Convert .txt graph to json graph and add parameters
def convertToInt(file):
    with open(file) as x:
        infile = [line for line in x.readlines()]
        for l in infile:
            line = l.split()
            #TODO add url
            data["links"].append({"source": line[0], "target": line[1], "value": int(float(line[2])), "url": "TODO"})
    with open('input/graph.json', 'w') as outfile:
        json.dump(data, outfile)


def main(argv):
    csvToList(argv[2])
    convertToInt(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
