const path = require('path');
const { chromium } = require('playwright');

const proofPath = path.resolve(__dirname, 'chatbot-real-proof.png');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1440, height: 900 } });
  await page.addInitScript(() => {
    localStorage.clear();
    sessionStorage.clear();
  });

  const consoleErrors = [];
  page.on('console', (msg) => {
    if (msg.type() === 'error') consoleErrors.push(msg.text());
  });
  page.on('pageerror', (err) => consoleErrors.push(err.message));

  await page.goto('http://127.0.0.1:3005/', { waitUntil: 'domcontentloaded' });
  await page.locator('#chatBtn').click({ force: true });
  await page.locator('#chatWindow textarea').fill('cấp cứu hóc dị vật');
  await page.locator('#chatWindow button').last().click({ force: true });

  await page.waitForFunction(() => {
    const text = document.querySelector('#chatWindow')?.innerText || '';
    return text.includes('0353.374.156') || text.includes('Heimlich') || text.includes('hóc dị vật');
  }, null, { timeout: 20000 });

  await page.waitForTimeout(1500);
  const chatText = await page.locator('#chatWindow').innerText();
  const failures = [];

  if (!chatText.includes('0353.374.156') && !chatText.includes('Heimlich') && !chatText.includes('hóc dị vật')) {
    failures.push('không thấy nội dung cấp cứu hợp lệ');
  }
  if (chatText.includes('Kết nối gián đoạn')) failures.push('vẫn hiện lỗi kết nối gián đoạn');
  if (chatText.includes('data:')) failures.push('vẫn lộ prefix SSE data:');
  if (chatText.includes('Vuilòng') || chatText.includes('Dạsếp') || chatText.includes('Xinchào')) {
    failures.push('vẫn có chữ bị dính do mất khoảng trắng stream');
  }

  await page.screenshot({ path: proofPath, fullPage: true });
  await browser.close();

  if (failures.length) {
    console.error(`CHATBOT_REAL_UI_TEST=FAIL\n${failures.join('\n')}\nSCREENSHOT=${proofPath}\n--- UI TEXT ---\n${chatText}\n--- CONSOLE ERRORS ---\n${consoleErrors.join('\n')}`);
    process.exit(1);
  }

  console.log(`CHATBOT_REAL_UI_TEST=PASS\nSCREENSHOT=${proofPath}`);
})();
