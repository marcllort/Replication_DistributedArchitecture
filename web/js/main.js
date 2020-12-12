const keys = [];
const layer_nodes = ['A1', 'A2', 'A3', 'B1', 'B2', 'C1', 'C2'];

document.onreadystatechange = function () {
    if (document.readyState === "interactive") {
        load()
    }
}

function addKey(key) {
    let line = "<tr id=\"key-" + key + "\"><td id=\"A" + key + "-0\">" + key + "</td>";
    for (let i in layer_nodes) {
        line += "<td id=\"" + layer_nodes[i] + "-" + key + "\"></td>";
    }
    line += "</tr>";
    return line;
}

function previousId(key) {
    let newValue = 0;
    for (let i in keys) {
        if (keys[i] < key && keys[i] > newValue) {
            newValue = keys[i];
        }
    }
    if (newValue === 0) {
        document.getElementById('header_table').insertAdjacentHTML('afterend', addKey(key));
    } else {
        document.getElementById("key-" + newValue).insertAdjacentHTML('afterend', addKey(key));
    }
}

function load() {
    const basePort = 4010;
    const websockets = [];

    for (let i in layer_nodes) {
        let port = basePort + +i;
        websockets[i] = new WebSocket("ws://localhost:" + (port));
        websockets[i].onmessage = function (event) {
            let msg = event.data.split('-');
            if (msg.length === 1) {
                console.error("Message not properly formatted!")
                return;
            }

            let key = msg[0];
            if (!keys.includes(key)) {
                previousId(key);
                keys.push(key);
            }
            document.getElementById(layer_nodes[i] + "-" + key).innerHTML = msg[1]; // value
        };
    }
}