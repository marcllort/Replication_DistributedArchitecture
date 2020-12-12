var layer_node = Object.freeze(['A1', 'A2', 'A3', 'B1', 'B2', 'C1', 'C2']);
var keys = []

function previousId(variable) {
    var candidate = 0;
    for (let i in keys) {
        if (keys[i] < variable && keys[i] > candidate) {
            candidate = keys[i];
        }
    }
    return candidate === 0 ? null : "value-" + candidate;
}

function addKey(key) {
    str = "<tr id=\"value-" + key + "\"><td>" + key + "</td>";
    for (let i in layer_node) {
        str += "<td id=\"" + layer_node[i] + "-" + key + "\"></td>";
    }
    str += "</tr>";
    return str;
}

function load() {
    var ws = [];
    var initialPort = 4010;

    for (let i in layer_node) {
        ws[i] = new WebSocket("ws://localhost:" + (initialPort + +i));
        ws[i].onmessage = function (event) {

            let msg = event.data.split('-');
            if (msg.length === 1)
                return;

            let key = msg[0];
            let value = msg[1];

            if (!keys.includes(key)) {
                let id = previousId(key);
                let row = addKey(key);
                if (id != null)
                    document.getElementById(id).insertAdjacentHTML('afterend', row);
                else
                    document.getElementById('header_table').insertAdjacentHTML('afterend', row);
                keys.push(key);
            }

            document.getElementById(layer_node[i] + "-" + key).innerHTML = value;
        };
    }
}

document.onreadystatechange = function () {
    if (document.readyState == "interactive") {
        load()
    }
}
