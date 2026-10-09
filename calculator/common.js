"use strict";

const TPS = 20;
const DATA = window.ALTAR_DATA;
const $ = (id) => document.getElementById(id);

function fmt(n, digits = 0) {
  if (!isFinite(n)) return "∞";
  return n.toLocaleString("en-US", { maximumFractionDigits: digits, minimumFractionDigits: 0 });
}

function pct(x) {
  return `${x >= 0 ? "+" : ""}${fmt(x * 100, 1)}%`;
}

function mult(x) {
  return `×${fmt(x, 3)}`;
}

function duration(ticks) {
  if (!isFinite(ticks)) return "never";
  const s = ticks / TPS;
  if (s < 60) return `${fmt(s, s < 10 ? 2 : 1)} s`;
  const m = Math.floor(s / 60);
  const rs = Math.round(s - m * 60);
  if (m < 60) return rs ? `${m} min ${rs} s` : `${m} min`;
  const h = Math.floor(m / 60);
  const rm = m - h * 60;
  if (h < 48) return rm ? `${h} h ${rm} min` : `${h} h`;
  return `${fmt(h / 24, 1)} days`;
}

function esc(s) {
  return String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" })[c]);
}

function texSrc(key) {
  return key && DATA.textures[key] ? DATA.textures[key].src : null;
}

function icon(key, label) {
  const src = texSrc(key);
  if (src) return `<img class="px" src="${src}" alt="">`;
  return `<span class="ph" aria-hidden="true">${esc((label || "?").charAt(0).toUpperCase())}</span>`;
}

function toast(text) {
  let el = document.querySelector(".toast");
  if (!el) {
    el = document.createElement("div");
    el.className = "toast";
    el.setAttribute("role", "status");
    document.body.appendChild(el);
  }
  el.textContent = text;
  el.classList.add("show");
  clearTimeout(el._t);
  el._t = setTimeout(() => el.classList.remove("show"), 2200);
}

function downloadJson(filename, data) {
  const blob = new Blob([JSON.stringify(data, null, 2) + "\n"], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

function pickJsonFile() {
  return new Promise((resolve, reject) => {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "application/json,.json";
    input.addEventListener("change", () => {
      const file = input.files && input.files[0];
      if (!file) return;
      file
        .text()
        .then((text) => resolve(JSON.parse(text)))
        .catch(() => reject(new Error("That file is not valid JSON.")));
    });
    input.click();
  });
}

async function copyLink(beforeCopy) {
  if (beforeCopy) beforeCopy();
  try {
    await navigator.clipboard.writeText(location.href);
    toast("Link copied. Anyone who opens it sees this plan.");
  } catch {
    toast("Copy the address bar to share this plan");
  }
}

function bindPlanActions({ planner, filename, exportPlan, importPlan, writeHash }) {
  $("share").addEventListener("click", () => copyLink(writeHash));
  $("export").addEventListener("click", () => {
    downloadJson(filename(), { planner, version: 1, modVersion: DATA.modVersion, ...exportPlan() });
    toast("Plan saved as JSON");
  });
  $("import").addEventListener("click", async () => {
    try {
      const plan = await pickJsonFile();
      if (!plan || plan.planner !== planner) {
        throw new Error(plan && plan.planner ? "That file is a plan for the other planner." : "That file is not a plan from this planner.");
      }
      const skipped = importPlan(plan) || 0;
      toast(skipped ? `Plan loaded. ${skipped} unknown entr${skipped === 1 ? "y was" : "ies were"} skipped.` : "Plan loaded");
    } catch (err) {
      toast(err.message);
    }
  });
}
