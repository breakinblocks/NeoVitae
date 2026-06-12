# Automating Spiritus

A mature setup turns [Spiritus](Spiritus-Aspects-and-Crystals) into a self-sustaining farm: crystals grow, a ritual harvests them, the harvest is burned back into the chunk's Aura, and routing keeps the surplus circulating so your rituals, the [Hellfire Forge](Hellfire-Forge-and-Sentient-Equipment), and the [Athanor](Athanor-and-Materials) never run dry. This page walks the full life cycle; the underlying mechanics live on the [Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals) page.

## The life cycle

1. **Grow.** A **Crystallarium Maleficum** seeds a crystal cluster when its chunk holds enough Spiritus, and the cluster then grows on its own. Growth scales with the chunk's Aura saturation, so a fuller chunk grows faster.
2. **Aspect it (optional).** A Crystallarium only grows **Raw** clusters until the chunk's Aura is dominated by another Aspect. Break the cycle with a **Catalyst**: right-click a grown Raw cluster with an aspected catalyst (Ruina, Nihilum, Vindicta, Invictus) to transmute it, then burn its shards to make that Aspect dominant. From then on the Crystallarium grows that Aspect natively.
3. **Harvest.** The **Crystallum Fractura** ritual (see [Rituals](Rituals)) auto-harvests fully-grown clusters in range, dropping shards into a chest atop the Master Ritual Stone. It also **doubles** crystal growth and adds **+25% to Spiritus injection** across its chunks. Bias that bonus toward a chosen Aspect with the Ritual Configurator (carry exactly one aspect crystal in your hotbar in SET_WILL_CONFIG mode).
4. **Burn.** A **Vas Maleficum** consumes Spiritus Crystals fed into it and releases their Spiritus into the chunk's **Aura** (50 per spire; a net gain over the growth cost, and far more when same-Aspect catalysts have supercharged the cluster).
5. **Route.** Point a **Routing Node** at the harvest chest and feed the shards into the Vas Maleficum to keep the chunk saturated, closing the loop. To move Aura to where it is used, chain **Spira Infernalis** pylons from the saturated chunk toward your worksite; each pulls Aura from a richer chunk 16 blocks away.
6. **Consume.** Rituals draw on the Aura, the Hellfire Forge fills empty gems with the chunk's dominant Aspect, and the Athanor's ore-yield boost scales with local Raw Spiritus.

## The closed loop

Catalyst + Crystallum Fractura + a Routing Node is a hands-off farm: the ritual harvests and the node ships the shards back into the Vas Maleficum to maintain saturation. A **same-Aspect catalyst** on a matching cluster drops the per-spire cost to 25 and accelerates growth tenfold, so feeding catalysts into the cycle turns it strongly net-positive.

## Running multiple Aspects

The Aura is **chunk-based** and caps at **100 per Aspect**, and a chunk grows clusters of its **dominant** Aspect best (off-Aspect clusters cost more and grow slower). So farm each Aspect in its own chunk: keep that chunk biased to the Aspect (burn its shards there), run a Crystallarium + Vas + a Crystallum Fractura tuned to it, and ferry the finished Aura out with Spira Infernalis. Repeat per Aspect (Raw, Ruina, Nihilum, Vindicta, Invictus) for a full will supply.

See also: **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** (all the mechanics and numbers), **[Rituals](Rituals)** (Crystallum Fractura), **[Routing Network](Routing-Network)** (node setup).
