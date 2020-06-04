$.getJSON("graph.json", function (data) {
    const jsonObj = data; //json output
    let selectedNodes = new Set();
    let listNode = []

    const Graph = ForceGraph3D()(document.getElementById("3d-graph"))
        .graphData(jsonObj)
        .nodeLabel("name")
        .linkDirectionalArrowLength(3)
        .linkDirectionalArrowRelPos(1)
        .nodeOpacity(0.5)
        .linkOpacity(0.1)
        .nodeColor((node) =>
            selectedNodes.has(node) ? "yellow" : node.type == "CLASS" ? "grey" : "blue"
        )
        .onNodeClick((node, event) => {
            if (event.shiftKey && event.ctrlKey) {
                //add selected node and his neighboors to the selection
                selectedNodes.has(node)
                    ? (node.neighbors.forEach((neighborNode) =>
                        selectedNodes.delete(neighborNode)
                    ),
                        selectedNodes.delete(node))
                    : (node.neighbors.forEach((neighborNode) =>
                        selectedNodes.add(neighborNode)
                    ),
                        selectedNodes.add(node));
            } else if (event.ctrlKey) {
                //add one node to the selection
                selectedNodes.has(node)
                    ? selectedNodes.delete(node)
                    : selectedNodes.add(node);
            } else if (event.shiftKey) {
                const untoggle =
                    selectedNodes.has(node) && selectedNodes.size === 1;
                selectedNodes.clear();
                !untoggle
                    && selectedNodes.add(node)
                    && node.neighbors.forEach((neighborNode) => selectedNodes.add(neighborNode));
            } else if (event.altKey) {
                // single-selection
                const untoggle =
                    selectedNodes.has(node) && selectedNodes.size === 1;
                selectedNodes.clear();
                !untoggle && selectedNodes.add(node);
            } else {
                window.open(node.url, "_blank");
            }
            Graph.nodeColor(Graph.nodeColor()); // update color of selected nodes
        })
        .onNodeDrag((node, translate) => {
            if (selectedNodes.has(node)) {
                // moving a selected node
                [...selectedNodes]
                    .filter((selNode) => selNode !== node) // don't touch node being dragged
                    .forEach((node) =>
                        ["x", "y", "z"].forEach(
                            (coord) =>
                                (node[`f${coord}`] = node[coord] + translate[coord])
                        )
                    ); // translate other nodes by same amount
            }
        })
        .onNodeDragEnd((node) => {
            if (selectedNodes.has(node)) {
                // finished moving a selected node
                [...selectedNodes].forEach((node) =>
                    ["x", "y", "z"].forEach(
                        (coord) => (node[`f${coord}`] = node[coord])
                    )
                ); // fix all nodes
            }
        });

    jsonObj.links.forEach(link => {
        const a = jsonObj.nodes[link.source];
        const b = jsonObj.nodes[link.target];
        !a.neighbors && (a.neighbors = []);
        !b.neighbors && (b.neighbors = []);
        a.neighbors.push(b);
        b.neighbors.push(a);

        !a.links && (a.links = []);
        !b.links && (b.links = []);
        a.links.push(link);
        b.links.push(link);
    });

    jsonObj.nodes.forEach(elem => {
        listNode.push(elem.name)
    })

    for (var i in listNode.sort()) {
        var li = `<li class="list-group-item">`;
        $("ul").append(li.concat(listNode[i]))
    }

    $("ul").on("click", "li.list-group-item", function () {
        selectedNodes = new Set;
        jsonObj.nodes.forEach(node => {
            if (node.name == $(this).text()) {
                node.neighbors.forEach((neighborNode) =>
                    selectedNodes.add(neighborNode)
                );
                selectedNodes.add(node);
                Graph.nodeColor((node) => selectedNodes.has(node)
                    ? "yellow" : node.type == "CLASS" ? "grey" : "blue")
                return true;
            }
        })
    });

    //Define Link distance
    const linkForce = Graph.d3Force("link").distance((link) =>
        link.id == true ? settings.linkDistance : null
    );

    //Define GUI
    const Settings = function () {
        this.linkDistance = 0;
    };
    const settings = new Settings();
    const gui = new dat.GUI();
    const controller = gui.add(settings, "linkDistance", 0, 100);
    controller.onChange(updateLinkDistance);

    function updateLinkDistance() {
        linkForce.distance(settings.linkDistance);
        Graph.numDimensions(3); // Re-heat simulation
    }



    // Spread nodes a little wider
    Graph.d3Force("charge").strength(-130);
});