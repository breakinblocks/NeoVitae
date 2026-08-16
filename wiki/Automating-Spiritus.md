# Automating Spiritus

A mature setup turns [Spiritus](Spiritus-Aspects-and-Crystals) into a self-sustaining farm: crystals grow, a ritual harvests them, the harvest is burned back into the chunk's Aura, and routing keeps the surplus circulating so your rituals, the [Hellfire Forge](Hellfire-Forge-and-Sentient-Equipment), and the [Athanor](Athanor-and-Materials) never run dry. This page walks the full life cycle; the underlying mechanics live on the [Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals) page.

## The life cycle

1. **Grow.** A **Crystallarium Maleficum** seeds a crystal cluster when its chunk holds enough Spiritus, and the cluster then grows on its own. Growth scales with the chunk's Aura saturation, so a fuller chunk grows faster.
2. **Aspect it (optional).** A Crystallarium only grows **Raw** clusters until the chunk's Aura is dominated by another Aspect. Break the cycle with a **Catalyst**: right-click a grown Raw cluster with an aspected catalyst (Ruina, Nihilum, Vindicta, Invictus) to transmute it, then burn its shards to make that Aspect dominant. From then on the Crystallarium grows that Aspect natively.
3. **Harvest.** The **Crystallum Fractura** ritual (see [Rituals](Rituals)) auto-harvests fully-grown clusters in range, dropping shards into a chest atop the Master Ritual Stone. It also **doubles** crystal growth and adds **+25% to Spiritus injection** across its chunks. Bias that bonus toward a chosen Aspect with the Ritual Configurator (carry exactly one aspect crystal in your hotbar in SET_WILL_CONFIG mode).
4. **Burn.** A **Vas Maleficum** consumes Spiritus Crystals fed into it and releases their Spiritus into the chunk's **Aura** (50 per spire; a net gain over the growth cost, and far more when same-Aspect catalysts have supercharged the cluster).
5. **Route.** Point a **Routing Node** at the harvest chest and feed the shards into the Vas Maleficum to keep the chunk saturated, closing the loop. To move Aura to where it is used, chain **Spira Infernalis** pylons from the saturated chunk toward your worksite; each pulls Aura from a richer chunk 16 blocks away.
6. **Bank and distribute.** A **Spirit Accumulator** on the farm chunk skims everything above 75 into a 1,000-point reserve, and an **Output Node** in any other chunk keeps that chunk stocked from the same network. This carries Aura any distance the network reaches, without a pylon chain.
7. **Consume.** Rituals draw on the Aura, a redstone-charged Vas Maleficum fills empty gems from the chunk's Aura, and the Athanor's ore-yield boost scales with local Raw Spiritus.

## The closed loop

Catalyst + Crystallum Fractura + a Routing Node is a hands-off farm: the ritual harvests and the node ships the shards back into the Vas Maleficum to maintain saturation. A **same-Aspect catalyst** on a matching cluster drops the per-spire cost to 25 and accelerates growth tenfold, so feeding catalysts into the cycle turns it strongly net-positive.

## Banking the surplus

A farm chunk pinned at the 100 cap wastes every crystal it burns after that. Park a **[Spirit Accumulator](Spiritus-Aspects-and-Crystals)** in it, attuned with a single shard of the farmed Aspect, and it skims the surplus above **75** into a **1,000**-point reserve at up to 25 per tick. The chunk stays saturated enough to keep growing crystals, and the overflow is banked instead of lost.

Bind the Accumulator to a Master (it auto-binds within 16 blocks, same as any node) and that reserve becomes network stock. Now put an **Output Node** wherever you want Aura, open its **Spiritus** tab, pick the Aspect and a Keep figure, and the network holds that chunk at that level. The node needs no adjacent block; it can stand alone in the middle of the chunk it feeds.

That covers the distances a Spira Infernalis chain used to handle, and it holds a level rather than equilibrating. One Accumulator on the farm plus one Output Node per worksite keeps a ritual chunk, an Athanor chunk, and a Crystallarium chunk all topped up from a single crystal farm.

> A Spirit Accumulator in the **same chunk** as an Output Node stocking the **same Aspect** will skim back whatever the node delivers above 75. Keep them in separate chunks, or set the node's Keep figure to 75 or lower.

## Running multiple Aspects

The Aura is **chunk-based** and caps at **100 per Aspect**, and a chunk grows clusters of its **dominant** Aspect best (off-Aspect clusters cost more and grow slower). So farm each Aspect in its own chunk: keep that chunk biased to the Aspect (burn its shards there), run a Crystallarium + Vas + a Crystallum Fractura tuned to it, and ferry the finished Aura out with Spira Infernalis or a Spirit Accumulator feeding the routing network. Repeat per Aspect (Raw, Ruina, Nihilum, Vindicta, Invictus) for a full will supply.

Because an Accumulator is locked to one Aspect at a time, run one per farmed Aspect. They can all share a single network; each Output Node names the Aspect it wants and draws only from the matching Accumulators.

See also: **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** (all the mechanics and numbers), **[Rituals](Rituals)** (Crystallum Fractura), **[Routing Network](Routing-Network)** (node setup).
