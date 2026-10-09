"use strict";

const G = DATA.garden;
const MAXD = G.maxRoadDistance + 1;
const RINGS = MAXD - 1;
const SIZE = MAXD * 2 + 1;
const LAYER_NAMES = ["Ground", "Middle", "Top"];
const TYPE_LABEL = { earthen: "Earthen", plant: "Plant", crop: "Crop", tree: "Tree", water: "Water", fire: "Fire", lava: "Lava" };
const TYPE_COLOR = Object.fromEntries(G.types.map((t) => [t.type, t.color]));
const PATH_COLORS = { "minecraft:dirt_path": "#9a7b4c", "minecraft:stone_bricks": "#7c7b7a" };

const GBRUSHES = [{ key: "air", kind: "air", name: "Air" }];
for (const p of G.paths) {
  GBRUSHES.push({ key: `path:${p.id}`, kind: "path", id: p.id, name: p.name, level: p.level, texture: p.texture, color: PATH_COLORS[p.id] || "#8a8079" });
}
for (const b of G.blocks) {
  GBRUSHES.push({
    key: b.key,
    kind: "tranquility",
    type: b.type,
    value: b.value,
    name: `${TYPE_LABEL[b.type]}${b.value !== 1 ? ` (${fmt(b.value, 2)} each)` : ""}`,
    examples: b.examples,
    texture: b.texture,
    color: b.color,
  });
}
const GB = Object.fromEntries(GBRUSHES.map((b) => [b.key, b]));
const CHARS = "-abcdefghijklmnopqrstuvwxyz";

const state = {
  cells: {},
  brush: (GBRUSHES.find((b) => b.kind === "path" && b.texture) || GBRUSHES[1]).key,
  view: "0",
  pathRings: RINGS,
  hover: null,
};

const ck = (x, z, l) => `${x},${z},${l}`;
const cheb = (x, z) => Math.max(Math.abs(x), Math.abs(z));

function isPathCell(x, z) {
  const d = cheb(x, z);
  if (d < 2) return false;
  return (Math.abs(x) === d && Math.abs(z) <= 1) || (Math.abs(z) === d && Math.abs(x) <= 1);
}

function ringCells(d) {
  const out = [];
  for (let x = -d; x <= d; x++) {
    for (let z = -d; z <= d; z++) {
      if (cheb(x, z) === d) out.push([x, z]);
    }
  }
  return out;
}

function pathCellsOf(d) {
  return ringCells(d).filter(([x, z]) => isPathCell(x, z));
}

function cellBrush(x, z, l) {
  const k = state.cells[ck(x, z, l)];
  return k && GB[k] ? GB[k] : null;
}

function setCell(x, z, l, key) {
  const k = ck(x, z, l);
  if (key === "air" || !GB[key]) delete state.cells[k];
  else state.cells[k] = key;
}

function bonusFromRoads(roads) {
  let prev = 0;
  for (let i = 0; i < G.bonuses.length; i++) {
    if (roads >= G.roadsRequired[i]) prev = G.bonuses[i];
    else return prev;
  }
  return prev;
}

function bonusFromTranquility(t) {
  let possible = 0;
  for (let i = 0; i < G.bonuses.length; i++) {
    if (t >= G.tranquilityRequired[i]) {
      possible = G.bonuses[i];
    } else if (i >= 1) {
      const pr = G.tranquilityRequired[i - 1];
      const cr = G.tranquilityRequired[i];
      possible = G.bonuses[i - 1] + ((G.bonuses[i] - G.bonuses[i - 1]) * (t - pr)) / (cr - pr);
      break;
    }
  }
  return possible;
}

function applied(totals) {
  return Object.values(totals).reduce((a, v) => a + Math.sqrt(v), 0);
}

