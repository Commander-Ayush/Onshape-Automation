async function uploadAssignment() {

    const name = document.getElementById("name").value;
    const dimension = document.getElementById("dimension").value;
    const college = document.getElementById("college").value;
    const branch = document.getElementById("branch").value;
    const price = document.getElementById("price").value;
    const automationName = document.getElementById("automationName").value;

    try {

        const response = await fetch('/assignment-upload', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                nameOfAssignment: name,
                dimensionOfAssignment: dimension,
                collegeOfAssignment: college,
                branchOfAssignment: branch,
                priceOfAssignment: price,
                automationName: automationName
            })
        });

        const result = await response.json();

        if (result.success) {
            alert("Assignment uploaded successfully");
        }

    } catch (error) {
        console.error(error);
    }
}