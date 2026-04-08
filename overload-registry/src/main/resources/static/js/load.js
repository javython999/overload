async function fetchLoad(loadId) {
    const res = await fetch(`/api/loads/${loadId}`, {method: "GET"});
    return await res.json();
}