function evaluate() {
  let rings = 0;
  let fail = null;
  for (let d = 2; d <= MAXD; d++) {
    const need = d - 2;
    const missing = pathCellsOf(d).filter(([x, z]) => {
      const b = cellBrush(x, z, 0);
      return !(b && b.kind === "path" && b.level >= need);
    });
    if (missing.length) {
      fail = { d, ring: d - 1, need, missing };
      break;
    }
    rings++;
  }
  const totals = {};
  const counts = {};
  for (const [k, key] of Object.entries(state.cells)) {
    const b = GB[key];
    if (!b || b.kind !== "tranquility") continue;
    const [x, z] = k.split(",").map(Number);
    const d = cheb(x, z);
    if (d < 2 || d >= 2 + rings) continue;
    totals[b.type] = (totals[b.type] || 0) + b.value;
    counts[b.type] = (counts[b.type] || 0) + 1;
  }
  const t = applied(totals);
  const roadCap = bonusFromRoads(rings);
  const tranqBonus = bonusFromTranquility(t);
  return { rings, fail, totals, counts, tranquility: t, roadCap, tranqBonus, bonus: Math.min(roadCap, tranqBonus) };
}

function blocksToReach(totals, target) {
  const work = { ...totals };
  for (const ty of Object.keys(TYPE_LABEL)) work[ty] = work[ty] || 0;
  let t = applied(work);
  let n = 0;
  while (t < target && n < 5000) {
    let best = null;
    let gain = -1;
    for (const ty of Object.keys(work)) {
      const g = Math.sqrt(work[ty] + 1) - Math.sqrt(work[ty]);
      if (g > gain) {
        gain = g;
        best = ty;
      }
    }
    work[best] += 1;
    t += gain;
    n++;
  }
  return n;
}

const images = new Map();
function image(key) {
  if (!key) return null;
  if (images.has(key)) return images.get(key);
  const img = new Image();
  img.onload = draw;
  img.src = texSrc(key);
  images.set(key, img);
  return img;
}

const canvas = $("garden");
const ctx = canvas.getContext("2d");
let cellPx = 22;
let lastEval = null;

function resizeCanvas() {
  const host = $("garden-host");
  const avail = Math.min(host.clientWidth, 720);
  cellPx = Math.max(10, Math.floor(avail / SIZE));
  const css = cellPx * SIZE;
  const dpr = Math.min(window.devicePixelRatio || 1, 2);
  canvas.style.width = `${css}px`;
  canvas.style.height = `${css}px`;
  canvas.width = css * dpr;
  canvas.height = css * dpr;
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  draw();
}

function drawBrush(b, px, py, size) {
  const img = image(b.texture);
  if (img && img.complete && img.naturalWidth) {
    ctx.imageSmoothingEnabled = false;
    const sh = Math.min(img.naturalWidth, img.naturalHeight);
    ctx.drawImage(img, 0, 0, img.naturalWidth, sh, px, py, size, size);
    return;
  }
  ctx.fillStyle = b.color || "#666";
  ctx.fillRect(px, py, size, size);
  if (b.kind === "path") {
    ctx.strokeStyle = "rgba(0,0,0,0.28)";
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(px, py + size / 2);
    ctx.lineTo(px + size, py + size / 2);
    ctx.moveTo(px + size / 2, py);
    ctx.lineTo(px + size / 2, py + size / 2);
    ctx.stroke();
  } else if (size >= 12) {
    ctx.fillStyle = "rgba(255,255,255,0.85)";
    ctx.font = `600 ${Math.floor(size * 0.55)}px "IBM Plex Sans", sans-serif`;
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillText(TYPE_LABEL[b.type].charAt(0), px + size / 2, py + size / 2 + 1);
  }
}

