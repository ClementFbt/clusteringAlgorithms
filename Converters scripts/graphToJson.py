import sys
import json

data = {"nodes": [], "links": []}

# Add nodes and all parameters
def createNodes(classList, classType, classPath):
    baseUrl = "https://github.com/adempiere/adempiere/tree/develop/base/src/"
    baseDir = ["cm", "esb", "impexp", "intf", "model", "process", "report", "sla", "tools", "util", "wf"]
    adUrl = "https://github.com/adempiere/adempiere/tree/develop/ad/src/"
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
                                "url": (baseUrl if linePath[2] in baseDir else adUrl) + '/'.join(linePath)
                            })
                            del(lineType)
                            del(linePath)

# Add links
def createLinks(graphINT):
    with open(graphINT) as x:
        infile = [line.split() for line in x.readlines()]
        for line in infile:
            data["links"].append({"source": line[0], "target": line[1], "value": int(float(line[2])*10)})

def convertToJson(output):
        with open(output, 'w') as outfile:
            json.dump(data, outfile)


def main(argv):
    if len(argv) == 1:
        print('Create json file for 3d-force-graph. Arguments : \n')
        print('[1] output\n')
        print('[2] graph with id \n')
        print('[3] list of nodes with their id \n')
        print('[4] list of node with their type (class, interface) \n')
        print('[5] Path of each class \n')
        print('python graphToJson.py input/graph.json input/graphINT.txt input/classList.txt input/classType.txt input/classpath.txt \n')
    else:
        createLinks(argv[2])
        createNodes(argv[3], argv[4], argv[5])
        convertToJson(argv[1])


if __name__ == "__main__":
    sys.exit(main(sys.argv))
