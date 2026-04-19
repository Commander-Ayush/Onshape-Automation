const { getLoginBrowser } = require("./browserManager");

async function loginOnce(browser, username, user_password) {
    const page = await browser.newPage();
    try {
        await page.goto("https://cad.onshape.com/signin", { waitUntil: "networkidle2" });

        await page.waitForSelector('input[name="username"]');
        await page.type('input[name="username"]', username);
        await page.keyboard.press("Enter");

        const emailStep = await Promise.race([
            page.waitForSelector('input[name="password"]').then(() => "password"),
            page.waitForSelector('span.osx-bubble-message').then(() => "email_error")
        ]);

        if (emailStep === "email_error") {
            await page.close();
            return false;
        }

        await page.type('input[name="password"]', user_password);
        await page.keyboard.press("Enter");

        const loginResult = await Promise.race([
            page.waitForNavigation({ waitUntil: "networkidle2" }).then(() => "success"),
            page.waitForSelector('span.osx-bubble-message').then(() => "password_error")
        ]);

        if (loginResult === "password_error") {
            await page.close();
            return false;
        }

        const success = page.url().includes("documents");
        await page.close();
        return success;

    } catch (err) {
        console.log("[loginOnce error]", err.message);
        await page.close();
        return false;
    }
}

async function login(username, user_password) {
    const startTime = Date.now();
    const browser = await getLoginBrowser();

    const results = await Promise.all([
        loginOnce(browser, username, user_password),
        loginOnce(browser, username, user_password),
        loginOnce(browser, username, user_password)
    ]);

    const success = results.some(r => r === true);

    console.log("Login attempts:", results);
    console.log("Login result:", success);
    console.log("Execution Time:", (Date.now() - startTime) / 1000, "seconds");

    return success;
}

module.exports = { login };