function draw() {
  if (!lastEval) return;
  const s = cellPx;
  const ev = lastEval;
  ctx.clearRect(0, 0, s * SIZE, s * SIZE);
  ctx.fillStyle = "#140d0f";
  ctx.fillRect(0, 0, s * SIZE, s * SIZE);
  const failMissing = new Set(ev.fail ? ev.fail.missing.map(([x, z]) => `${x},${z}`) : []);
  for (let z = -MAXD; z <= MAXD; z++) {
    for (let x = -MAXD; x <= MAXD; x++) {
      const px = (x + MAXD) * s;
      const py = (z + MAXD) * s;
      const d = cheb(x, z);
      if (d === 0) {
        const img = image(G.incenseAltarTexture);
        if (img && img.complete && img.naturalWidth) {
          ctx.imageSmoothingEnabled = false;
          ctx.drawImage(img, px, py, s, s);
        } else {
          ctx.fillStyle = "#c0283a";
          ctx.fillRect(px, py, s, s);
        }
        continue;
      }
      if (d === 1) {
        ctx.fillStyle = "#0b0708";
        ctx.fillRect(px, py, s, s);
        continue;
      }
      ctx.fillStyle = d % 2 ? "#221619" : "#1d1315";
      ctx.fillRect(px, py, s, s);
      if (state.view === "all") {
        const g = cellBrush(x, z, 0);
        const m = cellBrush(x, z, 1);
        const t = cellBrush(x, z, 2);
        if (g) drawBrush(g, px, py, s);
        if (m) drawBrush(m, px + s * 0.18, py + s * 0.18, s * 0.64);
        if (t) drawBrush(t, px + s * 0.34, py + s * 0.34, s * 0.32);
      } else {
        const l = Number(state.view);
        const b = cellBrush(x, z, l);
        if (l > 0) {
          const g = cellBrush(x, z, 0);
          if (g) {
            ctx.globalAlpha = 0.3;
            drawBrush(g, px, py, s);
            ctx.globalAlpha = 1;
          }
        }
        if (b) drawBrush(b, px, py, s);
      }
      if (d >= 2 + ev.rings) {
        ctx.fillStyle = "rgba(8,4,5,0.55)";
        ctx.fillRect(px, py, s, s);
      }
      if (isPathCell(x, z)) {
        const missing = failMissing.has(`${x},${z}`);
        ctx.lineWidth = missing ? 2 : 1.25;
        ctx.strokeStyle = missing ? "#e2475a" : d < 2 + ev.rings ? "#d4a64a" : "rgba(212,166,74,0.45)";
        ctx.setLineDash(missing || d < 2 + ev.rings ? [] : [3, 3]);
        ctx.strokeRect(px + 1, py + 1, s - 2, s - 2);
        ctx.setLineDash([]);
      }
    }
  }
  ctx.strokeStyle = "rgba(255,255,255,0.05)";
  ctx.lineWidth = 1;
  ctx.beginPath();
  for (let i = 0; i <= SIZE; i++) {
    ctx.moveTo(i * s + 0.5, 0);
    ctx.lineTo(i * s + 0.5, SIZE * s);
    ctx.moveTo(0, i * s + 0.5);
    ctx.lineTo(SIZE * s, i * s + 0.5);
  }
  ctx.stroke();
  if (state.hover) {
    const [x, z] = state.hover;
    ctx.strokeStyle = "#ffffff";
    ctx.lineWidth = 2;
    ctx.strokeRect((x + MAXD) * s + 1, (z + MAXD) * s + 1, s - 2, s - 2);
  }
}

function cellAt(e) {
  const rect = canvas.getBoundingClientRect();
  const x = Math.floor((e.clientX - rect.left) / cellPx) - MAXD;
  const z = Math.floor((e.clientY - rect.top) / cellPx) - MAXD;
  if (Math.abs(x) > MAXD || Math.abs(z) > MAXD) return null;
  return [x, z];
}

function targetLayer(b) {
  if (b.kind === "path") return 0;
  if (state.view !== "all") return Number(state.view);
  if (b.kind === "tranquility" && ["earthen", "water", "lava"].includes(b.type)) return 0;
  return 1;
}

function applyAt(cell, erase) {
  const [x, z] = cell;
  if (cheb(x, z) < 2) return false;
  if (erase) {
    if (state.view === "all") {
      for (let l = 2; l >= 0; l--) {
        if (cellBrush(x, z, l)) {
          setCell(x, z, l, "air");
          return true;
        }
      }
      return false;
    }
    if (!cellBrush(x, z, Number(state.view))) return false;
    setCell(x, z, Number(state.view), "air");
    return true;
  }
  const b = GB[state.brush];
  if (b.kind === "air") return applyAt(cell, true);
  const l = targetLayer(b);
  if (state.cells[ck(x, z, l)] === b.key) return false;
  setCell(x, z, l, b.key);
  return true;
}

let dragging = null;

canvas.addEventListener("pointerdown", (e) => {
  const cell = cellAt(e);
  if (!cell) return;
  e.preventDefault();
  canvas.setPointerCapture(e.pointerId);
  dragging = { erase: e.button === 2, last: null };
  if (applyAt(cell, dragging.erase)) update();
  dragging.last = cell.join(",");
});

canvas.addEventListener("pointermove", (e) => {
  const cell = cellAt(e);
  state.hover = cell;
  showTip(cell, e);
  if (dragging && cell && cell.join(",") !== dragging.last) {
    dragging.last = cell.join(",");
    if (applyAt(cell, dragging.erase)) {
      update();
      return;
    }
  }
  draw();
});

