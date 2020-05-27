#!/usr/bin/env python3
import sys
import json
import getpass
import re
from random import randrange

data = {"nodes": [], "links": []}

def csvToList(classList, classType, classPath):
    baseUrl = "https://github.com/adempiere/adempiere/tree/develop/base/src/"
    with open(classList) as f, open(classType) as g, open(classPath) as h:
        inList = [line.split() for line in f.readlines()]
        inType = [line.split() for line in g.readlines()]
        inPath = [line.split(".") for line in h.readlines()]
        for lineList in inList:
            for lineType in inType:
                if lineList[1] == lineType[0]:
                    for linePath in inPath:
                        linePath[-1] = linePath[-1].rstrip()
                        if lineList[1] == linePath[-1]:
                            data["nodes"].append({
                                "id": lineList[0],
                                "name": lineList[1],
                                "type": lineType[1],
                                "url": baseUrl + '/'.join(linePath)
                            })
                            del(lineType)
                            del(linePath)

# Convert .txt graph to json graph and add parameters
def convertToInt(file):
    with open(file) as x:
        infile = [line for line in x.readlines()]
        for l in infile:
            line = l.split()
            #TODO add url
            data["links"].append({"source": line[0], "target": line[1], "value": int(float(line[2])*10)})
    with open('input/graph.json', 'w') as outfile:
        json.dump(data, outfile)


def main(argv):
    csvToList(argv[2], argv[3], argv[4])
    convertToInt(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
