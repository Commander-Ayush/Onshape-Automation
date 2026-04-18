const { getAutomationBrowser } = require("./browserManager");

async function runTestDrive(username, password) {

    const browser = await getAutomationBrowser();
    const page = await browser.newPage();

    try {
        await page.goto("https://cad.onshape.com/signin/", {
            waitUntil: "networkidle2"
        });

        await page.type('input[name="username"]', username);
        await page.keyboard.press('Enter');

        await page.waitForSelector('input[name="password"]');
        await page.type('input[name="password"]', password);
        await page.keyboard.press('Enter');

        try {
            await page.waitForFunction(
                () => window.location.href.includes('dashboard'),
                { timeout: 5000 }
            );
        } catch {
            await page.goto("https://cad.onshape.com/documents", {
                waitUntil: "networkidle2"
            });
        }

        await page.waitForSelector('#create-new-type');
        await page.click('#create-new-type');

        await page.locator('text=Document…').setTimeout(5000).click();

        console.log("document button clicked");

        await page.waitForSelector('#document-name-input');
        await delay(2000);
        await page.keyboard.press('Enter');

        console.log("create button clicked");

        await page.waitForSelector("#canvas");
        console.log("canvas loaded");

        await delay(3000);

        await humanClick(1259, 175, page);
        await delay(3000);

        await humanClick(1264, 179, page);
        await delay(1000);

        await humanClick(929, 270, page);
        await delay(1000);

        await shiftS(page);
        await delay(1000);

        await humanClick(929, 270, page);

        await page.keyboard.press("KeyL");
        await page.keyboard.press("KeyL");
        await page.keyboard.press("KeyL");

        await humanClick(723, 223, page);
        await delay(1000);

        await humanClick(1031, 395, page);
        await delay(1000);

        await humanClick(690, 482, page);
        await delay(1000);

        await humanClick(723, 223, page);

        await page.close();

        console.log("test-drive completed")

        return { success: true };

    } catch (err) {
        console.error("[TEST DRIVE ERROR]", err.message);
        await page.close();
        return { success: false };
    }
}

async function humanClick(x, y, page) {
    await new Promise(r => setTimeout(r, 1500));
    await page.mouse.move(x, y, { steps: 25 });
    await new Promise(r => setTimeout(r, 1500));
    await page.mouse.down();
    await new Promise(r => setTimeout(r, 300));
    await page.mouse.up();

    console.log("moved to", x + ", " + y);
}

async function delay(x) {
    await new Promise(r => setTimeout(r, x));
}

async function shiftS(page) {
    await page.keyboard.down('Shift');
    await page.keyboard.press('KeyS');
    await page.keyboard.up('Shift');
}

module.exports = { runTestDrive };