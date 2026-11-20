/* KainoVaii — main.js */

/* ── CURSOR ─────────────────────────────────────────────────── */
const $cursor = document.getElementById('cursor');
const $ring   = document.getElementById('cursorRing');
let mx = 0, my = 0, rx = 0, ry = 0;

document.addEventListener('mousemove', e => {
  mx = e.clientX; my = e.clientY;
  $cursor.style.left = mx + 'px';
  $cursor.style.top  = my + 'px';
});
(function rafRing() {
  rx += (mx - rx) * 0.11;
  ry += (my - ry) * 0.11;
  $ring.style.left = rx + 'px';
  $ring.style.top  = ry + 'px';
  requestAnimationFrame(rafRing);
})();

document.querySelectorAll('a,button,input,textarea,.pe-card,.stat-glass,.tech-pill').forEach(el => {
  el.addEventListener('mouseenter', () => $ring.classList.add('big'));
  el.addEventListener('mouseleave', () => $ring.classList.remove('big'));
});

/* ── NAV ────────────────────────────────────────────────────── */
const $nav = document.getElementById('nav');
window.addEventListener('scroll', () => {
  $nav.classList.toggle('scrolled', scrollY > 60);
}, { passive: true });

/* ── RIPPLE on .btn-primary ─────────────────────────────────── */
document.querySelectorAll('.btn-primary').forEach(btn => {
  btn.addEventListener('click', () => {
    btn.classList.remove('ripple');
    void btn.offsetWidth;
    btn.classList.add('ripple');
  });
});

