"use strict";

const f = Math.fround;
const trunc = Math.trunc;

const state = {
  tier: 3,
  slots: {},
  brush: "rune_speed",
  tab: "production",
  recipe: null,
  well: { on: true, count: 20 },
  knife: { on: false, players: 1, raw: true, sentient: true },
  nexus: { on: false, mob: "minecraft:zombie", spawners: 4 },
};

const BRUSHES = [];
const BRUSH = {};
const SLOTS = DATA.blocks.filter((b) => b.kind === "rune" && b.upgradeFrom != null);
const posKey = (p) => p.join(",");

function buildBrushes() {
  const blank = { key: "blank", name: DATA.blankRune.name, texture: DATA.blankRune.texture, glow: DATA.blankRune.glow };
  BRUSHES.push(blank);
  for (const r of DATA.runes) BRUSHES.push({ key: r.key, name: r.name, texture: r.texture, glow: r.glow, rune: r });
  for (const a of DATA.addonRunes || []) {
    for (const s of a.states) {
      BRUSHES.push({ key: s.key, name: `${a.name} (${s.label})`, texture: a.texture, glow: null, addon: a, state: s });
    }
  }
  for (const b of BRUSHES) BRUSH[b.key] = b;
}

function activeSlots() {
  return SLOTS.filter((s) => s.upgradeFrom <= state.tier);
}

function slotBrush(slot) {
  const k = state.slots[posKey(slot.p)];
  return k && BRUSH[k] ? k : "blank";
}

function currentCounts() {
  const counts = {};
  for (const s of activeSlots()) {
    const k = slotBrush(s);
    if (k !== "blank") counts[k] = (counts[k] || 0) + 1;
  }
  return counts;
}

function computeModifiers(counts) {
  const C = DATA.constants;
  let cap = 0, aug = 1, cons = 0, sac = 0, self = 0, disl = 1, orb = 0, acc = 0, chg = 0, eff = 1, chargingRunes = 0;
  const sources = DATA.runes.map((r) => [r.stats, counts[r.key] || 0]);
  for (const a of DATA.addonRunes || []) {
    for (const st of a.states) sources.push([a.stats || {}, counts[st.key] || 0]);
  }
  for (const [s, n] of sources) {
    for (let i = 0; i < n; i++) {
      cap += s.capacity_mod || 0;
      cons += s.consumption_mod || 0;
      sac += s.sacrifice_mod || 0;
      self += s.self_sacrifice_mod || 0;
      orb += s.orb_capacity_mod || 0;
      acc += s.acceleration_mod || 0;
      chg += s.charge_amount_mod || 0;
      if (s.augmented_capacity_power != null && s.augmented_capacity_power !== 1) aug *= s.augmented_capacity_power;
      if (s.dislocation_power != null && s.dislocation_power !== 1) disl *= s.dislocation_power;
      if (s.efficiency_power != null && s.efficiency_power !== 1) eff *= s.efficiency_power;
      if (s.charge_amount_mod != null) chargingRunes++;
    }
  }
  const capacity = f((1 + cap) * aug);
  const consumption = f(cons);
  const m = {
    capacity,
    tickRate: Math.max(C.minTickRate, C.baseTickRate - acc),
    consumption,
    sacrifice: f(sac),
    selfSacrifice: f(self),
    dislocation: f(disl),
    orb: f(orb),
    chargeAmount: f(chg * f(1 + f(consumption / 2))),
    chargeCapacity: f(f(Math.max(C.chargeCapacityMinFactor * capacity, 1)) * chargingRunes),
    efficiency: f(eff),
  };
  for (const rune of DATA.addonRunes || []) {
    for (const s of rune.states) {
      const n = counts[s.key] || 0;
      if (!n) continue;
      if (s.consumptionAdd) m.consumption = f(m.consumption + f(f(s.consumptionAdd) * n));
      if (s.dislocationPerRune) m.dislocation = f(m.dislocation * f(1 + f(f(s.dislocationPerRune) * n)));
    }
  }
  m.mainCap = trunc(f(f(C.bucket * 10) * m.capacity));
  m.ioCap = trunc(f(C.bucket * m.capacity));
  m.chargeCap = trunc(f(C.bucket * m.chargeCapacity));
  m.ioPerOp = Math.min(trunc(f(C.baseIoRate * m.dislocation)), m.ioCap);
  m.ioPerSec = (m.ioPerOp / m.tickRate) * TPS;
  m.chargePerOp = trunc(m.chargeAmount);
  m.chargeFillTicks = m.chargeCap > 0 && m.chargePerOp > 0 ? Math.ceil(m.chargeCap / m.chargePerOp) * m.tickRate : Infinity;
  return m;
}

function altarGain(ev, bonus) {
  return trunc(f(f(1 + bonus) * ev));
}

function runeEffect(stats) {
  const parts = [];
  if (stats.consumption_mod) parts.push(`${pct(stats.consumption_mod)} speed`);
  if (stats.efficiency_power) parts.push("less stall loss");
  if (stats.sacrifice_mod) parts.push(`${pct(stats.sacrifice_mod)} mob sacrifice`);
  if (stats.self_sacrifice_mod) parts.push(`${pct(stats.self_sacrifice_mod)} self sacrifice`);
  if (stats.capacity_mod) parts.push(`${pct(stats.capacity_mod)} capacity`);
  if (stats.augmented_capacity_power) parts.push(`${mult(stats.augmented_capacity_power)} capacity`);
  if (stats.dislocation_power) parts.push(`${mult(stats.dislocation_power)} transfer`);
  if (stats.orb_capacity_mod) parts.push(`${pct(stats.orb_capacity_mod)} network cap`);
  if (stats.acceleration_mod) parts.push(`−${stats.acceleration_mod} tick interval`);
  if (stats.charge_amount_mod) parts.push(`+${stats.charge_amount_mod} charge`);
  return parts.join(", ");
}

function stateEffect(s, base = {}) {
  const parts = [];
  const speed = (base.consumption_mod || 0) + (s.consumptionAdd || 0);
  if (speed) parts.push(`${pct(speed)} speed`);
  if (s.dislocationPerRune) parts.push(`${pct(s.dislocationPerRune)} transfer`);
  return parts.join(", ") || "no effect";
}

function maxRing() {
  return activeSlots().reduce((a, s) => Math.max(a, -s.p[1]), 0);
}

