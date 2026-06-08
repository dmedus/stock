const CACHE_NAME = 'stock-vinos-v1';
const STATIC_ASSETS = [
  '/css/styles.css',
  '/css/index.css',
  '/offline.html'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(STATIC_ASSETS))
  );
  self.skipWaiting();
});

self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(keys.filter(k => k !== CACHE_NAME).map(k => caches.delete(k)))
    )
  );
  self.clients.claim();
});

self.addEventListener('fetch', event => {
  const { request } = event;
  if (request.method !== 'GET') return;

  const url = new URL(request.url);

  // Cache-first for local static assets (CSS, images, JS)
  if (url.origin === self.location.origin &&
      (url.pathname.startsWith('/css/') ||
       url.pathname.startsWith('/img/') ||
       url.pathname.startsWith('/js/'))) {
    event.respondWith(
      caches.match(request).then(cached =>
        cached || fetch(request).then(response => {
          const clone = response.clone();
          caches.open(CACHE_NAME).then(cache => cache.put(request, clone));
          return response;
        })
      )
    );
    return;
  }

  // Network-first for HTML navigation — fall back to offline page if no network
  if (request.mode === 'navigate') {
    event.respondWith(
      fetch(request).catch(() => caches.match('/offline.html'))
    );
  }
});