canvas.addEventListener("pointerup", () => {
  dragging = null;
});
canvas.addEventListener("pointerleave", () => {
  state.hover = null;
  $("tooltip").hidden = true;
  draw();
});
canvas.addEventListener("contextmenu", (e) => e.preventDefault());

function showTip(cell, e) {
  const tip = $("tooltip");
  if (!cell) {
    tip.hidden = true;
    return;
  }
  const [x, z] = cell;
  const d = cheb(x, z);
  let text;
  if (d === 0) text = "Incense Altar";
  else if (d === 1) text = "Next to the altar, not counted";
  else {
    const parts = [0, 1, 2].map((l) => {
      const b = cellBrush(x, z, l);
      return `${LAYER_NAMES[l]}: ${b ? b.name : "empty"}`;
    });
    text = `Ring ${d - 1}${isPathCell(x, z) ? `, path spot (level ${d - 2}+)` : ""}. ${parts.join(". ")}`;
    if (lastEval && d >= 2 + lastEval.rings) text += ". Not counted: an inner ring is incomplete";
  }
  tip.textContent = text;
  tip.hidden = false;
  const rect = $("garden-host").getBoundingClientRect();
  tip.style.left = `${Math.min(e.clientX - rect.left + 14, rect.width - 260)}px`;
  tip.style.top = `${e.clientY - rect.top + 14}px`;
}

function buildViewButtons() {
  const views = [
    ["0", "Ground"],
    ["1", "Middle"],
    ["2", "Top"],
    ["all", "All layers"],
  ];
  $("view-buttons").innerHTML = views.map(([v, label]) => `<button type="button" data-view="${v}" aria-pressed="false">${label}</button>`).join("");
  $("view-buttons").addEventListener("click", (e) => {
    const b = e.target.closest("button[data-view]");
    if (!b) return;
    state.view = b.dataset.view;
    update();
  });
}

function brushButton(b) {
  const sw = texSrc(b.texture)
    ? `<img class="px" src="${texSrc(b.texture)}" alt="">`
    : `<span class="swatch" style="background:${b.color}"></span>`;
  const sub =
    b.kind === "path"
      ? `Level ${b.level}, reaches ring ${Math.min(b.level + 1, RINGS)}`
      : `${esc(b.examples.slice(0, 4).join(", "))}${b.examples.length > 4 ? `, and ${b.examples.length - 4} more` : ""}`;
  return `<button type="button" class="gbrush" data-brush="${b.key}" aria-pressed="false" title="${esc(b.kind === "tranquility" ? b.examples.join(", ") : b.name)}">
    ${sw}<span class="label"><b>${esc(b.name)}</b><small>${sub}</small></span><span class="count mono"></span>
  </button>`;
}

function buildPalette() {
  $("path-brushes").innerHTML = GBRUSHES.filter((b) => b.kind === "path").map(brushButton).join("");
  $("tranq-brushes").innerHTML =
    GBRUSHES.filter((b) => b.kind === "tranquility").map(brushButton).join("") +
    `<button type="button" class="gbrush" data-brush="air" aria-pressed="false"><span class="swatch air"></span><span class="label"><b>Air (eraser)</b><small>Clears the cell on the chosen layer</small></span><span class="count mono"></span></button>`;
  for (const id of ["path-brushes", "tranq-brushes"]) {
    $(id).addEventListener("click", (e) => {
      const b = e.target.closest("button[data-brush]");
      if (!b) return;
      state.brush = b.dataset.brush;
      update();
    });
  }
  $("path-rings").innerHTML = Array.from({ length: RINGS }, (_, i) => `<option value="${i + 1}">${i + 1}</option>`).join("");
}

function layPaths() {
  const b = GB[state.brush].kind === "path" ? GB[state.brush] : GBRUSHES.find((x) => x.kind === "path" && x.texture) || GBRUSHES.find((x) => x.kind === "path");
  const n = state.pathRings;
  for (let d = 2; d <= n + 1; d++) {
    const best = b.level >= d - 2 ? b : GBRUSHES.filter((x) => x.kind === "path" && x.level >= d - 2).sort((p, q) => p.level - q.level)[0];
    if (!best) continue;
    for (const [x, z] of pathCellsOf(d)) setCell(x, z, 0, best.key);
  }
  update();
  toast(`Paths laid for ${n} ring${n === 1 ? "" : "s"}`);
}

