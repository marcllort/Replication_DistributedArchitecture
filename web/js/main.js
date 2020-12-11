var nodes = Object.freeze(['A1', 'A2', 'A3', 'B1', 'B2', 'C1', 'C2']);
var keys = []

function updateNodeValue(node_id, value) {
    document.getElementById(node_id).innerHTML = value;
}

function findPreviousRowId(variable) {
    let candidate = 0;
    for (let index in keys) {
        if (keys[index] < variable && keys[index] > candidate) {
            candidate = keys[index];
        }
    }
    return candidate === 0 ? null : "value-" + candidate;
}

function addRow(key) {
    str = "<tr id=\"value-" + key + "\"><td>" + key + "</td>";
    for (let index in nodes) {
        str += "<td id=\"" + nodes[index] + "-" + key + "\"></td>";
    }
    str += "</tr>";
    return str;
}

function ready() {
    var ws = [];
    var initialPort = 4010;

    for (let index in nodes) {
        ws[index] = new WebSocket("ws://localhost:" + (initialPort + +index));
        ws[index].onmessage = function (event) {

            let msg = event.data.split('-');
            if (msg.length === 1)
                return;

            let variable = msg[0];
            let value = msg[1];

            if (!keys.includes(variable)) {
                let id = findPreviousRowId(variable);
                console.log(id);
                let row = addRow(variable);
                if (id != null)
                    document.getElementById(id).insertAdjacentHTML('afterend', row);
                else
                    document.getElementById('table-header').insertAdjacentHTML('afterend', row);
                keys.push(variable);
            }

            console.log(nodes[index] + "-" + variable);
            updateNodeValue(nodes[index] + "-" + variable, value);
        };
    }
}

document.onreadystatechange = function () {
    if (document.readyState == "interactive") {
        ready()
    }
}