function paint(slot, key) {
  const k = posKey(slot.p);
  if (key === "blank") delete state.slots[k];
  else state.slots[k] = key;
}

function addOne(key, delta) {
  const slots = activeSlots();
  if (delta > 0) {
    const target = slots.find((s) => slotBrush(s) === "blank");
    if (target) paint(target, key);
  } else {
    const target = [...slots].reverse().find((s) => slotBrush(s) === key);
    if (target) paint(target, "blank");
  }
  update();
}

const View = {
  ok: false,
  pending: false,
  slotMeshes: [],
  pickables: [],
  textures: new Map(),
  materials: new Map(),
  hover: null,
  painting: false,
  down: null,
};

function initView() {
  const host = $("viewport");
  if (typeof THREE === "undefined" || !THREE.OrbitControls) {
    showViewFallback("The 3D view could not load three.js. Check your connection; the rest of the planner still works.");
    return;
  }
  let renderer;
  try {
    renderer = new THREE.WebGLRenderer({ antialias: true, alpha: false });
  } catch (err) {
    showViewFallback("This browser could not start WebGL, so the 3D view is off. The rest of the planner still works.");
    return;
  }
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
  renderer.outputEncoding = THREE.sRGBEncoding;
  renderer.setClearColor(0x0d0809, 1);
  host.appendChild(renderer.domElement);

  const scene = new THREE.Scene();
  scene.fog = new THREE.Fog(0x0d0809, 30, 70);
  const camera = new THREE.PerspectiveCamera(40, 1, 0.1, 200);
  scene.add(new THREE.AmbientLight(0xffffff, 0.62));
  const sun = new THREE.DirectionalLight(0xfff1e6, 0.75);
  sun.position.set(6, 12, 8);
  scene.add(sun);
  const fill = new THREE.DirectionalLight(0xff9aa6, 0.25);
  fill.position.set(-8, 4, -6);
  scene.add(fill);

  const canvas = renderer.domElement;
  canvas.addEventListener("pointerdown", onPointerDown);
  canvas.addEventListener("pointermove", onPointerMove);
  canvas.addEventListener("pointerup", onPointerUp);
  canvas.addEventListener("pointerleave", () => setHover(null));
  canvas.addEventListener("contextmenu", (e) => {
    e.preventDefault();
    if (View.hover && View.hover.userData.slot) {
      paint(View.hover.userData.slot, "blank");
      update();
    }
  });

  const controls = new THREE.OrbitControls(camera, canvas);
  controls.mouseButtons = { LEFT: THREE.MOUSE.ROTATE, MIDDLE: THREE.MOUSE.PAN, RIGHT: -1 };
  controls.enableDamping = false;
  controls.maxPolarAngle = Math.PI * 0.62;
  controls.minDistance = 3;
  controls.maxDistance = 60;
  controls.addEventListener("change", requestRender);

  const group = new THREE.Group();
  scene.add(group);

  const highlight = new THREE.LineSegments(
    new THREE.EdgesGeometry(new THREE.BoxGeometry(1.04, 1.04, 1.04)),
    new THREE.LineBasicMaterial({ color: 0xf0c46a })
  );
  highlight.visible = false;
  scene.add(highlight);

  Object.assign(View, { ok: true, renderer, scene, camera, controls, group, highlight, raycaster: new THREE.Raycaster() });
  View.box = new THREE.BoxGeometry(1, 1, 1);
  View.glowBox = new THREE.BoxGeometry(1.012, 1.012, 1.012);

  new ResizeObserver(resizeView).observe(host);
  resizeView();
}

function showViewFallback(text) {
  const el = $("viewport-fallback");
  el.textContent = text;
  el.hidden = false;
}

function resizeView() {
  if (!View.ok) return;
  const host = $("viewport");
  const w = host.clientWidth;
  const h = host.clientHeight;
  View.renderer.setSize(w, h, false);
  View.camera.aspect = w / Math.max(1, h);
  View.camera.updateProjectionMatrix();
  requestRender();
}

function requestRender() {
  if (!View.ok || View.pending) return;
  View.pending = true;
  requestAnimationFrame(() => {
    View.pending = false;
    View.renderer.render(View.scene, View.camera);
  });
}

function loadTexture(key) {
  if (View.textures.has(key)) return View.textures.get(key);
  const info = DATA.textures[key];
  const tex = new THREE.TextureLoader().load(info.src, requestRender);
  tex.magFilter = THREE.NearestFilter;
  tex.minFilter = THREE.NearestFilter;
  tex.generateMipmaps = false;
  tex.encoding = THREE.sRGBEncoding;
  if (info.h > info.w) {
    tex.repeat.set(1, info.w / info.h);
    tex.offset.set(0, 1 - info.w / info.h);
  }
  View.textures.set(key, tex);
  return tex;
}

function solidMaterial(key, opts = {}) {
  const id = `s:${key}:${opts.dim ? 1 : 0}:${opts.glass ? 1 : 0}:${opts.ghost ? 1 : 0}`;
  if (View.materials.has(id)) return View.materials.get(id);
  const see = opts.glass || opts.ghost;
  const mat = new THREE.MeshLambertMaterial({
    map: key ? loadTexture(key) : stoneTexture(),
    transparent: !!see,
    opacity: opts.ghost ? 0.28 : opts.glass ? 0.85 : 1,
    depthWrite: !opts.ghost,
    alphaTest: see ? 0 : 0.1,
    color: opts.dim ? 0x9a8e8c : 0xffffff,
  });
  View.materials.set(id, mat);
  return mat;
}

function glowMaterial(key) {
  const id = `g:${key}`;
  if (View.materials.has(id)) return View.materials.get(id);
  const mat = new THREE.MeshBasicMaterial({
    map: loadTexture(key),
    transparent: true,
    blending: THREE.AdditiveBlending,
    depthWrite: false,
  });
  View.materials.set(id, mat);
  return mat;
}

