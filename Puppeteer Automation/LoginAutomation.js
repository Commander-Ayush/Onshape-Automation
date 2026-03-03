const puppeteer = require("puppeteer");

const args = process.argv;
const username = args[2];
const user_password = args[3];
const startTime = Date.now();

if (!username || !user_password) {
    console.log("Usage: node login.js <email> <password>");
    process.exit(1);
}

(async () => {

    const browser = await puppeteer.launch({
        headless: true,
        defaultViewport: {
            width: 1366,
            height: 768
        },
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    });

    const page = await browser.newPage();
    await page.goto("https://cad.onshape.com/signin", { waitUntil: "networkidle2" });

    // console.log("Page Loaded:", await page.title());

    // -----------------------
    // STEP 1: Enter Email
    // -----------------------

    await page.waitForSelector('input[name="username"]');
    await page.type('input[name="username"]', username);
    await page.keyboard.press("Enter");

    // Wait for password field OR email error bubble
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
        await browser.close();
        const endTime = Date.now();
        const duration = (endTime - startTime) / 1000;
        console.log(`Execution Time: ${duration} seconds`);
        return;
    }

    // -----------------------
    // STEP 2: Enter Password
    // -----------------------

    await page.type('input[name="password"]', user_password);
    await page.keyboard.press("Enter");

    // Race success navigation vs password error
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
        await browser.close();
        const endTime = Date.now();
        const duration = (endTime - startTime) / 1000;
        console.log(`Execution Time: ${duration} seconds`);
        return;
    }

    // -----------------------
    // SUCCESS
    // -----------------------

    if (page.url().includes("documents")) {
        console.log("Login Successful");
    } else {
        console.log("Unexpected state reached.");
    }

    await browser.close();
    const endTime = Date.now();
    const duration = (endTime - startTime) / 1000;
    console.log(`Execution Time: ${duration} seconds`);

})();