const express = require("express");
const pLimit = require("p-limit").default;
const { login } = require("./LoginAutomation");
const { runTestDrive } = require("./test-drive");
const { runTillerArm } = require("./tiller-arm");

const app = express();
app.use(express.json());

const limit = pLimit(10);

app.post("/login", async (req, res) => {
    const { username, password } = req.body;

    if (limit.pendingCount > 40) {
        return res.status(429).json({ success: false, error: "Server busy. Try again later." });
    }

    try {
        const result = await limit(() => login(username, password));
        res.json({ success: result });
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, error: "Automation failed" });
    }
});

// ── NEW ROUTE ──────────────────────────────────────────
app.post("/test-drive", async (req, res) => {
    const { email, password } = req.body;

    if (limit.pendingCount > 40) {
        return res.status(429).json({ success: false, error: "Server busy. Try again later." });
    }

    try {
        const result = await limit(() => runTestDrive(email, password));
        res.json(result);
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, error: "Automation failed" });
    }
});

app.post("/tiller-arm", async (req, res) => {
    const { email, password } = req.body;

    if (limit.pendingCount > 40) {
        return res.status(429).json({ success: false, error: "Server busy. Try again later." });
    }

    try {
        const result = await limit(() => runTillerArm(email, password));
        res.json(result);
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, error: "Automation failed" });
    }
});



app.listen(3000, () => {
    console.log("Automation Server Running on Port 3000");
});