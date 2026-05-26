const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1440, height: 900 } });

  await page.route('**/api/chat', async (route) => {
    await route.fulfill({
      status: 200,
      headers: {
        'content-type': 'text/event-stream; charset=utf-8',
        'cache-control': 'no-cache',
      },
      body: 'data:X\n\ndata:in\n\ndata: chào\n\ndata: Sen\n\ndata:!\n\n',
    });
  });

  await page.goto('http://127.0.0.1:3005/', { waitUntil: 'domcontentloaded' });
  await page.locator('#chatBtn').click({ force: true });
  await page.locator('textarea[placeholder="Nhắn tin cho Bác sĩ Thú y Rexi..."]').fill('test stream spacing');
  await page.locator('#chatWindow button').last().click();

  const expected = 'Xin chào Sen!';
  await page.waitForFunction((text) => document.querySelector('#chatWindow')?.innerText.includes(text), expected, {
    timeout: 10000,
  });

  const chatText = await page.locator('#chatWindow').innerText();
  const failures = [];
  if (!chatText.includes(expected)) failures.push(`missing expected text: ${expected}`);
  if (chatText.includes('data:')) failures.push('raw SSE data prefix is visible');
  if (chatText.includes('Xinchào') || chatText.includes('Vuilòng')) failures.push('streamed words are still collapsed');

  await browser.close();

  if (failures.length) {
    console.error(`CHATBOT_STREAM_TEST=FAIL\n${failures.join('\n')}\n--- UI TEXT ---\n${chatText}`);
    process.exit(1);
  }

  console.log(`CHATBOT_STREAM_TEST=PASS\nFOUND="${expected}"`);
})();
