#!/usr/bin/env python3
import sys
import ast
import json
import getpass
import os

#open output of Girvan Newman script
def openfile(file):
    f = open(file, 'r')
    x = [line for line in f.readlines()]
    x = x[0]
    x = x[1:]
    x = x[:-1]
    x = x.replace(')', '').split('],[')
    return x

#create one file per clusterd graph
def cluster_creation(path, itr_list):
    for cluster in itr_list:
        data = {}
        res = cluster.replace('set(', '')
        _tmp_cluster = '[' + res + ']'
        cluster_list = eval(_tmp_cluster)
        itr = 0
        for nodes in cluster_list:
            itr += 1
            data[itr] = nodes
        json_file(path, data, itr)

#converte dictionary to json
def json_file(path, data, itr):
    json_file_name = "clusterNodes_{0}.json".format(itr)
    path_file = os.path.join(path, json_file_name)
    with open(path_file, 'w') as outfile:
        json.dump(data, outfile)


def main(argv):
    currentPath = os.path.abspath(os.getcwd())
    path = os.path.join(currentPath, 'Clusters\\')
    if not os.path.exists(path):
        os.makedirs(path)
    cluster_creation(path, openfile(argv[1]))


if __name__ == "__main__":
    sys.exit(main(sys.argv))