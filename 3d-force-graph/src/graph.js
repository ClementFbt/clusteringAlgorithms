$.getJSON("graph.json", function (data) {
    const jsonObj = data; //json output

    let selectedNodes = new Set();

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


    $('.list-group-item').on('click', function() {
        var $this = $(this);
        var $alias = $this.data('alias');
        console.log($this);
        console.log($alias);
        
    })

    var nodeList = `<div class="Container">
        <div class="Content">
            <ul class="list-group list-group-flush">`
    jsonObj.nodes.forEach(elem => {
        nodeList += `<li class="list-group-item">`
        nodeList += elem.name
        nodeList += `</li>`
    })
    nodeList += `</ul>
        </div>`
    const ele = document.createElement('div');
    ele.innerHTML = nodeList;
    document.body.appendChild(ele.firstChild);

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
            if (event.ctrlKey) {
                //add one node to the selection
                selectedNodes.has(node)
                    ? selectedNodes.delete(node)
                    : selectedNodes.add(node);
            } else if (event.shiftKey) {
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