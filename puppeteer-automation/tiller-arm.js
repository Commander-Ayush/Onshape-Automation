const { getAutomationBrowser } = require("./browserManager");

async function runTillerArm(username, password) {

    try {

        const browser = await getAutomationBrowser();
        const page = await browser.newPage();

        await startOnshape(page);

        await login(page, username, password);

        await gotoDocumentAndCreateNewDocument(page);

        await loadCanvas(page);

        await initialFrontFace(page);

        await selectTheSketchPlane(page);

        await initialFrontFace(page);

        await sketch(page);

        await centerPointCircle(page);

        await clickAtCenterOfTheSketchBoard(page);

        await mhc(page, 855, 318);

        await type(page, 3);

        await typeAndEnter(page, 5);

        await centerPointCircle(page);

        await moveAndHover(page, 694, 363);

        await dragTo(page, 1022, 124);

        await moveAndHover(page, 1062, 206);

        await centerPointCircle(page);

        await clickAtCenterOfTheSketchBoard(page);

        await moveAndHover(page, 910, 288);

        await clickHere(page);

        await type(page, 7);

        await typeAndEnter(page, 0);

        await centerPointCircle(page);

        await moveAndHover(page, 867, 324);

        await dragTo(page, 975, 109);

        await moveAndHover(page, 1062, 206);

        await centerPointCircle(page);

        await moveAndHover(page, 463, 362);

        await mhc(page, 463, 362);

        await mhc(page, 492, 337);

        await type(page, 2);

        await typeAndEnter(page, 0);

        await d(page);

        await mhc(page, 463, 361);

        await mhc(page, 807, 361);

        await mhc(page, 634, 584);

        await type(page, 1);

        await type(page, 0);

        await typeAndEnter(page, 8);

        await centerPointCircle(page);

        await mhc(page, 412, 361);

        await mhc(page, 515, 293);

        await type(page, 3);

        await typeAndEnter(page, 5);

        await centerPointCircle(page);

        await mhc(page, 465, 327);

        await mhc(page, 303, 270);

        await l(page);

        await mhc(page, 410, 297);

        await mhc(page, 807, 230);

        await l(page);

        await l(page);

        await mhc(page, 401, 424);

        await mhc(page, 807, 494);

        await l(page);

        await t(page);

        await mhc(page, 573, 453);

        await mhc(page, 478, 389);

        await r(page);

        await clickAtCenterOfTheSketchBoard(page);

        await mhc(page, 876, 321);

        await type(page, 4);

        await typeAndEnter(page, 0);

        await type(page, 1);

        await typeAndEnter(page, 0);

        await mirror(page);

        await mhc(page, 588, 363);

        await mhc(page, 585, 455);

        await mirror(page);

        await delay(3);

        await mirror(page);

        await mhc(page, 805, 186);

        await delay(2);

        await mhc(page, 642, 260);

        await mhc(page, 642, 461);

        await mhc(page, 477, 363);

        await mhc(page, 448, 383);

        await mirror(page);

        await page.keyboard.down('Shift');

        await mhc(page, 598, 331);

        await mhc(page, 463, 361);

        await mhc(page, 739, 291);

        await page.keyboard.up('Shift');

        await extrude(page);

        await enter(page);

        await delay(2);

        await page.locator('text=Sketch 1').click();

        await delay(0.15);

        await page.locator('text=Sketch 1').click();

        await delay(2);

        await page.keyboard.down('Shift');

        await moveAndHover(page, 1150, 324);

        await moveAndHover(page, 1152, 324);

        await mhc(page, 1195, 414);

        await mhc(page, 1047, 343);

        await page.keyboard.down('Shift');

        await extrude(page);

        await depth2(page, 1, 0);

        return { success: true };

    } catch (err) {
        console.error("[Tiller Arm: ]", err.message);
        await page.close();
        return { success: false };
    }
}

// ---------------- HELPERS ----------------

async function delay(x) {
    await new Promise(r => setTimeout(r, x * 1000));
}


// ---------------- CORE FUNCTIONS ----------------

