
var nodes = Object.freeze(['A1', 'A2', 'A3', 'B1', 'B2', 'C1', 'C2']);
var variables = []

function updateNodeValue(node_id, value) {
    document.getElementById(node_id).innerHTML = value;
}

function findPreviousRowId(variable){
    let candidate = 0;
    for (let index in variables) {
        if (variables[index] < variable && variables[index] > candidate) {
            candidate = variables[index];
        }
    }
    return candidate == 0 ? null : "value-" + candidate;
}

function createRow(variable) {
    str = "<tr id=\"value-" +variable + "\"><th>" + variable + "</th>";
    for (let index in nodes) {
        str += "<th id=\"" + nodes[index] + "-" + variable + "\"></th>";
    }
    str += "</tr>";
    return str;
}

$(document).ready(function(){
    var ws = [];
    var initialPort = 4010;

    for (let index in nodes) {
        ws[index] = new WebSocket("ws://localhost:" + (initialPort + +index));
        ws[index].onmessage = function(event) {

            let msg = event.data.split('|');
            if (msg.length == 1)
                return;

            let variable = msg[0];
            let value = msg[1];

            if (!variables.includes(variable)) {
                let id = findPreviousRowId(variable);
                console.log(id);
                let row = createRow(variable);
                if (id != null)
                    document.getElementById(id).insertAdjacentHTML('afterend', row);
                else
                    document.getElementById('table-header').insertAdjacentHTML('afterend', row);
                variables.push(variable);
            }

            console.log(nodes[index] + "-" + variable);
            updateNodeValue(nodes[index] + "-" + variable, value);
        };
    }
});
