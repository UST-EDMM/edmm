
export function postUploadModel(endpoint, body, edmmId) {
    console.log("postUploadModel")

    const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            target: "multi",
            input: body
        })
    };

    fetch(endpoint + "/orchestration/transform", requestOptions)
        .then(response => {
            if (response.status === 200) {
                setTimeout(function () {
                    fetch(endpoint + "/orchestration/transfer", {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({
                            endpoint: endpoint,
                            multiId: edmmId
                        })
                    }).then(r => console.log(r.status))
                }, 2000)
            }
        });
}

export function postStartOrchestration(endpoint, edmmId) {

    const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            endpoint: endpoint,
            multiId: edmmId
        })
    };

    console.log(requestOptions.body)

    return fetch(endpoint + "/orchestration/initiate",
        requestOptions)
        .then(response => response.status);
}
