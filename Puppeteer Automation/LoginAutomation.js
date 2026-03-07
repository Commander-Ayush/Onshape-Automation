const { getLoginBrowser } = require("./browserManager");

async function login(username, user_password) {

    const startTime = Date.now();

    const browser = await getLoginBrowser();
    const page = await browser.newPage();

    try {

        await page.goto("https://cad.onshape.com/signin", { waitUntil: "networkidle2" });

        // STEP 1: Enter Email
        await page.waitForSelector('input[name="username"]');
        await page.type('input[name="username"]', username);
        await page.keyboard.press("Enter");

        const emailStep = await Promise.race([
            page.waitForSelector('input[name="password"]').then(() => "password"),
            page.waitForSelector('span.osx-bubble-message').then(() => "email_error")
        ]);

        if (emailStep === "email_error") {

            const errorText = await page.$eval(
                "span.osx-bubble-message",
                el => el.textContent.trim()
            );

            console.log("Authentication Failed:", errorText);

            await page.close();
            return false;
        }

        // STEP 2: Enter Password
        await page.type('input[name="password"]', user_password);
        await page.keyboard.press("Enter");

        const loginResult = await Promise.race([
            page.waitForNavigation({ waitUntil: "networkidle2" }).then(() => "success"),
            page.waitForSelector('span.osx-bubble-message').then(() => "password_error")
        ]);

        if (loginResult === "password_error") {

            const errorText = await page.$eval(
                "span.osx-bubble-message",
                el => el.textContent.trim()
            );

            console.log("Login Failed:", errorText);

            await page.close();
            return false;
        }

        // SUCCESS
        if (page.url().includes("documents")) {

            console.log("Login Successful");

            const endTime = Date.now();
            console.log("Execution Time:", (endTime - startTime) / 1000, "seconds");

            await page.close();
            return true;
        }

        await page.close();
        return false;

    } catch (err) {

        console.log(err);
        await page.close();
        return false;
    }
}

module.exports = { login };