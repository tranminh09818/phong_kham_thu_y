const path = require('path');
const { chromium } = require('playwright');

const proofPath = path.resolve(__dirname, 'chatbot-vaccine-proof.png');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1440, height: 900 } });
  await page.addInitScript(() => {
    localStorage.clear();
    sessionStorage.clear();
    window.__AI_ACTION_TAG__ = 'DELETE:stale-action-from-old-autopilot';
  });

  const consoleErrors = [];
  const responses = [];
  page.on('console', (msg) => {
    if (msg.type() === 'error') consoleErrors.push(msg.text());
  });
  page.on('pageerror', (err) => consoleErrors.push(err.message));
  page.on('response', (res) => {
    const url = res.url();
    if (url.includes('/api/chat')) responses.push(`${res.status()} ${url}`);
  });
  page.on('request', (req) => {
    const url = req.url();
    if (url.includes('/api/chat')) {
      const headers = req.headers();
      if (headers['x-ai-action']) responses.push(`REQUEST_HAS_X_AI_ACTION=${headers['x-ai-action']}`);
    }
  });

  await page.goto('http://127.0.0.1:3005/', { waitUntil: 'domcontentloaded' });
  await page.locator('#chatBtn').click({ force: true });
  await page.locator('#chatWindow textarea').fill('Lịch tiêm phòng vaccine định kỳ cho chó mèo?');
  await page.locator('#chatWindow textarea').press('Enter');

  try {
    await page.waitForResponse((res) => res.url().includes('/api/chat') && res.status() === 200, { timeout: 60000 });
    await page.waitForTimeout(3000);
  } catch (err) {
    await page.screenshot({ path: proofPath, fullPage: true });
    const chatText = await page.locator('#chatWindow').innerText().catch(() => '');
    console.error(`CHATBOT_VACCINE_UI_TEST=FAIL\nkhông nhận được /api/chat 200 trong 60s\nSCREENSHOT=${proofPath}\n--- RESPONSES ---\n${responses.join('\n')}\n--- UI TEXT ---\n${chatText}`);
    await browser.close();
    process.exit(1);
  }

  const chatText = await page.locator('#chatWindow').innerText();
  const failures = [];

  if (!responses.some(item => item.startsWith('200 '))) failures.push(`không có /api/chat 200: ${responses.join(', ')}`);
  if (responses.some(item => item.startsWith('403 '))) failures.push(`/api/chat vẫn bị 403: ${responses.join(', ')}`);
  if (chatText.includes('Kết nối gián đoạn')) failures.push('vẫn hiện "Kết nối gián đoạn"');
  if (chatText.includes('data:')) failures.push('vẫn lộ prefix SSE data:');
  if (chatText.includes('Vuilòng') || chatText.includes('Dạsếp') || chatText.includes('Xinchào')) {
    failures.push('vẫn có chữ bị dính');
  }

  await page.screenshot({ path: proofPath, fullPage: true });
  await browser.close();

  if (failures.length) {
    console.error(`CHATBOT_VACCINE_UI_TEST=FAIL\n${failures.join('\n')}\nSCREENSHOT=${proofPath}\n--- RESPONSES ---\n${responses.join('\n')}\n--- UI TEXT ---\n${chatText}\n--- CONSOLE ERRORS ---\n${consoleErrors.join('\n')}`);
    process.exit(1);
  }

  console.log(`CHATBOT_VACCINE_UI_TEST=PASS\nSCREENSHOT=${proofPath}\nRESPONSES=${responses.join('; ')}`);
})();