function fillRing(d) {
  const b = GB[state.brush];
  const l = b.kind === "air" ? (state.view === "all" ? null : Number(state.view)) : targetLayer(b);
  for (const [x, z] of ringCells(d)) {
    if (b.kind === "air") {
      for (const layer of l === null ? [0, 1, 2] : [l]) setCell(x, z, layer, "air");
      continue;
    }
    if (l === 0 && isPathCell(x, z) && b.kind !== "path") continue;
    if (b.kind === "path" && !isPathCell(x, z)) continue;
    setCell(x, z, l, b.key);
  }
  update();
}

function renderPaletteState() {
  const used = {};
  for (const key of Object.values(state.cells)) used[key] = (used[key] || 0) + 1;
  for (const btn of document.querySelectorAll(".gbrush")) {
    btn.setAttribute("aria-pressed", String(btn.dataset.brush === state.brush));
    const n = used[btn.dataset.brush] || 0;
    btn.querySelector(".count").textContent = btn.dataset.brush === "air" || !n ? "" : n;
  }
  for (const b of $("view-buttons").querySelectorAll("button")) b.setAttribute("aria-pressed", String(b.dataset.view === state.view));
  const brush = GB[state.brush];
  $("layer-hint").textContent =
    brush.kind === "air"
      ? "Erasing"
      : `Placing ${brush.name} on ${LAYER_NAMES[targetLayer(brush)]}${brush.kind === "path" && state.view !== "0" ? " (paths always go on Ground)" : ""}`;
  $("path-rings").value = String(state.pathRings);
  $("ring-tools").innerHTML = `<span class="ring-label">Fill ring</span>${Array.from({ length: RINGS }, (_, i) => `<button type="button" class="ghost small" data-ring="${i + 2}">${i + 1}</button>`).join("")}`;
}

function stat(label, value, unit, detail, accent) {
  return `<div class="stat${accent ? " accent" : ""}"><div class="k">${label}</div><div class="v">${value}${unit ? `<span class="u">${unit}</span>` : ""}</div>${detail ? `<div class="d">${detail}</div>` : ""}</div>`;
}

