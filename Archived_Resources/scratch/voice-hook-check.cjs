const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  await page.goto('http://127.0.0.1:3005/dich-vu/cap-cuu-247', {
    waitUntil: 'domcontentloaded',
    timeout: 15000
  });
  await page.waitForTimeout(1500);
  await page.evaluate(() => document.querySelector('[data-ai-id="button-chatbot-yhoj"]')?.click());
  await page.waitForTimeout(800);

  const hook = await page.evaluate(() => !!window.__REXI_VOICE_TEST__);
  await page.evaluate(() => window.__REXI_VOICE_TEST__.say('nói nhanh', { confidence: 0.96 }));
  await page.waitForTimeout(500);
  const fast = await page.textContent('body');

  await page.evaluate(() => window.__REXI_VOICE_TEST__.say('đợi tôi tí', { confidence: 0.96 }));
  await page.waitForTimeout(500);
  const wait = await page.textContent('body');

  await page.evaluate(() => window.__REXI_VOICE_TEST__.say('tiếp tục', { confidence: 0.96 }));
  await page.waitForTimeout(500);
  await page.evaluate(() => window.__REXI_VOICE_TEST__.say('xóa', { confidence: 0.2 }));
  await page.waitForTimeout(900);
  const unclear = await page.textContent('body');

  console.log(JSON.stringify({
    hook,
    hasFast: /FAST|Đã bật chế độ nói nhanh|noi nhanh/i.test(fast || ''),
    hasWait: /WAIT|Đang chờ|Rexi đang chờ/i.test(wait || ''),
    hasUnclear: /nghe chưa rõ|nói lại/i.test(unclear || '')
  }, null, 2));
  await browser.close();
})().catch((error) => {
  console.error(error);
  process.exit(1);
});