let stoneTex = null;
function stoneTexture() {
  if (stoneTex) return stoneTex;
  const c = document.createElement("canvas");
  c.width = c.height = 16;
  const g = c.getContext("2d");
  let seed = 7;
  const rnd = () => ((seed = (seed * 16807) % 2147483647) / 2147483647);
  for (let y = 0; y < 16; y++) {
    for (let x = 0; x < 16; x++) {
      const v = 108 + Math.floor(rnd() * 22);
      g.fillStyle = `rgb(${v},${v - 4},${v - 6})`;
      g.fillRect(x, y, 1, 1);
    }
  }
  g.fillStyle = "rgb(74,70,68)";
  for (const y of [3, 7, 11, 15]) g.fillRect(0, y, 16, 1);
  for (const [x, y0] of [[7, 0], [15, 4], [7, 8], [15, 12]]) g.fillRect(x, y0, 1, 3);
  stoneTex = new THREE.CanvasTexture(c);
  stoneTex.magFilter = THREE.NearestFilter;
  stoneTex.minFilter = THREE.NearestFilter;
  stoneTex.encoding = THREE.sRGBEncoding;
  return stoneTex;
}

function altarMesh() {
  const model = DATA.altarModel;
  const group = new THREE.Group();
  const mat = solidMaterial(model.texture);
  const hidden = new THREE.MeshBasicMaterial({ visible: false });
  const order = ["east", "west", "up", "down", "south", "north"];
  for (const e of model.elements) {
    const size = [0, 1, 2].map((i) => Math.max(0.001, (e.to[i] - e.from[i]) / 16));
    const geo = new THREE.BoxGeometry(size[0], size[1], size[2]);
    const uv = geo.attributes.uv;
    const mats = [];
    order.forEach((side, faceIndex) => {
      const face = e.faces[side];
      if (!face) {
        mats.push(hidden);
        return;
      }
      mats.push(mat);
      const [u1, v1, u2, v2] = face.uv.map((n) => n / 16);
      let corners = [
        [u1, 1 - v1],
        [u2, 1 - v1],
        [u2, 1 - v2],
        [u1, 1 - v2],
      ];
      const steps = ((face.rotation || 0) / 90) % 4;
      for (let i = 0; i < steps; i++) corners = [corners[3], corners[0], corners[1], corners[2]];
      const [tl, tr, br, bl] = corners;
      const base = faceIndex * 4;
      uv.setXY(base, tl[0], tl[1]);
      uv.setXY(base + 1, tr[0], tr[1]);
      uv.setXY(base + 2, bl[0], bl[1]);
      uv.setXY(base + 3, br[0], br[1]);
    });
    uv.needsUpdate = true;
    const mesh = new THREE.Mesh(geo, mats);
    mesh.position.set(
      (e.from[0] + e.to[0]) / 32 - 0.5,
      (e.from[1] + e.to[1]) / 32,
      (e.from[2] + e.to[2]) / 32 - 0.5
    );
    group.add(mesh);
  }
  return group;
}

function blockMesh(textureKey, glowKey, opts) {
  const mesh = new THREE.Mesh(View.box, solidMaterial(textureKey, opts));
  const glow = new THREE.Mesh(View.glowBox, glowKey ? glowMaterial(glowKey) : undefined);
  glow.visible = !!glowKey;
  mesh.add(glow);
  mesh.userData.glow = glow;
  return mesh;
}

function rebuildScene() {
  if (!View.ok) return;
  const { group } = View;
  while (group.children.length) group.remove(group.children[0]);
  View.slotMeshes = [];
  View.pickables = [];
  let radius = 1;
  let minY = 0;
  let maxY = 1;
  for (const b of DATA.blocks) {
    if (b.from > state.tier) continue;
    const [x, y, z] = b.p;
    radius = Math.max(radius, Math.abs(x), Math.abs(z));
    minY = Math.min(minY, y);
    maxY = Math.max(maxY, y + 1);
    if (b.kind === "altar") {
      const a = altarMesh();
      a.position.set(x, y, z);
      group.add(a);
      continue;
    }
    let mesh;
    if (b.kind === "rune") {
      const isSlot = b.upgradeFrom != null && b.upgradeFrom <= state.tier;
      mesh = blockMesh(DATA.blankRune.texture, DATA.blankRune.glow, { dim: !isSlot });
      if (isSlot) {
        mesh.userData.slot = b;
        View.slotMeshes.push(mesh);
      } else {
        mesh.userData.info = `${DATA.blankRune.name}, gives no bonus until Tier ${b.upgradeFrom}`;
      }
    } else if (b.kind === "pillar") {
      mesh = blockMesh(null, null, {});
      mesh.userData.info = "Pillar: any solid block, or leave it empty";
    } else {
      const mat = DATA.materials[b.material];
      mesh = blockMesh(mat.base, mat.glow, { glass: /glass/.test(b.material) });
      mesh.userData.info = `Capstone: ${mat.name}`;
    }
    mesh.position.set(x, y + 0.5, z);
    group.add(mesh);
    View.pickables.push(mesh);
  }
  const occupied = new Set(DATA.blocks.filter((b) => b.from <= state.tier).map((b) => posKey(b.p)));
  for (const b of DATA.blocks) {
    if (b.kind !== "cap" || b.from > state.tier) continue;
    const [x, y, z] = b.p;
    for (let sy = y - 1; sy >= -b.from + 1 && !occupied.has(posKey([x, sy, z])); sy--) {
      const ghost = blockMesh(null, null, { ghost: true });
      ghost.position.set(x, sy + 0.5, z);
      ghost.userData.info = "Support: any block or none, the altar does not check it";
      group.add(ghost);
      View.pickables.push(ghost);
    }
  }
  const floor = new THREE.Mesh(
    new THREE.CircleGeometry(radius * 1.5 + 3, 48),
    new THREE.MeshBasicMaterial({ color: new THREE.Color(0x1a1012).convertSRGBToLinear() })
  );
  floor.rotation.x = -Math.PI / 2;
  floor.position.y = minY - 0.01;
  group.add(floor);
  View.frame = { radius, minY, maxY };
  updateSlotMeshes();
}

function fitCamera() {
  if (!View.ok || !View.frame) return;
  const { radius, minY, maxY } = View.frame;
  const r = radius + 1.5;
  const cy = (minY + maxY) / 2;
  View.camera.position.set(r * 1.35, cy + r * 1.25 + 1.5, r * 1.75);
  View.controls.target.set(0, cy, 0);
  View.controls.update();
  requestRender();
}

function updateSlotMeshes() {
  if (!View.ok) return;
  for (const mesh of View.slotMeshes) {
    const b = BRUSH[slotBrush(mesh.userData.slot)];
    mesh.material = solidMaterial(b.texture);
    const glow = mesh.userData.glow;
    glow.visible = !!b.glow;
    if (b.glow) glow.material = glowMaterial(b.glow);
  }
  requestRender();
}