function renderResults(ev) {
  const nextRoad = G.roadsRequired.find((r, i) => i < G.bonuses.length && r > ev.rings);
  const hp = 20;
  const ceremonialHp = Math.max(1, Math.trunc(hp - hp / 10));
  const ceremonial = Math.trunc(ceremonialHp * DATA.constants.selfSacrificeConversion * (1 + ev.bonus));
  $("garden-stats").innerHTML = [
    stat("Incense bonus", pct(ev.bonus), "", `self sacrifice ${mult(1 + ev.bonus)} while incense is lit`, true),
    stat("Path rings", `${ev.rings} / ${RINGS}`, "", ev.fail ? `ring ${ev.fail.ring} is missing ${ev.fail.missing.length} path block${ev.fail.missing.length === 1 ? "" : "s"}` : "every ring is complete"),
    stat("Ring cap", pct(ev.roadCap), "", nextRoad ? `${nextRoad} rings raise it to ${pct(bonusFromRoads(nextRoad))}` : "at the highest cap"),
    stat("Tranquility", fmt(ev.tranquility, 2), "", `worth ${pct(ev.tranqBonus)} before the ring cap`),
    stat("Ceremonial sacrifice", fmt(ceremonial), "EV", `${ceremonialHp} health at 20 max health, before altar runes`),
    stat("Incense fills in", "5", "s", "standing within 5 blocks of the Incense Altar"),
  ].join("");

  const types = Object.keys(TYPE_LABEL);
  const rows = types
    .map((ty) => {
      const total = ev.totals[ty] || 0;
      const next = Math.sqrt(total + 1) - Math.sqrt(total);
      return `<tr>
        <td><span class="cell-item"><span class="swatch" style="background:${TYPE_COLOR[ty]}"></span>${TYPE_LABEL[ty]}</span></td>
        <td class="num">${fmt(ev.counts[ty] || 0)}</td>
        <td class="num">${fmt(total, 1)}</td>
        <td class="num">${fmt(Math.sqrt(total), 2)}</td>
        <td class="num">+${fmt(next, 2)}</td>
      </tr>`;
    })
    .join("");
  $("type-table").innerHTML = `<thead><tr><th>Type</th><th class="r">Blocks</th><th class="r">Total</th><th class="r">Adds</th><th class="r">Next block</th></tr></thead><tbody>${rows}</tbody>
    <tfoot><tr><td>All types</td><td class="num">${fmt(Object.values(ev.counts).reduce((a, b) => a + b, 0))}</td><td></td><td class="num">${fmt(ev.tranquility, 2)}</td><td></td></tr></tfoot>`;

  const tiers = G.bonuses
    .map((b, i) => {
      const reached = ev.bonus >= b - 1e-9;
      const tOk = ev.tranquility >= G.tranquilityRequired[i];
      const rOk = ev.rings >= G.roadsRequired[i];
      return `<tr class="${reached ? "reached" : ""}">
        <td class="num">${pct(b)}</td>
        <td class="num ${tOk ? "ok" : ""}">${fmt(G.tranquilityRequired[i], 2)}</td>
        <td class="num ${rOk ? "ok" : ""}">${G.roadsRequired[i]}</td>
        <td>${reached ? "Reached" : !rOk && !tOk ? "Needs both" : !rOk ? "Needs rings" : "Needs tranquility"}</td>
      </tr>`;
    })
    .join("");
  $("tier-table").innerHTML = `<thead><tr><th class="r">Bonus</th><th class="r">Tranquility</th><th class="r">Rings</th><th>Status</th></tr></thead><tbody>${tiers}</tbody>`;

  const alerts = [];
  const capRings = G.roadsRequired[G.bonuses.length - 1];
  const maxLevel = Math.max(...GBRUSHES.filter((b) => b.kind === "path").map((b) => b.level));
  if (ev.fail && ev.fail.need > maxLevel) {
    alerts.push(["ok", `Ring ${ev.fail.ring} would need level ${ev.fail.need} paths, which no block provides, so ${ev.rings} rings is the most a garden can have.`]);
  } else if (ev.fail && ev.rings >= capRings) {
    alerts.push(["ok", `${ev.rings} rings already reach the highest cap. More rings only add room for tranquility blocks.`]);
  } else if (ev.fail) {
    alerts.push(["warn", `Ring ${ev.fail.ring} needs path blocks on all 12 outlined spots (level ${ev.fail.need} or higher). ${ev.fail.missing.length} ${ev.fail.missing.length === 1 ? "is" : "are"} missing, so nothing from ring ${ev.fail.ring} outward counts.`]);
  }
  const idx = G.bonuses.findIndex((b) => b > ev.bonus + 1e-9);
  if (idx >= 0) {
    const needT = G.tranquilityRequired[idx];
    const needR = G.roadsRequired[idx];
    if (ev.tranquility < needT) {
      const n = blocksToReach(ev.totals, needT);
      alerts.push(["warn", `About ${fmt(n)} more tranquility blocks, spread across the types with the biggest "Next block" value, reach ${fmt(needT, 2)} tranquility for ${pct(G.bonuses[idx])}.`]);
    }
    if (ev.rings < needR) alerts.push(["warn", `${pct(G.bonuses[idx])} also needs ${needR} complete path rings. You have ${ev.rings}.`]);
  } else {
    alerts.push(["ok", "This garden gives the highest incense bonus."]);
  }
  $("garden-alerts").innerHTML = alerts.map(([k, t]) => `<div class="alert ${k}">${t}</div>`).join("");
}

function encodeGrid() {
  let out = "";
  let prev = null;
  let run = 0;
  const flush = () => {
    if (prev !== null) out += prev + (run > 1 ? run : "");
  };
  for (let l = 0; l < 3; l++) {
    for (let z = -MAXD; z <= MAXD; z++) {
      for (let x = -MAXD; x <= MAXD; x++) {
        if (cheb(x, z) < 2) continue;
        const key = state.cells[ck(x, z, l)] || "air";
        const c = CHARS[GBRUSHES.findIndex((b) => b.key === key)] || "-";
        if (c === prev) run++;
        else {
          flush();
          prev = c;
          run = 1;
        }
      }
    }
  }
  flush();
  return out.replace(/-\d*$/, "");
}

