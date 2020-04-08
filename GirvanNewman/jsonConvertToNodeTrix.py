#!/usr/bin/env python3
import sys
import ast
import json
import getpass
import os

data = {"nodes": [], "links": []}


def openjson(file):
    with open(file) as f:
        return json.load(f)


def openfile(file):
    f = open(file, 'r')
    x = [line for line in f.readlines()]
    return x


def alterJson(dict, graph):
    for cluster in dict:
        for value in dict[cluster]:
            data["nodes"].append({"name": value, "group": cluster})
    return data


def main(argv):
    path = os.path.abspath(os.getcwd())
    dict = openjfile(argv[1])
    graph = openfile(argv[2])
    print(graph)
    data = alterJson(dict, graph)
    path_file = os.path.join(path, "ClustersNodeTrix.json")
    with open(path_file, 'w') as outfile:
        json.dump(data, outfile)


if __name__ == "__main__":
    sys.exit(main(sys.argv))
