const puppeteer = require("puppeteer");

let browser = null;

async function getBrowser() {

    if (!browser) {
        browser = await puppeteer.launch({
            headless: true,
            defaultViewport: {
                width: 1366,
                height: 768
            },
            args: ['--no-sandbox', '--disable-setuid-sandbox']
        });

        console.log("Browser Started");
    }

    return browser;
}

module.exports = { getBrowser };