function decodeGrid(str) {
  const cells = [];
  for (let l = 0; l < 3; l++) {
    for (let z = -MAXD; z <= MAXD; z++) {
      for (let x = -MAXD; x <= MAXD; x++) {
        if (cheb(x, z) >= 2) cells.push([x, z, l]);
      }
    }
  }
  const re = /([-a-z])(\d*)/g;
  let i = 0;
  let m;
  while ((m = re.exec(str)) && i < cells.length) {
    const n = m[2] ? parseInt(m[2], 10) : 1;
    const b = GBRUSHES[CHARS.indexOf(m[1])];
    for (let k = 0; k < n && i < cells.length; k++, i++) {
      if (b && b.key !== "air") state.cells[ck(...cells[i])] = b.key;
    }
  }
}

function writeHash() {
  const p = new URLSearchParams();
  const g = encodeGrid();
  if (g) p.set("g", g);
  p.set("b", state.brush);
  if (state.view !== "0") p.set("v", state.view);
  history.replaceState(null, "", `#${p.toString()}`);
}

function readHash() {
  const p = new URLSearchParams(location.hash.slice(1));
  if (p.get("g")) decodeGrid(p.get("g"));
  if (GB[p.get("b")]) state.brush = p.get("b");
  if (["0", "1", "2", "all"].includes(p.get("v"))) state.view = p.get("v");
}

function exportPlan() {
  const cells = [];
  for (const [k, key] of Object.entries(state.cells)) {
    const [x, z, layer] = k.split(",").map(Number);
    const b = GB[key];
    if (b.kind === "path") cells.push({ x, z, layer, path: b.id });
    else cells.push({ x, z, layer, type: b.type, value: b.value });
  }
  cells.sort((a, b) => a.layer - b.layer || a.z - b.z || a.x - b.x);
  return { cells };
}

function importPlan(plan) {
  let skipped = 0;
  state.cells = {};
  for (const c of Array.isArray(plan.cells) ? plan.cells : []) {
    const x = Number(c.x);
    const z = Number(c.z);
    const l = Number(c.layer);
    const ok = Number.isInteger(x) && Number.isInteger(z) && [0, 1, 2].includes(l) && cheb(x, z) >= 2 && cheb(x, z) <= MAXD;
    let b = null;
    if (c.path) b = GBRUSHES.find((g) => g.kind === "path" && g.id === c.path);
    else if (c.type) b = GBRUSHES.find((g) => g.kind === "tranquility" && g.type === c.type && g.value === Number(c.value ?? 1));
    if (ok && b && (b.kind !== "path" || l === 0)) setCell(x, z, l, b.key);
    else skipped++;
  }
  update();
  return skipped;
}

function update() {
  lastEval = evaluate();
  renderPaletteState();
  renderResults(lastEval);
  draw();
  writeHash();
}

function init() {
  if (!DATA || !DATA.garden) throw new Error("data.js is missing or out of date");
  if (DATA.modVersion) $("mod-version").textContent = `Neo Vitae ${DATA.modVersion}`;
  const crest = texSrc(G.incenseAltarTexture);
  if (crest) $("crest").src = crest;
  readHash();
  buildViewButtons();
  buildPalette();
  $("lay-paths").addEventListener("click", layPaths);
  $("path-rings").addEventListener("change", (e) => {
    state.pathRings = Number(e.target.value);
  });
  $("clear-all").addEventListener("click", () => {
    state.cells = {};
    update();
  });
  $("clear-layer").addEventListener("click", () => {
    const layers = state.view === "all" ? [0, 1, 2] : [Number(state.view)];
    for (const k of Object.keys(state.cells)) if (layers.includes(Number(k.split(",")[2]))) delete state.cells[k];
    update();
  });
  $("ring-tools").addEventListener("click", (e) => {
    const b = e.target.closest("button[data-ring]");
    if (b) fillRing(Number(b.dataset.ring));
  });
  bindPlanActions({
    planner: "neovitae-tranquility-garden",
    filename: () => "tranquility-garden.json",
    exportPlan,
    importPlan,
    writeHash,
  });
  new ResizeObserver(resizeCanvas).observe($("garden-host"));
  update();
  resizeCanvas();
}

try {
  init();
} catch (err) {
  document.querySelector(".layout").innerHTML = `<p class="note warn">Could not start the planner (${esc(err.message)}). Run tools/build-altar-calculator.py to generate calculator/data.js.</p>`;
}
