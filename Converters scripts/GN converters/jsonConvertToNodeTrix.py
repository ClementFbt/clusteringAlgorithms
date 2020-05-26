#!/usr/bin/env python3
import sys
import ast
import json
import getpass
import os

data = {"nodes": [], "links": []}

#open clustred Graph json input file
def openjson(file):
    with open(file) as f:
        return json.load(f)

#open graph.txt file
def openfile(file):
    f = open(file, 'r')
    x = [line for line in f.readlines()]
    return x

#alter nodes to desired output
def alterNodes(dict, graph):
    listIndex = []
    graphNum = graph
    for cluster in dict:
        for value in dict[cluster]:
            data["nodes"].append({"name": value, "group": cluster})
            listIndex.append(value)
    for line in graphNum:
        item = line.split(',')
        data["links"].append({"source": listIndex.index(item[0]),
                              "target": listIndex.index(item[1]),
                              "value": float(item[2])})


def main(argv):
    path = os.path.abspath(os.getcwd())
    dict = openjson(argv[1])
    graph = openfile(argv[2])
    print(graph)
    alterNodes(dict, graph)
    path_file = os.path.join(path, "ClustersNodeTrix.json")
    with open(path_file, 'w') as outfile:
        json.dump(data, outfile)


if __name__ == "__main__":
    sys.exit(main(sys.argv))
