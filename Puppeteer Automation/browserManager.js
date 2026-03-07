const puppeteer = require("puppeteer");

let loginBrowser = null;
let automationBrowser = null;

async function getLoginBrowser() {

    if (!loginBrowser) {

        loginBrowser = await puppeteer.launch({
            headless: true,
            defaultViewport: { width: 1366, height: 768 },
            args: ['--no-sandbox', '--disable-setuid-sandbox']
        });

        console.log("Login Browser Started");
    }

    return loginBrowser;
}

async function getAutomationBrowser() {

    if (!automationBrowser) {

        automationBrowser = await puppeteer.launch({
            headless: true,
            defaultViewport: { width: 1366, height: 768 },
            args: ['--no-sandbox', '--disable-setuid-sandbox']
        });

        console.log("Automation Browser Started");
    }

    return automationBrowser;
}

module.exports = {
    getLoginBrowser,
    getAutomationBrowser
};