function pick(e) {
  const rect = View.renderer.domElement.getBoundingClientRect();
  const v = new THREE.Vector2(((e.clientX - rect.left) / rect.width) * 2 - 1, -((e.clientY - rect.top) / rect.height) * 2 + 1);
  View.raycaster.setFromCamera(v, View.camera);
  const hit = View.raycaster.intersectObjects(View.pickables, false)[0];
  return hit ? hit.object : null;
}

function setHover(mesh, e) {
  View.hover = mesh;
  const tip = $("tooltip");
  if (!mesh) {
    View.highlight.visible = false;
    tip.hidden = true;
    View.renderer.domElement.style.cursor = "";
    requestRender();
    return;
  }
  View.highlight.position.copy(mesh.position);
  View.highlight.visible = true;
  View.renderer.domElement.style.cursor = mesh.userData.slot ? "pointer" : "";
  let text = mesh.userData.info;
  if (mesh.userData.slot) {
    const slot = mesh.userData.slot;
    text = `${BRUSH[slotBrush(slot)].name}, ring ${-slot.p[1]}`;
  }
  tip.textContent = text;
  tip.hidden = false;
  if (e) {
    const rect = $("viewport").getBoundingClientRect();
    tip.style.left = `${e.clientX - rect.left + 14}px`;
    tip.style.top = `${e.clientY - rect.top + 14}px`;
  }
  requestRender();
}

function onPointerDown(e) {
  View.down = { x: e.clientX, y: e.clientY, button: e.button };
  if (e.button === 0 && e.shiftKey) {
    View.painting = true;
    View.controls.enabled = false;
    const mesh = pick(e);
    if (mesh && mesh.userData.slot) {
      paint(mesh.userData.slot, state.brush);
      update();
    }
  }
}

function onPointerMove(e) {
  const mesh = pick(e);
  if (mesh !== View.hover || mesh) setHover(mesh, e);
  if (View.painting && mesh && mesh.userData.slot && slotBrush(mesh.userData.slot) !== state.brush) {
    paint(mesh.userData.slot, state.brush);
    update();
    setHover(mesh, e);
  }
}

function onPointerUp(e) {
  if (View.painting) {
    View.painting = false;
    View.controls.enabled = true;
    View.down = null;
    return;
  }
  const d = View.down;
  View.down = null;
  if (!d || d.button !== 0) return;
  if (Math.hypot(e.clientX - d.x, e.clientY - d.y) > 5) return;
  const mesh = pick(e);
  if (mesh && mesh.userData.slot) {
    paint(mesh.userData.slot, state.brush);
    update();
    setHover(mesh, e);
  }
}

function buildTierButtons() {
  $("tier-buttons").innerHTML = DATA.tiers
    .map(
      (t) =>
        `<button type="button" data-tier="${t.tier}" aria-pressed="false"><span class="t">${t.tier}</span><span class="s">${t.upgradeSlots} slot${t.upgradeSlots === 1 ? "" : "s"}</span></button>`
    )
    .join("");
  $("tier-buttons").addEventListener("click", (e) => {
    const b = e.target.closest("button[data-tier]");
    if (!b) return;
    state.tier = Number(b.dataset.tier);
    rebuildScene();
    fitCamera();
    update();
  });
}

function brushCell(b) {
  return `<div class="bcell" data-key="${b.key}">
    <button type="button" class="step" data-add="${b.key}" data-n="-1" aria-label="Remove one ${esc(b.name)}">−</button>
    <button type="button" class="pick" data-pick="${b.key}" aria-pressed="false" title="${esc(b.name)}">${icon(b.texture, b.name)}<span class="count mono">0</span></button>
    <button type="button" class="step" data-add="${b.key}" data-n="1" aria-label="Add one ${esc(b.name)}">+</button>
  </div>`;
}

function buildBrushPanel() {
  const groups = [];
  for (const r of DATA.runes) {
    let g = groups.find((x) => x.type === r.type);
    if (!g) groups.push((g = { type: r.type }));
    g[r.reinforced ? "reinforced" : "standard"] = BRUSH[r.key];
  }
  const blank = BRUSH.blank;
  let html = `<div class="brow">
      <div class="bname">${icon(blank.texture, blank.name)}<span class="label"><b>${esc(blank.name)}</b><small>Empty slot, no bonus</small></span></div>
      <div class="bcell blank-cell" data-key="blank"><button type="button" class="pick" data-pick="blank" aria-pressed="false" title="${esc(blank.name)}">${icon(blank.texture, blank.name)}<span class="count mono">0</span></button></div>
    </div>
    <div class="brow bhead"><span>Neo Vitae</span><span class="col-c">Standard</span><span class="col-c">Reinforced</span></div>`;
  for (const g of groups) {
    const base = g.standard || g.reinforced;
    html += `<div class="brow">
      <div class="bname">${icon(base.texture, base.name)}<span class="label"><b>${esc(base.name)}</b><small>${esc(runeEffect(base.rune.stats))}</small></span></div>
      ${g.standard ? brushCell(g.standard) : "<span></span>"}
      ${g.reinforced ? brushCell(g.reinforced) : "<span></span>"}
    </div>`;
  }
  for (const a of DATA.addonRunes || []) {
    const needs = [
      a.requires ? `Needs ${a.requires}` : "",
      a.versions ? `${a.versions.join(", ")} only` : "",
      a.minVersion ? `${a.mod} ${a.minVersion} or newer` : "",
    ]
      .filter(Boolean)
      .join(". ");
    html += `<div class="brow bhead"><span>${esc(a.mod)}</span>${a.states.map((s) => `<span class="col-c">${esc(s.label)}</span>`).join("")}</div>
      <div class="brow">
        <div class="bname">${icon(a.texture, a.name)}<span class="label"><b>${esc(a.name)}</b><small>${esc(a.states.map((s) => `${s.label}: ${stateEffect(s, a.stats)}`).join(". "))}</small>${needs ? `<small class="needs">${esc(needs)}</small>` : ""}</span></div>
        ${a.states.map((s) => brushCell(BRUSH[s.key])).join("")}
      </div>`;
  }
  $("brushes").innerHTML = html;
  $("brushes").addEventListener("click", (e) => {
    const add = e.target.closest("button[data-add]");
    if (add) {
      addOne(add.dataset.add, Number(add.dataset.n));
      return;
    }
    const pickBtn = e.target.closest("button[data-pick]");
    if (pickBtn) {
      state.brush = pickBtn.dataset.pick;
      update();
    }
  });
}

