// YOUR API BASE URL (updated for your setup)
const API_BASE = "http://localhost:8080/api/v1";

document.getElementById('shortenBtn').addEventListener('click', async () => {
  const longUrl = document.getElementById('longUrl').value.trim();
  const resultDiv = document.getElementById('result');
  const btn = document.getElementById('shortenBtn');
  const btnText = document.getElementById('btnText');

  if (!longUrl || !/^https?:\/\//i.test(longUrl)) {
    showResult('Please enter a valid URL starting with http:// or https://', true);
    return;
  }

  btn.disabled = true;
  btnText.innerHTML = '<div class="spinner"></div> Shortening...';

  try {
    const res = await fetch(`${API_BASE}/shorten`, {
      method: 'POST',
      headers: { 'Content-Type': 'text/plain' },
      body: longUrl
    });

    // NEW — read as plain text, not JSON
    const text = await res.text();

    if (res.ok && text) {
      // Your backend can return either:
      // 1. Just the shortKey like "abc123"
      // 2. Or the full URL like "http://localhost:8080/abc123"
      const shortUrl = text.startsWith('http') ? text : `${window.location.origin}/${text}`;
      
      showResult(`
        <div class="short-url">${shortUrl}</div>
        <button class="copy-btn" onclick="copyToClipboard('${shortUrl}')">
          Copy
        </button>
      `, false);
    } else {
      showResult(await res.text() || 'Failed to shorten URL', true);
    }
  } catch (err) {
    console.log(err);
    showResult('Cannot connect to server. Is Spring Boot running on port 8080?', true);
  } finally {
    btn.disabled = false;
    btnText.textContent = 'Shorten URL';
  }
});

function showResult(html, isError = false) {
  const resultDiv = document.getElementById('result');
  resultDiv.innerHTML = html;
  resultDiv.classList.toggle('error', isError);
  resultDiv.style.display = 'block';
}

function copyToClipboard(text) {
  navigator.clipboard.writeText(text).then(() => {
    // Find the button that was clicked by scanning all copy buttons
    const buttons = document.querySelectorAll('.copy-btn');
    const clickedButton = Array.from(buttons).find(btn => 
      btn.closest('.result')?.querySelector('.short-url')?.textContent === text
    );

    if (clickedButton) {
      const original = clickedButton.innerHTML;
      clickedButton.innerHTML = 'Copied!';
      setTimeout(() => clickedButton.innerHTML = original, 2000);
    }
  }).catch(err => {
    console.error('Failed to copy: ', err);
    alert('Failed to copy to clipboard');
  });
}

// Auto-redirect when visiting a short link (e.g. localhost:8080/abc123)
const path = window.location.pathname;
if (path.length > 1 && path !== '/' && !path.includes('.')) {
  const shortKey = path.slice(1);

  document.getElementById('shortener-form').style.display = 'none';
  document.getElementById('redirecting').style.display = 'block';

  // Try to get the long URL first (optional - nice for analytics)
  fetch(`${API_BASE}/${shortKey}`)
    .then(r => {
      if (r.ok) return r.json();
      throw new Error();
    })
    .then(data => {
      setTimeout(() => window.location.href = data.url || data.longUrl || '/', 1000);
    })
    .catch(() => {
      // Fallback: let your Spring Boot controller do the redirect
      setTimeout(() => window.location.reload(), 1000);
    });
}