async function startOnshape(page) {
    await page.goto("https://cad.onshape.com/signin/", {
        waitUntil: "networkidle2"
    });
    console.log("started onshape");
}

async function login(page, username, password) {
    await page.waitForSelector('input[name="username"]');
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
}

async function gotoDocumentAndCreateNewDocument(page) {
    await page.waitForSelector('#create-new-type');
    await page.click('#create-new-type');

    await page.locator('text=Document…').setTimeout(5000).click();

    console.log("document button clicked");

    await page.waitForSelector('#document-name-input');
    await delay(2);
    await page.keyboard.press('Enter');
}

async function loadCanvas(page) {
    await page.waitForSelector('#canvas');
    console.log("canvas loaded");
    await delay(3);
}

async function initialFrontFace(page) {
    await delay(2);
    await mhc(page, 1272, 167);
    await delay(3);
}

async function initialRightFace(page) {
    await mhc(page, 1299, 172);
    await delay(3);
}

async function initialTopFace(page) {
    await mhc(page, 1275, 140);
    await delay(3);
}

async function selectTheSketchPlane(page) {
    await mhc(page, 700, 261);
    await delay(3);
}



async function sketch(page) {
    await shift(page, 'S');
    await delay(2);
}

async function centerPointCircle(page) {
    await page.keyboard.press('KeyC');
    await delay(2.5);
}

async function d(page) {
    await page.keyboard.press('KeyD');
    await delay(1.5);
}

async function l(page) {
    await page.keyboard.press('KeyL');
    await delay(1.5);
}

async function t(page) {
    await page.keyboard.press('KeyT');
    await delay(1.5);
}

async function r(page) {
    await page.keyboard.press('KeyR');
    await delay(1.5);
}

async function clickAtCenterOfTheSketchBoard(page) {
    await mhc(page, 806, 363);
    await delay(2);
}

async function dragTo(page, endingX, endingY) {
    await page.mouse.down();
    await page.mouse.move(endingX, endingY, { steps: 20 });
    await delay(0.5);
    await page.mouse.up();
    console.log("mouse lifted")
    await delay(2);
}

async function doubleClick(page, x, y) {
    await moveAndHover(page, x, y);
    await page.mouse.click(x, y, { clickCount: 2 });
    await delay(2);
}

async function extrude(page) {
    shift(page, 'E')
    await delay(2.5);
}

async function depth2(page, x, y) {
    const selector = 'input[data-bs-original-title*="Depth"]';

    await page.waitForSelector(selector, { visible: true });
    await page.click(selector, { clickCount: 2 });

    await type(page, x);
    await typeAndEnter(page, y);
}

// ---------------- LOW LEVEL ACTIONS ----------------

async function mhc(page, x, y) {
    await moveAndHover(page, x, y);
    await delay(1.5);
    await page.mouse.down();
    await delay(0.3);
    await page.mouse.up();
    console.log("clicked at", x, y);
}

async function shift(page, s) {
    await page.keyboard.down('Shift');
    await page.keyboard.press('Key' + s.toUpperCase());
    await page.keyboard.up('Shift');
}

async function enter(page) {
    await page.keyboard.press('Enter');
    await delay(1.5);
}

async function type(page, text) {
    await delay(1.5);
    await page.keyboard.type(String(text), { delay: 1 });
    console.log("Entered " + text);
    await delay(1);
}

async function typeAndEnter(page, text) {
    await page.keyboard.type(String(text), { delay: 1 });
    await delay(2);
    await page.keyboard.press('Enter');
    console.log("Entered " + text);
}

async function moveAndHover(page, x, y) {
    await page.mouse.move(x, y);
    await delay(0.5);
    await page.mouse.move(x + 2, y + 2);
    await delay(0.5);
    await page.mouse.move(x, y);
}

async function mirror(page) {
    await moveAndHover(page, 820, 56);
    delay(0.6);
    await mhc(page, 820, 56);
}
async function clickHere(page) {
    await page.mouse.down();
    await delay(0.2);
    await page.mouse.up();
}

module.exports = { runTillerArm };