function renderPalette(counts) {
  const slots = activeSlots();
  const used = slots.length - slots.filter((s) => slotBrush(s) === "blank").length;
  for (const cell of document.querySelectorAll(".bcell")) {
    const key = cell.dataset.key;
    const n = key === "blank" ? slots.length - used : counts[key] || 0;
    cell.querySelector(".count").textContent = n;
    cell.classList.toggle("has-value", key !== "blank" && n > 0);
    const btn = cell.querySelector(".pick");
    btn.setAttribute("aria-pressed", String(state.brush === key));
  }
  for (const b of $("tier-buttons").querySelectorAll("button")) {
    b.setAttribute("aria-pressed", String(Number(b.dataset.tier) === state.tier));
  }
  const t = DATA.tiers[state.tier];
  $("slots-text").textContent = `${used} / ${t.upgradeSlots}`;
  $("slots-fill").style.width = `${t.upgradeSlots ? (used / t.upgradeSlots) * 100 : 0}%`;
  let note;
  if (state.tier === 0) note = "A lone Ara Vitae takes no runes. Pick Tier 1 or higher to start placing them.";
  else if (t.runeBlocks > t.upgradeSlots) note = `Tier ${state.tier} has ${t.runeBlocks} runes, but only the ${t.upgradeSlots} on the sides give bonuses. The corners count as blank until Tier 2.`;
  else note = `${t.upgradeSlots - used} slot${t.upgradeSlots - used === 1 ? "" : "s"} still blank. Selected: ${BRUSH[state.brush].name}.`;
  $("slots-note").textContent = note;
  const rings = maxRing();
  $("ring-tools").innerHTML = rings
    ? `<span class="ring-label">Paint ring</span>${Array.from({ length: rings }, (_, i) => `<button type="button" class="ghost small" data-ring="${i + 1}">${i + 1}</button>`).join("")}`
    : "";
}

function stat(label, value, unit, detail, accent) {
  return `<div class="stat${accent ? " accent" : ""}"><div class="k">${label}</div><div class="v">${value}${unit ? `<span class="u">${unit}</span>` : ""}</div>${detail ? `<div class="d">${detail}</div>` : ""}</div>`;
}

function mini(label, value, unit, detail) {
  return `<div class="mini"><div class="k">${label}</div><div class="v">${value}${unit ? `<span class="u">${unit}</span>` : ""}</div>${detail ? `<div class="d">${detail}</div>` : ""}</div>`;
}

function renderStats(m) {
  $("stat-grid").innerHTML = [
    stat("Main tank", fmt(m.mainCap), "EV", `${mult(m.capacity)} of the 10,000 EV base`, true),
    stat("Input and output buffers", fmt(m.ioCap), "EV", "each, for pipes and tanks"),
    stat("Speed", pct(m.consumption), "", `crafting and orb filling ${mult(1 + m.consumption)}`),
    stat("Operation interval", fmt(m.tickRate), m.tickRate === 1 ? "tick" : "ticks", `${fmt(m.tickRate / TPS, 2)} s between transfers and charging`),
    stat("Pipe transfer", fmt(m.ioPerOp), "EV/op", `${fmt(m.ioPerSec, 1)} EV/s in, and the same out`),
    stat("Stall loss", mult(1 + m.efficiency), "", "of a recipe's drain rate while the tank is dry"),
    stat("Mob sacrifice", pct(m.sacrifice), "", "Well of Suffering, Torment Nexus"),
    stat("Self sacrifice", pct(m.selfSacrifice), "", "Feathered Knife, orb use"),
    m.chargeCap > 0
      ? stat("Charge tank", fmt(m.chargeCap), "EV", `${fmt(m.chargePerOp)} EV per operation, full in ${duration(m.chargeFillTicks)}`)
      : stat("Charge tank", "0", "EV", "add Charging Runes to store a burst for the next craft"),
    stat("Orb network cap", pct(m.orb), "", "added to each orb's network capacity"),
  ].join("");
}

function computeProduction(m) {
  const R = DATA.rituals;
  const C = DATA.constants;
  const sources = [];

  const well = R.wellOfSuffering;
  const witch = DATA.mobs.find((x) => x.id === "minecraft:witch");
  const perWitch = trunc(f(witch.evPerDamage * f(well.damage)));
  const wellBase = perWitch * state.well.count;
  const wellOps = TPS / well.refreshTime;
  sources.push({
    key: "well",
    on: state.well.on,
    gain: altarGain(wellBase, m.sacrifice) * wellOps,
    cost: wellBase > 0 ? well.refreshCost * wellOps : 0,
    detail: `${state.well.count} witches, ${fmt(perWitch)} EV each per hit, one hit every ${duration(well.refreshTime)}`,
  });

  const knife = R.featheredKnife;
  const refresh = state.knife.raw ? Math.max(1, Math.floor(knife.refreshTime / 2)) : knife.refreshTime;
  let perPlayer = trunc(knife.healthPerUse * C.selfSacrificeConversion) * knife.evMultiplier;
  if (state.knife.sentient) perPlayer = trunc(perPlayer * knife.sentientBonus);
  const knifeOps = TPS / refresh;
  sources.push({
    key: "knife",
    on: state.knife.on,
    gain: altarGain(perPlayer * state.knife.players, m.selfSacrifice) * knifeOps,
    cost: state.knife.players > 0 ? knife.refreshCost * knifeOps : 0,
    detail: `${fmt(perPlayer)} EV per player every ${duration(refresh)}. Each player must regain ${fmt(knife.healthPerUse * knifeOps, 1)} health per second to keep up.`,
  });

  const nexus = R.tormentNexus;
  const mob = DATA.mobs.find((x) => x.id === state.nexus.mob) || DATA.mobs[0];
  let baseEv = trunc(f(mob.evPerDamage * f(mob.maxHealth)));
  if (mob.maxPerHit != null) baseEv = Math.min(baseEv, mob.maxPerHit);
  const perKill = trunc((baseEv * nexus.evModifierPercent) / 100);
  const avgDelay = (Math.max(1, nexus.minSpawnDelay) + Math.max(nexus.minSpawnDelay, nexus.maxSpawnDelay)) / 2;
  const killsPerSec = (state.nexus.spawners * nexus.spawnCount * TPS) / avgDelay;
  const nexusOps = TPS / nexus.refreshTime;
  const killsPerOp = killsPerSec / nexusOps;
  const rawCost = killsPerOp * nexus.evPerKill;
  const costPerOp = nexus.maxEvPerOperation > 0 ? Math.min(rawCost, nexus.maxEvPerOperation) : rawCost;
  const capAt = Math.ceil(nexus.maxEvPerOperation / ((nexus.spawnCount * nexus.refreshTime) / avgDelay) / nexus.evPerKill);
  sources.push({
    key: "nexus",
    on: state.nexus.on,
    gain: perKill * killsPerSec * f(1 + m.sacrifice),
    cost: costPerOp * nexusOps,
    detail: `${fmt(killsPerSec, 2)} kills/s, ${fmt(perKill)} EV per kill before runes, ${fmt(nexus.evPerKill)} EV network cost per kill until the ${fmt(nexus.maxEvPerOperation)} EV per operation ceiling (reached at ${fmt(capAt)} spawners).`,
  });
  return sources;
}