/* ── GITHUB DASHBOARD ───────────────────────────────────────── */
(function initGHDashboard() {
  const GH    = 'https://api.github.com';
  const USER  = 'kainovaii';
  const TOKEN = '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';
  const H     = { 'Authorization': 'Bearer ' + TOKEN };

  const LANG_COLORS = {
    Java:'#b07219', JavaScript:'#f1e05a', HTML:'#e34c26', CSS:'#563d7c',
    TypeScript:'#2b7489', Python:'#3572A5', Twig:'#a0522d', Shell:'#89e051',
    Kotlin:'#F18E33', Vue:'#41b883', PHP:'#4F5D95'
  };
  function langColor(l) { return LANG_COLORS[l] || '#4a4a6a'; }

  function timeAgo(iso) {
    const d = (Date.now() - new Date(iso)) / 1000;
    if (d < 60)      return Math.floor(d) + 's';
    if (d < 3600)    return Math.floor(d/60) + 'min';
    if (d < 86400)   return Math.floor(d/3600) + 'h';
    if (d < 2592000) return Math.floor(d/86400) + 'j';
    return Math.floor(d/2592000) + 'mois';
  }

  function buildLangs(repos) {
    const counts = {};
    repos.forEach(r => { if (r.language) counts[r.language] = (counts[r.language]||0) + 1; });
    const total = Object.values(counts).reduce((a,b)=>a+b,0) || 1;
    return Object.entries(counts)
      .sort((a,b) => b[1]-a[1]).slice(0,5)
      .map(([lang,count]) => ({ lang, pct: Math.round(count/total*100) }));
  }

  function animNum(el, target) {
    if (!el) return;
    const start = performance.now();
    const dur = 1200;
    (function step(now) {
      const p = Math.min((now - start) / dur, 1);
      el.textContent = Math.round((1 - Math.pow(2, -10*p)) * target);
      if (p < 1) requestAnimationFrame(step);
      else el.textContent = target;
    })(performance.now());
  }

  async function fetchAll() {
    const since = new Date(Date.now() - 60*24*3600*1000).toISOString();
    const [uRes, rRes, eRes] = await Promise.all([
      fetch(`${GH}/users/${USER}`,                                 { headers: H }),
      fetch(`${GH}/users/${USER}/repos?per_page=100&sort=pushed`,  { headers: H }),
      fetch(`${GH}/users/${USER}/events?per_page=50`,              { headers: H }),
    ]);
    const rem = eRes.headers.get('x-ratelimit-remaining');
    if (rem !== null) {
      const el = document.getElementById('ghRateLabel');
      if (el) el.textContent = rem + ' req/h';
    }
    if (!uRes.ok) throw new Error('Profil introuvable');
    const user   = await uRes.json();
    const repos  = rRes.ok ? await rRes.json() : [];
    const events = eRes.ok ? await eRes.json() : [];

    // Orgas connues (obsidian-framework non détectée via /user/orgs avec ce token)
    const ORGS = ['obsidian-framework', 'sparkjava-community', 'laycloud-fr'];
    const orgRepoArrays = await Promise.all(
      ORGS.map(o =>
        fetch(`${GH}/orgs/${o}/repos?per_page=100&sort=pushed`, { headers: H })
          .then(res => res.ok ? res.json() : [])
          .catch(() => [])
      )
    );
    const orgs = ORGS.map(login => ({ login })); // pour le log
    const allRepos = [...repos, ...orgRepoArrays.flat()]
      .sort((a,b) => new Date(b.pushed_at) - new Date(a.pushed_at))
      .slice(0, 20);

    // Vrais commits depuis les 20 repos les plus récents (perso + orga)
    const commitArrays = await Promise.all(
      allRepos.map(r =>
        fetch(`${GH}/repos/${r.full_name}/commits?author=${USER}&since=${since}&per_page=100`, { headers: H })
          .then(res => res.ok ? res.json() : [])
          .then(list => Array.isArray(list) ? list.map(c => ({
            msg:  c.commit.message.split('\n')[0].slice(0, 50),
            date: c.commit.author.date,
            repo: r.name,
          })) : [])
          .catch(() => [])
      )
    );
    const commits = commitArrays.flat().sort((a,b) => new Date(b.date) - new Date(a.date));
    return { user, repos: allRepos, events, commits };
  }

  function render({ user, repos, events, commits }) {
    const langs = buildLangs(repos);
    const $body = document.getElementById('ghBody');
    if (!$body) return;

    const fl = document.getElementById('ghFooterLeft');
    const fr = document.getElementById('ghFooterRight');
    if (fl) fl.textContent = `${user.login} · ${user.public_repos} repos`;
    if (fr) fr.textContent = new Date().toLocaleTimeString('fr-FR');
    setInterval(() => { if (fr) fr.textContent = new Date().toLocaleTimeString('fr-FR'); }, 1000);

    const now      = Date.now();
    const monthMs  = 30 * 24 * 3600 * 1000;
    const weekMs   =  7 * 24 * 3600 * 1000;

    // Commits ce mois / mois dernier
    const commitsThisMonth = commits.filter(c => now - new Date(c.date) < monthMs).length;
    const commitsLastMonth = commits.filter(c => { const a = now - new Date(c.date); return a >= monthMs && a < 2*monthMs; }).length;
    const deltaCommits = commitsLastMonth > 0
      ? (commitsThisMonth >= commitsLastMonth ? '↑ +' : '↓ ')
        + Math.abs(Math.round((commitsThisMonth - commitsLastMonth) / commitsLastMonth * 100)) + '% vs mois dernier'
      : commitsThisMonth > 0 ? '↑ actif ce mois' : '— aucun commit récent';

    // Issues
    const issuesClosed   = events.filter(e => e.type === 'IssuesEvent' && e.payload.action === 'closed').length;
    const issuesThisWeek = events.filter(e => e.type === 'IssuesEvent' && e.payload.action === 'closed' && now - new Date(e.created_at) < weekMs).length;
    const deltaIssues    = issuesThisWeek > 0 ? `↑ +${issuesThisWeek} cette semaine` : '— aucune récente';

    // Barres hebdo
    const buckets = Array(8).fill(0);
    commits.forEach(c => {
      const wi = Math.floor((now - new Date(c.date)) / weekMs);
      if (wi < 8) buckets[7 - wi]++;
    });
    const maxB = Math.max(...buckets, 1);

    // Feed activité
    const activity = events
      .filter(e => ['PushEvent','CreateEvent','IssuesEvent','PullRequestEvent','ForkEvent','WatchEvent','ReleaseEvent'].includes(e.type))
      .slice(0, 5)
      .map(e => {
        const repo = e.repo.name.replace(USER + '/', '');
        const map = {
          PushEvent:        { icon: '↑', color: '#8B5CF6', msg: `push → ${repo}` },
          CreateEvent:      { icon: '✦', color: '#4ade80', msg: `créé ${e.payload.ref_type} ${e.payload.ref ? '"'+e.payload.ref+'"' : ''} → ${repo}` },
          IssuesEvent:      { icon: '◆', color: '#f9a8d4', msg: `issue ${e.payload.action} → ${repo}` },
          PullRequestEvent: { icon: '⇄', color: '#7dd3fc', msg: `PR ${e.payload.action} → ${repo}` },
          ForkEvent:        { icon: '⑂', color: '#fbbf24', msg: `fork de ${repo}` },
          WatchEvent:       { icon: '★', color: '#fbbf24', msg: `starred ${repo}` },
          ReleaseEvent:     { icon: '◉', color: '#4ade80', msg: `release ${e.payload.release?.tag_name || ''} → ${repo}` },
        };
        return { ...map[e.type], time: timeAgo(e.created_at) };
      });

    $body.innerHTML = `
      <!-- Stat cards -->
      <div class="gh-dash-row">
        <div class="gh-dash-card">
          <div class="gh-dc-label">Commits / mois</div>
          <div class="gh-dc-val" id="ghCommitVal">0</div>
          <div class="gh-dc-delta">${deltaCommits}</div>
        </div>
        <div class="gh-dash-card">
          <div class="gh-dc-label">Issues fermées</div>
          <div class="gh-dc-val" id="ghIssueVal">0</div>
          <div class="gh-dc-delta">${deltaIssues}</div>
        </div>
      </div>

      <!-- Feed activité -->
      <div>
        <div class="gh-section-label">// Activité · temps réel</div>
        <div class="gh-activity">
          ${activity.length ? activity.map((a, i) => `
            <div class="gh-act-item" style="animation-delay:${i * 0.07}s">
              <span class="gh-act-icon" style="color:${a.color}">${a.icon}</span>
              <span class="gh-act-msg">${a.msg}</span>
              <span class="gh-act-time">${a.time}</span>
            </div>`).join('')
          : '<div style="font-family:var(--mono);font-size:.62rem;color:var(--muted);padding:8px 0">Aucune activité récente.</div>'}
        </div>
      </div>

      <!-- Langages -->
      ${langs.length ? `
      <div>
        <div class="gh-section-label">// Langages · ${repos.length} repos</div>
        <div class="gh-lang-bar">
          ${langs.map(l => `<div class="gh-lang-seg" style="flex:${l.pct};background:${langColor(l.lang)}" title="${l.lang} ${l.pct}%"></div>`).join('')}
        </div>
        <div class="gh-lang-legend">
          ${langs.map(l => `<div class="gh-ll"><span class="gh-ll-dot" style="background:${langColor(l.lang)}"></span>${l.lang} <span style="color:var(--purple)">${l.pct}%</span></div>`).join('')}
        </div>
      </div>` : ''}
    `;

    setTimeout(() => {
      animNum(document.getElementById('ghCommitVal'), commitsThisMonth);
      animNum(document.getElementById('ghIssueVal'), issuesClosed);
    }, 150);
  }

  function renderError(msg) {
    const $body = document.getElementById('ghBody');
    if ($body) $body.innerHTML = `<div class="gh-error">${msg}</div>`;
  }

  fetchAll().then(render).catch(() => {
    renderError('Impossible de charger les données.<br><span style="color:var(--muted)">Limite API ou réseau.</span>');
  });
})();

