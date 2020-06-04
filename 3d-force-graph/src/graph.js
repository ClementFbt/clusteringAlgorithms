$.getJSON("graph.json", function (data) {
    const jsonObj = data; //json output
    let selectedNodes = new Set();
    let listNode = []
    let listNeighbour = []
    let centralNode = null;

    const Graph = ForceGraph3D()(document.getElementById("3d-graph"))
        .graphData(jsonObj)
        .nodeLabel("name")
        .linkDirectionalArrowLength(3)
        .linkDirectionalArrowRelPos(1)
        .nodeOpacity(0.5)
        .linkOpacity(0.1)
        .nodeRelSize(5)
        .nodeColor((node) =>
            selectedNodes.has(node) ? "yellow" : node.type == "CLASS" ? "grey" : "blue"
        )
        .onNodeClick((node, event) => {
            if (event.shiftKey && event.ctrlKey) {
                //add selected node and his neighboors to the selection
                selectedNodes.has(node)
                    ? (node.neighbours.forEach((neighbourNode) =>
                        selectedNodes.delete(neighbourNode)
                    ),
                        selectedNodes.delete(node))
                    : (node.neighbours.forEach((neighbourNode) =>
                        selectedNodes.add(neighbourNode)
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
                    && node.neighbours.forEach((neighbourNode) => selectedNodes.add(neighbourNode));
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
        !a.neighbours && (a.neighbours = []);
        !b.neighbours && (b.neighbours = []);
        a.neighbours.push(b);
        b.neighbours.push(a);

        !a.links && (a.links = []);
        !b.links && (b.links = []);
        a.links.push(link);
        b.links.push(link);
    });

    jsonObj.nodes.forEach(elem => {
        listNode.push(elem.name)
    })

    for (var i in listNode.sort()) {
        var li = `<a href="#" class="node list-group-item list-group-item-action">`;
        $(".ContentNodes").append(li.concat(listNode[i]))
    }

    $(".ContentNodes").on("click", ".node", function () {
        selectedNodes = new Set;
        jsonObj.nodes.forEach(node => {
            if (node.name == $(this).text()) {
                centralNode = node;
                listNeighbour = [];
                listNeighbour.push(centralNode.name);
                selectedNodes.add(node);
                node.neighbours.forEach(neighbourNode => {
                    listNeighbour.push(neighbourNode.name);
                    selectedNodes.add(neighbourNode)
                });
                Graph.nodeColor(node => node == centralNode
                    ? "red" : selectedNodes.has(node)
                        ? "yellow" : node.type == "CLASS" ? "grey" : "blue")
                return true;
            }
        })
    });

    $(".ContentNeighbour").on("click", ".node", function () {
        selectedNodes = new Set;
        jsonObj.nodes.forEach(node => {
            if (node.name == $(this).text()) {
                $(".ContentNeighbour").empty();
                centralNode = node;
                var li = `<a href="#" class="node list-group-item list-group-item-action active">`;
                $(".ContentNeighbour").append(li.concat(centralNode.name))
                var li = `<a href="#" class="node list-group-item list-group-item-action"">`;
                selectedNodes.add(node);
                node.neighbours.forEach(neighbourNode => {
                    $(".ContentNeighbour").append(li.concat(neighbourNode.name));
                    selectedNodes.add(neighbourNode)
                });
                Graph.nodeColor(node => node == centralNode
                    ? "red" : selectedNodes.has(node)
                        ? "yellow" : node.type == "CLASS" ? "grey" : "blue")
                return true;
            }
        })
    });

    $("#btn-list").click(function () {
        document.getElementById("nodes").style.display = "block";
        document.getElementById("neighbours").style.display = "none";
    });

    $("#btn-neighbours").click(function () {
        document.getElementById("nodes").style.display = "none";
        document.getElementById("neighbours").style.display = "block";
        $(".ContentNeighbour").empty();
        for (var i in listNeighbour) {
            if (i == 0) {
                var li = `<a href="#" class="node list-group-item list-group-item-action active">`;
            } else {
                var li = `<a href="#" class="node list-group-item list-group-item-action"">`;
            }
            $(".ContentNeighbour").append(li.concat(listNeighbour[i]))
        }
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