function sourceCard(s, title, controls) {
  return `<div class="source${s.on ? " on" : ""}">
    <label class="source-head"><input type="checkbox" data-toggle="${s.key}"${s.on ? " checked" : ""}><span>${title}</span></label>
    <div class="source-controls">${controls}</div>
    <div class="source-nums">
      <div><span class="k">Into altar</span><span class="v">${fmt(s.gain, 1)}<span class="u">EV/s</span></span></div>
      <div><span class="k">Network cost</span><span class="v">${fmt(s.cost, 1)}<span class="u">EV/s</span></span></div>
      <div><span class="k">Net</span><span class="v">${fmt(s.gain - s.cost, 1)}<span class="u">EV/s</span></span></div>
    </div>
    <p class="source-detail">${s.detail}</p>
  </div>`;
}

function renderProduction(m, sources) {
  const [well, knife, nexus] = sources;
  const mobOptions = DATA.mobs
    .map((x) => `<option value="${x.id}"${x.id === state.nexus.mob ? " selected" : ""}>${esc(x.name)}</option>`)
    .join("");
  const active = document.activeElement && document.activeElement.closest("#sources") ? document.activeElement.id : null;
  $("sources").innerHTML = [
    sourceCard(
      well,
      "Well of Suffering",
      `<label class="field narrow"><span>Witches</span><input type="number" id="well-count" min="0" max="500" value="${state.well.count}"></label>
       <p class="note">Witches heal themselves, so every hit lands and none of them die.</p>`
    ),
    sourceCard(
      knife,
      "Feathered Knife (maxed self sacrifice)",
      `<label class="field narrow"><span>Players</span><input type="number" id="knife-players" min="0" max="100" value="${state.knife.players}"></label>
       <label class="check"><input type="checkbox" id="knife-raw"${state.knife.raw ? " checked" : ""}><span>Raw Spiritus at the ritual (twice as fast)</span></label>
       <label class="check"><input type="checkbox" id="knife-sentient"${state.knife.sentient ? " checked" : ""}><span>Full Sentient armor (+10%)</span></label>`
    ),
    sourceCard(
      nexus,
      "Torment Nexus",
      `<label class="field grow"><span>Spawner mob</span><select id="nexus-mob">${mobOptions}</select></label>
       <label class="field narrow"><span>Spawners</span><input type="number" id="nexus-spawners" min="0" max="1000" value="${state.nexus.spawners}"></label>
       <p class="note">Vanilla spawners: 4 mobs every 10 to 40 seconds. Kills keep running free once the cost ceiling is reached.</p>`
    ),
  ].join("");
  if (active && $(active)) {
    const el = $(active);
    el.focus();
    if (el.type === "number") {
      const len = String(el.value).length;
      try {
        el.setSelectionRange(len, len);
      } catch {}
    }
  }

  const on = sources.filter((s) => s.on);
  const gain = on.reduce((a, s) => a + s.gain, 0);
  const cost = on.reduce((a, s) => a + s.cost, 0);
  $("totals").innerHTML = on.length
    ? `<div class="mini-grid">
        ${mini("Total into altar", fmt(gain, 1), "EV/s", `${fmt(gain * 3600)} EV per hour`)}
        ${mini("Network cost", fmt(cost, 1), "EV/s", `${fmt(cost * 3600)} EV per hour`)}
        ${mini("Net production", fmt(gain - cost, 1), "EV/s", `${fmt((gain - cost) * 3600)} EV per hour`)}
        ${mini("Fills the main tank", gain > 0 ? duration((m.mainCap / gain) * TPS) : "never", "", `${fmt(m.mainCap)} EV from empty`)}
      </div>
      <p class="note">EV that arrives while the main tank is full is lost, so match production to what you craft or pipe out.</p>`
    : `<p class="note">Tick a source above to see total and net production.</p>`;
  return gain;
}

function buildRecipeSelect() {
  const byTier = new Map();
  for (const r of DATA.recipes) {
    if (!byTier.has(r.minTier)) byTier.set(r.minTier, []);
    byTier.get(r.minTier).push(r);
  }
  $("recipe").innerHTML = [...byTier.entries()]
    .map(([tier, list]) => `<optgroup label="Tier ${tier}">${list.map((r) => `<option value="${r.id}">${esc(r.name)}</option>`).join("")}</optgroup>`)
    .join("");
  $("recipe").addEventListener("change", (e) => {
    state.recipe = e.target.value;
    update();
  });
}