/* ── SCROLL REVEAL + COUNTERS ────────────────────────────────── */
let constellationResize = null;

function animCounter(el) {
  const target = +el.dataset.target;
  const start = performance.now();
  const dur = 1400;
  (function step(now) {
    const p = Math.min((now - start) / dur, 1);
    const ease = 1 - Math.pow(2, -10 * p);
    el.textContent = Math.round(ease * target);
    if (p < 1) requestAnimationFrame(step);
    else el.textContent = target;
  })(performance.now());
}

const revealObs = new IntersectionObserver(entries => {
  entries.forEach(e => {
    if (!e.isIntersecting) return;
    e.target.classList.add('visible');
    e.target.querySelectorAll('.counter').forEach(c => {
      if (!c.dataset.done) { c.dataset.done = '1'; animCounter(c); }
    });
    if (e.target.classList.contains('constellation-wrap') && constellationResize) {
      requestAnimationFrame(() => constellationResize());
    }
  });
}, { threshold: 0.08 });

document.querySelectorAll('.reveal').forEach(r => revealObs.observe(r));

/* ── CONSTELLATION CANVAS ────────────────────────────────────── */
(function initConstellation() {
  const canvas = document.getElementById('constellation');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  let W = 0, H = 0;

  const NODES = [
    { id:'kv',    label:'KainoVaii', kind:'center',   parent:null, r:40 },
    { id:'be',    label:'Backend',   kind:'cat',      parent:'kv', r:26, color:'#8B5CF6' },
    { id:'fe',    label:'Frontend',  kind:'cat',      parent:'kv', r:26, color:'#7C3AED' },
    { id:'to',    label:'Outils',    kind:'cat',      parent:'kv', r:26, color:'#6D28D9' },
    { id:'java',  label:'Java',      kind:'leaf', parent:'be', r:20, lvl:5 },
    { id:'spark', label:'Spark',     kind:'leaf', parent:'be', r:18, lvl:5 },
    { id:'obsid', label:'Obsidian◈', kind:'leaf', parent:'be', r:22, lvl:5, star:true },
    { id:'ajdbc', label:'ActiveJDBC',kind:'leaf', parent:'be', r:16, lvl:4 },
    { id:'pebb',  label:'Pebble',    kind:'leaf', parent:'be', r:15, lvl:4 },
    { id:'html',  label:'HTML/CSS',  kind:'leaf', parent:'fe', r:18, lvl:5 },
    { id:'js',    label:'JS',        kind:'leaf', parent:'fe', r:17, lvl:4 },
    { id:'tw',    label:'Tailwind',  kind:'leaf', parent:'fe', r:15, lvl:4 },
    { id:'git',   label:'Git',       kind:'leaf', parent:'to', r:16, lvl:5 },
    { id:'mvn',   label:'Maven',     kind:'leaf', parent:'to', r:15, lvl:5 },
    { id:'ij',    label:'IntelliJ',  kind:'leaf', parent:'to', r:15, lvl:4 },
    { id:'sql',   label:'SQL',       kind:'leaf', parent:'to', r:15, lvl:4 },
    { id:'dck',   label:'Docker',    kind:'leaf', parent:'to', r:14, lvl:3 },
    { id:'lnx',   label:'Linux',     kind:'leaf', parent:'to', r:14, lvl:4 },
  ];

  // State
  let nodes = NODES.map(d => ({ ...d, x:0, y:0, dx:0, dy:0 }));
  let dragging = null, hovered = null;
  let dragOx = 0, dragOy = 0;

  // Float params per node
  const floatP = {};
  nodes.forEach(n => {
    floatP[n.id] = {
      phase: Math.random() * Math.PI * 2,
      amp:   n.kind === 'center' ? 2.5 : n.kind === 'cat' ? 5 : 7,
      speed: 0.28 + Math.random() * 0.35
    };
  });

  // Edge pulse particles
  const pulses = [];
  function spawnPulses() {
    nodes.forEach(n => {
      if (!n.parent) return;
      const parent = nodes.find(p => p.id === n.parent);
      if (!parent) return;
      if (Math.random() < 0.04) {
        pulses.push({ from: parent, to: n, t: 0, speed: 0.003 + Math.random() * 0.004 });
      }
    });
    // Keep max 18
    while (pulses.length > 18) pulses.shift();
  }

  function resize() {
    const wrap = canvas.parentElement;
    W = canvas.width  = wrap.offsetWidth  || 800;
    H = canvas.height = wrap.offsetHeight || 560;
    placeNodes();
  }
  constellationResize = resize;

  function placeNodes() {
    const cx = W / 2, cy = H / 2;
    const R1 = Math.min(W, H) * 0.195;
    const R2 = Math.min(W, H) * 0.385;
    const catAngles = { be: -Math.PI/2, fe: Math.PI*7/6, to: Math.PI/6 + 0.1 };

    nodes.forEach(n => {
      if (n.kind === 'center') { n.x = cx; n.y = cy; }
      else if (n.kind === 'cat') {
        const a = catAngles[n.id];
        n.x = cx + Math.cos(a) * R1;
        n.y = cy + Math.sin(a) * R1;
      } else {
        const siblings = NODES.filter(s => s.parent === n.parent);
        const idx = siblings.indexOf(NODES.find(d => d.id === n.id));
        const total = siblings.length;
        const pa = catAngles[n.parent];
        const spread = Math.PI * 0.72;
        const a = pa - spread/2 + (spread / Math.max(total-1, 1)) * idx;
        const jitter = (Math.random() - 0.5) * 22;
        n.x = cx + Math.cos(a) * (R2 + jitter);
        n.y = cy + Math.sin(a) * (R2 + jitter);
      }
      n.dx = n.x; n.dy = n.y;
    });
  }

  function getNode(id) { return nodes.find(n => n.id === id); }

  function drawEdge(a, b, alpha) {
    if (!a || !b) return;
    const ax = a.dx ?? a.x, ay = a.dy ?? a.y;
    const bx = b.dx ?? b.x, by = b.dy ?? b.y;
    const g = ctx.createLinearGradient(ax, ay, bx, by);
    g.addColorStop(0, `rgba(139,92,246,${alpha})`);
    g.addColorStop(1, `rgba(109,40,217,${alpha * 0.25})`);
    ctx.beginPath();
    ctx.moveTo(ax, ay);
    ctx.lineTo(bx, by);
    ctx.strokeStyle = g;
    ctx.lineWidth = a.kind === 'center' ? 1.5 : 0.75;
    ctx.stroke();
  }

  function drawPulse(p) {
    const ax = p.from.dx ?? p.from.x, ay = p.from.dy ?? p.from.y;
    const bx = p.to.dx  ?? p.to.x,   by = p.to.dy  ?? p.to.y;
    const x = ax + (bx - ax) * p.t;
    const y = ay + (by - ay) * p.t;
    const g = ctx.createRadialGradient(x, y, 0, x, y, 6);
    g.addColorStop(0, 'rgba(196,181,253,0.95)');
    g.addColorStop(1, 'transparent');
    ctx.beginPath();
    ctx.arc(x, y, 6, 0, Math.PI * 2);
    ctx.fillStyle = g;
    ctx.fill();
  }

  function drawNode(n, t) {
    const fp = floatP[n.id];
    const isDragged = n === dragging;
    const floatX = isDragged ? 0 : Math.cos(t * fp.speed * 0.7 + fp.phase) * fp.amp * 0.5;
    const floatY = isDragged ? 0 : Math.sin(t * fp.speed + fp.phase) * fp.amp;
    n.dx = n.x + floatX;
    n.dy = n.y + floatY;
    const nx = n.dx, ny = n.dy;

    const isHov = n === hovered;
    const r = n.r + (isHov ? 4 : 0);

    // Outer glow
    const glowMult = n.kind === 'center' ? 3.8 : isHov ? 3 : 2.2;
    const glowAlpha = n.kind === 'center' ? 0.22 : n.star ? 0.28 : (isHov ? 0.2 : 0.08);
    if (glowAlpha > 0.06) {
      const gr = ctx.createRadialGradient(nx, ny, 0, nx, ny, r * glowMult);
      gr.addColorStop(0, `rgba(139,92,246,${glowAlpha})`);
      gr.addColorStop(1, 'transparent');
      ctx.beginPath();
      ctx.arc(nx, ny, r * glowMult, 0, Math.PI * 2);
      ctx.fillStyle = gr;
      ctx.fill();
    }

    // Fill
    ctx.beginPath();
    ctx.arc(nx, ny, r, 0, Math.PI * 2);
    if (n.kind === 'center') {
      const gf = ctx.createRadialGradient(nx - r * .3, ny - r * .3, 0, nx, ny, r);
      gf.addColorStop(0, '#c4b5fd');
      gf.addColorStop(1, '#6d28d9');
      ctx.fillStyle = gf;
    } else if (n.kind === 'cat') {
      ctx.fillStyle = 'rgba(139,92,246,0.18)';
    } else {
      ctx.fillStyle = n.star ? 'rgba(167,139,250,0.20)' : 'rgba(20,14,40,0.85)';
    }
    ctx.fill();

    // Border
    ctx.beginPath();
    ctx.arc(nx, ny, r, 0, Math.PI * 2);
    ctx.strokeStyle = n.kind === 'center' ? '#c4b5fd'
      : isHov  ? '#8B5CF6'
      : n.star ? 'rgba(167,139,250,0.7)'
      : n.kind === 'cat' ? 'rgba(139,92,246,0.6)'
      : 'rgba(139,92,246,0.28)';
    ctx.lineWidth = n.kind === 'center' ? 2 : 1;
    ctx.stroke();

    // Label
    const fs = n.kind === 'center' ? 13 : n.kind === 'cat' ? 11 : 9.5;
    const fw = (n.kind === 'center' || n.star) ? '600' : '400';
    ctx.font = `${fw} ${fs}px 'JetBrains Mono', monospace`;
    ctx.fillStyle = n.kind === 'center' ? '#fff'
      : isHov ? '#e2d9f3'
      : 'rgba(240,238,255,0.65)';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(n.label, nx, ny);

    // Level pips (leaf only)
    if (n.kind === 'leaf' && n.lvl) {
      const py = ny + r + 9;
      const spacing = 7;
      const totalW = 4 * spacing;
      for (let i = 0; i < 5; i++) {
        const px = nx - totalW / 2 + i * spacing;
        ctx.beginPath();
        ctx.arc(px, py, 2, 0, Math.PI * 2);
        ctx.fillStyle = i < n.lvl ? '#8B5CF6' : 'rgba(139,92,246,0.18)';
        ctx.fill();
      }
    }
  }

  let tick = 0;
  function draw() {
    ctx.clearRect(0, 0, W, H);
    tick += 0.016;

    // Edges
    nodes.forEach(n => {
      if (!n.parent) return;
      const parent = getNode(n.parent);
      drawEdge(parent, n, n.kind === 'leaf' ? 0.22 : 0.5);
    });

    // Pulses
    spawnPulses();
    for (let i = pulses.length - 1; i >= 0; i--) {
      const p = pulses[i];
      p.t += p.speed;
      if (p.t > 1) { pulses.splice(i, 1); continue; }
      drawPulse(p);
    }

    // Nodes (leaves first, center last)
    const order = { leaf: 0, cat: 1, center: 2 };
    [...nodes].sort((a, b) => order[a.kind] - order[b.kind])
      .forEach(n => drawNode(n, tick));

    requestAnimationFrame(draw);
  }

  // Hit testing
  function hitNode(ex, ey) {
    for (let i = nodes.length - 1; i >= 0; i--) {
      const n = nodes[i];
      if (Math.hypot(ex - (n.dx ?? n.x), ey - (n.dy ?? n.y)) < n.r + 10) return n;
    }
    return null;
  }

  function pos(e) {
    const r = canvas.getBoundingClientRect();
    const s = e.touches ? e.touches[0] : e;
    return { ex: s.clientX - r.left, ey: s.clientY - r.top };
  }

  canvas.addEventListener('mousedown', e => {
    const { ex, ey } = pos(e);
    const hit = hitNode(ex, ey);
    if (hit) { dragging = hit; dragOx = ex - hit.x; dragOy = ey - hit.y; }
  });
  canvas.addEventListener('mousemove', e => {
    const { ex, ey } = pos(e);
    if (dragging) {
      dragging.x = ex - dragOx;
      dragging.y = ey - dragOy;
      canvas.style.cursor = 'grabbing';
    } else {
      hovered = hitNode(ex, ey);
      canvas.style.cursor = hovered ? 'grab' : 'default';
    }
  });
  canvas.addEventListener('mouseup',    () => { dragging = null; });
  canvas.addEventListener('mouseleave', () => { dragging = null; hovered = null; });
  canvas.addEventListener('touchstart', e => {
    e.preventDefault();
    const { ex, ey } = pos(e);
    const hit = hitNode(ex, ey);
    if (hit) { dragging = hit; dragOx = ex - hit.x; dragOy = ey - hit.y; }
  }, { passive: false });
  canvas.addEventListener('touchmove', e => {
    e.preventDefault();
    if (!dragging) return;
    const { ex, ey } = pos(e);
    dragging.x = ex - dragOx; dragging.y = ey - dragOy;
  }, { passive: false });
  canvas.addEventListener('touchend', () => { dragging = null; });

  window.addEventListener('resize', resize);
  requestAnimationFrame(resize);
  draw();
})();