function renderCraft(m, production) {
  const recipe = DATA.recipes.find((r) => r.id === state.recipe) || DATA.recipes[0];
  state.recipe = recipe.id;
  $("recipe").value = recipe.id;
  const rate = trunc(f(recipe.craftSpeed * f(1 + m.consumption)));
  const ticks = rate > 0 ? Math.max(1, Math.ceil(recipe.bloodNeeded / rate)) : Infinity;
  const charge = Math.min(m.chargeCap, recipe.bloodNeeded);
  const chargedTicks = rate > 0 ? Math.max(1, Math.ceil((recipe.bloodNeeded - charge) / rate)) : Infinity;
  const cooldown = DATA.constants.craftingCooldownTicks;
  const perHour = 72000 / (ticks + cooldown);
  const fed = production > 0 ? Math.min(perHour, (production * 3600) / recipe.bloodNeeded) : 0;
  const locked = recipe.minTier > state.tier;
  const alerts = [];
  if (locked) alerts.push(["bad", `Needs a Tier ${recipe.minTier} altar. This one is Tier ${state.tier}.`]);
  if (production > 0 && fed < perHour) {
    alerts.push(["warn", `Your production keeps up with ${fmt(fed, fed < 10 ? 1 : 0)} per hour. Add more sources to reach the altar's ${fmt(perHour, perHour < 10 ? 1 : 0)} per hour.`]);
  } else if (production > 0) {
    alerts.push(["ok", "Your production keeps the altar crafting at full speed."]);
  }
  $("craft-card").innerHTML = `
    <div class="craft-head">${icon(recipe.icon, recipe.name)}<b>${esc(recipe.name)}</b><span class="pill ${locked ? "bad" : "ok"}">Tier ${recipe.minTier}+</span></div>
    <div class="mini-grid">
      ${mini("Craft time", duration(ticks), "", "with the altar kept supplied")}
      ${m.chargeCap > 0 ? mini("With a full charge", duration(chargedTicks), "", "starting from a full charge tank") : ""}
      ${mini("Per hour, fully supplied", fmt(perHour, perHour < 10 ? 1 : 0), "", `includes the ${cooldown} tick cooldown between crafts`)}
      ${mini("Per hour, on your production", production > 0 ? fmt(fed, fed < 10 ? 1 : 0) : "none", "", production > 0 ? "using the sources ticked under Production" : "tick a source under Production")}
    </div>
    ${alerts.length ? `<div class="alerts">${alerts.map(([k, t]) => `<div class="alert ${k}">${t}</div>`).join("")}</div>` : ""}`;
}

function renderOrbTable(m) {
  const rows = DATA.orbs
    .map((o) => {
      const perTick = trunc(f(o.fillRate * f(1 + m.consumption)));
      const cap = trunc(f(o.animaCapacity * f(1 + m.orb)));
      const fill = perTick > 0 ? Math.ceil(cap / perTick) : Infinity;
      return `<tr>
        <td><span class="cell-item">${icon(o.icon, o.name)}${esc(o.name)}${o.mod ? ` <span class="pill">${esc(o.mod)}</span>` : ""}</span></td>
        <td class="num">${fmt(perTick)}</td>
        <td class="num">${fmt(perTick * TPS)}</td>
        <td class="num">${fmt(cap)}</td>
        <td class="num">${duration(fill)}</td>
      </tr>`;
    })
    .join("");
  $("orb-table").innerHTML = `<thead><tr><th>Orb</th><th class="r">EV/tick</th><th class="r">EV/s</th><th class="r">Network cap</th><th class="r">Empty to full</th></tr></thead><tbody>${rows}</tbody>`;
}

const CHARS = "-abcdefghijklmnopqrstuvwxyz";

function encodeSlots() {
  let out = "";
  let prev = null;
  let run = 0;
  const flush = () => {
    if (prev !== null) out += prev + (run > 1 ? run : "");
  };
  for (const s of SLOTS) {
    const idx = BRUSHES.findIndex((b) => b.key === slotBrush(s));
    const c = CHARS[idx] || "-";
    if (c === prev) run++;
    else {
      flush();
      prev = c;
      run = 1;
    }
  }
  flush();
  return out.replace(/-\d*$/, "");
}

function decodeSlots(str) {
  const re = /([-a-z])(\d*)/g;
  let i = 0;
  let m;
  while ((m = re.exec(str)) && i < SLOTS.length) {
    const n = m[2] ? parseInt(m[2], 10) : 1;
    const b = BRUSHES[CHARS.indexOf(m[1])];
    for (let k = 0; k < n && i < SLOTS.length; k++, i++) {
      if (b && b.key !== "blank") state.slots[posKey(SLOTS[i].p)] = b.key;
    }
  }
}

function writeHash() {
  const p = new URLSearchParams();
  p.set("t", state.tier);
  const s = encodeSlots();
  if (s) p.set("s", s);
  p.set("b", state.brush);
  if (state.tab !== "production") p.set("v", state.tab);
  if (state.recipe) p.set("c", state.recipe);
  p.set("w", `${state.well.on ? 1 : 0}.${state.well.count}`);
  p.set("k", `${state.knife.on ? 1 : 0}.${state.knife.players}.${state.knife.raw ? 1 : 0}.${state.knife.sentient ? 1 : 0}`);
  p.set("n", `${state.nexus.on ? 1 : 0}.${state.nexus.mob.split(":")[1]}.${state.nexus.spawners}`);
  history.replaceState(null, "", `#${p.toString()}`);
}

function readHash() {
  const p = new URLSearchParams(location.hash.slice(1));
  const t = Number(p.get("t"));
  if (p.has("t") && Number.isInteger(t) && t >= 0 && t < DATA.tiers.length) state.tier = t;
  if (p.get("s")) decodeSlots(p.get("s"));
  if (BRUSH[p.get("b")]) state.brush = p.get("b");
  if (["production", "craft", "orbs"].includes(p.get("v"))) state.tab = p.get("v");
  if (DATA.recipes.some((r) => r.id === p.get("c"))) state.recipe = p.get("c");
  const num = (v, d, max) => {
    const n = parseInt(v, 10);
    return Number.isFinite(n) && n >= 0 ? Math.min(n, max) : d;
  };
  if (p.get("w")) {
    const [on, count] = p.get("w").split(".");
    state.well.on = on === "1";
    state.well.count = num(count, state.well.count, 500);
  }
  if (p.get("k")) {
    const [on, players, raw, sentient] = p.get("k").split(".");
    state.knife.on = on === "1";
    state.knife.players = num(players, state.knife.players, 100);
    state.knife.raw = raw !== "0";
    state.knife.sentient = sentient !== "0";
  }
  if (p.get("n")) {
    const [on, mob, spawners] = p.get("n").split(".");
    state.nexus.on = on === "1";
    if (DATA.mobs.some((x) => x.id === `minecraft:${mob}`)) state.nexus.mob = `minecraft:${mob}`;
    state.nexus.spawners = num(spawners, state.nexus.spawners, 1000);
  }
}

function brushToJson(key) {
  const b = BRUSH[key];
  if (b.rune) return { rune: b.rune.id };
  if (b.addon) return { rune: b.addon.id, state: b.state.label };
  return null;
}

function brushFromJson(entry) {
  if (!entry || typeof entry.rune !== "string") return null;
  const b = BRUSHES.find(
    (x) => (x.rune && x.rune.id === entry.rune) || (x.addon && x.addon.id === entry.rune && x.state.label === entry.state)
  );
  return b ? b.key : null;
}

function exportPlan() {
  const runes = [];
  for (const s of SLOTS) {
    const key = slotBrush(s);
    if (key === "blank") continue;
    runes.push({ pos: s.p, ...brushToJson(key) });
  }
  return {
    tier: state.tier,
    runes,
    production: {
      wellOfSuffering: { enabled: state.well.on, witches: state.well.count },
      featheredKnife: { enabled: state.knife.on, players: state.knife.players, rawSpiritus: state.knife.raw, fullSentientSet: state.knife.sentient },
      tormentNexus: { enabled: state.nexus.on, mob: state.nexus.mob, spawners: state.nexus.spawners },
    },
    recipe: state.recipe,
  };
}

function importPlan(plan) {
  let skipped = 0;
  const tier = Number(plan.tier);
  if (Number.isInteger(tier) && tier >= 0 && tier < DATA.tiers.length) state.tier = tier;
  const valid = new Set(SLOTS.map((s) => posKey(s.p)));
  state.slots = {};
  for (const r of Array.isArray(plan.runes) ? plan.runes : []) {
    const key = brushFromJson(r);
    const pos = Array.isArray(r.pos) ? posKey(r.pos) : null;
    if (key && pos && valid.has(pos)) state.slots[pos] = key;
    else skipped++;
  }
  const p = plan.production || {};
  const clamp = (v, d, max) => (Number.isFinite(Number(v)) && Number(v) >= 0 ? Math.min(Math.floor(Number(v)), max) : d);
  if (p.wellOfSuffering) {
    state.well.on = !!p.wellOfSuffering.enabled;
    state.well.count = clamp(p.wellOfSuffering.witches, state.well.count, 500);
  }
  if (p.featheredKnife) {
    state.knife.on = !!p.featheredKnife.enabled;
    state.knife.players = clamp(p.featheredKnife.players, state.knife.players, 100);
    state.knife.raw = p.featheredKnife.rawSpiritus !== false;
    state.knife.sentient = p.featheredKnife.fullSentientSet !== false;
  }
  if (p.tormentNexus) {
    state.nexus.on = !!p.tormentNexus.enabled;
    if (DATA.mobs.some((x) => x.id === p.tormentNexus.mob)) state.nexus.mob = p.tormentNexus.mob;
    state.nexus.spawners = clamp(p.tormentNexus.spawners, state.nexus.spawners, 1000);
  }
  if (DATA.recipes.some((r) => r.id === plan.recipe)) state.recipe = plan.recipe;
  rebuildScene();
  fitCamera();
  update();
  return skipped;
}

function showTab(name) {
  state.tab = name;
  for (const b of document.querySelectorAll(".tabs button")) b.setAttribute("aria-selected", String(b.dataset.tab === name));
  for (const panel of document.querySelectorAll(".tab")) panel.hidden = panel.id !== `tab-${name}`;
}

function update() {
  const counts = currentCounts();
  const m = computeModifiers(counts);
  renderPalette(counts);
  renderStats(m);
  const sources = computeProduction(m);
  const production = renderProduction(m, sources);
  renderCraft(m, production);
  renderOrbTable(m);
  updateSlotMeshes();
  writeHash();
}

function bind() {
  $("clear-runes").addEventListener("click", () => {
    state.slots = {};
    update();
  });
  $("fill-empty").addEventListener("click", () => {
    for (const s of activeSlots()) if (slotBrush(s) === "blank") paint(s, state.brush);
    update();
  });
  $("ring-tools").addEventListener("click", (e) => {
    const b = e.target.closest("button[data-ring]");
    if (!b) return;
    const ring = Number(b.dataset.ring);
    for (const s of activeSlots()) if (-s.p[1] === ring) paint(s, state.brush);
    update();
  });
  $("reset-view").addEventListener("click", fitCamera);
  bindPlanActions({
    planner: "neovitae-ara-vitae",
    filename: () => `ara-vitae-tier-${state.tier}.json`,
    exportPlan,
    importPlan,
    writeHash,
  });
  document.querySelector(".tabs").addEventListener("click", (e) => {
    const b = e.target.closest("button[data-tab]");
    if (!b) return;
    showTab(b.dataset.tab);
    writeHash();
  });
  const sources = $("sources");
  sources.addEventListener("change", (e) => {
    const t = e.target;
    if (t.dataset.toggle) state[t.dataset.toggle].on = t.checked;
    else if (t.id === "knife-raw") state.knife.raw = t.checked;
    else if (t.id === "knife-sentient") state.knife.sentient = t.checked;
    else if (t.id === "nexus-mob") state.nexus.mob = t.value;
    else return;
    update();
  });
  sources.addEventListener("input", (e) => {
    const t = e.target;
    const v = parseInt(t.value, 10);
    if (!Number.isFinite(v) || v < 0) return;
    if (t.id === "well-count") state.well.count = Math.min(v, 500);
    else if (t.id === "knife-players") state.knife.players = Math.min(v, 100);
    else if (t.id === "nexus-spawners") state.nexus.spawners = Math.min(v, 1000);
    else return;
    update();
  });
}

function init() {
  if (!DATA) throw new Error("data.js is missing");
  buildBrushes();
  if (DATA.modVersion) $("mod-version").textContent = `Neo Vitae ${DATA.modVersion}`;
  const crest = DATA.orbs.find((o) => o.id === "neovitae:blood_orb_transcendent");
  if (crest && texSrc(crest.icon)) $("crest").src = texSrc(crest.icon);
  readHash();
  buildTierButtons();
  buildBrushPanel();
  buildRecipeSelect();
  bind();
  showTab(state.tab);
  initView();
  rebuildScene();
  fitCamera();
  update();
}

try {
  init();
} catch (err) {
  document.querySelector(".layout").innerHTML = `<p class="note warn">Could not start the planner (${esc(err.message)}). Run tools/build-altar-calculator.py to generate calculator/data.js.</p>`;
}
